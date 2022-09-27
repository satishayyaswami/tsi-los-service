package in.tsiconsulting.accelerator.system.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
}
