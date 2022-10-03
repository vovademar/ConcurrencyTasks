import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Lab10 {

    static Semaphore semaphore = new Semaphore(1);


    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Thread thread = new MyThread(i);
            thread.start();
            sleep(10);
            System.out.println("Hello from main " + i);
        }
    }

    public static class MyThread extends Thread {
        int i;

        public MyThread(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                try {
                    System.out.println("hello from child " + i);
                    sleep(10);
                } finally {
                    semaphore.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}