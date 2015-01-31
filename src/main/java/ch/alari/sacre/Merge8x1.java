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
public class Merge8x1 extends Merge3x1
{
    protected InPort<Token> in4;
    protected InPort<Token> in5;
    protected InPort<Token> in6;
    protected InPort<Token> in7;
    protected InPort<Token> in8;
    
    boolean in1Live = true, in2Live = true, in3Live = true, in4Live = true, in5Live = true, in6Live = true, in7Live = true, in8Live = true;

    public Merge8x1(String name)
    {
        super(name);
        setType("Merge8x1");
        
        in4 = new InPort<>(this);
        in5 = new InPort<>(this);
        in6 = new InPort<>(this);
        in7 = new InPort<>(this);
        in8 = new InPort<>(this);
    }
    
    @Override
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

        if(in3Live)
        {
            Token b3 = in3.take(); // poll() makes more sense but cpu-hungry
            if(b3 != null)
            {
                if(b3.isStop())
                    in3Live = false;
                else
                    out.put(b3);
            }
        }
        
        if(in4Live)
        {
            Token b4 = in4.take(); // poll() makes more sense but cpu-hungry
            if(b4 != null)
            {
                if(b4.isStop())
                    in4Live = false;
                else
                    out.put(b4);
            }
        }
        
        if(in5Live)
        {
            Token b5 = in5.take(); // poll() makes more sense but cpu-hungry
            if(b5 != null)
            {
                if(b5.isStop())
                    in5Live = false;
                else
                    out.put(b5);
            }
        }
        
        if(in6Live)
        {
            Token b6 = in6.take(); // poll() makes more sense but cpu-hungry
            if(b6 != null)
            {
                if(b6.isStop())
                    in6Live = false;
                else
                    out.put(b6);
            }
        }
        
        if(in7Live)
        {
            Token b7 = in7.take(); // poll() makes more sense but cpu-hungry
            if(b7 != null)
            {
                if(b7.isStop())
                    in7Live = false;
                else
                    out.put(b7);
            }
        }
        
        if(in8Live)
        {
            Token b8 = in8.take(); // poll() makes more sense but cpu-hungry
            if(b8 != null)
            {
                if(b8.isStop())
                    in8Live = false;
                else
                    out.put(b8);
            }
        }
        
        if(!in1Live && !in2Live && !in3Live && !in4Live && !in5Live && !in6Live && !in7Live && !in8Live)
        {
            out.put(new Token(Token.STOP));
            state = State.STOPPED;
            return;
        }
        //} // /end while
    }

}
