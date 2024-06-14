package in.tsiconsulting.accelerator.los;

import in.tsiconsulting.accelerator.framework.*;
import org.json.simple.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Types;

public class Document implements REST {

    private static final String FUNCTION = "_func";

    private static final String UPLOAD = "upload";

    private static final String FILE_PATH = "C:\\tmp\\upload";

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
                    String file_extn = (String) input.get("file_extn");
                    String file_data = (String) input.get("file_data");
                    int docid = insertDocument(input);
                    saveDocFile(FILE_PATH,docid,file_extn,Base64.decode(file_data));
                    output = new JSONObject();
                    output.put("_docid",docid);
                }
            }
            OutputProcessor.send(res, HttpServletResponse.SC_OK, output);
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unknown server error");
            e.printStackTrace();
        }
    }

    private int insertDocument(JSONObject input) throws Exception{
        int did = 0;
        String sql = null;
        DBQuery query = null;
        String name = (String) input.get("name");
        String file_extn = (String) input.get("file_extn");
        String clientuserid = (String) input.get("client-user-id");

        sql = "insert into _document (name,file_path,file_extn,client_user_id) values (?,?,?,?)";
        query = new DBQuery( sql);
        query.setValue(Types.VARCHAR,name);
        query.setValue(Types.VARCHAR,FILE_PATH);
        query.setValue(Types.VARCHAR,file_extn);
        query.setValue(Types.VARCHAR,clientuserid);
        did = DB.insert(query);
        return did;
    }

    public static boolean saveDocFile(String filepath, int docid, String fileextn, byte[] content) {
        boolean isSuccess = false;
        FileOutputStream fos = null;
        try {
            File file = new File(filepath, docid+"."+fileextn);
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
