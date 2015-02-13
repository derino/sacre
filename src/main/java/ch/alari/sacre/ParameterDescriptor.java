// parametre degeri olarak evet hayir'lar degisti 1 ya da 0 olacak.
// indeksknk'de tip=kanal eklendi, ona gore degismeli ishatlari
// hayvanknk'de TODO_CHECK'ler var.
// filesink detayli 0|1
// filesrc kucukharfli 0|1
// baslikknk not default for baslik
// baslikgirdileri basucu 0|1, siralama yerine yeniden-eskiye=0|1
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.alari.sacre;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author onur
 */
public class ParameterDescriptor<T> 
{
    protected String name;
    
    /**
     * implies that this parameter has to be supplied when constructing the component.
     * if true, defaultValue is not used.
     */ 
    protected boolean required;
    
    protected List<ParameterPrecondition> preconditions;
//  protected ParameterDescriptor[] conflicts;
    
    protected T value; // null value implies not set.
    
    protected T defaultValue;
    
    protected StringToValueConverter<T> converter;
    
    protected String[] allowedValues; //protected T[] allowedValues;

    private Component component;
    
    private String description;
    
    public ParameterDescriptor(Component c, String name, boolean required, List<ParameterPrecondition> preconditions, T defaultValue, StringToValueConverter<T> converter, String... allowedValues)
    {
        this.description = "no description given.";
        this.name = name;
        this.required = required;
        this.preconditions = preconditions;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.converter = converter;
        
        if(allowedValues.length == 0)
            this.allowedValues = null;
        else
            this.allowedValues = allowedValues;
        
        c.addParameterDescriptor(this);
        this.component = c; // used for printing better error messages by refering to the component type with wrong parameter.
    }
    
    // constructor without preconditions
    public ParameterDescriptor(Component c, String name, boolean required, T defaultValue, StringToValueConverter<T> converter, String... allowedValues)
    {
        this.description = "no description given.";
        this.name = name;
        this.required = required;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.converter = converter;
        
        if(allowedValues.length == 0)
            this.allowedValues = null;
        else
            this.allowedValues = allowedValues;
        
        c.addParameterDescriptor(this);
        this.component = c; // used for printing better error messages by refering to the component type with wrong parameter.
    }

    private String getDescription() {
        return description;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }
    
    public static class UnallowedParameterValueException extends Exception {

        public UnallowedParameterValueException() {
        }
    }

    public static class RequiredParameterNotSuppliedException extends Exception {

        public RequiredParameterNotSuppliedException() {
        }
    }

    public static class PreconditionsNotMetException extends Exception {

        public PreconditionsNotMetException() {
        }
    }
    
    
    
    @FunctionalInterface
    public interface StringToValueConverter<T>
    {
        // returns null if default value is to be kept (for not-required params)
        public T valueOf(String s);
    }
    
    public static StringToValueConverter<Integer> integerConverter = (str) -> (Integer.valueOf(str));
    public static StringToValueConverter<Long> longConverter = (str) -> (Long.valueOf(str));
    public static StringToValueConverter<Boolean> booleanConverter = (str) -> (str.equals("1"));
    public static StringToValueConverter<String> stringConverter = (str) -> (str);
    public static StringToValueConverter<String> urlEncodedStringConverter = (str) -> {
        String encStr = null;
        try {
            encStr = URLEncoder.encode(str, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ParameterDescriptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encStr;
    };
    public static StringToValueConverter<String[]> arrayConverter = (str) -> {
        StringTokenizer st = new StringTokenizer(str, "+");
        if(st.countTokens()==0)
            return null;
        String[] alanlar = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreElements()) 
        {
            alanlar[i] = ((String)st.nextElement()).trim().toLowerCase(Locale.forLanguageTag("tr"));
            //System.out.println("alanlar[" + i + "]: " + alanlar[i]);
            i++;
        }
        return alanlar;
    };
    
    
    
    // main method to be used to set parameter's value
    public void setValue(String valStr) throws UnallowedParameterValueException, RequiredParameterNotSuppliedException, PreconditionsNotMetException
    {
        if(preconditions != null && valStr != null)
        {
            boolean metAll = true;
            for(ParameterPrecondition p: preconditions)
            {
                if(!p.test()) {
                    SacreLib.logger.log(Level.WARNING, "To be able to use {0} parameter for {1}, it is required that {2}", new Object[]{name, component.getName(), p});
                    metAll = false;
                }
            }
            if(!metAll)
                throw new PreconditionsNotMetException();
        }
        if(required && valStr == null)
            throw new RequiredParameterNotSuppliedException();
        if(valStr != null)
        {
            boolean isValueAllowed = true;
            if(allowedValues != null)  // check if allowed
            {
                isValueAllowed = false;
                for(String s: allowedValues)
                    if(s.equals(valStr))
                        isValueAllowed = true;
                if(!isValueAllowed)
                {
                    SacreLib.logger.log(Level.WARNING, "Unallowed value for parameter: {0} (allowed values: {1})", new Object[]{name, Arrays.toString(allowedValues)});
                    throw new UnallowedParameterValueException();
                }
            }
            
            if(isValueAllowed)
            {
                try 
                {
                    T val = converter.valueOf(valStr);
                    if(val == null)
                    {
                        if(required)
                        {
                            SacreLib.logger.log(Level.WARNING, "The value of the required {0} parameter for {1} was not given properly.", new Object[]{name, component.getType()});
                            throw new UnallowedParameterValueException(); // required parameter was not properly given.
                        }
                        else //continue with default value
                            SacreLib.logger.log(Level.WARNING, "The value of the {0} parameter for {1} was not given properly. Continuing with default value: {2}", new Object[]{name, component.getType(), defaultValue});
                    }
                    _setValue( val );
                } catch(NumberFormatException nfe) {
                    if(!nfe.getMessage().equals("OD")) // "OD"nin anlami converter lambdasi sadece built-in exception atabildigi icin olusmus baska bir exception'i anlamak icin kullaniliyor. log mesaji converter'da yazilmis diye varsayiliyor. Simdilik EksiSozlukUtilities.basicDateConverter kullaniyor bunu sadece.
                        SacreLib.logger.log(Level.WARNING, "The value of {0} parameter for {1} should be a number!", new Object[]{name, component.getType()});
                    throw new UnallowedParameterValueException();
                }
            }
        }
        // else default value remains set.
    }
    
    public void _setValue(T value) throws UnallowedParameterValueException
    {
//        if(allowedValues != null)
//        {
//            boolean isValueAllowed = false;
//            for(T v: allowedValues)
//                if(v.equals(value))
//                    isValueAllowed = true;
//            if(isValueAllowed)
//                this.value = value;
//            else
//            {
//                SacreLib.logger.log(Level.WARNING, "Unallowed value for parameter: {0} (allowed values: {1})", new Object[]{name, Arrays.toString(allowedValues)});
//                throw new UnallowedParameterValueException();
//            }
//        }
//        else
//        {
            if(value == null && !required) // if the converter returns value as null, then set to, if available, default value
                this.value = defaultValue;
            else
                this.value = value;
//        }
    }
    
    public T getValue()
    {
        return value;
    }
    
    public boolean isSet()
    {
        return value != null;
    }
    
    public ParameterPrecondition parameterIsSet()
    {
        return new ParameterIsSet(this);
    }
    
    public ParameterPrecondition parameterEqualsValue(T reqVal)
    {
        return new ParameterEqualsValue(this, reqVal);
    }
    
    public String getName()
    {
        return name;
    }
    
//    public void setPreconditions(List<ParameterPrecondition> preconditions)
//    {
//        this.preconditions = preconditions;
//    }
    
    public void addPreconditions(ParameterPrecondition... preconditions)
    {
        if(this.preconditions == null)
            this.preconditions = new ArrayList<>();
        
        this.preconditions.addAll(Arrays.asList(preconditions));
    }
    
    public String toHelpString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(": ");
        
        if(required)
            sb.append("(required) ");
        else
        {
            String def;
            if(defaultValue == null)
                def = "none";
            else if(defaultValue.getClass().isArray())
                def = Arrays.toString((Object[])defaultValue);
            else if(defaultValue instanceof Calendar)
            {
                Calendar cal = ((Calendar)defaultValue);
                String gun = cal.get(Calendar.DAY_OF_MONTH) + ".";
                gun += (cal.get(Calendar.MONTH)+1) + ".";
                gun += cal.get(Calendar.YEAR);
                def = gun;
            }   
            else
                def = defaultValue.toString();
            sb.append("(default: ").append(def).append(") ");
        }
        
        sb.append(getDescription()).append(System.getProperty("line.separator"));
        
        if(allowedValues != null && allowedValues.length != 0)
        {
            sb.append("   Possible values: ").append(Arrays.toString(allowedValues)).append(System.getProperty("line.separator"));
        }
        
        if(preconditions != null && !preconditions.isEmpty())
        {
            sb.append("   Preconditions: ");
            for(ParameterPrecondition p: preconditions)
                sb.append(p).append("; ");
            sb.append(System.getProperty("line.separator"));
        }
        sb.append(System.getProperty("line.separator"));
        
        return sb.toString();
    }
    
    public String toOrgString()
    {
        StringBuilder sb = new StringBuilder();
//        sb.append("| parametre | varsayılan değeri | açıklama | alabildiği değerler | önkoşulları |");
        sb.append("| ").append(getName()).append(" ");
        
        if(required)
            sb.append("(zorunlu) | ");
        else
        {
            String def;
            if(defaultValue == null)
                def = "yok";
            else if(defaultValue.getClass().isArray())
                def = Arrays.toString((Object[])defaultValue);
            else if(defaultValue instanceof Calendar)
            {
                Calendar cal = ((Calendar)defaultValue);
                String gun = cal.get(Calendar.DAY_OF_MONTH) + ".";
                gun += (cal.get(Calendar.MONTH)+1) + ".";
                gun += cal.get(Calendar.YEAR);
                def = gun;
            }   
            else
                def = defaultValue.toString();
            sb.append("| ").append("~").append(def).append("~").append(" | ");
        }
        
        sb.append(getDescription()).append(" | ");
        
        if(allowedValues != null && allowedValues.length != 0)
        {
            sb.append(Arrays.toString(allowedValues)).append(" | ");
        }
        else
            sb.append(" | ");
        
        if(preconditions != null && !preconditions.isEmpty())
        {
            //sb.append("   Preconditions: ");
            for(ParameterPrecondition p: preconditions)
                sb.append(p).append("; ");
        }
        sb.append(" | ");
        sb.append(System.getProperty("line.separator"));
        
        return sb.toString();
    }
}
