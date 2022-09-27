package in.tsiconsulting.accelerator.system.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.sql.Types;

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
        String apikey = request.getHeader("api-key");
        String apisecret = request.getHeader("api-secret");

        if(apikey!=null && apisecret!=null){
            sql = "select account_code from _sys_api_users where api_key=? and api_secret=?";
            query = new DBQuery( null, sql);
            query.addFilter(Types.VARCHAR,apikey);
            query.addFilter(Types.VARCHAR,apisecret);
            rs = DB.fetch(query);
            if(rs.hasNext()){
                record = (JSONObject) rs.next();
                accountcode = (String) record.get("account_code");
                request.setAttribute(ACCOUNT_CODE, accountcode);
            }
        }
    }

    public static String getAccountCode(HttpServletRequest req){
        return (String) req.getAttribute(InputProcessor.ACCOUNT_CODE);
    }

    public static AccountConfig getAccountConfig(HttpServletRequest req) throws Exception{
        String accountdesc = null;
        JSONObject dbconfig = null;
        JSONArray apimodules = null;
        String accountcode = (String) req.getAttribute(InputProcessor.ACCOUNT_CODE);
        DBQuery query = null;
        DBResult rs = null;
        JSONObject record = null;
        String sql = null;
        JSONParser parser = new JSONParser();
        AccountConfig aconfig = null;

        if(accountcode != null) {
            sql = "select account_desc,db_config,api_modules from _sys_accounts where account_code=?";
            query = new DBQuery(null, sql);
            query.addFilter(Types.VARCHAR, accountcode);
            rs = DB.fetch(query);
            if (rs.hasNext()) {
                record = (JSONObject) rs.next();
                accountdesc = (String)record.get("account_desc");
                dbconfig = (JSONObject) parser.parse((String)record.get("db_config"));
                apimodules = (JSONArray) parser.parse((String)record.get("api_modules"));
                aconfig = new AccountConfig(accountcode,
                                            accountdesc,
                                            dbconfig,
                                            apimodules);
            }
        }
        return aconfig;
    }

    public static JSONObject getInput(HttpServletRequest req) throws Exception{
        return (JSONObject) new JSONParser().parse((String) req.getAttribute(InputProcessor.REQUEST_DATA));
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
