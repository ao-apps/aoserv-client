package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A brand has separate website, packages, nameservers, and support.
 *
 * @see  Business
 * @see  Reseller
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Brand extends CachedObjectStringKey<Brand> {

    static final int COLUMN_ACCOUNTING = 0;
    static final String COLUMN_ACCOUNTING_name = "accounting";

    private String nameserver1;
    private String nameserver2;
    private String nameserver3;
    private String nameserver4;
    private int smtp_linux_server_account;
    private String smtp_host;
    private String smtp_password;
    private int imap_linux_server_account;
    private String imap_host;
    private String imap_password;
    private int support_email_address;
    private String support_email_display;
    private int signup_email_address;
    private String signup_email_display;
    private int ticket_encryption_from;
    private int ticket_encryption_recipient;
    private int signup_encryption_from;
    private int signup_encryption_recipient;
    private String support_toll_free;
    private String support_day_phone;
    private String support_emergency_phone1;
    private String support_emergency_phone2;
    private String support_fax;
    private String support_mailing_address1;
    private String support_mailing_address2;
    private String support_mailing_address3;
    private String support_mailing_address4;
    private boolean english_enabled;
    private boolean japanese_enabled;
    private String aoweb_struts_http_url_base;
    private String aoweb_struts_https_url_base;
    private String aoweb_struts_google_verify_content;
    private boolean aoweb_struts_noindex;
    private String aoweb_struts_google_analytics_new_tracking_code;
    private String aoweb_struts_signup_admin_address;
    private int aoweb_struts_vnc_bind;
    private String aoweb_struts_keystore_type;
    private String aoweb_struts_keystore_password;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_ACCOUNTING : return pkey;
            case 1: return nameserver1;
            case 2: return nameserver2;
            case 3: return nameserver3;
            case 4: return nameserver4;
            case 5: return smtp_linux_server_account;
            case 6: return smtp_host;
            case 7: return smtp_password;
            case 8: return imap_linux_server_account;
            case 9: return imap_host;
            case 10: return imap_password;
            case 11: return support_email_address;
            case 12: return support_email_display;
            case 13: return signup_email_address;
            case 14: return signup_email_display;
            case 15: return ticket_encryption_from;
            case 16: return ticket_encryption_recipient;
            case 17: return signup_encryption_from;
            case 18: return signup_encryption_recipient;
            case 19: return support_toll_free;
            case 20: return support_day_phone;
            case 21: return support_emergency_phone1;
            case 22: return support_emergency_phone2;
            case 23: return support_fax;
            case 24: return support_mailing_address1;
            case 25: return support_mailing_address2;
            case 26: return support_mailing_address3;
            case 27: return support_mailing_address4;
            case 28: return english_enabled;
            case 29: return japanese_enabled;
            case 30: return aoweb_struts_http_url_base;
            case 31: return aoweb_struts_https_url_base;
            case 32: return aoweb_struts_google_verify_content;
            case 33: return aoweb_struts_noindex;
            case 34: return aoweb_struts_google_analytics_new_tracking_code;
            case 35: return aoweb_struts_signup_admin_address;
            case 36: return aoweb_struts_vnc_bind;
            case 37: return aoweb_struts_keystore_type;
            case 38: return aoweb_struts_keystore_password;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Business getBusiness() throws IOException, SQLException {
        Business bu = table.connector.getBusinesses().get(pkey);
        if(bu==null) throw new SQLException("Unable to find Business: "+pkey);
        return bu;
    }

    public String getNameserver1() {
        return nameserver1;
    }

    public String getNameserver2() {
        return nameserver2;
    }

    public String getNameserver3() {
        return nameserver3;
    }

    public String getNameserver4() {
        return nameserver4;
    }

    public LinuxServerAccount getSmtpLinuxServerAccount() throws IOException, SQLException {
        LinuxServerAccount lsa = table.connector.getLinuxServerAccounts().get(smtp_linux_server_account);
        if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+smtp_linux_server_account);
        return lsa;
    }

    /**
     * Gets the host that should be used for SMTP.  Will use the hostname
     * of the SmtpLinuxServerAccount's AOServer if smtp_host is null.
     */
    public String getSmtpHost() throws IOException, SQLException {
        return smtp_host!=null ? smtp_host : getSmtpLinuxServerAccount().getAOServer().getHostname();
    }

    public String getSmtpPassword() {
        return smtp_password;
    }

    public LinuxServerAccount getImapLinuxServerAccount() throws SQLException, IOException {
        LinuxServerAccount lsa = table.connector.getLinuxServerAccounts().get(imap_linux_server_account);
        if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+imap_linux_server_account);
        return lsa;
    }

    /**
     * Gets the host that should be used for IMAP.  Will use the hostname
     * of the ImapLinuxServerAccount's AOServer if imap_host is null.
     */
    public String getImapHost() throws IOException, SQLException {
        return imap_host!=null ? imap_host : getImapLinuxServerAccount().getAOServer().getHostname();
    }

    public String getImapPassword() {
        return imap_password;
    }

    public EmailAddress getSupportEmailAddress() throws IOException, SQLException {
        EmailAddress ea = table.connector.getEmailAddresses().get(support_email_address);
        if(ea==null) throw new SQLException("Unable to find EmailAddress: "+support_email_address);
        return ea;
    }

    public String getSupportEmailDisplay() {
        return support_email_display;
    }

    public EmailAddress getSignupEmailAddress() throws IOException, SQLException {
        EmailAddress ea = table.connector.getEmailAddresses().get(signup_email_address);
        if(ea==null) throw new SQLException("Unable to find EmailAddress: "+signup_email_address);
        return ea;
    }

    public String getSignupEmailDisplay() {
        return signup_email_display;
    }

    /**
     * May be filtered.
     */
    public EncryptionKey getTicketEncryptionFrom() throws IOException, SQLException {
        return table.connector.getEncryptionKeys().get(ticket_encryption_from);
    }

    /**
     * May be filtered.
     */
    public EncryptionKey getTicketEncryptionRecipient() throws IOException, SQLException {
        return table.connector.getEncryptionKeys().get(ticket_encryption_recipient);
    }

    /**
     * May be filtered.
     */
    public EncryptionKey getSignupEncryptionFrom() throws IOException, SQLException {
        return table.connector.getEncryptionKeys().get(signup_encryption_from);
    }

    /**
     * May be filtered.
     */
    public EncryptionKey getSignupEncryptionRecipient() throws IOException, SQLException {
        return table.connector.getEncryptionKeys().get(signup_encryption_recipient);
    }

    public String getSupportTollFree() {
        return support_toll_free;
    }

    public String getSupportDayPhone() {
        return support_day_phone;
    }

    public String getSupportEmergencyPhone1() {
        return support_emergency_phone1;
    }

    public String getSupportEmergencyPhone2() {
        return support_emergency_phone2;
    }

    public String getSupportFax() {
        return support_fax;
    }
    
    public String getSupportMailingAddress1() {
        return support_mailing_address1;
    }

    public String getSupportMailingAddress2() {
        return support_mailing_address2;
    }

    public String getSupportMailingAddress3() {
        return support_mailing_address3;
    }

    public String getSupportMailingAddress4() {
        return support_mailing_address4;
    }

    public boolean getEnglishEnabled() {
        return english_enabled;
    }

    public boolean getJapaneseEnabled() {
        return japanese_enabled;
    }

    public String getAowebStrutsHttpUrlBase() {
        return aoweb_struts_http_url_base;
    }

    public URL getAowebStrutsHttpURL() throws MalformedURLException {
        return new URL(aoweb_struts_http_url_base);
    }

    public String getAowebStrutsHttpsUrlBase() {
        return aoweb_struts_https_url_base;
    }

    public URL getAowebStrutsHttpsURL() throws MalformedURLException {
        return new URL(aoweb_struts_https_url_base);
    }

    public String getAowebStrutsGoogleVerifyContent() {
        return aoweb_struts_google_verify_content;
    }

    public boolean getAowebStrutsNoindex() {
        return aoweb_struts_noindex;
    }

    public String getAowebStrutsGoogleAnalyticsNewTrackingCode() {
        return aoweb_struts_google_analytics_new_tracking_code;
    }

    public String getAowebStrutsSignupAdminAddress() {
        return aoweb_struts_signup_admin_address;
    }

    public NetBind getAowebStrutsVncBind() throws IOException, SQLException {
        NetBind nb = table.connector.getNetBinds().get(aoweb_struts_vnc_bind);
        if(nb==null) throw new SQLException("Unable to find NetBind: "+aoweb_struts_vnc_bind);
        return nb;
    }

    public String getAowebStrutsKeystoreType() {
        return aoweb_struts_keystore_type;
    }

    public String getAowebStrutsKeystorePassword() {
        return aoweb_struts_keystore_password;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.BRANDS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getString(pos++);
        nameserver1 = result.getString(pos++);
        nameserver2 = result.getString(pos++);
        nameserver3 = result.getString(pos++);
        nameserver4 = result.getString(pos++);
        smtp_linux_server_account = result.getInt(pos++);
        smtp_host = result.getString(pos++);
        smtp_password = result.getString(pos++);
        imap_linux_server_account = result.getInt(pos++);
        imap_host = result.getString(pos++);
        imap_password = result.getString(pos++);
        support_email_address = result.getInt(pos++);
        support_email_display = result.getString(pos++);
        signup_email_address = result.getInt(pos++);
        signup_email_display = result.getString(pos++);
        ticket_encryption_from = result.getInt(pos++);
        ticket_encryption_recipient = result.getInt(pos++);
        signup_encryption_from = result.getInt(pos++);
        signup_encryption_recipient = result.getInt(pos++);
        support_toll_free = result.getString(pos++);
        support_day_phone = result.getString(pos++);
        support_emergency_phone1 = result.getString(pos++);
        support_emergency_phone2 = result.getString(pos++);
        support_fax = result.getString(pos++);
        support_mailing_address1 = result.getString(pos++);
        support_mailing_address2 = result.getString(pos++);
        support_mailing_address3 = result.getString(pos++);
        support_mailing_address4 = result.getString(pos++);
        english_enabled = result.getBoolean(pos++);
        japanese_enabled = result.getBoolean(pos++);
        aoweb_struts_http_url_base = result.getString(pos++);
        aoweb_struts_https_url_base = result.getString(pos++);
        aoweb_struts_google_verify_content = result.getString(pos++);
        aoweb_struts_noindex = result.getBoolean(pos++);
        aoweb_struts_google_analytics_new_tracking_code = result.getString(pos++);
        aoweb_struts_signup_admin_address = result.getString(pos++);
        aoweb_struts_vnc_bind = result.getInt(pos++);
        aoweb_struts_keystore_type = result.getString(pos++);
        aoweb_struts_keystore_password = result.getString(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        nameserver1 = in.readUTF();
        nameserver2 = in.readUTF();
        nameserver3 = in.readNullUTF();
        nameserver4 = in.readNullUTF();
        smtp_linux_server_account = in.readCompressedInt();
        smtp_host = in.readNullUTF();
        smtp_password = in.readUTF();
        imap_linux_server_account = in.readCompressedInt();
        imap_host = in.readNullUTF();
        imap_password = in.readUTF();
        support_email_address = in.readCompressedInt();
        support_email_display = in.readUTF();
        signup_email_address = in.readCompressedInt();
        signup_email_display = in.readUTF();
        ticket_encryption_from = in.readCompressedInt();
        ticket_encryption_recipient = in.readCompressedInt();
        signup_encryption_from = in.readCompressedInt();
        signup_encryption_recipient = in.readCompressedInt();
        support_toll_free = in.readNullUTF();
        support_day_phone = in.readNullUTF();
        support_emergency_phone1 = in.readNullUTF();
        support_emergency_phone2 = in.readNullUTF();
        support_fax = in.readNullUTF();
        support_mailing_address1 = in.readNullUTF();
        support_mailing_address2 = in.readNullUTF();
        support_mailing_address3 = in.readNullUTF();
        support_mailing_address4 = in.readNullUTF();
        english_enabled = in.readBoolean();
        japanese_enabled = in.readBoolean();
        aoweb_struts_http_url_base = in.readUTF();
        aoweb_struts_https_url_base = in.readUTF();
        aoweb_struts_google_verify_content = in.readNullUTF();
        aoweb_struts_noindex = in.readBoolean();
        aoweb_struts_google_analytics_new_tracking_code = in.readNullUTF();
        aoweb_struts_signup_admin_address = in.readUTF();
        aoweb_struts_vnc_bind = in.readCompressedInt();
        aoweb_struts_keystore_type = in.readUTF();
        aoweb_struts_keystore_password = in.readUTF();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(nameserver1);
        out.writeUTF(nameserver2);
        out.writeNullUTF(nameserver3);
        out.writeNullUTF(nameserver4);
        out.writeCompressedInt(smtp_linux_server_account);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_46)>=0) out.writeNullUTF(smtp_host);
        out.writeUTF(smtp_password);
        out.writeCompressedInt(imap_linux_server_account);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_46)>=0) out.writeNullUTF(imap_host);
        out.writeUTF(imap_password);
        out.writeCompressedInt(support_email_address);
        out.writeUTF(support_email_display);
        out.writeCompressedInt(signup_email_address);
        out.writeUTF(signup_email_display);
        out.writeCompressedInt(ticket_encryption_from);
        out.writeCompressedInt(ticket_encryption_recipient);
        out.writeCompressedInt(signup_encryption_from);
        out.writeCompressedInt(signup_encryption_recipient);
        out.writeNullUTF(support_toll_free);
        out.writeNullUTF(support_day_phone);
        out.writeNullUTF(support_emergency_phone1);
        out.writeNullUTF(support_emergency_phone2);
        out.writeNullUTF(support_fax);
        out.writeNullUTF(support_mailing_address1);
        out.writeNullUTF(support_mailing_address2);
        out.writeNullUTF(support_mailing_address3);
        out.writeNullUTF(support_mailing_address4);
        out.writeBoolean(english_enabled);
        out.writeBoolean(japanese_enabled);
        out.writeUTF(aoweb_struts_http_url_base);
        out.writeUTF(aoweb_struts_https_url_base);
        out.writeNullUTF(aoweb_struts_google_verify_content);
        out.writeBoolean(aoweb_struts_noindex);
        out.writeNullUTF(aoweb_struts_google_analytics_new_tracking_code);
        out.writeUTF(aoweb_struts_signup_admin_address);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_52)>=0) out.writeCompressedInt(aoweb_struts_vnc_bind);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_53)>=0) {
            out.writeUTF(aoweb_struts_keystore_type);
            out.writeUTF(aoweb_struts_keystore_password);
        }
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getBusiness(),
            getSmtpLinuxServerAccount(),
            getImapLinuxServerAccount(),
            getSupportEmailAddress(),
            getSignupEmailAddress(),
            getTicketEncryptionFrom(),
            getTicketEncryptionRecipient(),
            getSignupEncryptionFrom(),
            getSignupEncryptionRecipient(),
            getAowebStrutsVncBind()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    /**
     * Gets the Reseller for this Brand or <code>null</code> if not a reseller.
     */
    public Reseller getReseller() throws IOException, SQLException {
        return table.connector.getResellers().getReseller(this);
    }

    public List<TicketBrandCategory> getTicketBrandCategories() throws IOException, SQLException {
        return table.connector.getTicketBrandCategories().getTicketBrandCategories(this);
    }

    /**
     * Gets the immediate parent of this brand or <code>null</code> if none available.
     */
    public Brand getParentBrand() throws IOException, SQLException {
        Business bu = getBusiness();
        if(bu==null) return null;
        Business parent = bu.getParentBusiness();
        while(parent!=null) {
            Brand parentBrand = parent.getBrand();
            if(parentBrand!=null) return parentBrand;
        }
        return null;
    }

    /**
     * The children of the brand are any brands that have their closest parent
     * business (that is a brand) equal to this one.
     */
    public List<Brand> getChildBrands() throws IOException, SQLException {
        List<Brand> children = new ArrayList<Brand>();
        for(Brand brand : table.connector.getBrands().getRows()) {
            if(!brand.equals(this) && this.equals(brand.getParentBrand())) children.add(brand);
        }
        return children;
    }
}
