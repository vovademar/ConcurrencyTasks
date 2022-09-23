import java.util.Arrays;

public class Lab3 {

    private static void init(String[] strings, String prefix) {
        for (int i = 0; i < strings.length; i++) {
            strings[i] = prefix + (i + 1);
        }
    }

    public static void main(String[] args) {


        String[] strings = new String[4];
        init(strings, "A");
        Thread thread = new MyThread();
        System.out.println(Arrays.toString(strings));
    }

    public static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Hello, World!");
        }
    }

}
