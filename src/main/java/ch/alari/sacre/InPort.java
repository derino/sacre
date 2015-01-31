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
    public InPort(Component c)
    {
        super(c.getName()+".inport"+c.uniqueInPortID++);
        c.addInPort(this);
    }

    public T take() throws InterruptedException
    {
        T t; // T to be returned
        t = q.take();

//        boolean tokenAllowedByHooks = true;
//        for(Hook<T> h: hooks)
//        {
//            tokenAllowedByHooks &= h.newToken(t);
//        }
//        if(tokenAllowedByHooks)
            return t;
//        else
//            return null;
    }
}
