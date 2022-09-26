package in.tsiconsulting.accelerator.system.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Types;

public class DBQuery {

    protected JSONObject tenant = null;
    protected String sql = null;
    protected JSONArray filters = null;

    public DBQuery(JSONObject tenant,
                   String sql){
        this.tenant = tenant;
        this.sql = sql;
        this.filters = new JSONArray();
    }

    public void addFilter(int type, String value){
        JSONObject filter = new JSONObject();
        filter.put("type", type);
        filter.put("value",value);
        filters.add(filter);
    }
}
