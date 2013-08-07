/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.Map;

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
    private static SacreComponentFactory instance = new SacreComponentFactory();

    private SacreComponentFactory()
    {

    }

    public static ComponentFactory instance()
    {
        return instance;
    }

    /**
     * @param cType: Command-line alias for the component
     * @param cName: identifies a component in a pipeline
     */
    public Component create(String cType, String cName)
    {
        return create(cType, cName, null);
    }

    public Component create(String cType, String cName, Map<String, String> params)
    {
        if(cType.equalsIgnoreCase("merge"))
            return new Merge(cName);
        else if(cType.equalsIgnoreCase("merge3x1"))
            return new Merge3x1(cName);
        else if(cType.equalsIgnoreCase("merge8x1"))
            return new Merge8x1(cName);
        else if(cType.equalsIgnoreCase("intersection"))
            return new Intersection(cName);
        else if(cType.equalsIgnoreCase("fork"))
            return new Fork(cName);
        else if(cType.equalsIgnoreCase("gnd"))
            return new Ground(cName);
        else
            return null;
    }
}
