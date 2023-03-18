package in.tsiconsulting.accelerator.business.events;

import in.tsiconsulting.accelerator.framework.DB;
import in.tsiconsulting.accelerator.framework.DBQuery;
import org.json.simple.JSONObject;

import java.sql.Types;

public class Event {

    public final static String ONBOARD_CUSTOMER = "onboard_customer";
    public final static String APPLY_LOAN = "apply_loan";

    public static void publish(String name, JSONObject ctx, String clientuserid) throws Exception{
        String sql = null;
        DBQuery query = null;

        sql = "insert into _event (name,ctx,client_user_id) values (?,?::json,?)";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,name);
        query.setValue(Types.VARCHAR,ctx.toJSONString());
        query.setValue(Types.VARCHAR,clientuserid);
        DB.insert(query);
    }
}
