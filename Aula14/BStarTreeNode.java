import java.util.ArrayList;
import java.util.List;

// Interface comum para nós de B* Tree
public interface BStarTreeNode {
    List<Integer> getKeys();
    List<BStarTreeNode> getChildren(); // Pode ser lista vazia para folhas
    boolean isLeaf();
    boolean isFull();
    int getMinKeys();
    int getMaxKeys();
    void insertKey(int key, Produto value, int index);
    void removeKey(int key);
    boolean canRedistributeWith(BStarTreeNode sibling);
    void redistribute(BStarTreeNode sibling, int parentKeyIndex, BStarTreeInternalNode parent, boolean isLeftSibling);
}

class BStarTreeLeafNode implements BStarTreeNode {
    private List<Integer> keys;
    private List<Produto> values;
    private final int order;

    public BStarTreeLeafNode(int order) {
        this.order = order;
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    @Override
    public List<Integer> getKeys() { return keys; }

    public List<Produto> getValues() { return values; }

    @Override
    public List<BStarTreeNode> getChildren() { return new ArrayList<>(); } // Folhas não têm filhos

    @Override
    public boolean isLeaf() { return true; }

    @Override
    public boolean isFull() {
        return keys.size() == order - 1;
    }

    @Override
    public int getMinKeys() {
        return (int) Math.ceil((order - 1) / 2.0);
    }

    @Override
    public int getMaxKeys() { return order - 1; }

    @Override
    public void insertKey(int key, Produto value, int index) {
        keys.add(index, key);
        values.add(index, value);
    }

    @Override
    public void removeKey(int key) {
        int index = keys.indexOf(key);
        if (index >= 0) {
            keys.remove(index);
            values.remove(index);
        }
    }

    @Override
    public boolean canRedistributeWith(BStarTreeNode sibling) {
        return !sibling.isFull() || (this.getKeys().size() + sibling.getKeys().size() < (order - 1) * 2);
    }

    @Override
    public void redistribute(BStarTreeNode sibling, int parentKeyIndex, BStarTreeInternalNode parent, boolean isLeftSibling) {
        BStarTreeLeafNode siblingLeaf = (BStarTreeLeafNode) sibling;
        if (isLeftSibling) {
            int keyToMove = siblingLeaf.getKeys().remove(siblingLeaf.getKeys().size() - 1);
            Produto valueToMove = siblingLeaf.getValues().remove(siblingLeaf.getValues().size() - 1);
            this.getKeys().add(0, keyToMove);
            this.getValues().add(0, valueToMove);
            parent.getKeys().set(parentKeyIndex, keyToMove);
        } else {
            int keyToMove = this.getKeys().remove(this.getKeys().size() - 1);
            Produto valueToMove = this.getValues().remove(this.getValues().size() - 1);
            siblingLeaf.getKeys().add(0, keyToMove);
            siblingLeaf.getValues().add(0, valueToMove);
            parent.getKeys().set(parentKeyIndex, siblingLeaf.getKeys().get(0));
        }
    }
}

class BStarTreeInternalNode implements BStarTreeNode {
    private List<Integer> keys;
    private List<BStarTreeNode> children;
    private final int order;

    public BStarTreeInternalNode(int order) {
        this.order = order;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    @Override
    public List<Integer> getKeys() { return keys; }

    @Override
    public List<BStarTreeNode> getChildren() { return children; }

    @Override
    public boolean isLeaf() { return false; }

    @Override
    public boolean isFull() { return keys.size() == order - 1; }

    @Override
    public int getMinKeys() {
        return (int) Math.ceil((order - 1) * (2.0 / 3.0));
    }

    @Override
    public int getMaxKeys() { return order - 1; }

    public int findChildIndex(int key) {
        int i = 0;
        while (i < keys.size() && key >= keys.get(i)) {
            i++;
        }
        return i;
    }

    @Override
    public void insertKey(int key, Produto value, int index) {
        keys.add(index, key);
    }

    @Override
    public void removeKey(int key) {
        int index = keys.indexOf(key);
        if (index >= 0) {
            keys.remove(index);
        }
    }

    @Override
    public boolean canRedistributeWith(BStarTreeNode sibling) {
        return !sibling.isFull() || (this.getKeys().size() + sibling.getKeys().size() < (order - 1) * 2);
    }

    @Override
    public void redistribute(BStarTreeNode sibling, int parentKeyIndex, BStarTreeInternalNode parent, boolean isLeftSibling) {
        // Implementação omitida para simplificação
    }
}