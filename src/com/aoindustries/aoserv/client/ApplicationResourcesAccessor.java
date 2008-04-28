package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.Locale;

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

    private static final com.aoindustries.util.ApplicationResourcesAccessor accessor = new com.aoindustries.util.ApplicationResourcesAccessor("com.aoindustries.aoserv.client.ApplicationResources");

    public static String getMessage(Locale locale, String key) {
        return accessor.getMessage(locale, key);
    }
    
    public static String getMessage(Locale locale, String key, Object... args) {
        return accessor.getMessage(locale, key, args);
    }
}
