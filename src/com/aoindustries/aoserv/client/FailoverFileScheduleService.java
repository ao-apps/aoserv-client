/*
 * Copyright 2003-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see FailoverFileSchedule
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.failover_file_schedule)
public interface FailoverFileScheduleService extends AOServService<Integer,FailoverFileSchedule> {

    /* TODO
    void setFailoverFileSchedules(final FailoverFileReplication ffr, final List<Short> hours, final List<Short> minutes) throws IOException, SQLException {
        if(hours.size()!=minutes.size()) throw new IllegalArgumentException("hours.size()!=minutes.size(): "+hours.size()+"!="+minutes.size());

        connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_FAILOVER_FILE_SCHEDULES.ordinal());
                    out.writeCompressedInt(ffr.getPkey());
                    int size = hours.size();
                    out.writeCompressedInt(size);
                    for(int c=0;c<size;c++) {
                        out.writeShort(hours.get(c));
                        out.writeShort(minutes.get(c));
                    }
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    connector.tablesUpdated(invalidateList);
                }
            }
        );
    }
     */
}