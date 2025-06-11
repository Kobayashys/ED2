import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Interface para padronizar os algoritmos de ordenação.
 * Cada implementação de algoritmo deve fornecer seu próprio método de ordenação
 * e rastrear movimentos e comparações.
 */
interface SortingAlgorithm {
    String getName(); // Retorna o nome do algoritmo
    void sort(int[] arr); // Método principal de ordenação
    long getMovements(); // Retorna a quantidade de movimentos
    long getComparisons(); // Retorna a quantidade de comparações
    void resetMetrics(); // Reseta as métricas para uma nova execução
}

/**
 * Classe para armazenar as métricas de desempenho de um algoritmo de ordenação.
 */
class SortMetrics {
    private String algorithmName;
    private long executionTimeMillis;
    private long movements;
    private long comparisons;
    private String scenario; // Cenário: "Caso Médio", "Melhor Caso", "Pior Caso"

    public SortMetrics(String algorithmName, long executionTimeMillis, long movements, long comparisons, String scenario) {
        this.algorithmName = algorithmName;
        this.executionTimeMillis = executionTimeMillis;
        this.movements = movements;
        this.comparisons = comparisons;
        this.scenario = scenario;
    }

    // Getters
    public String getAlgorithmName() { return algorithmName; }
    public long getExecutionTimeMillis() { return executionTimeMillis; }
    public long getMovements() { return movements; }
    public long getComparisons() { return comparisons; }
    public String getScenario() { return scenario; }

    /**
     * Formata o tempo de execução em HH:MM:SS:mm.
     * @return String formatada do tempo.
     */
    public String getFormattedTime() {
        long hours = executionTimeMillis / 3600000;
        long minutes = (executionTimeMillis % 3600000) / 60000;
        long seconds = ((executionTimeMillis % 3600000) % 60000) / 1000;
        long milliseconds = executionTimeMillis % 1000;
        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds);
    }

    @Override
    public String toString() {
        return String.format("%-15s | %-20s | %-15d | %-15d | %s",
                algorithmName, getFormattedTime(), movements, comparisons, scenario);
    }
}

/**
 * Implementação do algoritmo Quick Sort.
 */
class QuickSort implements SortingAlgorithm {
    private long movements;
    private long comparisons;

    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public void sort(int[] arr) {
        resetMetrics();
        quickSort(arr, 0, arr.length - 1);
    }

    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = randomizedPartition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    // Novo método para escolher pivô aleatório
    private int randomizedPartition(int[] arr, int low, int high) {
        int pivotIndex = low + (int)(Math.random() * (high - low + 1));
        // Troca o pivô aleatório com o último elemento
        int temp = arr[pivotIndex];
        arr[pivotIndex] = arr[high];
        arr[high] = temp;
        movements += 3; // Conta os movimentos da troca
        return partition(arr, low, high);
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high]; // Escolhe o último elemento como pivô
        int i = (low - 1); // Índice do menor elemento

        for (int j = low; j < high; j++) {
            comparisons++; // Uma comparação para cada elemento com o pivô
            if (arr[j] <= pivot) {
                i++;
                // Troca arr[i] e arr[j]
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                movements += 3; // 3 movimentos para a troca (leitura, escrita, escrita)
            }
        }

        // Troca arr[i+1] e arr[high] (ou pivô)
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        movements += 3; // 3 movimentos para a troca

        return i + 1;
    }

    @Override
    public long getMovements() {
        return movements;
    }

    @Override
    public long getComparisons() {
        return comparisons;
    }

    @Override
    public void resetMetrics() {
        this.movements = 0;
        this.comparisons = 0;
    }
}

/**
 * Implementação do algoritmo Merge Sort.
 */
class MergeSort implements SortingAlgorithm {
    private long movements;
    private long comparisons;

    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    public void sort(int[] arr) {
        resetMetrics();
        mergeSort(arr, 0, arr.length - 1);
    }

    private void mergeSort(int[] arr, int l, int r) {
        if (l < r) {
            int m = (l + r) / 2;
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);
            merge(arr, l, m, r);
        }
    }

    private void merge(int[] arr, int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; ++i) {
            L[i] = arr[l + i];
            movements++; // Movimento de cópia para o array auxiliar
        }
        for (int j = 0; j < n2; ++j) {
            R[j] = arr[m + 1 + j];
            movements++; // Movimento de cópia para o array auxiliar
        }

        int i = 0, j = 0;
        int k = l;
        while (i < n1 && j < n2) {
            comparisons++; // Uma comparação para L[i] <= R[j]
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            movements++; // Movimento de escrita no array original
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            movements++; // Movimento de escrita no array original
            i++;
            k++;
        }

        while (j < n2) {
            arr[k] = R[j];
            movements++; // Movimento de escrita no array original
            j++;
            k++;
        }
    }

    @Override
    public long getMovements() {
        return movements;
    }

    @Override
    public long getComparisons() {
        return comparisons;
    }

    @Override
    public void resetMetrics() {
        this.movements = 0;
        this.comparisons = 0;
    }
}

/**
 * Implementação do algoritmo Radix Sort (LSD - Least Significant Digit).
 * Este Radix Sort é adaptado para lidar com números negativos, separando-os e
 * ordenando-os de forma diferente.
 */
class RadixSort implements SortingAlgorithm {
    private long movements;
    private long comparisons; // Radix Sort não faz comparações no sentido tradicional

    @Override
    public String getName() {
        return "Radix Sort";
    }

    @Override
    public void sort(int[] arr) {
        resetMetrics();
        if (arr == null || arr.length == 0) {
            return;
        }

        // Separa números positivos e negativos
        ArrayList<Integer> positives = new ArrayList<>();
        ArrayList<Integer> negatives = new ArrayList<>();
        for (int num : arr) {
            if (num >= 0) {
                positives.add(num);
            } else {
                negatives.add(Math.abs(num)); // Trabalha com o valor absoluto para negativos
            }
        }

        // Converte ArrayList para array primitivo para ordenação
        int[] posArr = positives.stream().mapToInt(Integer::intValue).toArray();
        int[] negArr = negatives.stream().mapToInt(Integer::intValue).toArray();

        // Ordena os números positivos
        if (posArr.length > 0) {
            radixSortPositive(posArr);
        }

        // Ordena os números negativos (se houver) e os reverte
        if (negArr.length > 0) {
            radixSortPositive(negArr); // Ordena os valores absolutos
            // Reverter a ordem dos negativos e torná-los negativos novamente
            for (int i = 0; i < negArr.length / 2; i++) {
                int temp = negArr[i];
                negArr[i] = -negArr[negArr.length - 1 - i];
                negArr[negArr.length - 1 - i] = -temp;
                movements += 6; // 6 movimentos para a troca de 2 elementos negativos
            }
            if (negArr.length % 2 == 1) { // Se o número de elementos for ímpar, o elemento do meio precisa ser negado
                negArr[negArr.length / 2] = -negArr[negArr.length / 2];
                movements += 2;
            }
            // Para garantir que os negativos fiquem em ordem crescente (ex: -5, -3, -1)
            // Se negArr era [1, 3, 5] (valores absolutos ordenados), precisamos que seja [-5, -3, -1]
            // Então, invertemos e negamos
            for (int i = 0; i < negArr.length; i++) {
                negArr[i] = -negArr[i]; // Converte de volta para negativo
            }
            // A ordem final para negativos será decrescente nos valores absolutos,
            // o que se traduz em ordem crescente nos valores negativos.
            // Ex: [5, 3, 1] -> [-5, -3, -1]
            for (int i = 0; i < negArr.length / 2; i++) {
                int temp = negArr[i];
                negArr[i] = negArr[negArr.length - 1 - i];
                negArr[negArr.length - 1 - i] = temp;
                movements += 3;
            }
        }


        // Combina os arrays ordenados (negativos e positivos) no array original
        int current = 0;
        for (int num : negArr) {
            arr[current++] = num;
            movements++; // Movimento de escrita
        }
        for (int num : posArr) {
            arr[current++] = num;
            movements++; // Movimento de escrita
        }
    }

    private void radixSortPositive(int[] arr) {
        int max = getMax(arr);

        // Faz o counting sort para cada dígito.
        // exp é 10^i onde i é o número do dígito atual
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countSort(arr, exp);
        }
    }

    private int getMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            comparisons++; // Consideramos uma comparação para encontrar o máximo
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    private void countSort(int[] arr, int exp) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10]; // 10 dígitos (0-9)

        Arrays.fill(count, 0); // Inicializa o array de contagem com zeros

        // Armazena a contagem de ocorrências em count[]
        for (int i = 0; i < n; i++) {
            count[(arr[i] / exp) % 10]++;
            movements++; // Leitura para determinar o dígito
        }

        // Modifica count[i] para que count[i] contenha a posição real deste dígito no array de saída
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
            movements++; // Escrita em count[]
        }

        // Constrói o array de saída
        for (int i = n - 1; i >= 0; i--) {
            output[count[(arr[i] / exp) % 10] - 1] = arr[i];
            count[(arr[i] / exp) % 10]--;
            movements += 2; // Leitura de arr[i] e escrita em output[]
        }

        // Copia os elementos de output[] para arr[], para que arr[] contenha números ordenados
        // de acordo com o dígito atual
        for (int i = 0; i < n; i++) {
            arr[i] = output[i];
            movements++; // Leitura de output[] e escrita em arr[]
        }
    }

    @Override
    public long getMovements() {
        return movements;
    }

    @Override
    public long getComparisons() {
        return comparisons;
    }

    @Override
    public void resetMetrics() {
        this.movements = 0;
        this.comparisons = 0;
    }
}

/**
 * Classe utilitária para lidar com leitura e escrita de arquivos.
 */
class FileHandler {
    /**
     * Lê um arquivo de texto contendo números inteiros separados por vírgulas e colchetes.
     * Ex: [1,2,3,4,5]
     * @param filename O nome do arquivo a ser lido.
     * @return Um array de inteiros lido do arquivo.
     * @throws IOException Se ocorrer um erro durante a leitura do arquivo.
     */
    public static int[] readNumbersFromFile(String filename) throws IOException {
        ArrayList<Integer> numbers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove colchetes e espaços e divide por vírgula
                line = line.trim().replaceAll("[\\[\\]\\s]", "");
                if (line.isEmpty()) continue; // Pula linhas vazias

                String[] numStrings = line.split(",");
                for (String numStr : numStrings) {
                    if (!numStr.trim().isEmpty()) {
                        numbers.add(Integer.parseInt(numStr.trim()));
                    }
                }
            }
        }
        return numbers.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Grava um array de inteiros em um arquivo de texto, com um número por linha.
     * @param filename O nome do arquivo a ser escrito.
     * @param data O array de inteiros a ser gravado.
     * @throws IOException Se ocorrer um erro durante a escrita do arquivo.
     */
    public static void writeNumbersToFile(String filename, int[] data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (int num : data) {
                bw.write(String.valueOf(num));
                bw.newLine();
            }
        }
    }
}

/**
 * Classe principal para executar e comparar os algoritmos de ordenação.
 */
public class Main {

    private static final String INPUT_FILE = "dados500_mil.txt";
    private static final String OUTPUT_PREFIX = "sorted_";
    private static final String OUTPUT_SUFFIX = ".txt";

    public static void main(String[] args) {
        System.out.println("Iniciando comparação de algoritmos de ordenação...");

        ArrayList<SortMetrics> results = new ArrayList<>();

        try {
            // 1. Ler o arquivo de dados original (Caso Médio)
            System.out.println("Lendo arquivo original: " + INPUT_FILE);
            int[] originalData = FileHandler.readNumbersFromFile(INPUT_FILE);
            System.out.println("Arquivo lido. Total de " + originalData.length + " números.");

            // Criar arrays para os cenários
            int[] mediumCaseData = Arrays.copyOf(originalData, originalData.length);
            int[] bestCaseData = Arrays.copyOf(originalData, originalData.length);
            int[] worstCaseData = Arrays.copyOf(originalData, originalData.length);
            
            // Preparar dados para o "Melhor Caso": ordenar uma vez para reuso
            Arrays.sort(bestCaseData); // Usar Arrays.sort para criar um array já ordenado
            
            // Preparar dados para o "Pior Caso": ordenar e depois inverter o array
            Arrays.sort(worstCaseData); // Ordena primeiro
            int n = worstCaseData.length;
            for (int i = 0; i < n / 2; i++) {
                int temp = worstCaseData[i];
                worstCaseData[i] = worstCaseData[n - 1 - i];
                worstCaseData[n - 1 - i] = temp;
            }

            // Cenários a serem testados
            String[] scenarios = {"Caso Médio", "Melhor Caso", "Pior Caso"};
            int[][] dataSets = {mediumCaseData, bestCaseData, worstCaseData};

            SortingAlgorithm[] algorithms = {
                new QuickSort(),
                new MergeSort(),
                new RadixSort()
            };

            for (int i = 0; i < scenarios.length; i++) {
                String currentScenario = scenarios[i];
                int[] currentData = dataSets[i];

                System.out.println("\n--- Testando Cenário: " + currentScenario + " ---");

                for (SortingAlgorithm algo : algorithms) {
                    // Criar uma cópia do array para cada execução do algoritmo
                    // para garantir que o algoritmo sempre opere no mesmo conjunto de dados inicial para o cenário
                    int[] dataCopy = Arrays.copyOf(currentData, currentData.length);

                    System.out.println("Executando " + algo.getName() + "...");
                    Instant start = Instant.now(); // Início da medição de tempo
                    algo.sort(dataCopy); // Executa a ordenação
                    Instant end = Instant.now(); // Fim da medição de tempo

                    long timeElapsed = Duration.between(start, end).toMillis(); // Tempo em milissegundos

                    // Armazenar os resultados
                    results.add(new SortMetrics(algo.getName(), timeElapsed, algo.getMovements(), algo.getComparisons(), currentScenario));

                    // Gravar os dados ordenados em um novo arquivo
                    String outputFilename = OUTPUT_PREFIX + algo.getName().replaceAll(" ", "_") + "_" + currentScenario.replaceAll(" ", "_") + OUTPUT_SUFFIX;
                    FileHandler.writeNumbersToFile(outputFilename, dataCopy);
                    System.out.println("Dados ordenados por " + algo.getName() + " salvos em: " + outputFilename);
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler/gravar o arquivo: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        // Exibir a tabela de resultados
        System.out.println("\n--- Tabela de Resultados da Ordenação ---");
        System.out.println(String.format("%-15s | %-20s | %-15s | %-15s | %s",
                "Algoritmo", "Tempo (HH:MM:SS:mm)", "Movimentos", "Comparações", "Cenário"));
        System.out.println("---------------------------------------------------------------------------------------------------");
        for (SortMetrics metric : results) {
            System.out.println(metric);
        }

        System.out.println("\nComparação de algoritmos de ordenação concluída.");
    }
}
