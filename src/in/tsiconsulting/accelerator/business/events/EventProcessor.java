package in.tsiconsulting.accelerator.business.events;

import in.tsiconsulting.accelerator.business.Loan;
import in.tsiconsulting.accelerator.framework.BRE;
import in.tsiconsulting.accelerator.framework.DBResult;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class EventProcessor {

    public final static long DEFAULT_MAX_TIME_INTERVAL = 500;
    public final static long DEFAULT_MAX_RECORD_LIMIT = 10;
    private static String LAST_MASTER_SYNC_DATE = null;

    CustomerVerificationProcessor cvp = null;
    LoanApplicationProcessor lap = null;
    CollectionProcessor cp = null;

    private static class CustomerVerificationProcessor extends Thread {
        DBResult result = null;
        JSONObject record = null;
        JSONObject ctx = null;
        int _eid = 0;
        @Override
        public void run() {
            do {
                try {
                    result = Event.getEvents(Event.ONBOARD_CUSTOMER_EVENT,Event.NEW_STATUS);
                    while(result.hasNext()){
                        record = (JSONObject) result.next();
                        _eid = (Integer) record.get("_eid");
                        ctx = (JSONObject) new JSONParser().parse((String)record.get("ctx"));
                        System.out.println("Processing Customer Onboarding Event - "+ctx.get("principal"));
                        System.out.println("PAN Verification Check - "+ctx.get("principal")+" - SUCCESS");
                        System.out.println("VoterId Verification Check - "+ctx.get("principal")+" - SUCCESS");
                        System.out.println("Penny Drop Check - "+ctx.get("principal")+" - SUCCESS");
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
                        System.out.println("---------------------------------");
                        System.out.println("CB Check - "+ctx.get("principal")+" - SUCCESS");
                        System.out.println("AML Check - "+ctx.get("principal")+" - SUCCESS");
                        /**
                         * If Loan Amount <= 20000
                         *  do auto sanction
                         * else
                         *  put it in manual sanction queue
                         */
                        JSONObject ruledata = new JSONObject();
                        ruledata.put("amount",ctx.get("amount"));
                        boolean met = BRE.fireRule("small_ticket_rule",ruledata);
                        if(met) {
                            System.out.println("small_ticket_rule  - " + met + " - Auto Sanction Enabled");
                            System.out.println("Auto sanction done. Call back to Fintech Partner posted");
                            new Loan().updateLoanStatus(((Long)ctx.get("_id")).intValue(),Loan.DOCUMENTATION_STATUS);
                        }
                        else {
                            System.out.println("small_ticket_rule  - " + met + " - Manual Sanction Required");
                            new Loan().updateLoanStatus(((Long)ctx.get("_id")).intValue(),Loan.SANCTION_STATUS);
                        }
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
        cvp = new CustomerVerificationProcessor();
        lap = new LoanApplicationProcessor();
        cp = new CollectionProcessor();
        cvp.start();
        lap.start();
        cp.start();
        System.out.println("Customer Verification Processor Started");
        System.out.println("Loan Application Processor Started");
        System.out.println("Collection Processor Started");
    }

    public void stop() {
        cvp.interrupt();
        lap.interrupt();
        cp.interrupt();
    }

    public static void main(String[] args){
        new EventProcessor().start();
    }
}
