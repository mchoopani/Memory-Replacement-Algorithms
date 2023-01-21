import java.util.Arrays;

public class Queue {
    private final int[] queue;
    protected int index;
    protected int size = 0;

    public Queue(int capacity) {
        this.queue = new int[capacity];
        this.index = 0;
    }

    public boolean isFull() {
        return size == queue.length;
    }

    public boolean add(int element) {
        if (this.isFull())
            return false;
        size++;
        this.queue[index++] = element;
        return true;
    }

    public boolean replaceElement(int index, int element) {
        if (index >= queue.length)
            return false;
        queue[index] = element;
        return true;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int[] getQueue() {
        return queue;
    }

    public boolean exists(int element) {
        for(int item: this.getQueue())
            if (item == element)
                return true;
        return false;
    }

    public int indexOf(int element) {
        for (int i = 0; i < this.size; i++) {
            if (this.queue[i] == element)
                return i;
        }
        return -1;
    }

    public int get(int index) {
        return this.queue[index];
    }

    @Override
    public String toString() {
        return Arrays.toString(queue);
    }
}

class CircularQueue extends Queue {

    public CircularQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean add(int element) {
        super.size++;
        if (super.index == super.getQueue().length) {
            super.index = 0;
        }
        return super.replaceElement(this.index++, element);
    }
}
