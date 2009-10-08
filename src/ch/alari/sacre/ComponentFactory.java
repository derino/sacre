/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.Map;

/**
 *
 * @author onur
 */
public interface ComponentFactory
{
    /**
     * @param cType: Command-line alias for the component
     * @param cName: identifies a component in a pipeline
     */
    public Component create(String cType, String cName);

    public Component create(String cType, String cName, Map<String, String> params);

}
