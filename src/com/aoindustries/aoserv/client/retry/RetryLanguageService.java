package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Language;
import com.aoindustries.aoserv.client.LanguageService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryLanguageService extends RetryServiceStringKey<Language> implements LanguageService<RetryConnector,RetryConnectorFactory> {

    RetryLanguageService(RetryConnector connector) {
        super(connector, Language.class);
    }
}
