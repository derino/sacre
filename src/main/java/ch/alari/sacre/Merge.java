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


/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class Merge extends Component
{
    protected InPort<Token> in1;
    protected InPort<Token> in2;
    protected OutPort<Token> out;
    
    boolean in1Live = true, in2Live = true;

    public Merge(String name)
    {
        super(name);
        setType("Merge");
        
        in1 = new InPort<>(this);
        in2 = new InPort<>(this);
        out = new OutPort<>(this);
    }
    
    public void task() throws InterruptedException, Exception
    {
        //while(true)
        //{//TODO: how to do it with generics to avoid casting
        if(in1Live)
        {
            Token b1 = in1.take(); // TODO: poll() makes more sense but cpu-hungry
            if(b1 != null)
            {
                if(b1.isStop())
                    in1Live = false;
                else
                    out.put(b1);
            }
        }

        if(in2Live)
        {
            Token b2 = in2.take(); // poll() makes more sense but cpu-hungry
            if(b2 != null)
            {
                if(b2.isStop())
                    in2Live = false;
                else
                    out.put(b2);
            }
        }

        if(!in1Live && !in2Live)
        {
            out.put(new Token(Token.STOP));
            state = State.STOPPED;
            return;
        }
        //} // /end while
    }

}
