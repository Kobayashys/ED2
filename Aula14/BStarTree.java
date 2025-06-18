import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BStarTree {
    private BStarTreeNode root;
    private final int order;

    private static abstract class BStarTreeNode {
        protected int order;
        protected List<Integer> keys;

        public BStarTreeNode(int order) {
            this.order = order;
            this.keys = new ArrayList<>();
        }

        public List<Integer> getKeys() {
            return keys;
        }

        public abstract boolean isLeaf();
        public abstract boolean isFull();
    }

    private static class BStarTreeInternalNode extends BStarTreeNode {
        private List<BStarTreeNode> children;

        public BStarTreeInternalNode(int order) {
            super(order);
            this.children = new ArrayList<>();
        }

        public List<BStarTreeNode> getChildren() {
            return children;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        @Override
        public boolean isFull() {
            return keys.size() >= order - 1;
        }

        public int findChildIndex(int key) {
            int i = 0;
            while (i < keys.size() && key >= keys.get(i)) {
                i++;
            }
            return i;
        }

        public boolean canRedistributeWith(BStarTreeInternalNode sibling) {
            return false;
        }
        public void redistribute(BStarTreeInternalNode sibling, int index, BStarTreeInternalNode parent, boolean left) {
        }
    }

    private static class BStarTreeLeafNode extends BStarTreeNode {
        private List<Produto> values;

        public BStarTreeLeafNode(int order) {
            super(order);
            this.values = new ArrayList<>();
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
            return keys.size() >= order - 1;
        }

        public void insertKey(int key, Produto value, int index) {
            keys.add(index, key);
            values.add(index, value);
        }

        public boolean canRedistributeWith(BStarTreeLeafNode sibling) {
            return false;
        }
        public void redistribute(BStarTreeLeafNode sibling, int index, BStarTreeInternalNode parent, boolean left) {
        }
    }

    public BStarTree(int order) {
        this.order = order;
        this.root = new BStarTreeLeafNode(order);
    }

    public void insert(Produto product) {
        int key = product.getId();
        List<BStarTreeInternalNodePath> path = new ArrayList<>();
        BStarTreeNode node = root;

        while (!node.isLeaf()) {
            BStarTreeInternalNode internalNode = (BStarTreeInternalNode) node;
            int childIndex = internalNode.findChildIndex(key);
            path.add(new BStarTreeInternalNodePath(internalNode, childIndex));
            node = internalNode.getChildren().get(childIndex);
        }

        BStarTreeLeafNode leaf = (BStarTreeLeafNode) node;
        leaf.insertKey(key, product, Collections.binarySearch(leaf.getKeys(), key) < 0 ? -Collections.binarySearch(leaf.getKeys(), key) - 1 : Collections.binarySearch(leaf.getKeys(), key));


        if (leaf.isFull()) {

            BStarTreeInternalNode parent = path.isEmpty() ? null : path.get(path.size() - 1).node;
            int leafIndexInParent = path.isEmpty() ? -1 : path.get(path.size() - 1).childIndex;

            BStarTreeLeafNode leftSibling = null;
            BStarTreeLeafNode rightSibling = null;

            if (parent != null) {
                if (leafIndexInParent > 0) {
                    leftSibling = (BStarTreeLeafNode) parent.getChildren().get(leafIndexInParent - 1);
                }
                if (leafIndexInParent < parent.getChildren().size() - 1) {
                    rightSibling = (BStarTreeLeafNode) parent.getChildren().get(leafIndexInParent + 1);
                }
            }

            boolean redistributed = false;
            if (leftSibling != null && leftSibling.canRedistributeWith(leaf)) {
                leaf.redistribute(leftSibling, leafIndexInParent -1, parent, true);
                redistributed = true;
            } else if (rightSibling != null && rightSibling.canRedistributeWith(leaf)) {
                leaf.redistribute(rightSibling, leafIndexInParent, parent, false);
                redistributed = true;
            }

            if (!redistributed) {
                int midIndex = leaf.getKeys().size() / 2;
                int splitKey = leaf.getKeys().get(midIndex);

                BStarTreeLeafNode newLeaf = new BStarTreeLeafNode(order);
                newLeaf.getKeys().addAll(leaf.getKeys().subList(midIndex + 1, leaf.getKeys().size()));
                newLeaf.getValues().addAll(leaf.getValues().subList(midIndex + 1, leaf.getValues().size()));

                leaf.getKeys().subList(midIndex, leaf.getKeys().size()).clear();
                leaf.getValues().subList(midIndex, leaf.getValues().size()).clear();

                if (path.isEmpty()) {
                    BStarTreeInternalNode newRoot = new BStarTreeInternalNode(order);
                    newRoot.getKeys().add(splitKey);
                    newRoot.getChildren().add(leaf);
                    newRoot.getChildren().add(newLeaf);
                    this.root = newRoot;
                } else {
                    propagateBStarSplit(path, splitKey, newLeaf);
                }
            }
        }
    }

    private static class BStarTreeInternalNodePath {
        BStarTreeInternalNode node;
        int childIndex;

        BStarTreeInternalNodePath(BStarTreeInternalNode node, int childIndex) {
            this.node = node;
            this.childIndex = childIndex;
        }
    }

    private void propagateBStarSplit(List<BStarTreeInternalNodePath> path, int keyToPropagate, BStarTreeNode newNode) {
        BStarTreeInternalNodePath currentPath = path.remove(path.size() - 1);
        BStarTreeInternalNode parent = currentPath.node;
        int childIndex = currentPath.childIndex;

        parent.getKeys().add(childIndex, keyToPropagate);
        parent.getChildren().add(childIndex + 1, newNode);

        if (parent.isFull()) {
            BStarTreeInternalNode leftSibling = null;
            BStarTreeInternalNode rightSibling = null;

            int parentIndexInGrandparent = -1;
            if (!path.isEmpty()) {
                parentIndexInGrandparent = path.get(path.size() - 1).childIndex;
                BStarTreeInternalNode grandparent = path.get(path.size() - 1).node;
                if (parentIndexInGrandparent > 0) {
                    leftSibling = (BStarTreeInternalNode) grandparent.getChildren().get(parentIndexInGrandparent - 1);
                }
                if (parentIndexInGrandparent < grandparent.getChildren().size() - 1) {
                    rightSibling = (BStarTreeInternalNode) grandparent.getChildren().get(parentIndexInGrandparent + 1);
                }
            }

            boolean redistributed = false;
            if (leftSibling != null && leftSibling.canRedistributeWith(parent)) {
                parent.redistribute(leftSibling, parentIndexInGrandparent -1, parent, true);
                redistributed = true;
            } else if (rightSibling != null && rightSibling.canRedistributeWith(parent)) {
                parent.redistribute(rightSibling, parentIndexInGrandparent, parent, false);
                redistributed = true;
            }

            if (!redistributed) {
                int midIndex = parent.getKeys().size() / 2;
                int splitKey = parent.getKeys().get(midIndex);

                BStarTreeInternalNode newInternalNode = new BStarTreeInternalNode(order);

                newInternalNode.getKeys().addAll(parent.getKeys().subList(midIndex + 1, parent.getKeys().size()));
                newInternalNode.getChildren().addAll(parent.getChildren().subList(midIndex + 1, parent.getChildren().size()));

                parent.getKeys().subList(midIndex, parent.getKeys().size()).clear();
                parent.getChildren().subList(midIndex + 1, parent.getChildren().size()).clear();

                if (path.isEmpty()) {
                    BStarTreeInternalNode newRoot = new BStarTreeInternalNode(order);
                    newRoot.getKeys().add(splitKey);
                    newRoot.getChildren().add(parent);
                    newRoot.getChildren().add(newInternalNode);
                    this.root = newRoot;
                } else {
                    propagateBStarSplit(path, splitKey, newInternalNode);
                }
            }
        }
    }


    public Produto search(int key) {
        BStarTreeNode current = root;
        while (!current.isLeaf()) {
            BStarTreeInternalNode internalNode = (BStarTreeInternalNode) current;
            int i = 0;
            while (i < internalNode.getKeys().size() && key >= internalNode.getKeys().get(i)) {
                i++;
            }
            current = internalNode.getChildren().get(i);
        }
        BStarTreeLeafNode leaf = (BStarTreeLeafNode) current;
        int index = Collections.binarySearch(leaf.getKeys(), key);
        if (index >= 0) {
            return leaf.getValues().get(index);
        }
        return null;
    }

    public boolean delete(int key) {
        if (search(key) == null) {
            return false;
        }

        System.out.println("Produto com ID " + key + " removido (simulado) da B* Tree.");
        return true;
    }
}