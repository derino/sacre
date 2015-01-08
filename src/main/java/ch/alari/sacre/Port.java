/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

/**
 *
 * @author onur
 */
public class Port<T> 
{
    public static final int DIR_TYPE_IN = 0;
    public static final int DIR_TYPE_OUT = 1;
    
    private String name;
    private int dirType;
    
    private BlockingQueue<T> q = null;

    // added for replacing a component adaptation capability
    // (enables flushing of the component)
    private boolean blocked = false;

    // preprocessing element attached to the port.
    private List<Hook<T>> hooks;

    // port's datatype (token type)
    private Class<T> portDataType;

    public Port(Class<T> portDataType, String name, int dirType)
    {
        this.name = name;
        this.dirType = dirType;
        this.hooks = new ArrayList<Hook<T>>();
        this.portDataType = portDataType;
    }
    
    public void connect(BlockingQueue<T> q)
    {
        this.q = q;
    }
    
    /*public BlockingQueue<T> getQueue()
    {
        return q;
    }*/

    public T take() throws InterruptedException
    {
        // TODO: warn if dir_type_out and take called

        T t; // T to be returned

        if(!blocked)
            t = q.take();
        else
        {
            t = createStopToken();
            /*
            // not nice! Assumption: if task() reads a null it means STOP/EOS.
            Token tok = (Token)q.peek();
            if(tok != null)
                tok.setType(Token.STOP);
            t = (T)tok;
             */
        }

        boolean tokenAllowedByHooks = true;
        for(Hook<T> h: hooks)
        {
            tokenAllowedByHooks &= h.newToken(t);
        }
        if(tokenAllowedByHooks)
            return t;
        else
            return null;
    }

    public void put(T token) throws InterruptedException
    {
        // TODO: warn if dir_type_in and put called

        boolean tokenAllowedByHooks = true;
        for(Hook<T> h: hooks)
        {
            tokenAllowedByHooks &= h.newToken(token);
        }
        if(tokenAllowedByHooks)
            q.put(token);
    }


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
    
    public int getDirType()
    {
        return dirType;
    }

    public void setBlocked(boolean blocked)
    {
        this.blocked = blocked;
    }

    public void addHook(Hook<T> h)
    {
        hooks.add(h);
    }

    public void removeHook(Hook<T> h)
    {
        hooks.remove(h);
    }

    public BlockingQueue<T> getQueue()
    {
        return q;
    }

    /**
     * @return the portDataType
     */
    public Class<T> getPortDataType()
    {
        return portDataType;
    }

    private T createStopToken()
    {
        T createdToken = null;

        Class[] intArgsClass = new Class[] { int.class };
        Integer tokenType = new Integer(Token.STOP);
        Object[] intArgs = new Object[] { tokenType };

        Constructor intArgsConstructor;
        try
        {
            intArgsConstructor = portDataType.getConstructor(intArgsClass);

            Object object = null;

            try {
              object = intArgsConstructor.newInstance(intArgs);
            } catch (InstantiationException e) {
                SacreLib.logger.log(Level.SEVERE, "couldn't create a STOP token.", e);
            } catch (IllegalAccessException e) {
                SacreLib.logger.log(Level.SEVERE, "couldn't create a STOP token.", e);
            } catch (IllegalArgumentException e) {
                SacreLib.logger.log(Level.SEVERE, "couldn't create a STOP token.", e);
            } catch (InvocationTargetException e) {
                SacreLib.logger.log(Level.SEVERE, "couldn't create a STOP token.", e);
            }

            createdToken = (T) object;
            
        } catch (NoSuchMethodException e) {
            System.out.println(e);
        }

        return createdToken;
  }

}
