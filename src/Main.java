import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

record Order(int id, String shoeType, int quantity) {}

class ShoeWarehouse {
    public static final List<String> PRODUCT_TYPES = List.of(
        "Кроссовки Nike", "Кроссовки Adidas", "Кеды Converse",
        "Кеды Vans", "Кроссовки New Balance", "Кроссовки Puma"
    );
    
    private final Queue<Order> orders = new LinkedList<>();
    private final int MAX_CAPACITY = 10;
    private final AtomicInteger orderCounter = new AtomicInteger(1);
    
    public synchronized void receiveOrder(Order order) {
        while (orders.size() >= MAX_CAPACITY) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        orders.offer(order);
        System.out.println("Получен заказ: " + order);
        notifyAll();
    }
    
    public synchronized Order fulfillOrder() {
        while (orders.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        
        Order order = orders.poll();
        System.out.println("Обработан заказ: " + order);
        notifyAll();
        return order;
    }
    
    public Order generateRandomOrder() {
        Random random = new Random();
        String shoeType = PRODUCT_TYPES.get(random.nextInt(PRODUCT_TYPES.size()));
        int quantity = random.nextInt(5) + 1;
        return new Order(orderCounter.getAndIncrement(), shoeType, quantity);
    }
}

class Producer implements Runnable {
    private final ShoeWarehouse warehouse;
    private final int orderCount;
    
    public Producer(ShoeWarehouse warehouse, int orderCount) {
        this.warehouse = warehouse;
        this.orderCount = orderCount;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < orderCount; i++) {
            Order order = warehouse.generateRandomOrder();
            warehouse.receiveOrder(order);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

class Consumer implements Runnable {
    private final ShoeWarehouse warehouse;
    private final int ordersToProcess;
    
    public Consumer(ShoeWarehouse warehouse, int ordersToProcess) {
        this.warehouse = warehouse;
        this.ordersToProcess = ordersToProcess;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < ordersToProcess; i++) {
            warehouse.fulfillOrder();
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
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
        
        System.out.println("\n=== Задание 2: ===");
        ShoeWarehouse warehouse = new ShoeWarehouse();
        int orderCount = 20;
        int consumerCount = 3;
        int ordersPerConsumer = 5;
        
        Thread producerThread = new Thread(new Producer(warehouse, orderCount));
        List<Thread> consumerThreads = new ArrayList<>();
        
        for (int i = 0; i < consumerCount; i++) {
            consumerThreads.add(new Thread(new Consumer(warehouse, ordersPerConsumer)));
        }
        
        producerThread.start();
        for (Thread consumer : consumerThreads) {
            consumer.start();
        }
        
        try {
            producerThread.join();
            for (Thread consumer : consumerThreads) {
                consumer.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}




