package in.tsiconsulting.accelerator.system.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletContext;


public class Config {
    private static Properties appConfig;
    private static Properties schemaConfig;
    private static Properties processorConfig;

    public static void load(ServletContext ctx) {
        if (appConfig == null) {
            appConfig = new Properties();
            try {
                appConfig.load(ctx.getResourceAsStream("/WEB-INF/_accelerator.tsi"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Loaded "+ appConfig);
        }

        if (schemaConfig == null) {
            schemaConfig = new Properties();
            try {
                schemaConfig.load(ctx.getResourceAsStream("/WEB-INF/_schema.tsi"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Loaded "+ schemaConfig);
        }

        if (processorConfig == null) {
            processorConfig = new Properties();
            try {
                processorConfig.load(ctx.getResourceAsStream("/WEB-INF/_processor.tsi"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Loaded "+ processorConfig);
        }
    }

    public static String getAppConfig(String propertyName) {
        return appConfig.getProperty(propertyName);
    }
    public static String getSchema(String propertyName) {
        return schemaConfig.getProperty(propertyName);
    }
    public static String getProcessor(String propertyName) {
        return processorConfig.getProperty(propertyName);
    }

    public static Properties getProcessorConfig(){
        return processorConfig;
    }
}
