package in.tsiconsulting.accelerator.business;

import in.tsiconsulting.accelerator.framework.Base64;
import in.tsiconsulting.accelerator.framework.InputProcessor;
import in.tsiconsulting.accelerator.framework.OutputProcessor;
import in.tsiconsulting.accelerator.framework.REST;
import org.json.simple.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;

public class Document implements REST {

    private static final String FUNCTION = "_func";

    private static final String UPLOAD = "upload";

    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        String func = null;

        try {
            input = InputProcessor.getInput(req);
            func = (String) input.get(FUNCTION);

            if(func != null){
                if(func.equalsIgnoreCase(UPLOAD)){
                    String name = (String) input.get("name");
                    System.out.println(name);
                    String filedata = (String) input.get("filedata");
                    String type = (String) input.get("type");
                    saveDocFile(Base64.decode(filedata));
                }
            }

            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
    }

    public static boolean saveDocFile(byte[] content) {
        boolean isSuccess = false;
        FileOutputStream fos = null;
        try {
            String sStorageDir = "C:\\tmp\\upload";
            File file = new File(sStorageDir, "out.png");
            fos = new FileOutputStream(file);
            fos.write(content);
            fos.close();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
        return isSuccess;
    }

    @Override
    public void delete(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public boolean validate(String method, HttpServletRequest req, HttpServletResponse res) {
        return true;
    }
}
