package ch.alari.sacre;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author onur, Faheem
 */
public class PluginManager implements ComponentFactory {

    public static String PLUGINS_PATH = "./";
    //private ArrayList<Component> plugins = new ArrayList<Component>();

    private ClassLoader loader;
    private Map<String, String> pluginsInfo;
    
    /**
     * Load plugin descriptors from the plugins info file and then load plugin
     * classes.
     *
     * @return
     */
    public void loadPlugins()
    {
        pluginsInfo = new HashMap<String, String>();
        
        // storage for plugins info
        Set<String[]> deger = (Set<String[]>) new LinkedHashSet<String[]>();
        readTextFileIntoCollection( PLUGINS_PATH + "plugins.info", deger );
        
        ArrayList<URL> urls = new ArrayList<URL>();
        File filePath = new File(PLUGINS_PATH + "jars/");
        File files[] = filePath.listFiles();
        if(files == null) // plugins folder does not contain jars folder.
            return;
        
        //Iterate over files in the plugin directory
        for (File file : files) 
        {
            if (file.isFile() && file.getAbsolutePath().endsWith(".jar")) 
            {
                // Convert File to a URL
                //URI uri = file.toURI();
                //System.out.println("uri: " + uri.toString());
                //System.out.println("file://" + PLUGINS_PATH + "jars/" + file.getName());
                URI uri = URI.create("file://" + PLUGINS_PATH + "jars/" + file.getName());
                URL url = null;
                try {
                    url = uri.toURL();
                } catch (MalformedURLException ex) {
                    Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                urls.add(url);
            } 
            else // skip folders
                continue;
        }
        
        // Create a new class loader with the directory
        loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), PluginManager.class.getClassLoader());
        
        for(String[] ss: deger)
        {
            //System.out.println("Eklenti bulundu: " + ss[0] + " == " + ss[1]);
            pluginsInfo.put(ss[0].toLowerCase(Locale.forLanguageTag("tr")), ss[1]);
            SacreLib.logger.log(Level.INFO, "Eklenti bulundu: " + ss[0] + ": " + ss[1]);
        }
        
        //add loaded plugin to plugin list
        //plugins.add((Component) cons.newInstance(fullyQualifiedName+"1", null));
    }

    public Component create(String cType, String cName, Map<String, String> params)
    {
        //System.out.println("yaratalim..." + cType);
        Component c = null;
        try {
            String className = pluginsInfo.get( cType.toLowerCase(Locale.forLanguageTag("tr")) );
            //System.out.println("className: " + className);
            if(className == null)
                return null;
            Class cls = loader.loadClass(className);
            Constructor cons = cls.getConstructor(String.class, Map.class); // Map<String, String>.class diye birsey yok (http://stackoverflow.com/questions/12100955/java-getconstructortypes-with-parametised-types)
            c = (Component)cons.newInstance(cName, params);
        } catch (InstantiationException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
        //if (c==null)
        //    System.out.println("!!!!!!!!!!!!");
        return c;
    }

    public Component create(String cType, String cName) {
        return create(cType, cName, null);
    }
    
    
    public static void readTextFileIntoCollection(String dosya, Collection<String[]> deger) 
    {
        BufferedReader br = null;

        try {
                String sCurrentLine;

                br = new BufferedReader(new FileReader(dosya));

                while ((sCurrentLine = br.readLine()) != null) 
                {
                    //System.out.println(sCurrentLine);
                    StringTokenizer st = new StringTokenizer(sCurrentLine, ":");
                    String[] satirDegerleri = new String[st.countTokens()];
                    int i = 0;
                    while (st.hasMoreElements()) 
                    {
                        //System.out.println("StringTokenizer Output: " + st.nextElement());
                        satirDegerleri[i] = ((String)st.nextElement()).trim();
                        deger.add(satirDegerleri);
                        i++;
                    }
                    //System.out.println("");
                }

        } catch (FileNotFoundException e) {
            SacreLib.logger.log(Level.FINE, dosya + " dosyası bulunamadı.");
        }
        catch (IOException e) {
            SacreLib.logger.log(Level.WARNING, dosya + " dosyasının okunması sırasında bir hata oluştu.");
            //e.printStackTrace();
        } finally {
                try {
                        if (br != null)
                            br.close();
                } catch (IOException ex) {
                        ex.printStackTrace();
                }
        }
    }    






}
