package in.tsiconsulting.accelerator.system.core;

import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletContext;


public class SystemConfig {
    private static Properties appConfig;
    private static Properties mschemaConfig;

    private static Properties tschemaConfig;
    private static Properties processorConfig;

    public static void load(ServletContext ctx) {
        if (appConfig == null) {
            appConfig = new Properties();
            try {
                appConfig.load(ctx.getResourceAsStream("/WEB-INF/_admin.tsi"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Loaded _admin.tsi");
        }

        if (mschemaConfig == null) {
            mschemaConfig = new Properties();
            try {
                mschemaConfig.load(ctx.getResourceAsStream("/WEB-INF/_mschema.tsi"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Loaded _mschema.tsi");
        }

        if (tschemaConfig == null) {
            tschemaConfig = new Properties();
            try {
                tschemaConfig.load(ctx.getResourceAsStream("/WEB-INF/_tschema.tsi"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Loaded _tschema.tsi");
        }

        if (processorConfig == null) {
            processorConfig = new Properties();
            try {
                processorConfig.load(ctx.getResourceAsStream("/WEB-INF/_processor.tsi"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Loaded _processor.tsi");
        }
    }

    public static Properties getAppConfig() {
        return appConfig;
    }
    public static Properties getMasterSchema() { return mschemaConfig;}

    public static Properties getTenantSchema() { return tschemaConfig;}

    public static Properties getProcessorConfig(){
        return processorConfig;
    }
}
