package org.example;


import java.util.Random;
public class VirtualThreadExample {
    public static void main( String[] args ) {
        var vThreads = false;

        var iniciado = System.currentTimeMillis();

        Random random = new Random();
        Runnable runnable = () -> { double i = random.nextDouble(1000) % random.nextDouble(1000);  };
        for (int i = 0; i < 50000; i++){
            if (vThreads){
                Thread.startVirtualThread(runnable);
            } else {
                Thread t = new Thread(runnable);
                t.start();
            }
        }

        var finalizado = System.currentTimeMillis();
        var tempoDecorrido = finalizado - iniciado;
        System.out.println("Tempo decorrido: " + tempoDecorrido);
    }
}
