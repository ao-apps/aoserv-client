package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

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
}
