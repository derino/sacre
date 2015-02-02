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
    
    public void connect(InPort<T> in)
    {
        if( isConnected() )
        {
            SacreLib.logger.log(Level.SEVERE, "attempting to reconnect already connected port: " + getName() + "(with " + in.getName() + ")");
        }
        q = new LinkedBlockingQueue<T>();
        in.connect(q);
        connect(q);
    }

}