package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import com.aoindustries.util.sort.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  PackageDefinition
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinitionTable extends CachedTableIntegerKey<PackageDefinition> {

    PackageDefinitionTable(AOServConnector connector) {
	super(connector, PackageDefinition.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(PackageDefinition.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(PackageDefinition.COLUMN_CATEGORY_name, ASCENDING),
        new OrderBy(PackageDefinition.COLUMN_MONTHLY_RATE_name, ASCENDING),
        new OrderBy(PackageDefinition.COLUMN_NAME_name, ASCENDING),
        new OrderBy(PackageDefinition.COLUMN_VERSION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addPackageDefinition(
        Business business,
        PackageCategory category,
        String name,
        String version,
        String display,
        String description,
        int setupFee,
        TransactionType setupFeeTransactionType,
        int monthlyRate,
        TransactionType monthlyRateTransactionType
    ) {
        try {
            int pkey;
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                out.writeCompressedInt(SchemaTable.TableID.PACKAGE_DEFINITIONS.ordinal());
                out.writeUTF(business.pkey);
                out.writeUTF(category.pkey);
                out.writeUTF(name);
                out.writeUTF(version);
                out.writeUTF(display);
                out.writeUTF(description);
                out.writeCompressedInt(setupFee);
                out.writeBoolean(setupFeeTransactionType!=null);
                if(setupFeeTransactionType!=null) out.writeUTF(setupFeeTransactionType.pkey);
                out.writeCompressedInt(monthlyRate);
                out.writeUTF(monthlyRateTransactionType.pkey);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    pkey=in.readCompressedInt();
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unknown response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
            return pkey;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public PackageDefinition get(Object pkey) {
	return getUniqueRow(PackageDefinition.COLUMN_PKEY, pkey);
    }

    public PackageDefinition get(int pkey) {
	return getUniqueRow(PackageDefinition.COLUMN_PKEY, pkey);
    }

    PackageDefinition getPackageDefinition(Business business, PackageCategory category, String name, String version) {
        String accounting=business.pkey;
        String categoryName=category.pkey;
        List<PackageDefinition> pds=getRows();
        int size=pds.size();
        for(int c=0;c<size;c++) {
            PackageDefinition pd=pds.get(c);
            if(
                pd.accounting.equals(accounting)
                && pd.category.equals(categoryName)
                && pd.name.equals(name)
                && pd.version.equals(version)
            ) return pd;
        }
        return null;
    }

    List<PackageDefinition> getPackageDefinitions(Business business, PackageCategory category) {
	String accounting=business.pkey;
        String categoryName=category.pkey;

	List<PackageDefinition> cached=getRows();
	List<PackageDefinition> matches=new ArrayList<PackageDefinition>();
	int size=cached.size();
	for(int c=0;c<size;c++) {
            PackageDefinition pd=cached.get(c);
            if(
                pd.accounting.equals(accounting)
                && pd.category.equals(categoryName)
            ) matches.add(pd);
	}
	return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PACKAGE_DEFINITIONS;
    }
}
