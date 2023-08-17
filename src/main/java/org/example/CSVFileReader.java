package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVFileReader {
    public static String caminhoDaPasta = "./files_in/"; // Insira o caminho para a pasta que contém os arquivos CSV
    public static HashMap<String, List > mapLinhas = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Instant iniciado = Instant.now();
        readCSVFilesFromFolder(caminhoDaPasta);
        Instant finalizado = Instant.now();
        long seconds = ChronoUnit.SECONDS.between(iniciado, finalizado);
        System.out.println(String.format("Tempo decorrido %d segundos", seconds));

    }

    public static void readCSVFilesFromFolder(String caminhoDaPasta) throws IOException {

        File arquivo = new File(caminhoDaPasta);
        File[] csvFiles = arquivo.listFiles((pasta, nome) -> nome.toLowerCase().endsWith(".csv"));
        System.out.println("Quantidade de arquivos: " + csvFiles.length);

        for (File file : csvFiles) {
            separaRegistrosPorPais(file);
            escreveArquivoParaCadaPais();
        }

    }


    public static void separaRegistrosPorPais(File arquivo) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
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
        Path caminho = Paths.get(pais + ".txt");
        escreveRegistrosNoArquivo(caminho, registros);

    }

    private static void escreveRegistrosNoArquivo(Path caminho, List<String> registros) {
        try (BufferedWriter writer = Files.newBufferedWriter(caminho)) {
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
