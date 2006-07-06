package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;
import java.util.*;

/**
 * The default client configuration is stored in a properties resource named
 * <code>/com/aoindustries/aoserv/client/aoesrv-client.properties</code>.
 *
 * @see  AOServConnector#getConnector()
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class AOServClientConfiguration {

    private static Properties props;

    public static String getProperty(String name) throws IOException {
        Profiler.startProfile(Profiler.IO, AOServClientConfiguration.class, "getProperty(String)", null);
        try {
            if (props == null) {
                synchronized (AOServClientConfiguration.class) {
		    Properties newProps = new Properties();
                    if (props == null) {
                        InputStream in = new BufferedInputStream(AOServClientConfiguration.class.getResourceAsStream("aoserv-client.properties"));
                        try {
                            newProps.load(in);
                        } finally {
                            in.close();
                        }
                        props = newProps;
                    }
                }
            }
            return props.getProperty(name);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
