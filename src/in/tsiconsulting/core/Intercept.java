package com.dvarasolutions.lpserver.core;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dvarasolutions.lpserver.util.OutputProcessor;
import org.apache.log4j.Logger;

import com.dvarasolutions.lpserver.util.Config;
import com.dvarasolutions.lpserver.util.InputProcessor;
import org.json.simple.JSONObject;

public class Intercept implements Filter {
	
	private static Logger log=Logger.getLogger(Intercept.class);
	
	private static String URL_DELIMITER 	= "/";
	private static String LIQUIDITY_PLATFORM_FRAMEWORK 	= "lps";
	private static String ADMIN_OPERATION   = "adm";
	private static String API_OPERATION = "api";


	private static HashMap<String, String> handlers = new HashMap<String, String>();
	private static HashMap<String, String> processors = new HashMap<String, String>();
	private static HashMap<String, Integer> apiRegistry = new HashMap<String, Integer>();	
	@Override
	public void destroy() {
		// Any cleanup of resources
	}
	
	static{
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
		
		// set response header
		res.setHeader("Access-Control-Allow-Origin", "*");	
		res.setCharacterEncoding("UTF-8");
		
		if(handlers.containsKey(servletPath.trim())){
			StringTokenizer strTok = new StringTokenizer(servletPath,URL_DELIMITER);
			if(strTok.countTokens() != 3){
				res.sendError(400);
				return;
			}
			String framework = strTok.nextToken();
			if(!framework.equalsIgnoreCase(LIQUIDITY_PLATFORM_FRAMEWORK)){
				res.sendError(400);
				return;
			}
			
			// Check
			InputProcessor.processInput(req, res);
			String operation = strTok.nextToken();
			String classname = (String) handlers.get(servletPath.trim());
			System.out.println("operation:"+operation+" classname:"+classname);
			if(classname == null || method == null) res.sendError(400);
			try{
				if(operation.equalsIgnoreCase(ADMIN_OPERATION)) {
					// Add admin related security
				}else if(operation.equalsIgnoreCase(API_OPERATION)) {
					// Add API security validation
				}else{
					res.sendError(400);return;
				}
				REST action = ((REST)Class.forName(classname).newInstance());
				if(method.equalsIgnoreCase("GET")){
					res.setContentType("application/json");
					action.get(req, res);
					OutputProcessor.send(res,req.getAttribute(InputProcessor.OUTPUT_DATA));
				}else if(method.equalsIgnoreCase("POST")){
					res.setContentType("application/json");
					action.post(req, res);
					OutputProcessor.send(res,req.getAttribute(InputProcessor.OUTPUT_DATA));
				}else if(method.equalsIgnoreCase("PUT")){
					res.setContentType("application/json");
					action.put(req, res);
					OutputProcessor.send(res,req.getAttribute(InputProcessor.OUTPUT_DATA));
				}else if(method.equalsIgnoreCase("DELETE")){
					res.setContentType("application/json");
					action.delete(req, res);
					OutputProcessor.send(res,req.getAttribute(InputProcessor.OUTPUT_DATA));
				}else{
					res.sendError(400);return;
				}
			}catch(Exception e){
				log.error("",e);
				e.printStackTrace();
				res.sendError(400);return;
			}
		}
		}


	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String name = null;
		String value = null;
		Config.load(filterConfig.getServletContext()); // load lpserver.properties
		Enumeration en = filterConfig.getInitParameterNames();
		while(en.hasMoreElements()){
			name = (String) en.nextElement();
			value = filterConfig.getInitParameter(name);
			log.info("Registering "+name+" - "+value);
			handlers.put(name, value);
		}		
		
		// Admin Interface
		handlers.put("/lps/adm/originator", "com.dvarasolutions.lpserver.admin.Originator");
		handlers.put("/lps/adm/lender", "com.dvarasolutions.lpserver.admin.Lender");
		handlers.put("/lps/adm/datatemplate", "com.dvarasolutions.lpserver.admin.DataTemplate");
		handlers.put("/lps/adm/relationship", "com.dvarasolutions.lpserver.admin.Relationship");
		handlers.put("/lps/adm/deal", "com.dvarasolutions.lpserver.admin.Deal");

		// Public API
		handlers.put("/lps/api/clmloan","com.dvarasolutions.lpserver.api.CLMLoan");
		handlers.put("/lps/api/clmrepayment","com.dvarasolutions.lpserver.api.CLMRepayment");
	}
}
