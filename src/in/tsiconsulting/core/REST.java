package com.dvarasolutions.lpserver.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface REST {

    public String NONE = "none";
    public String CSV_OUTPUT = "csv";
    public String JSON_OUTPUT = "json";
    public String XML_OUTPUT = "xml";
    public String DELIMITER = ".";

    void get(HttpServletRequest req, HttpServletResponse res);
    void post(HttpServletRequest req, HttpServletResponse res);
    void delete(HttpServletRequest req, HttpServletResponse res);
    void put(HttpServletRequest req, HttpServletResponse res);
}
