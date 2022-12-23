package in.tsiconsulting.accelerator.framework;

import com.networknt.schema.ValidationMessage;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Types;
import java.util.Base64;
import java.util.Set;
import java.util.StringTokenizer;

public class InputProcessor {
    private static final Logger log = Logger.getLogger(InputProcessor.class);

    public final static String REQUEST_DATA = "input_json";
    public final static String ACCOUNT_CODE = "account_code";

    public static void processInput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contentType = request.getContentType();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append(System.lineSeparator());
        }
        String data = buffer.toString();
        request.setAttribute(REQUEST_DATA, data);
    }

    public static void processHeader(HttpServletRequest request, HttpServletResponse response) throws Exception {
        DBQuery query = null;
        DBResult rs = null;
        JSONObject record = null;
        String sql = null;
        String accountcode = null;
        String apikey = null;
        String apisecret = null;
        JSONObject keysecret = null;


        keysecret = getKeySecret(request,response);
        apikey = (String) keysecret.get("apikey");
        apisecret = (String) keysecret.get("apisecret");

        System.out.println(keysecret);

        if(apikey!=null && apisecret!=null){
            sql = "select account_code from _sys_api_users where api_key=? and api_secret=?";
            query = new DBQuery( sql);
            query.setValue(Types.VARCHAR,apikey);
            query.setValue(Types.VARCHAR,apisecret);
            rs = DB.fetch(query);
            if(rs.hasNext()){
                record = (JSONObject) rs.next();
                accountcode = (String) record.get("account_code");
                request.setAttribute(ACCOUNT_CODE, accountcode);
            }
        }
    }

    public static JSONObject getKeySecret(HttpServletRequest req, HttpServletResponse res) throws Exception{
        JSONObject keysecret = new JSONObject();
        String authorization = null;
        StringTokenizer strTok = null;
        String encodedtoken = null;
        byte[] decodedBytes = null;
        String decodedtoken = null;

        try {
            authorization = req.getHeader("Authorization");
            strTok = new StringTokenizer(authorization, " ");
            strTok.nextToken();
            encodedtoken = strTok.nextToken();
            decodedBytes = Base64.getDecoder().decode(encodedtoken);
            decodedtoken = new String(decodedBytes);
            strTok = new StringTokenizer(decodedtoken, ":");
            keysecret.put("apikey",strTok.nextToken());
            keysecret.put("apisecret",strTok.nextToken());
        }catch (Exception e){
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
        return keysecret;
    }

    public static String getAccountCode(HttpServletRequest req){
        return (String) req.getAttribute(InputProcessor.ACCOUNT_CODE);
    }

    public static JSONObject getInput(HttpServletRequest req) throws Exception{
        JSONObject input = null;
        String inputs = null;
        try {
            inputs = (String) req.getAttribute(InputProcessor.REQUEST_DATA);
            if(inputs!=null) inputs = inputs.trim();
            input = (JSONObject) new JSONParser().parse(inputs);
        }catch(Exception e){
            e.printStackTrace();
        }
        return input;
    }

    public static boolean validate(HttpServletRequest req, HttpServletResponse res) {

        JSONObject input = null;
        Set<ValidationMessage> errors = null;
        boolean valid = true;
        String func = null;

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get("_func");

            if(func == null){
                OutputProcessor.sendError(res,HttpServletResponse.SC_BAD_REQUEST,"_func missing");
                valid = false;
            }else{
                errors = JSONSchemaValidator.getHandle().validateSchema(func, input);
            }

            if(errors != null && errors.size()>0) {
                OutputProcessor.sendError(res,HttpServletResponse.SC_BAD_REQUEST, errors.toString());
                valid = false;
            }

        }catch(Exception e){
            e.printStackTrace();
            OutputProcessor.sendError(res,HttpServletResponse.SC_BAD_REQUEST,"Unknown input validation error");
            valid = false;
        }
        return valid;
    }

    public static String applyRules(String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                value = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
            value = StringEscapeUtils.unescapeHtml(value);
        } else {
            value = "";
        }
        return value;
    }
}
