/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

/**
 *
 * @author onur
 * @param <T>
 */
public class InPort<T extends Token> extends Port<T>
{
    /**
     * the component that this port belongs to.
     */
    private Component component;
    
    private boolean stopped;
    
    public InPort(Component c)
    {
        super(c.getName()+".inport"+c.uniqueInPortID++);
        this.component = c;
        this.component.addInPort(this);
        this.stopped = false;
    }

    /**
     * 
     * @return the next token in the stream, null if it is a STOP_TOKEN
     * @throws InterruptedException 
     */
    public T take() throws InterruptedException
    {
        if(isStopped()) // this check is VERY IMPORTANT (had a problem with MergeNx1).
            return null;
        
        T t; // T to be returned
        t = q.take();
        
        if(t.isStop())
        {
            this.stopped = true;
            component.reachedEndOfStream(this); // for components with a single input port, normally calls stopAndExit()
            return null; // never reached if single input port
        }
        else 
            return t;

//        boolean tokenAllowedByHooks = true;
//        for(Hook<T> h: hooks)
//        {
//            tokenAllowedByHooks &= h.newToken(t);
//        }
//        if(tokenAllowedByHooks)
//            return t;
//        else
//            return null;
    }
    
    public boolean isStopped()
    {
        return stopped;
    }
    
    // InPort and OutPort have a slight difference in connect().
    // This is to enforce type safety. InPort of a supertype can be fed by an OutPort of a subtype.
    public void connect(OutPort<? extends T> out) //T
    {
        if( isConnected() )
        {
            SacreLib.logger.log(Level.SEVERE, "attempting to reconnect already connected port: " + getName() + "(with " + out.getName() + ")");
        }
        q = new LinkedBlockingQueue<T>();
        out.connect(q);
        connect(q);
    }    
    
    public void connect(BlockingQueue<? extends T> q) //T
    {
        this.q = (BlockingQueue<T>)q;
    }

}
