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

package ch.alari.sacre.casestudy.case1.comps;

import ch.alari.sacre.Component;
import ch.alari.sacre.Port;
import ch.alari.sacre.SacreLib;
import ch.alari.sacre.Token;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class ConsoleSink extends Component 
{
    
    public ConsoleSink(String name)
    {
        super(name);
        setType("ConsoleSink");
        
        addPort(new Port<Token>(Token.class, "in", Port.DIR_TYPE_IN));
    }
    
    public void task() throws InterruptedException, Exception
    {
        //while(true)
        //{//TODO: how to do it with generics to avoid casting
            Token t = (Token)port("in").take(); //Token t = (Token)port("in").getQueue().take(); //qIn.take();
            if( t.isStop())
            {
                state = State.STOPPED; // TODO: handle in Component with output hooks? what if there are more than one output ports?
                return;
            }
            System.out.println(t);
            //Thread.sleep(1); // design for interrupts
            //Thread.yield();
        //} // /end while
    }

}
