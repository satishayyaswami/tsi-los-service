package in.tsiconsulting.accelerator.solutions.finance.loan.los;

import in.tsiconsulting.accelerator.system.core.*;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Types;

public class Proto implements REST {

    private static final String API_PROVIDER = "tsi";

    private static final String API_NAME = "los";

    private static final String METHOD = "_method";
    private static final String DATA = "_data";

    private static final String DEFINE_LOS_WORKFLOW = "define_los_workflow";
    private static final String CREATE_LOS_APPLICATION = "create_los_application";
    private static final String POST_LOS_ACTIVITY = "post_los_activity";


    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        JSONObject wfdef = null;
        String method = null;
        AccountConfig accountConfig = null;
        JSONObject apiConfig = null;
        JSONObject data = null;
        int loanappid = 0;

        try {
            input = InputProcessor.getInput(req);
            method = (String) input.get(METHOD);
            accountConfig = InputProcessor.getAccountConfig(req);
            apiConfig = accountConfig.getAPIConfig(API_PROVIDER,API_NAME);

            if(method != null){
                if(method.equalsIgnoreCase(DEFINE_LOS_WORKFLOW)){
                    output = defineLOSWorkflow(accountConfig.getTenant(),input);
                }else if(method.equalsIgnoreCase(CREATE_LOS_APPLICATION)){
                    wfdef = getLOSWorkflowDef(accountConfig.getTenant(),input);
                    if(wfdef != null){
                        loanappid = createLoanApplication(accountConfig.getTenant(), wfdef, input);
                        output = new JSONObject();
                        output.put("loan-app-id",loanappid);
                    }
                }else if(method.equalsIgnoreCase(POST_LOS_ACTIVITY)) {
                    
                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
    }

    private JSONObject defineLOSWorkflow(JSONObject tenant, JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        String workflowcode = (String) input.get("los-workflow-code");

        if(workflowexists(tenant,workflowcode)){
            // update
            updateLOSWorkflow(tenant, input);
            out.put("updated",true);
        }else{
            // insert
            insertLOSWorkflow(tenant, input);
            out.put("created",true);
        }

        // sync tenantdb json indexes
        // new DBSync().newaccountsync();

        return out;
    }

    private boolean workflowexists(JSONObject tenant, String workflowcode) throws Exception{
        boolean exists = false;
        String sql = null;
        DBQuery query = null;
        int count = 0;

        sql = "select count(*) from _solutions_finance_los_tsi_wf_def where wf_code=?";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,workflowcode);
        count = DB.fetchCount(query);
        if(count > 0) exists = true;

        return exists;
    }

    private void insertLOSWorkflow(JSONObject tenant, JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String workflowcode = (String) input.get("los-workflow-code");
        String workflowname = (String) input.get("los-workflow-name");
        String clientuserid = (String) input.get("client-user-id");
        JSONObject wfdef = (JSONObject) input.get("los-workflow-definition");

        sql = "insert into _solutions_finance_los_tsi_wf_def (wf_code,wf_name,client_user_id,wf_def) values (?,?,?,?::json)";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,workflowcode);
        query.setValue(Types.VARCHAR,workflowname);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,wfdef.toJSONString());

        DB.update(query);
    }

    private void updateLOSWorkflow(JSONObject tenant, JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String workflowcode = (String) input.get("los-workflow-code");
        String workflowname = (String) input.get("los-workflow-name");
        String clientuserid = (String) input.get("client-user-id");
        JSONObject wfdef = (JSONObject) input.get("los-workflow-definition");

        sql = "update _solutions_finance_los_tsi_wf_def set wf_name=?,client_user_id=?,wf_def=?::json where wf_code=?";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,workflowname);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,wfdef.toJSONString());
        query.setValue(Types.VARCHAR,workflowcode);

        DB.update(query);
    }

    private JSONObject getLOSWorkflowDef(JSONObject tenant, JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        JSONObject wfdef = null;
        DBResult rs = null;
        String workflowcode = (String) input.get("los-workflow-code");

        sql = "select wf_def from _solutions_finance_los_tsi_wf_def where wf_code=?";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,workflowcode);
        rs = DB.fetch(query);
        if(rs.hasNext()){
            wfdef = rs.next();
        }
        return wfdef;
    }

    private int createLoanApplication(JSONObject tenant, JSONObject wfdef, JSONObject input) throws Exception{
        String sql1,sql2 = null;
        DBQuery query = null;
        DBResult rs = null;
        int loanAppId = 0;

        String workflowcode = (String) input.get("los-workflow-code");
        String clientuserid = (String) input.get("client-user-id");
        JSONObject data = (JSONObject) input.get("data");

        sql1 = "insert into _solutions_finance_los_tsi_wf_loan (wf_code,client_user_id,wf_def,data,state) values (?,?,?::json,?::json,?)";
        query = new DBQuery( tenant, sql1);
        query.setValue(Types.VARCHAR,workflowcode);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,wfdef.toJSONString());
        query.setValue(Types.VARCHAR,data.toJSONString());
        query.setValue(Types.VARCHAR,"loan-applied-state");

        loanAppId = DB.insert(query);
        return loanAppId;
    }

    private void postWorkflowHistory(JSONObject tenant, JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String workflowcode = (String) input.get("los-workflow-code");
        String clientuserid = (String) input.get("client-user-id");
        JSONObject data = (JSONObject) input.get("data");

        sql = "insert into _solutions_finance_los_tsi_wf_history (wf_code,wf_loan_id,wf_def,data,state) values (?,?,?::json,?::json,?)";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,workflowcode);
        query.setValue(Types.VARCHAR,clientuserid);
       // query.setValue(Types.VARCHAR,wfdef.toJSONString());
        query.setValue(Types.VARCHAR,data.toJSONString());
        query.setValue(Types.VARCHAR,"loan-applied-state");
        DB.update(query);
    }

    @Override
    public void delete(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void validate(String method, HttpServletRequest req, HttpServletResponse res) {

    }
}
