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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class MergeNx1 extends Component
{
    protected OutPort<Token> out;
    
    public MergeNx1(String name, Map<String, String> parameters)
    {
        super(name);
        setType("MergeNx1");
        
        out = new OutPort<>(this);
        
        int n = 2; // default value
        if(parameters != null)
        {
            if( parameters.get("n") != null )
            {
                n = new Integer(parameters.get("n"));
            }
        }
        
        for(int i=0; i<n; i++)
        {
            new InPort<Token>(this); // gets added to output ports list
        }
    }
    
    public void task() throws InterruptedException, Exception
    {
//        Spliterator<InPort<? extends Token>> ips = getInPorts().spliterator();
//        try {
//            while(ips.tryAdvance(new Consumer<InPort<? extends Token>>() {
//
//                public void accept(InPort<? extends Token> ip) {
//                    try {
//                        Token t = ip.take();
//                        if(!ip.isStopped())
//                            out.put(t);
//                    } catch (InterruptedException ex) {
//                        if(ex.getMessage().equals("EOS"))
//                            new UnsupportedOperationException("EOS"); //Logger.getLogger(MergeNx1.class.getName()).log(Level.SEVERE, null, ex);
//                        //else ignored, not ideal. but could not find a way to throw an InterruptException inside Consumer.
//                            
//                    }
//                }
//            }));        
//        } catch(UnsupportedOperationException uoe)
//        {
//            if(uoe.getMessage().equals("EOS"))
//                new InterruptedException("EOS");
//        }
        
        
        
//        List<InPort<? extends Token>> ips = Collections.synchronizedList(getInPorts());
//        synchronized(ips)
//        {
//            System.out.println(getName() + " got ips");
//            Iterator<InPort<? extends Token>> it = ips.iterator();
//            while(it.hasNext())
//            {
//                InPort<? extends Token> p = it.next();
//                Token t = p.take(); // if this take() receives a STOP_TOKEN, then null is returned by take().
//                if(!p.isStopped())
//                {
//                    out.put(t);
//                }
//                else
//                {
//                    if(ips.size() == 2) //(getInPorts().size() == 2)
//                        System.out.println("merge2x1 read stop token: " + t);
//                }
//            }       
//            System.out.println(getName() + " released ips");
//        }

    
        for(InPort p: getInPorts())
        {
//            if(getInPorts().size() == 2)
//                System.out.println("merge2x1 reading token");
            Token t = p.take(); // if this take() receives a STOP_TOKEN, then null is returned by take().
//            if(getInPorts().size() == 2)
            System.out.println(getName() + " read token: " + t);
            if(!p.isStopped())
            {
                out.put(t);
//                if(getInPorts().size() == 2)
//                    System.out.println("merge2x1 wrote token: " + t);
            }
//            else
//            {
//                if(getInPorts().size() == 2) //(getInPorts().size() == 2)
//                    System.out.println("merge2x1 read stop token: " + t);
//            }
        }       

    
    
    }

    public InPort getIn(int i)
    {
        return getInPorts().get(i);
    }
    
    public OutPort<Token> getOut()
    {
        return out;
    }
}
