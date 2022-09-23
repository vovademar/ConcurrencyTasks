package Lab6;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public final class Founder {
    private final List<Runnable> workers;
    private CyclicBarrier cyclicBarrier;
    private Company company;
    private int departmentAmount;

    public Founder(Company company) {
        this.company = company;
        departmentAmount = company.getDepartmentsCount();
        this.workers = new ArrayList<>(company.getDepartmentsCount());
        cyclicBarrier = new CyclicBarrier(company.getDepartmentsCount(), company::showCollaborativeResult);
    }

    public void start() {
        for (int i = 0; i < departmentAmount; i++) {
            workers.add(new Worker(company.getFreeDepartment(i)));
        }
        for (Runnable r : workers) {
            new Thread(r).start();
        }
    }

    private class Worker implements Runnable {
        Department department;

        Worker(Department department) {
            this.department = department;
        }

        @Override
        public void run() {
            department.performCalculations();
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // print list of workers
    public void printWorkers() {
        for (Runnable r : workers) {
            System.out.println(r);
        }
    }


    public static void main(String[] args) {
        Company company = new Company(5);
        Founder founder = new Founder(company);
        founder.start();
        founder.printWorkers();
    }

}
