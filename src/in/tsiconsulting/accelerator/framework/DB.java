package in.tsiconsulting.accelerator.framework;

import org.apache.commons.dbcp2.BasicDataSource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;


@SuppressWarnings("unchecked")
public class DB {
    private static BasicDataSource basicDataSource;

    private static synchronized void initBasicDataSource() {
        if (basicDataSource != null) {
            basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName("org.postgresql.Driver");
            basicDataSource.setUrl(SystemConfig.getAppConfig().getProperty("framework.db.host")+"/"+ SystemConfig.getAppConfig().getProperty("framework.db.name"));
            basicDataSource.setUsername(SystemConfig.getAppConfig().getProperty("framework.db.user"));
            basicDataSource.setPassword(SystemConfig.getAppConfig().getProperty("framework.db.password"));
            basicDataSource.setInitialSize(10);
            basicDataSource.setMaxTotal(300);
            basicDataSource.setTestOnBorrow(true);
            basicDataSource.setTestOnReturn(true);
            basicDataSource.setTestWhileIdle(true);
            basicDataSource.setTimeBetweenEvictionRunsMillis(300000);
            basicDataSource.setMinEvictableIdleTimeMillis(600000);
        }
    }

    public static Connection getConnection(boolean autocommit) throws SQLException {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(   SystemConfig.getAppConfig().getProperty("framework.db.host")+"/"+ SystemConfig.getAppConfig().getProperty("framework.db.name"),
                                                            SystemConfig.getAppConfig().getProperty("framework.db.user"),
                                                            SystemConfig.getAppConfig().getProperty("framework.db.password"));
            if(!autocommit)
                connection.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;

//		if(basicDataSource == null) {
//			initBasicDataSource();
//		}
//		connection = basicDataSource.getConnection();
//		connection.setAutoCommit(false);
//		return connection;
    }

    public static int fetchCount(DBQuery query) throws Exception{
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        ResultSet rs = null;
        int count = 0;
        Iterator<JSONObject> valueIt = null;
        JSONObject value = null;
        int type = 0;
        int i=0;

        try {
            con = DB.getConnection(true);
            pstmt = con.prepareStatement(query.sql);
            if(query != null){
                valueIt = query.values.iterator();
                while(valueIt.hasNext()){
                    value = (JSONObject) valueIt.next();
                    type = (int) value.get("type");
                    i++;
                    if(type == Types.INTEGER){
                        pstmt.setInt(i,Integer.parseInt((String) value.get("value")));
                    }else if(type == Types.DOUBLE){
                        pstmt.setDouble(i,Double.parseDouble((String) value.get("value")));
                    }else{
                        pstmt.setString(i,(String) value.get("value"));
                    }
                }
            }
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

    public static DBResult fetch(DBQuery query) throws Exception{
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        ResultSet rs = null;
        int count = 0;
        Iterator<JSONObject> valueIt = null;
        JSONObject value = null;
        int type = 0;
        int i=0;
        JSONArray output = null;

        try {
            con = DB.getConnection(true);
            pstmt = con.prepareStatement(query.sql);
            if(query.values != null){
                valueIt = query.values.iterator();
                while(valueIt.hasNext()){
                    value = (JSONObject) valueIt.next();
                    type = (int) value.get("type");
                    i++;
                    if(type == Types.INTEGER){
                        pstmt.setInt(i,Integer.parseInt((String) value.get("value")));
                    }else if(type == Types.DOUBLE){
                        pstmt.setDouble(i,Double.parseDouble((String) value.get("value")));
                    }else{
                        pstmt.setString(i,(String) value.get("value"));
                    }
                }
            }
            rs = pstmt.executeQuery();
            output = getResults(rs);
        } finally {
            DB.close(rs);
            DB.close(pstmt);
            DB.close(con);
        }
        return new DBResult(output);
    }

    public static void update(DBQuery query) throws Exception{
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        Iterator<JSONObject> valueIt = null;
        JSONObject value = null;
        int type = 0;
        int i=0;
        JSONArray output = null;

        try {
            con = DB.getConnection(true);
            pstmt = con.prepareStatement(query.sql);
            if(query.values != null){
                valueIt = query.values.iterator();
                while(valueIt.hasNext()){
                    value = (JSONObject) valueIt.next();
                    type = (int) value.get("type");
                    i++;
                    if(type == Types.INTEGER){
                        pstmt.setInt(i,Integer.parseInt((String) value.get("value")));
                    }else if(type == Types.DOUBLE){
                        pstmt.setDouble(i,Double.parseDouble((String) value.get("value")));
                    }else{
                        pstmt.setString(i,(String) value.get("value"));
                    }
                }
            }
            pstmt.executeUpdate();
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }

    }


    public static void update(List<DBQuery> queries) throws Exception{
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        Iterator<JSONObject> valueIt = null;
        JSONObject value = null;
        int type = 0;
        int i=0;
        JSONArray output = null;
        DBQuery firstQuery, query = null;
        Iterator it = null;

        try {
            firstQuery = queries.get(0);
            con = DB.getConnection(false);
            it = queries.iterator();
            while(it.hasNext()) {
                query = (DBQuery) it.next();
                pstmt = con.prepareStatement(query.sql);
                if (query.values != null) {
                    valueIt = query.values.iterator();
                    while (valueIt.hasNext()) {
                        value = (JSONObject) valueIt.next();
                        type = (int) value.get("type");
                        i++;
                        if (type == Types.INTEGER) {
                            pstmt.setInt(i, Integer.parseInt((String) value.get("value")));
                        } else if (type == Types.DOUBLE) {
                            pstmt.setDouble(i, Double.parseDouble((String) value.get("value")));
                        } else {
                            pstmt.setString(i, (String) value.get("value"));
                        }
                    }
                }
                pstmt.executeUpdate();
            }
            con.commit();
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }
    }

    public static int insert(DBQuery insert) throws Exception{
        PreparedStatement pstmt = null;
        StringBuffer buff = null;
        Connection con = null;
        Iterator<JSONObject> valueIt = null;
        JSONObject value = null;
        int type = 0;
        int id=0;
        int i=0;
        ResultSet rs = null;
        JSONArray output = null;

        try {
            con = DB.getConnection(true);

            // insert
            pstmt = con.prepareStatement(insert.sql,Statement.RETURN_GENERATED_KEYS);
            if (insert.values != null) {
                valueIt = insert.values.iterator();
                while (valueIt.hasNext()) {
                    value = (JSONObject) valueIt.next();
                    type = (int) value.get("type");
                    i++;
                    if (type == Types.INTEGER) {
                        pstmt.setInt(i, Integer.parseInt((String) value.get("value")));
                    } else if (type == Types.DOUBLE) {
                        pstmt.setDouble(i, Double.parseDouble((String) value.get("value")));
                    } else {
                        pstmt.setString(i, (String) value.get("value"));
                    }
                }
            }
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } finally {
            DB.close(pstmt);
            DB.close(con);
        }
        return id;
    }

    public static void close(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
        }
    }

    public static void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
        }
    }

    public static void close(PreparedStatement pStmt) {
        try {
            if (pStmt != null) {
                pStmt.close();
            }
        } catch (Exception e) {
        }
    }

    public static void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
        }
    }

    public static void rollback(Connection con) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (Exception e) {
        }
    }

    public static JSONArray getResults(ResultSet rs) {
        JSONArray jsonResultArr = new JSONArray();
        try {
            if (rs != null) {
                while (rs.next()) {
                    JSONObject json = getJSON(rs);
                    jsonResultArr.add(json);
                }
            }
        } catch (SQLException e) {
        }
        return jsonResultArr;
    }

    public static JSONObject getResult(ResultSet rs) {
        JSONObject jsonResult = new JSONObject();
        try {
            if (rs != null && rs.next()) {
                jsonResult = getJSON(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static JSONObject getResultsWithHeader(ResultSet rs) {
        JSONObject jsonResult = new JSONObject();
        try {
            if (rs != null) {
                ResultSetMetaData rsmd = rs.getMetaData();
                JSONArray header = new JSONArray();
                for (int i = 1, colCount = rsmd.getColumnCount(); i <= colCount; i++) {
                    header.add(rsmd.getColumnLabel(i));
                }
                JSONArray jsonResultArr = new JSONArray();
                while (rs.next()) {
                    JSONObject json = getJSON(rs);
                    jsonResultArr.add(json);
                }
                jsonResult.put("header", header);
                jsonResult.put("data", jsonResultArr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

//	public static boolean writeToExcel(ResultSet rs, OutputStream os) {
//		try {
//			WritableWorkbook wworkbook = Workbook.createWorkbook(os);
//			WritableSheet wsheet = wworkbook.createSheet("Sheet 1", 0);
//
//		    // Create cell font and format
//		    WritableFont cellFontTitle = new WritableFont(WritableFont.ARIAL, 14);
//		    cellFontTitle.setBoldStyle(WritableFont.BOLD);
//		    WritableCellFormat cellFormatTitle = new WritableCellFormat(cellFontTitle);
//		    cellFormatTitle.setBorder(Border.ALL, BorderLineStyle.THIN);
//		    cellFormatTitle.setAlignment(Alignment.CENTRE);
//		    cellFormatTitle.setWrap(false);
//
//			// Create cell font and format
//		    WritableFont cellFontHeader = new WritableFont(WritableFont.ARIAL, 10);
//		    cellFontHeader.setBoldStyle(WritableFont.BOLD);
//		    WritableCellFormat cellFormatHeader = new WritableCellFormat(cellFontHeader);
//		    cellFormatHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
//		    cellFormatHeader.setAlignment(Alignment.CENTRE);
//		    cellFormatHeader.setWrap(false);
//
//
//		    // Create cell font and format
//		    WritableFont cellFontNormal = new WritableFont(WritableFont.ARIAL, 10);
//		    WritableCellFormat cellFormatNormal = new WritableCellFormat(cellFontNormal);
//		    cellFormatNormal.setBorder(Border.ALL, BorderLineStyle.THIN);
//		    cellFormatNormal.setWrap(false);
//
//		    int row = 1;
//
//			if(rs != null) {
//				ResultSetMetaData rsmd = rs.getMetaData();
//				for(int i=1, colCount = rsmd.getColumnCount();i<=colCount;i++){
//					wsheet.addCell(new Label(i-1, row, rsmd.getColumnLabel(i), cellFormatHeader));
//				}
//				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//				SimpleDateFormat sdfTimeStamp = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
//				while(rs.next()){
//					row++;
//					for(int i=1, colCount = rsmd.getColumnCount();i<=colCount;i++) {
//
//						switch(rsmd.getColumnType(i)){
//							case Types.VARCHAR :
//							case Types.CHAR :{
//								wsheet.addCell(new Label(i-1, row, rs.getString(i), cellFormatNormal));
//								break;
//							}
//							case Types.TIMESTAMP:{
//								Timestamp timeStamp = rs.getTimestamp(i);
//								String sTimeStamp = "";
//								if(timeStamp != null){
//									sTimeStamp = sdfTimeStamp.format(new Date(timeStamp.getTime()));
//								}
//								wsheet.addCell(new Label(i-1, row, sTimeStamp, cellFormatNormal));
//								break;
//							}
//							case Types.DATE : {
//								Date date = rs.getDate(i);
//								String sDate = "";
//								if(date != null) {
//									sdf.format(date);
//								}
//								wsheet.addCell(new Label(i-1, row, sDate, cellFormatNormal));
//								break;
//							}
//							case Types.BIGINT :
//							case Types.NUMERIC :{
//								wsheet.addCell(new Number(i-1, row, rs.getLong(i), cellFormatNormal));
//								break;
//							}
//							case Types.INTEGER :{
//								wsheet.addCell(new Number(i-1, row, rs.getInt(i), cellFormatNormal));
//								break;
//							}
//
//							case Types.FLOAT :{
//								wsheet.addCell(new Number(i-1, row, rs.getFloat(i), cellFormatNormal));
//								break;
//							}
//							case Types.DECIMAL :{
//								wsheet.addCell(new Number(i-1, row, rs.getDouble(i), cellFormatNormal));
//								break;
//							}
//							case Types.DOUBLE :{
//								wsheet.addCell(new Number(i-1, row, rs.getDouble(i), cellFormatNormal));
//								break;
//							}
//							default : {
//								wsheet.addCell(new Label(i-1, row, rs.getString(i), cellFormatNormal));
//							}
//
//						}
//					}
//				}
//			}
//			wworkbook.write();
//		    wworkbook.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		} catch (WriteException e) {
//			e.printStackTrace();
//		}
//		return true;
//	}

    private static JSONObject getJSON(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        JSONObject json = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfTimeStamp = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        for (int index = 1, colCount = rsmd.getColumnCount(); index <= colCount; index++) {
            Object value = null;
            switch (rsmd.getColumnType(index)) {
                case Types.VARCHAR:
                case Types.CHAR: {
                    value = rs.getString(index);
                    if (value == null) value = "";
                }
                break;

                case Types.TIMESTAMP: {
                    Timestamp timeStamp = rs.getTimestamp(index);
                    String sTimeStamp = "";
                    if (timeStamp != null) {
                        sTimeStamp = sdfTimeStamp.format(new Date(timeStamp.getTime()));
                    }
                    value = sTimeStamp;
                }
                break;

                case Types.DATE: {
                    Date date = rs.getDate(index);
                    if (date != null) {
                        value = sdf.format(date);
                    }
                }
                break;

                case Types.BIGINT:
                case Types.NUMERIC: {
                    value = rs.getLong(index);
                }
                break;

                case Types.INTEGER: {
                    value = rs.getInt(index);
                }
                break;

                case Types.FLOAT: {
                    value = rs.getFloat(index);
                }
                break;

                case Types.DECIMAL: {
                    value = rs.getDouble(index);
                }
                break;

                case Types.DOUBLE: {
                    value = rs.getDouble(index);
                }
                break;

                default: {
                    value = rs.getString(index);
                    if (value == null) value = "";
                }
            }
            json.put(rsmd.getColumnLabel(index), value);
        }
        return json;
    }
}