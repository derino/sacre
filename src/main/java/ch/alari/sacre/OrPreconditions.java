/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.alari.sacre;

/**
 *
 * @author onur
 */
public class OrPreconditions implements ParameterPrecondition {

    private final ParameterPrecondition[] pps;

    public OrPreconditions(ParameterPrecondition... pps) {
        this.pps = pps;
    }

    @Override
    public boolean test() 
    {
        boolean res = false;
        for(ParameterPrecondition pp: pps)
            res |= pp.test();
        
        return res;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("at least one of the following: ");
        for(ParameterPrecondition pp: pps)
            res.append(pp).append(", ");
        res.delete(res.lastIndexOf(","), res.length()); // remove last comma and space
        return res.toString();
    }
}
