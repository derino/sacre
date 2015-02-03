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
public class OutPort<T extends Token> extends Port<T>
{
    public OutPort(Component c)
    {
        super(c.getName()+".outport"+c.uniqueOutPortID++);
        c.addOutPort(this);
    }

    public void put(T token) throws InterruptedException
    {
//        boolean tokenAllowedByHooks = true;
//        for(Hook<T> h: hooks)
//        {
//            tokenAllowedByHooks &= h.newToken(token);
//        }
//        if(tokenAllowedByHooks)
        q.put(token);
    }
    
    // InPort and OutPort have a slight difference in connect().
    // This is to enforce type safety. InPort of a supertype can be fed by an OutPort of a subtype.
    public void connect(InPort<? super T> in) //T
    {
        if( isConnected() )
        {
            SacreLib.logger.log(Level.SEVERE, "attempting to reconnect already connected port: {0}(with {1})", new Object[]{getName(), in.getName()});
        }
        q = new LinkedBlockingQueue<T>();
        in.connect(q);
        connect(q);
    }    
    
    public void connect(BlockingQueue<? super T> q) //T
    {
        this.q = (BlockingQueue<T>)q;
    }

}
