import java.util.Scanner;

import static java.lang.Thread.sleep;

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
                    sleep(1000);
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

    public static void over(PiThread[] threads, int actualThreadCount) throws InterruptedException {
        System.out.println(0);
        for (int i = 0; i < actualThreadCount; i++) {
            threads[i].interrupt();
        }
        System.out.println(1);
        double pi1 = 0;
//        for (int i = 0; i < actualThreadCount; i++) {
//            System.out.println("sum = "+threads[i].getSum());
//            pi1 += threads[i].getSum();
//        }
        //sleep(actualThreadCount * 100);

        double pi = finishCount(threads, actualThreadCount);
        System.out.print("PI = " + pi * 4);
        System.out.print("\nPI1 = " + pi1 * 4);
        System.out.println("\ncount: " + actualThreadCount);
    }

    public static double finishCount(PiThread[] threads, int threadCount) {
        int max = 0;
        int maxIndex = -1;
        for (int i = 0; i < threadCount; i++) {
            if (threads[i].getMaxSizeOfRow() > max) {
                max = threads[i].getMaxSizeOfRow();
                maxIndex = i;
            }
        }
//        System.out.println("maxsize"+threads[0].getMaxSizeOfRow());
//        System.out.println("max" + max);
//        System.out.println(maxIndex);
        //print threads
//        for (int i = 0; i < threadCount; i++) {
//            System.out.println("was sum"+threads[i].getSum());
//        }

//        System.out.println("\nmaxsizeofrow"+ max + "\n");
        double allSum = 0;
        double currentSum;
        for (int i = 0; i < threadCount; i++) {
            if (i != maxIndex) {
                currentSum = threads[i].getSum();
                for (int j = threads[i].maxSizeOfRow; j<max;j+=threadCount){
                    currentSum += Math.pow(-1, j) / (2 * j + 1);
                }
                System.out.println("cur sum = "+ currentSum);
                allSum+=currentSum;
            }

        }
        System.out.println("maxthread = " + threads[maxIndex].getSum()+ "max index = " + maxIndex);
        allSum+=threads[maxIndex].getSum();
        return allSum;
    }


    static class PiThread extends Thread {

        private final int threadCount;
        private final int threadRemainder;
        private final int N;
        private double sum = 0;
        private int maxSizeOfRow = 0;

        public PiThread(int threadCount, int threadRemainder, int n) {
            this.threadCount = threadCount;
            this.threadRemainder = threadRemainder;
            N = n;
        }

        public int getMaxSizeOfRow() {
            return maxSizeOfRow;
        }

        @Override
        public void run() {
            for (int i = threadRemainder; true; i += threadCount) {
                sum += Math.pow(-1, i) / (2 * i + 1);
                maxSizeOfRow++;
//                try {
//                    sleep(100);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
            }

        }

        public double getSum() {
            return sum;
        }
    }

}
