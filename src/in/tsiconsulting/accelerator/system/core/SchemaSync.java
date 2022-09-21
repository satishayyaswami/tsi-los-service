package in.tsiconsulting.accelerator.system.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Set;

public class SchemaSync implements ServletContextListener {

    private Properties masterProps = null;
    private Properties tenantProps = null;


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Config.load(servletContextEvent.getServletContext());
        masterProps = Config.getMasterSchema();
        tenantProps = Config.getTenantSchema();
        try {
            syncmaster(masterProps);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void syncmaster(Properties masterProps) throws Exception{

        // No. of sql statements in property file
        Set masterKeys = masterProps.keySet();
        int lastkey = Integer.parseInt((String) masterKeys.toArray()[masterKeys.size()-1]);

        // Check if master schema registry is present. If not create it
        createMasterSchemaRegistry();

        // Get sql executed count in master schema registry
        int mrcount = getMasterRegistryCount();

        if(lastkey>mrcount){
            for(int i=mrcount+1; i<=lastkey; i++){
                executeMasterSQL(i, masterProps.getProperty(i+""));
                System.out.println("Master sync "+i+" completed");
            }
        }
        System.out.println("Synced master schema registry");
    }

    private void createMasterSchemaRegistry() throws Exception {
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;

        try {
            con = DB.getAdmin(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("CREATE TABLE IF NOT EXISTS _sys_master_schema_registry (");
            buff.append("schema_seq SERIAL PRIMARY KEY,");
            buff.append("schema_no INTEGER NOT NULL,");
            buff.append("created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            pstmt = con.prepareStatement(buff.toString());
            int i = pstmt.executeUpdate();
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }
    }

    private int getMasterRegistryCount() throws Exception {
        Statement stmt = null;
        StringBuffer buff = null;
        Connection con = null;
        ResultSet rs = null;
        int count = 0;

        try {
            con = DB.getAdmin(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("select count(*) from _sys_master_schema_registry");
            stmt = con.createStatement();
            rs = stmt.executeQuery(buff.toString());
            rs.next();
            count = rs.getInt(1);
        } finally {
            DB.close(rs);
            DB.close(stmt);
            DB.close(con);
        }
        return count;
    }

    private void executeMasterSQL(int schemano, String sql) throws Exception {
        Statement stmt = null;
        Connection con = null;
        ResultSet rs = null;
        int count = 0;
        StringBuffer buff = null;
        PreparedStatement pstmt = null;

        try {
            con = DB.getAdmin(false);

            // execute sql
            stmt = con.createStatement();
            stmt.executeUpdate(sql);

            // update registry
            buff = new StringBuffer();
            buff.append("INSERT INTO _sys_master_schema_registry (schema_no) values(?)");
            pstmt = con.prepareStatement(buff.toString());
            pstmt.setInt(1,schemano);
            pstmt.executeUpdate();

            con.commit();
        } finally {
            DB.close(stmt);
            DB.close(con);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
