package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectAccountingCodeKey;
import com.aoindustries.aoserv.client.AOServServiceAccountingCodeKey;
import com.aoindustries.aoserv.client.validator.AccountingCode;

/**
 * @author  AO Industries, Inc.
 */
abstract class RetryServiceAccountingCodeKey<V extends AOServObjectAccountingCodeKey<V>> extends RetryService<AccountingCode,V> implements AOServServiceAccountingCodeKey<RetryConnector,RetryConnectorFactory,V> {

    RetryServiceAccountingCodeKey(RetryConnector connector, Class<V> valueClass) {
        super(connector, AccountingCode.class, valueClass);
    }
}
