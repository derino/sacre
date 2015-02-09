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
public class NegatedPrecondition implements ParameterPrecondition {

    private ParameterPrecondition pp;

    public NegatedPrecondition(ParameterPrecondition pp) {
        this.pp = pp;
    }

    @Override
    public boolean test() {
        return !pp.test();
    }

    // ideal degil. her durumda dogru sonuc vermez. or. NOT(AND(a,b)
    @Override
    public String toString() {
        String res = pp.toString().replace(" is ", " is not ").replace("=", " is not ").replace(" is not not ", " is ");
        if(res.equals(pp.toString()))
            return "!(" + pp + ")";
        else
            return res;
    }
}
