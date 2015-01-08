/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author onur
 */
public class Token implements Cloneable
{
    public static final int STOP = 1;
    public static final int DATA = 2;

    protected int type;

    protected Set<String> tags;
    
    // Assumption: subclasses should also have a constructor with type argument
    // reason 1: in order to be able to create a stop token in task()
    // reason 2: when blocked, port creates stop tokens in response to take()s by means of such a constuctor.
    public Token(int type)
    {
        this.type = type;
        tags = new LinkedHashSet<String>();
    }

    public Token()
    {
        this.type = DATA;
    }

    public boolean isStop()
    {
        return type == Token.STOP;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public void addTag(String s)
    {
        tags.add(s);
    }
    
    public Set<String> getTags()
    {
        return tags;
    }
    
    @Override
    public String toString()
    {
        if(type == DATA)
            return "TOKEN";
        else // type=STOP
            return "STOP TOKEN";
    }
    
    public String toXMLString()
    {
        if(type == DATA)
            return "<TOKEN />";
        else // type=STOP
            return "<STOP TOKEN />";
    }
    
    public String toHTMLString()
    {
        if(type == DATA)
            return "<p>TOKEN</p>";
        else // type=STOP
            return "<p>STOP TOKEN</p>";
    }
    
    @Override
    public Object clone()
    {
        Token t = new Token();
        t.type = this.type;
        t.tags = new LinkedHashSet<String>(this.tags);
        return t;
    }
}
