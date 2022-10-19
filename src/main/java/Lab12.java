import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lab12 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String str = in.nextLine();
        List<String> list = new ArrayList<>();
        while (str.compareTo("") != 0) {
            list.add(str);
            str = in.nextLine();
        }
        System.out.println(list);

    }

    public void sort(List<String> list) {
        int size = list.size();
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                for (int j = i; j < size; j++) {
                    String first = list.get(i);
                    String second = list.get(j);
                    if (second.compareTo(first) < 0) {
                        list.set(i, second);
                        list.set(j, first);
                    }
                }
            }
        }
    }


    static class Sorting extends Thread {

        @Override
        public void run() {
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
