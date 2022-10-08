package in.tsiconsulting.accelerator.solutions.finance.loan.los;

import in.tsiconsulting.accelerator.system.core.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Types;
import java.util.Iterator;

public class Proto implements REST {

    private static final String API_PROVIDER = "tsi";

    private static final String API_NAME = "los";

    private static final String FUNCTION = "_func";
    private static final String DATA = "_data";

    private static final String POST_LOS_WORKFLOW = "post_los_workflow";
    private static final String POST_LOS_APPLICATION = "post_los_application";
    private static final String POST_LOS_ACTIVITY = "post_los_activity";

    private static final String GET_LOS_ACTIVITY_SAMPLE = "get_los_activity_sample";

    private static final String BEGIN_TRANSITION = "apply-loan";
    private static final String BEGIN_STATE = "loan-applied-state";


    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        String func = null;
        AccountConfig accountConfig = null;

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);
            accountConfig = InputProcessor.getAccountConfig(req);

            if(func != null){
                if(func.equalsIgnoreCase(POST_LOS_WORKFLOW)){

                }else if(func.equalsIgnoreCase(POST_LOS_APPLICATION)){

                }else if(func.equalsIgnoreCase(POST_LOS_ACTIVITY)) {

                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }




    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        JSONObject wfdef = null;
        String func = null;
        AccountConfig accountConfig = null;
        JSONObject data = null;
        int loanappid = 0;
        String transition = null;
        JSONObject loanapp = null;
        String startingstate = null;
        String useraction = null;
        JSONObject destination = null;
        String destinationtype = null;
        String destinationname = null;

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);
            accountConfig = InputProcessor.getAccountConfig(req);

            if(func != null){
                if(func.equalsIgnoreCase(POST_LOS_WORKFLOW)){
                    output = defineLOSWorkflow(accountConfig.getTenant(),input);
                }else if(func.equalsIgnoreCase(POST_LOS_APPLICATION)){
                    wfdef = getLOSWorkflowDef(accountConfig.getTenant(),input);
                    if(wfdef != null){
                        loanappid = createLoanApplication(accountConfig.getTenant(), wfdef, input);
                        postWorkflowHistory(accountConfig.getTenant(),input,loanappid, BEGIN_TRANSITION);
                        output = new JSONObject();
                        output.put("loan-app-id",loanappid);
                    }
                }else if(func.equalsIgnoreCase(POST_LOS_ACTIVITY)) {
                    transition = (String) input.get("transition");
                    useraction = (String) input.get("action");
                    loanappid = Integer.parseInt((String) input.get("loan-app-id"));
                    loanapp = getLoanApplication(accountConfig.getTenant(),loanappid);
                    // To do: check if transition is applicable for the current state
                    wfdef = (JSONObject) new JSONParser().parse((String)loanapp.get("wf_def"));
                    startingstate = (String) loanapp.get("state");
                    destination = getDestination(wfdef, transition, startingstate, useraction);
                    if(destination == null){
                        OutputProcessor.sendError(res,HttpServletResponse.SC_FORBIDDEN,"Input does not match the workflow configuration");
                    }else {
                        destinationtype = (String) destination.get("destination-type");
                        destinationname = (String) destination.get("destination-name");
                        while (destinationtype.equalsIgnoreCase("transition")) {
                            postWorkflowHistory(accountConfig.getTenant(), input, loanappid, transition);
                            destination = getDestination(wfdef, transition, startingstate, useraction);
                            destinationtype = (String) destination.get("destination-type");
                            destinationname = (String) destination.get("destination-name");
                        }

                        // Update the final transition and state
                        postWorkflowHistory(accountConfig.getTenant(), input, loanappid, transition);
                        updateLoanApplicationState(accountConfig.getTenant(), loanappid, destinationname);

                        // return output
                        output = new JSONObject();
                        output.put("updated", true);
                    }
                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
    }

    private JSONObject getDestination(JSONObject wfdef, String transition, String startingstate, String useraction) throws Exception{
        JSONObject destination = null;
        JSONArray transitions, actions = null;
        Iterator<JSONObject> it = null;
        JSONObject def,transitionJSON = null;
        JSONArray startingstates = null;

        def = (JSONObject) new JSONParser().parse((String)wfdef.get("wf_def"));
        transitions = (JSONArray) def.get("transitions");
        it = transitions.iterator();
        while(it.hasNext()){
            transitionJSON = (JSONObject) it.next();
            if(transition.equalsIgnoreCase((String)transitionJSON.get("transition"))){
                startingstates = (JSONArray) transitionJSON.get("current-states");
                actions = (JSONArray) transitionJSON.get("actions");
                if(isStartingStatePresent(startingstates,startingstate) &&
                        isUserActionPresent(actions, useraction)){
                    destination = getDestination(actions,useraction);
                    break;
                }
            }
        }
        return destination;
    }

    private JSONObject getDestination(JSONArray actions, String useraction){
        JSONObject destination = null;
        String action = null;
        Iterator it = actions.iterator();
        while(it.hasNext()){
            destination = (JSONObject) it.next();
            action = (String) destination.get("action");
            if(action.equalsIgnoreCase(useraction)){
                break;
            }
        }
        return destination;
    }

    private boolean isUserActionPresent(JSONArray actions, String useraction){
        boolean present = false;
        JSONObject destination = null;
        String action = null;
        Iterator it = actions.iterator();
        while(it.hasNext()){
            destination = (JSONObject) it.next();
            action = (String) destination.get("action");
            if(action.equalsIgnoreCase(useraction)){
                present = true;
                break;
            }
        }
        return present;
    }

    private boolean isStartingStatePresent(JSONArray states, String startingstate){
        boolean present = false;
        String state = null;
        Iterator it = states.iterator();
        while(it.hasNext()){
            state = (String) it.next();
            if(state.equalsIgnoreCase(startingstate)){
                present = true;
                break;
            }
        }
        return present;
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
        query.setValue(Types.VARCHAR,BEGIN_STATE);

        loanAppId = DB.insert(query);
        return loanAppId;
    }

    private void updateLoanApplicationState(JSONObject tenant, int loanappid, String state) throws Exception{
        String sql1,sql2 = null;
        DBQuery query = null;
        DBResult rs = null;

        sql1 = "update _solutions_finance_los_tsi_wf_loan set state=? where wf_loan_id=?";
        query = new DBQuery( tenant, sql1);
        query.setValue(Types.VARCHAR,state);
        query.setValue(Types.INTEGER,loanappid+"");

        DB.update(query);
    }


    private JSONObject getLoanApplication(JSONObject tenant, int loanappid) throws Exception{
        String sql = null;
        DBQuery query = null;
        DBResult rs = null;
        JSONObject record = null;
        String state = null;
        JSONObject wfdef = null;

        sql = "select state,wf_def from _solutions_finance_los_tsi_wf_loan where wf_loan_id=?";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.INTEGER,loanappid+"");

        rs = DB.fetch(query);
        if(rs.hasNext()){
            record = (JSONObject) rs.next();
        }
        return record;
    }

    private void postWorkflowHistory(JSONObject tenant, JSONObject input, int loanAppId, String transition) throws Exception{
        String sql = null;
        DBQuery query = null;
        String workflowcode = (String) input.get("los-workflow-code");
        String clientuserid = (String) input.get("client-user-id");
        String clientusername = (String) input.get("client-user-name");
         String latt = (String) input.get("lat");
        String longt = (String) input.get("long");

        sql = "insert into _solutions_finance_los_tsi_wf_history (wf_code,wf_loan_id,client_user_id,client_user_name,transition,lat,long) values (?,?,?,?,?,?,?)";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,workflowcode);
        query.setValue(Types.INTEGER,loanAppId+"");
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,clientusername);
        query.setValue(Types.VARCHAR,transition);
        query.setValue(Types.DOUBLE,latt);
        query.setValue(Types.DOUBLE,longt);
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

        JSONObject input = null;
        JSONObject output = null;
        JSONObject wfdef = null;
        String func = null;
        AccountConfig accountConfig = null;
        JSONObject apiConfig = null;
        JSONObject data = null;

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);
            accountConfig = InputProcessor.getAccountConfig(req);
            apiConfig = accountConfig.getAPIConfig(API_PROVIDER,API_NAME);

            if(method.equalsIgnoreCase("POST") && func != null){
                if(func.equalsIgnoreCase(POST_LOS_WORKFLOW)){

                }else if(func.equalsIgnoreCase(POST_LOS_APPLICATION)){

                }else if(func.equalsIgnoreCase(POST_LOS_ACTIVITY)) {

                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }

    }
}
