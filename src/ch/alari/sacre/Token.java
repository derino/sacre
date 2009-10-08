/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.alari.sacre;

/**
 *
 * @author onur
 */
public class Token
{
    public static final int STOP = 1;
    public static final int DATA = 2;

    protected int type;

    // Assumption: subclasses should also have a constructor with type argument
    // reason 1: in order to be able to create a stop token in task()
    // reason 2: when blocked, port creates stop tokens in response to take()s by means of such a constuctor.
    public Token(int type)
    {
        this.type = type;
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
    
    @Override
    public String toString()
    {
        if(type == DATA)
            return "TOKEN";
        else // type=STOP
            return "STOP TOKEN";
    }
}
