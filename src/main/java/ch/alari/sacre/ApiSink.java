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


/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class ApiSink extends Component
{
    private InPort<Token> in;
    private ParameterDescriptor<InteractionType> tip;
    private enum InteractionType {SYNCHRONOUS, ASYNCHRONOUS};
//    private InteractionType tip;
    private int uniqueKey;

    private String pipelineStr;
    private boolean pipelineNotificationDone = false;
    
    public void setUniqueKey(int uniqueKey)
    {
        this.uniqueKey = uniqueKey;
    }
    
    public void setPipelineStr(String pipelineStr)
    {
        this.pipelineStr = pipelineStr;
    }
    
    public ApiSink(String name, Map<String, String> params)
    {
        super(name, params);
        setType("APISink");
        
        in = new InPort<>(this);
        
        // define tip
        String[] tipDomain = {"senkron", "asenkron"};
        InteractionType[] tipRange  = InteractionType.values();        
        tip = new ParameterDescriptor<>(this, "tip", false, tipRange[0], new DictionaryConverter<>(tipDomain, tipRange), tipDomain);
        
        // result is defined in Component. 
        // After the component thread ends, result is returned to the Pipeline, 
        // which in turn returns it to the original pipeline runner when pipeline thread ends.
        result = (Object) new ArrayList<Token>();
        
        
//        if(params != null)
//        {
//            if( params.get("tip") != null )
//            {
//                if( params.get("tip").equals("asenkron") )
//                    tip = InteractionType.ASYNCHRONOUS;
//                else if( params.get("tip").equals("senkron") )
//                    tip = InteractionType.SYNCHRONOUS;
//                else
//                {
//                    tip = InteractionType.SYNCHRONOUS;
//                }
//            }
//            else
//            {
//                tip = InteractionType.SYNCHRONOUS;
//            }
//        }
//        else // default value
//            tip = InteractionType.SYNCHRONOUS;
    }
    
    public void task() throws InterruptedException, Exception
    {
        // notify the listeners about pipelineStr before execution just for once.
        // cannot be moved to the constructor because listeners are added only after the ApiSink component is created.
        if(!pipelineNotificationDone && tip.getValue() == InteractionType.ASYNCHRONOUS)
        {
            for(ApiSinkListener asl: SacreLib.getApiSinkListeners(uniqueKey))
            {
                //SacreLib.logger.warning("apisink notified pStr: " + pipelineStr);
                asl.setPipelineString(pipelineStr);
            }
            pipelineNotificationDone = true;
        }
        
        
        Token t = in.take();
        
        if(tip.getValue() == InteractionType.SYNCHRONOUS)
            ((List<Token>)result).add(t);
        else // (tip == InteractionType.ASYNCHRONOUS)
        {
            for(ApiSinkListener asl: SacreLib.getApiSinkListeners(uniqueKey))
            {
                asl.newToken(t);
            }
        }

        
//        if(t != null)
//        {
//            if(t.isStop())
//            {
//                state = State.STOPPED;
//                if(tip == InteractionType.ASYNCHRONOUS)
//                {
//                    for(ApiSinkListener asl: SacreLib.getApiSinkListeners(uniqueKey))
//                    {
//                        asl.pipelineFinished(t);
//                    }        
//                    SacreLib.clearApiSinkListeners(uniqueKey);
//                }
//            }
//            else
//            {
//                if(tip == InteractionType.SYNCHRONOUS)
//                    ((List<Token>)result).add(t);
//                else // (tip == InteractionType.ASYNCHRONOUS)
//                {
//                    for(ApiSinkListener asl: SacreLib.getApiSinkListeners(uniqueKey))
//                    {
//                        asl.newToken(t);
//                    }
//                }
//            }
//        }

    }
    
    @Override
    public void exiting()
    {
        if(tip.getValue() == InteractionType.ASYNCHRONOUS)
        {
            for(ApiSinkListener asl: SacreLib.getApiSinkListeners(uniqueKey))
            {
                asl.pipelineFinished(new Token(Token.STOP));
            }        
            SacreLib.clearApiSinkListeners(uniqueKey);
        }
    }

}
