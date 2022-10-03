import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Lab10 {

    static Semaphore semaphore = new Semaphore(1);


    public static void main(String[] args) throws InterruptedException {
        Thread thread = new MyThread();
        thread.start();
        printMain();
    }

    public static void printMain() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            semaphore.acquire();
            System.out.println("Hello from main " + i);
            semaphore.release();
            sleep(100);
        }
    }

    public static class MyThread extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    semaphore.acquire();
                    try {
                        System.out.println("hello from child " + i);
                    } finally {
                        semaphore.release();
                        sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}