package in.tsiconsulting.accelerator.system.core;

import java.util.LinkedHashMap;

import org.json.simple.JSONValue;

import javax.servlet.http.HttpServletResponse;

public class OutputProcessor {

    public static final String MEDIA_TYPE_JSON = "application/json";

    public static String getOrderedJSONifiedString(LinkedHashMap hm) {
		  /* try {
			JSONValue.writeJSONString(hm, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        String jsonText = JSONValue.toJSONString(hm);
        // System.out.print(jsonText);
        return jsonText;

    }

    public static void send(HttpServletResponse res, Object data) {
        try {
            res.setContentType(MEDIA_TYPE_JSON);
            res.setCharacterEncoding("UTF-8");
            //int status = (Integer)(((JSONObject)data).get("status"));
            //res.setStatus(status);
            if (data != null) {
                if (data instanceof byte[]) {
                    res.getOutputStream().write((byte[]) data);
                } else {
                    res.getOutputStream().print(String.valueOf(data));
                }
            }
        } catch (Exception e) {
        }
    }
}
