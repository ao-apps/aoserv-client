/*
 * Copyright 2002-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  DisableLog
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

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
					out.writeCompressedInt(SchemaTable.TableID.DISABLE_LOG.ordinal());
					out.writeUTF(bu.pkey.toString());
					out.writeBoolean(disableReason!=null); if(disableReason!=null) out.writeUTF(disableReason);
				}

				@Override
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

				@Override
				public Integer afterRelease() {
					connector.tablesUpdated(invalidateList);
					return result;
				}
			}
		);
	}

	@Override
	public DisableLog get(int pkey) throws IOException, SQLException {
		return getUniqueRow(DisableLog.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DISABLE_LOG;
	}
}
