package in.tsiconsulting.accelerator.business;

import in.tsiconsulting.accelerator.framework.*;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Types;

public class Customer implements REST {

    private static final String FUNCTION = "_func";

    private static final String ONBOARD = "onboard";

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
                if(func.equalsIgnoreCase(ONBOARD)){
                    output = onboard(input);
                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
    }

    private JSONObject onboard(JSONObject input) throws Exception{
        JSONObject out = new JSONObject();
        String mobile = (String) input.get("mobile");

        if(customerexists(mobile)){
            // update
            updateCustomer(input);
            out.put("updated",true);
        }else{
            // insert
            insertCustomer(input);
            out.put("created",true);
        }
        return out;
    }

    private boolean customerexists(String mobile) throws Exception{
        boolean exists = false;
        String sql = null;
        DBQuery query = null;
        int count = 0;

        sql = "select count(*) from _customer where mobile=?";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,mobile);
        count = DB.fetchCount(query);
        if(count > 0) exists = true;
        return exists;
    }

    private void insertCustomer(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String mobile = (String) input.get("mobile");
        JSONObject ctx = (JSONObject) input.get("ctx");
        JSONObject pii_attrs = (JSONObject) input.get("pii_attrs");
        JSONObject other_attrs = (JSONObject) input.get("other_attrs");
        String clientuserid = (String) input.get("client-user-id");

        sql = "insert into _customer (mobile,ctx,pii_attrs,other_attrs,client_user_id) values (?,?::json,?::json,?::json,?)";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,mobile);
        query.setValue(Types.VARCHAR,ctx.toJSONString());
        query.setValue(Types.VARCHAR,pii_attrs.toJSONString());
        query.setValue(Types.VARCHAR,other_attrs.toJSONString());
        query.setValue(Types.VARCHAR,clientuserid);
        DB.update(query);
    }

    private void updateCustomer(JSONObject input) throws Exception{
        String sql = null;
        DBQuery query = null;
        String mobile = (String) input.get("mobile");
        JSONObject ctx = (JSONObject) input.get("ctx");
        JSONObject pii_attrs = (JSONObject) input.get("pii_attrs");
        JSONObject other_attrs = (JSONObject) input.get("other_attrs");
        String clientuserid = (String) input.get("client-user-id");

        sql = "update _customer set ctx=?::json,pii_attrs=?::json,other_attrs=?::json,client_user_id=? where o_code=?";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,ctx.toJSONString());
        query.setValue(Types.VARCHAR,pii_attrs.toJSONString());
        query.setValue(Types.VARCHAR,other_attrs.toJSONString());
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR,mobile);
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
        return false;
    }
}
