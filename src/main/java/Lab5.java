import static java.lang.Thread.sleep;

public class Lab5 {

    public static void main(String[] args) throws InterruptedException {

        Thread thread = new MyThread();
        thread.start();
        sleep(2000);
        thread.interrupt();
        sleep(300);
        System.out.println("Hello from main");

    }

    public static class MyThread extends Thread {

        @Override
        public void run() {
            while (true) {
                if (!Thread.interrupted()) {
                    System.out.println("Hello, World!");
                } else {
                    return;
                }
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    System.out.println("thread was interrupted");
                    return;
                }
            }
        }

    }
}