package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.security.Principal;
import java.security.acl.Group;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A <code>Business</code> is one distinct set of resources and permissions.
* Some businesses may have child businesses associated with them.  When that is the
 * case, the top level business is ultimately responsible for all actions taken and
 * resources used by itself and all child businesses.
 *
 * @author  AO Industries, Inc.
 */
final public class Business extends AOServObjectAccountingCodeKey<Business> implements BeanFactory<com.aoindustries.aoserv.client.beans.Business>, Group /* TODO: implements Disablable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The maximum depth of the business tree.
     */
    public static final int MAXIMUM_BUSINESS_TREE_DEPTH=7;

    /**
     * The minimum payment for auto-enabling accounts, in pennies.
     */
    // TODO: Currencies: public static final BigDecimal MINIMUM_PAYMENT = BigDecimal.valueOf(3000, 2);

    /**
     * The default inbound email burst before rate limiting.
     */
    public static final int DEFAULT_EMAIL_IN_BURST = 1000;

    /**
     * The default sustained inbound email rate in emails/second.
     */
    public static final float DEFAULT_EMAIL_IN_RATE = 10f;

    /**
     * The default outbound email burst before rate limiting.
     */
    public static final int DEFAULT_EMAIL_OUT_BURST = 200;

    /**
     * The default sustained outbound email rate in emails/second.
     */
    public static final float DEFAULT_EMAIL_OUT_RATE = .2f;

    /**
     * The default relay email burst before rate limiting.
     */
    public static final int DEFAULT_EMAIL_RELAY_BURST = 100;

    /**
     * The default sustained relay email rate in emails/second.
     */
    public static final float DEFAULT_EMAIL_RELAY_RATE = .1f;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String contractVersion;
    final private Timestamp created;
    final private Timestamp canceled;
    private String cancelReason;
    private AccountingCode parent;
    final private boolean canAddBackupServer;
    final private boolean canAddBusinesses;
    final private boolean canSeePrices;
    final private Integer disableLog;
    private String doNotDisableReason;
    final private boolean autoEnable;
    final private boolean billParent;
    final private int packageDefinition;
    private UserId createdBy;
    final private Integer emailInBurst;
    final private Float emailInRate;
    final private Integer emailOutBurst;
    final private Float emailOutRate;
    final private Integer emailRelayBurst;
    final private Float emailRelayRate;

    public Business(
        BusinessService<?,?> service,
        AccountingCode accounting,
        String contractVersion,
        Timestamp created,
        Timestamp canceled,
        String cancelReason,
        AccountingCode parent,
        boolean canAddBackupServer,
        boolean canAddBusinesses,
        boolean canSeePrices,
        Integer disableLog,
        String doNotDisableReason,
        boolean autoEnable,
        boolean billParent,
        int packageDefinition,
        UserId createdBy,
        Integer emailInBurst,
        Float emailInRate,
        Integer emailOutBurst,
        Float emailOutRate,
        Integer emailRelayBurst,
        Float emailRelayRate
    ) {
        super(service, accounting);
        this.contractVersion = contractVersion;
        this.created = created;
        this.canceled = canceled;
        this.cancelReason = cancelReason;
        this.parent = parent;
        this.canAddBackupServer = canAddBackupServer;
        this.canAddBusinesses = canAddBusinesses;
        this.canSeePrices = canSeePrices;
        this.disableLog = disableLog;
        this.doNotDisableReason = doNotDisableReason;
        this.autoEnable = autoEnable;
        this.billParent = billParent;
        this.packageDefinition = packageDefinition;
        this.createdBy = createdBy;
        this.emailInBurst = emailInBurst;
        this.emailInRate = emailInRate;
        this.emailOutBurst = emailOutBurst;
        this.emailOutRate = emailOutRate;
        this.emailRelayBurst = emailRelayBurst;
        this.emailRelayRate = emailRelayRate;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        contractVersion = intern(contractVersion);
        cancelReason = intern(cancelReason);
        parent = intern(parent);
        doNotDisableReason = intern(doNotDisableReason);
        createdBy = intern(createdBy);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=0, name=COLUMN_ACCOUNTING, index=IndexType.PRIMARY_KEY, description="the unique identifier for this business.")
    public AccountingCode getAccounting() {
        return getKey();
    }

    @SchemaColumn(order=1, name="contract_version", description="the version number of the contract")
    public String getContractVersion() {
    	return contractVersion;
    }

    @SchemaColumn(order=2, name="created", description="the time the account was created")
    public Timestamp getCreated() {
    	return created;
    }

    @SchemaColumn(order=3, name="canceled", description="the time the account was deactivated")
    public Timestamp getCanceled() {
    	return canceled;
    }

    @SchemaColumn(order=4, name="cancel_reason", description="the reason the account was canceled")
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * May be filtered.
     */
    static final String COLUMN_PARENT = "parent";
    @SchemaColumn(order=5, name=COLUMN_PARENT, index=IndexType.INDEXED, description="the parent business to this one")
    public Business getParentBusiness() throws RemoteException {
        if(parent==null) return null;
        return getService().filterUnique(COLUMN_ACCOUNTING, parent);
    }

    @SchemaColumn(order=6, name="can_add_backup_server", description="the business may add servers to the backup system")
    public boolean canAddBackupServer() {
        return canAddBackupServer;
    }

    @SchemaColumn(order=7, name="can_add_businesses", description="if <code>true</code> this business can create and be the parent of other businesses")
    public boolean canAddBusinesses() {
    	return canAddBusinesses;
    }

    @SchemaColumn(order=8, name="can_see_prices", description="control whether prices will be visible or filtered")
    public boolean canSeePrices() {
        return canSeePrices;
    }

    static final String COLUMN_DISABLE_LOG = "disable_log";
    @SchemaColumn(order=9, name=COLUMN_DISABLE_LOG, index=IndexType.INDEXED, description="indicates the business is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        return getService().getConnector().getDisableLogs().get(disableLog);
    }

    @SchemaColumn(order=10, name="do_not_disable_reason", description="a reason why we should not disable the account")
    public String getDoNotDisableReason() {
        return doNotDisableReason;
    }

    @SchemaColumn(order=11, name="auto_enable", description="allows the account to be automatically reenabled on payment")
    public boolean getAutoEnable() {
        return autoEnable;
    }

    @SchemaColumn(order=12, name="bill_parent", description="if <code>true</code>, the parent business will be charged for all resources used by this account")
    public boolean billParent() {
        return billParent;
    }

    /* TODO
    @SchemaColumn(order=13, name="package_definition", description="the definition of the package")
    public PackageDefinition getPackageDefinition() {
        return getService().getConnector().getPackageDefinitions().get(package_definition);
    } */

    /**
     * May be filtered.  May also be null for the root business only.
     */
    static final String COLUMN_CREATED_BY = "created_by";
    @SchemaColumn(order=13, name=COLUMN_CREATED_BY, index=IndexType.INDEXED, description="the user who added this business")
    public BusinessAdministrator getCreatedBy() throws RemoteException {
        if(createdBy==null) return null;
        try {
            return getService().getConnector().getBusinessAdministrators().get(createdBy);
        } catch(NoSuchElementException err) {
            // Filtered
            return null;
        }
    }

    /**
     * Gets the inbound burst limit for emails, the number of emails that may be sent before limiting occurs.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=14, name="email_in_burst", description="the maximum burst of inbound email before limiting begins")
    public Integer getEmailInBurst() {
        return emailInBurst;
    }

    /**
     * Gets the inbound sustained email rate in emails/second.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=15, name="email_in_rate", description="the number of sustained inbound emails per second")
    public Float getEmailInRate() {
        return emailInRate;
    }

    /**
     * Gets the outbound burst limit for emails, the number of emails that may be sent before limiting occurs.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=16, name="email_out_burst", description="the maximum burst of outbound email before limiting begins")
    public Integer getEmailOutBurst() {
        return emailOutBurst;
    }

    /**
     * Gets the outbound sustained email rate in emails/second.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=17, name="email_out_rate", description="the number of sustained outbound emails per second")
    public Float getEmailOutRate() {
        return emailOutRate;
    }

    /**
     * Gets the relay burst limit for emails, the number of emails that may be sent before limiting occurs.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=18, name="email_relay_burst", description="the maximum burst of relay email before limiting begins")
    public Integer getEmailRelayBurst() {
        return emailRelayBurst;
    }

    /**
     * Gets the relay sustained email rate in emails/second.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=19, name="email_relay_rate", description="the number of sustained relay emails per second")
    public Float getEmailRelayRate() {
        return emailRelayRate;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Business getBean() {
        return new com.aoindustries.aoserv.client.beans.Business(getKey().getBean(), contractVersion, created, canceled, cancelReason, parent==null ? null : parent.getBean(), canAddBackupServer, canAddBusinesses, canSeePrices, disableLog, doNotDisableReason, autoEnable, billParent, packageDefinition, createdBy==null ? null : createdBy.getBean(), emailInBurst, emailInRate, emailOutBurst, emailOutRate, emailRelayBurst, emailRelayRate);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getParentBusiness(),
            getDisableLog(),
            // TODO: getPackageDefinition(),
            getCreatedBy()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            AOServObjectUtils.createDependencySet(
                getBrand()
            ),
            getAoservRoles(),
            getChildBusinesses(),
            // TODO: getBusinessProfiles(),
            getBusinessServers(),
            // TODO: getCreditCards(),
            // TODO: getCreditCardProcessors(),
            // TODO: getCreditCardTransactions(),
            // TODO: getCreditCardTransactionsByCreditCardAccounting(),
            getDisableLogs(),
            // TODO: getDnsZones(),
            getGroupNames(),
            // TODO: getIPAddresses(),
            // TODO: getEmailDomains(),
            // TODO: getLinuxGroups(),
            // TODO: getEncryptionKeys(),
            // TODO: getEmailPipes(),
            // TODO: getEmailSmtpRelays(),
            // TODO: getHttpdServers(),
            // TODO: getHttpdSites(),
            // TODO: getMonthlyCharges(),
            // TODO: getMonthlyChargesBySourceBusiness(),
            // TODO: getMysqlDatabases(),
            // TODO: getNoticeLogs(),
            // TODO: getPackageDefinitions(),
            getResources(),
            getServers(),
            getServerFarms(),
            getUsernames()
            // TODO: getTickets(),
            // TODO: getTicketActionsByOldBusiness(),
            // TODO: getTicketActionsByNewBusiness(),
            // TODO: getTransactions(),
            // TODO: getTransactionsBySourceAccounting()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /**
     * Gets the Brand for this business or <code>null</code> if not a brand.
     */
    public IndexedSet<AOServRole> getAoservRoles() throws RemoteException {
        return getService().getConnector().getAoservRoles().filterIndexed(AOServRole.COLUMN_ACCOUNTING, this);
    }

    public Brand getBrand() throws RemoteException {
        return getService().getConnector().getBrands().filterUnique(Brand.COLUMN_ACCOUNTING, this);
    }

    public IndexedSet<Business> getChildBusinesses() throws RemoteException {
        return getService().getConnector().getBusinesses().filterIndexed(COLUMN_PARENT, this);
    }

    public IndexedSet<BusinessServer> getBusinessServers() throws RemoteException {
        return getService().getConnector().getBusinessServers().filterIndexed(BusinessServer.COLUMN_ACCOUNTING, this);
    }

    public IndexedSet<DisableLog> getDisableLogs() throws RemoteException {
        return getService().getConnector().getDisableLogs().filterIndexed(DisableLog.COLUMN_ACCOUNTING, this);
    }

    public IndexedSet<GroupName> getGroupNames() throws RemoteException {
        return getService().getConnector().getGroupNames().filterIndexed(GroupName.COLUMN_ACCOUNTING, this);
    }

    public IndexedSet<Resource> getResources() throws RemoteException {
        return getService().getConnector().getResources().filterIndexed(Resource.COLUMN_ACCOUNTING, this);
    }

    public IndexedSet<Server> getServers() throws RemoteException {
        return getService().getConnector().getServers().filterIndexed(Server.COLUMN_ACCOUNTING, this);
    }

    public IndexedSet<ServerFarm> getServerFarms() throws RemoteException {
        return getService().getConnector().getServerFarms().filterIndexed(ServerFarm.COLUMN_OWNER, this);
    }

    public IndexedSet<Username> getUsernames() throws RemoteException {
        return getService().getConnector().getUsernames().filterIndexed(Username.COLUMN_ACCOUNTING, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Group">
    /**
     * A group contains all users of its own business plus all parent businesses.
     */
    public boolean addMember(Principal user) {
        if(!(user instanceof BusinessAdministrator)) throw new IllegalArgumentException("Not BusinessAdministrator: "+user.getName());
        try {
            Business userBusiness = ((BusinessAdministrator)user).getUsername().getBusiness();
            Business thisBusiness = this;
            do {
                if(userBusiness.equals(thisBusiness)) return false;
                thisBusiness = thisBusiness.getParentBusiness();
            } while(thisBusiness!=null);
            throw new UnsupportedOperationException("Not implemented");
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * A group contains all users of its own business plus all parent businesses.
     */
    public boolean removeMember(Principal user) {
        if(!(user instanceof BusinessAdministrator)) throw new IllegalArgumentException("Not BusinessAdministrator: "+user.getName());
        try {
            Business userBusiness = ((BusinessAdministrator)user).getUsername().getBusiness();
            Business thisBusiness = this;
            do {
                if(userBusiness.equals(thisBusiness)) throw new UnsupportedOperationException("Not implemented");
                thisBusiness = thisBusiness.getParentBusiness();
            } while(thisBusiness!=null);
            return false;
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * A group contains all users of its own business plus all parent businesses.
     */
    public boolean isMember(Principal user) {
        if(!(user instanceof BusinessAdministrator)) throw new IllegalArgumentException("Not BusinessAdministrator: "+user.getName());
        try {
            Business userBusiness = ((BusinessAdministrator)user).getUsername().getBusiness();
            Business thisBusiness = this;
            do {
                if(userBusiness.equals(thisBusiness)) return true;
                thisBusiness = thisBusiness.getParentBusiness();
            } while(thisBusiness!=null);
            return false;
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * A group contains all users of its own business plus all parent businesses.
     */
    public Enumeration<BusinessAdministrator> members() {
        List<BusinessAdministrator> members = new ArrayList<BusinessAdministrator>();
        try {
            Business thisBusiness = this;
            do {
                for(Username un : thisBusiness.getUsernames()) {
                    BusinessAdministrator ba = un.getBusinessAdministrator();
                    if(ba!=null) members.add(ba);
                }
                thisBusiness = thisBusiness.getParentBusiness();
            } while(thisBusiness!=null);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
        return Collections.enumeration(members);
    }

    public String getName() {
        return getKey().getAccounting();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int addBusinessProfile(
        String name,
        boolean isPrivate,
        String phone,
        String fax,
        String address1,
        String address2,
        String city,
        String state,
        String country,
        String zip,
        boolean sendInvoice,
        String billingContact,
        String billingEmail,
        String technicalContact,
        String technicalEmail
    ) throws IOException, SQLException {
        return service.connector.getBusinessProfiles().addBusinessProfile(
            this,
            name,
            isPrivate,
            phone,
            fax,
            address1,
            address2,
            city,
            state,
            country,
            zip,
            sendInvoice,
            billingContact,
            billingEmail,
            technicalContact,
            technicalEmail
        );
    }

    public int addBusinessServer(
        Server server
    ) throws IOException, SQLException {
        return service.connector.getBusinessServers().addBusinessServer(this, server);
    }

    public int addCreditCard(
        CreditCardProcessor processor,
        String groupName,
        String cardInfo,
        String providerUniqueId,
        String firstName,
        String lastName,
        String companyName,
        String email,
        String phone,
        String fax,
        String customerTaxId,
        String streetAddress1,
        String streetAddress2,
        String city,
        String state,
        String postalCode,
        CountryCode countryCode,
        String principalName,
        String description,
        String cardNumber,
        byte expirationMonth,
        short expirationYear
    ) throws IOException, SQLException {
        return service.connector.getCreditCards().addCreditCard(
            processor,
            this,
            groupName,
            cardInfo,
            providerUniqueId,
            firstName,
            lastName,
            companyName,
            email,
            phone,
            fax,
            customerTaxId,
            streetAddress1,
            streetAddress2,
            city,
            state,
            postalCode,
            countryCode,
            principalName,
            description,
            cardNumber,
            expirationMonth,
            expirationYear
        );
    }
    */

    /**
     * Adds a transaction in the pending state.
     */
    /* TODO
    public int addCreditCardTransaction(
        CreditCardProcessor processor,
        String groupName,
        boolean testMode,
        int duplicateWindow,
        String orderNumber,
        String currencyCode,
        BigDecimal amount,
        BigDecimal taxAmount,
        boolean taxExempt,
        BigDecimal shippingAmount,
        BigDecimal dutyAmount,
        String shippingFirstName,
        String shippingLastName,
        String shippingCompanyName,
        String shippingStreetAddress1,
        String shippingStreetAddress2,
        String shippingCity,
        String shippingState,
        String shippingPostalCode,
        String shippingCountryCode,
        boolean emailCustomer,
        String merchantEmail,
        String invoiceNumber,
        String purchaseOrderNumber,
        String description,
        BusinessAdministrator creditCardCreatedBy,
        String creditCardPrincipalName,
        Business creditCardAccounting,
        String creditCardGroupName,
        String creditCardProviderUniqueId,
        String creditCardMaskedCardNumber,
        String creditCardFirstName,
        String creditCardLastName,
        String creditCardCompanyName,
        String creditCardEmail,
        String creditCardPhone,
        String creditCardFax,
        String creditCardCustomerTaxId,
        String creditCardStreetAddress1,
        String creditCardStreetAddress2,
        String creditCardCity,
        String creditCardState,
        String creditCardPostalCode,
        String creditCardCountryCode,
        String creditCardComments,
        long authorizationTime,
        String authorizationPrincipalName
    ) throws IOException, SQLException {
        return service.connector.getCreditCardTransactions().addCreditCardTransaction(
            processor,
            this,
            groupName,
            testMode,
            duplicateWindow,
            orderNumber,
            currencyCode,
            amount,
            taxAmount,
            taxExempt,
            shippingAmount,
            dutyAmount,
            shippingFirstName,
            shippingLastName,
            shippingCompanyName,
            shippingStreetAddress1,
            shippingStreetAddress2,
            shippingCity,
            shippingState,
            shippingPostalCode,
            shippingCountryCode,
            emailCustomer,
            merchantEmail,
            invoiceNumber,
            purchaseOrderNumber,
            description,
            creditCardCreatedBy,
            creditCardPrincipalName,
            creditCardAccounting,
            creditCardGroupName,
            creditCardProviderUniqueId,
            creditCardMaskedCardNumber,
            creditCardFirstName,
            creditCardLastName,
            creditCardCompanyName,
            creditCardEmail,
            creditCardPhone,
            creditCardFax,
            creditCardCustomerTaxId,
            creditCardStreetAddress1,
            creditCardStreetAddress2,
            creditCardCity,
            creditCardState,
            creditCardPostalCode,
            creditCardCountryCode,
            creditCardComments,
            authorizationTime,
            authorizationPrincipalName
        );
    }*/

    /* TODO
    public int addDisableLog(
        String disableReason
    ) throws IOException, SQLException {
        return service.connector.getDisableLogs().addDisableLog(this, disableReason);
    }*/

    /* TODO
    public void addNoticeLog(
        String billingContact,
        String emailAddress,
        int balance,
        String type,
        int transid
    ) throws IOException, SQLException {
	    service.connector.getNoticeLogs().addNoticeLog(
            pkey,
            billingContact,
            emailAddress,
            balance,
            type,
            transid
	);
    }

    public int addTransaction(
        Business sourceBusiness,
        BusinessAdministrator business_administrator,
        TransactionType type,
        String description,
        int quantity,
        int rate,
        PaymentType paymentType,
        String paymentInfo,
        CreditCardProcessor processor,
    	byte payment_confirmed
    ) throws IOException, SQLException {
    	return service.connector.getTransactions().addTransaction(
            this,
            sourceBusiness,
            business_administrator,
            type.pkey,
            description,
            quantity,
            rate,
            paymentType,
            paymentInfo,
            processor,
            payment_confirmed
        );
    }
    */

    /* TODO
    public void cancel(String cancelReason) throws IllegalArgumentException, IOException, SQLException {
        // Automatically disable if not already disabled
        if(disableLog==null) {
            new SimpleAOClient(service.connector).disableBusiness(pkey, "Account canceled");
        }

        // Now cancel the account
        if(cancelReason!=null && (cancelReason=cancelReason.trim()).length()==0) cancelReason=null;
        final String finalCancelReason = cancelReason;
        service.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.CANCEL_BUSINESS.ordinal());
                    out.writeUTF(pkey);
                    out.writeNullUTF(finalCancelReason);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    service.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }
    */
    /* TODO
    public boolean canCancel() throws IOException, SQLException {
        return canceled==null && !isRootBusiness();
    }

    public boolean isRootBusiness() throws IOException, SQLException {
        return pkey.equals(service.connector.getBusinesses().getRootAccounting());
    }

    public boolean canDisable() throws IOException, SQLException {
        // already disabled
        if(disableLog!=null) return false;

        if(isRootBusiness()) return false;

        // Can only disabled when all dependent objects are already disabled
        for(HttpdSharedTomcat hst : getHttpdSharedTomcats()) if(hst.disableLog==null) return false;
        for(EmailPipe ep : getEmailPipes()) if(ep.disableLog==null) return false;
        for(CvsRepository cr : getCvsRepositories()) if(cr.disableLog==null) return false;
        for(Username un : getUsernames()) if(un.disableLog==null) return false;
        for(HttpdSite hs : getHttpdSites()) if(hs.disableLog==null) return false;
        for(EmailList el : getEmailLists()) if(el.disableLog==null) return false;
        for(EmailSmtpRelay ssr : getEmailSmtpRelays()) if(ssr.disableLog==null) return false;

        return true;
    }

    public boolean canEnable() throws SQLException, IOException {
        // Cannot enable a canceled business
        if(canceled!=null) return false;

        // Can only enable if it is disabled
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable();
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        service.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.BUSINESSES, dl.pkey, pkey);
    }

    public void enable() throws IOException, SQLException {
        service.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.BUSINESSES, pkey);
    }

    public BigDecimal getAccountBalance() throws IOException, SQLException {
        return service.connector.getTransactions().getAccountBalance(pkey);
    }

    public BigDecimal getAccountBalance(long before) throws IOException, SQLException {
        return service.connector.getTransactions().getAccountBalance(pkey, before);
    }*/

    /**
     * @see  #getAccountBalance()
     */
    /*
    public String getAccountBalanceString() throws IOException, SQLException {
        return "$"+getAccountBalance().toPlainString();
    }*/

    /**
     * @see  #getAccountBalance(long)
     */
    /*
    public String getAccountBalanceString(long before) throws IOException, SQLException {
        return "$"+getAccountBalance(before).toPlainString();
    }

    public BigDecimal getAutoEnableMinimumPayment() throws IOException, SQLException {
        BigDecimal balance = getAccountBalance();
        if(balance.compareTo(BigDecimal.ZERO)<=0) return BigDecimal.valueOf(0, 2);
        BigDecimal minimum = balance.divide(BigDecimal.valueOf(2), RoundingMode.CEILING);
        if(minimum.compareTo(MINIMUM_PAYMENT)<0) minimum=MINIMUM_PAYMENT;
        if(minimum.compareTo(balance)>0) minimum=balance;
        return minimum;
    }*/

    /**
     * Gets the <code>Business</code> in the business tree that is one level down
     * from the top level business.
     */
    /* TODO
    public Business getTopLevelBusiness() throws IOException, SQLException {
        String rootAccounting=service.connector.getBusinesses().getRootAccounting();
        Business bu=this;
        Business tempParent;
        while((tempParent=bu.getParentBusiness())!=null && !tempParent.getAccounting().equals(rootAccounting)) bu=tempParent;
        return bu;
    }*/

    /**
     * Gets the <code>Business</code> the is responsible for paying the bills created by this business.
     */
    /* TODO
    public Business getAccountingBusiness() throws SQLException, IOException {
        Business bu=this;
        while(bu.bill_parent) {
            bu=bu.getParentBusiness();
            if(bu==null) throw new AssertionError("Unable to find the accounting business for '"+pkey+'\'');
        }
        return bu;
    }*/

    /**
     * Gets the <code>BusinessProfile</code> with the highest priority.
     */
    /* TODO
    public BusinessProfile getBusinessProfile() throws IOException, SQLException {
        return service.connector.getBusinessProfiles().getBusinessProfile(this);
    }*/

    /**
     * Gets a list of all <code>BusinessProfiles</code> for this <code>Business</code>
     * sorted with the highest priority profile at the zero index.
     */
    /* TODO
    public List<BusinessProfile> getBusinessProfiles() throws IOException, SQLException {
        return service.connector.getBusinessProfiles().getBusinessProfiles(this);
    }

    public BusinessServer getBusinessServer(Server server) throws IOException, SQLException {
        return service.connector.getBusinessServers().getBusinessServer(pkey, server.pkey);
    }

    public BigDecimal getConfirmedAccountBalance() throws IOException, SQLException {
    	return getService().getConnector().getTransactions().getConfirmedAccountBalance(pkey);
    }

    public BigDecimal getConfirmedAccountBalance(long before) throws IOException, SQLException {
    	return service.connector.getTransactions().getConfirmedAccountBalance(pkey, before);
    }

    public List<CreditCardProcessor> getCreditCardProcessors() throws IOException, SQLException {
    	return service.connector.getCreditCardProcessors().getIndexedRows(CreditCardProcessor.COLUMN_ACCOUNTING, pkey);
    }

    public List<CreditCardTransaction> getCreditCardTransactions() throws IOException, SQLException {
    	return getService().getConnector().getCreditCardTransactions().getIndexedRows(CreditCardTransaction.COLUMN_ACCOUNTING, pkey);
    }

    public List<CreditCardTransaction> getCreditCardTransactionsByCreditCardAccounting() throws IOException, SQLException {
    	return getService().getConnector().getCreditCardTransactions().getIndexedRows(CreditCardTransaction.COLUMN_CREDIT_CARD_ACCOUNTING, pkey);
    }

    public List<CreditCard> getCreditCards() throws IOException, SQLException {
    	return getService().getConnector().getCreditCards().getCreditCards(this);
    }

    public Server getDefaultServer() throws IOException, SQLException {
        // May be null when the account is canceled or not using servers
	return getService().getConnector().getBusinessServers().getDefaultServer(this);
    }

    public boolean isDisabled() {
        return disableLog!=null;
    }

    public List<EmailForwarding> getEmailForwarding() throws SQLException, IOException {
    	return getService().getConnector().getEmailForwardings().getEmailForwarding(this);
    }

    public List<EmailList> getEmailLists() throws IOException, SQLException {
    	return getService().getConnector().getEmailLists().getEmailLists(this);
    }

    public LinuxServerGroup getLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
    	return getService().getConnector().getLinuxServerGroups().getLinuxServerGroup(aoServer, this);
    }

    public List<LinuxAccount> getMailAccounts() throws IOException, SQLException {
	return getService().getConnector().getLinuxAccounts().getMailAccounts(this);
    }

    public CreditCard getMonthlyCreditCard() throws IOException, SQLException {
        return getService().getConnector().getCreditCards().getMonthlyCreditCard(this);
    }

    public List<MonthlyCharge> getMonthlyCharges() throws SQLException, IOException {
        return getService().getConnector().getMonthlyCharges().getMonthlyCharges(this);
    }

    public List<MonthlyCharge> getMonthlyChargesBySourceBusiness() throws SQLException, IOException {
        return getService().getConnector().getMonthlyCharges().getMonthlyChargesBySourceBusiness(this);
    }*/

    /**
     * Gets an approximation of the monthly rate paid by this account.  This is not guaranteed to
     * be exactly the same as the underlying accounting database processes.
     */
    /* TODO
    public BigDecimal getMonthlyRate() throws SQLException, IOException {
        BigDecimal total = BigDecimal.valueOf(0, 2);
        for(MonthlyCharge mc : getMonthlyCharges()) if(mc.isActive()) total = total.add(new BigDecimal(SQLUtility.getDecimal(mc.getPennies())));
        return total;
    }*/

    /**
     * @see  #getMonthlyRate()
     */
    /* TODO
    public String getMonthlyRateString() throws SQLException, IOException {
        return "$"+getMonthlyRate();
    }

    public List<NoticeLog> getNoticeLogs() throws IOException, SQLException {
        return getService().getConnector().getNoticeLogs().getNoticeLogs(this);
    }

    public List<EmailDomain> getEmailDomains() throws SQLException, IOException {
        return getService().getConnector().getEmailDomains().getEmailDomains(this);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.BUSINESSES;
    }
    */
    /**
     * Gets the total monthly rate or <code>null</code> if unavailable.
     */
    /* TODO
    public BigDecimal getTotalMonthlyRate() throws SQLException, IOException {
        BigDecimal sum = BigDecimal.valueOf(0, 2);
        BigDecimal monthlyRate = getPackageDefinition().getMonthlyRate();
        if(monthlyRate==null) return null;
        sum = sum.add(monthlyRate);
        return sum;
    }

    public List<Transaction> getTransactions() throws IOException, SQLException {
        return getService().getConnector().getTransactions().getTransactions(pkey);
    }

    public List<WhoisHistory> getWhoisHistory() throws IOException, SQLException {
        return getService().getConnector().getWhoisHistory().getWhoisHistory(this);
    }
    */
    /**
     * @deprecated  Please use <code>isBusinessOrParentOf</code> instead.
     */
    /* TODO
    public boolean isBusinessOrParent(Business other) throws IOException, SQLException {
        return isBusinessOrParentOf(other);
    }
     */

    /**
     * Determines if this <code>Business</code> is the other business
     * or a parent of it.  This is often used for access control between
     * accounts.
     */
    /* TODO
    public boolean isBusinessOrParentOf(Business other) throws IOException, SQLException {
        while(other!=null) {
            if(equals(other)) return true;
            other=other.getParentBusiness();
        }
        return false;
    }
     */

    /**
     * Determines if this <code>Business</code> is a parent of the other business.
     * This is often used for access control between accounts.
     */
    /* TODO
    public boolean isParentOf(Business other) throws IOException, SQLException {
        if(other!=null) {
            other=other.getParentBusiness();
            while(other!=null) {
                if(equals(other)) return true;
                other=other.getParentBusiness();
            }
        }
        return false;
    }

    public void setAccounting(String accounting) throws SQLException, IOException {
        if(!isValidAccounting(accounting)) throw new SQLException("Invalid accounting code: "+accounting);
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_BUSINESS_ACCOUNTING, this.pkey, accounting);
    }

    public List<Ticket> getTickets() throws SQLException, IOException {
        return getService().getConnector().getTickets().getTickets(this);
    }
    */
    /**
     * Gets all of the encryption keys for this business.
     */
    /* TODO
    public List<EncryptionKey> getEncryptionKeys() throws IOException, SQLException {
        return getService().getConnector().getEncryptionKeys().getEncryptionKeys(this);
    }*/

    /**
     * Sets the credit card that will be used monthly.  Any other selected card will
     * be deselected.  If <code>creditCard</code> is null, none will be used automatically.
     */
    /*
    public void setUseMonthlyCreditCard(CreditCard creditCard) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.SET_CREDIT_CARD_USE_MONTHLY,
            pkey,
            creditCard==null ? Integer.valueOf(-1) : Integer.valueOf(creditCard.getPkey())
        );
    }*/

    /**
     * Gets the most recent credit card transaction.
     */
    /*
    public CreditCardTransaction getLastCreditCardTransaction() throws IOException, SQLException {
        return getService().getConnector().getCreditCardTransactions().getLastCreditCardTransaction(this);
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
        return getService().getConnector().getPackageDefinitions().addPackageDefinition(
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
        return getService().getConnector().getPackageDefinitions().getPackageDefinition(this, category, name, version);
    }

    public List<PackageDefinition> getPackageDefinitions(PackageCategory category) throws IOException, SQLException {
        return getService().getConnector().getPackageDefinitions().getPackageDefinitions(this, category);
    }*/

    /**
     * Gets all active package definitions for this business.
     */
    /*
    public Map<PackageCategory,List<PackageDefinition>> getActivePackageDefinitions() throws IOException, SQLException {
        // Determine the active packages per category
        List<PackageCategory> allCategories = getService().getConnector().getPackageCategories().getRows();
        Map<PackageCategory,List<PackageDefinition>> categories = new LinkedHashMap<PackageCategory,List<PackageDefinition>>(allCategories.size()*4/3+1);
        for(PackageCategory category : allCategories) {
            List<PackageDefinition> allDefinitions = getPackageDefinitions(category);
            List<PackageDefinition> definitions = new ArrayList<PackageDefinition>(allDefinitions.size());
            for(PackageDefinition definition : allDefinitions) {
                if(definition.isActive()) definitions.add(definition);
            }
            if(!definitions.isEmpty()) categories.put(category, Collections.unmodifiableList(definitions));
        }
        return Collections.unmodifiableMap(categories);
    }*/

    /*
    public void addDnsZone(String zone, String ip, int ttl) throws IOException, SQLException {
	    getService().getConnector().getDnsZones().addDnsZone(this, zone, ip, ttl);
    }

    public int addEmailSmtpRelay(AOServer aoServer, String host, EmailSmtpRelayType type, long duration) throws IOException, SQLException {
        return getService().getConnector().getEmailSmtpRelays().addEmailSmtpRelay(this, aoServer, host, type, duration);
    }

    public void addLinuxGroup(String name, LinuxGroupType type) throws IOException, SQLException {
    	addLinuxGroup(name, type.pkey);
    }

    public void addLinuxGroup(String name, String type) throws IOException, SQLException {
	    getService().getConnector().getLinuxGroups().addLinuxGroup(name, this, type);
    }

    public void addUsername(String username) throws IOException, SQLException {
	    getService().getConnector().getUsernames().addUsername(this, username);
    }

    public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
        return getService().getConnector().getCvsRepositories().getCvsRepositories(this);
    }

    public List<DnsZone> getDnsZones() throws IOException, SQLException {
        return getService().getConnector().getDnsZones().getDnsZones(this);
    }

    public List<EmailPipe> getEmailPipes() throws IOException, SQLException {
        return getService().getConnector().getEmailPipes().getEmailPipes(this);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
        return getService().getConnector().getHttpdSharedTomcats().getHttpdSharedTomcats(this);
    }

    public List<HttpdServer> getHttpdServers() throws IOException, SQLException {
        return getService().getConnector().getHttpdServers().getHttpdServers(this);
    }

    public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
        return getService().getConnector().getHttpdSites().getHttpdSites(this);
    }

    public List<IPAddress> getIPAddresses() throws IOException, SQLException {
        return getService().getConnector().getIpAddresses().getIPAddresses(this);
    }

    public List<LinuxGroup> getLinuxGroups() throws IOException, SQLException {
        return getService().getConnector().getLinuxGroups().getLinuxGroups(this);
    }

    public List<MySQLDatabase> getMysqlDatabases() throws IOException, SQLException {
        return getService().getConnector().getMysqlDatabases().getMySQLDatabases(this);
    }
    */

    /*
    public List<FailoverMySQLReplication> getFailoverMySQLReplications() throws IOException, SQLException {
        return getService().getConnector().getFailoverMySQLReplications().getFailoverMySQLReplications(this);
    }

    public List<MySQLServer> getMysqlServers() throws IOException, SQLException {
        return getService().getConnector().getMysqlServers().getMySQLServers(this);
    }
     */

    /*
    public List<MySQLUser> getMysqlUsers() throws IOException, SQLException {
        return getService().getConnector().getMysqlUsers().getMySQLUsers(this);
    }*/

    /*
    public List<NetBind> getNetBinds() throws IOException, SQLException {
        return getService().getConnector().getNetBinds().getNetBinds(this);
    }

    public List<NetBind> getNetBinds(IPAddress ip) throws IOException, SQLException {
        return getService().getConnector().getNetBinds().getNetBinds(this, ip);
    }
     */

    /*
    public List<PostgresDatabase> getPostgresDatabases() throws IOException, SQLException {
        return getService().getConnector().getPostgresDatabases().getPostgresDatabases(this);
    }

    public List<PostgresUser> getPostgresUsers() throws SQLException, IOException {
        return getService().getConnector().getPostgresUsers().getPostgresUsers(this);
    }

    public Server getServer(String name) throws IOException, SQLException {
        // Use index first
        for(Server se : getServers()) if(se.getName().equals(name)) return se;
        return null;
    }

    public List<EmailSmtpRelay> getEmailSmtpRelays() throws IOException, SQLException {
        return getService().getConnector().getEmailSmtpRelays().getEmailSmtpRelays(this);
    }

    public List<PackageDefinition> getPackageDefinitions() throws IOException, SQLException {
        return getService().getConnector().getPackageDefinitions().getIndexedRows(PackageDefinition.COLUMN_ACCOUNTING, pkey);
    }

    public List<TicketAction> getTicketActionsByOldBusiness() throws IOException, SQLException {
        return getService().getConnector().getTicketActions().getIndexedRows(TicketAction.COLUMN_OLD_ACCOUNTING, pkey);
    }

    public List<TicketAction> getTicketActionsByNewBusiness() throws IOException, SQLException {
        return getService().getConnector().getTicketActions().getIndexedRows(TicketAction.COLUMN_NEW_ACCOUNTING, pkey);
    }

    public List<Transaction> getTransactionsBySourceAccounting() throws IOException, SQLException {
        return getService().getConnector().getTransactions().getIndexedRows(Transaction.COLUMN_SOURCE_ACCOUNTING, pkey);
    }
     */
    // </editor-fold>
}
