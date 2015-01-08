/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.Map;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class HighPriorityThreadFactory implements ThreadFactory
{
    // Different from non-sacre component factories, each component should be
    // created with a unique name. e.g. Intersection can appear many times in a pipeline.
    // they should be distinguished.

    // singleton
    private static HighPriorityThreadFactory instance = new HighPriorityThreadFactory();

    private HighPriorityThreadFactory()
    {

    }

    public static ThreadFactory instance()
    {
        return instance;
    }

    public Thread newThread(Runnable r)
    {
        Thread t = new Thread(r);
        t.setPriority(Thread.MAX_PRIORITY);
        return t;
    }
}
