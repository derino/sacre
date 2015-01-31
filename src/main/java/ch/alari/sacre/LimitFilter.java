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
public class LimitFilter extends Component 
{
    protected InPort<Token> in;
    protected OutPort<Token> out;
    
    protected boolean initSuccess;
    
    private long limit;
    private long count;
    
    public LimitFilter(String name, Map<String, String> parameters)
    {
        super(name);
        setType("LimitFlt");
        
        in = new InPort<>(this);
        out = new OutPort<>(this);
                
        initSuccess = true;
        
        count = 0;
        
        if(parameters != null)
        {
            if( parameters.get("limit") != null )
                limit = new Long((String)parameters.get("limit")).longValue();
            else
            {
                limit = 1;
            }
        }
        else // default value
            limit = 1;
            
    }
    
    public void task() throws InterruptedException//, Exception
    {
        if(!initSuccess)
        {
            out.put(new Token(Token.STOP));
            state = State.STOPPED;
            throw new InterruptedException();
        }
        
        Token curr = in.take();
        if(curr.isStop())
        {
            out.put(new Token(Token.STOP));
            state = State.STOPPED;
            return;
        }
        
        // filtreleme
        // baslik olarak *xxx*xxx* formati girilebilir.
        if ( ++count <= limit )
        {
            out.put(curr);
        }
    }
}
