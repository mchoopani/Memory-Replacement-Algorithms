import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Client {
    private static Socket socket;
    private static DataOutputStream dos = null;
    private static DataInputStream dis = null;

    public static void initConnection() {
        try {
            socket = new Socket("localhost", Consts.PORT);
            dos = getDataOutputStream();
            dis = getDataInputStream();
        } catch (ConnectException e) {
            System.out.println("Connection Refused");
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        if (dos == null || dis == null) {
            System.out.println("streams are null");
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        initConnection();

        int receivedCapacity = 0;
        try {
            receivedCapacity = dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        FIFO fifo = new FIFO(receivedCapacity);
        LRU lru = new LRU(receivedCapacity);
        SC secondChance = new SC(receivedCapacity);

        Thread algorithmThread = new Thread(()->{});
        algorithmThread.start();
        while (true) {
            int receivedData = 0;
            try {
                receivedData = dis.readInt();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            if (receivedData == 0) {
                break;
            }

            algorithmThread.join();
            int finalReceivedData = receivedData;
            algorithmThread = new Thread(()->{
                lru.replace(finalReceivedData);
                fifo.replace(finalReceivedData);
                secondChance.replace(finalReceivedData);
            });
            algorithmThread.start();

        }

        System.out.printf(
                "LRU:%d,FIFO:%d,Second-chance:%d\n",
                lru.getPageFaults(),
                fifo.getPageFaults(),
                secondChance.getPageFaults()
        );


    }



    public static DataInputStream getDataInputStream() throws IOException {
        if (socket == null)
            return null;
        return new DataInputStream(socket.getInputStream());
    }

    public static DataOutputStream getDataOutputStream() throws IOException {
        if (socket == null)
            return null;
        return new DataOutputStream(socket.getOutputStream());
    }

}

interface Replacer {
    void replace(int newComer);
}

class FIFO implements Replacer {
    private final Queue queue;
    private int pageFaults = 0;

    public FIFO(int capacity) {
        this.queue = new CircularQueue(capacity);
    }

    @Override
    public void replace(int newComer) {
        if (queue.exists(newComer))
            return;

        queue.add(newComer);
        pageFaults++;
    }

    public int getPageFaults() {
        return pageFaults;
    }
}

class LRU implements Replacer {
    private final Queue queue;
    private int pageFaults = 0;
    private final Stack<Integer> recentlyUsed = new Stack<>();

    public LRU(int capacity) {
        queue = new Queue(capacity);
    }

    @Override
    public void replace(int newComer) {
        if (queue.exists(newComer)) {
            recentlyUsed.push(newComer);
            return;
        }
        pageFaults++;
        if (!queue.isFull()) {
            queue.add(newComer);
        } else {
            int latestUsedIndexBefore = getReplacementIndex();
            queue.replaceElement(latestUsedIndexBefore, newComer);
        }
        recentlyUsed.push(newComer);
    }

    private int getReplacementIndex() {
        Map<Integer, Integer> whereOccurred = new HashMap<>();
        int stackSize = this.recentlyUsed.size();
        Stack<Integer> tempStack = new Stack<>();
        for (int i = 0; i < stackSize; i++) {
            int recentlyUsedItem = this.recentlyUsed.pop();
            tempStack.push(recentlyUsedItem);
            int recentlyUsedItemIndexInQueue = queue.indexOf(recentlyUsedItem);
            if (recentlyUsedItemIndexInQueue != -1) {
                if (whereOccurred.containsKey(recentlyUsedItem))
                    continue;
                whereOccurred.put(recentlyUsedItem, stackSize - i);
            }
        }
        int minimumIndex = Integer.MAX_VALUE;
        int minimumElement = 0;
        for (int key : whereOccurred.keySet()) {
            int index = whereOccurred.get(key);
            if (index < minimumIndex) {
                minimumIndex = index;
                minimumElement = key;
            }
        }
        for (int i = 0; i < stackSize; i++) {
            this.recentlyUsed.push(tempStack.pop());
        }
        return queue.indexOf(minimumElement);
    }

    public int getPageFaults() {
        return pageFaults;
    }
}

class SC implements Replacer {

    Queue queue;
    Map<Integer, Boolean> seenRecently = new HashMap<>();
    private int pageFaults;
    private int capacity;
    int replacerPtr = 0;

    public SC(int capacity) {
        this.queue = new CircularQueue(capacity);
        pageFaults = 0;
        this.capacity = capacity;
    }

    @Override
    public void replace(int newComer) {
        if (queue.exists(newComer)) {
            seenRecently.put(newComer, true);
            return;
        }
        pageFaults++;
        if (!queue.isFull()) {
            queue.add(newComer);
            seenRecently.put(newComer, false);
            return;
        }

        while (seenRecently.get(queue.get(replacerPtr))) {
            seenRecently.put(queue.get(replacerPtr), false);
            replacerPtr = (replacerPtr + 1) % capacity;
        }
        seenRecently.put(newComer, false);
        seenRecently.remove(queue.get(replacerPtr));
        queue.replaceElement(replacerPtr, newComer);
        replacerPtr = (replacerPtr + 1) % capacity;
    }

    public int getPageFaults() {
        return pageFaults;
    }
}
