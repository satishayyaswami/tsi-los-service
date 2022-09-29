package in.tsiconsulting.accelerator.system.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Types;

public class DBQuery {

    protected JSONObject tenant = null;
    protected String sql = null;
    protected JSONArray values = null;

    public DBQuery(JSONObject tenant,
                   String sql){
        this.tenant = tenant;
        this.sql = sql;
        this.values = new JSONArray();
    }

    public void setValue(int type, String value){
        JSONObject filter = new JSONObject();
        filter.put("type", type);
        filter.put("value",value);
        values.add(filter);
    }
}
