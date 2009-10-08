/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre.casestudy.case1;

import ch.alari.sacre.Pipeline;
import ch.alari.sacre.SacreLib;
import ch.alari.sacre.casestudy.case1.comps.Case1ComponentFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        String pStr = "FrameSrc [data=framedata, num=20] ! Encoder ! ConsoleSink";
        SacreLib.logger.fine("run pipeline:" + pStr);


        ExecutorService e = Executors.newCachedThreadPool();
        Pipeline pipeline = new Pipeline(Case1ComponentFactory.instance());
        pipeline.parse(pStr);
        Future<?> futurePipeline = e.submit(pipeline);
        e.shutdown();

        try
        {
            if( futurePipeline.get() == null)
            {
                SacreLib.logger.fine("Pipeline executed successfully!");
            }
        }
        catch(ExecutionException ee)
        {
            SacreLib.logger.log(Level.WARNING, "Exception occurred in Pipeline!", ee);
        }
        catch(InterruptedException ie)
        {
            SacreLib.logger.log(Level.WARNING, "Exception occurred in Pipeline!", ie);
        }
    }

}
