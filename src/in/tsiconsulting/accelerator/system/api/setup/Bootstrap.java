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
        String reboot = null;
        try {
            input = (JSONObject) new JSONParser().parse((String) req.getAttribute(InputProcessor.REQUEST_DATA));
            reboot = (String) input.get("reboot");
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
            buff.append("CREATE TABLE _sys_schema_mgr (");
            buff.append("schema_seq SERIAL PRIMARY KEY,");
            buff.append("sql VARCHAR(1000) NOT NULL,");
            buff.append("active SMALLINT NOT NULL,");
            buff.append("created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");

            int i = pstmt.executeUpdate();
            System.out.println(i+" records inserted");
            con.commit();
            DB.close(pstmt);
            DB.close(con);
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }
        result = new JSONObject();
        result.put("status",HttpServletResponse.SC_OK);
        return result;
    }
}
