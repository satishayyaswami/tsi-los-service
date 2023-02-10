package in.tsiconsulting.accelerator.business;

import in.tsiconsulting.accelerator.framework.*;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Types;

public class User implements REST {

    private static final String FUNCTION = "_func";
    private static final String DATA = "_data";

    private static final String CREATE_OFFICE = "create_office";
    private static final String CREATE_ROLE = "create_role";
    private static final String CREATE_APP = "create_app";
    private static final String CREATE_USER = "create_user";

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
                if(func.equalsIgnoreCase(CREATE_OFFICE)){
                    output = createOffice(input);
                }else if(func.equalsIgnoreCase(CREATE_ROLE)){
                    output = createRole(input);
                }else if(func.equalsIgnoreCase(CREATE_APP)){
                    output = createApp(input);
                }else if(func.equalsIgnoreCase(CREATE_USER)){
                    output = createUser(input);
                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
    }

    private JSONObject createOffice(JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        String ocode = (String) input.get("o-code");

        if(officeexists(ocode)){
            // update
            updateOffice(input);
            out.put("updated",true);
        }else{
            // insert
            insertOffice(input);
            out.put("created",true);
        }
        return out;
    }

    private boolean officeexists(String ocode) throws Exception{
        boolean exists = false;
        String sql = null;
        DBQuery query = null;
        int count = 0;

        sql = "select count(*) from _office where o_code=?";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,ocode);
        count = DB.fetchCount(query);
        if(count > 0) exists = true;
        return exists;
    }

    private void insertOffice(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String ocode = (String) input.get("o-code");
        String oname = (String) input.get("o-name");
        String odesc = (String) input.get("o-desc");
        String clientuserid = (String) input.get("client-user-id");

        sql = "insert into _office (o_code,o_name,o_desc,client_user_id) values (?,?,?,?)";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,ocode);
        query.setValue(Types.VARCHAR,oname);
        query.setValue(Types.VARCHAR,odesc);
        query.setValue(Types.VARCHAR,clientuserid);
        DB.update(query);
    }

    private void updateOffice(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String ocode = (String) input.get("o-code");
        String oname = (String) input.get("o-name");
        String odesc = (String) input.get("o-desc");
        String clientuserid = (String) input.get("client-user-id");

        sql = "update _office set o_name=?,o_desc=?,client_user_id=? where o_code=?";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,oname);
        query.setValue(Types.VARCHAR,odesc);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,ocode);
        DB.update(query);
    }

    private JSONObject createRole(JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        String rcode = (String) input.get("r-code");

        if(roleexists(rcode)){
            // update
            updateRole(input);
            out.put("updated",true);
        }else{
            // insert
            insertRole(input);
            out.put("created",true);
        }
        return out;
    }

    private boolean roleexists(String rcode) throws Exception{
        boolean exists = false;
        String sql = null;
        DBQuery query = null;
        int count = 0;

        sql = "select count(*) from _role where r_code=?";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,rcode);
        count = DB.fetchCount(query);
        if(count > 0) exists = true;
        return exists;
    }

    private void insertRole(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String rcode = (String) input.get("r-code");
        String rname = (String) input.get("r-name");
        String rdesc = (String) input.get("r-desc");
        JSONObject apps = (JSONObject)  input.get("apps");
        String clientuserid = (String) input.get("client-user-id");

        sql = "insert into _role (r_code,r_name,r_desc,apps,client_user_id) values (?,?,?,?::json,?)";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,rcode);
        query.setValue(Types.VARCHAR,rname);
        query.setValue(Types.VARCHAR,rdesc);
        query.setValue(Types.VARCHAR,apps.toJSONString());
        query.setValue(Types.VARCHAR,clientuserid);
        DB.update(query);
    }

    private void updateRole(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String rcode = (String) input.get("r-code");
        String rname = (String) input.get("r-name");
        String rdesc = (String) input.get("r-desc");
        JSONObject apps = (JSONObject)  input.get("apps");
        String clientuserid = (String) input.get("client-user-id");

        sql = "update _role set r_name=?,r_desc=?,apps=?::json,client_user_id=? where r_code=?";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,rname);
        query.setValue(Types.VARCHAR,rdesc);
        query.setValue(Types.VARCHAR,apps.toJSONString());
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,rcode);
        DB.update(query);
    }

    private JSONObject createApp(JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        String acode = (String) input.get("a-code");

        if(appexists(acode)){
            // update
            updateApp(input);
            out.put("updated",true);
        }else{
            // insert
            insertApp(input);
            out.put("created",true);
        }
        return out;
    }

    private boolean appexists(String acode) throws Exception{
        boolean exists = false;
        String sql = null;
        DBQuery query = null;
        int count = 0;

        sql = "select count(*) from _app where a_code=?";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,acode);
        count = DB.fetchCount(query);
        if(count > 0) exists = true;
        return exists;
    }

    private void insertApp(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String acode = (String) input.get("a-code");
        String aname = (String) input.get("a-name");
        String adesc = (String) input.get("a-desc");
        JSONObject defaultcontrols = (JSONObject)  input.get("default-controls");
        String clientuserid = (String) input.get("client-user-id");

        sql = "insert into _app (a_code,a_name,a_desc,default_controls,client_user_id) values (?,?,?,?::json,?)";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,acode);
        query.setValue(Types.VARCHAR,aname);
        query.setValue(Types.VARCHAR,adesc);
        query.setValue(Types.VARCHAR,defaultcontrols.toJSONString());
        query.setValue(Types.VARCHAR,clientuserid);
        DB.update(query);
    }

    private void updateApp(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String acode = (String) input.get("a-code");
        String aname = (String) input.get("a-name");
        String adesc = (String) input.get("a-desc");
        JSONObject defaultcontrols = (JSONObject)  input.get("default-controls");
        String clientuserid = (String) input.get("client-user-id");

        sql = "update _app set a_name=?,a_desc=?,default_controls=?::json, client_user_id=? where a_code=?";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,aname);
        query.setValue(Types.VARCHAR,adesc);
        query.setValue(Types.VARCHAR,defaultcontrols.toJSONString());
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,acode);
        DB.update(query);
    }

    private JSONObject createUser(JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        String mobile = (String) input.get("mobile");
        String email = (String) input.get("email");

        if(userexists(mobile, email)){
            // update
            updateApp(input);
            out.put("updated",true);
        }else{
            // insert
            insertApp(input);
            out.put("created",true);
        }
        return out;
    }

    private boolean userexists(String mobile, String email) throws Exception{
        boolean exists = false;
        String sql = null;
        DBQuery query = null;
        int count = 0;

        if(mobile != null && mobile.trim().length()>0) {
            sql = "select count(*) from _user where mobile=?";
            query = new DBQuery(sql);
            query.setValue(Types.VARCHAR, mobile);
            count = DB.fetchCount(query);
            if (count > 0) exists = true;
        } else if (email != null && email.trim().length()>0) {
            sql = "select count(*) from _user where email=?";
            query = new DBQuery(sql);
            query.setValue(Types.VARCHAR, email);
            count = DB.fetchCount(query);
            if (count > 0) exists = true;
        }
        return exists;
    }

    private void insertUser(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String mobile = (String) input.get("mobile");
        String email = (String) input.get("email");
        String passwd = (String) input.get("passwd");
        String ocode = (String) input.get("o-code");
        String rcode = (String) input.get("r-code");
        JSONObject attr = (JSONObject)  input.get("attr");
        passwd = new Crypt().encrypt(passwd); // encrypt
        String clientuserid = (String) input.get("client-user-id");

        sql = "insert into _user (mobile,email,passwd,o_code,r_code,attr,client_user_id) values (?,?,?,?,?,?::json,?)";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,mobile);
        query.setValue(Types.VARCHAR,email);
        query.setValue(Types.VARCHAR,passwd);
        query.setValue(Types.VARCHAR,ocode);
        query.setValue(Types.VARCHAR,rcode);
        query.setValue(Types.VARCHAR,attr.toJSONString());
        query.setValue(Types.VARCHAR,clientuserid);
        DB.update(query);
    }

    private void updateUser(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String mobile = (String) input.get("mobile");
        String email = (String) input.get("email");
        String passwd = (String) input.get("passwd");
        String ocode = (String) input.get("o-code");
        String rcode = (String) input.get("r-code");
        JSONObject attr = (JSONObject)  input.get("attr");
        passwd = new Crypt().encrypt(passwd); // encrypt
        String clientuserid = (String) input.get("client-user-id");

        sql = "update _user set o_code=?,r_code=?,attr=?::json, client_user_id=? where mobile=? and email=?";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,ocode);
        query.setValue(Types.VARCHAR,rcode);
        query.setValue(Types.VARCHAR,attr.toJSONString());
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,mobile);
        query.setValue(Types.VARCHAR,email);
        DB.update(query);
    }

    @Override
    public void delete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public void put(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public boolean validate(String s, HttpServletRequest req, HttpServletResponse res) {
        // Add additional validation if required
        //return InputProcessor.validate( req, res);
        return true;
    }
}
