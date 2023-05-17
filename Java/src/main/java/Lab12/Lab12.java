package Lab12;

import java.util.Scanner;

public class Lab12 {
    public static ListSort list = new ListSort();

    public static void main(String[] args) {

        Thread sorter = new Thread(new Sorting(list));
        sorter.start();
        Scanner in = new Scanner(System.in);
        String str = in.nextLine();
        while (str.compareTo("") != 0) {
            if (str.length() >= 80) {
                String[] lines = str.split("(?<=\\G.{" + 80 + "})");
                for (String line : lines) {
                    list.add(line);
                }
            }
            else {
                list.add(str);
            }
            str = in.nextLine();
        }
        list.print();
    }


    static class Sorting extends Thread {
        private final ListSort list;

        public Sorting(ListSort list) {
            this.list = list;
        }

        @Override
        public void run() {
            while (!list.isNeedSorting()) {
                try {
                    list.sort();
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
