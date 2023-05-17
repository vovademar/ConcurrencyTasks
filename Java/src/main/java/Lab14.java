import java.util.concurrent.Semaphore;

public class Lab14 {
    public static void main(String[] args) {
        Semaphore semA = new Semaphore(0);
        Semaphore semB = new Semaphore(0);
        Semaphore semC = new Semaphore(0);

        Thread a = new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("part A is ready");
                semA.release();
            }
        });

        Thread b = new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("part B is ready");
                semB.release();
            }
        });

        Thread c = new Thread(()->{
            while (true) {
                try {
                    semA.acquire();
                    semB.acquire();
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("module C is ready");
                semC.release();
            }
        });

        Thread widget = new Thread(()->{
            while (true) {
                try {
                    semC.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("widget is ready");
            }
        });

        a.start();
        b.start();
        c.start();
        widget.start();

    }
}
