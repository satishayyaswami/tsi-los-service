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

    public static void processInput(HttpServletRequest request) throws IOException {
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

    public static void processHeader(HttpServletRequest request) throws Exception {
        DBQuery query = null;
        JSONArray filters = null;
        JSONObject filter1,filter2 = null;
        JSONArray result = null;
        String apikey = request.getHeader("api-key");
        String apisecret = request.getHeader("api-secret");
        System.out.println("apikey:"+apikey+" "+"apisecret:"+apisecret);
        if(apikey!=null && apisecret!=null){
            filters = new JSONArray();
            filter1 = new JSONObject();
            filter1.put("type", Types.VARCHAR);
            filter1.put("value",apikey);
            filter2 = new JSONObject();
            filter2.put("type", Types.VARCHAR);
            filter2.put("value",apisecret);
            filters.add(filter1);
            filters.add(filter2);

            query = new DBQuery(    null,
                                    "select account_code from _sys_api_users where api_key=? and api_secret=?",
                                     filters);
            result = DB.fetch(query);
            System.out.println("output:"+result);
        }
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
