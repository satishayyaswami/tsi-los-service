package in.tsiconsulting.accelerator.framework;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class BRE {

    public static final String SEGMENTS = "segments";
    public static final String RULES = "rules";
    public static final String SLUG = "slug";
    public static final String FACT = "fact";
    public static final String OPERATOR = "operator";
    public static final String VALUE = "value";

    public static final String LESS_THAN_OR_EQUAL_TO_OPERATOR = "<=";
    public static final String LESS_THAN_OPERATOR = "<";
    public static final String GREATER_THAN_OR_EQUAL_TO_OPERATOR = ">=";
    public static final String GREATER_THAN_OPERATOR = ">";
    public static final String STRING_EQUALS_OPERATOR = "EQ";

    public static JSONArray getSegments(){
        JSONArray segments = null;
        JSONObject bre = SystemConfig.getBRE();
        segments = (JSONArray) bre.get(SEGMENTS);
        return segments;
    }

    public static JSONObject getSegment(String slug){
        JSONObject result = null;
        JSONObject segment = null;
        String segmentslug = null;
        JSONArray segments = getSegments();
        Iterator<JSONObject> it = segments.iterator();
        while(it.hasNext()){
            segment = (JSONObject) it.next();
            segmentslug = (String) segment.get(SLUG);
            if(segmentslug.equalsIgnoreCase(slug)){
                result = segment;
                break;
            }
        }
        return result;
    }

    public static JSONArray getRules(){
        JSONArray rules = null;
        JSONObject bre = SystemConfig.getBRE();
        rules = (JSONArray) bre.get(RULES);
        return rules;
    }

    public static JSONObject getRule(String slug){
        JSONObject result = null;
        JSONObject rule = null;
        String ruleslug = null;
        JSONArray rules = getRules();
        Iterator<JSONObject> it = rules.iterator();
        while(it.hasNext()){
            rule = (JSONObject) it.next();
            ruleslug = (String) rule.get(SLUG);
            if(ruleslug.equalsIgnoreCase(slug)){
                result = rule;
                break;
            }
        }
        return result;
    }

    public static boolean fireRule(String ruleslug, JSONObject data){
        boolean met = false;
        String fact = null;
        String operator = null;
        String value = null;
        String datavalue = null;
        int valueInt = 0;
        int datavalueint = 0;
        JSONObject rule = getRule(ruleslug);
        fact = (String) rule.get("fact");
        operator = (String) rule.get("operator");
        if(operator.equals(STRING_EQUALS_OPERATOR)){
            value = (String) rule.get("value");
            datavalue = (String) data.get(fact);
            if(value.equalsIgnoreCase(datavalue))
                met = true;
        }else if(operator.equals(LESS_THAN_OR_EQUAL_TO_OPERATOR)){
            valueInt = ((Long) rule.get("value")).intValue();
            datavalueint = ((Long) data.get(fact)).intValue();
            if(datavalueint <= valueInt)
                met = true;
        }else if(operator.equals(LESS_THAN_OPERATOR)){
            valueInt = ((Long) rule.get("value")).intValue();
            datavalueint = ((Long) data.get(fact)).intValue();
            if(datavalueint < valueInt)
                met = true;
        }else if(operator.equals(GREATER_THAN_OPERATOR)){
            valueInt = ((Long) rule.get("value")).intValue();
            datavalueint = ((Long) data.get(fact)).intValue();
            if(datavalueint > valueInt)
                met = true;
        }else if(operator.equals(GREATER_THAN_OR_EQUAL_TO_OPERATOR)){
            valueInt = ((Long) rule.get("value")).intValue();
            datavalueint = ((Long) data.get(fact)).intValue();
            if(datavalueint >= valueInt)
                met = true;
        }
        return met;
    }
}
