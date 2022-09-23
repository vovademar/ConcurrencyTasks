import java.util.Scanner;

public class Lab8 {
    static int actualCount = 0;

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        int threadCount = in.nextInt();
        int N = 100000000;
        PiThread[] threads = new PiThread[threadCount];

        var shutdownListener = new Thread() {
            public void run() {
                System.out.println("\nshutdown in 2s ");
                try {
                    over(threads, actualCount);
                    sleep(2000);
                } catch (InterruptedException e) {
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownListener);

        for (int i = 0; i < threadCount; i++) {
            actualCount++;
            threads[i] = new PiThread(threadCount, i, N);
            threads[i].start();
        }

        while (true) {
        }
    }

    public static void over(PiThread[] threads, int threadCount) throws InterruptedException {
        for (int i = 0; i < threadCount; i++) {
            threads[i].join();
        }
        double pi = 0;
        for (int i = 0; i < threadCount; i++) {
            pi += threads[i].getSum();
        }
        System.out.print("PI = " + pi * 4);
        System.out.println("\ncount: " + threadCount);
    }


    static class PiThread extends Thread {

        private final int threadCount;
        private final int threadRemainder;
        private final int N;
        private double sum = 0;

        public PiThread(int threadCount, int threadRemainder, int n) {
            this.threadCount = threadCount;
            this.threadRemainder = threadRemainder;
            N = n;
        }

        @Override
        public void run() {
            for (int i = threadRemainder; true; i += threadCount) {
                sum += Math.pow(-1, i) / (2 * i + 1);
            }
        }

        public double getSum() {
            return sum;
        }
    }

}
