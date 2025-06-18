import java.util.ArrayList;
import java.util.List;

public class BPlusTree {
    private BPlusNode root;
    private final int order;

    // Abstract base class for B+ Tree nodes
    public static abstract class BPlusNode {
        protected int order;
        public BPlusNode(int order) {
            this.order = order;
        }
        public abstract boolean isLeaf();
        public abstract List<Integer> getKeys();
    }

    public BPlusTree(int order) {
        this.order = order;
        this.root = new BPlusTreeLeafNode(order);
    }

    // Leaf node class for B+ Tree
    public static class BPlusTreeLeafNode extends BPlusNode {
        private List<Integer> keys;
        private List<Produto> values;
        private BPlusTreeLeafNode nextLeaf;

        public BPlusTreeLeafNode(int order) {
            super(order);
            this.keys = new ArrayList<>();
            this.values = new ArrayList<>();
            this.nextLeaf = null;
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

        @Override
        public List<Integer> getKeys() {
            return keys;
        }

        public List<Produto> getValues() {
            return values;
        }

        public BPlusTreeLeafNode getNextLeaf() {
            return nextLeaf;
        }

        public void setNextLeaf(BPlusTreeLeafNode nextLeaf) {
            this.nextLeaf = nextLeaf;
        }

        public void insert(Produto product) {
            int key = product.getId();
            int idx = 0;
            while (idx < keys.size() && keys.get(idx) < key) {
                idx++;
            }
            keys.add(idx, key);
            values.add(idx, product);
        }

        public boolean isFull() {
            return keys.size() >= order;
        }

        public int getMinKeys() {
            return (int) Math.ceil(order / 2.0);
        }

        public Produto search(int key) {
            for (int i = 0; i < keys.size(); i++) {
                if (keys.get(i) == key) {
                    return values.get(i);
                }
            }
            return null;
        }

        public void remove(int key) {
            for (int i = 0; i < keys.size(); i++) {
                if (keys.get(i) == key) {
                    keys.remove(i);
                    values.remove(i);
                    break;
                }
            }
        }
    }

    public void insert(Produto product) {
        int key = product.getId();
        BPlusNode node = root;
        List<BPlusInternalNodePath> path = new ArrayList<>();

        while (!node.isLeaf()) {
            BPlusTreeInternalNode internalNode = (BPlusTreeInternalNode) node;
            int childIndex = internalNode.findChildIndex(key);
            path.add(new BPlusInternalNodePath(internalNode, childIndex));
            node = internalNode.getChildren().get(childIndex);
        }

        BPlusTreeLeafNode leaf = (BPlusTreeLeafNode) node;
        leaf.insert(product);

        if (leaf.isFull()) {
            // Dividir o nó folha
            int midIndex = leaf.getKeys().size() / 2;
            int splitKey = leaf.getKeys().get(midIndex);

            BPlusTreeLeafNode newLeaf = new BPlusTreeLeafNode(order);
            newLeaf.setNextLeaf(leaf.getNextLeaf());
            leaf.setNextLeaf(newLeaf);

            newLeaf.getKeys().addAll(leaf.getKeys().subList(midIndex + 1, leaf.getKeys().size()));
            newLeaf.getValues().addAll(leaf.getValues().subList(midIndex + 1, leaf.getValues().size()));

            leaf.getKeys().subList(midIndex, leaf.getKeys().size()).clear();
            leaf.getValues().subList(midIndex, leaf.getValues().size()).clear();

            if (path.isEmpty()) {
                BPlusTreeInternalNode newRoot = new BPlusTreeInternalNode(order);
                newRoot.getKeys().add(splitKey);
                newRoot.getChildren().add(leaf);
                newRoot.getChildren().add(newLeaf);
                this.root = newRoot;
            } else {
                propagateSplit(path, splitKey, newLeaf);
            }
        }
    }

    // Internal node class for B+ Tree
    public static class BPlusTreeInternalNode extends BPlusNode {
        private List<Integer> keys;
        private List<BPlusNode> children;

        public BPlusTreeInternalNode(int order) {
            super(order);
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        @Override
        public List<Integer> getKeys() {
            return keys;
        }

        public List<BPlusNode> getChildren() {
            return children;
        }

        public boolean isFull() {
            return keys.size() >= order;
        }

        public int getMinKeys() {
            return (int) Math.ceil(order / 2.0) - 1;
        }

        public int findChildIndex(int key) {
            int idx = 0;
            while (idx < keys.size() && key >= keys.get(idx)) {
                idx++;
            }
            return idx;
        }
    }

    private static class BPlusInternalNodePath {
        BPlusTreeInternalNode node;
        int childIndex;

        BPlusInternalNodePath(BPlusTreeInternalNode node, int childIndex) {
            this.node = node;
            this.childIndex = childIndex;
        }
    }

    private void propagateSplit(List<BPlusInternalNodePath> path, int keyToPropagate, BPlusNode newNode) {
        BPlusInternalNodePath currentPath = path.remove(path.size() - 1);
        BPlusTreeInternalNode parent = currentPath.node;
        int childIndex = currentPath.childIndex;

        parent.getKeys().add(childIndex, keyToPropagate);
        parent.getChildren().add(childIndex + 1, newNode);

        if (parent.isFull()) {
            int midIndex = parent.getKeys().size() / 2;
            int splitKey = parent.getKeys().get(midIndex);

            BPlusTreeInternalNode newInternalNode = new BPlusTreeInternalNode(order);

            newInternalNode.getKeys().addAll(parent.getKeys().subList(midIndex + 1, parent.getKeys().size()));
            newInternalNode.getChildren().addAll(parent.getChildren().subList(midIndex + 1, parent.getChildren().size()));

            parent.getKeys().subList(midIndex, parent.getKeys().size()).clear();
            parent.getChildren().subList(midIndex + 1, parent.getChildren().size()).clear();

            if (path.isEmpty()) {
                BPlusTreeInternalNode newRoot = new BPlusTreeInternalNode(order);
                newRoot.getKeys().add(splitKey);
                newRoot.getChildren().add(parent);
                newRoot.getChildren().add(newInternalNode);
                this.root = newRoot;
            } else {
                propagateSplit(path, splitKey, newInternalNode);
            }
        }
    }


    public Produto search(int key) {
        BPlusNode current = root;
        while (!current.isLeaf()) {
            BPlusTreeInternalNode internalNode = (BPlusTreeInternalNode) current;
            int i = 0;
            while (i < internalNode.getKeys().size() && key >= internalNode.getKeys().get(i)) {
                i++;
            }
            current = internalNode.getChildren().get(i);
        }
        BPlusTreeLeafNode leaf = (BPlusTreeLeafNode) current;
        return leaf.search(key);
    }

    public boolean delete(int key) {
        BPlusNode node = root;
        List<BPlusInternalNodePath> path = new ArrayList<>();

        // 1. Encontrar a folha
        while (!node.isLeaf()) {
            BPlusTreeInternalNode internalNode = (BPlusTreeInternalNode) node;
            int childIndex = internalNode.findChildIndex(key);
            path.add(new BPlusInternalNodePath(internalNode, childIndex));
            node = internalNode.getChildren().get(childIndex);
        }

        BPlusTreeLeafNode leaf = (BPlusTreeLeafNode) node;
        Produto productToRemove = leaf.search(key);
        if (productToRemove == null) {
            return false;
        }

        leaf.remove(key);
        if (leaf.getKeys().size() < leaf.getMinKeys() && !path.isEmpty()) {
        }

        if (root.getKeys().isEmpty() && !root.isLeaf()) {
            BPlusTreeInternalNode internalRoot = (BPlusTreeInternalNode) root;
            if (!internalRoot.getChildren().isEmpty()) {
                 root = internalRoot.getChildren().get(0);
            } else {
                 root = new BPlusTreeLeafNode(order);
            }
        }
        return true;
    }
}