/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * Beginning with PostgreSQL 7.1, multiple character encoding formats are
 * supported, the <code>PostgresEncoding</code>s represent the possible
 * formats.
 *
 * @see  PostgresDatabase
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresEncoding extends AOServObjectIntegerKey implements Comparable<PostgresEncoding>, DtoFactory<com.aoindustries.aoserv.client.dto.PostgresEncoding> {

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
    private String encoding;
    final private int postgresVersion;

    public PostgresEncoding(AOServConnector connector, int pkey, String encoding, int postgresVersion) {
        super(connector, pkey);
        this.encoding = encoding;
        this.postgresVersion = postgresVersion;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        encoding = intern(encoding);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PostgresEncoding other) {
        try {
            int diff = AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(encoding, other.encoding);
            if(diff!=0) return diff;
            return postgresVersion==other.postgresVersion ? 0 : getPostgresVersion().compareTo(other.getPostgresVersion());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
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

    static final String COLUMN_POSTGRES_VERSION = "postgres_version";
    @DependencySingleton
    @SchemaColumn(order=2, name=COLUMN_POSTGRES_VERSION, index=IndexType.INDEXED, description="the version of PostgreSQL")
    public PostgresVersion getPostgresVersion() throws RemoteException {
        return getConnector().getPostgresVersions().get(postgresVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PostgresEncoding(AOServConnector connector, com.aoindustries.aoserv.client.dto.PostgresEncoding dto) {
        this(
            connector,
            dto.getPkey(),
            dto.getEncoding(),
            dto.getPostgresVersion()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PostgresEncoding getDto() {
        return new com.aoindustries.aoserv.client.dto.PostgresEncoding(key, encoding, postgresVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<PostgresDatabase> getPostgresDatabases() throws RemoteException {
    	return getConnector().getPostgresDatabases().filterIndexed(PostgresDatabase.COLUMN_ENCODING, this);
    }
    // </editor-fold>
}
