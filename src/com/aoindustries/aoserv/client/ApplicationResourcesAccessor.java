package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.StringUtility;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides a simplified interface for obtaining localized values from the ApplicationResources.properties files.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
final class ApplicationResourcesAccessor {

    /**
     * Make no instances.
     */
    private ApplicationResourcesAccessor() {
    }
    
    public static String getMessage(Locale locale, String key) {
        String message = getString(locale, key);
        return message;
    }
    
    public static String getMessage(Locale locale, String key, Object arg0) {
        String message = getString(locale, key);
        message = StringUtility.replace(message, "{0}", arg0==null ? "null" : arg0.toString());
        return message;
    }

    /**
     * Cache for resource lookups.
     */
    private static final Map<Locale,Map<String,String>> cache = new HashMap<Locale,Map<String,String>>();

    /**
     * Looks for a match, caches results.
     */
    private static String getString(Locale locale, String key) {
        synchronized(cache) {
            // Find the locale-specific cache
            Map<String,String> localeMap = cache.get(locale);
            if(localeMap==null) cache.put(locale, localeMap = new HashMap<String,String>());

            // Look in the cache
            String string = localeMap.get(key);
            if(string==null) {
                try {
                    ResourceBundle applicationResources = ResourceBundle.getBundle("com.aoindustries.aoserv.client.ApplicationResources", locale);
                    string = applicationResources.getString(key);
                } catch(MissingResourceException err) {
                    // string remains null
                }

                // Default to struts-style ??? formatting
                if(string==null) string="???"+locale.toString()+"."+key+"???";

                // Add to cache
                localeMap.put(key, string);
            }
            return string;
        }
    }
}
