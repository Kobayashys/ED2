import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

interface BPlusNode {
    List<Integer> getKeys();
    boolean isLeaf();
    boolean isFull();
    int getMinKeys();
    int getMaxKeys();
}

class BPlusTreeLeafNode implements BPlusNode {
    private List<Integer> keys;
    private List<Produto> values; 
    private BPlusTreeLeafNode nextLeaf;
    private final int order;

    public BPlusTreeLeafNode(int order) {
        this.order = order;
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    @Override
    public List<Integer> getKeys() {
        return keys;
    }

    public List<Produto> getValues() {
        return values;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isFull() {
        return keys.size() == order - 1; 
    }

    @Override
    public int getMinKeys() {
        return (int) Math.ceil((order - 1) / 2.0);
    }

    @Override
    public int getMaxKeys() {
        return order - 1;
    }

    public BPlusTreeLeafNode getNextLeaf() {
        return nextLeaf;
    }

    public void setNextLeaf(BPlusTreeLeafNode nextLeaf) {
        this.nextLeaf = nextLeaf;
    }

    public void insert(Produto product) {
        int i = Collections.binarySearch(keys, product.getId());
        if (i < 0) {
            i = -i - 1;
        }
        keys.add(i, product.getId());
        values.add(i, product);
    }

    public void remove(int key) {
        int index = Collections.binarySearch(keys, key);
        if (index >= 0) {
            keys.remove(index);
            values.remove(index);
        }
    }

    public Produto search(int key) {
        int index = Collections.binarySearch(keys, key);
        if (index >= 0) {
            return values.get(index);
        }
        return null;
    }
}

class BPlusTreeInternalNode implements BPlusNode {
    private List<Integer> keys;
    private List<BPlusNode> children;
    private final int order;

    public BPlusTreeInternalNode(int order) {
        this.order = order;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    @Override
    public List<Integer> getKeys() {
        return keys;
    }

    public List<BPlusNode> getChildren() {
        return children;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isFull() {
        return keys.size() == order - 1;
    }

    @Override
    public int getMinKeys() {
        return (int) Math.ceil((order - 1) / 2.0);
    }

    @Override
    public int getMaxKeys() {
        return order - 1;
    }

    public int findChildIndex(int key) {
        int i = 0;
        while (i < keys.size() && key >= keys.get(i)) {
            i++;
        }
        return i;
    }

    public void insertKeyAndChild(int key, BPlusNode child, int index) {
        keys.add(index, key);
        children.add(index + 1, child);
    }

    public void removeKeyAndChild(int key) {
        int index = keys.indexOf(key);
        if (index >= 0) {
            keys.remove(index);
            children.remove(index + 1);
        }
    }
}

