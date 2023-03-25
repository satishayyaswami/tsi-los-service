package in.tsiconsulting.accelerator.business;

import in.tsiconsulting.accelerator.business.events.Event;
import in.tsiconsulting.accelerator.framework.*;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Types;

public class Loan implements REST {

    private static final String FUNCTION = "_func";

    // Function definitions
    private static final String APPLY_LOAN = "apply-loan";
    private static final String POST_SANCTION = "post-sanction";
    private static final String POST_DISBURSEMENT = "post-disbursement";

    public static final String APPLIED_STATUS = "APPLIED";
    public static final String SANCTION_STATUS = "SANCTION";
    public static final String DOCUMENTATION_STATUS = "DOCUMENTATION";
    public static final String DISBURSED_STATUS = "DISBURSED";
    public static final String PAYMENT_STATUS = "PAYMENT_COMPLETE";

    @Override
    public void get(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        String func = null;

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);

            if(func != null){
                if(func.equalsIgnoreCase(APPLY_LOAN)){
                    output = applyLoan(input);
                }else if(func.equalsIgnoreCase(POST_SANCTION)){
                    output = postSanction(input);
                }else if(func.equalsIgnoreCase(POST_DISBURSEMENT)){
                    output = postDisbursement(input);
                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
    }

    private JSONObject applyLoan(JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        int _lid = 0;
        String _cid = (String) input.get("_cid");

        // insert
        _lid = insertLoan(input);
        out.put("loanid",_lid);
        return out;
    }

    private JSONObject postSanction(JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        int _lid = Integer.parseInt((String) input.get("_cid"));
        updateLoanStatus(_lid, Loan.DOCUMENTATION_STATUS);
        out.put("posted",true);
        return out;
    }

    private JSONObject postDisbursement(JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        int _lid = Integer.parseInt((String) input.get("_cid"));
        updateLoanStatus(_lid, Loan.DISBURSED_STATUS);
        out.put("posted",true);
        return out;
    }

    private int insertLoan(JSONObject input) throws Exception{
        String sql = null;
        int _lid = 0;
        DBQuery query = null;
        String _cid = (String) input.get("_cid");
        JSONObject ctx = (JSONObject) input.get("ctx");
        JSONObject loan_details = (JSONObject) input.get("loan_details");
        JSONObject schedule = (JSONObject) input.get("schedule");
        JSONObject documents = (JSONObject) input.get("documents");
        String status = (String) input.get("status");
        String clientuserid = (String) input.get("client-user-id");

        sql = "insert into _loan (_cid,ctx,loan_details,schedule,documents,status,client_user_id) values (?,?::json,?::json,?::json,?::json,?,?)";
        query = new DBQuery( sql);
        query.setValue(Types.INTEGER,_cid);
        query.setValue(Types.VARCHAR,ctx.toJSONString());
        query.setValue(Types.VARCHAR,loan_details.toJSONString());
        query.setValue(Types.VARCHAR,schedule.toJSONString());
        query.setValue(Types.VARCHAR,documents.toJSONString());
        query.setValue(Types.VARCHAR,status);
        query.setValue(Types.VARCHAR,clientuserid);
        _lid = DB.insert(query);

        // Publish Event
        ctx.put("_id",_lid);
        Event.add(Event.APPLY_LOAN_EVENT,ctx,clientuserid);
        return _lid;
    }

    public void updateLoanStatus(int loanId, String status) throws Exception{
        String sql = null;
        DBQuery query = null;

        sql = "update _loan set status=? where _lid=?";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,status);
        query.setValue(Types.INTEGER,loanId);
        DB.update(query);
    }

    @Override
    public void delete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public void put(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public boolean validate(String s, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return true;
    }
}
