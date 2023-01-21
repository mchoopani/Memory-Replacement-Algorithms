import java.util.HashMap;
import java.util.Map;

public class SC implements Replacer {

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

    @Override
    public void printCurrentQueue() {
        System.out.printf("SC: %s\n", queue);
    }

    public int getPageFaults() {
        return pageFaults;
    }
}