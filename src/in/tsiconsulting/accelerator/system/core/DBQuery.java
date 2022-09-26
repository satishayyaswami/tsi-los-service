package in.tsiconsulting.accelerator.system.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DBQuery {

    protected JSONObject tenant = null;
    protected String sql = null;
    protected JSONArray filters = null;

    public DBQuery(JSONObject tenant,
                   String sql,
                   JSONArray filters){
        this.tenant = tenant;
        this.sql = sql;
        this.filters = filters;
    }
}
