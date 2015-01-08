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
import java.util.logging.Level;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class Encoder2 extends Component
{
    public Encoder2(String name, Map<String, String> parameters)
    {
        super(name);
        setType("Encoder2");
        addPort(new Port<Frame>(Frame.class, "in", Port.DIR_TYPE_IN));
        addPort(new Port<Frame>(Frame.class, "out", Port.DIR_TYPE_OUT));
    }
    
    public void task() throws InterruptedException, Exception
    {
        //while(true)
        //{
        SacreLib.logger.fine("trying to take a token");
        Frame currFrame = (Frame)port("in").take();

        // no need to check for null. no more assuming that, in Port.take(), null also means STOP/EOS. Solved the issue by constructing the stop token through reflection. New assumption is all token class should have a constructor with the type argument.
        if( currFrame.isStop())
        {
            SacreLib.logger.fine("sending STOP token");
            port("out").put(new Frame(Token.STOP));
            state = State.STOPPED; // TODO: handle in Component with output hooks? what if there are more than one output ports?
            return;
        }

        SacreLib.logger.fine("processing frame: " + currFrame);
        Frame processedFrame = processFrame(currFrame);

        SacreLib.logger.fine("sending processed frame: " + processedFrame);
        port("out").put(processedFrame);

        //} /end while
    }
    
    public Frame processFrame(Frame f) //throws InterruptedException
    {
        // process frame
        return new Frame("encoded2" + f.getData());
    }

}
