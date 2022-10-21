package in.tsiconsulting.accelerator.solutions.jobs;

import in.tsiconsulting.accelerator.system.core.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Types;

public class Proto implements REST {

    private static final String API_PROVIDER = "Proto";

    private static final String API_NAME = "los";

    private static final String FUNCTION = "_func";
    private static final String DATA = "_data";

    private static final String POST_CANDIDATE = "post_candidate";
    private static final String POST_JOB = "post_job";
    private static final String POST_CANDIDATE_SCORE_DEFINITION = "post_candidate_score_def";
    private static final String POST_CANDIDATE_SCORE_DATA = "post_candidate_score_data";


    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        JSONObject scoredef = null;
        String func = null;
        AccountConfig accountConfig = null;
        JSONObject data = null;

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);
            //System.out.println("func:"+func);
            accountConfig = InputProcessor.getAccountConfig(req);

            if(func != null){
                if(func.equalsIgnoreCase(POST_CANDIDATE_SCORE_DEFINITION)){
                    output = defineCandidateScorecard(accountConfig.getTenant(), input);
                }else if(func.equalsIgnoreCase(POST_CANDIDATE_SCORE_DATA)){

                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
    }

    private JSONObject defineCandidateScorecard(JSONObject tenant, JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        String scode = (String) input.get("scorecard-code");

        if(scorecardexists(tenant,scode)){
            // update
            updateCandidateScorecard(tenant, input);
            out.put("updated",true);
        }else{
            // insert
            insertCandidateScorecard(tenant, input);
            out.put("created",true);
        }


        return out;
    }

    private boolean scorecardexists(JSONObject tenant, String scode) throws Exception{
        boolean exists = false;
        String sql = null;
        DBQuery query = null;
        int count = 0;

        sql = "select count(*) from _solutions_jobs_candidate_score_def where s_code=?";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,scode);
        count = DB.fetchCount(query);
        if(count > 0) exists = true;

        return exists;
    }

    private void insertCandidateScorecard(JSONObject tenant, JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String scode = (String) input.get("scorecard-code");
        String sdesc = (String) input.get("scorecard-desc");
        String clientuserid = (String) input.get("client-user-id");
        JSONArray variables = (JSONArray) input.get("variables");

        sql = "insert into _solutions_jobs_candidate_score_def (s_code,s_desc,client_user_id,variables) values (?,?,?,?::json)";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,scode);
        query.setValue(Types.VARCHAR,sdesc);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,variables.toJSONString());

        DB.update(query);
    }

    private void updateCandidateScorecard(JSONObject tenant, JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String scode = (String) input.get("scorecard-code");
        String sdesc = (String) input.get("scorecard-desc");
        String clientuserid = (String) input.get("client-user-id");
        JSONArray variables = (JSONArray) input.get("variables");

        sql = "update _solutions_jobs_candidate_score_def set s_desc=?,client_user_id=?,variables=?::json where s_code=?";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,sdesc);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,variables.toJSONString());
        query.setValue(Types.VARCHAR,scode);

        DB.update(query);
    }

    @Override
    public void delete(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public boolean validate(String method, HttpServletRequest req, HttpServletResponse res) {
        return true;
    }
}
