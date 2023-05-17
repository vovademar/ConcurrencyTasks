import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class Lab13 {
    static ReentrantLock forkLocker = new ReentrantLock();
    static Condition condition = forkLocker.newCondition();

    public static void main(String[] args) throws InterruptedException {
        Philosopher[] philosophers = new Philosopher[5];
        ReentrantLock[] forks = new ReentrantLock[5];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new ReentrantLock();
        }
        for (int i = 0; i < philosophers.length; i++) {
            ReentrantLock leftFork = forks[i];
            ReentrantLock rightFork = forks[(i + 1) % forks.length];
            if (i == philosophers.length - 1) {
                philosophers[i] = new Philosopher(leftFork, forks[0]);
            } else {
                philosophers[i] = new Philosopher(leftFork, rightFork);
            }
            Thread t = new Thread(philosophers[i], "Philosopher " + (i + 1));
            t.start();
        }
    }

    public static class Philosopher implements Runnable {
        private boolean food = false;
        private ReentrantLock leftFork;
        private ReentrantLock rightFork;


        public Philosopher(ReentrantLock leftFork, ReentrantLock rightFork) {
            this.leftFork = leftFork;
            this.rightFork = rightFork;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    doAction("Thinking");
                    forkLocker.lock();
                    while (!food) {
                        if (leftFork.tryLock()) {
                            doAction("Picked up left fork");
                            if (rightFork.tryLock()) {
                                forkLocker.unlock();
                                doAction("Picked up right fork - eating");
                                food = true;
                                doAction("Put down right fork");
                                rightFork.unlock();
                                forkLocker.lock();
                            }
                            leftFork.unlock();
                            doAction("Put down left fork. Back to thinking");
                        }
                        if (food) {
                            condition.signalAll();
                            forkLocker.unlock();
                        } else {
                            try {
                                condition.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    food = false;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void doAction(String action) throws InterruptedException {
            System.out.println(Thread.currentThread().getName() + " " + action);
            sleep(((int) (Math.random() * 1500)));
        }
    }
}
