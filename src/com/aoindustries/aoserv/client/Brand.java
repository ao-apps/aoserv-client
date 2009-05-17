package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private String smtp_password;
    private int imap_linux_server_account;
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

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_ACCOUNTING : return pkey;
            case 1: return nameserver1;
            case 2: return nameserver2;
            case 3: return nameserver3;
            case 4: return nameserver4;
            case 5: return smtp_linux_server_account;
            case 6: return smtp_password;
            case 7: return imap_linux_server_account;
            case 8: return imap_password;
            case 9: return support_email_address;
            case 10: return support_email_display;
            case 11: return signup_email_address;
            case 12: return signup_email_display;
            case 13: return ticket_encryption_from;
            case 14: return ticket_encryption_recipient;
            case 15: return signup_encryption_from;
            case 16: return signup_encryption_recipient;
            case 17: return support_toll_free;
            case 18: return support_day_phone;
            case 19: return support_emergency_phone1;
            case 20: return support_emergency_phone2;
            case 21: return support_fax;
            case 22: return support_mailing_address1;
            case 23: return support_mailing_address2;
            case 24: return support_mailing_address3;
            case 25: return support_mailing_address4;
            case 26: return english_enabled;
            case 27: return japanese_enabled;
            case 28: return aoweb_struts_http_url_base;
            case 29: return aoweb_struts_https_url_base;
            case 30: return aoweb_struts_google_verify_content;
            case 31: return aoweb_struts_noindex;
            case 32: return aoweb_struts_google_analytics_new_tracking_code;
            case 33: return aoweb_struts_signup_admin_address;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Business getBusiness() throws SQLException, IOException {
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

    public LinuxServerAccount getSmtpLinuxServerAccount() throws SQLException, IOException {
        LinuxServerAccount lsa = table.connector.getLinuxServerAccounts().get(smtp_linux_server_account);
        if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+smtp_linux_server_account);
        return lsa;
    }

    public String getSmtpPassword() {
        return smtp_password;
    }

    public LinuxServerAccount getImapLinuxServerAccount() throws SQLException, IOException {
        LinuxServerAccount lsa = table.connector.getLinuxServerAccounts().get(imap_linux_server_account);
        if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+imap_linux_server_account);
        return lsa;
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

    public EncryptionKey getTicketEncryptionFrom() throws IOException, SQLException {
        EncryptionKey ek = table.connector.getEncryptionKeys().get(ticket_encryption_from);
        if(ek==null) throw new SQLException("Unable to find EncryptionKey: "+ticket_encryption_from);
        return ek;
    }

    public EncryptionKey getTicketEncryptionRecipient() throws IOException, SQLException {
        EncryptionKey ek = table.connector.getEncryptionKeys().get(ticket_encryption_recipient);
        if(ek==null) throw new SQLException("Unable to find EncryptionKey: "+ticket_encryption_recipient);
        return ek;
    }

    public EncryptionKey getSignupEncryptionFrom() throws IOException, SQLException {
        EncryptionKey ek = table.connector.getEncryptionKeys().get(signup_encryption_from);
        if(ek==null) throw new SQLException("Unable to find EncryptionKey: "+signup_encryption_from);
        return ek;
    }

    public EncryptionKey getSignupEncryptionRecipient() throws IOException, SQLException {
        EncryptionKey ek = table.connector.getEncryptionKeys().get(signup_encryption_recipient);
        if(ek==null) throw new SQLException("Unable to find EncryptionKey: "+signup_encryption_recipient);
        return ek;
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

    public String getAowebStrutsHttpsUrlBase() {
        return aoweb_struts_https_url_base;
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
        smtp_password = result.getString(pos++);
        imap_linux_server_account = result.getInt(pos++);
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
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        nameserver1 = in.readUTF();
        nameserver2 = in.readUTF();
        nameserver3 = in.readNullUTF();
        nameserver4 = in.readNullUTF();
        smtp_linux_server_account = in.readCompressedInt();
        smtp_password = in.readUTF();
        imap_linux_server_account = in.readCompressedInt();
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
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(nameserver1);
        out.writeUTF(nameserver2);
        out.writeNullUTF(nameserver3);
        out.writeNullUTF(nameserver4);
        out.writeCompressedInt(smtp_linux_server_account);
        out.writeUTF(smtp_password);
        out.writeCompressedInt(imap_linux_server_account);
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

    public int addPackageDefinition(
        PackageCategory category,
        String name,
        String version,
        String display,
        String description,
        int setupFee,
        TransactionType setupFeeTransactionType,
        int monthlyRate,
        TransactionType monthlyRateTransactionType
    ) throws IOException, SQLException {
        return table.connector.getPackageDefinitions().addPackageDefinition(
            this,
            category,
            name,
            version,
            display,
            description,
            setupFee,
            setupFeeTransactionType,
            monthlyRate,
            monthlyRateTransactionType
        );
    }

    public PackageDefinition getPackageDefinition(PackageCategory category, String name, String version) throws IOException, SQLException {
        return table.connector.getPackageDefinitions().getPackageDefinition(this, category, name, version);
    }

    public List<PackageDefinition> getPackageDefinitions(PackageCategory category) throws IOException, SQLException {
        return table.connector.getPackageDefinitions().getPackageDefinitions(this, category);
    }
}