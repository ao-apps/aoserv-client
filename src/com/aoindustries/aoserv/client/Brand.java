/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.AddTicketCommand;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.Email;
import com.aoindustries.aoserv.client.validator.Hostname;
import com.aoindustries.table.IndexType;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * A brand has separate website, packages, nameservers, and support.
 *
 * @see  Business
 * @see  Reseller
 *
 * @author  AO Industries, Inc.
 */
final public class Brand extends AOServObjectAccountingCodeKey<Brand> implements BeanFactory<com.aoindustries.aoserv.client.beans.Brand> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
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
        BrandService<?,?> service,
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
        super(service, accounting);
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

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=0, name=COLUMN_ACCOUNTING, index=IndexType.PRIMARY_KEY, description="the business that is a brand")
    public Business getBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().get(getKey());
    }

    @SchemaColumn(order=1, name="nameserver1", description="the primary nameserver")
    public DomainName getNameserver1() {
        return nameserver1;
    }

    @SchemaColumn(order=2, name="nameserver2", description="the secondary nameserver")
    public DomainName getNameserver2() {
        return nameserver2;
    }

    @SchemaColumn(order=3, name="nameserver3", description="the tertiary nameserver (optional)")
    public DomainName getNameserver3() {
        return nameserver3;
    }

    @SchemaColumn(order=4, name="nameserver4", description="the quaternary nameserver (optional)")
    public DomainName getNameserver4() {
        return nameserver4;
    }

    static final String COLUMN_SMTP_EMAIL_INBOX = "smtp_email_inbox";
    @SchemaColumn(order=5, name=COLUMN_SMTP_EMAIL_INBOX, index=IndexType.UNIQUE, description="the inbox used for outgoing email")
    public EmailInbox getSmtpEmailInbox() throws RemoteException {
        return getService().getConnector().getEmailInboxes().get(smtpEmailInbox);
    }

    /**
     * Gets the host that should be used for SMTP.  Will use the hostname
     * of the SmtpEmailInbox's AOServer if smtp_host is null.
     */
    @SchemaColumn(order=6, name="smtp_host", description="the host used for outgoing email")
    public Hostname getSmtpHost() throws RemoteException {
        return smtpHost!=null ? smtpHost : Hostname.valueOf(getSmtpEmailInbox().getLinuxAccount().getAoServerResource().getAoServer().getHostname());
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=7, name="smtp_password", description="the password used for outgoing email")
    public String getSmtpPassword() {
        return smtpPassword;
    }

    static final String COLUMN_IMAP_EMAIL_INBOX = "imap_email_inbox";
    @SchemaColumn(order=8, name=COLUMN_IMAP_EMAIL_INBOX, index=IndexType.UNIQUE, description="the inbox used for incoming email")
    public EmailInbox getImapEmailInbox() throws RemoteException {
        return getService().getConnector().getEmailInboxes().get(imapEmailInbox);
    }

    /**
     * Gets the host that should be used for IMAP.  Will use the hostname
     * of the ImapLinuxServerAccount's AOServer if imap_host is null.
     */
    @SchemaColumn(order=9, name="imap_host", description="the host used for incoming email")
    public Hostname getImapHost() throws RemoteException {
        return imapHost!=null ? imapHost : Hostname.valueOf(getImapEmailInbox().getLinuxAccount().getAoServerResource().getAoServer().getHostname());
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=10, name="imap_password", description="the password used for incoming email")
    public String getImapPassword() {
        return imapPassword;
    }

    /* TODO
    @SchemaColumn(order=11, name="support_email_address", index=IndexType.UNIQUE, description="the support address")
    public EmailAddress getSupportEmailAddress() throws RemoteException {
        return getService().getConnector().getEmailAddresses().get(supportEmailAddress);
    }*/

    @SchemaColumn(order=11, name="support_email_display", index=IndexType.UNIQUE, description="the support address display")
    public String getSupportEmailDisplay() {
        return supportEmailDisplay;
    }

    /* TODO
    @SchemaColumn(order=13, name="signup_email_address", index=IndexType.UNIQUE, description="the signup address")
    public EmailAddress getSignupEmailAddress() throws RemoteException {
        return getService().getConnector().getEmailAddresses().get(signupEmailAddress);
    }*/

    @SchemaColumn(order=12, name="signup_email_display", index=IndexType.UNIQUE, description="the signup address display")
    public String getSignupEmailDisplay() {
        return signupEmailDisplay;
    }

    /* TODO
    @SchemaColumn(order=15, name="ticket_encryption_from", description="the key used to encrypt ticket data")
    public EncryptionKey getTicketEncryptionFrom() throws IOException, SQLException {
        return getService().getConnector().getEncryptionKeys().get(ticketEncryptionFrom);
    }

    @SchemaColumn(order=16, name="ticket_encryption_recipient", description="the key used to decrypt ticket data")
    public EncryptionKey getTicketEncryptionRecipient() throws IOException, SQLException {
        return getService().getConnector().getEncryptionKeys().get(ticketEncryptionRecipient);
    }

    @SchemaColumn(order=17, name="signup_encryption_from", description="the key used to encrypt signup data")
    public EncryptionKey getSignupEncryptionFrom() throws IOException, SQLException {
        return getService().getConnector().getEncryptionKeys().get(signupEncryptionFrom);
    }

    @SchemaColumn(order=18, name="signup_encryption_recipient", description="the key used to decrypt signup data")
    public EncryptionKey getSignupEncryptionRecipient() throws IOException, SQLException {
        return getService().getConnector().getEncryptionKeys().get(signupEncryptionRecipient);
    }
     */

    @SchemaColumn(order=13, name="support_toll_free", description="the support toll-free number")
    public String getSupportTollFree() {
        return supportTollFree;
    }

    @SchemaColumn(order=14, name="support_day_phone", description="the support day phone number")
    public String getSupportDayPhone() {
        return supportDayPhone;
    }

    @SchemaColumn(order=15, name="support_emergency_phone1", description="the support 24-hour phone number")
    public String getSupportEmergencyPhone1() {
        return supportEmergencyPhone1;
    }

    @SchemaColumn(order=16, name="support_emergency_phone2", description="the secondary support 24-hour phone number")
    public String getSupportEmergencyPhone2() {
        return supportEmergencyPhone2;
    }

    @SchemaColumn(order=17, name="support_fax", description="")
    public String getSupportFax() {
        return supportFax;
    }

    @SchemaColumn(order=18, name="support_mailing_address1", description="the support mailing address line 1")
    public String getSupportMailingAddress1() {
        return supportMailingAddress1;
    }

    @SchemaColumn(order=19, name="support_mailing_address2", description="the support mailing address line 2")
    public String getSupportMailingAddress2() {
        return supportMailingAddress2;
    }

    @SchemaColumn(order=20, name="support_mailing_address3", description="the support mailing address line 3")
    public String getSupportMailingAddress3() {
        return supportMailingAddress3;
    }

    @SchemaColumn(order=21, name="support_mailing_address4", description="the support mailing address line 4")
    public String getSupportMailingAddress4() {
        return supportMailingAddress4;
    }

    @SchemaColumn(order=22, name="english_enabled", description="enables the English language in all support tools")
    public boolean getEnglishEnabled() {
        return englishEnabled;
    }

    @SchemaColumn(order=23, name="japanese_enabled", description="enables the Japanese language in all support tools")
    public boolean getJapaneseEnabled() {
        return japaneseEnabled;
    }

    @SchemaColumn(order=24, name="aoweb_struts_http_url_base", description="the base URL for the non-SSL aoweb-struts installation")
    public String getAowebStrutsHttpUrlBase() {
        return aowebStrutsHttpUrlBase;
    }

    @SchemaColumn(order=25, name="aoweb_struts_https_url_base", description="the base URL for the SSL aoweb-struts installation")
    public String getAowebStrutsHttpsUrlBase() {
        return aowebStrutsHttpsUrlBase;
    }

    @SchemaColumn(order=26, name="aoweb_struts_google_verify_content", description="the Google Webmaster Tools verification code")
    public String getAowebStrutsGoogleVerifyContent() {
        return aowebStrutsGoogleVerifyContent;
    }

    @SchemaColumn(order=27, name="aoweb_struts_noindex", description="indicates this site will have ROBOTS NOINDEX meta tags on aoweb-struts common code")
    public boolean getAowebStrutsNoindex() {
        return aowebStrutsNoindex;
    }

    @SchemaColumn(order=28, name="aoweb_struts_google_analytics_new_tracking_code", description="the Google Analytics tracking code")
    public String getAowebStrutsGoogleAnalyticsNewTrackingCode() {
        return aowebStrutsGoogleAnalyticsNewTrackingCode;
    }

    @SchemaColumn(order=29, name="aoweb_struts_signup_admin_address", description="the email address that will receive copies of all signups")
    public Email getAowebStrutsSignupAdminAddress() {
        return aowebStrutsSignupAdminAddress;
    }

    static final String COLUMN_AOWEB_STRUTS_VNC_BIND = "aoweb_struts_vnc_bind";
    @SchemaColumn(order=30, name=COLUMN_AOWEB_STRUTS_VNC_BIND, index=IndexType.UNIQUE, description="the port that listens for VNC connections")
    public NetBind getAowebStrutsVncBind() throws RemoteException {
        return getService().getConnector().getNetBinds().get(aowebStrutsVncBind);
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=31, name="aoweb_struts_keystore_type", description="the keystore type for native Java SSL")
    public String getAowebStrutsKeystoreType() {
        return aowebStrutsKeystoreType;
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=32, name="aoweb_struts_keystore_password", description="the keystore password for native Java SSL")
    public String getAowebStrutsKeystorePassword() {
        return aowebStrutsKeystorePassword;
    }

    static final String COLUMN_PARENT = "parent";
    @SchemaColumn(order=33, name=COLUMN_PARENT, index=IndexType.INDEXED, description="the immediate parent of this brand or <code>null</code> if none available")
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

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Brand getBean() {
        return new com.aoindustries.aoserv.client.beans.Brand(
            getBean(getKey()),
            getBean(nameserver1),
            getBean(nameserver2),
            getBean(nameserver3),
            getBean(nameserver4),
            smtpEmailInbox,
            getBean(smtpHost),
            smtpPassword,
            imapEmailInbox,
            getBean(imapHost),
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
            getBean(aowebStrutsSignupAdminAddress),
            aowebStrutsVncBind,
            aowebStrutsKeystoreType,
            aowebStrutsKeystorePassword
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBusiness(),
            getSmtpEmailInbox(),
            getImapEmailInbox(),
            // TODO: getSupportEmailAddress(),
            // TODO: getSignupEmailAddress(),
            // TODO: getTicketEncryptionFrom(),
            // TODO: getTicketEncryptionRecipient(),
            // TODO: getSignupEncryptionFrom(),
            // TODO: getSignupEncryptionRecipient(),
            getAowebStrutsVncBind()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            AOServObjectUtils.createDependencySet(
                getReseller()
            ),
            getTickets()
            // TODO: getSignupRequests(),
            // TODO: getTicketBrandCategories()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /**
     * Gets the Reseller for this Brand or <code>null</code> if not a reseller.
     */
    public Reseller getReseller() throws RemoteException {
        return getService().getConnector().getResellers().filterUnique(Reseller.COLUMN_ACCOUNTING, this);
    }

    /* TODO
    public List<TicketBrandCategory> getTicketBrandCategories() throws IOException, SQLException {
        return getService().getConnector().getTicketBrandCategories().getTicketBrandCategories(this);
    }*/

    /**
     * The children of the brand are any brands that have their closest parent
     * business (that is a brand) equal to this one.
     */
    public IndexedSet<Brand> getChildBrands() throws RemoteException {
        return getService().filterIndexed(COLUMN_PARENT, this);
    }

    public IndexedSet<Ticket> getTickets() throws RemoteException {
        return getService().getConnector().getTickets().filterIndexed(Ticket.COLUMN_BRAND, this);
    }

    /* TODO
    public List<SignupRequest> getSignupRequests() throws IOException, SQLException {
        return getService().getConnector().getSignupRequests().getIndexedRows(SignupRequest.COLUMN_BRAND, pkey);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Commands">
    public int addTicket(
        Business business,
        Language language,
        TicketCategory category,
        TicketType ticketType,
        Email fromAddress,
        String summary,
        String details,
        TicketPriority clientPriority,
        String contactEmails,
        String contactPhoneNumbers
    ) throws RemoteException {
        return new AddTicketCommand(
            getKey(),
            business==null ? null : business.getAccounting(),
            language.getKey(),
            category==null ? null : category.getKey(),
            ticketType.getKey(),
            fromAddress,
            summary,
            details,
            clientPriority.getKey(),
            contactEmails,
            contactPhoneNumbers
        ).execute(getService().getConnector());
    }
    // </editor-fold>

    public URL getAowebStrutsHttpURL() throws MalformedURLException {
        return new URL(aowebStrutsHttpUrlBase);
    }

    public URL getAowebStrutsHttpsURL() throws MalformedURLException {
        return new URL(aowebStrutsHttpsUrlBase);
    }
}
