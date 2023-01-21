public class FIFO implements Replacer {
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

    @Override
    public void printCurrentQueue() {
        System.out.printf("FIFO: %s\n", queue);
    }

    public int getPageFaults() {
        return pageFaults;
    }
}