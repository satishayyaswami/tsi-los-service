package in.tsiconsulting.accelerator.system.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TSIController implements Filter {

    private static final String URL_DELIMITER = "/";
    private static final String TSI_ACCELERATOR_FRAMEWORK = "tsi";

    private static final HashMap<String, String> filterConfig = new HashMap<String, String>();
    @Override
    public void destroy() {
        // Any cleanup of resources
    }

    static {
        //log.info("Logger inits");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String responseJson = "";
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String method = req.getMethod();
        String servletPath = req.getServletPath();
        String uri = req.getRequestURI();
        String classname = null;
        String operation = null;
        Properties apiRegistry = null;
        boolean validrequest = true;

        // set response header
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");


        apiRegistry = SystemConfig.getProcessorConfig();
        if (apiRegistry.containsKey(servletPath.trim())) {
            StringTokenizer strTok = new StringTokenizer(servletPath, URL_DELIMITER);
            String framework = strTok.nextToken();
            if (!framework.equalsIgnoreCase(TSI_ACCELERATOR_FRAMEWORK)) {
                res.sendError(400);
                return;
            }

            // Check
             try {
                 if(!servletPath.contains("tsi/system")) {
                     InputProcessor.processHeader(req, res);
                 }
                InputProcessor.processInput(req,res);

                operation = strTok.nextToken();
                classname = apiRegistry.getProperty(servletPath.trim());
                //System.out.println("operation:" + operation + " classname:" + classname);
                if (classname == null || method == null) res.sendError(400);

                REST action = ((REST) Class.forName(classname).getConstructor().newInstance());
                validrequest = action.validate(method,req,res);
                if(validrequest) {
                    if (method.equalsIgnoreCase("GET")) {
                        res.setContentType("application/json");
                        action.get(req, res);
                    } else if (method.equalsIgnoreCase("POST")) {
                        res.setContentType("application/json");
                        action.post(req, res);
                    } else if (method.equalsIgnoreCase("PUT")) {
                        res.setContentType("application/json");
                        action.put(req, res);
                    } else if (method.equalsIgnoreCase("DELETE")) {
                        res.setContentType("application/json");
                        action.delete(req, res);
                    } else {
                        res.sendError(400);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.sendError(400);
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        SystemConfig.load(filterConfig.getServletContext());
        JSONSchemaValidator.createInstance(filterConfig.getServletContext());
    }
}
