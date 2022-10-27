package in.tsiconsulting.accelerator.solutions.jobs;

import in.tsiconsulting.accelerator.system.core.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Types;
import java.util.Iterator;
import java.util.Set;

public class Proto implements REST {

    private static final String API_PROVIDER = "proto";

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
        JSONArray variables,grades = null;
        String scode = null;
        long score = 0;
        String grade = "";

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);
            //System.out.println("func:"+func);
            accountConfig = InputProcessor.getAccountConfig(req);
            scode = (String) input.get("s-code");

            if(func != null){
                if(func.equalsIgnoreCase(POST_CANDIDATE_SCORE_DEFINITION)){
                    output = defineCandidateScorecard(accountConfig.getTenant(), input);
                }else if(func.equalsIgnoreCase(POST_CANDIDATE_SCORE_DATA)){
                    scoredef = getScorecardVariablesAndGrades(accountConfig.getTenant(), scode);
                    variables = (JSONArray) new JSONParser().parse((String)scoredef.get("variables"));
                    grades = (JSONArray) new JSONParser().parse((String)scoredef.get("grades"));
                    score =  compute(variables, input);
                    grade = grade(grades, score);
                    output = new JSONObject();
                    output.put("score",score);
                    output.put("grade",grade);
                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
    }

    private long compute(JSONArray variables, JSONObject input) throws Exception{
        JSONObject data,var = null;
        Iterator<JSONObject> it = null;
        long totalscore=0,weightage=0,levelweightage = 0;
        String grade = null;
        String name=null,type = null;
        JSONArray levels = null;
        double varscore=0.0;

        data = (JSONObject) input.get(DATA);
        it = variables.iterator();
        while(it.hasNext()){
            var = (JSONObject) it.next();
            name = (String) var.get("name");
            type = (String) var.get("type");
            levels = (JSONArray) var.get("levels");
            weightage = (long) var.get("weightage");
            if(type.equalsIgnoreCase("NUM")){
                levelweightage = getNumTypeLevelWeightage(levels,(long) data.get(name));
            }else if(type.equalsIgnoreCase("SELECTION")){
                levelweightage = getSelectionTypeLevelWeightage(levels, (String) data.get(name));
            }
            varscore = Math.round((levelweightage * weightage)/100);
            System.out.println("var:"+name+" weightage:"+weightage+" levelweightage:"+levelweightage+" varscore:"+varscore);
            totalscore += varscore;
        }
        return totalscore;
    }

    private String grade(JSONArray grades, long score) throws Exception{
        String grade = "";
        Iterator<JSONObject> it = null;
        JSONObject gradeob = null;
        long min,max=0;

        it = grades.iterator();
        while(it.hasNext()){
            gradeob = (JSONObject) it.next();
            min = (long) gradeob.get("min");
            max = (long) gradeob.get("max");
            if(min <= score && score <= max) {
                grade = (String) gradeob.get("name");
                break;
            }
        }
        return grade;
    }

    private long getNumTypeLevelWeightage(JSONArray levels, long value){
        long weightage = 0;
        JSONObject level = null;
        Iterator<JSONObject> it = null;
        long min,max=0;

        it = levels.iterator();
        while(it.hasNext()){
            level = (JSONObject) it.next();
            min = (long) level.get("min");
            max = (long) level.get("max");
            if(min <= value && value <= max) {
                weightage = (long) level.get("level-weightage");
                break;
            }
        }
        return weightage;
    }

    private long getSelectionTypeLevelWeightage(JSONArray levels, String value){
        long weightage = 0;
        JSONObject level = null;
        Iterator<JSONObject> it = null;
        String scode = null;

        it = levels.iterator();
        while(it.hasNext()){
            level = (JSONObject) it.next();
            scode = (String) level.get("choice");
            if(scode.equalsIgnoreCase(value)) {
                weightage = (long) level.get("level-weightage");
                break;
            }
        }
        return weightage;
    }

    private JSONObject defineCandidateScorecard(JSONObject tenant, JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        String scode = (String) input.get("s-code");

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
        String scode = (String) input.get("s-code");
        String sdesc = (String) input.get("s-desc");
        String clientuserid = (String) input.get("client-user-id");
        JSONArray variables = (JSONArray) input.get("variables");
        JSONArray grades = (JSONArray) input.get("grades");

        sql = "insert into _solutions_jobs_candidate_score_def (s_code,s_desc,client_user_id,variables,grades) values (?,?,?,?::json,?::json)";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,scode);
        query.setValue(Types.VARCHAR,sdesc);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,variables.toJSONString());
        query.setValue(Types.VARCHAR,grades.toJSONString());

        DB.update(query);
    }

    private void updateCandidateScorecard(JSONObject tenant, JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String scode = (String) input.get("s-code");
        String sdesc = (String) input.get("s-desc");
        String clientuserid = (String) input.get("client-user-id");
        JSONArray variables = (JSONArray) input.get("variables");
        JSONArray grades = (JSONArray) input.get("grades");

        sql = "update _solutions_jobs_candidate_score_def set s_desc=?,client_user_id=?,variables=?::json,grades=?::json where s_code=?";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,sdesc);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,variables.toJSONString());
        query.setValue(Types.VARCHAR,grades.toJSONString());
        query.setValue(Types.VARCHAR,scode);

        DB.update(query);
    }

    private JSONObject getScorecardVariablesAndGrades(JSONObject tenant, String scode) throws Exception{
        String sql = null;
        DBQuery query = null;
        JSONArray variables,grades = null;
        DBResult rs = null;
        JSONObject record = null;
        JSONObject result = null;

        sql = "select variables,grades from _solutions_jobs_candidate_score_def where s_code=?";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,scode);
        rs = DB.fetch(query);
        if(rs.hasNext()){
            record = (JSONObject) rs.next();
        }
        return record;
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
        return InputProcessor.validate( API_PROVIDER,
                req,
                res);
    }
}
