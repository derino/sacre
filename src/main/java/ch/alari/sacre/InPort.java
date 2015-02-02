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

    /**
     * 
     * @return the next token in the stream, null if it is a STOP_TOKEN
     * @throws InterruptedException 
     */
    public T take() throws InterruptedException
    {
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
}
