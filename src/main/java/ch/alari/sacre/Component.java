/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

/**
 *
 * @author onur
 */
public abstract class Component implements Callable<Object> 
{
    // instance name of the component
    private String name;

    // set by subclass constructor. I may enforce a way to check at compile-time that it is done
    protected String type;

    // subclasses should initialize (if needed) from given params in their constructor.
    protected Map<String, Object> params;
    
    // subclasses should set the port list of the component via addPort()
    private List<Port<? extends Token>> ports;
    
    // if name parameter is not given at pipeline construction time, a component instance automatically gets 
    // Type+uniqueInstanceID as its name.
    private static int uniqueInstanceID = 1;

    // event queue: receives event like reconfigure, flush
    protected LinkedBlockingQueue<Event> eventQueue;

    // execution state of the component
    protected State state;
    public enum State {NOT_STARTED, RUNNING, STOPPED, FLUSHED};

    // The sink components can return an object as a result of the execution of the whole pipeline.
    // This is helpful in order to use the sacre functionality as api-like functions.
    protected Object result;
    
    public Component(String name)
    {
        this.name = name;
        params = new HashMap<String, Object>();
        ports = new ArrayList<Port<? extends Token>>();
        eventQueue = new LinkedBlockingQueue<Event>();
        state = State.NOT_STARTED;
    }

    /**
     *
     * @param <T>
     * @param portName
     * @return Port object with specified name
     */
    protected <T> Port<T> port(String portName)
    {
        for(Port<? extends Token> p: ports)
            if(p.getName().equals(portName))
                return (Port<T>) p;
        return null; // shouldn't happen
    }
    
    public String getName()
    {
        return name;
    }
 
    protected void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    /**
     * used by Pipeline.parse(). There is an assumption in the pipeline
     * syntax that for ex. in A ! B,C B is connected to the first and C to
     * the second port of A.
     * @param dirType: given directional type
     * @return the next unconnected port of given directional type
     */
    public Port nextPortToConnect(int dirType)
    {
        for(Port p: ports)
        {
            if(!p.isConnected() && p.getDirType() == dirType)
                return p;
        }
        return null; // shouldn't happen
    }
    
    @Override
    public String toString()
    {
        StringBuilder cStr = new StringBuilder();
        cStr.append("Component: name=")
                .append(name)
                .append(", type=")
                .append(type)
                .append("\n");
        if(params != null)
            cStr.append("params: ")
                    .append(params);
        return cStr.toString();
    }
    
    public static String getUniqueInstanceID()
    {
        return "_instance" + uniqueInstanceID++;
    }

    public Object call()
    {
        state = State.RUNNING;
        
        while(state == State.RUNNING)
        {
            // handle all the events in the eventQueue
            Event e = eventQueue.poll();
            while( e != null)
            {
                handleEvent(e);
                e = eventQueue.poll();
            }

            // execute user task
            try
            {
                task();
                if(Thread.interrupted())
                    throw new InterruptedException();
            }
            catch(InterruptedException ie)
            {
                SacreLib.logger.fine(type + "(instance name:" + name + ") thread interrupted.");
                state = State.STOPPED; // not necessarily needed.
                return null;
            }
            catch(Exception ex)
            {
                SacreLib.logger.log(Level.WARNING, "Exception in " + type + "(instance name:" + name + ") thread", ex);
            }
        }
        SacreLib.logger.fine(type + "(instance name:" + name + ") thread finished.");
        return result;
    }

    public abstract void task() throws InterruptedException, Exception;

    private void handleEvent(Event e)
    {
        SacreLib.logger.fine("Component " + name + " is handling event " + e);
        if(e == Event.FLUSH)
        {
            // block all input ports
            for(Port p: ports)
            {
                if(p.getDirType() == Port.DIR_TYPE_IN)
                    p.setBlocked(true);
                else // output ports
                {
                    p.addHook(new Hook()
                                {
                                    public boolean newToken(Object t)
                                    {
                                        if( ((Token)t).isStop() )
                                        {
                                            componentFlushed();
                                            return false;
                                        }
                                        else
                                            return true;
                                    }
                                });
                }
            }
        }
    }

    public void sendEvent(Event e) throws InterruptedException
    {
        eventQueue.put(e);
    }

    protected void componentFlushed()
    {
        state = State.FLUSHED;
    }

    public State getState()
    {
        return state;
    }

    /**
     * The list should be iteratable in the same order as nextPortToConnect().
     * @param <T>
     * @param dirType
     * @return port list
     */
    public List<Port<? extends Token>> getPorts()
    {
        return Collections.unmodifiableList(ports);
        //return ports;
    }

    /**
     * subclasses of Component should use this method to define their ports.
     * @param p
     */
    protected void addPort(Port<? extends Token> p)
    {
        ports.add(p);

        // I'm not sure TODO: in order to remove from task() body the setting of the state to STOPPED
        // after sending a STOP token, for each output port, we add a hook which sets
        // state to STOPPED. not so nice when there are multiple output ports.
        // what if a STOP token on one port doesn't imply state to be STOPPED!
    }
    
}
