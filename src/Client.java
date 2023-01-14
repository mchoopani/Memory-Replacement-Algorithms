import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

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

        Thread algorithmThread = new Thread(() -> {
        });
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
            algorithmThread = new Thread(() -> {
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
