import java.util.*;

class EvenThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                System.out.println("Четный поток: " + i);
            }
        }
    }
}

class OddRunnable implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            if (i % 2 != 0) {
                System.out.println("Нечетный поток: " + i);
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Задание 1: Базовые потоки ===");
        Thread evenThread = new EvenThread();
        Thread oddThread = new Thread(new OddRunnable());
        
        evenThread.start();
        oddThread.start();
        
        try {
            evenThread.join();
            oddThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}