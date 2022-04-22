package Genetico;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

class Vertice {
    public int identificador;
    public int x, y;
    public boolean visitado;

    public Vertice(){
        super();
    }

    public Vertice (int identificador, int x, int y){
        this.identificador = identificador;
        this.x = x;
        this.y = y;
    }

    public int calculaDistancia(Vertice verticeProximo){
        int x = verticeProximo.x - this.x;
        int y = verticeProximo.y - this.y;
        return (int) Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }
}

class Caminho{
    ArrayList<Vertice> caminho = new ArrayList<Vertice>();
    int fitness;

    public void calcula_fitness(){
        this.fitness = 0;
        for(int i = 0; i < this.caminho.size()-1; i++) this.fitness = this.fitness + this.caminho.get(i).calculaDistancia(this.caminho.get(i+1));
    }
}

class Main {
    //Criação das tabelas em .txt de cada combinação dos algoritmos
    //Utilizamos o arquivo para gerar um gráfico no excel e apresenta-lo no relatório
    private static PrintWriter criaTabela(String nomeArquivo){
        try{
            FileWriter leitor = new FileWriter(nomeArquivo.concat(".txt"));
            PrintWriter bufferWriter = new PrintWriter(leitor);
            bufferWriter.println("Ciclo,Melhoria,Otimizado");
            return bufferWriter;
        }catch(IOException ex){
            System.out.println("Falha na leitura do arquivo");
            System.exit(0);
            return null;
        }
    }

    //Retorna a lista selecionada, usada para conseguir usar um for nas funções operadores
    private static Caminho select_list(int i, Caminho individuo_1, Caminho individuo_2){
        if(i == 1) return individuo_1;
        else return individuo_2;
    }
   
    //Cria um array com n números randomicos diferentes em um intervalo de 0 a list_size
    private static ArrayList<Integer> n_random_numbers(int n, int list_size){
        Random random = new Random();
        Set<Integer>buffer = new LinkedHashSet<Integer>();
        while (buffer.size() < n) {
            buffer.add(random.nextInt(list_size));
        }
        ArrayList<Integer> posicao_array = new ArrayList<Integer>(buffer);
        return posicao_array;
    }
    
    //Seleção dos indivíduos a partir da técnica do selecao_torneio
    //Dentre k indivíduos aleatórios da população selecione o que houver melhor fitness
    private static Caminho selecao_torneio(int k_individuos, ArrayList<Caminho> populacao){
        int min_fitness = Integer.MAX_VALUE;
        Caminho min_caminho = null;

        ArrayList<Integer> posicao_array = n_random_numbers(k_individuos, populacao.size());

        for(int i = 0; i < posicao_array.size(); i++){
            Caminho individuo = populacao.get(i);
            if(min_fitness < individuo.fitness){
                min_fitness = individuo.fitness;
                min_caminho = individuo;
            }
        }
        return min_caminho;
    }

    //Operador de Cruzamento --> produz 2 filhos a partir de 2 pais
    private static void operador_Ox2(Caminho pai_1, Caminho pai_2){
        int quantidade_posicoes = 3;

        Caminho filho_1 = new Caminho();
        Caminho filho_2 = new Caminho();

        for(int i = 1; i <= 2; i++){
            ArrayList<Integer> posicao_array = n_random_numbers(quantidade_posicoes, (select_list((i % 2) + 1, pai_1, pai_2).caminho.size()));

            posicao_array.forEach((posicao) -> System.out.print(posicao + " "));
            System.out.println();
            
            Caminho filho_i = select_list(i, filho_1, filho_2);
            filho_i.caminho.addAll(select_list(i, pai_1, pai_2).caminho);
            
            ArrayList<Integer> conteudo_array = new ArrayList<Integer>();

            for(int j = 0; j < posicao_array.size(); j++){
                conteudo_array.add(select_list(i,pai_1, pai_2).caminho.indexOf(select_list((i % 2) + 1, pai_1, pai_2).caminho.get(posicao_array.get(j))));
            }

            Collections.sort(conteudo_array);

            for(int k = 0; k < conteudo_array.size(); k++){
                filho_i.caminho.set(conteudo_array.get(k), select_list((i % 2) + 1, pai_1, pai_2).caminho.get(posicao_array.get(k)));
            }
        }

        filho_1.calcula_fitness();
        filho_2.calcula_fitness();

        System.out.println("Filho1: ");
        filho_1.caminho.forEach((vertice) -> System.out.print(vertice.identificador + " -> "));
        System.out.println("\nFilho2: ");
        filho_2.caminho.forEach((vertice) -> System.out.print(vertice.identificador + " -> "));
    }

    //Mutação de um indivíduo a partir da troca dos valores de duas posições
    private static void gerar_mutacao(Caminho individuo){
        ArrayList<Integer> posicao_array = n_random_numbers(2, individuo.caminho.size());

        Collections.swap(individuo.caminho, posicao_array.get(0), posicao_array.get(1));
    }

    public static void main(String[] args) throws Exception {
        ArrayList<Vertice> listVertice = new ArrayList<Vertice>();
        HashMap<String,Double> resultado = new HashMap<String,Double>();
        
        //String entrada = args[0];
        String entrada = "a280.tsp";
        
        Scanner readerOtimo = new Scanner(new FileReader("Genetico/resultados.txt"));
        Scanner reader = new Scanner(new FileReader("Genetico/".concat(entrada)));

        //Leitura do arquivo resultados.txt, para calcular a precisão dos algoritmos
        while(readerOtimo.hasNextLine()){
            String[] linha = readerOtimo.nextLine().split("[:]");
            resultado.put(linha[0],Double.parseDouble(linha[1]));
        }
        readerOtimo.close();

        //Pula a leitura das 6 primeiras linhas
        while(!reader.hasNextInt()) reader.nextLine();
        
        while(reader.hasNextInt()){
            int identificador;
            int x, y;

            identificador = reader.nextInt();
            x = reader.nextInt();
            y = reader.nextInt();

            listVertice.add(new Vertice(identificador, x, y));
        }

        reader.close();

        Caminho pai_1 = new Caminho();
        Caminho pai_2 = new Caminho();

        pai_1.caminho.add(0, listVertice.get(5)); //6
        pai_1.caminho.add(1, listVertice.get(2)); //3
        pai_1.caminho.add(2, listVertice.get(4)); //5
        pai_1.caminho.add(3, listVertice.get(0)); //1
        pai_1.caminho.add(4, listVertice.get(1)); //2
        pai_1.caminho.add(5, listVertice.get(3)); //4
        pai_1.caminho.add(6, listVertice.get(7)); //8
        pai_1.caminho.add(7, listVertice.get(6)); //7

        pai_2.caminho.add(0, listVertice.get(7)); //8
        pai_2.caminho.add(1, listVertice.get(4)); //5
        pai_2.caminho.add(2, listVertice.get(1)); //2
        pai_2.caminho.add(3, listVertice.get(3)); //4
        pai_2.caminho.add(4, listVertice.get(2)); //3
        pai_2.caminho.add(5, listVertice.get(0)); //1
        pai_2.caminho.add(6, listVertice.get(6)); //7
        pai_2.caminho.add(7, listVertice.get(5)); //6

        operador_Ox2(pai_1, pai_2);

        //Execução dos algoritmos construtivos          
        System.out.println("-------------- Algoritmo Genético --------------");
    }
}