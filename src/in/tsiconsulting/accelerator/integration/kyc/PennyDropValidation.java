package in.tsiconsulting.accelerator.integration.kyc;

import java.io.FileReader;
import java.sql.Types;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.concurrent.TimeUnit;

import in.tsiconsulting.accelerator.framework.DB;
import in.tsiconsulting.accelerator.framework.DBQuery;
import in.tsiconsulting.accelerator.framework.HttpClient;
import in.tsiconsulting.accelerator.framework.InputProcessor;
import in.tsiconsulting.accelerator.framework.OutputProcessor;
import in.tsiconsulting.accelerator.framework.REST;
import in.tsiconsulting.accelerator.framework.SystemConfig;

public class PennyDropValidation implements REST{

	private static final String PROVIDER = "digitap";
    private static final String FUNCTION = "_func";
    private static final String DATA = "_data";
    private static final String POST_PENNY_DROP_VALIDATION="penny_drop_validation";
    private static final String SUCCESS_STATE = "SUCCESS";
    private static final String PENDING_STATE = "PENDING";
    private static final String FAILED_STATE = "FAILED";    

    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        JSONObject model = null;
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
        String status = null;
        String transactionid = null;
        int i =0;
        
        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);
            providerUrl = (String) SystemConfig.getAppConfig().get("digitap.pennydrop.apiurl");
            clientId = (String) SystemConfig.getAppConfig().get("digitap.apikey");
            secret = (String) SystemConfig.getAppConfig().get("digitap.apisecret");
            authinput = clientId+":"+secret;
            authorization = Base64.getEncoder().encodeToString(authinput.getBytes());
            System.out.println(authorization);
            clientuserid = (String) input.get("client_user_id");

            if(func != null){
                if(func.equalsIgnoreCase(POST_PENNY_DROP_VALIDATION)){
                    serviceurl = providerUrl+"penny-drop/v1/check-valid";
                    data = new JSONObject();
                    data.put("client_ref_num",(String) input.get("client_ref_num"));
                    data.put("ifsc",(String) input.get("ifsc"));
                    data.put("accNo",(String) input.get("accNo"));
                    data.put("benificiaryName",(String) input.get("benificiaryName"));
                    data.put("address",(String) input.get("address"));
                    
                    //output = new HttpClient().sendPost(serviceurl, authorization, data,"Pennydrop");
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(new FileReader("C:\\Users\\Divyush Raj\\Desktop\\sample.json"));
                    output = (JSONObject)obj;
                    
                    model = (JSONObject) output.get("model");
                    System.out.println(model.toString());
                    status = (String) model.get("status");
                    System.out.println("Status : "+model.get("status").toString());
                    if(status.equalsIgnoreCase("PENDING") ) {
                    	transactionid = (String) model.get("transactionId");
                    	System.out.println(transactionid);
                    	// call status api
                    	JSONObject statuscheck = checkstatus(transactionid);
                    	
                    }                    
                    System.out.println(output.get("model").getClass());
                    
                    
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
    
    private JSONObject checkstatus(String transactionId) {
    	JSONObject output = null;
        JSONObject model = null;
    	String clientId = (String) SystemConfig.getAppConfig().get("digitap.apikey");
        String secret = (String) SystemConfig.getAppConfig().get("digitap.apisecret");
        String authinput = clientId+":"+secret;
        String authorization = Base64.getEncoder().encodeToString(authinput.getBytes());
        System.out.println(authorization);
    	String providerUrl = (String) SystemConfig.getAppConfig().get("digitap.pennydrop.apiurl");
    	String serviceurl = providerUrl+"penny-drop/v1/check-status";
    	try {
    		output = new HttpClient().sendGet(serviceurl+"?transactionId="+transactionId, authorization);
    		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("Output : "+output.toString());
    	return output;
    }
}
