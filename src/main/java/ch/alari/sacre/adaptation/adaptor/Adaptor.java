/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre.adaptation.adaptor;

import ch.alari.sacre.Component;
import ch.alari.sacre.Event;
import ch.alari.sacre.Pipeline;
import ch.alari.sacre.Port;
import ch.alari.sacre.SacreLib;
import ch.alari.sacre.casestudy.case1.comps.Case1ComponentFactory;
import java.util.logging.Level;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class Adaptor
{
    private Pipeline p;

    public Adaptor(Pipeline p)
    {
        this.p = p;
    }

    public void replace(String compName, String newCompName, String newCompType)
    {
        Component c = p.getComponent(compName);

        if( c == null)
        {
            SacreLib.logger.warning("Such a component with name " + compName + " doesn't exist in the pipeline!");
            // TODO: return false or throw exception to indicate failure.
            return;
        }

        // flush the component to be replaced by sending a flush command
        try
        {
            SacreLib.logger.fine("sending FLUSH event to " + c.getName());
            c.sendEvent( Enum.valueOf(Event.class, "FLUSH") );
        }
        catch (InterruptedException ex)
        {
            SacreLib.logger.log(Level.SEVERE, null, ex);
            return;
        }

        // TODO: flushed'dan hemen sonra component stopped oluyor. hook'un etkisi gecici cunku hemen altinda state=stopped diyorum.
        // wait until Component is flushed
        while(c.getState() != Component.State.FLUSHED 
                && c.getState() != Component.State.STOPPED) // by the time FLUSH event is handled, component thread may already be finished.
        {
            SacreLib.logger.fine("waiting for " + c.getName() + " to be FLUSHed.");
            Thread.yield();
        }

        // yukardan ancak stopped ile geciyor su anda. o yuzden bunlar commentli.
        //if(c.getState() == Component.State.STOPPED)
        //{
        //    SacreLib.logger.fine("Component " + c.getName() + " has already finished processing. No need for replacement!");
            // TODO: report false or sth.
        //    return;
        //}
        //else
            SacreLib.logger.fine(c.getName() + " is FLUSHed.");

        // unlink the component, link the new component
        Component cNew = Case1ComponentFactory.instance().create(newCompType, newCompName);
        
        //  - connect all queues of the ports of the component to the ports of the new component
        for(Port port: c.getPorts() ) // getPorts is iterable in partial order of input and output ports
        {
            if(port.getDirType() == Port.DIR_TYPE_IN)
                cNew.nextPortToConnect(Port.DIR_TYPE_IN).connect( port.getQueue() );
            else // output ports
                cNew.nextPortToConnect(Port.DIR_TYPE_OUT).connect( port.getQueue() );
            System.out.println("Contents of the queue before " + c.getName() + ".port(" + port.getName() + ")");
            for( Object o: port.getQueue().toArray() )
                System.out.println(o);
        }
        // update pipeline's hashtable
        p.removeComponent(compName);
        p.addComponent(cNew);
        SacreLib.logger.fine(cNew.getName() + " replaced " + c.getName());

        // run the new component
        SacreLib.logger.fine("trying to run " + cNew.getName() + " thread");
        p.runComponent(cNew);
        SacreLib.logger.fine(cNew.getName() + " is run.");

        /*
        // block all input queues
        // send a STOP token to all input ports
        for(Port p: c.inputPorts())
        {
            p.getQueue().block();
            p.put(new Token(Token.STOP));
        }
        // don't allow STOP token to go on to output queues
        // unlink the component, link the new component
        // unblock the input queues
         */

    }
}
