/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

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

    public T take() throws InterruptedException
    {
        T t; // T to be returned
        t = q.take();
        
        if(t.isStop())
        {
            this.stopped = true;
            component.reachedEndOfStream(this); // normally throws InterruptedException
            return null; // normally never reached
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
}
