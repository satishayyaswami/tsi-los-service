package in.tsiconsulting.accelerator.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface REST {

    String NONE = "none";
    String CSV_OUTPUT = "csv";
    String JSON_OUTPUT = "json";
    String XML_OUTPUT = "xml";
    String DELIMITER = ".";

    void get(HttpServletRequest req, HttpServletResponse res);

    void post(HttpServletRequest req, HttpServletResponse res);

    void delete(HttpServletRequest req, HttpServletResponse res);

    void put(HttpServletRequest req, HttpServletResponse res);
}
