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
public class DictionaryConverter<T> implements ParameterDescriptor.StringToValueConverter<T>
    {
        private final String[] domain;
        private final T[] range;
        
        // dict: first row has words, second row has definitions (in order to ease providing allowedParams in ParameterDescription constructor).
        public DictionaryConverter(String[] domain, T[] range)
        {
            assert domain.length == range.length;
            this.domain = domain;
            this.range = range;
        }
        
        @Override
        public T valueOf(String s) 
        {
            for(int i=0; i< domain.length; i++)
                if(s.equals(domain[i]))
                    return range[i];
            
//            for(String[] row: dict)
//            {
//                if(s.equals(row[0]))
//                    return row[1];
//            }
            return null;
        }
        
    }