import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LRU implements Replacer {
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

    @Override
    public void printCurrentQueue() {
        System.out.printf("LRU: %s\n", queue);
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

