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
public class OnlyOneOfPreconditions implements ParameterPrecondition {

    private final ParameterPrecondition[] pps;

    public OnlyOneOfPreconditions(ParameterPrecondition... pps) {
        this.pps = pps;
    }

    @Override
    public boolean test() 
    {
        //boolean res = false;
        int res = 0;
        for(ParameterPrecondition pp: pps)
            res += (pp.test()?1:0);
        
        return res == 1;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("only one of the following should be true: ");
        for(ParameterPrecondition pp: pps)
            res.append(pp).append("; ");
        res.delete(res.lastIndexOf(";"), res.length()); // remove last comma and space
        return res.toString();
    }
}
