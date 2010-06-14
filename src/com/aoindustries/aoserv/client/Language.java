package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * @author  AO Industries, Inc.
 */
final public class Language extends AOServObjectStringKey<Language> implements BeanFactory<com.aoindustries.aoserv.client.beans.Language> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        EN="en",
        JA="ja"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public Language(LanguageService<?,?> table, String code) {
        super(table, code);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="code", index=IndexType.PRIMARY_KEY, description="the language code")
    public String getCode() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.Language getBean() {
        return new com.aoindustries.aoserv.client.beans.Language(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getTickets()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("Language."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<Ticket> getTickets() throws RemoteException {
        return getService().getConnector().getTickets().filterIndexed(Ticket.COLUMN_LANGUAGE, this);
    }
    // </editor-fold>
}