package in.tsiconsulting.accelerator.los.events;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class EventListener implements ServletContextListener {

    EventProcessor ep = null;

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        ep.stop();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ep = new EventProcessor();
        ep.start();
    }
}
