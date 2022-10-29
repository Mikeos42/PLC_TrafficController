import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TrafficControllerFair implements TrafficController {
    TrafficRegistrar registrar;

    private boolean blocked = false; // TODO: make static or not
    private Lock lock = new ReentrantLock(true);
    private Condition bridgeEmpty = lock.newCondition();

    public TrafficControllerFair(TrafficRegistrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public void enterRight(Vehicle v) {
        lock.lock();
        try {
            while (blocked) {
//                System.out.println(Thread.currentThread().getName() + ": Right waiting...");
                bridgeEmpty.await();
            }
            System.out.println(Thread.currentThread().getName() + ":...Right going-");
            registrar.registerRight(v);
            blocked = true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void enterLeft(Vehicle v) {
        lock.lock();
        try {
            while (blocked) {
//                System.out.println(Thread.currentThread().getName() + ": Left waiting...");
                bridgeEmpty.await();
            }
            System.out.println(Thread.currentThread().getName() + ":...Left going-");
            registrar.registerLeft(v);
            blocked = true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void leaveLeft(Vehicle v) {
        lock.lock();
        try {
            blocked = false;
            System.out.println(Thread.currentThread().getName() + ": Right leaving...");
            bridgeEmpty.signalAll();
            registrar.deregisterLeft(v);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void leaveRight(Vehicle v) {
        lock.lock();
        try {
            blocked = false;
            System.out.println(Thread.currentThread().getName() + ":Left leaving...");
            bridgeEmpty.signalAll();
            registrar.deregisterRight(v);
        } finally {
            lock.unlock();
        }
    }
}
