package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * A <code>MySQLReservedWord</code> cannot be used for database or
 * table names.
 *
 * @see  MySQLDatabase
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLReservedWord extends AOServObjectStringKey<MySQLReservedWord> implements BeanFactory<com.aoindustries.aoserv.client.beans.MySQLReservedWord> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public MySQLReservedWord(MySQLReservedWordService<?,?> service, String word) {
        super(service, word);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="word", index=IndexType.PRIMARY_KEY, description="the word that may not be used")
    public String getWord() {
    	return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.MySQLReservedWord getBean() {
        return new com.aoindustries.aoserv.client.beans.MySQLReservedWord(key);
    }
    // </editor-fold>
}
