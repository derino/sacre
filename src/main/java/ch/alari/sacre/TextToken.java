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

import ch.alari.sacre.Token;

/**
 *
 * @author Onur Derin <oderin at users.sourceforge.net>
 */
public class TextToken extends Token
{
    
    private String text = null;
    
    public TextToken(String text)
    {
        super(Token.DATA);
        this.text = text;
    }
    
    public TextToken(int type)
    {
        super(type);
    }
    
    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
//        return "[Baslik: " + baslik + " " + baslikAdresi + "]";
        return "* " + text;
    }
    
    @Override
    public String toXMLString()
    {
        // TODO: baslikadresinde &'lar var. &amp; donustur.
        StringBuilder sb = new StringBuilder();
        sb.append("<text>").append(System.getProperty("line.separator"))
                .append(text)
                .append(System.getProperty("line.separator"))
        .append("</text>").append(System.getProperty("line.separator"));
        return sb.toString();
    }
    
    @Override
    public String toHTMLString()
    {
        // TODO: baslikadresinde &'lar var. &amp; donustur.
        StringBuilder sb = new StringBuilder();
        sb.append("<li>").append(System.getProperty("line.separator"))
                .append(text)
                .append(System.getProperty("line.separator"))
        .append("</li>").append(System.getProperty("line.separator"));
        return sb.toString();
    }


    @Override
    public boolean equals(Object b)
    {
        if(this == b)
            return true;
        else if(b == null)
            return false;
        else if(b instanceof TextToken && ((TextToken)b).getText().equals(text))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 89 * hash + (this.text != null ? this.text.hashCode() : 0);
        return hash;
    }

}
