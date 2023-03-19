package in.tsiconsulting.accelerator.business.events;

import in.tsiconsulting.accelerator.framework.DBResult;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class EventProcessor {

    public final static long DEFAULT_MAX_TIME_INTERVAL = 500;
    public final static long DEFAULT_MAX_RECORD_LIMIT = 10;
    private static String LAST_MASTER_SYNC_DATE = null;

    LoanApplicationProcessor lap = null;
    CollectionProcessor cp = null;

    private static class LoanApplicationProcessor extends Thread {
        DBResult result = null;
        JSONObject record = null;
        JSONObject ctx = null;
        int _eid = 0;
        @Override
        public void run() {
            do {
                try {
                    result = Event.getEvents(Event.APPLY_LOAN_EVENT,Event.NEW_STATUS);
                    while(result.hasNext()){
                        record = (JSONObject) result.next();
                        _eid = (Integer) record.get("_eid");
                        ctx = (JSONObject) new JSONParser().parse((String)record.get("ctx"));
                        System.out.println("Processing Loan Application Event - "+ctx.get("principal"));
                        Event.updateStatus(_eid,Event.PROCESSED_STATUS);
                    }
                    Thread.sleep(DEFAULT_MAX_TIME_INTERVAL);
                } catch(Exception e) {
                    e.printStackTrace();
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

                    Thread.sleep(DEFAULT_MAX_TIME_INTERVAL);
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
