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
    
    // subclasses should set the port list of the component via addInPort()
    private List<InPort<? extends Token>> inPorts;
    
    // subclasses should set the port list of the component via addOutPort()
    private List<OutPort<? extends Token>> outPorts;
    
    // if name parameter is not given at pipeline construction time, a component instance automatically gets 
    // Type+uniqueInstanceID as its name.
    private static int uniqueInstanceID = 1;

    public int uniqueInPortID = 1;
    public int uniqueOutPortID = 1;
    
//    // event queue: receives event like reconfigure, flush
//    protected LinkedBlockingQueue<Event> eventQueue;

    // execution state of the component
    protected State state;
    public enum State {NOT_STARTED, RUNNING, STOPPED, FLUSHED};

    // The sink components can return an object as a result of the execution of the whole pipeline.
    // This is helpful in order to use the sacre functionality as api-like functions.
    protected Object result;

    
    public abstract void task() throws InterruptedException, Exception;

    public Component(String name)
    {
        this.name = name;
        params = new HashMap<String, Object>();
        inPorts = new ArrayList<InPort<? extends Token>>();
        outPorts = new ArrayList<OutPort<? extends Token>>();
//        eventQueue = new LinkedBlockingQueue<Event>();
        state = State.NOT_STARTED;
    }

    /**
     *
     * @param <T>
     * @param portName
     * @return Port object with specified name. null if portName doesn't exist.
     */
    public <T extends Token> InPort<T> inPort(String portName)
    {
        for(InPort<? extends Token> p: inPorts)
            if(p.getName().equals(portName))
                return (InPort<T>) p;
        
        return null; // shouldn't happen
    }
    
    /**
     *
     * @param <T>
     * @param portName
     * @return Port object with specified name. null if portName doesn't exist.
     */
    public <T extends Token> OutPort<T> outPort(String portName)
    {
        for(OutPort<? extends Token> p: outPorts)
            if(p.getName().equals(portName))
                return (OutPort<T>) p;
        
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
    public InPort<? extends Token> nextInPortToConnect()
    {
        for(InPort<? extends Token> p: inPorts)
        {
            if(!p.isConnected() )
                return p;
        }
        return null; // shouldn't happen
    }

    public OutPort<? extends Token> nextOutPortToConnect()
    {
        for(OutPort<? extends Token> p: outPorts)
        {
            if(!p.isConnected())
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
//            // handle all the events in the eventQueue
//            Event e = eventQueue.poll();
//            while( e != null)
//            {
//                handleEvent(e);
//                e = eventQueue.poll();
//            }

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


//    private void handleEvent(Event e)
//    {
//        SacreLib.logger.fine("Component " + name + " is handling event " + e);
//        if(e == Event.FLUSH)
//        {
//            // block all input ports
//            for(Port p: ports)
//            {
//                if(p.getDirType() == Port.DIR_TYPE_IN)
//                    p.setBlocked(true);
//                else // output ports
//                {
//                    p.addHook(new Hook()
//                                {
//                                    public boolean newToken(Object t)
//                                    {
//                                        if( ((Token)t).isStop() )
//                                        {
//                                            componentFlushed();
//                                            return false;
//                                        }
//                                        else
//                                            return true;
//                                    }
//                                });
//                }
//            }
//        }
//    }
//
//    public void sendEvent(Event e) throws InterruptedException
//    {
//        eventQueue.put(e);
//    }

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
    public List<InPort<? extends Token>> getInPorts()
    {
        return Collections.unmodifiableList(inPorts);
        //return ports;
    }

    /**
     * The list should be iteratable in the same order as nextPortToConnect().
     * @param <T>
     * @param dirType
     * @return port list
     */
    public List<OutPort<? extends Token>> getOutPorts()
    {
        return Collections.unmodifiableList(outPorts);
        //return ports;
    }    
    
    /**
     * subclasses of Component should use this method to define their ports.
     * @param p
     */
    protected void addInPort(InPort<? extends Token> p)
    {
        inPorts.add(p);

        // I'm not sure TODO: in order to remove from task() body the setting of the state to STOPPED
        // after sending a STOP token, for each output port, we add a hook which sets
        // state to STOPPED. not so nice when there are multiple output ports.
        // what if a STOP token on one port doesn't imply state to be STOPPED!
    }
    
    protected void addOutPort(OutPort<? extends Token> p)
    {
        outPorts.add(p);

        // I'm not sure TODO: in order to remove from task() body the setting of the state to STOPPED
        // after sending a STOP token, for each output port, we add a hook which sets
        // state to STOPPED. not so nice when there are multiple output ports.
        // what if a STOP token on one port doesn't imply state to be STOPPED!
    }
}
