package in.tsiconsulting.accelerator.system.api.setup;

import in.tsiconsulting.accelerator.system.core.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class APIUser implements REST {
    private static final String API_PROVIDER = "system";

    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        boolean userexist = false;
        String accountcode = null;
        String email = null;
        String username = null;
        JSONObject out = null;
        try {
            input = InputProcessor.getInput(req);
            accountcode = (String) input.get("account-code");
            email = (String) input.get("email");
            username = (String) input.get("user-name");
            userexist = exists(accountcode, email);
            if (userexist) {
                OutputProcessor.sendError(res, HttpServletResponse.SC_NOT_ACCEPTABLE, "User already exists");
                return;
            }
            out = create(accountcode, email, username);
        } catch (Exception e) {
            OutputProcessor.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown server error");
            //e.printStackTrace();
        }
        OutputProcessor.send(res, HttpServletResponse.SC_OK, out);
    }

    @Override
    public void delete(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void put(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public boolean validate(String method, HttpServletRequest req, HttpServletResponse res) {
        // Add additional validation if required
        return InputProcessor.validate( API_PROVIDER,
                req,
                res);
    }

    private boolean exists(String accountcode, String email) throws Exception {
        boolean exists = false;
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        ResultSet rs = null;

        try {
            con = DB.getAdmin(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("select * from _sys_api_users where account_code=? and email=?");
            pstmt = con.prepareStatement(buff.toString());
            pstmt.setString(1, accountcode);
            pstmt.setString(2, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                exists = true;
            }
        } finally {
            DB.close(rs);
            DB.close(pstmt);
            DB.close(con);
        }
        return exists;
    }

    private JSONObject create(String accountcode, String email, String username) throws Exception {
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        JSONObject out = null;
        String secret = Crypt.getToken(accountcode + "-" + email + "-" + System.currentTimeMillis());

        try {
            con = DB.getAdmin(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("insert into _sys_api_users (email,account_code,user_name,api_key,api_secret) values (?,?,?,?,?)");
            pstmt = con.prepareStatement(buff.toString());
            pstmt.setString(1, email);
            pstmt.setString(2, accountcode);
            pstmt.setString(3, username);
            pstmt.setString(4, email);
            pstmt.setString(5, secret);
            pstmt.executeUpdate();
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }
        out = new JSONObject();
        out.put("user",email);
        out.put("secret",secret);
        return out;
    }
}
