package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.*;

/**
 * <code>AOServTable</code> events with a delay greater
 * than zero are processed in an asynchronous
 * <code>TableEventThread</code>.
 *
 * @see  AOServTable#addTableListener(TableListener)
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public class TableEventThread extends Thread {

    private final AOServTable table;

    public TableEventThread(AOServTable table) {
	super("TableEventThread #"+table.getTableID()+" - "+table.getClass().getName());
	this.table=table;
	setDaemon(true);
	start();
    }

    public void run() {
	while (table.thread==this) {
            try {
                synchronized (table.eventLock) {
                    if(table.thread==this) {
                        long time = System.currentTimeMillis();
                        // Run anything that should be ran, calculating the maximum sleep time
                        // for the next wait period.
                        long minTime = Long.MAX_VALUE;
                        int size = table.tableListeners.size();
                        for (int c = 0; c < size; c++) {
                            TableListenerEntry entry = (TableListenerEntry) table.tableListeners.get(c);
                            // skip immediate listeners
                            long delay = entry.delay;
                            if(delay>0) {
                                long delayStart = entry.delayStart;
                                // Is the table idle?
                                if (delayStart != -1) {
                                    // Has the system time been modified to an earlier time?
                                    if (delayStart > time) delayStart = entry.delayStart = time;
                                    long endTime = delayStart + delay;
                                    if (time >= endTime) {
                                        // Ready to run
                                        entry.delayStart = -1;
                                        entry.listener.tableUpdated(table);
                                    } else {
                                        // Remaining delay
                                        long remaining = endTime - time;
                                        if (remaining < minTime) minTime = remaining;
                                    }
                                }
                            }
                        }
                        table.eventLock.wait(minTime);
                    }
                }
            } catch (ThreadDeath TD) {
                throw TD;
            } catch (Throwable T) {
                table.connector.errorHandler.reportError(T, null);
            }
	}
    }
}