package Genetico;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

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

    private static void operador_Ox2(ArrayList<Vertice> pai_1, ArrayList<Vertice> pai_2){
        int quantidade_posicoes = 3;

        ArrayList<Vertice> filho_1 = new ArrayList<Vertice>();
        ArrayList<Vertice> filho_2 = new ArrayList<Vertice>();

        for(int i = 1; i <= 2; i++){
            ArrayList<Integer> posicao_array = new ArrayList<Integer>();

            if(i == 1){
                posicao_array.add(0);
                posicao_array.add(2);
                posicao_array.add(7);

            }else{
                posicao_array.add(1);
                posicao_array.add(4);
                posicao_array.add(6);
            }

            /*Random random = new Random();
            for(int posicao = 0; posicao < quantidade_posicoes; posicao++){
                posicao_array.add(random.nextInt(select_list((i % 2) + 1, pai_1, pai_2).size() - 1));
            }*/
            
            Collections.sort(posicao_array);
            
            ArrayList<Vertice> filho_i = select_list(i, filho_1, filho_2);
            filho_i.addAll(select_list(i, pai_1, pai_2));
            
            ArrayList<Integer> conteudo_array = new ArrayList<Integer>();

            for(int j = 0; j < posicao_array.size(); j++){
                conteudo_array.add(select_list(i,pai_1, pai_2).indexOf(select_list((i % 2) + 1, pai_1, pai_2).get(posicao_array.get(j))));
            }

            Collections.sort(conteudo_array);

            for(int k = 0; k < conteudo_array.size(); k++){
                filho_i.set(conteudo_array.get(k), select_list((i % 2) + 1, pai_1, pai_2).get(posicao_array.get(k)));
            }
        }
        System.out.println("Filho1: ");
        filho_1.forEach((vertice) -> System.out.print(vertice.identificador + " -> "));
        System.out.println("\nFilho2: ");
        filho_2.forEach((vertice) -> System.out.print(vertice.identificador + " -> "));
    }

    private static ArrayList<Vertice> select_list(int i, ArrayList<Vertice> array_1, ArrayList<Vertice> array_2){
        if(i == 1) return array_1;
        else return array_2;
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

        ArrayList<Vertice> pai_1 = new ArrayList<Vertice>();
        ArrayList<Vertice> pai_2 = new ArrayList<Vertice>();

        pai_1.add(0, listVertice.get(5));
        pai_1.add(1, listVertice.get(2));
        pai_1.add(2, listVertice.get(4));
        pai_1.add(3, listVertice.get(0));
        pai_1.add(4, listVertice.get(1));
        pai_1.add(5, listVertice.get(3));
        pai_1.add(6, listVertice.get(7));
        pai_1.add(7, listVertice.get(6));

        pai_2.add(0, listVertice.get(7));
        pai_2.add(1, listVertice.get(4));
        pai_2.add(2, listVertice.get(1));
        pai_2.add(3, listVertice.get(3));
        pai_2.add(4, listVertice.get(2));
        pai_2.add(5, listVertice.get(0));
        pai_2.add(6, listVertice.get(6));
        pai_2.add(7, listVertice.get(5));

        operador_Ox2(pai_1, pai_2);

        //Execução dos algoritmos construtivos          
        System.out.println("-------------- Algoritmo Genético --------------");
    }
}