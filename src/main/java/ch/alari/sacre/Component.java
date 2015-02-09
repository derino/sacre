/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;

/**
 *
 * @author onur
 */
public abstract class Component implements Callable<Object> 
{
    // instance name of the component
    private String name;

    // description of the component, used for help messages
    protected String description = "no description given";
    
    // set by subclass constructor. I may enforce a way to check at compile-time that it is done
    protected String type;

    // subclasses should initialize (if needed) from given params in their constructor.
    //protected Map<String, Object> params;
    protected Map<String, String> params;
    protected Map<String, ParameterDescriptor<?>> parameters;
  
    // implies component has been created with proper parameters
    // this variable is true at the end of the constructor if the initilization is correct.
    // otherwise the task is stopped right in the beginning of task()
    protected boolean initSuccess;
    
    // a list of preconditions to be satisfied by the given parameters. (additional on top of parameter preconditions)
    // to be populated via addPreconditions() method in the constructor of subclasses if such preconditions exist.
    protected List<ParameterPrecondition> preconditions;
    
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

    
    /**
     * Main functionality of the component is to be implemented by task().
     * It is called as long as there is input to be processed or, in the case of source components, until stopAndExit() is explicitly called.
     * @throws InterruptedException
     * @throws Exception 
     */
    public abstract void task() throws InterruptedException, Exception;
    
    /**
     * Override if a component requires any action while exiting when the input stream has ended.
     * default behavior does nothing.
     * Those components that produce their output when all their input is consumed should override this method
     * to write their results to their output ports.
     * @throws java.lang.InterruptedException
     */
    public void exiting() throws InterruptedException 
    {
//        System.out.println(getName() + " exiting");
    }

    /**
     * STOP_WHEN_ALL_STOP_TOKENS_RECEIVED (default): for most components (at first, thought to be only for Merge-like components, but in fact applies to all others.
     * CUSTOM_POLICY: to be selected if "reachedEndOfStream(InPort<? extends Token> ip)" method is overridden.
     * //STOP_WHEN_FIRST_STOP_TOKEN_RECEIVED: for most components
     */
    public enum EndOfStreamPolicy {STOP_WHEN_ALL_STOP_TOKENS_RECEIVED /*, CUSTOM_POLICY, STOP_WHEN_FIRST_STOP_TOKEN_RECEIVED*/};
    
    /**
     * - The STOP_WHEN_ALL_STOP_TOKENS_RECEIVED policy implies that a component finishes on the last call to InPort.take() 
     * at the time of which all input ports have previously received a STOP_TOKEN.
     * //- The STOP_WHEN_FIRST_STOP_TOKEN_RECEIVED policy implies that a component finishes its execution when a InPort.take() is issued that
     * receives a STOP_TOKEN.
     * //- To be set in the subclass's constructor if different than STOP_WHEN_FIRST_STOP_TOKEN_RECEIVED
     */
    protected EndOfStreamPolicy endOfStreamPolicy;
    
    /**
     * - called by source components at the end of the stream.
     * - called eventually by an input port of the component when the component thread is to be finished.
     * - calls exiting() function possibly overridden by the component before
     *   sending STOP_TOKENs to all output ports and exiting the component thread.
     * @throws InterruptedException 
     */
    public void stopAndExit() throws InterruptedException
    {
        exiting();
        sendOutStopTokens();
        throw new InterruptedException("EOS");
    }

    private void sendOutStopTokens() throws InterruptedException
    {
        for(OutPort op: getOutPorts())
        {
            op.put(new Token(Token.STOP));
        }
    }    
        
    public void stopAndExitIfAllStopped() throws InterruptedException
    {
        boolean allInputPortsStopped = true;

        for(InPort ip: getInPorts())
        {
            allInputPortsStopped &= ip.isStopped();
        }
        if(allInputPortsStopped)
        {
            stopAndExit();
        }
    }
    
    /**
     * called by an input port when a STOP_TOKEN is received.
     * default behavior sends a STOP_TOKEN to all output ports and stops the component's thread
     * when all input ports have received a STOP_TOKEN.
     * To be overridden if a component wants to implement a custom stop policy.
     * @param ip - the input port that has received a STOP_TOKEN.
     * @throws InterruptedException 
     */
    public void reachedEndOfStream(InPort<? extends Token> ip) throws InterruptedException
    {
        if(endOfStreamPolicy == EndOfStreamPolicy.STOP_WHEN_ALL_STOP_TOKENS_RECEIVED)
            stopAndExitIfAllStopped();
    }
    
    public Component(String name, Map<String, String> params)
    {
        this.name = name;
        //params = new HashMap<String, Object>();
        this.params = params;
        initSuccess = true;
        inPorts = new ArrayList<>();
        outPorts = new ArrayList<>();
        state = State.NOT_STARTED;
        endOfStreamPolicy = EndOfStreamPolicy.STOP_WHEN_ALL_STOP_TOKENS_RECEIVED; //EndOfStreamPolicy.STOP_WHEN_FIRST_STOP_TOKEN_RECEIVED;
//        eventQueue = new LinkedBlockingQueue<Event>();        
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
 
    protected void setDescription(String description)
    {
        this.description = description;
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
     * syntax that for ex. in "A ! B & C" B and C are connected to the first and second port of A, respectively.
     * @return the next unconnected port of given directional type
     */
    public InPort<? extends Token> nextInPortToConnect()
    {
        for(InPort<? extends Token> p: inPorts)
        {
            if(!p.isConnected() )
                return p;
        }
        return null;
    }

    public OutPort<? extends Token> nextOutPortToConnect()
    {
        for(OutPort<? extends Token> p: outPorts)
        {
            if(!p.isConnected())
                return p;
        }
        return null;
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

    @Override
    public Object call()
    {
        if(!initSuccess)
        {
            try {
                for(OutPort p: getOutPorts())
                    p.put(new Token(Token.STOP));
            } catch(InterruptedException iex)
            {
                SacreLib.logger.log(Level.SEVERE, "Error stopping components after " + type + "(instance name:" + name + ")", iex);
            }
            state = State.STOPPED;
            SacreLib.logger.log(Level.SEVERE, "Error initializing component {0}(instance name:{1}). Check its parameters!", new Object[]{type, name});
            //throw new ExecutionException();
            return null;
        }
        
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
                if(ie.getMessage().equals("EOS")) // task exits normally at the end of stream with stopAndExit()
                {
                    SacreLib.logger.log(Level.FINE, "{0}(instance name:{1}) thread finished.", new Object[]{type, name});
                    return result;
                }
                else
                {
                    SacreLib.logger.log(Level.FINE, "{0}(instance name:{1}) thread interrupted.", new Object[]{type, name});
                    state = State.STOPPED; // not necessarily needed.
                    return null;
                }
            }
            catch(Exception ex)
            {
                SacreLib.logger.log(Level.WARNING, "Exception in " + type + "(instance name:" + name + ") thread", ex);
            }
        }
        
        // should never execute
        assert(false);
        return null;
//        SacreLib.logger.fine(type + "(instance name:" + name + ") thread finished.");
//        return result;
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

//    protected void componentFlushed()
//    {
//        state = State.FLUSHED;
//    }

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
    
    protected void addInPort(InPort<? extends Token> p)
    {
        inPorts.add(p);
    }
    
    protected void addOutPort(OutPort<? extends Token> p)
    {
        outPorts.add(p);
    }
    
    protected void addParameterDescriptor(ParameterDescriptor<?> pd)
    {
        if(parameters == null)
            parameters = new HashMap<>();
        parameters.put(pd.getName(), pd);
    }
    
    protected void initParameters()
    {
        // set each parameter in parameters according to params. 
        // if exists, parameters individual preconditions are checked.
        parameters.values().stream().forEach((pd) -> {
            try {
                pd.setValue(params.get(pd.getName()));
            } catch (ParameterDescriptor.UnallowedParameterValueException | ParameterDescriptor.RequiredParameterNotSuppliedException | ParameterDescriptor.PreconditionsNotMetException ex) {
                initSuccess = false;
            }
        });
        
        // check component's preconditions. (e.g., whether two specific parameters are given or not)
        if( !arePreconditionsMet() )
        {
            initSuccess = false;
        }
    }
    
    
    private boolean arePreconditionsMet() 
    {
        if(preconditions== null || preconditions.isEmpty())
            return true;

        //return preconditions.stream().allMatch(p -> p.test());
        
        // for better error msg
        boolean allmet = true;
        for(ParameterPrecondition pp: preconditions) 
        {
            if(!pp.test())
            {
                allmet = false;
                SacreLib.logger.log(Level.WARNING, "For {0}, it is required that {1}", new Object[]{getType(), pp});
            }
        }
        return allmet;
    }
    
    public void addPreconditions(ParameterPrecondition... preconditions)
    {
        if(this.preconditions == null)
            this.preconditions = new ArrayList<>();
        
        this.preconditions.addAll(Arrays.asList(preconditions));
    }

    // TODO_CHECK
    public String toHelpString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Component: ").append(getType()).append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
        
        sb.append("           ").append(description).append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
        
        sb.append("Input ports: ");
        if(getInPorts().isEmpty())
            sb.append("none");
        sb.append(System.getProperty("line.separator"));
        for(InPort ip: getInPorts())
        {
            String tokenType = Pipeline.getPortTypeOfComponentsPort(this, ip); // ch.alari.sacre.Token
            if(tokenType != null)
                tokenType = tokenType.substring( tokenType.lastIndexOf(".") + 1); // Token
            else
                tokenType = "Token"; // ONEMLI: MergeNx1 gibi bilesenlerde port tipi tespit edilemediginden null donuyor. Elle Token olarak duzeltiyorum.
            sb.append(" - ").append(ip.getName()).append("(token type: ").append(tokenType).append(")").append(System.getProperty("line.separator"));
        }
        sb.append(System.getProperty("line.separator"));
        
        sb.append("Output ports: ");
        if(getOutPorts().isEmpty())
            sb.append("none");
        sb.append(System.getProperty("line.separator"));
        for(OutPort op: getOutPorts())
        {
            String tokenType = Pipeline.getPortTypeOfComponentsPort(this, op); // ch.alari.sacre.Token
            if(tokenType != null)
                tokenType = tokenType.substring( tokenType.lastIndexOf(".") + 1); // Token
            else
                tokenType = "Token"; // ONEMLI: MergeNx1 gibi bilesenlerde port tipi tespit edilemediginden null donuyor. Elle Token olarak duzeltiyorum.
            sb.append(" - ").append(op.getName()).append("(token type: ").append(tokenType).append(")").append(System.getProperty("line.separator"));            
        }
        sb.append(System.getProperty("line.separator"));
        
        sb.append("Parameters: ");
        if(parameters!=null)
        {
            sb.append(System.getProperty("line.separator"));
            for(ParameterDescriptor pd: parameters.values())
                sb.append(" - ").append(pd.toHelpString());
        }
        else
            sb.append("none").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));;
        
        
        if(preconditions != null && !preconditions.isEmpty())
        {
            sb.append("Preconditions: ").append(System.getProperty("line.separator"));
            for(ParameterPrecondition p: preconditions)
                sb.append(" - ").append(p).append(System.getProperty("line.separator"));;
        }
        sb.append(System.getProperty("line.separator"));
        
        return sb.toString();
    }
}
