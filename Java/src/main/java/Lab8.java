import java.util.Scanner;

public class Lab8 {
    static int actualCount = 0;

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        int threadCount = in.nextInt();
        PiThread[] threads = new PiThread[threadCount];

        var shutdownListener = new Thread() {
            public void run() {
                System.out.println("\nshutdown in 2s ");
                try {
                    over(threads, actualCount, threadCount);
                    sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownListener);

        for (int i = 0; i < threadCount; i++) {
            actualCount++;
            threads[i] = new PiThread(threadCount, i);
            threads[i].start();
        }

        while (true) {
        }
    }


    public static void over(PiThread[] threads, int actualThreadCount, int totalThreadCount) throws InterruptedException {
        for (int i = 0; i < actualThreadCount; i++) {
            threads[i].interrupt();
        }

        double pi = finishCount(threads, actualThreadCount, totalThreadCount);
        System.out.print("PI = " + pi * 4);
        System.out.println("\ncount: " + actualThreadCount);
    }

    public static double finishCount(PiThread[] threads, int threadCount, int totalThreadCount) {
        int max = 0;
        int maxIndex = -1;
        for (int i = 0; i < threadCount; i++) {
            if (threads[i].getMaxSizeOfRow() > max) {
                max = threads[i].getMaxSizeOfRow();
                maxIndex = i;
            }
        }
        double allSum = 0;
        double currentSum;
        for (int i = 0; i < threadCount; i++) {
            if (i != maxIndex) {
                currentSum = threads[i].getSum();
                for (int j = threads[i].maxSizeOfRow; j < max; j += totalThreadCount) {
                    currentSum += Math.pow(-1, j) / (2 * j + 1);
                }
                allSum += currentSum;
            }

        }
        allSum += threads[maxIndex].getSum();
        return allSum;
    }


    static class PiThread extends Thread {

        private final int threadCount;
        private final int threadRemainder;
        private double sum = 0;
        private int maxSizeOfRow = 0;

        public PiThread(int threadCount, int threadRemainder) {
            this.threadCount = threadCount;
            this.threadRemainder = threadRemainder;
        }

        public int getMaxSizeOfRow() {
            return maxSizeOfRow;
        }

        @Override
        public void run() {

               try {
                   for (int i = threadRemainder; true; i += threadCount) {
                       sum += Math.pow(-1, i) / (2 * i + 1);
                       maxSizeOfRow++;
                       sleep(0);
                   }
               }
               catch (InterruptedException IO){
                     System.out.println("interrupted");
               }
        }

        public double getSum() {
            return sum;
        }
    }

}
