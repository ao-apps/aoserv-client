package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.util.Locale;

/**
 * The possible backup retention values allowed in the system.
 *
 * @author  AO Industries, Inc.
 */
final public class BackupRetention extends AOServObjectShortKey<BackupRetention> implements BeanFactory<com.aoindustries.aoserv.client.beans.BackupRetention> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String display;

    public BackupRetention(BackupRetentionService<?,?> service, short days, String display) {
        super(service, days);
        this.display = display;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="days", index=IndexType.PRIMARY_KEY, description="the number of days to keep the backup data")
    public short getDays() {
        return key;
    }

    @SchemaColumn(order=1, name="display", description="the text displayed for this time increment")
    public String getDisplay() {
        return display;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.BackupRetention getBean() {
        return new com.aoindustries.aoserv.client.beans.BackupRetention(key, display);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
    	return display;
    }
    // </editor-fold>
}
