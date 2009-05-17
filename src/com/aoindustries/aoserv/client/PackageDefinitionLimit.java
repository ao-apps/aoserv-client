package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>PackageDefinitionLimit</code> stores one limit that is part of a <code>PackageDefinition</code>.
 *
 * @see  PackageDefinition
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinitionLimit extends CachedObjectIntegerKey<PackageDefinitionLimit> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_PACKAGE_DEFINITION=1
    ;
    static final String COLUMN_PKEY_name = "pkey";
    static final String COLUMN_PACKAGE_DEFINITION_name = "package_definition";

    /**
     * Indicates a particular value is unlimited.
     */
    public static final int UNLIMITED=-1;

    int package_definition;
    String resource;
    int soft_limit;
    int hard_limit;
    int additional_rate;
    String additional_transaction_type;

    public PackageDefinitionLimit() {
    }

    public PackageDefinitionLimit(
        PackageDefinition package_definition,
        Resource resource,
        int soft_limit,
        int hard_limit,
        int additional_rate,
        TransactionType additional_transaction_type
    ) {
        this.pkey=-1;
        this.package_definition=package_definition.pkey;
        this.resource=resource.pkey;
        this.soft_limit=soft_limit;
        this.hard_limit=hard_limit;
        this.additional_rate=additional_rate;
        this.additional_transaction_type=additional_transaction_type==null ? null : additional_transaction_type.pkey;

        // The table is set from the connector of the package definition
        setTable(package_definition.table.connector.getPackageDefinitionLimits());
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_PACKAGE_DEFINITION: return Integer.valueOf(package_definition);
            case 2: return resource;
            case 3: return soft_limit==UNLIMITED ? null : Integer.valueOf(soft_limit);
            case 4: return hard_limit==UNLIMITED ? null : Integer.valueOf(hard_limit);
            case 5: return additional_rate==-1 ? null : Integer.valueOf(additional_rate);
            case 6: return additional_transaction_type;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public PackageDefinition getPackageDefinition() throws IOException, SQLException {
        PackageDefinition pd = table.connector.getPackageDefinitions().get(package_definition);
        if(pd == null) throw new SQLException("Unable to find PackageDefinition: " + package_definition);
        return pd;
    }
    
    public Resource getResource() throws SQLException {
        Resource r=table.connector.getResources().get(resource);
        if(r==null) throw new SQLException("Unable to find Resource: "+resource);
        return r;
    }

    public int getSoftLimit() {
        return soft_limit;
    }
    
    public int getHardLimit() {
        return hard_limit;
    }
    
    public int getAdditionalRate() {
        return additional_rate;
    }
    
    public TransactionType getAdditionalTransactionType() throws SQLException {
        if(additional_transaction_type==null) return null;
        TransactionType tt=table.connector.getTransactionTypes().get(additional_transaction_type);
        if(tt==null) throw new SQLException("Unable to find TransactionType: "+additional_transaction_type);
        return tt;
    }
    
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PACKAGE_DEFINITION_LIMITS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        package_definition=result.getInt(2);
        resource=result.getString(3);
        soft_limit=result.getInt(4);
        if(result.wasNull()) soft_limit=UNLIMITED;
        hard_limit=result.getInt(5);
        if(result.wasNull()) hard_limit=UNLIMITED;
        String S=result.getString(6);
        additional_rate=S==null ? -1 : SQLUtility.getPennies(S);
        additional_transaction_type=result.getString(7);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        package_definition=in.readCompressedInt();
        resource=in.readUTF().intern();
        soft_limit=in.readCompressedInt();
        hard_limit=in.readCompressedInt();
        additional_rate=in.readCompressedInt();
        additional_transaction_type=StringUtility.intern(in.readNullUTF());
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(package_definition);
        out.writeUTF(resource);
        out.writeCompressedInt(soft_limit);
        out.writeCompressedInt(hard_limit);
        out.writeCompressedInt(additional_rate);
        out.writeNullUTF(additional_transaction_type);
    }
}