/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.AoCollections;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.i18n.CurrencyComparator;
import com.aoindustries.util.i18n.Money;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.security.Principal;
import java.security.acl.Group;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A <code>Business</code> is one distinct set of resources and permissions.
* Some businesses may have child businesses associated with them.  When that is the
 * case, the top level business is ultimately responsible for all actions taken and
 * resources used by itself and all child businesses.
 *
 * @author  AO Industries, Inc.
 */
final public class Business
extends AOServObjectAccountingCodeKey
implements
    Comparable<Business>,
    DtoFactory<com.aoindustries.aoserv.client.dto.Business>,
    Group /* TODO: implements Disablable*/ {

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
    final private long created;
    final private Long canceled;
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
        AOServConnector connector,
        AccountingCode accounting,
        String contractVersion,
        long created,
        Long canceled,
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
        super(connector, accounting);
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
    
    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(Business other) {
        return getKey().compareTo(other.getKey());
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
    	return new Timestamp(created);
    }

    @SchemaColumn(order=3, name="canceled", description="the time the account was deactivated")
    public Timestamp getCanceled() {
    	return canceled==null ? null : new Timestamp(canceled);
    }

    @SchemaColumn(order=4, name="cancel_reason", description="the reason the account was canceled")
    public String getCancelReason() {
        return cancelReason;
    }

    static final String COLUMN_PARENT = "parent";
    /**
     * Gets the parent of this business or <code>null</code> if filtered or is top-level business.
     */
    @DependencySingleton
    @SchemaColumn(order=5, name=COLUMN_PARENT, index=IndexType.INDEXED, description="the parent business to this one")
    public Business getParentBusiness() throws RemoteException {
        if(parent==null) return null;
        return getConnector().getBusinesses().filterUnique(COLUMN_ACCOUNTING, parent);
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
    // Caused cycle in dependency DAG: @DependencySingleton
    @SchemaColumn(order=9, name=COLUMN_DISABLE_LOG, index=IndexType.INDEXED, description="indicates the business is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        return getConnector().getDisableLogs().get(disableLog);
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

    /**
     * May be filtered.
     */
    static final String COLUMN_PACKAGE_DEFINITION = "package_definition";
    @DependencySingleton
    @SchemaColumn(order=13, name=COLUMN_PACKAGE_DEFINITION, index=IndexType.INDEXED, description="the definition of the package")
    public PackageDefinition getPackageDefinition() throws RemoteException {
        return getConnector().getPackageDefinitions().filterUnique(PackageDefinition.COLUMN_PKEY, packageDefinition);
    }

    /**
     * May be filtered.  May also be null for the root business only.
     */
    static final String COLUMN_CREATED_BY = "created_by";
    @DependencySingleton
    @SchemaColumn(order=14, name=COLUMN_CREATED_BY, index=IndexType.INDEXED, description="the user who added this business")
    public BusinessAdministrator getCreatedBy() throws RemoteException {
        if(createdBy==null) return null;
        try {
            return getConnector().getBusinessAdministrators().get(createdBy);
        } catch(NoSuchElementException err) {
            // Filtered
            return null;
        }
    }

    /**
     * Gets the inbound burst limit for emails, the number of emails that may be sent before limiting occurs.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=15, name="email_in_burst", description="the maximum burst of inbound email before limiting begins")
    public Integer getEmailInBurst() {
        return emailInBurst;
    }

    /**
     * Gets the inbound sustained email rate in emails/second.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=16, name="email_in_rate", description="the number of sustained inbound emails per second")
    public Float getEmailInRate() {
        return emailInRate;
    }

    /**
     * Gets the outbound burst limit for emails, the number of emails that may be sent before limiting occurs.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=17, name="email_out_burst", description="the maximum burst of outbound email before limiting begins")
    public Integer getEmailOutBurst() {
        return emailOutBurst;
    }

    /**
     * Gets the outbound sustained email rate in emails/second.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=18, name="email_out_rate", description="the number of sustained outbound emails per second")
    public Float getEmailOutRate() {
        return emailOutRate;
    }

    /**
     * Gets the relay burst limit for emails, the number of emails that may be sent before limiting occurs.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=19, name="email_relay_burst", description="the maximum burst of relay email before limiting begins")
    public Integer getEmailRelayBurst() {
        return emailRelayBurst;
    }

    /**
     * Gets the relay sustained email rate in emails/second.
     * A value of <code>null</code> indicates unlimited.
     */
    @SchemaColumn(order=20, name="email_relay_rate", description="the number of sustained relay emails per second")
    public Float getEmailRelayRate() {
        return emailRelayRate;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public Business(AOServConnector connector, com.aoindustries.aoserv.client.dto.Business dto) throws ValidationException {
        this(
            connector,
            getAccountingCode(dto.getAccounting()),
            dto.getContractVersion(),
            getTimeMillis(dto.getCreated()),
            getTimeMillis(dto.getCanceled()),
            dto.getCancelReason(),
            getAccountingCode(dto.getParent()),
            dto.isCanAddBackupServer(),
            dto.isCanAddBusinesses(),
            dto.isCanSeePrices(),
            dto.getDisableLog(),
            dto.getDoNotDisableReason(),
            dto.isAutoEnable(),
            dto.isBillParent(),
            dto.getPackageDefinition(),
            getUserId(dto.getCreatedBy()),
            dto.getEmailInBurst(),
            dto.getEmailInRate(),
            dto.getEmailOutBurst(),
            dto.getEmailOutRate(),
            dto.getEmailRelayBurst(),
            dto.getEmailRelayRate()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.Business getDto() {
        return new com.aoindustries.aoserv.client.dto.Business(getDto(getKey()), contractVersion, created, canceled, cancelReason, getDto(parent), canAddBackupServer, canAddBusinesses, canSeePrices, disableLog, doNotDisableReason, autoEnable, billParent, packageDefinition, getDto(createdBy), emailInBurst, emailInRate, emailOutBurst, emailOutRate, emailRelayBurst, emailRelayRate);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /**
     * Gets the Brand for this business or <code>null</code> if not a brand.
     */
    @DependentObjectSet
    public IndexedSet<AOServRole> getAoservRoles() throws RemoteException {
        return getConnector().getAoservRoles().filterIndexed(AOServRole.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSingleton
    public Brand getBrand() throws RemoteException {
        return getConnector().getBrands().filterUnique(Brand.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<Business> getChildBusinesses() throws RemoteException {
        return getConnector().getBusinesses().filterIndexed(COLUMN_PARENT, this);
    }

    @DependentObjectSet
    public IndexedSet<BusinessServer> getBusinessServers() throws RemoteException {
        return getConnector().getBusinessServers().filterIndexed(BusinessServer.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<DisableLog> getDisableLogs() throws RemoteException {
        return getConnector().getDisableLogs().filterIndexed(DisableLog.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<GroupName> getGroupNames() throws RemoteException {
        return getConnector().getGroupNames().filterIndexed(GroupName.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<Resource> getResources() throws RemoteException {
        return getConnector().getResources().filterIndexed(Resource.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<PackageDefinitionBusiness> getPackageDefinitionBusinesses() throws RemoteException {
        return getConnector().getPackageDefinitionBusinesses().filterIndexed(PackageDefinitionBusiness.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<Server> getServers() throws RemoteException {
        return getConnector().getServers().filterIndexed(Server.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<ServerFarm> getServerFarms() throws RemoteException {
        return getConnector().getServerFarms().filterIndexed(ServerFarm.COLUMN_OWNER, this);
    }

    @DependentObjectSet
    public IndexedSet<Username> getUsernames() throws RemoteException {
        return getConnector().getUsernames().filterIndexed(Username.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<CreditCardProcessor> getCreditCardProcessors() throws RemoteException {
    	return getConnector().getCreditCardProcessors().filterIndexed(CreditCardProcessor.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<CreditCard> getCreditCards() throws RemoteException {
    	return getConnector().getCreditCards().filterIndexed(CreditCard.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<CreditCardTransaction> getCreditCardTransactions() throws RemoteException {
    	return getConnector().getCreditCardTransactions().filterIndexed(CreditCardTransaction.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<CreditCardTransaction> getCreditCardTransactionsByCreditCardAccounting() throws RemoteException {
    	return getConnector().getCreditCardTransactions().filterIndexed(CreditCardTransaction.COLUMN_CREDIT_CARD_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<TicketAction> getTicketActionsByOldBusiness() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_OLD_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<TicketAction> getTicketActionsByNewBusiness() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_NEW_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<Ticket> getTickets() throws RemoteException {
        return getConnector().getTickets().filterIndexed(Ticket.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<Transaction> getTransactions() throws RemoteException {
        return getConnector().getTransactions().filterIndexed(Transaction.COLUMN_ACCOUNTING, this);
    }

    @DependentObjectSet
    public IndexedSet<Transaction> getTransactionsBySourceAccounting() throws RemoteException {
        return getConnector().getTransactions().filterIndexed(Transaction.COLUMN_SOURCE_ACCOUNTING, this);
    }

    /**
     * Determines if this <code>Business</code> is the other business
     * or a parent of it.  This is often used for access control between
     * accounts.
     */
    public boolean isBusinessOrParentOf(Business other) throws RemoteException {
        while(other!=null) {
            if(equals(other)) return true;
            other = other.getParentBusiness();
        }
        return false;
    }

    /**
     * Gets all of the <code>BusinessProfile</code>s for this <code>Business</code>.
     */
    @DependentObjectSet
    public IndexedSet<BusinessProfile> getBusinessProfiles() throws RemoteException {
        return getConnector().getBusinessProfiles().filterIndexed(BusinessProfile.COLUMN_ACCOUNTING, this);
    }

    /**
     * Gets the <code>BusinessProfile</code> with the highest priority or <code>null</code> if there
     * are no business profiles for this <code>Business</code>.
     */
    public BusinessProfile getBusinessProfile() throws RemoteException {
        return Collections.min(getBusinessProfiles());
    }

    /**
     * Gets all active package definitions for this business.
     */
    /* TODO
    public Map<PackageCategory,List<PackageDefinition>> getActivePackageDefinitions() throws IOException, SQLException {
        // Determine the active packages per category
        List<PackageCategory> allCategories = getConnector().getPackageCategories().getRows();
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Group">
    /**
     * A group contains all users of its own business plus all parent businesses.
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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

    @Override
    public String getName() {
        return getKey().toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Account Balances">
    /**
     * <p>
     * Gets the account balances for this account.  This will always include
     * the currencies used for any package that is part of the business, even
     * if there is not any transactions for that currency.
     * </p>
     * <p>
     * If the business has no packages, will default to $0.00.
     * </p>
     */
    public SortedMap<Currency,Money> getAccountBalances() throws RemoteException {
        SortedMap<Currency,Money> totals = new TreeMap<Currency,Money>(CurrencyComparator.getInstance());
        // Add the currency that is used by the package definition of this account
        Currency pdCurrency = getPackageDefinition().getMonthlyRate().getCurrency();
        totals.put(pdCurrency, new Money(pdCurrency, BigDecimal.ZERO));

        // Get the total for each currency
        for(Transaction tr : getTransactions()) {
            if(tr.getStatus()!=Transaction.Status.N) {
                Money amount = tr.getAmount();
                Currency currency = amount.getCurrency();
                Money total = totals.get(currency);
                if(total==null) total = amount;
                else total = total.add(amount);
                totals.put(currency, total);
            }
        }

        // Add $0.00 if there is no balance yet
        if(totals.isEmpty()) {
            Currency usd = Currency.getInstance("USD");
            totals.put(usd, new Money(usd, BigDecimal.ZERO));
        }
        return AoCollections.optimalUnmodifiableSortedMap(totals);
    }

    /**
     * Determines if this account has any non-zero balance in any currency.
     */
    public boolean hasNonZeroBalance() throws RemoteException {
        for(Money balance : getAccountBalances().values()) {
            if(balance.getValue().compareTo(BigDecimal.ZERO)!=0) return true;
        }
        return false;
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
        return getConnector().getBusinessProfiles().addBusinessProfile(
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
        return getConnector().getBusinessServers().addBusinessServer(this, server);
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
        return getConnector().getCreditCardTransactions().addCreditCardTransaction(
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
        return getConnector().getDisableLogs().addDisableLog(this, disableReason);
    }*/

    /* TODO
    public void addNoticeLog(
        String billingContact,
        String emailAddress,
        int balance,
        String type,
        int transid
    ) throws IOException, SQLException {
	    getConnector().getNoticeLogs().addNoticeLog(
            pkey,
            billingContact,
            emailAddress,
            balance,
            type,
            transid
	);
    }

    public boolean isRootBusiness() throws IOException, SQLException {
        return pkey.equals(getConnector().getBusinesses().getRootAccounting());
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

    public BigDecimal getAccountBalance(long before) throws IOException, SQLException {
        return getConnector().getTransactions().getAccountBalance(pkey, before);
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
        String rootAccounting=getConnector().getBusinesses().getRootAccounting();
        Business bu=this;
        Business tempParent;
        while((tempParent=bu.getParentBusiness())!=null && !tempParent.getAccounting()==rootAccounting) bu=tempParent; // OK - interned
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
    }

    public BusinessServer getBusinessServer(Server server) throws IOException, SQLException {
        return getConnector().getBusinessServers().getBusinessServer(pkey, server.pkey);
    }

    public BigDecimal getConfirmedAccountBalance() throws IOException, SQLException {
    	return getConnector().getTransactions().getConfirmedAccountBalance(pkey);
    }

    public BigDecimal getConfirmedAccountBalance(long before) throws IOException, SQLException {
    	return getConnector().getTransactions().getConfirmedAccountBalance(pkey, before);
    }

    public Server getDefaultServer() throws IOException, SQLException {
        // May be null when the account is canceled or not using servers
	return getConnector().getBusinessServers().getDefaultServer(this);
    }

    public boolean isDisabled() {
        return disableLog!=null;
    }

    public List<EmailForwarding> getEmailForwarding() throws SQLException, IOException {
    	return getConnector().getEmailForwardings().getEmailForwarding(this);
    }

    public List<EmailList> getEmailLists() throws IOException, SQLException {
    	return getConnector().getEmailLists().getEmailLists(this);
    }

    public LinuxServerGroup getLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
    	return getConnector().getLinuxServerGroups().getLinuxServerGroup(aoServer, this);
    }

    public List<LinuxAccount> getMailAccounts() throws IOException, SQLException {
	return getConnector().getLinuxAccounts().getMailAccounts(this);
    }

    public CreditCard getMonthlyCreditCard() throws IOException, SQLException {
        return getConnector().getCreditCards().getMonthlyCreditCard(this);
    }

    public List<MonthlyCharge> getMonthlyCharges() throws SQLException, IOException {
        return getConnector().getMonthlyCharges().getMonthlyCharges(this);
    }

    public List<MonthlyCharge> getMonthlyChargesBySourceBusiness() throws SQLException, IOException {
        return getConnector().getMonthlyCharges().getMonthlyChargesBySourceBusiness(this);
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
        return getConnector().getNoticeLogs().getNoticeLogs(this);
    }

    public List<EmailDomain> getEmailDomains() throws SQLException, IOException {
        return getConnector().getEmailDomains().getEmailDomains(this);
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

    public List<WhoisHistory> getWhoisHistory() throws IOException, SQLException {
        return getConnector().getWhoisHistory().getWhoisHistory(this);
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
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_BUSINESS_ACCOUNTING, this.pkey, accounting);
    }
    */
    /**
     * Gets all of the encryption keys for this business.
     */
    /* TODO
    public List<EncryptionKey> getEncryptionKeys() throws IOException, SQLException {
        return getConnector().getEncryptionKeys().getEncryptionKeys(this);
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
        return getConnector().getPackageDefinitions().addPackageDefinition(
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
        return getConnector().getPackageDefinitions().getPackageDefinition(this, category, name, version);
    }

    public List<PackageDefinition> getPackageDefinitions(PackageCategory category) throws IOException, SQLException {
        return getConnector().getPackageDefinitions().getPackageDefinitions(this, category);
    }

    public void addDnsZone(String zone, String ip, int ttl) throws IOException, SQLException {
	    getConnector().getDnsZones().addDnsZone(this, zone, ip, ttl);
    }

    public int addEmailSmtpRelay(AOServer aoServer, String host, EmailSmtpRelayType type, long duration) throws IOException, SQLException {
        return getConnector().getEmailSmtpRelays().addEmailSmtpRelay(this, aoServer, host, type, duration);
    }

    public void addLinuxGroup(String name, LinuxGroupType type) throws IOException, SQLException {
    	addLinuxGroup(name, type.pkey);
    }

    public void addLinuxGroup(String name, String type) throws IOException, SQLException {
	    getConnector().getLinuxGroups().addLinuxGroup(name, this, type);
    }

    public void addUsername(String username) throws IOException, SQLException {
	    getConnector().getUsernames().addUsername(this, username);
    }

    public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
        return getConnector().getCvsRepositories().getCvsRepositories(this);
    }

    public List<DnsZone> getDnsZones() throws IOException, SQLException {
        return getConnector().getDnsZones().getDnsZones(this);
    }

    public List<EmailPipe> getEmailPipes() throws IOException, SQLException {
        return getConnector().getEmailPipes().getEmailPipes(this);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
        return getConnector().getHttpdSharedTomcats().getHttpdSharedTomcats(this);
    }

    public List<HttpdServer> getHttpdServers() throws IOException, SQLException {
        return getConnector().getHttpdServers().getHttpdServers(this);
    }

    public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
        return getConnector().getHttpdSites().getHttpdSites(this);
    }

    public List<IPAddress> getIPAddresses() throws IOException, SQLException {
        return getConnector().getIpAddresses().getIPAddresses(this);
    }

    public List<LinuxGroup> getLinuxGroups() throws IOException, SQLException {
        return getConnector().getLinuxGroups().getLinuxGroups(this);
    }

    public List<MySQLDatabase> getMysqlDatabases() throws IOException, SQLException {
        return getConnector().getMysqlDatabases().getMySQLDatabases(this);
    }
    */

    /*
    public List<FailoverMySQLReplication> getFailoverMySQLReplications() throws IOException, SQLException {
        return getConnector().getFailoverMySQLReplications().getFailoverMySQLReplications(this);
    }

    public List<MySQLServer> getMysqlServers() throws IOException, SQLException {
        return getConnector().getMysqlServers().getMySQLServers(this);
    }
     */

    /*
    public List<MySQLUser> getMysqlUsers() throws IOException, SQLException {
        return getConnector().getMysqlUsers().getMySQLUsers(this);
    }*/

    /*
    public List<NetBind> getNetBinds() throws IOException, SQLException {
        return getConnector().getNetBinds().getNetBinds(this);
    }

    public List<NetBind> getNetBinds(IPAddress ip) throws IOException, SQLException {
        return getConnector().getNetBinds().getNetBinds(this, ip);
    }
     */

    /*
    public List<PostgresDatabase> getPostgresDatabases() throws IOException, SQLException {
        return getConnector().getPostgresDatabases().getPostgresDatabases(this);
    }

    public List<PostgresUser> getPostgresUsers() throws SQLException, IOException {
        return getConnector().getPostgresUsers().getPostgresUsers(this);
    }

    public Server getServer(String name) throws IOException, SQLException {
        // Use index first
        for(Server se : getServers()) if(se.getName()==name) return se; // OK - interned
        return null;
    }

    public List<EmailSmtpRelay> getEmailSmtpRelays() throws IOException, SQLException {
        return getConnector().getEmailSmtpRelays().getEmailSmtpRelays(this);
    }
     */
    // </editor-fold>
}
