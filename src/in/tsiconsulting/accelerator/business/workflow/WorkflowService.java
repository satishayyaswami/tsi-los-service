package in.tsiconsulting.accelerator.business.workflow;

import in.tsiconsulting.accelerator.framework.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Types;
import java.util.Iterator;

public class WorkflowService implements REST {

    private static final String FUNCTION = "_func";
    private static final String DATA = "_data";

    private static final String POST_WORKFLOW = "post_workflow_def";
    private static final String POST_TASK = "post_task";
    private static final String POST_TASK_ACTIVITY = "post_task_activity";

    private static final String GET_WORKFLOW = "get_workflow";
    private static final String GET_DATA_FIELDS = "get_data_fields";

    private static final String GET_TASKS = "get_tasks";

    private static final String GET_TASK_ACTIVITIES = "get_task_activities";

    private static final String BEGIN_TRANSITION = "init";
    private static final String BEGIN_STATE = "start";


    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        JSONArray outputArr = null;
        String func = null;
        JSONObject wfdef = null;
        JSONArray inputfields = null;
        String transition = null;

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);

            if(func != null){
                if(func.equalsIgnoreCase(GET_WORKFLOW)){
                    output = getWorkflow(input);
                }else if(func.equalsIgnoreCase(GET_TASKS)){
                    outputArr = getTasks(input);
                }else if(func.equalsIgnoreCase(GET_TASK_ACTIVITIES)) {
                    outputArr = getTaskActivities(input);
                }else if(func.equalsIgnoreCase(GET_DATA_FIELDS)) {
                    transition = (String) input.get("transition");
                    wfdef = getWorkflow(input);
                    inputfields = getNestedInputFields(wfdef,transition, new JSONArray());
                    outputArr = inputfields;
                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output!=null?output:outputArr);
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
        JSONObject data = null;
        int taskid = 0;
        String transition = null;
        JSONObject task = null;
        String startingstate = null;
        String useraction = null;
        JSONObject destination = null;
        String destinationtype = null;
        String destinationname = null;

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);

            if(func != null){
                if(func.equalsIgnoreCase(POST_WORKFLOW)){
                    output = defineWorkflow(input);
                }else if(func.equalsIgnoreCase(POST_TASK)){
                    wfdef = getWorkflow(input);
                    if(wfdef != null){
                        taskid = createTask( wfdef, input);
                        postTaskActivity(input,taskid, BEGIN_TRANSITION);
                        output = new JSONObject();
                        output.put("task-id",taskid);
                    }
                }else if(func.equalsIgnoreCase(POST_TASK_ACTIVITY)) {
                    transition = (String) input.get("transition");
                    useraction = (String) input.get("action");
                    taskid = Integer.parseInt((String) input.get("task-id"));
                    task = getTask(taskid);
                    // To do: check if transition is applicable for the current state
                    wfdef = (JSONObject) new JSONParser().parse((String)task.get("wf_def"));
                    startingstate = (String) task.get("state");
                    destination = getDestination(wfdef, transition, startingstate, useraction);
                    if(destination == null){
                        OutputProcessor.sendError(res,HttpServletResponse.SC_FORBIDDEN,"Input does not match the workflow configuration");
                    }else {
                        destinationtype = (String) destination.get("destination-type");
                        destinationname = (String) destination.get("destination-name");
                        while (destinationtype.equalsIgnoreCase("transition")) {
                            postTaskActivity(input, taskid, transition);
                            destination = getDestination(wfdef, transition, startingstate, useraction);
                            destinationtype = (String) destination.get("destination-type");
                            destinationname = (String) destination.get("destination-name");
                        }

                        // Update the final transition and state
                        postTaskActivity(input, taskid, transition);
                        updateTaskState(taskid, destinationname);

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

        transitions = (JSONArray) wfdef.get("transitions");
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

    private JSONObject getTransition(JSONObject wfdef, String transitionname){
        JSONObject transition = null;
        JSONArray transitions = null;
        Iterator<JSONObject> it = null;

        transitions = (JSONArray) wfdef.get("transitions");
        it = transitions.iterator();
        while(it.hasNext()){
            transition = (JSONObject) it.next();
            if(transitionname.equalsIgnoreCase((String)transition.get("transition"))){
                break;
            }
        }
        return transition;
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

    private JSONArray getNestedInputFields(JSONObject wfdef,String transitionname, JSONArray inputfields){
        boolean present = false;
        JSONObject destination = null;
        String action = null;
        JSONArray actions = null;
        String destinationtype = null;
        String destinationname = null;
        JSONObject transition = null;

        // add input fields
        transition = getTransition(wfdef, transitionname);
        inputfields.addAll((JSONArray) transition.get("data-fields"));

        actions = (JSONArray) transition.get("actions");
        Iterator it = actions.iterator();
        while(it.hasNext()){
            destination = (JSONObject) it.next();
            action = (String) destination.get("action");
            destinationtype = (String) destination.get("destination-type");
            destinationname = (String) destination.get("destination-name");
            if(destinationtype.equalsIgnoreCase("transition")){
                getNestedInputFields(wfdef,destinationname,inputfields);
            }
        }
        return inputfields;
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

    private JSONObject defineWorkflow(JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        String workflowcode = (String) input.get("workflow-code");

        if(workflowexists(workflowcode)){
            // update
            updateWorkflow(input);
            out.put("updated",true);
        }else{
            // insert
            insertWorkflow(input);
            out.put("created",true);
        }

        // sync tenantdb json indexes
        // new DBSync().newaccountsync();

        return out;
    }

    private boolean workflowexists(String workflowcode) throws Exception{
        boolean exists = false;
        String sql = null;
        DBQuery query = null;
        int count = 0;

        sql = "select count(*) from _solutions_finance_los_tsi_wf_def where wf_code=?";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,workflowcode);
        count = DB.fetchCount(query);
        if(count > 0) exists = true;

        return exists;
    }

    private void insertWorkflow(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String workflowcode = (String) input.get("los-workflow-code");
        String workflowname = (String) input.get("los-workflow-name");
        String clientuserid = (String) input.get("client-user-id");
        JSONObject wfdef = (JSONObject) input.get("los-workflow-definition");

        sql = "insert into _solutions_finance_los_tsi_wf_def (wf_code,wf_name,client_user_id,wf_def) values (?,?,?,?::json)";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,workflowcode);
        query.setValue(Types.VARCHAR,workflowname);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,wfdef.toJSONString());

        DB.update(query);
    }

    private void updateWorkflow(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String workflowcode = (String) input.get("los-workflow-code");
        String workflowname = (String) input.get("los-workflow-name");
        String clientuserid = (String) input.get("client-user-id");
        JSONObject wfdef = (JSONObject) input.get("los-workflow-definition");

        sql = "update _solutions_finance_los_tsi_wf_def set wf_name=?,client_user_id=?,wf_def=?::json where wf_code=?";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,workflowname);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,wfdef.toJSONString());
        query.setValue(Types.VARCHAR,workflowcode);

        DB.update(query);
    }

    private JSONObject getWorkflow(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        JSONObject wfdef = null;
        DBResult rs = null;
        String workflowcode = (String) input.get("los-workflow-code");

        sql = "select wf_def from _solutions_finance_los_tsi_wf_def where wf_code=?";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,workflowcode);
        rs = DB.fetch(query);
        if(rs.hasNext()){
            wfdef = rs.next();
            wfdef = (JSONObject) new JSONParser().parse((String)wfdef.get("wf_def"));
        }
        return wfdef;
    }

    private int createTask(JSONObject wfdef, JSONObject input) throws Exception{
        String sql1,sql2 = null;
        DBQuery query = null;
        DBResult rs = null;
        int taskId = 0;

        String workflowcode = (String) input.get("los-workflow-code");
        String clientuserid = (String) input.get("client-user-id");
        JSONObject data = (JSONObject) input.get("data");

        sql1 = "insert into _solutions_finance_los_tsi_wf_loan (wf_code,client_user_id,wf_def,data,state) values (?,?,?::json,?::json,?)";
        query = new DBQuery( sql1);
        query.setValue(Types.VARCHAR,workflowcode);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,wfdef.toJSONString());
        query.setValue(Types.VARCHAR,data.toJSONString());
        query.setValue(Types.VARCHAR,"");

        taskId = DB.insert(query);
        return taskId;
    }

    private void updateTaskState(int taskid, String state) throws Exception{
        String sql1,sql2 = null;
        DBQuery query = null;
        DBResult rs = null;

        sql1 = "update _solutions_finance_los_tsi_wf_loan set state=? where wf_loan_id=?";
        query = new DBQuery( sql1);
        query.setValue(Types.VARCHAR,state);
        query.setValue(Types.INTEGER,taskid+"");

        DB.update(query);
    }

    private JSONArray getTasks(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        DBResult rs = null;
        int loanAppId = 0;
        DBResult result = null;

        String workflowcode = (String) input.get("los-workflow-code");

        sql = "select wf_loan_id,client_user_id,data,state,created from _solutions_finance_los_tsi_wf_loan where wf_code=?";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,workflowcode);

        result = DB.fetch(query);
        return result.toJSONArray();
    }


    private JSONObject getTask(int taskid) throws Exception{
        String sql = null;
        DBQuery query = null;
        DBResult rs = null;
        JSONObject record = null;
        String state = null;
        JSONObject wfdef = null;

        sql = "select state,wf_def from _solutions_finance_los_tsi_wf_loan where wf_loan_id=?";
        query = new DBQuery( sql);
        query.setValue(Types.INTEGER,taskid+"");

        rs = DB.fetch(query);
        if(rs.hasNext()){
            record = (JSONObject) rs.next();
        }
        return record;
    }

    private void postTaskActivity(JSONObject input, int loanAppId, String transition) throws Exception{
        String sql = null;
        DBQuery query = null;
        String workflowcode = (String) input.get("los-workflow-code");
        String clientuserid = (String) input.get("client-user-id");
        String clientusername = (String) input.get("client-user-name");
         String latt = (String) input.get("lat");
        String longt = (String) input.get("long");

        sql = "insert into _solutions_finance_los_tsi_wf_history (wf_code,wf_loan_id,client_user_id,client_user_name,transition,lat,long) values (?,?,?,?,?,?,?)";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,workflowcode);
        query.setValue(Types.INTEGER,loanAppId+"");
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,clientusername);
        query.setValue(Types.VARCHAR,transition);
        query.setValue(Types.DOUBLE,latt);
        query.setValue(Types.DOUBLE,longt);
        DB.update(query);
    }

    private JSONArray getTaskActivities(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        DBResult result = null;

        String workflowcode = (String) input.get("workflow-code");
        String taskid = (String) input.get("task-id");

        sql = "select client_user_id,client_user_name,transition,lat,long,created from _solutions_finance_los_tsi_wf_history where wf_code=? and wf_loan_id=?";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,workflowcode);
        query.setValue(Types.INTEGER,taskid);
        result = DB.fetch(query);
        return result.toJSONArray();
    }

    @Override
    public void delete(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public boolean validate(String method, HttpServletRequest req, HttpServletResponse res) {
        // Add additional validation if required
        return InputProcessor.validate( req, res);
    }
}
