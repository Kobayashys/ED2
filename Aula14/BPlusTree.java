import java.util.ArrayList;
import java.util.List;

public class BPlusTree {
    private BPlusNode root;
    private final int order; // Ordem da árvore (número máximo de filhos)

    public BPlusTree(int order) {
        this.order = order;
        this.root = new BPlusTreeLeafNode(order); // Começa com um nó folha
    }

    public void insert(Produto product) {
        int key = product.getId();
        BPlusNode node = root;
        List<BPlusInternalNodePath> path = new ArrayList<>(); // Armazena o caminho percorrido

        // 1. Encontrar a folha correta para inserção
        while (!node.isLeaf()) {
            BPlusTreeInternalNode internalNode = (BPlusTreeInternalNode) node;
            int childIndex = internalNode.findChildIndex(key);
            path.add(new BPlusInternalNodePath(internalNode, childIndex)); // Salva o nó e o índice do filho
            node = internalNode.getChildren().get(childIndex);
        }

        BPlusTreeLeafNode leaf = (BPlusTreeLeafNode) node;
        leaf.insert(product); // Inserir o produto na folha

        // 2. Lidar com a divisão se o nó folha estiver cheio
        if (leaf.isFull()) {
            // Dividir o nó folha
            int midIndex = leaf.getKeys().size() / 2;
            int splitKey = leaf.getKeys().get(midIndex);

            BPlusTreeLeafNode newLeaf = new BPlusTreeLeafNode(order);
            newLeaf.setNextLeaf(leaf.getNextLeaf());
            leaf.setNextLeaf(newLeaf);

            newLeaf.getKeys().addAll(leaf.getKeys().subList(midIndex + 1, leaf.getKeys().size()));
            newLeaf.getValues().addAll(leaf.getValues().subList(midIndex + 1, leaf.getValues().size()));

            leaf.getKeys().subList(midIndex, leaf.getKeys().size()).clear(); // Remove chaves movidas e a chave de split
            leaf.getValues().subList(midIndex, leaf.getValues().size()).clear();

            // Propagar a chave de split para cima
            if (path.isEmpty()) { // A raiz era uma folha e foi dividida
                BPlusTreeInternalNode newRoot = new BPlusTreeInternalNode(order);
                newRoot.getKeys().add(splitKey);
                newRoot.getChildren().add(leaf);
                newRoot.getChildren().add(newLeaf);
                this.root = newRoot;
            } else { // Propagar para o pai
                propagateSplit(path, splitKey, newLeaf);
            }
        }
    }

    // Classe auxiliar para armazenar o nó interno e o índice do filho
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

        // Inserir a chave e o novo filho no nó pai
        parent.getKeys().add(childIndex, keyToPropagate);
        parent.getChildren().add(childIndex + 1, newNode);

        // Manter a ordem das chaves (opcional se a inserção já for ordenada)
        // Collections.sort(parent.getKeys()); // Não é necessário se a inserção for no lugar certo

        // Se o pai estiver cheio, dividi-lo
        if (parent.isFull()) {
            int midIndex = parent.getKeys().size() / 2;
            int splitKey = parent.getKeys().get(midIndex);

            BPlusTreeInternalNode newInternalNode = new BPlusTreeInternalNode(order);

            // Mover chaves e filhos para o novo nó interno
            newInternalNode.getKeys().addAll(parent.getKeys().subList(midIndex + 1, parent.getKeys().size()));
            newInternalNode.getChildren().addAll(parent.getChildren().subList(midIndex + 1, parent.getChildren().size()));

            // Limpar chaves e filhos do nó original
            parent.getKeys().subList(midIndex, parent.getKeys().size()).clear();
            parent.getChildren().subList(midIndex + 1, parent.getChildren().size()).clear();

            if (path.isEmpty()) { // A raiz atual foi dividida
                BPlusTreeInternalNode newRoot = new BPlusTreeInternalNode(order);
                newRoot.getKeys().add(splitKey);
                newRoot.getChildren().add(parent);
                newRoot.getChildren().add(newInternalNode);
                this.root = newRoot;
            } else { // Propagar a divisão para o avô
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
        // A remoção em árvores B+ é a parte mais complexa, exigindo fusão e redistribuição.
        // O esqueleto abaixo apenas demonstra a estrutura de como você abordaria.
        // A implementação completa exigiria manipular as chaves e filhos em nós internos
        // e folhas, e garantir que cada nó tenha pelo menos o número mínimo de chaves.
        // Isso pode envolver:
        // 1. Encontrar o nó folha e o item a ser removido.
        // 2. Remover o item.
        // 3. Se o nó folha ficar abaixo do mínimo de chaves, tentar redistribuir com um irmão.
        // 4. Se a redistribuição não for possível, fundir com um irmão, o que pode propagar
        //    a necessidade de fusão para o nó pai.

        BPlusNode node = root;
        List<BPlusInternalNodePath> path = new ArrayList<>(); // Armazena o caminho percorrido

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
            return false; // Produto não encontrado
        }

        leaf.remove(key); // Remove o produto da folha

        // 2. Lógica de rebalanceamento após a remoção (fusão/redistribuição)
        //    Esta é a parte mais complexa e exigiria métodos auxiliares para:
        //    - `handleUnderflowInLeaf(leaf, path)`
        //    - `handleUnderflowInInternalNode(internalNode, path)`
        //    - `mergeNodes(...)`
        //    - `redistributeKeys(...)`

        // Exemplo simplificado (NÃO É UMA IMPLEMENTAÇÃO COMPLETA DE REMOÇÃO):
        if (leaf.getKeys().size() < leaf.getMinKeys() && !path.isEmpty()) {
            // Lógica para lidar com underflow no nó folha
            // ... (Redistribuição ou fusão com irmãos)
            // Se houver fusão, pode ser necessário remover uma chave do pai e
            // propagar o underflow para cima.
        }

        // Se a raiz se tornar vazia (após fusões), o único filho se torna a nova raiz
        if (root.getKeys().isEmpty() && !root.isLeaf()) {
            BPlusTreeInternalNode internalRoot = (BPlusTreeInternalNode) root;
            if (!internalRoot.getChildren().isEmpty()) {
                 root = internalRoot.getChildren().get(0);
            } else {
                 root = new BPlusTreeLeafNode(order); // Árvore vazia
            }
        }
        return true;
    }
}