/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

/**
 * A brand has separate website, packages, nameservers, and support.
 *
 * @see  Business
 * @see  Reseller
 *
 * @author  AO Industries, Inc.
 */
final public class Brand extends AOServObjectAccountingCodeKey implements Comparable<Brand>, DtoFactory<com.aoindustries.aoserv.client.dto.Brand> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    // TODO: private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private DomainName nameserver1;
    private DomainName nameserver2;
    private DomainName nameserver3;
    private DomainName nameserver4;
    final private int smtpEmailInbox;
    private Hostname smtpHost;
    private String smtpPassword;
    final private int imapEmailInbox;
    private Hostname imapHost;
    private String imapPassword;
    final private int supportEmailAddress;
    private String supportEmailDisplay;
    final private int signupEmailAddress;
    private String signupEmailDisplay;
    final private int ticketEncryptionFrom;
    final private int ticketEncryptionRecipient;
    final private int signupEncryptionFrom;
    final private int signupEncryptionRecipient;
    private String supportTollFree;
    private String supportDayPhone;
    private String supportEmergencyPhone1;
    private String supportEmergencyPhone2;
    private String supportFax;
    private String supportMailingAddress1;
    private String supportMailingAddress2;
    private String supportMailingAddress3;
    private String supportMailingAddress4;
    final private boolean englishEnabled;
    final private boolean japaneseEnabled;
    private String aowebStrutsHttpUrlBase;
    private String aowebStrutsHttpsUrlBase;
    private String aowebStrutsGoogleVerifyContent;
    final private boolean aowebStrutsNoindex;
    private String aowebStrutsGoogleAnalyticsNewTrackingCode;
    private Email aowebStrutsSignupAdminAddress;
    final private int aowebStrutsVncBind;
    private String aowebStrutsKeystoreType;
    private String aowebStrutsKeystorePassword;

    public Brand(
        AOServConnector connector,
        AccountingCode accounting,
        DomainName nameserver1,
        DomainName nameserver2,
        DomainName nameserver3,
        DomainName nameserver4,
        int smtpEmailInbox,
        Hostname smtpHost,
        String smtpPassword,
        int imapEmailInbox,
        Hostname imapHost,
        String imapPassword,
        int supportEmailAddress,
        String supportEmailDisplay,
        int signupEmailAddress,
        String signupEmailDisplay,
        int ticketEncryptionFrom,
        int ticketEncryptionRecipient,
        int signupEncryptionFrom,
        int signupEncryptionRecipient,
        String supportTollFree,
        String supportDayPhone,
        String supportEmergencyPhone1,
        String supportEmergencyPhone2,
        String supportFax,
        String supportMailingAddress1,
        String supportMailingAddress2,
        String supportMailingAddress3,
        String supportMailingAddress4,
        boolean englishEnabled,
        boolean japaneseEnabled,
        String aowebStrutsHttpUrlBase,
        String aowebStrutsHttpsUrlBase,
        String aowebStrutsGoogleVerifyContent,
        boolean aowebStrutsNoindex,
        String aowebStrutsGoogleAnalyticsNewTrackingCode,
        Email aowebStrutsSignupAdminAddress,
        int aowebStrutsVncBind,
        String aowebStrutsKeystoreType,
        String aowebStrutsKeystorePassword
    ) {
        super(connector, accounting);
        this.nameserver1 = nameserver1;
        this.nameserver2 = nameserver2;
        this.nameserver3 = nameserver3;
        this.nameserver4 = nameserver4;
        this.smtpEmailInbox = smtpEmailInbox;
        this.smtpHost = smtpHost;
        this.smtpPassword = smtpPassword;
        this.imapEmailInbox = imapEmailInbox;
        this.imapHost = imapHost;
        this.imapPassword = imapPassword;
        this.supportEmailAddress = supportEmailAddress;
        this.supportEmailDisplay = supportEmailDisplay;
        this.signupEmailAddress = signupEmailAddress;
        this.signupEmailDisplay = signupEmailDisplay;
        this.ticketEncryptionFrom = ticketEncryptionFrom;
        this.ticketEncryptionRecipient = ticketEncryptionRecipient;
        this.signupEncryptionFrom = signupEncryptionFrom;
        this.signupEncryptionRecipient = signupEncryptionRecipient;
        this.supportTollFree = supportTollFree;
        this.supportDayPhone = supportDayPhone;
        this.supportEmergencyPhone1 = supportEmergencyPhone1;
        this.supportEmergencyPhone2 = supportEmergencyPhone2;
        this.supportFax = supportFax;
        this.supportMailingAddress1 = supportMailingAddress1;
        this.supportMailingAddress2 = supportMailingAddress2;
        this.supportMailingAddress3 = supportMailingAddress3;
        this.supportMailingAddress4 = supportMailingAddress4;
        this.englishEnabled = englishEnabled;
        this.japaneseEnabled = japaneseEnabled;
        this.aowebStrutsHttpUrlBase = aowebStrutsHttpUrlBase;
        this.aowebStrutsHttpsUrlBase = aowebStrutsHttpsUrlBase;
        this.aowebStrutsGoogleVerifyContent = aowebStrutsGoogleVerifyContent;
        this.aowebStrutsNoindex = aowebStrutsNoindex;
        this.aowebStrutsGoogleAnalyticsNewTrackingCode = aowebStrutsGoogleAnalyticsNewTrackingCode;
        this.aowebStrutsSignupAdminAddress = aowebStrutsSignupAdminAddress;
        this.aowebStrutsVncBind = aowebStrutsVncBind;
        this.aowebStrutsKeystoreType = aowebStrutsKeystoreType;
        this.aowebStrutsKeystorePassword = aowebStrutsKeystorePassword;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        nameserver1 = intern(nameserver1);
        nameserver2 = intern(nameserver2);
        nameserver3 = intern(nameserver3);
        nameserver4 = intern(nameserver4);
        smtpHost = intern(smtpHost);
        smtpPassword = intern(smtpPassword);
        imapHost = intern(imapHost);
        imapPassword = intern(imapPassword);
        supportEmailDisplay = intern(supportEmailDisplay);
        signupEmailDisplay = intern(signupEmailDisplay);
        supportTollFree = intern(supportTollFree);
        supportDayPhone = intern(supportDayPhone);
        supportEmergencyPhone1 = intern(supportEmergencyPhone1);
        supportEmergencyPhone2 = intern(supportEmergencyPhone2);
        supportFax = intern(supportFax);
        supportMailingAddress1 = intern(supportMailingAddress1);
        supportMailingAddress2 = intern(supportMailingAddress2);
        supportMailingAddress3 = intern(supportMailingAddress3);
        supportMailingAddress4 = intern(supportMailingAddress4);
        aowebStrutsHttpUrlBase = intern(aowebStrutsHttpUrlBase);
        aowebStrutsHttpsUrlBase = intern(aowebStrutsHttpsUrlBase);
        aowebStrutsGoogleVerifyContent = intern(aowebStrutsGoogleVerifyContent);
        aowebStrutsGoogleAnalyticsNewTrackingCode = intern(aowebStrutsGoogleAnalyticsNewTrackingCode);
        aowebStrutsSignupAdminAddress = intern(aowebStrutsSignupAdminAddress);
        aowebStrutsKeystoreType = intern(aowebStrutsKeystoreType);
        aowebStrutsKeystorePassword = intern(aowebStrutsKeystorePassword);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(Brand other) {
        return getKey().compareTo(other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_BUSINESS = getMethodColumn(Brand.class, "business");
    @DependencySingleton
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the business that is a brand")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(getKey());
    }

    @SchemaColumn(order=1, description="the primary nameserver")
    public DomainName getNameserver1() {
        return nameserver1;
    }

    @SchemaColumn(order=2, description="the secondary nameserver")
    public DomainName getNameserver2() {
        return nameserver2;
    }

    @SchemaColumn(order=3, description="the tertiary nameserver (optional)")
    public DomainName getNameserver3() {
        return nameserver3;
    }

    @SchemaColumn(order=4, description="the quaternary nameserver (optional)")
    public DomainName getNameserver4() {
        return nameserver4;
    }

    public static final MethodColumn COLUMN_SMTP_EMAIL_INBOX = getMethodColumn(Brand.class, "smtpEmailInbox");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=5, index=IndexType.UNIQUE, description="the inbox used for outgoing email")
    public EmailInbox getSmtpEmailInbox() throws RemoteException {
        try {
            return getConnector().getEmailInboxes().get(smtpEmailInbox);
        } catch(NoSuchElementException err) {
            return null;
        }
    }

    /**
     * Gets the host that should be used for SMTP.  Will use the hostname
     * of the SmtpEmailInbox's AOServer if smtp_host is null.
     */
    @SchemaColumn(order=6, description="the host used for outgoing email")
    public Hostname getSmtpHost() throws RemoteException {
        return smtpHost!=null ? smtpHost : Hostname.valueOf(getSmtpEmailInbox().getLinuxAccount().getAoServer().getHostname());
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=7, description="the password used for outgoing email")
    public String getSmtpPassword() {
        return smtpPassword;
    }

    public static final MethodColumn COLUMN_IMAP_EMAIL_INBOX = getMethodColumn(Brand.class, "imapEmailInbox");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=8, index=IndexType.UNIQUE, description="the inbox used for incoming email")
    public EmailInbox getImapEmailInbox() throws RemoteException {
        try {
            return getConnector().getEmailInboxes().get(imapEmailInbox);
        } catch(NoSuchElementException err) {
            return null;
        }
    }

    /**
     * Gets the host that should be used for IMAP.  Will use the hostname
     * of the ImapLinuxServerAccount's AOServer if imap_host is null.
     */
    @SchemaColumn(order=9, description="the host used for incoming email")
    public Hostname getImapHost() throws RemoteException {
        return imapHost!=null ? imapHost : Hostname.valueOf(getImapEmailInbox().getLinuxAccount().getAoServer().getHostname());
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=10, description="the password used for incoming email")
    public String getImapPassword() {
        return imapPassword;
    }

    /* TODO
    @DependencySingleton
    @SchemaColumn(order=11, index=IndexType.UNIQUE, description="the support address")
    public EmailAddress getSupportEmailAddress() throws RemoteException {
        return getConnector().getEmailAddresses().get(supportEmailAddress);
    }*/

    @SchemaColumn(order=11, index=IndexType.UNIQUE, description="the support address display")
    public String getSupportEmailDisplay() {
        return supportEmailDisplay;
    }

    /* TODO
    @DependencySingleton
    @SchemaColumn(order=13, index=IndexType.UNIQUE, description="the signup address")
    public EmailAddress getSignupEmailAddress() throws RemoteException {
        return getConnector().getEmailAddresses().get(signupEmailAddress);
    }*/

    @SchemaColumn(order=12, index=IndexType.UNIQUE, description="the signup address display")
    public String getSignupEmailDisplay() {
        return signupEmailDisplay;
    }

    /* TODO
    @DependencySingleton
    @SchemaColumn(order=15, description="the key used to encrypt ticket data")
    public EncryptionKey getTicketEncryptionFrom() throws IOException, SQLException {
        return getConnector().getEncryptionKeys().get(ticketEncryptionFrom);
    }

    @DependencySingleton
    @SchemaColumn(order=16, description="the key used to decrypt ticket data")
    public EncryptionKey getTicketEncryptionRecipient() throws IOException, SQLException {
        return getConnector().getEncryptionKeys().get(ticketEncryptionRecipient);
    }

    @DependencySingleton
    @SchemaColumn(order=17, description="the key used to encrypt signup data")
    public EncryptionKey getSignupEncryptionFrom() throws IOException, SQLException {
        return getConnector().getEncryptionKeys().get(signupEncryptionFrom);
    }

    @DependencySingleton
    @SchemaColumn(order=18, description="the key used to decrypt signup data")
    public EncryptionKey getSignupEncryptionRecipient() throws IOException, SQLException {
        return getConnector().getEncryptionKeys().get(signupEncryptionRecipient);
    }
     */

    @SchemaColumn(order=13, description="the support toll-free number")
    public String getSupportTollFree() {
        return supportTollFree;
    }

    @SchemaColumn(order=14, description="the support day phone number")
    public String getSupportDayPhone() {
        return supportDayPhone;
    }

    @SchemaColumn(order=15, description="the support 24-hour phone number")
    public String getSupportEmergencyPhone1() {
        return supportEmergencyPhone1;
    }

    @SchemaColumn(order=16, description="the secondary support 24-hour phone number")
    public String getSupportEmergencyPhone2() {
        return supportEmergencyPhone2;
    }

    @SchemaColumn(order=17, description="")
    public String getSupportFax() {
        return supportFax;
    }

    @SchemaColumn(order=18, description="the support mailing address line 1")
    public String getSupportMailingAddress1() {
        return supportMailingAddress1;
    }

    @SchemaColumn(order=19, description="the support mailing address line 2")
    public String getSupportMailingAddress2() {
        return supportMailingAddress2;
    }

    @SchemaColumn(order=20, description="the support mailing address line 3")
    public String getSupportMailingAddress3() {
        return supportMailingAddress3;
    }

    @SchemaColumn(order=21, description="the support mailing address line 4")
    public String getSupportMailingAddress4() {
        return supportMailingAddress4;
    }

    @SchemaColumn(order=22, description="enables the English language in all support tools")
    public boolean isEnglishEnabled() {
        return englishEnabled;
    }

    @SchemaColumn(order=23, description="enables the Japanese language in all support tools")
    public boolean isJapaneseEnabled() {
        return japaneseEnabled;
    }

    @SchemaColumn(order=24, description="the base URL for the non-SSL aoweb-struts installation")
    public String getAowebStrutsHttpUrlBase() {
        return aowebStrutsHttpUrlBase;
    }

    @SchemaColumn(order=25, description="the base URL for the SSL aoweb-struts installation")
    public String getAowebStrutsHttpsUrlBase() {
        return aowebStrutsHttpsUrlBase;
    }

    @SchemaColumn(order=26, description="the Google Webmaster Tools verification code")
    public String getAowebStrutsGoogleVerifyContent() {
        return aowebStrutsGoogleVerifyContent;
    }

    @SchemaColumn(order=27, description="indicates this site will have ROBOTS NOINDEX meta tags on aoweb-struts common code")
    public boolean getAowebStrutsNoindex() {
        return aowebStrutsNoindex;
    }

    @SchemaColumn(order=28, description="the Google Analytics tracking code")
    public String getAowebStrutsGoogleAnalyticsNewTrackingCode() {
        return aowebStrutsGoogleAnalyticsNewTrackingCode;
    }

    @SchemaColumn(order=29, description="the email address that will receive copies of all signups")
    public Email getAowebStrutsSignupAdminAddress() {
        return aowebStrutsSignupAdminAddress;
    }

    public static final MethodColumn COLUMN_AOWEB_STRUTS_VNC_BIND = getMethodColumn(Brand.class, "aowebStrutsVncBind");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=30, index=IndexType.UNIQUE, description="the port that listens for VNC connections")
    public NetBind getAowebStrutsVncBind() throws RemoteException {
        try {
            return getConnector().getNetBinds().get(aowebStrutsVncBind);
        } catch(NoSuchElementException err) {
            return null;
        }
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=31, description="the keystore type for native Java SSL")
    public String getAowebStrutsKeystoreType() {
        return aowebStrutsKeystoreType;
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=32, description="the keystore password for native Java SSL")
    public String getAowebStrutsKeystorePassword() {
        return aowebStrutsKeystorePassword;
    }

    public static final MethodColumn COLUMN_PARENT_BRAND = getMethodColumn(Brand.class, "parentBrand");
    @SchemaColumn(order=33, index=IndexType.INDEXED, description="the immediate parent of this brand or <code>null</code> if none available")
    public Brand getParentBrand() throws RemoteException {
        Business bu = getBusiness();
        if(bu==null) return null;
        Business parent = bu.getParentBusiness();
        while(parent!=null) {
            Brand parentBrand = parent.getBrand();
            if(parentBrand!=null) return parentBrand;
        }
        return null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public Brand(AOServConnector connector, com.aoindustries.aoserv.client.dto.Brand dto) throws ValidationException {
        this(
            connector,
            getAccountingCode(dto.getAccounting()),
            getDomainName(dto.getNameserver1()),
            getDomainName(dto.getNameserver2()),
            getDomainName(dto.getNameserver3()),
            getDomainName(dto.getNameserver4()),
            dto.getSmtpEmailInbox(),
            getHostname(dto.getSmtpHost()),
            dto.getSmtpPassword(),
            dto.getImapEmailInbox(),
            getHostname(dto.getImapHost()),
            dto.getImapPassword(),
            dto.getSupportEmailAddress(),
            dto.getSupportEmailDisplay(),
            dto.getSignupEmailAddress(),
            dto.getSignupEmailDisplay(),
            dto.getTicketEncryptionFrom(),
            dto.getTicketEncryptionRecipient(),
            dto.getSignupEncryptionFrom(),
            dto.getSignupEncryptionRecipient(),
            dto.getSupportTollFree(),
            dto.getSupportDayPhone(),
            dto.getSupportEmergencyPhone1(),
            dto.getSupportEmergencyPhone2(),
            dto.getSupportFax(),
            dto.getSupportMailingAddress1(),
            dto.getSupportMailingAddress2(),
            dto.getSupportMailingAddress3(),
            dto.getSupportMailingAddress4(),
            dto.isEnglishEnabled(),
            dto.isJapaneseEnabled(),
            dto.getAowebStrutsHttpUrlBase(),
            dto.getAowebStrutsHttpsUrlBase(),
            dto.getAowebStrutsGoogleVerifyContent(),
            dto.isAowebStrutsNoindex(),
            dto.getAowebStrutsGoogleAnalyticsNewTrackingCode(),
            getEmail(dto.getAowebStrutsSignupAdminAddress()),
            dto.getAowebStrutsVncBind(),
            dto.getAowebStrutsKeystoreType(),
            dto.getAowebStrutsKeystorePassword()
        );
    }
    @Override
    public com.aoindustries.aoserv.client.dto.Brand getDto() {
        return new com.aoindustries.aoserv.client.dto.Brand(
            getDto(getKey()),
            getDto(nameserver1),
            getDto(nameserver2),
            getDto(nameserver3),
            getDto(nameserver4),
            smtpEmailInbox,
            getDto(smtpHost),
            smtpPassword,
            imapEmailInbox,
            getDto(imapHost),
            imapPassword,
            supportEmailAddress,
            supportEmailDisplay,
            signupEmailAddress,
            signupEmailDisplay,
            ticketEncryptionFrom,
            ticketEncryptionRecipient,
            signupEncryptionFrom,
            signupEncryptionRecipient,
            supportTollFree,
            supportDayPhone,
            supportEmergencyPhone1,
            supportEmergencyPhone2,
            supportFax,
            supportMailingAddress1,
            supportMailingAddress2,
            supportMailingAddress3,
            supportMailingAddress4,
            englishEnabled,
            japaneseEnabled,
            aowebStrutsHttpUrlBase,
            aowebStrutsHttpsUrlBase,
            aowebStrutsGoogleVerifyContent,
            aowebStrutsNoindex,
            aowebStrutsGoogleAnalyticsNewTrackingCode,
            getDto(aowebStrutsSignupAdminAddress),
            aowebStrutsVncBind,
            aowebStrutsKeystoreType,
            aowebStrutsKeystorePassword
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /**
     * Gets the Reseller for this Brand or <code>null</code> if not a reseller.
     */
    @DependentObjectSingleton
    public Reseller getReseller() throws RemoteException {
        return getConnector().getResellers().filterUnique(Reseller.COLUMN_BRAND, this);
    }

    /* TODO
    @DependentObjectSet
    public List<TicketBrandCategory> getTicketBrandCategories() throws IOException, SQLException {
        return getConnector().getTicketBrandCategories().getTicketBrandCategories(this);
    }*/

    /**
     * The children of the brand are any brands that have their closest parent
     * business (that is a brand) equal to this one.
     */
    public IndexedSet<Brand> getChildBrands() throws RemoteException {
        return getConnector().getBrands().filterIndexed(COLUMN_PARENT_BRAND, this);
    }

    @DependentObjectSet
    public IndexedSet<Ticket> getTickets() throws RemoteException {
        return getConnector().getTickets().filterIndexed(Ticket.COLUMN_BRAND, this);
    }

    /* TODO
    @DependentObjectSet
    public List<SignupRequest> getSignupRequests() throws IOException, SQLException {
        return getConnector().getSignupRequests().getIndexedRows(SignupRequest.COLUMN_BRAND, pkey);
    }
     */
    // </editor-fold>

    public URL getAowebStrutsHttpURL() throws MalformedURLException {
        return new URL(aowebStrutsHttpUrlBase);
    }

    public URL getAowebStrutsHttpsURL() throws MalformedURLException {
        return new URL(aowebStrutsHttpsUrlBase);
    }
}
