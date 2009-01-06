package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  DisableLog
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DisableLogTable extends CachedTableIntegerKey<DisableLog> {

    DisableLogTable(AOServConnector connector) {
	super(connector, DisableLog.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(DisableLog.COLUMN_TIME_name, ASCENDING),
        new OrderBy(DisableLog.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(DisableLog.COLUMN_PKEY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addDisableLog(
        Business bu,
        String disableReason
    ) {
        try {
            IntList invalidateList;
            int result;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                out.writeCompressedInt(SchemaTable.TableID.DISABLE_LOG.ordinal());
                out.writeUTF(bu.pkey);
                out.writeBoolean(disableReason!=null); if(disableReason!=null) out.writeUTF(disableReason);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    result=in.readCompressedInt();
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
            return result;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public DisableLog get(Object pkey) {
	return getUniqueRow(DisableLog.COLUMN_PKEY, pkey);
    }

    public DisableLog get(int pkey) {
	return getUniqueRow(DisableLog.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DISABLE_LOG;
    }
}