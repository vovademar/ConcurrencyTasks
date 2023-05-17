import java.util.Scanner;

public class Lab7 {

    public static void main(String[] args) throws InterruptedException {


        Scanner in = new Scanner(System.in);
        int threadCount = in.nextInt();
        int N = 1000000;
        PiThread[] threads = new PiThread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new PiThread(threadCount, i, N);
            threads[i].start();
        }
        for (int i = 0; i < threadCount; i++) {
            threads[i].join();
        }
        double pi = 0;

        for (int i = 0; i < threadCount; i++) {
            pi += threads[i].getSum();
        }
        System.out.print("PI = " + pi * 4);

    }


    static class PiThread extends Thread {

        private final int threadCount;
        private final int threadRemainder;
        private final int n;
        private double sum = 0;

        public PiThread(int threadCount, int threadRemainder, int n) {
            this.threadCount = threadCount;
            this.threadRemainder = threadRemainder;
            this.n = n;
        }


        @Override
        public void run() {
            for (int i = threadRemainder; i <= n; i += threadCount) {
                sum += Math.pow(-1, i) / (2 * i + 1);
            }
        }

        public double getSum() {
            return sum;
        }
    }
}