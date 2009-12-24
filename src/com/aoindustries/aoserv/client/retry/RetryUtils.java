package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.security.LoginException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author  AO Industries, Inc.
 */
final class RetryUtils {

    private RetryUtils() {
    }

    /**
     * The delay for each retry attempt.
     */
    static final long[] retryAttemptDelays = {
        0,
        1,
        2,
        3,
        4,
        6,
        8,
        12,
        16,
        23,
        32,
        48,
        64,
        96,
        128,
        192,
        256,
        384,
        512,
        768,
        1024,
        1536,
        2048,
        3072
    };

    /**
     * The number of attempts that will be made when request retry is allowed.
     */
    static final int RETRY_ATTEMPTS = retryAttemptDelays.length + 1;

    /**
     * Certain errors will not be retried.
     */
    static boolean isImmediateFail(Throwable T) {
        while(T!=null) {
            String message = T.getMessage();
            if(
                (
                    (T instanceof IOException)
                    && message!=null
                    && (
                        message.equals("Connection attempted with invalid password")
                        || message.equals("Connection attempted with empty password")
                        || message.equals("Connection attempted with empty connect username")
                        || message.startsWith("Unable to find BusinessAdministrator: ")
                        || message.startsWith("Not allowed to switch users from ")
                    )
                ) || (T instanceof LoginException)
            ) return true;
            T = T.getCause();
        }
        return false;
    }

    static final ExecutorService executorService = Executors.newCachedThreadPool(
        new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, RetryUtils.class.getName());
            }
        }
    );
}
