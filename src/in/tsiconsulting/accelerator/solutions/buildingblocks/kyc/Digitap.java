package in.tsiconsulting.accelerator.solutions.buildingblocks.kyc;

import in.tsiconsulting.accelerator.system.core.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Types;
import java.util.Base64;

public class Digitap implements REST {

    private static final String API_PROVIDER = "digitap";

    private static final String API_NAME = "kyc";

    private static final String FUNCTION = "_func";
    private static final String DATA = "_data";
    private static final String PAN_BASIC_VALIDATION="pan_basic";

    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        String func = null;
        AccountConfig accountConfig = null;
        JSONObject apiConfig = null;
        String providerUrl = null;
        String clientId = null;
        String secret = null;
        String authinput = null;
        String authorization = null;
        String clientuserid = null;
        String clientrefnum = null;
        String pan = null;
        String name = null;
        String serviceurl = null;
        JSONObject data = null;

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);
            accountConfig = InputProcessor.getAccountConfig(req);
            apiConfig = accountConfig.getAPIConfig(API_PROVIDER,API_NAME);
            providerUrl = (String) apiConfig.get("provider.config.apiurl");
            clientId = (String) apiConfig.get("provider.config.apikey");
            secret = (String) apiConfig.get("provider.config.apisecret");
            authinput = clientId+":"+secret;
            authorization = "Basic "+ Base64.getEncoder().encodeToString(authinput.getBytes());
            clientuserid = (String) input.get("client_user_id");

            if(func != null){
                if(func.equalsIgnoreCase(PAN_BASIC_VALIDATION)){
                    serviceurl = providerUrl+"kyc/v1/pan_basic";
                    data = new JSONObject();
                    data.put("client_ref_num",(String) input.get("client_ref_num"));
                    data.put("pan",(String) input.get("pan"));
                    data.put("name",(String) input.get("name"));

                    output = new TSIHttpClient().sendPost(serviceurl, authorization, data);
                    logtransaction(accountConfig.getTenant(),API_PROVIDER,clientuserid,input,output);
                }
            }

        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
        // send output
        OutputProcessor.send(res,HttpServletResponse.SC_OK,output);
    }

    private void logtransaction(JSONObject tenant, String provider, String clientuserid, JSONObject req, JSONObject res) throws Exception{
        String sql = null;
        DBQuery query = null;

        sql = "insert into _solutions_bb_kyc (provider,client_user_id,req,res) values (?,?,?::json,?::json)";
        query = new DBQuery( tenant, sql);
        query.setValue(Types.VARCHAR,provider);
        query.setValue(Types.VARCHAR,clientuserid);
        query.setValue(Types.VARCHAR, req.toJSONString());
        query.setValue(Types.VARCHAR, res.toJSONString());
        DB.update(query);
    }

    @Override
    public void delete(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void validate(String method, HttpServletRequest req, HttpServletResponse res) {

    }
}
