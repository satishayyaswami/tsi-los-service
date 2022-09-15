package in.tsiconsulting.accelerator.system.api.setup;


import in.tsiconsulting.accelerator.system.core.DB;
import in.tsiconsulting.accelerator.system.core.REST;
import in.tsiconsulting.accelerator.system.core.InputProcessor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Bootstrap implements REST {

    public static final String TSI_ACCELERATOR_DATABASE = "_tsi_accelerator";

    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {
    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        boolean reboot = false;
        try {
            input = (JSONObject) new JSONParser().parse((String) req.getAttribute(InputProcessor.REQUEST_DATA));
            reboot = (Boolean) input.get("reboot");
            output = bootstrap();
        }catch(Exception e){
            output = new JSONObject();
            output.put("status",500);
            output.put("message",e.getMessage());
            e.printStackTrace();
        }
        req.setAttribute(InputProcessor.OUTPUT_DATA,output);
    }

    @Override
    public void delete(HttpServletRequest req, HttpServletResponse res) {
    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse res) {
    }

    private static JSONObject bootstrap() throws SQLException {
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        JSONObject result = null;

        try {
            con = DB.getDBConnection(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("CREATE TABLE _sys_schema_registry (");
            buff.append("schema_seq SERIAL PRIMARY KEY,");
            buff.append("sql VARCHAR(1000) NOT NULL,");
            buff.append("active SMALLINT NOT NULL,");
            buff.append("created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            pstmt = con.prepareStatement(buff.toString());
            int i = pstmt.executeUpdate();
            System.out.println("_sys_schema_registry created");
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }
        result = new JSONObject();
        result.put("status",HttpServletResponse.SC_OK);
        return result;
    }
}
