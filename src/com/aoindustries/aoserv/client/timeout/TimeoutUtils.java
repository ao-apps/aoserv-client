package com.aoindustries.aoserv.client.timeout;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author  AO Industries, Inc.
 */
final class TimeoutUtils {

    private TimeoutUtils() {
    }

    static final ExecutorService executorService = Executors.newCachedThreadPool(
        new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, TimeoutUtils.class.getName());
            }
        }
    );
}
