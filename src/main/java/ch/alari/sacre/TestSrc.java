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
import java.util.Map;

//import ch.alari.sacre.annotation.PortType;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class TestSrc extends Component
{
    // add a getter for the port if you want to manually connect ports with type safety when creating pipelines.
    //@PortType ("TextToken")
    private OutPort<TextToken> out;
    //private OutPort out;
    
    List<String> basliklar; // .tema biciminden dolayi boyle arrayli
    
    public TestSrc(String name, Map<String, String> params)
    {
        super(name, params);
        setType("testknk");
        this.out = new OutPort<>(this);
        
        basliklar = new ArrayList<String>();
        
//        basliklar.add("deneme deneme tunceli");
//        basliklar.add("deneme tunceli deneme");
//        basliklar.add("tunceli deneme deneme");
//        basliklar.add("dersim deneme deneme");
//        basliklar.add("deneme deneme tunceli'de");
//        basliklar.add("deneme tunceli'de deneme");
//        basliklar.add("tunceli'de deneme deneme");
//        basliklar.add("deneme deneme tuncelide");
//        basliklar.add("deneme tuncelide deneme");
//        basliklar.add("tuncelide deneme deneme");
//        basliklar.add("deneme deneme detuncelide");
//        basliklar.add("deneme detuncelide deneme");
        basliklar.add("token1");
        basliklar.add("token2");
    }
    
    @Override
    public void task() throws InterruptedException, Exception
    {
        for(String b: basliklar)
        {
            out.put(new TextToken(b));
        }
        
        stopAndExit();
    }

    /**
     * @return the out
     */
    public OutPort<TextToken> getOut() {
        return out;
    }
}
