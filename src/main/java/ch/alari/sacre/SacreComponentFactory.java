/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class SacreComponentFactory implements ComponentFactory
{
    // Different from non-sacre component factories, each component should be
    // created with a unique name. e.g. Intersection can appear many times in a pipeline.
    // they should be distinguished.

    // singleton
    private static final SacreComponentFactory instance = new SacreComponentFactory();

    private static final List<ComponentFactory> cfs = new ArrayList<>();
    
    private final PluginManager pluginManager;
    
    private SacreComponentFactory()
    {
        pluginManager = new PluginManager();
        pluginManager.loadPlugins();
    }

    public static ComponentFactory instance()
    {
        return instance;
    }

    public static void addComponentFactory(ComponentFactory cf)
    {
        cfs.add(cf);
    }
    
    
//    public Component create(String cType, String cName)
//    {
//        return create(cType, cName, null);
//    }

    private void addStringMapToMap(String[][] sm, Map m)
    {
        for(int i=0; i < sm[0].length; i++)
            m.put(sm[0][i], sm[1][i]);   
    }
    
    @Override
    public String[][] getComponentsMap()
    {
        // combine components from all
        // native comps
        Map<String, String> comps = new HashMap<>();
        addStringMapToMap(componentsMap, comps);
        // other cfs
        for(ComponentFactory cf: cfs)
        {
            addStringMapToMap(cf.getComponentsMap(), comps);
        }
        // plugin comps
        addStringMapToMap(pluginManager.getComponentsMap(), comps);
        
        // map to string[][]
        int numComps = comps.keySet().size();
        String [][] allComponentsMap = new String[2][numComps];
        allComponentsMap[0] = comps.keySet().toArray(allComponentsMap[0]); 
        //componentsMap[1] = pluginsInfo.values().toArray(componentsMap[1]);
        for(int i=0; i<numComps; i++)
            allComponentsMap[1][i] = comps.get(allComponentsMap[0][i]);
        
        return allComponentsMap;
    }
    
    private final String[][] componentsMap = { 
        {"merge",                "mergenx1",                "merge3x1",                "merge8x1",                "intersection",                "fork",                "fork1x3",                "fork1xn",                "gnd",                   "apisink",                "limitflt",                "testknk",                "testcvt",                "testbtk"},
        {"ch.alari.sacre.Merge", "ch.alari.sacre.MergeNx1", "ch.alari.sacre.Merge3x1", "ch.alari.sacre.Merge8x1", "ch.alari.sacre.Intersection", "ch.alari.sacre.Fork", "ch.alari.sacre.Fork1x3", "ch.alari.sacre.Fork1xN", "ch.alari.sacre.Ground", "ch.alari.sacre.ApiSink", "ch.alari.sacre.LimitFilter", "ch.alari.sacre.TestSrc", "ch.alari.sacre.TestCvt", "ch.alari.sacre.TestSink"}
    };
    
    /**
     * @param cType: Command-line alias for the component
     * @param cName: identifies a component in a pipeline
     * @param params: component's initialization parameters
     * @return created component
     */
    @Override
    public Component create(String cType, String cName, Map<String, String> params)
    {
        Component c = null; // to be returned
        
        // first search in native Sacre components
        String cTypeSmall = cType.toLowerCase(Locale.forLanguageTag("tr"));
        int classNameIndex = Arrays.asList(componentsMap[0]).indexOf(cTypeSmall);
        if(classNameIndex != -1) 
        {
            String className; // ch.alari.xxx
            className = componentsMap[1][classNameIndex];
            try {
                // create from className
                c = (Component) Class.forName(className).getConstructor(String.class, Map.class ).newInstance(cName, params);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(SacreComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // search in registered component factories
        if(c == null) 
        {
            for(ComponentFactory cf: cfs)
            {
                c = cf.create(cType, cName, params);
                if(c != null)
                    break;
            }
        }
        
        // search in plugins
        if(c == null)
            c = pluginManager.create(cType, cName, params);
        
        // check null?
        return c;
        
        
            
        
//        if(cType.equalsIgnoreCase("merge"))
//            return new Merge(cName, params);
//        else if(cType.equalsIgnoreCase("mergenx1"))
//            return new MergeNx1(cName, params);
//        else if(cType.equalsIgnoreCase("merge3x1"))
//            return new Merge3x1(cName, params);
//        else if(cType.equalsIgnoreCase("merge8x1"))
//            return new Merge8x1(cName, params);
//        else if(cType.equalsIgnoreCase("intersection"))
//            return new Intersection(cName, params);
//        else if(cType.equalsIgnoreCase("fork"))
//            return new Fork(cName, params);
//        else if(cType.equalsIgnoreCase("fork1x3"))
//            return new Fork1x3(cName, params);
//        else if(cType.equalsIgnoreCase("fork1xn"))
//            return new Fork1xN(cName, params);
//        else if(cType.equalsIgnoreCase("gnd"))
//            return new Ground(cName, params);
//        else if(cType.equalsIgnoreCase("apisink"))
//            return new ApiSink(cName, params);
//        else if(cType.equalsIgnoreCase("LimitFlt"))
//            return new LimitFilter(cName, params);
//        else if(cType.equalsIgnoreCase("TestKnk"))
//            return new TestSrc(cName, params);
//        else if(cType.equalsIgnoreCase("TestCvt"))
//            return new TestCvt(cName, params);
//        else if(cType.equalsIgnoreCase("TestBtk"))
//            return new TestSink(cName, params);
//        else
//        {
//            return pluginManager.create(cType, cName, params);
//        }
        
    }
}