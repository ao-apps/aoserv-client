/*
 * Copyright 2001-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * <code>AOServTable</code> events with a delay greater
 * than zero are processed in an asynchronous
 * <code>TableEventThread</code>.
 *
 * @see  AOServTable#addTableListener(TableListener)
 *
 * @author  AO Industries, Inc.
 */
public class TableEventThread extends Thread {

    private final AOServTable table;

    public TableEventThread(AOServTable table) {
        setName("TableEventThread #" + getId() + " ("+table.getTableID()+") - "+table.getClass().getName());
        this.table=table;
        setDaemon(true);
        start();
        // System.out.println("DEBUG: Started TableEventThread: "+getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        OUTER_LOOP :
        while(true) {
            try {
                synchronized(table.eventLock) {
                    while(true) {
                        if(table.thread!=this) break OUTER_LOOP;
                        long time = System.currentTimeMillis();
                        // Run anything that should be ran, calculating the minimum sleep time
                        // for the next wait period.
                        long minTime = Long.MAX_VALUE;
                        // Get a copy to not hold lock too long
                        List<TableListenerEntry> tableListenersSnapshot;
                        synchronized(table.tableListenersLock) {
                            tableListenersSnapshot = table.tableListeners==null ? null : new ArrayList<TableListenerEntry>(table.tableListeners);
                        }
                        if(tableListenersSnapshot!=null) {
                            int size = tableListenersSnapshot.size();
                            for (int c = 0; c < size; c++) {
                                final TableListenerEntry entry = tableListenersSnapshot.get(c);
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
                                            // System.out.println("DEBUG: Started TableEventThread: run: "+getName()+" calling tableUpdated on "+entry.listener);
                                            // Run in a different thread to avoid deadlock and increase concurrency responding to table update events.
                                            AOServConnector.executorService.submit(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        entry.listener.tableUpdated(table);
                                                    }
                                                }
                                            );
                                        } else {
                                            // Remaining delay
                                            long remaining = endTime - time;
                                            if (remaining < minTime) minTime = remaining;
                                        }
                                    }
                                }
                            }
                        }
                        if(minTime==Long.MAX_VALUE) {
                            // System.out.println("DEBUG: TableEventThread: run: "+getName()+" size="+size+", waiting indefinitely");
                            table.eventLock.wait();
                        } else {
                            // System.out.println("DEBUG: TableEventThread: run: "+getName()+" size="+size+", waiting for "+minTime+" ms");
                            table.eventLock.wait(minTime);
                        }
                    }
                }
            } catch (ThreadDeath TD) {
                throw TD;
            } catch (Throwable T) {
                table.connector.logger.log(Level.SEVERE, null, T);
            }
        }
    }
}