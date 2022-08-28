package com.dvarasolutions.lpserver.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;


public class Config {
	private static Properties props;
	//static Logger log = Logger.getLogger(Config.class.getName());
	private static Logger log=Logger.getLogger(Config.class);
	public static void load(ServletContext ctx) {
		if(props == null){
		    Properties prop = new Properties();
		    try {
		    	prop.load(ctx.getResourceAsStream("/WEB-INF/lpserver.properties"));
		   	} catch (IOException ex) {
		   		//ex.printStackTrace();
		   		log.error("",ex);
		    }
		   	props = prop;
		   	log.info("propertyName"+props);
		}
	}
	
	public static void load(String propertyFilePath) {
		if(props == null){
		    Properties prop = new Properties();
		    try {
		    	prop.load(new FileInputStream(propertyFilePath));
		   	} catch (IOException ex) {
		   		//ex.printStackTrace();
		   		log.error("",ex);
		    }
		   	props = prop;		   	
		}
	}

	public static String get(String propertyName){
		return props.getProperty(propertyName);
	}
}
