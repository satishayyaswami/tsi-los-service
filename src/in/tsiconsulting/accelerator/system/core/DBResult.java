package in.tsiconsulting.accelerator.system.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DBResult {

    private JSONArray result = null;
    private int i = 0;

    public DBResult (JSONArray result){
        this.result = result;
    }

    public boolean hasNext(){
        if(result.size()>i)
            return true;
        else
            return false;
    }

    public JSONObject next(){
        JSONObject next = (JSONObject) result.get(i);
        i++;
        return next;
    }

    public JSONArray toJSONArray(){
        return this.result;
    }
}
