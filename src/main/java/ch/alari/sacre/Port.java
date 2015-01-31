/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author onur
 * @param <T>
 */
public class Port<T extends Token> 
{
    private final String name;
    
    protected BlockingQueue<T> q = null;

    // added for replacing a component adaptation capability
    // (enables flushing of the component)
//    protected boolean blocked = false;

    // preprocessing element attached to the port.
//    protected final List<Hook<T>> hooks;

    // port's datatype (token type)
    //private Class<T> portDataType;

    public Port(String name)
    {
        this.name = name;
//        hooks = new ArrayList<Hook<T>>();
    }
        
    public void connect(BlockingQueue<T> q)
    {
        this.q = q;
    }
    
    /*public BlockingQueue<T> getQueue()
    {
        return q;
    }*/

    public boolean isConnected()
    {
        return q != null;
    }
    
    public void disconnect()
    {
        q = null;
    }
    
    public String getName()
    {
        return name;
    }
    
//    public void setBlocked(boolean blocked)
//    {
//        this.blocked = blocked;
//    }

//    public void addHook(Hook<T> h)
//    {
//        hooks.add(h);
//    }
//
//    public void removeHook(Hook<T> h)
//    {
//        hooks.remove(h);
//    }

//    public BlockingQueue<T> getQueue()
//    {
//        return q;
//    }
}
