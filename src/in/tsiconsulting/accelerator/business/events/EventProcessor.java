package in.tsiconsulting.accelerator.business.events;

public class EventProcessor {

    public final static long DAFAULT_MAX_TIME_INTERVAL = 500;
    public final static long DAFAULT_MAX_RECORD_LIMIT = 10;
    private static String LAST_MASTER_SYNC_DATE = null;

    LoanApplicationProcessor lap = null;
    CollectionProcessor cp = null;

    private static class LoanApplicationProcessor extends Thread {
        @Override
        public void run() {
            do {
                try {

                    Thread.sleep(DAFAULT_MAX_TIME_INTERVAL);
                } catch(Exception e) {
                } finally {
                }
            } while(true);
        }
    };

    private static class CollectionProcessor extends Thread {
        @Override
        public void run() {
            do {
                try {

                    Thread.sleep(DAFAULT_MAX_TIME_INTERVAL);
                } catch(Exception e) {
                } finally {
                }
            } while(true);
        }
    };

    public void start() {
        lap = new LoanApplicationProcessor();
        cp = new CollectionProcessor();
        lap.start();
        cp.start();
        System.out.println("Loan Application Processor Started");
        System.out.println("Collection Processor Started");
    }

    public void stop() {
        lap.interrupt();
        cp.interrupt();
    }

    public static void main(String[] args){
        new EventProcessor().start();
    }
}
