package in.tsiconsulting.accelerator.integration.kyc;

import java.sql.Types;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import in.tsiconsulting.accelerator.framework.DB;
import in.tsiconsulting.accelerator.framework.DBQuery;
import in.tsiconsulting.accelerator.framework.HttpClient;
import in.tsiconsulting.accelerator.framework.InputProcessor;
import in.tsiconsulting.accelerator.framework.OutputProcessor;
import in.tsiconsulting.accelerator.framework.REST;
import in.tsiconsulting.accelerator.framework.SystemConfig;

public class AadhaarValidation implements REST{

	private static final String PROVIDER = "digitap";

    private static final String FUNCTION = "_func";
    private static final String DATA = "_data";
    private static final String POST_AADHAAR_BASIC_VALIDATION="aadhaar_basic_validation";

    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        String func = null;
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
            providerUrl = (String) SystemConfig.getAppConfig().get("digitap.apiurl");
            clientId = (String) SystemConfig.getAppConfig().get("digitap.apikey");
            secret = (String) SystemConfig.getAppConfig().get("digitap.apisecret");
            authinput = clientId+":"+secret;
            authorization = "Basic "+ Base64.getEncoder().encodeToString(authinput.getBytes());
            clientuserid = (String) input.get("client_user_id");

            if(func != null){
                if(func.equalsIgnoreCase(POST_AADHAAR_BASIC_VALIDATION)){
                    serviceurl = providerUrl+"kyc/v1/basic_aadhaar";
                    data = new JSONObject();
                    data.put("client_ref_num",(String) input.get("client_ref_num"));
                    data.put("aadhaar",(String) input.get("aadhaarno"));

                    output = new HttpClient().sendPost(serviceurl, authorization, data);
                    logtransaction(clientuserid,func, input,output);
                }
            }
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
        // send output
        OutputProcessor.send(res,HttpServletResponse.SC_OK,output);
    }

    private void logtransaction(String clientuserid, String func, JSONObject req, JSONObject res) throws Exception{
        String sql = null;
        DBQuery query = null;

        sql = "insert into _integration_kyc (func,provider,client_user_id,req,res) values (?,?,?,?::json,?::json)";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,func);
        query.setValue(Types.VARCHAR,PROVIDER);
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
    public boolean validate(String method, HttpServletRequest req, HttpServletResponse res) {
        // Add additional validation if required
        return InputProcessor.validate( req,
                res);
    }
}
