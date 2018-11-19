package utils;

import java.io.IOException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Context implements ServletContextListener {

  /** Denne metode kaldes, når context starter*/
  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {

    // We init config in order to read the file and set all the variables.
    try {
      Config.initializeConfig();
    } catch (IOException e) {

      System.out.println("Can't read config");
      Log.writeLog(this.getClass().getName(), this, "We are now including the config file", 2);

      e.printStackTrace();
    }

    // Here we can initialize our Logger class and write to our Logging.txt that the system has been
    System.out.println("Context is open");

    // Skriv til log at vi starter systemet
    Log.writeLog(this.getClass().getName(), this, "We've started the system", 2);
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {

    // Logging for when the system is stopped
    System.out.println("Context is closed");

    // Skriv til log at vi lukker systemet
    Log.writeLog(this.getClass().getName(), this, "The system has been stopped", 2);
  }
}
