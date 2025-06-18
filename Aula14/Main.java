import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        String dataFilePath = "produtos_corrigido.txt";
        List<Produto> produtos = carregarDadosDoArquivo(dataFilePath);

        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado no arquivo. Abortando.");
            return;
        }

        System.out.println("--- Testando B+ Tree (Ordem 3) ---");
        BPlusTree bPlusTree = new BPlusTree(3);
        long startTimeBPlus = System.nanoTime();
        for (Produto p : produtos) {
            bPlusTree.insert(p);
        }
        long endTimeBPlus = System.nanoTime();
        long durationBPlus = (endTimeBPlus - startTimeBPlus) / 1_000_000;
        System.out.println("Tempo de inserção na B+ Tree: " + durationBPlus + " ms");

        System.out.println("\nRemovendo 10 produtos aleatórios (ID entre 1000 e 2000) da B+ Tree...");
        Random random = new Random();
        List<Integer> idsToRemoveBPlus = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            idsToRemoveBPlus.add(random.nextInt(1001) + 1000);
        }

        long startTimeBPlusRemoval = System.nanoTime();
        for (int id : idsToRemoveBPlus) {
            Produto found = (Produto) bPlusTree.search(id);
            if (found != null) {
                System.out.println("Produto encontrado na B+ Tree: " + found);
                bPlusTree.delete(id);
            } else {
                System.out.println("Produto com ID " + id + " não encontrado na B+ Tree.");
            }
        }
        long endTimeBPlusRemoval = System.nanoTime();
        long durationBPlusRemoval = (endTimeBPlusRemoval - startTimeBPlusRemoval) / 1_000_000;
        System.out.println("Tempo de remoção na B+ Tree: " + durationBPlusRemoval + " ms");

        System.out.println("\n" + new String(new char[40]).replace('\0', '-') + "\n");

        System.out.println("--- Testando B* Tree (Ordem 3) ---");
        BStarTree bStarTree = new BStarTree(3);
        long startTimeBStar = System.nanoTime();
        for (Produto p : produtos) {
            bStarTree.insert(p);
        }
        long endTimeBStar = System.nanoTime();
        long durationBStar = (endTimeBStar - startTimeBStar) / 1_000_000;
        System.out.println("Tempo de inserção na B* Tree: " + durationBStar + " ms");

        System.out.println("\nRemovendo 10 produtos aleatórios (ID entre 1000 e 2000) da B* Tree...");
        List<Integer> idsToRemoveBStar = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            idsToRemoveBStar.add(random.nextInt(1001) + 1000);
        }

        long startTimeBStarRemoval = System.nanoTime();
        for (int id : idsToRemoveBStar) {
            Produto found = bStarTree.search(id);
            if (found != null) {
                System.out.println("Produto encontrado na B* Tree: " + found);
                bStarTree.delete(id);
            } else {
                System.out.println("Produto com ID " + id + " não encontrado na B* Tree.");
            }
        }
        long endTimeBStarRemoval = System.nanoTime();
        long durationBStarRemoval = (endTimeBStarRemoval - startTimeBStarRemoval) / 1_000_000;
        System.out.println("Tempo de remoção na B* Tree: " + durationBStarRemoval + " ms");
    }

    private static List<Produto> carregarDadosDoArquivo(String filePath) {
        List<Produto> produtos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String nome = parts[1].trim();
                        String categoria = parts[2].trim();
                        produtos.add(new Produto(id, nome, categoria));
                    } catch (NumberFormatException e) {
                        System.err.println("Erro ao parsear ID: " + parts[0] + " na linha: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de dados: " + e.getMessage());
        }
        return produtos;
    }
}