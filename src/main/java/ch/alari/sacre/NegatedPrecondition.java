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

    @Override
    public String toString() {
        return "!(" + pp + ")";
    }
}
