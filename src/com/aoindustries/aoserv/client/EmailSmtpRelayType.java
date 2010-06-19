/*
 * Copyright 2003-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * The <code>EmailSmtpRelayType</code> of an <code>EmailSmtpRelay</code>
 * controls the servers response.
 *
 * @see  EmailSmtpRelay
 *
 * @author  AO Industries, Inc.
 */
final public class EmailSmtpRelayType extends AOServObjectStringKey<EmailSmtpRelayType> implements BeanFactory<com.aoindustries.aoserv.client.beans.EmailSmtpRelayType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The different relay types.
     */
    public static final String
        ALLOW="allow",
        ALLOW_RELAY="allow_relay",
        DENY_SPAM="deny_spam",
        DENY="deny"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String sendmailConfig;

    public EmailSmtpRelayType(EmailSmtpRelayTypeService<?,?> service, String name, String sendmailConfig) {
        super(service, name);
        this.sendmailConfig = sendmailConfig;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        sendmailConfig = intern(sendmailConfig);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the name of the type")
    public String getName() {
        return getKey();
    }
    
    @SchemaColumn(order=1, name="sendmail_config", description="the config value used for sendmail")
    public String getSendmailConfig() {
        return sendmailConfig;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.EmailSmtpRelayType getBean() {
        return new com.aoindustries.aoserv.client.beans.EmailSmtpRelayType(getKey(), sendmailConfig);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getEmailSmtpRelays());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public IndexedSet<EmailSmtpRelay> getEmailSmtpRelays() throws RemoteException {
        return getService().getConnector().getTicketCategories().filterIndexed(COLUMN_PARENT, this);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public String getVerb() throws SQLException {
        if(pkey==ALLOW) return "allowed regular access"; // OK - interned
        if(pkey==ALLOW_RELAY) return "allowed unauthenticated relay access"; // OK - interned
        if(pkey==DENY_SPAM) return "blocked for sending unsolicited bulk email"; // OK - interned
        if(pkey==DENY) return "blocked"; // OK - interned
        throw new SQLException("Unknown value for name: "+pkey);
    }
     */
    // </editor-fold>
}