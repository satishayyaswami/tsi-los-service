package in.tsiconsulting.accelerator.system.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
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

        try {
            synctenants(tenantProps);
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

    private void synctenants(Properties tenantProps) throws Exception{
        JSONArray tenants = null;
        Iterator<JSONObject> tenantIt = null;
        JSONObject tenant = null;
        String accountcode = null;

        // No. of sql statements in property file
        Set tenantKeys = tenantProps.keySet();
        int lastkey = Integer.parseInt((String) tenantKeys.toArray()[tenantKeys.size()-1]);

        // Get the registered list of tenants
        tenants = getRegisteredTenants();
        tenantIt = tenants.iterator();
        while(tenantIt.hasNext()){
            tenant = (JSONObject) tenantIt.next();
            accountcode = (String) tenant.get("account-code");

            createTenantSchemaRegistry(tenant);

            int tcount = getTenantRegistryCount(tenant);
            if(lastkey>tcount){
                for(int i=tcount+1; i<=lastkey; i++){
                    executeTenantSQL(tenant, i, tenantProps.getProperty(i+""));
                    System.out.println("Tenant "+accountcode+" sync "+i+" completed");
                }
            }
            System.out.println("Synced Tenant "+accountcode);
        }
    }

    private JSONArray getRegisteredTenants() throws Exception{
        JSONArray tenants = new JSONArray();
        JSONObject tenant = null;
        Statement stmt = null;
        StringBuffer buff = null;
        Connection con = null;
        ResultSet rs = null;
        JSONParser parser = new JSONParser();

        try {
            con = DB.getAdmin(true);
            buff = new StringBuffer();
            buff.append("select account_code,db_config from _sys_accounts");
            stmt = con.createStatement();
            rs = stmt.executeQuery(buff.toString());
            while(rs.next()){
                tenant = (JSONObject) parser.parse(rs.getString("db_config"));
                tenant.put("account-code",rs.getString("account_code"));
                tenants.add(tenant);
            }
        } finally {
            DB.close(rs);
            DB.close(stmt);
            DB.close(con);
        }
        return tenants;
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
            buff.append("schema_no INTEGER NOT NULL,");
            buff.append("created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            pstmt = con.prepareStatement(buff.toString());
            int i = pstmt.executeUpdate();
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }
    }

    private void createTenantSchemaRegistry(JSONObject tenant) throws Exception {
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;

        try {
            con = DB.getTenant( (String) tenant.get("db-name"),
                    (String) tenant.get("db-user"),
                    (String) tenant.get("db-pass"),
                    true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("CREATE TABLE IF NOT EXISTS _sys_tenant_schema_registry (");
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

    private int getTenantRegistryCount(JSONObject tenant) throws Exception {
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        ResultSet rs = null;
        int count = 0;

        try {
            con = DB.getTenant( (String) tenant.get("db-name"),
                    (String) tenant.get("db-user"),
                    (String) tenant.get("db-pass"),
                    true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("select count(*) from _sys_tenant_schema_registry");
            pstmt = con.prepareStatement(buff.toString());
            rs = pstmt.executeQuery();
            rs.next();
            count = rs.getInt(1);
        } finally {
            DB.close(rs);
            DB.close(pstmt);
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

    private void executeTenantSQL(JSONObject tenant, int schemano, String sql) throws Exception {
        Statement stmt = null;
        Connection con = null;
        ResultSet rs = null;
        int count = 0;
        StringBuffer buff = null;
        PreparedStatement pstmt = null;

        try {
            con = DB.getTenant( (String) tenant.get("db-name"),
                                (String) tenant.get("db-user"),
                                (String) tenant.get("db-pass"),
                                false);

            // execute sql
            stmt = con.createStatement();
            stmt.executeUpdate(sql);

            // update registry
            buff = new StringBuffer();
            buff.append("INSERT INTO _sys_tenant_schema_registry (schema_no) values(?)");
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
