/*
 * Copyright 2003-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;

/**
 * The <code>EmailSmtpRelayType</code> of an <code>EmailSmtpRelay</code>
 * controls the servers response.
 *
 * @see  EmailSmtpRelay
 *
 * @author  AO Industries, Inc.
 */
final public class EmailSmtpRelayType extends AOServObjectStringKey implements Comparable<EmailSmtpRelayType>, DtoFactory<com.aoindustries.aoserv.client.dto.EmailSmtpRelayType> {

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

    public EmailSmtpRelayType(AOServConnector connector, String name, String sendmailConfig) {
        super(connector, name);
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

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(EmailSmtpRelayType other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
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

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public EmailSmtpRelayType(AOServConnector connector, com.aoindustries.aoserv.client.dto.EmailSmtpRelayType dto) {
        this(connector, dto.getName(), dto.getSendmailConfig());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.EmailSmtpRelayType getDto() {
        return new com.aoindustries.aoserv.client.dto.EmailSmtpRelayType(getKey(), sendmailConfig);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    @DependentObjectSet
    public IndexedSet<EmailSmtpRelay> getEmailSmtpRelays() throws RemoteException {
        return getConnector().getTicketCategories().filterIndexed(COLUMN_PARENT, this);
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