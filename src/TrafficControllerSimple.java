public class TrafficControllerSimple implements TrafficController {

    private TrafficRegistrar registrar;
    private static boolean blocked = false;

    public TrafficControllerSimple(TrafficRegistrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public synchronized void enterRight(Vehicle v) {
        while(blocked) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        registrar.registerRight(v);
        blocked = true;
        System.out.println("Right enters bridge");
    }

    @Override
    public synchronized void enterLeft(Vehicle v) {
        while(blocked) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        registrar.registerLeft(v);
        blocked = true;
        System.out.println("Left enters bridge");
    }

    @Override
    public synchronized void leaveLeft(Vehicle v) {
        registrar.deregisterLeft(v);
        blocked = false;
        notify();
        System.out.println("Right leaves bridge");

    }

    @Override
    public synchronized void leaveRight(Vehicle v) {
        registrar.deregisterRight(v);
        blocked = false;
        notify();
        System.out.println("Left leaves bridge");
    }
}
