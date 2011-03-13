package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  DisableLog
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.disable_log)
public interface DisableLogService extends AOServService<Integer,DisableLog> {
    /* TODO
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
    }*/
}