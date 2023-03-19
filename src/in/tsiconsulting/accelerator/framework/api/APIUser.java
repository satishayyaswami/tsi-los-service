package in.tsiconsulting.accelerator.framework.api;

import in.tsiconsulting.accelerator.framework.*;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class APIUser implements REST {

    @Override
    public void get(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public void post(HttpServletRequest req, HttpServletResponse res) {
        JSONObject input = null;
        JSONObject output = null;
        boolean userexist = false;
        String email = null;
        String username = null;
        JSONObject out = null;
        try {
            input = InputProcessor.getInput(req);
            email = (String) input.get("email");
            username = (String) input.get("user-name");
            userexist = exists(email);
            if (userexist) {
                OutputProcessor.sendError(res, HttpServletResponse.SC_NOT_ACCEPTABLE, "User already exists");
                return;
            }
            out = create(email, username);
        } catch (Exception e) {
            OutputProcessor.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown server error");
            e.printStackTrace();
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
        // return InputProcessor.validate( req, res);
        return true;
    }

    private boolean exists(String email) throws Exception {
        boolean exists = false;
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        ResultSet rs = null;

        try {
            con = DB.getConnection(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("select * from _sys_api_users where email=?");
            pstmt = con.prepareStatement(buff.toString());
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                exists = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DB.close(rs);
            DB.close(pstmt);
            DB.close(con);
        }
        return exists;
    }

    private JSONObject create(String email, String username) throws Exception {
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        JSONObject out = null;
        String secret = Crypt.getToken(email + "-" + System.currentTimeMillis());

        try {
            con = DB.getConnection(true);
            // insert schema mgr
            buff = new StringBuffer();
            buff.append("insert into _sys_api_users (email,user_name,api_key,api_secret) values (?,?,?,?)");
            pstmt = con.prepareStatement(buff.toString());
            pstmt.setString(1, email);
            pstmt.setString(2, username);
            pstmt.setString(3, email);
            pstmt.setString(4, secret);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DB.close(pstmt);
            DB.close(con);
        }
        out = new JSONObject();
        out.put("user",email);
        out.put("secret",secret);
        return out;
    }
}
