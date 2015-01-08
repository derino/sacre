/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public interface Hook<T>
{
    /**
     *
     * @param t
     * @return false if token t shouldn't propagate
     */
    public boolean newToken(T t);
}
