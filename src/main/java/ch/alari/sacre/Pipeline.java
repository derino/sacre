/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import ch.alari.sacre.annotation.PortType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author onur
 */
public class Pipeline implements Callable<Object> 
{
    private ExecutorService e;
    
    private Map<String, Component> pComps;

    private ComponentFactory cf;

    // Controller needed the state variable to determine when to stop running
    public enum State {RUNNING, STOPPED};
    private volatile State state;

    // the list of Futures of running component threads
    private ConcurrentMap<String, Future<?>> fMap;
    //private Collection<Future<?>> fList;

    // apisink bileseni yaratilirken bu id kullaniliyor. bu id'li apilistenerlari notify ediyor.
    private int apiSinkUniqueKey;
    
    //private YazarSrc badiSrc;
    //private ConsoleSink console;

    public Pipeline()
    {
        this.cf = null;
        this.apiSinkUniqueKey = -1; // i.e. not set
        pComps = new HashMap<String, Component>();

        state = State.STOPPED;
    }

    
    public Pipeline(ComponentFactory cf)
    {
        this.cf = cf;
        this.apiSinkUniqueKey = -1; // i.e. not set
        pComps = new HashMap<String, Component>();

        state = State.STOPPED;
        
        //BlockingQueue<Baslik> badiSrc_console = new LinkedBlockingQueue<Baslik>();
        //badiSrc = new YazarSrc(badiSrc_console);
        //console = new ConsoleSink(badiSrc_console);
    }
    
    public Pipeline(ComponentFactory cf, int apiSinkUniqueKey)
    {
        this.cf = cf;
        this.apiSinkUniqueKey = apiSinkUniqueKey;
        pComps = new HashMap<String, Component>();

        state = State.STOPPED;
        
        //BlockingQueue<Baslik> badiSrc_console = new LinkedBlockingQueue<Baslik>();
        //badiSrc = new YazarSrc(badiSrc_console);
        //console = new ConsoleSink(badiSrc_console);
    }
    
    /**
     * Constructs the components and links them together as given in pStr.
     * @param pStr: pipeline string with ssg++ syntax
     */
    public void parse(String pStr) //TODO: throw ParseException
    {
        String[] stmts = pStr.split(";");
        
        for(String stmt: stmts)
        {
            // prevComps keeps track of the last sequential component(s) connected.
            List<String> prevComps = new ArrayList<String>();

            SacreLib.logger.fine("Statement:" + stmt);
            
            String[] seqComps = stmt.split("!");
            for(int i = 0; i< seqComps.length; i++)
            {
                seqComps[i] = seqComps[i].trim();
                SacreLib.logger.fine("Sequential component:" + seqComps[i]);
                
                String[] paralComps = seqComps[i].split("&");
                
                if(paralComps.length > 1)
                {
                    List<String> pcComps = new ArrayList<String>(); // to keep the parallel created comps of this iteration
                    for(String paralComp: paralComps)
                    {
                        paralComp = paralComp.trim();
                        SacreLib.logger.fine("Parallel component:" + paralComp);
                        // create component
                        String pcComp = createComponent(paralComp);
                        pcComps.add(pcComp);
                    }
                    
                    // if i==0 there is no previous comp. to be connected to.
                    if(i!=0)
                    {
                        connect(prevComps, pcComps);
                    }
                    prevComps.clear();
                    prevComps.addAll(pcComps);
                        
                }
                else
                {
                    //single sequential comp
                    String cComp = createComponent(seqComps[i]);
                    
                    // if i==0 there is no previous comp. to be connected to.
                    if(i!=0)
                    {
                        List<String> cComps = new ArrayList<String>();
                        cComps.add(cComp);
                        connect(prevComps, cComps);
                    }
                    prevComps.clear();
                    prevComps.add(cComp);
                }
            }
        }

        // apisink'e pipeline'i vermek gerekiyor. (ssgpp-webui'de pagination icin geldi bu.)
        for(Component c: pComps.values())
        {
            // special case of apisink
            if(c instanceof ApiSink)
            {
                ((ApiSink)c).setPipelineStr(pStr);
            }
        }
        
        
        // post-check whether all ports are connected.
        boolean allConnected = true;
        for(Component c: pComps.values())
        {
            for(Port p: c.getInPorts())
            {
                if(!p.isConnected())
                    SacreLib.logger.severe(p.getName() + " is not connected!");
                allConnected &= p.isConnected();
            }
            for(Port p: c.getOutPorts())
            {
                if(!p.isConnected())
                    SacreLib.logger.severe(p.getName() + " is not connected!");
                allConnected &= p.isConnected();
            }
            if(!allConnected)
            {
                SacreLib.logger.severe("Error in the pipeline construction!");
                System.exit(-1);
            }
        }
        // END post-check
        
        SacreLib.logger.fine(pComps.toString());
    }
 
    /**
     * component parameters may not always be there.
     * @param cStr: e.g. YazarSrc [author=ssg, name=ssgsrc, prop=value, ...]
     * @return name of the created component
     */
    private String createComponent(String cStr)
    {
        String cName; // component name to be returned: ssgsrc
        String cType; // YazarSrc
        
        String[] splits = cStr.split("\\[", 2);
        cType = splits[0].trim();
        cName = cType;
        
        if(splits.length == 1) // only YazarSrc. no parameters.
        {
            if(pComps.get(cName) == null) // if not yet created
            {
                cName += Component.getUniqueInstanceID();
                Component c = createComponentFromComponentFactory( cType, cName, null ); // without a 'name' parameter, components take the component type as name.
                pComps.put(cName, c);
            }
        }
        
        // else there are parameters to be parsed.
        else
        {
            Map<String, String> params = new HashMap<String, String>();

            splits[1] = splits[1].trim();
            // TODO: error checking of given string
            //if( splits[1].substring(splits[1].length()-1).equals("\\]") )
            //    System.out.println("Syntax error");
            
            String keysValues = splits[1].substring(0, splits[1].length()-1); // throw out "]"
            String[] keyValuePairs = keysValues.split(",");
            boolean nameSpecified = false;
            for(String keyValuePair: keyValuePairs)
            {
                keyValuePair = keyValuePair.trim();
                String[] keyValue = keyValuePair.split("=", 2);
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                if(key.equals("name"))
                {
                    cName = value;
                    nameSpecified = true;
                }
                else
                {
                    value = SacreLib.unescapePipelineString(value);
                    params.put(key, value); // not adding 'name' to params because name is already stored with Component.name
                }
            }

            if(pComps.get(cName) == null)
            {
                if(!nameSpecified)
                {
                    try{
                        cName += Component.getUniqueInstanceID();
                    }catch(Throwable t)
                        {
                            t.printStackTrace();
                        }
                }
                pComps.put(cName, createComponentFromComponentFactory(cType, cName, params));
            }
        }
        
        return cName;
    }

    private Component createComponentFromComponentFactory(String cType, String cName, Map<String, String> params)
    {
        // first try the SacreComponentFactory
        Component c = SacreComponentFactory.instance().create(cType, cName, params);
        
        // special case of apisink
        if(c instanceof ApiSink)
        {
            ((ApiSink)c).setUniqueKey(apiSinkUniqueKey);
        }
        // then the external
        if(c == null && cf != null)
        {
            c = cf.create(cType, cName, params);
        }
        return c;
    }

    private void connect(List<String> prevComps, List<String> newComps)
    {
        for(String prevComp: prevComps)
        {
            for(String newComp: newComps)
            {
                OutPort portPrevComp = pComps.get(prevComp).nextOutPortToConnect();
                if(portPrevComp == null)
                    continue;
                String portTypePrev = getPortTypeOfComponentsPort( pComps.get(prevComp), portPrevComp, "OutPort");

                InPort portNewComp = pComps.get(newComp).nextInPortToConnect();
                if(portNewComp == null)
                {
                    //SacreLib.logger.log(Level.SEVERE, "attempted to connect non-existing ports: " + pComps.get(prevComp).getName() + " <-> " + pComps.get(newComp).getName() );
                    continue;
                    //System.exit(-1);
                }
                String portTypeNew = getPortTypeOfComponentsPort( pComps.get(newComp), portNewComp, "InPort");
                
                boolean arePortsCompatible;
                if(portTypePrev == null || portTypeNew == null)
                {
                    arePortsCompatible = false;
                }
                else
                {
                    arePortsCompatible = true;
                    try
                    {
                        // if the previous output port has a token type that is a subclass of 
                        // the token of the next input port, then they are compatible.
                        Class.forName(portTypePrev).asSubclass(Class.forName(portTypeNew));
                        //SacreLib.logger.log(Level.FINE, "dest port type subclass of source port type: " + portPrevComp.getName() + "<" + portTypePrev + ">" + " <-> " + portNewComp.getName() + "<" + portTypeNew + ">"  );
                    }
                    catch(ClassCastException cce)
                    {
                        arePortsCompatible = false;
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                        System.exit(-1);
                    }
                }
                
                if(arePortsCompatible) //if( portTypePrev.equals(portTypeNew) )
                {
                    portPrevComp.connect(portNewComp);
                    SacreLib.logger.log(Level.FINE, "compatible ports connected: " + portPrevComp.getName() + "<" + portTypePrev + ">" + " <-> " + portNewComp.getName() + "<" + portTypeNew + ">" );
                }
                else
                {
                    //SacreLib.logger.log(Level.SEVERE, "attempted to connect incompatible ports: " + portPrevComp.getName() + "<" + portTypePrev + ">" + " <-> " + portNewComp.getName() + "<" + portTypeNew + ">" );
                    //System.exit(-1);
                }
                
            }
        }
    }

    private String getPortTypeOfComponentsPort(Component comp, Port port, String inport_outport) throws SecurityException, IllegalArgumentException {
        Class<?> c = comp.getClass();
        //System.out.println("class: " + c.getName());
        Field[] fields = c.getDeclaredFields();
        ArrayList<Field> allFields = new ArrayList<>();
        for(Field f: fields)
            allFields.add(f);
        // add fields from superclasses (required in the case of multi-level inheriting classes, e.g., Merge8x1)
        Class<?> currSuperClass = c.getSuperclass();
        while( currSuperClass != null )
        {
            Field[] superfields = currSuperClass.getDeclaredFields();
            for(Field f: superfields)
                allFields.add(f);
            currSuperClass = currSuperClass.getSuperclass();
        }
        
        for (Field f: allFields) //(Field f : fields)
        {
            if (f.getType().getSimpleName().equals(inport_outport)) {
                //System.out.println(f.getName() + ": " + f.getType());
                f.setAccessible(true);
                try {
                    if (f.get(comp).equals(port)) {
                        //System.out.println("generic type: " + f.getGenericType().getTypeName());
                        String portType = f.getGenericType().getTypeName(); // ch.alari.sacre.OutPort<ch.alari.sacre.TextToken>
                        // extract <...>
                        int beg = portType.lastIndexOf('<');
                        String tokenType = "java.lang.Object";
                        if(beg != -1) // ch.alari.sacre.OutPort, may be defined as such. then tokenType="java.lang.Object"
                            tokenType = portType.substring( beg+1, portType.lastIndexOf('>') );
                        return tokenType;
//                        PortType anno = f.getAnnotation(PortType.class);
//                        if(anno!=null)
//                        {
//                            System.out.println("anno: " + anno.value());
//                            return anno.value();
//                        }
//                        else
//                            return null;
                    }
                }catch(IllegalAccessException iae) { iae.printStackTrace();}
            }
        }
        return null;
    }
    
//    private String getPortTypeOfComponentsInPort(Component comp, InPort port) throws SecurityException, IllegalArgumentException {
//        Class<?> c = comp.getClass();
//        //System.out.println("class: " + c.getName());
//        Field[] fields = c.getDeclaredFields();
//        Field[] superfields = c.getSuperclass().getDeclaredFields();
//        ArrayList<Field> allFields = new ArrayList<>();
//        for(Field f: fields)
//            allFields.add(f);
//        for(Field f: superfields)
//            allFields.add(f);
//        
//        for (Field f: allFields) //(Field f : fields)
//        {
//            if (f.getType().getSimpleName().equals("InPort")) {
//                //System.out.println(f.getName() + ": " + f.getType());
//                f.setAccessible(true);
//                try {
//                    if (f.get(comp).equals(port)) {
//                        //System.out.println("generic type: " + f.getGenericType().getTypeName());
//                        String portType = f.getGenericType().getTypeName(); // ch.alari.sacre.InPort<ch.alari.sacre.TextToken>
//                        // extract <...>
//                        int beg = portType.lastIndexOf('<');
//                        String tokenType = "java.lang.Object";
//                        if(beg != -1) // ch.alari.sacre.InPort, may be defined as such. then tokenType="java.lang.Object"
//                            tokenType = portType.substring( beg+1, portType.lastIndexOf('>') );
//                        return tokenType;
////                        PortType anno = f.getAnnotation(PortType.class);
////                        if(anno != null)
////                        {
////                            System.out.println("anno: " + anno.value());
////                            return anno.value();
////                        }
////                        else
////                            return null;
//                    }
//                }catch(IllegalAccessException iae) { iae.printStackTrace();}
//            }
//        }
//        return null;
//    }
    
    public Object call()
    {
        state = State.RUNNING;
        Object pipelineResult = null;
        
        try
        {
            e = Executors.newCachedThreadPool();
            //List<Future<?>> fList = new ArrayList<Future<?>>();
            //fList = new ConcurrentLinkedQueue<Future<?>>();
            fMap = new ConcurrentHashMap<String, Future<?>>();
            
            for(Component c: pComps.values())
            {
                runComponent(c);
            }
            
            // TODO: newcomp'un flist'e eklenmesi pek sallanmiyor gibi. flist'i her seferinde bastan gezmem gerekiyor sanirim.
            //for(Future<?> f: fList)
            // I don't know if f.get()'ing in a different order than submit()ing order works!
            //boolean allDone = false;
            //while(!allDone)
            //{
            
                for(String s: fMap.keySet())
                {
                    SacreLib.logger.fine("Thread " + s);
                    try
                    {
                        Object res = fMap.get(s).get();
                        
                        if( res != null)
                        {
                            pipelineResult = res;
                        }
                        SacreLib.logger.fine("Thread(" + s + ") executed successfully!");
                    }
                    catch(ExecutionException ee)
                    {
                        SacreLib.logger.log(Level.WARNING, "Exception occurred in Thread(" + s + ")!", ee);
                    }
                }

                for(String s: fMap.keySet())
                {
                    SacreLib.logger.fine("Thread " + s);
                    try
                    {
                        Object res = fMap.get(s).get();
                        if( res != null)
                        {
                            pipelineResult = res;
                        }
                        SacreLib.logger.fine("Thread(" + s + ") executed successfully!");
                    }
                    catch(ExecutionException ee)
                    {
                        SacreLib.logger.log(Level.WARNING, "Exception occurred in Thread(" + s + ")!", ee);
                    }
                }

                //allDone = true;
                //for(String s: fMap.keySet())
                //{
                //    SacreLib.logger.fine("Thread-" + s);
                //    allDone &= fMap.get(s).isDone();
                //}
            //}

                //Thread.currentThread().join();
        }
        catch(InterruptedException ie)
        {
            // if pipeline thread is shutdownNow'ed by its executor,
            // propagate it to the threads owned by the pipeline.
            stop();
        }

        state = State.STOPPED;
        e.shutdown();
        
        return pipelineResult;
    }

    // called by run and by adaptor after insertion of new components
    public void runComponent(Component c)
    {
        fMap.put( c.getName(), e.submit(c) );
    }
    
    private void stop()
    {
        //System.out.println("Pipeline shutting down...");
        e.shutdownNow();
        state = State.STOPPED;
    }

    /**
     * @return the state
     */
    public State getState()
    {
        return state;
    }

    /**
     * @return the component named name
     */
    public Component getComponent(String name)
    {
        return pComps.get(name);
    }

    public void removeComponent(String name)
    {
        pComps.remove(name);
    }

    public void addComponent(Component c)
    {
        pComps.put(c.getName(), c);
    }

    /**
     * @return the list of component typed type
     */
    public List<Component> getComponentsByType(String type)
    {
        List<Component> matches = new ArrayList<Component>();
        for(Component c: pComps.values())
        {
            if(c.getType().equalsIgnoreCase(type))
                matches.add(c);
        }
        return matches;
    }
}
