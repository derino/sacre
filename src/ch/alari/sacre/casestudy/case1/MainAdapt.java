/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre.casestudy.case1;

import ch.alari.sacre.HighPriorityThreadFactory;
import ch.alari.sacre.Pipeline;
import ch.alari.sacre.SacreLib;
import ch.alari.sacre.adaptation.controller.Controller;
import ch.alari.sacre.casestudy.case1.comps.Case1ComponentFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class MainAdapt {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        String pStr = "FrameSrc [data=framedata, num=2000] ! Encoder ! ConsoleSink";
        SacreLib.logger.fine("run pipeline:" + pStr);

        ExecutorService e = Executors.newCachedThreadPool();

        // start pipeline thread
        Pipeline pipeline = new Pipeline(Case1ComponentFactory.instance());
        pipeline.parse(pStr);
        Future<?> futurePipeline = e.submit(pipeline);


        // start controller thread
        Controller controller = new Controller(pipeline);
        //ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        //ScheduledFuture<?> sf = ses.scheduleAtFixedRate(controller, 500, 50, TimeUnit.MILLISECONDS);
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(HighPriorityThreadFactory.instance());
        ScheduledFuture<?> sf = ses.schedule(controller, 150, TimeUnit.MILLISECONDS);

        try
        {
            if( futurePipeline.get() == null)
            {
                SacreLib.logger.fine("Pipeline executed successfully!");
            }

            if( sf.isDone() )
                SacreLib.logger.fine("Controller executed successfully!");

        }
        catch(ExecutionException ee)
        {
            SacreLib.logger.log(Level.WARNING, "Exception occurred in Pipeline!", ee);
        }
        catch(InterruptedException ie)
        {
            SacreLib.logger.log(Level.WARNING, "Exception occurred in Pipeline!", ie);
        }

        e.shutdown(); // without this, although main() finishes, it doesn't exit.
        ses.shutdown(); // without this, although main() finishes, it doesn't exit.
    }

}
