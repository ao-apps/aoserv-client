package com.aoindustries.aoserv.client.cache;

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
abstract class CachedServiceAccountingCodeKey<V extends AOServObjectAccountingCodeKey<V>> extends CachedService<AccountingCode,V> implements AOServServiceAccountingCodeKey<CachedConnector,CachedConnectorFactory,V> {

    CachedServiceAccountingCodeKey(CachedConnector connector, Class<V> valueClass, AOServServiceAccountingCodeKey<?,?,V> wrapped) {
        super(connector, AccountingCode.class, valueClass, wrapped);
    }
}
