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

import java.util.Map;


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
    
    //boolean in1Live = true, in2Live = true, in3Live = true, in4Live = true, in5Live = true, in6Live = true, in7Live = true, in8Live = true;

    public Merge8x1(String name, Map<String, String> params)
    {
        super(name, params);
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
        Token b1 = in1.take();
        if(!in1.isStopped())
        {
            out.put(b1);
        }

        Token b2 = in2.take();
        if(!in2.isStopped())
        {
            out.put(b2);
        }

        Token b3 = in3.take();
        if(!in3.isStopped())
        {
            out.put(b3);
        }
        
        Token b4 = in4.take();
        if(!in4.isStopped())
        {
            out.put(b4);
        }

        Token b5 = in5.take();
        if(!in5.isStopped())
        {
            out.put(b5);
        }

        Token b6 = in6.take();
        if(!in6.isStopped())
        {
            out.put(b6);
        }
        
        Token b7 = in7.take();
        if(!in7.isStopped())
        {
            out.put(b7);
        }

        Token b8 = in8.take();
        if(!in8.isStopped())
        {
            out.put(b8);
        }
    }

}
