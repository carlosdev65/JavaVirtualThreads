package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CSVFileReaderThreadsAndVirtualThread {
    public static String caminhoDaPasta = "./files_in/"; // Insira o caminho para a pasta que contém os arquivos CSV
    public static HashMap<String, List > mapLinhas = new HashMap<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Instant iniciado = Instant.now();

        lerArquivosCSVdaPasta(caminhoDaPasta);
        escreveArquivoParaCadaPais();

        Instant finalizado = Instant.now();
        long segundos = ChronoUnit.SECONDS.between(iniciado, finalizado);
        System.out.println(String.format("Completado em %d segundos", segundos));

    }

    public static void lerArquivosCSVdaPasta(String caminhoDaPasta) throws IOException, InterruptedException {


        File arquivo = new File(caminhoDaPasta);
        File[] arquivosCSV = arquivo.listFiles((pasta, nome) -> nome.toLowerCase().endsWith(".csv"));
        System.out.println("Quantidade de arquivos: " + arquivosCSV.length);

        // obtenha o limite de threads -1 ou o sistema pode congelar em arquivos grandes
        int limiteDeThreads = Runtime.getRuntime().availableProcessors();
        limiteDeThreads = Math.max(limiteDeThreads, 1);

        String tempoMarcado = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date());
        System.out.println(tempoMarcado);

        System.out.println("Número de threads = " + limiteDeThreads);

        ExecutorService executor = Executors.newFixedThreadPool(5000);
        ExecutorService executorVirtual = Executors.newVirtualThreadPerTaskExecutor();

        executeTask(executor, arquivosCSV); // mude aqui entre o executor e executorVirtual

    }

    public static void executeTask(ExecutorService executor, File[] arquivosCSV) throws InterruptedException {
        try (ExecutorService e = executor) {
            for (File file : arquivosCSV) {
                e.execute(createTask(file));
            }
        }
    }

    public static Runnable createTask(File file){
        return () -> {
            try {
                separaRegistrosPorPais(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }


    public static void separaRegistrosPorPais(File file) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] str = linha.split(",");

                if(!mapLinhas.containsKey(str[0])) {
                    if(!str[0].contains("Region")){
                        List<String> linhas = new ArrayList<>();
                        linhas.add(linha);
                        mapLinhas.put(str[0],linhas);
                    }

                }else{
                    if(!str[0].contains("Region")){
                        List<String> linhas = mapLinhas.get(str[0]);
                        linhas.add(linha);
                        mapLinhas.put(str[0],linhas);
                    }
                }
            }
        }

    }

    static void escreveArquivoParaCadaPais() {
        mapLinhas.forEach((key, value) -> {
                    escreveArquivoParaPais(key , value);
                }
        );

    }

    private static void escreveArquivoParaPais(String pais, List<String> registros) {
        Path caminho = Paths.get(pais + ".csv");
        escreveRegistrosNoArquivo(caminho, registros);

    }

    private static void escreveRegistrosNoArquivo(Path pasta, List<String> registros) {
        try (BufferedWriter writer = Files.newBufferedWriter(pasta)) {
            writer.write("ID,Name,Condition,State,Price\n");
            registros.stream().forEach(linha -> {
                try {
                    writer.write(linha + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
