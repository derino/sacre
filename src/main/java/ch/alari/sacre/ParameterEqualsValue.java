package ch.alari.sacre;


/**
 *
 * @author onur
 */
public class ParameterEqualsValue<T> implements ParameterPrecondition {
        private ParameterDescriptor<T> pd;
        private T reqVal;
        
        public ParameterEqualsValue(ParameterDescriptor<T> pd, T reqVal)
        {
            this.pd = pd;
            this.reqVal = reqVal;
        }
        
        @Override
        public boolean test()
        {
            return pd.getValue().equals(reqVal);
        }
        
        @Override
        public String toString()
        {
           return pd.getName() + "=" + reqVal;
        }
    }