import java.util.Arrays;

public class Lab1 {

    public static class MyThread extends Thread {
        @Override
        public void run() {
                System.out.println("Hello, World!");
        }


    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Thread thread = new MyThread();
            thread.start();
            System.out.println("Hello");
        }
    }

}
