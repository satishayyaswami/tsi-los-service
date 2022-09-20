package in.tsiconsulting.accelerator.system.api.setup;


import in.tsiconsulting.accelerator.system.core.DB;
import in.tsiconsulting.accelerator.system.core.OutputProcessor;
import in.tsiconsulting.accelerator.system.core.REST;
import in.tsiconsulting.accelerator.system.core.InputProcessor;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Account implements REST {

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
            input = InputProcessor.getInput(req);
            reboot = (Boolean) input.get("reboot");
            //bootstrap();
        }catch(Exception e){
            OutputProcessor.sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
        }
        OutputProcessor.send(res,HttpServletResponse.SC_OK,null);
    }

    @Override
    public void delete(HttpServletRequest req, HttpServletResponse res) {
    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse res) {
    }

    @Override
    public void validate(String method, HttpServletRequest req, HttpServletResponse res) {
        // To do
    }

    private void setup() throws Exception{
        //createSchemaRegistry();
    }

    private void createAccount() throws Exception {
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;

        try {
            con = DB.getMaster(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("CREATE TABLE IF NOT EXISTS _sys_accounts (");
            buff.append("account_code VARCHAR(6) NOT NULL PRIMARY KEY,");
            buff.append("account_desc VARCHAR(200) NOT NULL,");
            buff.append("active SMALLINT NOT NULL,");
            buff.append("created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            pstmt = con.prepareStatement(buff.toString());
            int i = pstmt.executeUpdate();
            System.out.println("_sys_accounts created");
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }
    }

    private void createSchemaRegistry() throws Exception {
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;

        try {
            con = DB.getMaster(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("CREATE TABLE IF NOT EXISTS _sys_schema_registry (");
            buff.append("schema_seq SERIAL PRIMARY KEY,");
            buff.append("account_code VARCHAR(6) NOT NULL,");
            buff.append("schema_no INTEGER NOT NULL,");
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
    }
}
