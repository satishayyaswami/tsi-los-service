package in.tsiconsulting.accelerator.solutions.buildingblocks.kyc;

import in.tsiconsulting.accelerator.system.core.InputProcessor;
import in.tsiconsulting.accelerator.system.core.REST;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Digitap implements REST {
    private static final String METHOD = "_method";
    private static final String DATA = "_data";
    private static final String PAN_BASIC_VALIDATION="pan_basic";

    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        System.out.println("In digitap");
        JSONObject input = null;
        JSONObject output = null;
        String method = null;
         try {
            input = InputProcessor.getInput(req);
            method = (String) input.get(METHOD);
            if(method != null){
                if(method.equalsIgnoreCase(PAN_BASIC_VALIDATION)){

                }
            }
        }catch(Exception e){
            output = new JSONObject();
            output.put("status",500);
            output.put("message",e.getMessage());
            e.printStackTrace();
        }
        //req.setAttribute(InputProcessor.OUTPUT_DATA,output);
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
