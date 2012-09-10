/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class SacreLib
{
    public static Logger logger = Logger.getLogger("sacre");

    static
    {
        // Initialize logger
        try
        {
          LogManager lm = LogManager.getLogManager();

          lm.addLogger(logger);
          logger.setLevel(Level.ALL);

        // level of log messages on console = >INFO
        for(Handler h: Logger.getLogger("").getHandlers())
            h.setLevel(Level.WARNING); // TODO: when being debugged LEVEL.FINE, when released LEVEL.WARNING
        }
        catch (Exception e)
        {
          System.out.println("Logger initialization failed. Exception thrown: " + e);
          e.printStackTrace();
        }
    }
}
