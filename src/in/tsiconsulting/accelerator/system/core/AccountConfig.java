package in.tsiconsulting.accelerator.system.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class AccountConfig {

    private String accountcode = null;
    private String accountdesc = null;
    private JSONObject dbconfig = null;
    private JSONArray apimodules = null;

    public AccountConfig(String accountcode,
                         String accountdesc,
                         JSONObject dbconfig,
                         JSONArray apimodules){
        this.accountcode = accountcode;
        this.accountdesc = accountdesc;
        this.dbconfig = dbconfig;
        this.apimodules = apimodules;
    }

    public JSONObject getAPIConfig(String provider, String name){
        JSONObject config,result = null;
        Iterator<JSONObject> it = apimodules.iterator();
        while(it.hasNext()){
            config = (JSONObject) it.next();
            if(config.get("provider").equals(provider) && config.get("name").equals(name)){
                result = config;
                break;
            }
        }
        return result;
    }

    public JSONObject getTenant(){
        return dbconfig;
    }
}
