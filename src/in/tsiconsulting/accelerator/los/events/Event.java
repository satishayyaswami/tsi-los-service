package in.tsiconsulting.accelerator.los.events;

import in.tsiconsulting.accelerator.framework.DB;
import in.tsiconsulting.accelerator.framework.DBQuery;
import in.tsiconsulting.accelerator.framework.DBResult;
import org.json.simple.JSONObject;

import java.sql.Types;

public class Event {

    public final static String ONBOARD_CUSTOMER_EVENT = "onboard_customer";
    public final static String APPLY_LOAN_EVENT = "apply_loan";
    public final static String POST_DISBURSEMENT_EVENT = "post_disbursement";

    public final static String NEW_STATUS = "new";
    public final static String PROCESSED_STATUS = "processed";
    public final static String FAILED_STATUS = "failed";

    public static void add(String name, JSONObject ctx, String clientuserid) throws Exception{
        String sql = null;
        DBQuery query = null;

        sql = "insert into _event (name,ctx,status,client_user_id) values (?,?::json,?,?)";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,name);
        query.setValue(Types.VARCHAR,ctx.toJSONString());
        query.setValue(Types.VARCHAR,NEW_STATUS);
        query.setValue(Types.VARCHAR,clientuserid);
        DB.insert(query);
    }

    public static DBResult getEvents(String eventname, String status) throws Exception{
        String sql = null;
        DBQuery query = null;
        DBResult result = null;
        sql = "select * from _event where name=? and status=?";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,eventname);
        query.setValue(Types.VARCHAR,status);
        result = DB.fetch(query);
        return result;
    }

    public static void updateStatus(int eventid, String status) throws Exception{
        String sql = null;
        DBQuery query = null;
        sql = "update _event set status=? where _eid=?";
        query = new DBQuery(sql);
        query.setValue(Types.VARCHAR,status);
        query.setValue(Types.INTEGER,eventid);
        DB.update(query);
    }
}
