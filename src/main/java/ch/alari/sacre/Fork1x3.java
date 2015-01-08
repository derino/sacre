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
public class Fork1x3 extends Component
{
    
    public Fork1x3(String name)
    {
        super(name);
        setType("Fork");
        
        addPort(new Port<Token>(Token.class, "in", Port.DIR_TYPE_IN));
        addPort(new Port<Token>(Token.class, "out1", Port.DIR_TYPE_OUT));
        addPort(new Port<Token>(Token.class, "out2", Port.DIR_TYPE_OUT));
        addPort(new Port<Token>(Token.class, "out3", Port.DIR_TYPE_OUT));
    }
    
    public void task() throws InterruptedException, Exception
    {
        Token t = (Token)port("in").take();
        if(t != null)
        {
            if(t.isStop())
            {
                port("out1").put(new Token(Token.STOP));
                port("out2").put(new Token(Token.STOP));
                port("out3").put(new Token(Token.STOP));
                state = State.STOPPED;
                return;
            }
            else
            {
                port("out1").put(t);
                port("out2").put(t);
                port("out3").put(t);
            }
        }
    }
}
