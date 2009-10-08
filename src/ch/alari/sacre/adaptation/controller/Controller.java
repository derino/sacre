/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre.adaptation.controller;

import ch.alari.sacre.Pipeline;
import ch.alari.sacre.SacreLib;
import ch.alari.sacre.adaptation.adaptor.Adaptor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class Controller implements Runnable
{
    private Pipeline p;

    private Adaptor adaptor;
    /**
     *
     * @param p - pipeline to be controlled
     */
    public Controller(Pipeline p)
    {
        this.p = p;
        adaptor = new Adaptor(p);
    }

    public void run()
    {
        //while(p.getState() == Pipeline.State.RUNNING)
        //{
        
        if( p.getState() == Pipeline.State.RUNNING )
        {
            SacreLib.logger.fine("controller activated.");
            // monitor

            // control and adapt.
            adaptor.replace("Encoder_instance2", "NewEncoder_instance", "Encoder2"); // unfortunate that instance name is same as instance type of new component.
        }
        else
            SacreLib.logger.fine("controller not activated. Can't run if pipeline is not running.");

            /*try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException ex)
            {
                SacreLib.logger.log(Level.SEVERE, null, ex);
            }*/
        //}

    }

}
