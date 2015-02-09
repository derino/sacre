/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
        init(Level.WARNING); // TODO: when being debugged LEVEL.FINE, when released LEVEL.WARNING
//        // Initialize logger
//        try
//        {
//          LogManager lm = LogManager.getLogManager();
//
//          lm.addLogger(logger);
//          logger.setLevel(Level.ALL);
//
//        // level of log messages on console = >INFO
//        for(Handler h: Logger.getLogger("").getHandlers())
//            h.setLevel(Level.WARNING); // TODO: when being debugged LEVEL.FINE, when released LEVEL.WARNING
//        }
//        catch (Exception e)
//        {
//          System.out.println("Logger initialization failed. Exception thrown: " + e);
////          e.printStackTrace();
//        }
    }
    
    public static void init(Level debugLevel)
    {
        // Initialize logger
        try
        {
          LogManager lm = LogManager.getLogManager();

          lm.addLogger(logger);
          logger.setLevel(Level.ALL);

        // level of log messages on console = >INFO
        for(Handler h: Logger.getLogger("").getHandlers())
            h.setLevel(debugLevel); // TODO: when being debugged LEVEL.FINE, when released LEVEL.WARNING
        }
        catch (Exception e)
        {
          System.out.println("Logger initialization failed. Exception thrown: " + e);
//          e.printStackTrace();
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
        Integer apiSinkUniqueKeyInteger = apiSinkUniqueKey;
        List<ApiSinkListener> apls = mapApiSinkListeners.get(apiSinkUniqueKeyInteger);
        if(apls == null)
            apls = new ArrayList<>();
        apls.add(apl);
        mapApiSinkListeners.put(apiSinkUniqueKeyInteger, apls);
    }
    
    public static synchronized List<ApiSinkListener> getApiSinkListeners(int apiSinkUniqueKey)
    {
        return mapApiSinkListeners.get(apiSinkUniqueKey);
    }
    
    public static synchronized void clearApiSinkListeners(int apiSinkUniqueKey)
    {
        getApiSinkListeners(apiSinkUniqueKey).clear();
    }
    
    public static void addComponentFactory(ComponentFactory cf)
    {
        SacreComponentFactory.addComponentFactory(cf);
    }
    
    public static Object runPipeline(String pStr)
    {
        return runPipeline(pStr, -1);
    }
    
    // moved from EksiSozlukUtilities
    public static Object runPipeline(String pStr, int apiSinkUniqueKey)
    {
        SacreLib.logger.log(Level.FINE, "run pipeline:{0}", pStr);
        // String pStr = "BaslikSrc [baslik=tribundergi.com] ! Baslik2EntryCvt ! ConsoleSink";

        ExecutorService e = Executors.newCachedThreadPool();
        Pipeline pipeline = new Pipeline(/*EksiSozlukComponentFactory.instance(),*/ apiSinkUniqueKey);
        
        try 
        {
            pipeline.parse(pStr);
        } 
        catch (PipelineParseException ex) {
            SacreLib.logger.log(Level.WARNING, "Pipeline cannot be parsed!");
        }
        
        Future<?> futurePipeline = e.submit(pipeline);
        e.shutdown();

        Object res = null;
        try
        {
            res = futurePipeline.get();
            if(res != null)
                SacreLib.logger.fine("Pipeline executed successfully!");
//            else // bu sekilde bakmak dogru degildi
//                SacreLib.logger.warning("Pipeline did not produce any results!");
            /*if( futurePipeline.get() == null)
            {
                EksiSozlukUtilities.logger.fine("Pipeline executed successfully!");
            }*/
        }
        catch(ExecutionException | InterruptedException ee)
        {
            SacreLib.logger.log(Level.WARNING, "Exception occurred in Pipeline!", ee);
        }
        
        return res;
    }
}
