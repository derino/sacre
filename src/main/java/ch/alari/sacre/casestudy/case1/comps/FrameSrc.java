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
import ch.alari.sacre.casestudy.case1.Frame;
import java.util.Map;

/**
 * data: the data in the frames
 * num: number of frames to produce
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class FrameSrc extends Component
{
    // # of frames to produce
    private int numOfFrames = 1;

    // # num of Frames Produced
    private int numofFramesProduced = 1;

    public FrameSrc(String name, Map<String, String> parameters)
    {
        super(name);
        setType("FrameSrc");
        addPort(new Port<Frame>(Frame.class, "out", Port.DIR_TYPE_OUT));

        if( parameters.get("data") != null )
            params.put("data", parameters.get("data"));
        if( parameters.get("num") != null )
        {
            numOfFrames = new Integer(parameters.get("num")).intValue();
        }
    }
    
    public void task() throws InterruptedException, Exception
    {
        if(numofFramesProduced <= numOfFrames) //while(i <= numOfFrames)
            port("out").put( new Frame((String)params.get("data") + numofFramesProduced++) );
        else
        {
            // TODO: stopping the component thread should be handled by the Component superclass.
            port("out").put( new Frame(Token.STOP) );
            state = State.STOPPED; // TODO: handle in Component with output hooks? what if there are more than one output ports?
            return;
        }
    }
}
