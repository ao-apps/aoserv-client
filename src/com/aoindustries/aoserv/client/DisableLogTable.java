package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import java.io.*;
import java.sql.*;

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
        final Business bu,
        final String disableReason
    ) throws IOException, SQLException {
        return connector.requestResult(
            true,
            new AOServConnector.ResultRequest<Integer>() {
                IntList invalidateList;
                int result;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.DISABLE_LOG.ordinal());
                    out.writeUTF(bu.pkey);
                    out.writeBoolean(disableReason!=null); if(disableReason!=null) out.writeUTF(disableReason);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        result=in.readCompressedInt();
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public Integer afterRelease() {
                    connector.tablesUpdated(invalidateList);
                    return result;
                }
            }
        );
    }

    public DisableLog get(int pkey) throws IOException, SQLException {
        return getUniqueRow(DisableLog.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.DISABLE_LOG;
    }
}