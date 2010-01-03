/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Beginning with PostgreSQL 7.1, multiple character encoding formats are
 * supported, the <code>PostgresEncoding</code>s represent the possible
 * formats.
 *
 * @see  PostgresDatabase
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresEncoding extends AOServObjectIntegerKey<PostgresEncoding> implements BeanFactory<com.aoindustries.aoserv.client.beans.PostgresEncoding> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The supported encodings.
     */
    public static final String
        ALT="ALT",                      // 7.1    7.2   7.3   8.0
        BIG5="BIG5",                    // 7.1    7.2   7.3
        EUC_CN="EUC_CN",                // 7.1    7.2   7.3   8.0
        EUC_JP="EUC_JP",                // 7.1    7.2   7.3   8.0
        EUC_KR="EUC_KR",                // 7.1    7.2   7.3   8.0
        EUC_TW="EUC_TW",                // 7.1    7.2   7.3   8.0
        GB18030="GB18030",              //              7.3
        GBK="GBK",                      //              7.3
        ISO_8859_5="ISO_8859_5",        //        7.2   7.3   8.0
        ISO_8859_6="ISO_8859_6",        //        7.2   7.3   8.0
        ISO_8859_7="ISO_8859_7",        //        7.2   7.3   8.0
        ISO_8859_8="ISO_8859_8",        //        7.2   7.3   8.0
        JOHAB="JOHAB",                  //              7.3   8.0
        KOI8="KOI8",                    // 7.1    7.2   7.3   8.0
        LATIN1="LATIN1",                // 7.1    7.2   7.3   8.0
        LATIN2="LATIN2",                // 7.1    7.2   7.3   8.0
        LATIN3="LATIN3",                // 7.1    7.2   7.3   8.0
        LATIN4="LATIN4",                // 7.1    7.2   7.3   8.0
        LATIN5="LATIN5",                // 7.1    7.2   7.3   8.0
        LATIN6="LATIN6",                //        7.2   7.3   8.0
        LATIN7="LATIN7",                //        7.2   7.3   8.0
        LATIN8="LATIN8",                //        7.2   7.3   8.0
        LATIN9="LATIN9",                //        7.2   7.3   8.0
        LATIN10="LATIN10",              //        7.2   7.3   8.0
        MULE_INTERNAL="MULE_INTERNAL",  // 7.1    7.2   7.3   8.0
        SJIS="SJIS",                    // 7.1    7.2   7.3
        SQL_ASCII="SQL_ASCII",          // 7.1    7.2   7.3   8.0
        TCVN="TCVN",                    //              7.3   8.0
        UHC="UHC",                      //              7.3
        UNICODE="UNICODE",              // 7.1    7.2   7.3   8.0
        WIN="WIN",                      // 7.1    7.2   7.3   8.0
        WIN874="WIN874",                //              7.3   8.0
        WIN1250="WIN1250",              // 7.1    7.2   7.3   8.0
        WIN1256="WIN1256"               //              7.3   8.0
    ;

    /**
     * The PostgreSQL default encoding is SQL_ASCII.
     */
    public static final String DEFAULT_ENCODING=SQL_ASCII;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String encoding;
    final int postgresVersion;

    public PostgresEncoding(PostgresEncodingService<?,?> service, int pkey, String encoding, int postgresVersion) {
        super(service, pkey);
        this.encoding = encoding.intern();
        this.postgresVersion = postgresVersion;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(PostgresEncoding other) throws RemoteException {
        int diff = compareIgnoreCaseConsistentWithEquals(encoding, other.encoding);
        if(diff!=0) return diff;
        return postgresVersion==other.postgresVersion ? 0 : getPostgresVersion().compareTo(other.getPostgresVersion());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated unique key")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(order=1, name="encoding", description="the name of the encoding")
    public String getEncoding() {
        return encoding;
    }

    @SchemaColumn(order=2, name="postgres_version", description="the version of PostgreSQL")
    public PostgresVersion getPostgresVersion() throws RemoteException {
        return getService().getConnector().getPostgresVersions().get(postgresVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.PostgresEncoding getBean() {
        return new com.aoindustries.aoserv.client.beans.PostgresEncoding(key, encoding, postgresVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getPostgresVersion()
        );
    }
    // </editor-fold>
}
