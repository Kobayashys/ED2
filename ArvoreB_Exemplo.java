// Classe para representar um Livro
class Livro {
    String titulo;
    String autor;
    String isbn; // Usaremos ISBN como a chave para a Árvore B

    public Livro(String titulo, String autor, String isbn) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
    }

    // Método para comparar dois livros pelo ISBN
    // Retorna < 0 se este.isbn < outroLivro.isbn, > 0 se este.isbn > outroLivro.isbn, 0 se iguais
    public int compararISBN(Livro outroLivro) {
        return compareStrings(this.isbn, outroLivro.isbn);
    }

    // Método auxiliar para comparar strings (para ISBN e título) sem usar String.compareTo
    private static int compareStrings(String s1, String s2) {
        int minLength = Math.min(s1.length(), s2.length());
        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) < s2.charAt(i)) {
                return -1;
            } else if (s1.charAt(i) > s2.charAt(i)) {
                return 1;
            }
        }
        if (s1.length() < s2.length()) {
            return -1;
        } else if (s1.length() > s2.length()) {
            return 1;
        } else {
            return 0;
        }
    }
}

// Classe para representar um nó da Árvore B
class NoArvoreB {
    Livro[] chaves; // Array para armazenar até 4 objetos Livro 
    NoArvoreB[] filhos; // Array para armazenar até 5 filhos 
    int numChaves; // Número atual de chaves no nó 
    boolean ehFolha; // Indica se o nó é uma folha 

    // Construtor
    public NoArvoreB() {
        chaves = new Livro[4]; // Array para armazenar até 4 chaves 
        filhos = new NoArvoreB[5]; // Array para armazenar até 5 filhos 
        numChaves = 0; // Número atual de chaves no nó 
        ehFolha = true; // Indica se o nó é uma folha 

        // Inicialização dos arrays (Livro e NoArvoreB são nulos por padrão em Java, mas manter a lógica)
        for (int i = 0; i < 4; i++) {
            chaves[i] = null;
        }
        for (int i = 0; i < 5; i++) {
            filhos[i] = null;
        }
    }

    // Método para verificar se o nó está cheio 
    public boolean estaCheio() {
        return numChaves == 4; // Retorne numChaves == 4 
    }

    // Método para inserir uma chave (Livro) em um nó folha 
    public void inserirChave(Livro novoLivro) {
        int i = numChaves - 1; // inteiro i = numChaves - 1 

        // Move as chaves maiores para a direita 
        while (i >= 0 && chaves[i].compararISBN(novoLivro) > 0) { // enquanto i >= 0 e chaves[i] > chave faca 
            chaves[i + 1] = chaves[i]; // chaves[i + 1] = chaves[i] 
            i = i - 1; // i = i - 1 
        }

        // Insere a nova chave 
        chaves[i + 1] = novoLivro; // chaves[i + 1] = chave 
        numChaves = numChaves + 1; // numChaves = numChaves + 1 
    }

    // Método para dividir um nó filho cheio 
    public NoArvoreB dividirFilho(int indice) {
        NoArvoreB novoNo = new NoArvoreB(); // NoArvoreB novoNo = novo NoArvoreB() 
        NoArvoreB filhoCheio = filhos[indice]; // NoArvoreB filhoCheio = filhos[indice] 

        novoNo.ehFolha = filhoCheio.ehFolha; // novoNo.ehFolha = filhoCheio.ehFolha 
        novoNo.numChaves = 2; // novoNo.numChaves = 2 

        // Move as últimas 2 chaves (Livros) para o novo nó 
        for (int i = 0; i < 2; i++) { // para i de 0 ate 1 faca 
            novoNo.chaves[i] = filhoCheio.chaves[i + 2]; // novoNo.chaves[i] = filhoCheio.chaves[i + 2] 
        }

        // Se não é folha, move os filhos também 
        if (!filhoCheio.ehFolha) { // se nao filhoCheio.ehFolha entao 
            for (int i = 0; i < 3; i++) { // para i de 0 ate 2 faca 
                novoNo.filhos[i] = filhoCheio.filhos[i + 2]; // novoNo.filhos[i] = filhoCheio.filhos[i + 2] 
            }
        }

        filhoCheio.numChaves = 2; // filhoCheio.numChaves = 2 

        // Move os filhos do nó atual para abrir espaço 
        for (int i = numChaves; i >= indice + 1; i--) { // para i de numChaves ate indice + 1 faca 
            filhos[i + 1] = filhos[i]; // filhos[i + 1] = filhos[i] 
        }

        filhos[indice + 1] = novoNo; // filhos[indice + 1] = novoNo 

        // Move as chaves para abrir espaço para a chave promovida 
        for (int i = numChaves - 1; i >= indice; i--) { // para i de numChaves - 1 ate indice faca 
            chaves[i + 1] = chaves[i]; // chaves[i + 1] = chaves[i] 
        }

        // Promove a chave do meio 
        chaves[indice] = filhoCheio.chaves[2]; // chaves[indice] = filhoCheio.chaves[2] 
        numChaves = numChaves + 1; // numChaves = numChaves + 1 

        return novoNo; // retorne novoNo 
    }

    // Método para exibir as chaves (Livros) do nó 
    public void exibirNo() {
        System.out.print("["); // escreva("[") 
        for (int i = 0; i < numChaves; i++) { // para i de 0 ate numChaves - 1 faca 
            System.out.print(chaves[i].isbn + " (" + chaves[i].titulo + ")"); // escreva (chaves[i]) 
            if (i < numChaves - 1) { // se i < numChaves - 1 entao 
                System.out.print(", "); // escreva(", ") 
            }
        }
        System.out.print("]"); // escreva("]") 
    }
}

// Custom List implementation to avoid java.util.ArrayList (if strictly adhering to "no native libraries")
class ListaLivros {
    private Livro[] elementos;
    private int tamanhoAtual;
    private static final int CAPACIDADE_INICIAL = 10;

    public ListaLivros() {
        elementos = new Livro[CAPACIDADE_INICIAL];
        tamanhoAtual = 0;
    }

    public void adicionar(Livro livro) {
        if (tamanhoAtual == elementos.length) {
            // Aumentar capacidade (simples duplicação)
            Livro[] novoArray = new Livro[elementos.length * 2];
            for (int i = 0; i < elementos.length; i++) {
                novoArray[i] = elementos[i];
            }
            elementos = novoArray;
        }
        elementos[tamanhoAtual++] = livro;
    }

    public Livro obter(int indice) {
        if (indice < 0 || indice >= tamanhoAtual) {
            // Poderia lançar uma exceção, mas para simplicidade, retorna nulo
            return null;
        }
        return elementos[indice];
    }

    public void definir(int indice, Livro livro) {
        if (indice >= 0 && indice < tamanhoAtual) {
            elementos[indice] = livro;
        }
    }

    public int tamanho() {
        return tamanhoAtual;
    }
}


// Classe principal da Árvore B
class ArvoreB {
    NoArvoreB raiz; // NoArvoreB raiz 

    // Construtor 
    public ArvoreB() {
        raiz = new NoArvoreB(); // raiz = novo NoArvoreB() 
    }

    // Método público para inserir uma chave (Livro) 
    public void inserir(Livro novoLivro) {
        if (raiz.estaCheio()) { // se raiz.estaCheio() entao 
            NoArvoreB novaRaiz = new NoArvoreB(); // NoArvoreB novaRaiz = novo NoArvoreB() 
            novaRaiz.ehFolha = false; // novaRaiz.ehFolha = falso 
            novaRaiz.filhos[0] = raiz; // novaRaiz.filhos[0] = raiz 
            novaRaiz.dividirFilho(0); // novaRaiz.dividirFilho(0) 
            raiz = novaRaiz; // raiz = novaRaiz 
        }
        inserirNaoCheio(raiz, novoLivro); // inserirNaoCheio(raiz, chave) 
    }

    // Método auxiliar para inserir em nó não cheio 
    private void inserirNaoCheio(NoArvoreB no, Livro novoLivro) {
        int i = no.numChaves - 1; // inteiro i = no.numChaves - 1 

        if (no.ehFolha) { // se no.ehFolha entao 
            no.inserirChave(novoLivro); // no.inserirChave(chave) 
        } else {
            // Encontra o filho onde a chave deve ser inserida 
            while (i >= 0 && novoLivro.compararISBN(no.chaves[i]) < 0) { // enquanto i >= 0 e chave < no.chaves[i] faca 
                i = i - 1; // i = i - 1 
            }
            i = i + 1; // i = i + 1 

            // Se o filho está cheio, divide-o 
            if (no.filhos[i].estaCheio()) { // se no.filhos[i].estaCheio() entao 
                no.dividirFilho(i); // no.dividirFilho(i) 

                if (novoLivro.compararISBN(no.chaves[i]) > 0) { // se chave > no.chaves[i] entao 
                    i = i + 1; // i = i + 1 
                }
            }
            inserirNaoCheio(no.filhos[i], novoLivro); // inserirNaoCheio (no.filhos[i], chave) 
        }
    }

    // Método para buscar uma chave (ISBN) 
    public Livro buscar(String isbnBuscado) {
        return buscarNo(raiz, isbnBuscado); // retorne buscarNo(raiz, chave) 
    }

    // Método auxiliar para buscar em um nó 
    private Livro buscarNo(NoArvoreB no, String isbnBuscado) {
        int i = 0; // inteiro i = 0 

        // Encontra a primeira chave maior ou igual à chave procurada 
        while (i < no.numChaves && Livro.compareStrings(isbnBuscado, no.chaves[i].isbn) > 0) { // enquanto i < no.numChaves e chave > no.chaves[i] faca 
            i = i + 1; // i = i + 1 
        }

        // Se encontrou a chave 
        if (i < no.numChaves && Livro.compareStrings(isbnBuscado, no.chaves[i].isbn) == 0) { // se i < no.numChaves e chave == no.chaves[i] entao 
            return no.chaves[i]; // retorne verdadeiro 
        }

        // Se é folha e não encontrou, a chave não existe 
        if (no.ehFolha) { // se no.ehFolha entao 
            return null; // retorne falso 
        }

        // Busca recursivamente no filho apropriado 
        return buscarNo(no.filhos[i], isbnBuscado); // retorne buscarNo (no.filhos[i], chave) 
    }

    // Método para exibir a árvore em ordem (por ISBN, mas depois a lista será ordenada por título)
    public void exibirEmOrdemPorTitulo() {
        System.out.println("Livros em ordem alfabética por título:");
        ListaLivros listaDeLivros = new ListaLivros();

        // Percorre a árvore e coleta todos os livros
        coletarLivrosEmOrdem(raiz, listaDeLivros);

        // Ordena a lista de livros por título (usando Bubble Sort implementado manualmente)
        ordenarListaLivrosPorTitulo(listaDeLivros);

        // Exibe os livros ordenados
        for (int i = 0; i < listaDeLivros.tamanho(); i++) {
            Livro livro = listaDeLivros.obter(i);
            System.out.println("Título: " + livro.titulo + ", Autor: " + livro.autor + ", ISBN: " + livro.isbn);
        }
    }

    // Método auxiliar para coletar livros (percorre em ordem de ISBN)
    private void coletarLivrosEmOrdem(NoArvoreB no, ListaLivros lista) {
        int i;
        for (i = 0; i < no.numChaves; i++) {
            if (!no.ehFolha) { // se nao no.ehFolha entao 
                coletarLivrosEmOrdem(no.filhos[i], lista); // exibirEmOrdemNo(no.filhos[i]) 
            }
            lista.adicionar(no.chaves[i]);
        }
        if (!no.ehFolha) { // se nao no.ehFolha entao 
            coletarLivrosEmOrdem(no.filhos[i], lista); // exibirEmOrdemNo(no.filhos[i]) 
        }
    }

    // Implementação de Bubble Sort para ordenar a ListaLivros por título
    private void ordenarListaLivrosPorTitulo(ListaLivros lista) {
        int n = lista.tamanho();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (Livro.compareStrings(lista.obter(j).titulo, lista.obter(j + 1).titulo) > 0) {
                    // Trocar Livros
                    Livro temp = lista.obter(j);
                    lista.definir(j, lista.obter(j + 1));
                    lista.definir(j + 1, temp);
                }
            }
        }
    }


    // Método para exibir a estrutura da árvore 
    public void exibirEstrutura() {
        System.out.println("Estrutura da Árvore B:"); // escreva("Estrutura da Árvore B:\n") 
        exibirEstruturaNo(raiz, 0); // exibirEstruturaNo(raiz, 0) 
    }

    // Método auxiliar para exibir estrutura 
    private void exibirEstruturaNo(NoArvoreB no, int nivel) {
        // Indentação baseada no nível 
        for (int i = 0; i < nivel; i++) { // para i de 0 ate nivel faca 
            System.out.print("  "); // escreva(" ") 
        }

        no.exibirNo(); // no.exibirNo() 
        System.out.println(); // escreva("\n") 

        // Se não é folha, exibe os filhos 
        if (!no.ehFolha) { // se nao no.ehFolha entao 
            for (int i = 0; i <= no.numChaves; i++) { // para i de 0 ate no.numChaves faca 
                exibirEstruturaNo(no.filhos[i], nivel + 1); // exibirEstruturaNo (no.filhos [i], nivel + 1) 
            }
        }
    }
}

// Programa principal
public class ArvoreB_Exemplo {

    public static void main(String[] args) {
        // Declaração de variáveis
        ArvoreB arvore = new ArvoreB(); // arvore = novo ArvoreB() 
        java.util.Scanner scanner = new java.util.Scanner(System.in); // Usar Scanner para entrada

        int opcao; // inteiro opcao 
        String chaveISBN; // Usado para buscar 
        Livro livroEncontrado; // logico encontrado 

        // Menu principal 
        do {
            System.out.println("\n=== ÁRVORE B (até 4 chaves por nó) ==="); // escreva("\n=== ÁRVORE B (até 4 chaves por nó) ===\n") 
            System.out.println("1. Inserir livro"); // escreva ("1. Inserir chave\n") 
            System.out.println("2. Buscar livro por ISBN"); // escreva ("2. Buscar chave\n") 
            System.out.println("3. Listar todos os livros (por título)"); // escreva ("3. Exibir em ordem\n") 
            System.out.println("4. Exibir estrutura da árvore"); // escreva ("4. Exibir estrutura\n") 
            System.out.println("5. Sair"); // escreva ("5. Sair\n") 
            System.out.print("Escolha uma opção: "); // escreva ("Escolha uma opção: ") 

            opcao = scanner.nextInt(); // leia(opcao) 
            scanner.nextLine(); // Consumir a nova linha

            switch (opcao) { // escolha opcao 
                case 1:
                    System.out.print("Digite o título do livro: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Digite o autor do livro: ");
                    String autor = scanner.nextLine();
                    System.out.print("Digite o ISBN do livro: ");
                    String isbn = scanner.nextLine();
                    Livro novoLivro = new Livro(titulo, autor, isbn);
                    arvore.inserir(novoLivro); // arvore.inserir(chave) 
                    System.out.println("Livro '" + titulo + "' (ISBN: " + isbn + ") inserido com sucesso!"); // escreva ("Chave" + chave + " inserida com sucesso!\n") 
                    break;
                case 2:
                    System.out.print("Digite o ISBN do livro a ser buscado: "); // escreva("Digite a chave a ser buscada: ") 
                    chaveISBN = scanner.nextLine(); // leia(chave) 
                    livroEncontrado = arvore.buscar(chaveISBN); // encontrado = arvore.buscar(chave) 

                    if (livroEncontrado != null) { // se encontrado entao 
                        System.out.println("Livro encontrado!"); // escreva("Chave" + chave + " encontrada na árvore!\n") 
                        System.out.println("Título: " + livroEncontrado.titulo);
                        System.out.println("Autor: " + livroEncontrado.autor);
                        System.out.println("ISBN: " + livroEncontrado.isbn);
                    } else {
                        System.out.println("Livro com ISBN " + chaveISBN + " não encontrado na árvore!"); // escreva ("Chave " + chave + " não encontrada na árvore!\n") 
                    }
                    break;
                case 3:
                    arvore.exibirEmOrdemPorTitulo(); // arvore.exibirEmOrdem() 
                    break;
                case 4:
                    arvore.exibirEstrutura(); // arvore.exibirEstrutura() 
                    break;
                case 5:
                    System.out.println("Encerrando programa..."); // escreva("Encerrando programa...\n") 
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente."); // escreva("Opção inválida! Tente novamente.\n") 
                    break;
            }
        } while (opcao != 5); // ate_que opcao == 5 

        scanner.close(); // Fechar o scanner
    }
}