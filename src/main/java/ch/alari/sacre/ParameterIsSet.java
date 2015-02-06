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
public class ParameterIsSet implements ParameterPrecondition {

    private final ParameterDescriptor pd;
    
    public ParameterIsSet(ParameterDescriptor pd) {
        this.pd = pd;
    }

    @Override
    public boolean test() {
        return pd.isSet();
    }

    @Override
    public String toString() {
        return pd.getName() + " is set";
    }

}
