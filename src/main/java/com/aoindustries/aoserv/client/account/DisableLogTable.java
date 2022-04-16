/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.account;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  DisableLog
 *
 * @author  AO Industries, Inc.
 */
public final class DisableLogTable extends CachedTableIntegerKey<DisableLog> {

	DisableLogTable(AOServConnector connector) {
		super(connector, DisableLog.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(DisableLog.COLUMN_TIME_name, ASCENDING),
		new OrderBy(DisableLog.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(DisableLog.COLUMN_PKEY_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addDisableLog(
		final Account bu,
		final String disableReason
	) throws IOException, SQLException {
		return connector.requestResult(
			true,
			AoservProtocol.CommandID.ADD,
			// Java 9: new AOServConnector.ResultRequest<>
			new AOServConnector.ResultRequest<Integer>() {
				private IntList invalidateList;
				private int result;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(Table.TableID.DISABLE_LOG.ordinal());
					out.writeUTF(bu.getName().toString());
					out.writeBoolean(disableReason!=null); if(disableReason!=null) out.writeUTF(disableReason);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						result=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
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
	public Table.TableID getTableID() {
		return Table.TableID.DISABLE_LOG;
	}
}
