import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Lab11 {

    static Semaphore childSemaphore = new Semaphore(0);
    static Semaphore mainSemaphore = new Semaphore(1);



    public static void main(String[] args) throws InterruptedException {
        Thread thread = new MyThread();
        thread.start();
        printMain();
    }

    public static void printMain() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            mainSemaphore.acquire();
            System.out.println("Hello from main " + i);
            childSemaphore.release();
            sleep(100);
        }
    }

    public static class MyThread extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    childSemaphore.acquire();
                    try {
                        System.out.println("Hello from child " + i);
                    } finally {
                        mainSemaphore.release();
                        sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}