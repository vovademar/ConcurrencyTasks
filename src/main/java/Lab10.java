import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Lab10 {

    static Semaphore binarySemaphore = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new MyThread();
        thread.start();
        printMain();
    }

    public static void printMain() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            binarySemaphore.acquire();
            System.out.println("Hello from main " + i);
            binarySemaphore.release();
            sleep(100);
        }
    }

    public static class MyThread extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    binarySemaphore.acquire();
                    try {
                        System.out.println("hello from child " + i);
                    } finally {
                        binarySemaphore.release();
                        sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}