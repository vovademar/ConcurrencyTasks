import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Lab10 implements Runnable {

    private final Lock lock;
    private final Condition condition;
    private int flag = 0;

    public Lab10() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
        new Thread(this).start();
    }

    public static void main(String[] args) throws InterruptedException {
        Lab10 printer = new Lab10();
        printer.mainThread();
    }

    private void childThread() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            lock.lock();
            while (flag == 0) {
                condition.await();
            }
            flag = 0;
            System.out.println("Child "+ i);
            condition.signalAll();
            lock.unlock();

        }
    }

    private void mainThread() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            lock.lock();
            while (flag == 1) {
                condition.await();
            }
            flag = 1;
            System.out.println("Main " + i);
            condition.signalAll();
            lock.unlock();

        }
    }

    @Override
    public void run() {
        try {
            childThread();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}