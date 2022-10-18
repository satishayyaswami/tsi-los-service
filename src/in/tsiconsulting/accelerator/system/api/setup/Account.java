package in.tsiconsulting.accelerator.system.api.setup;


import in.tsiconsulting.accelerator.system.core.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Account implements REST {

    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {
    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        try {
            input = InputProcessor.getInput(req);
            output = setup(input);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
        OutputProcessor.send(res,HttpServletResponse.SC_OK,output);
    }

    @Override
    public void delete(HttpServletRequest req, HttpServletResponse res) {
    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse res) {
    }

    @Override
    public boolean validate(String method, HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        boolean valid = true;
        try {
            input = InputProcessor.getInput(req);
            //OutputProcessor.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Field account-code missing");
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
        return valid;

    }

    private JSONObject setup(JSONObject input) throws Exception{
        String accountcode = null;
        JSONObject dbconfig = null;
        JSONObject apimodules = null;
        JSONObject out = new JSONObject();

        accountcode = (String) input.get("account-code");
        dbconfig = (JSONObject) input.get("db-config");
        apimodules = (JSONObject) input.get("api_modules");

        // check if account code exists
        if(exists(accountcode)){
            // update
            update(input);
            out.put("updated",true);
        }else{
            // insert
            insert(input);
            out.put("created",true);
        }

        // sync tenantdb
        new DBSync().newaccountsync();

        return out;
    }

    private boolean exists(String accountcode) throws Exception {
        boolean exists = false;
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        ResultSet rs = null;

        try {
            con = DB.getAdmin(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("select * from _sys_accounts where account_code=?");
            pstmt = con.prepareStatement(buff.toString());
            pstmt.setString(1,accountcode);
            rs = pstmt.executeQuery();
            if(rs.next()){
                exists = true;
            }
        } finally {
            DB.close(rs);
            DB.close(pstmt);
            DB.close(con);
        }
        System.out.println("exists:"+exists);
        return exists;
    }

    private void insert(JSONObject input) throws Exception {
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        JSONObject dbconfig = (JSONObject) input.get("db-config");
        JSONArray apimodules = (JSONArray) input.get("api-modules");


        try {
            con = DB.getAdmin(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("insert into _sys_accounts (account_code,account_desc,db_config,api_modules) values (?,?,?::json,?::json)");
            pstmt = con.prepareStatement(buff.toString());
            pstmt.setString(1,(String) input.get("account-code"));
            pstmt.setString(2,(String) input.get("account-desc"));
            pstmt.setString(3,dbconfig.toJSONString());
            pstmt.setString(4,apimodules.toJSONString());
            pstmt.executeUpdate();
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }
    }

    private void update(JSONObject input) throws Exception {
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        JSONObject dbconfig = (JSONObject) input.get("db-config");
        JSONArray apimodules = (JSONArray) input.get("api-modules");

        try {
            con = DB.getAdmin(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("update _sys_accounts set account_desc=?,db_config=?::json,api_modules=?::json where account_code=?");
            pstmt = con.prepareStatement(buff.toString());
            pstmt.setString(1,(String) input.get("account-desc"));
            pstmt.setString(2,dbconfig.toJSONString());
            pstmt.setString(3,apimodules.toJSONString());
            pstmt.setString(4,(String) input.get("account-code"));
            pstmt.executeUpdate();
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }
    }
}
