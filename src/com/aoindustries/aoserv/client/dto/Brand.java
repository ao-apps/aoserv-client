/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class Brand extends AOServObject {

    private AccountingCode accounting;
    private DomainName nameserver1;
    private DomainName nameserver2;
    private DomainName nameserver3;
    private DomainName nameserver4;
    private int smtpEmailInbox;
    private Hostname smtpHost;
    private String smtpPassword;
    private int imapEmailInbox;
    private Hostname imapHost;
    private String imapPassword;
    private int supportEmailAddress;
    private String supportEmailDisplay;
    private int signupEmailAddress;
    private String signupEmailDisplay;
    private int ticketEncryptionFrom;
    private int ticketEncryptionRecipient;
    private int signupEncryptionFrom;
    private int signupEncryptionRecipient;
    private String supportTollFree;
    private String supportDayPhone;
    private String supportEmergencyPhone1;
    private String supportEmergencyPhone2;
    private String supportFax;
    private String supportMailingAddress1;
    private String supportMailingAddress2;
    private String supportMailingAddress3;
    private String supportMailingAddress4;
    private boolean englishEnabled;
    private boolean japaneseEnabled;
    private String aowebStrutsHttpUrlBase;
    private String aowebStrutsHttpsUrlBase;
    private String aowebStrutsGoogleVerifyContent;
    private boolean aowebStrutsNoindex;
    private String aowebStrutsGoogleAnalyticsNewTrackingCode;
    private Email aowebStrutsSignupAdminAddress;
    private int aowebStrutsVncBind;
    private String aowebStrutsKeystoreType;
    private String aowebStrutsKeystorePassword;

    public Brand() {
    }

    public Brand(
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
        this.accounting = accounting;
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
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public DomainName getNameserver1() {
        return nameserver1;
    }

    public void setNameserver1(DomainName nameserver1) {
        this.nameserver1 = nameserver1;
    }

    public DomainName getNameserver2() {
        return nameserver2;
    }

    public void setNameserver2(DomainName nameserver2) {
        this.nameserver2 = nameserver2;
    }

    public DomainName getNameserver3() {
        return nameserver3;
    }

    public void setNameserver3(DomainName nameserver3) {
        this.nameserver3 = nameserver3;
    }

    public DomainName getNameserver4() {
        return nameserver4;
    }

    public void setNameserver4(DomainName nameserver4) {
        this.nameserver4 = nameserver4;
    }

    public int getSmtpEmailInbox() {
        return smtpEmailInbox;
    }

    public void setSmtpEmailInbox(int smtpEmailInbox) {
        this.smtpEmailInbox = smtpEmailInbox;
    }

    public Hostname getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(Hostname smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public int getImapEmailInbox() {
        return imapEmailInbox;
    }

    public void setImapEmailInbox(int imapEmailInbox) {
        this.imapEmailInbox = imapEmailInbox;
    }

    public Hostname getImapHost() {
        return imapHost;
    }

    public void setImapHost(Hostname imapHost) {
        this.imapHost = imapHost;
    }

    public String getImapPassword() {
        return imapPassword;
    }

    public void setImapPassword(String imapPassword) {
        this.imapPassword = imapPassword;
    }

    public int getSupportEmailAddress() {
        return supportEmailAddress;
    }

    public void setSupportEmailAddress(int supportEmailAddress) {
        this.supportEmailAddress = supportEmailAddress;
    }

    public String getSupportEmailDisplay() {
        return supportEmailDisplay;
    }

    public void setSupportEmailDisplay(String supportEmailDisplay) {
        this.supportEmailDisplay = supportEmailDisplay;
    }

    public int getSignupEmailAddress() {
        return signupEmailAddress;
    }

    public void setSignupEmailAddress(int signupEmailAddress) {
        this.signupEmailAddress = signupEmailAddress;
    }

    public String getSignupEmailDisplay() {
        return signupEmailDisplay;
    }

    public void setSignupEmailDisplay(String signupEmailDisplay) {
        this.signupEmailDisplay = signupEmailDisplay;
    }

    public int getTicketEncryptionFrom() {
        return ticketEncryptionFrom;
    }

    public void setTicketEncryptionFrom(int ticketEncryptionFrom) {
        this.ticketEncryptionFrom = ticketEncryptionFrom;
    }

    public int getTicketEncryptionRecipient() {
        return ticketEncryptionRecipient;
    }

    public void setTicketEncryptionRecipient(int ticketEncryptionRecipient) {
        this.ticketEncryptionRecipient = ticketEncryptionRecipient;
    }

    public int getSignupEncryptionFrom() {
        return signupEncryptionFrom;
    }

    public void setSignupEncryptionFrom(int signupEncryptionFrom) {
        this.signupEncryptionFrom = signupEncryptionFrom;
    }

    public int getSignupEncryptionRecipient() {
        return signupEncryptionRecipient;
    }

    public void setSignupEncryptionRecipient(int signupEncryptionRecipient) {
        this.signupEncryptionRecipient = signupEncryptionRecipient;
    }

    public String getSupportTollFree() {
        return supportTollFree;
    }

    public void setSupportTollFree(String supportTollFree) {
        this.supportTollFree = supportTollFree;
    }

    public String getSupportDayPhone() {
        return supportDayPhone;
    }

    public void setSupportDayPhone(String supportDayPhone) {
        this.supportDayPhone = supportDayPhone;
    }

    public String getSupportEmergencyPhone1() {
        return supportEmergencyPhone1;
    }

    public void setSupportEmergencyPhone1(String supportEmergencyPhone1) {
        this.supportEmergencyPhone1 = supportEmergencyPhone1;
    }

    public String getSupportEmergencyPhone2() {
        return supportEmergencyPhone2;
    }

    public void setSupportEmergencyPhone2(String supportEmergencyPhone2) {
        this.supportEmergencyPhone2 = supportEmergencyPhone2;
    }

    public String getSupportFax() {
        return supportFax;
    }

    public void setSupportFax(String supportFax) {
        this.supportFax = supportFax;
    }

    public String getSupportMailingAddress1() {
        return supportMailingAddress1;
    }

    public void setSupportMailingAddress1(String supportMailingAddress1) {
        this.supportMailingAddress1 = supportMailingAddress1;
    }

    public String getSupportMailingAddress2() {
        return supportMailingAddress2;
    }

    public void setSupportMailingAddress2(String supportMailingAddress2) {
        this.supportMailingAddress2 = supportMailingAddress2;
    }

    public String getSupportMailingAddress3() {
        return supportMailingAddress3;
    }

    public void setSupportMailingAddress3(String supportMailingAddress3) {
        this.supportMailingAddress3 = supportMailingAddress3;
    }

    public String getSupportMailingAddress4() {
        return supportMailingAddress4;
    }

    public void setSupportMailingAddress4(String supportMailingAddress4) {
        this.supportMailingAddress4 = supportMailingAddress4;
    }

    public boolean isEnglishEnabled() {
        return englishEnabled;
    }

    public void setEnglishEnabled(boolean englishEnabled) {
        this.englishEnabled = englishEnabled;
    }

    public boolean isJapaneseEnabled() {
        return japaneseEnabled;
    }

    public void setJapaneseEnabled(boolean japaneseEnabled) {
        this.japaneseEnabled = japaneseEnabled;
    }

    public String getAowebStrutsHttpUrlBase() {
        return aowebStrutsHttpUrlBase;
    }

    public void setAowebStrutsHttpUrlBase(String aowebStrutsHttpUrlBase) {
        this.aowebStrutsHttpUrlBase = aowebStrutsHttpUrlBase;
    }

    public String getAowebStrutsHttpsUrlBase() {
        return aowebStrutsHttpsUrlBase;
    }

    public void setAowebStrutsHttpsUrlBase(String aowebStrutsHttpsUrlBase) {
        this.aowebStrutsHttpsUrlBase = aowebStrutsHttpsUrlBase;
    }

    public String getAowebStrutsGoogleVerifyContent() {
        return aowebStrutsGoogleVerifyContent;
    }

    public void setAowebStrutsGoogleVerifyContent(String aowebStrutsGoogleVerifyContent) {
        this.aowebStrutsGoogleVerifyContent = aowebStrutsGoogleVerifyContent;
    }

    public boolean isAowebStrutsNoindex() {
        return aowebStrutsNoindex;
    }

    public void setAowebStrutsNoindex(boolean aowebStrutsNoindex) {
        this.aowebStrutsNoindex = aowebStrutsNoindex;
    }

    public String getAowebStrutsGoogleAnalyticsNewTrackingCode() {
        return aowebStrutsGoogleAnalyticsNewTrackingCode;
    }

    public void setAowebStrutsGoogleAnalyticsNewTrackingCode(String aowebStrutsGoogleAnalyticsNewTrackingCode) {
        this.aowebStrutsGoogleAnalyticsNewTrackingCode = aowebStrutsGoogleAnalyticsNewTrackingCode;
    }

    public Email getAowebStrutsSignupAdminAddress() {
        return aowebStrutsSignupAdminAddress;
    }

    public void setAowebStrutsSignupAdminAddress(Email aowebStrutsSignupAdminAddress) {
        this.aowebStrutsSignupAdminAddress = aowebStrutsSignupAdminAddress;
    }

    public int getAowebStrutsVncBind() {
        return aowebStrutsVncBind;
    }

    public void setAowebStrutsVncBind(int aowebStrutsVncBind) {
        this.aowebStrutsVncBind = aowebStrutsVncBind;
    }

    public String getAowebStrutsKeystoreType() {
        return aowebStrutsKeystoreType;
    }

    public void setAowebStrutsKeystoreType(String aowebStrutsKeystoreType) {
        this.aowebStrutsKeystoreType = aowebStrutsKeystoreType;
    }

    public String getAowebStrutsKeystorePassword() {
        return aowebStrutsKeystorePassword;
    }

    public void setAowebStrutsKeystorePassword(String aowebStrutsKeystorePassword) {
        this.aowebStrutsKeystorePassword = aowebStrutsKeystorePassword;
    }
}
