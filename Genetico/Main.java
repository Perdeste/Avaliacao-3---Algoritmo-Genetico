package Genetico;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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

class Caminho implements Comparable{
    ArrayList<Vertice> caminho = new ArrayList<Vertice>();
    int fitness;

    public void calcula_fitness(){
        this.fitness = 0;
        for(int i = 0; i < this.caminho.size()-1; i++) this.fitness = this.fitness + this.caminho.get(i).calculaDistancia(this.caminho.get(i+1));
        this.fitness = this.fitness + this.caminho.get(this.caminho.size()-1).calculaDistancia(this.caminho.get(0));
    }

    @Override
    public int compareTo(Object comparestu){
        int compare_fitness=((Caminho)comparestu).fitness;
        return this.fitness-compare_fitness;
    }

}

class Main {
    private static void swap2OPT(Caminho saidaOPT, int verticeB, int i){
        Collections.swap(saidaOPT.caminho, verticeB, i);
        int a = verticeB+1;
        int b = i-1;
        while(a < b){
            Collections.swap(saidaOPT.caminho, a, b);
            a++;
            b--;
        }
    }

    private static Caminho melhorativo2OPT(Caminho saidaOut){
        //Algoritmo Melhorativo 2-OPT
        boolean temOPT = true;
        Caminho saidaOPT = new Caminho();
        saidaOPT.caminho.addAll(saidaOut.caminho);
        int melhoraAB = 0;
        int paradaAB = saidaOPT.caminho.size()-1;
        boolean stop = false;
    
        while(temOPT && !stop){
            //System.out.println(calculaPesoTotal(saidaOPT));
            //System.out.println(valorOtimo/calculaPesoTotal(saidaOPT));
            paradaAB = saidaOPT.caminho.size()-1;
            temOPT = false;
            for(int verticeA = melhoraAB; verticeA < paradaAB && !temOPT && !stop; verticeA++){
                if(melhoraAB > 0 && verticeA == saidaOPT.caminho.size()-2){
                    paradaAB = melhoraAB;
                    verticeA = 0;
                }
                int verticeB = verticeA + 1;
                for(int i = verticeB+1; i < saidaOPT.caminho.size()-1 && !temOPT && !stop; i++){
                    int j = i+1;
                    if(j != verticeA){
                        int menor = saidaOPT.caminho.get(verticeA).calculaDistancia(saidaOPT.caminho.get(verticeB)) + saidaOPT.caminho.get(i).calculaDistancia(saidaOPT.caminho.get(j));
                        int custo = (saidaOPT.caminho.get(verticeA).calculaDistancia(saidaOPT.caminho.get(i)) + saidaOPT.caminho.get(verticeB).calculaDistancia(saidaOPT.caminho.get(j)));
                        if(custo < menor){  
                            swap2OPT(saidaOPT, verticeB, i);     
                            temOPT = true;
                            melhoraAB = verticeA;
                        }   
                    
                    }
                }
            }         
        }
        return saidaOPT;
    }

    //Criação das tabelas em .txt de cada combinação dos algoritmos
    //Utilizamos o arquivo para gerar um gráfico no excel e apresenta-lo no relatório
    private static PrintWriter cria_tabela(String nomeArquivo){
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
    
    //Algoritmo Construtivo Vizinho mais próximo, servirá como método para a criação da população inicial
    private static Caminho construtivo_vizinho_proximo(ArrayList<Vertice> listVertice){   
        /**Inicializa:
         * i --> qual vertice começara (gerado o valor aleatoriamente de acordo com a quantidade de vértices)
         * menor --> menor peso do vértice (da posição i na lista) para o j
         */
        Caminho cromossomo = new Caminho();
        int menor = Integer.MAX_VALUE;
        int menorB = 0;     
        int A = new Random().nextInt(listVertice.size());
        Vertice vertice = listVertice.get(A);
        Vertice verticeProximo = new Vertice();
        listVertice.remove(A);
        cromossomo.caminho.add(vertice);
        while(!listVertice.isEmpty()){
            for(int B = 0; B < listVertice.size(); B++){
                verticeProximo = listVertice.get(B);
                int peso = vertice.calculaDistancia(verticeProximo);
                if(peso < menor){
                    menor = peso;
                    menorB = B;
                }               
            }
            vertice = listVertice.get(menorB);
            cromossomo.caminho.add(vertice);           
            listVertice.remove(menorB);
            menor = Integer.MAX_VALUE;
        }
        //cromossomo.caminho.add(cromossomo.caminho.get(0));
        cromossomo.calcula_fitness();

        return cromossomo;
    }

    //Seleção dos indivíduos a partir da técnica do selecao_torneio
    //Dentre k indivíduos aleatórios da população selecione o que houver melhor fitness
    private static Caminho selecao_torneio(int k_individuos, ArrayList<Caminho> populacao){
        int min_fitness = Integer.MAX_VALUE;
        Caminho min_caminho = null;

        ArrayList<Integer> posicao_array = n_random_numbers(k_individuos, populacao.size());

        for(int i = 0; i < posicao_array.size(); i++){
            Caminho individuo = populacao.get(i);
            if(min_fitness > individuo.fitness){
                min_fitness = individuo.fitness;
                min_caminho = individuo;
            }
        }

        populacao.remove(min_caminho);

        return min_caminho;
    }

    private static boolean confere_caminho(Caminho individuo, Caminho pivo){
        int i = 0;
        while(i < pivo.caminho.size()){
            if(individuo.caminho.get(i) != pivo.caminho.get(i)){
                return true;
            }       
            i++;   
        }
        return false;
    }

    //Operador de Cruzamento --> produz 2 filhos a partir de 2 pais
    private static void operador_Ox2(ArrayList<Caminho> populacao, int quantidade_posicoes, Caminho pai_1, Caminho pai_2){
        Caminho filho_1 = new Caminho();
        Caminho filho_2 = new Caminho();

        for(int i = 1; i <= 2; i++){
            ArrayList<Integer> posicao_array = n_random_numbers(quantidade_posicoes, (select_list((i % 2) + 1, pai_1, pai_2).caminho.size()));
            
            Caminho filho_i = select_list(i, filho_1, filho_2);
            filho_i.caminho.addAll(select_list(i, pai_1, pai_2).caminho);
            
            ArrayList<Integer> conteudo_array = new ArrayList<Integer>();

            for(int j = 0; j < posicao_array.size(); j++){
                conteudo_array.add(select_list(i,pai_1, pai_2).caminho.indexOf(select_list((i % 2) + 1, pai_1, pai_2).caminho.get(posicao_array.get(j))));
            }

            Collections.sort(conteudo_array);

            for(int k = 0; k < conteudo_array.size(); k++){
                //System.out.println("k = " + conteudo_array.get(k));
                filho_i.caminho.set(conteudo_array.get(k), select_list((i % 2) + 1, pai_1, pai_2).caminho.get(posicao_array.get(k)));
            }

            filho_i.calcula_fitness();
            
            populacao.add(filho_i);
        }

        // System.out.println("Filho1: ");
        // filho_1.caminho.forEach((vertice) -> System.out.print(vertice.identificador + " -> "));
        // System.out.println("\nFilho2: ");
        // filho_2.caminho.forEach((vertice) -> System.out.print(vertice.identificador + " -> "));
    }

    //Mutação de um indivíduo a partir da troca dos valores de duas posições
    private static void gerar_mutacao(ArrayList<Caminho> populacao){
        int i = new Random().nextInt(populacao.size());

        Caminho individuo_mutacao = new Caminho();
        individuo_mutacao.caminho.addAll(populacao.get(i).caminho);

        ArrayList<Integer> posicao_array = n_random_numbers(2, populacao.get(i).caminho.size());

        Collections.swap(individuo_mutacao.caminho, posicao_array.get(0), posicao_array.get(1));
        individuo_mutacao.calcula_fitness();

        populacao.add(individuo_mutacao);
    }

    //Etapa Criação da população inicial
    private static ArrayList<Caminho> etapa_populacao_inical(int tamanho_populacao_inicial, ArrayList<Vertice> listVertice){
        ArrayList<Caminho> populacao = new ArrayList<Caminho>();
        for (int i = 0; i < tamanho_populacao_inicial; i++) {
            populacao.add(construtivo_vizinho_proximo(new ArrayList<Vertice>(listVertice)));
        }
        return populacao;
    }

    //Etapa Calculo de aptidão
    private static void etapa_1_aptidao(ArrayList<Caminho> populacao, int tamanho_populacao){
        Collections.sort(populacao);
        if(populacao.size() > tamanho_populacao){
            populacao.subList(tamanho_populacao-1, populacao.size()).clear();
        }
    }

    //Etapa Seleção dos cromossomos a serem cruzados
    private static ArrayList<Caminho> etapa_2_selecao(int k_individuos, double taxa_cruzamento, ArrayList<Caminho> populacao){
        ArrayList<Caminho> selecao_populacao = new ArrayList<Caminho>();
        ArrayList<Caminho> buffer_populacao = new ArrayList<Caminho>(populacao);

        int quantidade_cromossomos = (int) (buffer_populacao.size() * taxa_cruzamento);

        quantidade_cromossomos = quantidade_cromossomos +(quantidade_cromossomos % 2);

        while(selecao_populacao.size() < quantidade_cromossomos){
            selecao_populacao.add(selecao_torneio(k_individuos, buffer_populacao));
        }

        // for (int i = 0; i < selecao_populacao.size(); i++) {
        //     System.out.println("Caminho: " + i + " " + selecao_populacao.get(i).fitness);
        // }
        
        return selecao_populacao;
    }

    //Etapa Cruzamento dos cromossomos selecionados da etapa 2
    private static void etapa_3_cruzamento(ArrayList<Caminho> populacao, ArrayList<Caminho> selecao_populacao, int quantidade_posicoes){
        for (int i = 0; i < selecao_populacao.size()-1; i++) {
            operador_Ox2(populacao, quantidade_posicoes, selecao_populacao.get(i), selecao_populacao.get(i+1));
        }
    }

    //Etapa Mutação de um dos indivíduos da nova população
    private static void etapa_4_mutacao(ArrayList<Caminho> populacao, double taxa_mutacao){
        double probabilidade = Math.random();

        if(probabilidade <= taxa_mutacao){

            gerar_mutacao(populacao);
        }
    }

    public static void main(String[] args) throws Exception {    
        /*
        --- Problema:
        Resolver o problema da população possuir clones e pouca variedade ---
        
        --- Possível Solução:
        - Modificar o método etapa_1_aptidao()
        - Definir um parâmetro para definir o quanto da lista original sera mantida
        - Declarar uma lista com os novos individuos
        - Os novos individuos são gerados a partir da "etapa_3_cruzamento()" e "etapa_4_mutacao"
        - Método para aplicar o merge da lista original com a lista de novos individuos de acordo com o parâmetro
        */

        //Parametros do Algoritmo Genético
        int tamanho_populacao_inicial = 100;
        int tamanho_populacao = 1000;
        int k_individuos_selecao = 3;
        double taxa_cruzamento = 0.6;
        int posicoes_cruzamento = 3;
        double taxa_mutacao = 0.1;

        //Condição de Parada
        int max_geracoes = 1000;

        ArrayList<Vertice> listVertice = new ArrayList<Vertice>();
        HashMap<String,Double> resultado = new HashMap<String,Double>();
        
        //String entrada = args[0];
        String entrada = "a280.tsp";
        
        Scanner readerOtimo = new Scanner(new FileReader("./resultados.txt"));
        Scanner reader = new Scanner(new FileReader("./entradas/".concat(entrada)));

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

        ArrayList<Caminho> populacao = etapa_populacao_inical(tamanho_populacao_inicial, listVertice);
        Collections.sort(populacao);

        System.out.println("Entrada:");
        System.out.println("Caminho: " + populacao.get(0).fitness);

        for(int geracao = 0; geracao < max_geracoes; geracao++){
            ArrayList<Caminho> selecao_populacao = etapa_2_selecao(k_individuos_selecao, taxa_cruzamento, populacao);

            System.out.println("Geração" + geracao + ": " + populacao.get(0).fitness);
            etapa_3_cruzamento(populacao, selecao_populacao, posicoes_cruzamento);

            etapa_4_mutacao(populacao, taxa_mutacao);

            etapa_1_aptidao(populacao, tamanho_populacao);
        }
        
        System.out.println("Saída:");
        System.out.println("Caminho 0: " + populacao.get(0).fitness);
        System.out.println("Caminho ?: " + populacao.get(populacao.size()-1).fitness);
        
        populacao.set(0, melhorativo2OPT(populacao.get(0))); 
        populacao.get(0).calcula_fitness();

        System.out.println("2-OPT:");
        System.out.println("Caminho: " + populacao.get(0).fitness);
                 
        System.out.println("-------------- Algoritmo Genético --------------");
    }
}