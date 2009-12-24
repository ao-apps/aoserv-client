package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingLanguageService extends NoSwingServiceStringKey<Language> implements LanguageService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingLanguageService(NoSwingConnector connector, LanguageService<?,?> wrapped) {
        super(connector, Language.class, wrapped);
    }
}
