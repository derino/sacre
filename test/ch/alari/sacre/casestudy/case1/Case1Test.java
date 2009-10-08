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
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class Case1Test {

    public Case1Test() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of pipeline running
     */
    @Ignore
    @Test
    public void testExec()
    {
        String pStr = "FrameSrc [data=framedata, num=2000] ! Encoder ! ConsoleSink";
        SacreLib.logger.fine("run pipeline:" + pStr);


        ExecutorService e = Executors.newCachedThreadPool();
        Pipeline pipeline = new Pipeline(Case1ComponentFactory.instance());
        pipeline.parse(pStr);
        Future<?> futurePipeline = e.submit(pipeline);
        //e.shutdown();

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

    /**
     * Test of pipeline adapting
     */
    //@Ignore
    @Test
    public void testExecAdapt()
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
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(HighPriorityThreadFactory.instance());
        //ScheduledFuture<?> sf = ses.scheduleAtFixedRate(controller, 500, 50, TimeUnit.MILLISECONDS);
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
    }
}