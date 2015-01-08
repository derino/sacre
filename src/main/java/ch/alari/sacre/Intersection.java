/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Onur Derin  All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 *  are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of the <ORGANIZATION> nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.alari.sacre;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class Intersection extends Component 
{
    List<Token> historyIn1 = new ArrayList<Token>();
    List<Token> historyIn2 = new ArrayList<Token>();
    boolean in1Live=true, in2Live=true;

    public Intersection(String name)
    {
        super(name);
        setType("Intersection");
        
        addPort(new Port<Token>(Token.class, "in1", Port.DIR_TYPE_IN));
        addPort(new Port<Token>(Token.class, "in2", Port.DIR_TYPE_IN));
        addPort(new Port<Token>(Token.class, "out", Port.DIR_TYPE_OUT));
    }
    
    public void task() throws InterruptedException, Exception
    {
        //while(true)
        //{//TODO: how to do it with generics to avoid casting

        if(in1Live)
        {
            Token b1 = (Token)port("in1").take(); // TODO: poll() makes more sense than take but CPU load is too much
            if( b1 != null )
            {
                if(b1.isStop())
                    in1Live =false;
                else
                {
                    // write to in1history
                    historyIn1.add(b1);
                    // compare to in2history, if match write out
                    if( existsIn(historyIn2, b1) )
                        port("out").put(b1);
                }
            }
        }

        if(in2Live)
        {
            Token b2 = (Token)port("in2").take(); // TODO: poll() makes more sense than take but check CPU load
            if( b2 != null )
            {
                if(b2.isStop())
                    in2Live =false;
                else
                {
                    // write to in2history
                    historyIn2.add(b2);
                    //compare to in1history, if match write out
                    if( existsIn(historyIn1, b2) )
                        port("out").put(b2);
                }
            }
        }

        if(!in1Live && !in2Live)
        {
            port("out").put(new Token(Token.STOP));
            state = State.STOPPED;
            return;
        }

        //} // /end while
    }

    private boolean existsIn(List<Token> history, Token b)
    {
        for(Token hb: history)
            if(b.equals(hb))
                return true;
        
        return false;
    }
}
