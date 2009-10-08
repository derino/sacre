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
import ch.alari.sacre.ComponentFactory;
import java.util.Map;


/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class Case1ComponentFactory implements ComponentFactory
{
    // singleton
    private static Case1ComponentFactory instance = new Case1ComponentFactory();

    private Case1ComponentFactory()
    {
        
    }

    public static ComponentFactory instance()
    {
        return instance;
    }

    /**
     * @param cType: Command-line alias for the component
     * @param cName: identifies a component in a pipeline
     */
    public Component create(String cType, String cName)
    {
        return create(cType, cName, null);
    }
    
    public Component create(String cType, String cName, Map<String, String> params)
    {
        if(cType.equalsIgnoreCase("FrameSrc"))
            return new FrameSrc(cName, params);
        else if(cType.equalsIgnoreCase("Encoder"))
            return new Encoder(cName, params);
        else if(cType.equalsIgnoreCase("Encoder2"))
            return new Encoder2(cName, params);
        else if(cType.equalsIgnoreCase("ConsoleSink"))
            return new ConsoleSink(cName);
        else
            return null;
    }
}
