/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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
    final private String sendmailConfig;

    public EmailSmtpRelayType(EmailSmtpRelayTypeService<?,?> service, String name, String sendmailConfig) {
        super(service, name);
        this.sendmailConfig = sendmailConfig.intern();
    }
    // </editor-fold>

    public String getName() {
        return key;
    }
    
    public String getSendmailConfig() {
        return sendmailConfig;
    }

    /* TODO
    public String getVerb() throws SQLException {
        if(pkey.equals(ALLOW)) return "allowed regular access";
        if(pkey.equals(ALLOW_RELAY)) return "allowed unauthenticated relay access";
        if(pkey.equals(DENY_SPAM)) return "blocked for sending unsolicited bulk email";
        if(pkey.equals(DENY)) return "blocked";
        throw new SQLException("Unknown value for name: "+pkey);
    }
     */
}