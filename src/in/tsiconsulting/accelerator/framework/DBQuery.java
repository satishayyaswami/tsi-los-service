package in.tsiconsulting.accelerator.framework;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DBQuery {

    protected String sql = null;
    protected JSONArray values = null;

    public DBQuery( String sql){
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
