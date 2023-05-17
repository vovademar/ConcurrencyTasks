package Lab12;

import java.util.LinkedList;
import java.util.List;

public class ListSort {
    public static List<String> list = new LinkedList<>();
    private boolean needSorting = false;

    public boolean isNeedSorting() {
        return needSorting;
    }

    public synchronized void add(String str){
        list.add(str);
    }

    public synchronized void print(){
        needSorting = true;
        for(String str : list){
            System.out.println(str);
        }
    }

    public synchronized void sort() {
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

}
