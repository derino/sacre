/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            h.setLevel(Level.FINE); // TODO: when being debugged LEVEL.FINE, when released LEVEL.WARNING
        }
        catch (Exception e)
        {
          System.out.println("Logger initialization failed. Exception thrown: " + e);
          e.printStackTrace();
        }
    }
    
    private static int apiSinkUniqueKeyCounter = 0;
            
    public static Map<Integer, List<ApiSinkListener>> mapApiSinkListeners = new HashMap<Integer, List<ApiSinkListener>>();
    
    //private static final Map<String, String> specialWordsMap = new HashMap<String, String>();
    private static final String[][] specialWords = {
        {"%%AMPERSANDAMPSEMICOLON%%", "&amp;"}, // bu ustte
        {"%%AMPERSAND%%", "&"}, // bu altta
        {"%%EXCLAMATION%%", "!"},
        {"%%EQUALS%%", "="},
        {"%%LEFT_PAREN%%", "["},
        {"%%RIGHT_PAREN%%", "]"},
        {"%%COMMA%%", ","}};
    
    // in order to execute pipelines such as
    // başlıkknk [başlık=d&r] ! başlıkgirdileri [limit=1] ! apisink
    // d&r parameter value should be escaped by passing it to this function beforehand. later on inside parse(), it is unescaped.
    // result d%%AMPERSAND%%r
    public static String escapePipelineString(String str) 
    {
        for(String[] sw: specialWords)
        {
            str = str.replace(sw[1], sw[0]);
        }
        return str;
    }

    public static String unescapePipelineString(String str) 
    {
        for(String[] sw: specialWords)
        {
            str = str.replace(sw[0], sw[1]);
        }
        return str;
    }
    
    public static synchronized int addApiSinkListener(ApiSinkListener apl)
    {
        addApiSinkListener(apiSinkUniqueKeyCounter++, apl);
        return apiSinkUniqueKeyCounter-1;
    }
    
    public static synchronized void addApiSinkListener(int apiSinkUniqueKey, ApiSinkListener apl)
    {
        Integer apiSinkUniqueKeyInteger = new Integer(apiSinkUniqueKey);
        List<ApiSinkListener> apls = mapApiSinkListeners.get(apiSinkUniqueKeyInteger);
        if(apls == null)
        {
            apls = new ArrayList<ApiSinkListener>();
        }
        apls.add(apl);
        mapApiSinkListeners.put(apiSinkUniqueKeyInteger, apls);
    }
    
    public static synchronized List<ApiSinkListener> getApiSinkListeners(int apiSinkUniqueKey)
    {
        return mapApiSinkListeners.get(new Integer(apiSinkUniqueKey));
    }
    
    public static synchronized void clearApiSinkListeners(int apiSinkUniqueKey)
    {
        getApiSinkListeners(apiSinkUniqueKey).clear();
    }
}
