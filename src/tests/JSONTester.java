package tests;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONTester {

    public static void main(String[] args) throws Exception{

        JSONArray jsona = new JSONArray();
        JSONObject json1 = new JSONObject();
        json1.put("title","T1");
        json1.put("id","id1");
        JSONObject json2 = new JSONObject();
        json2.put("title","T2");
        json2.put("id","id2");
        jsona.add(json1);
        jsona.add(json2);

        JSONObject json3 = new JSONObject();
        json3.put("final",jsona);
        String out = json3.toString();
        System.out.println(out);

        JSONObject json4 = (JSONObject) new JSONParser().parse(out);
        System.out.println(json4.toString());


    }
}
