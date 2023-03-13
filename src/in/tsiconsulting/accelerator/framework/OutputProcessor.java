package in.tsiconsulting.accelerator.framework;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;

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

    public static void send(HttpServletResponse res, int status, Object data) {
        try {
            res.setContentType(MEDIA_TYPE_JSON);
            res.setCharacterEncoding("UTF-8");
            res.setStatus(status);
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

    public static void sendError(HttpServletResponse res, int status, String message) {
        res.setContentType(MEDIA_TYPE_JSON);
        res.setCharacterEncoding("UTF-8");
        res.setStatus(status);
        JSONObject out = new JSONObject();
        out.put("status",status);
        out.put("error",message);
        try {
            res.getOutputStream().print(String.valueOf(out));
        } catch (Exception e) {
        }
    }
}
