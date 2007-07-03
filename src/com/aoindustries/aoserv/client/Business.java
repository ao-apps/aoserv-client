package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>Business</code> is one distinct set of packages, resources, and permissions.
* Some businesses may have child businesses associated with them.  When that is the
 * case, the top level business is ultimately responsible for all actions taken and
 * resources used by itself and all child businesses.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Business extends CachedObjectStringKey<Business> implements Disablable {

    /**
     * The maximum depth of the business tree.
     */
    public static final int MAXIMUM_BUSINESS_TREE_DEPTH=7;

    static final int COLUMN_ACCOUNTING=0;

    /**
     * The minimum payment for auto-enabling accounts, in pennies.
     */
    public static final int MINIMUM_PAYMENT=3000;

    String contractVersion;
    private long created;

    private long canceled;

    private String cancelReason;

    String parent;

    private boolean can_add_backup_server;
    private boolean can_add_businesses;
    private boolean can_see_prices;

    int disable_log;
    private String do_not_disable_reason;
    private boolean auto_enable;
    private boolean bill_parent;

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
	return table.connector.businessProfiles.addBusinessProfile(
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
	Server server,
        boolean can_configure_backup
    ) {
	return table.connector.businessServers.addBusinessServer(this, server, can_configure_backup);
    }

    public int addCreditCard(
        String cardInfo,
        CreditCardProcessor processor,
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
        String description
    ) {
	return table.connector.creditCards.addCreditCard(
            this,
            cardInfo,
            processor,
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
            description
	);
    }

    public int addDisableLog(
        String disableReason
    ) {
        return table.connector.disableLogs.addDisableLog(this, disableReason);
    }

    public void addNoticeLog(
	String billingContact,
	String emailAddress,
	int balance,
	String type,
	int transid
    ) {
	table.connector.noticeLogs.addNoticeLog(
            pkey,
            billingContact,
            emailAddress,
            balance,
            type,
            transid
	);
    }

    public int addPackage(
	String name,
        PackageDefinition packageDefinition
    ) {
	return table.connector.packages.addPackage(
            name,
            this,
            packageDefinition
	);
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
    ) {
        return table.connector.packageDefinitions.addPackageDefinition(
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

    public int addTransaction(
        Business sourceBusiness,
	BusinessAdministrator business_administrator,
	TransactionType type,
	String description,
	int quantity,
	int rate,
        PaymentType paymentType,
        String paymentInfo,
	byte payment_confirmed
    ) {
	return table.connector.transactions.addTransaction(
            this,
            sourceBusiness,
            business_administrator,
            type.pkey,
            description,
            quantity,
            rate,
            paymentType,
            paymentInfo,
            payment_confirmed
	);
    }

    public boolean canAddBackupServer() {
        return can_add_backup_server;
    }

    public boolean canAddBusinesses() {
	return can_add_businesses;
    }

    public void cancel(String cancelReason) {
        try {
            // Automatically disable if not already disabled
            if(disable_log==-1) {
                new SimpleAOClient(table.connector).disableBusiness(pkey, "Account canceled");
            }

            // Now cancel the account
            IntList invalidateList;
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.CANCEL_BUSINESS.ordinal());
                out.writeUTF(pkey);
                if(cancelReason!=null && (cancelReason=cancelReason.trim()).length()==0) cancelReason=null;
                out.writeNullUTF(cancelReason);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
            table.connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public boolean canCancel() {
        return canceled==-1 && !isRootBusiness();
    }
    
    public boolean isRootBusiness() {
        return pkey.equals(table.connector.businesses.getRootAccounting());
    }

    public boolean canDisable() {
        // already disabled
        if(disable_log!=-1) return false;
        
        if(isRootBusiness()) return false;

        // packages
        for(Package pk : getPackages()) if(pk.disable_log==-1) return false;
        
        return true;
    }

    public boolean canEnable() {
        // Cannot enable a canceled business
        if(canceled!=-1) return false;

        // Can only enable if it is disabled
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable();
    }

    public boolean canSeePrices() {
        return can_see_prices;
    }

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.BUSINESSES, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.BUSINESSES, pkey);
    }

    public int getAccountBalance() {
	return table.connector.transactions.getAccountBalance(pkey);
    }

    public int getAccountBalance(long before) {
	return table.connector.transactions.getAccountBalance(pkey, before);
    }

    public String getAccounting() {
	return pkey;
    }

    public boolean getAutoEnable() {
        return auto_enable;
    }
    
    public boolean billParent() {
        return bill_parent;
    }
    
    public int getAutoEnableMinimumPayment() {
        int balance=getAccountBalance();
        if(balance<0) return 0;
        int minimum=balance/2;
        if(minimum<MINIMUM_PAYMENT) minimum=MINIMUM_PAYMENT;
        if(minimum>balance) minimum=balance;
        return minimum;
    }

    public String getDoNotDisableReason() {
        return do_not_disable_reason;
    }
    
    /**
     * Gets the <code>Business</code> in the business tree that is one level down
     * from the top level business.
     */
    public Business getTopLevelBusiness() {
        String rootAccounting=table.connector.businesses.getRootAccounting();
        Business bu=this;
        Business parent;
        while((parent=bu.getParentBusiness())!=null && !parent.getAccounting().equals(rootAccounting)) bu=parent;
        return bu;
    }

    /**
     * Gets the <code>Business</code> the is responsible for paying the bills created by this business.
     */
    public Business getAccountingBusiness() {
        Business bu=this;
        while(bu.bill_parent) {
            bu=bu.getParentBusiness();
            if(bu==null) throw new WrappedException(new SQLException("Unable to find the accounting business for '"+pkey+'\''));
        }
        return bu;
    }

    /**
     * Gets the <code>BusinessProfile</code> with the highest priority.
     */
    public BusinessProfile getBusinessProfile() {
	return table.connector.businessProfiles.getBusinessProfile(this);
    }

    /**
     * Gets a list of all <code>BusinessProfiles</code> for this <code>Business</code>
     * sorted with the highest priority profile at the zero index.
     */
    public List<BusinessProfile> getBusinessProfiles() {
	return table.connector.businessProfiles.getBusinessProfiles(this);
    }

    public BusinessServer getBusinessServer(
	Server server
    ) {
	return table.connector.businessServers.getBusinessServer(this, server);
    }
    
    public List<BusinessServer> getBusinessServers() {
        return table.connector.businessServers.getBusinessServers(this);
    }

    public long getCanceled() {
	return canceled;
    }

    public String getCancelReason() {
	return cancelReason;
    }

    public List<Business> getChildBusinesses() {
	return table.connector.businesses.getChildBusinesses(this);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_ACCOUNTING: return pkey;
            case 1: return contractVersion;
            case 2: return new java.sql.Date(created);
            case 3: return canceled==-1?null:new java.sql.Date(canceled);
            case 4: return cancelReason;
            case 5: return parent;
            case 6: return can_add_backup_server?Boolean.TRUE:Boolean.FALSE;
            case 7: return can_add_businesses?Boolean.TRUE:Boolean.FALSE;
            case 8: return can_see_prices?Boolean.TRUE:Boolean.FALSE;
            case 9: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 10: return do_not_disable_reason;
            case 11: return auto_enable?Boolean.TRUE:Boolean.FALSE;
            case 12: return bill_parent?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getConfirmedAccountBalance() {
	return table.connector.transactions.getConfirmedAccountBalance(pkey);
    }

    public int getConfirmedAccountBalance(long before) {
	return table.connector.transactions.getConfirmedAccountBalance(pkey, before);
    }

    public String getContractVersion() {
	return contractVersion;
    }

    public long getCreated() {
	return created;
    }

    public List<CreditCardProcessor> getCreditCardProcessors() {
	return table.connector.creditCardProcessors.getCreditCardProcessors(this);
    }

    public List<CreditCard> getCreditCards() {
	return table.connector.creditCards.getCreditCards(this);
    }

    public Server getDefaultServer() {
        // May be null when the account is canceled or not using servers
	return table.connector.businessServers.getDefaultServer(this);
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public List<EmailForwarding> getEmailForwarding() {
	return table.connector.emailForwardings.getEmailForwarding(this);
    }

    public List<EmailList> getEmailLists() {
	return table.connector.emailLists.getEmailLists(this);
    }

    public LinuxServerGroup getLinuxServerGroup(AOServer aoServer) {
	return table.connector.linuxServerGroups.getLinuxServerGroup(aoServer, this);
    }

    public List<LinuxAccount> getMailAccounts() {
	return table.connector.linuxAccounts.getMailAccounts(this);
    }

    public CreditCard getMonthlyCreditCard() {
	return table.connector.creditCards.getMonthlyCreditCard(this);
    }

    public List<MonthlyCharge> getMonthlyCharges() {
        return table.connector.monthlyCharges.getMonthlyCharges(this);
    }

    /**
     * Gets an approximation of the monthly rate paid by this account.  This is not guaranteed to
     * be exactly the same as the underlying accounting database processes.
     */
    public int getMonthlyRate() {
        int total=0;
        for(MonthlyCharge mc : getMonthlyCharges()) if(mc.isActive()) total+=mc.getPennies();
        return total;
    }

    public List<NoticeLog> getNoticeLogs() {
        return table.connector.noticeLogs.getNoticeLogs(this);
    }

    public List<Package> getPackages() {
	return table.connector.packages.getPackages(this);
    }

    public PackageDefinition getPackageDefinition(PackageCategory category, String name, String version) {
        return table.connector.packageDefinitions.getPackageDefinition(this, category, name, version);
    }

    public List<PackageDefinition> getPackageDefinitions(PackageCategory category) {
        return table.connector.packageDefinitions.getPackageDefinitions(this, category);
    }

    public Business getParentBusiness() {
        if(parent==null) return null;
	// The parent business might not be found, even when the value is set.  This is normal due
	// to filtering.
	return table.connector.businesses.get(parent);
    }

    public List<EmailDomain> getEmailDomains() {
	return table.connector.emailDomains.getEmailDomains(this);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BUSINESSES;
    }

    public int getTotalMonthlyRate() {
	int sum = 0;
	for (Package pack : getPackages()) sum += pack.getPackageDefinition().getMonthlyRate();
	return sum;
    }

    public List<Transaction> getTransactions() {
	return table.connector.transactions.getTransactions(pkey);
    }

    public List<WhoisHistory> getWhoisHistory() {
        return table.connector.whoisHistory.getWhoisHistory(this);
    }

    /**
     * @deprecated  Please use <code>isBusinessOrParentOf</code> instead.
     */
    public boolean isBusinessOrParent(Business other) {
        return isBusinessOrParentOf(other);
    }

    /**
     * Determines if this <code>Business</code> is the other business
     * or a parent of it.  This is often used for access control between
     * accounts.
     */
    public boolean isBusinessOrParentOf(Business other) {
        while(other!=null) {
            if(equals(other)) return true;
            other=other.getParentBusiness();
        }
        return false;
    }

    public static boolean isValidAccounting(String accounting) {
	int len=accounting.length();
	if(len<2 || len>32) return false;
	char ch=accounting.charAt(0);
	if(ch<'A' || ch>'Z') return false;
	ch=accounting.charAt(len-1);
	if(
            (ch<'A' || ch>'Z')
            && (ch<'0' || ch>'9')
	) return false;
	for(int c=1;c<(len-1);c++) {
            ch=accounting.charAt(c);
            if(
                (ch<'A' || ch>'Z')
                && (ch<'0' || ch>'9')
                && ch!='_'
            ) return false;
	}
	return true;
    }

    public void move(AOServer from, AOServer to, TerminalWriter out) {
        try {
            if(from.equals(to)) throw new SQLException("Cannot move from AOServer "+from.getServer().getHostname()+" to AOServer "+to.getServer().getHostname()+": same AOServer");

            BusinessServer fromBusinessServer=getBusinessServer(from.getServer());
            if(fromBusinessServer==null) throw new SQLException("Unable to find BusinessServer for Business="+pkey+" and Server="+from.getServer().getHostname());

            // Grant the Business access to the other server if it does not already have access
            if(out!=null) {
                out.boldOn();
                out.println("Adding Business Privileges");
                out.attributesOff();
                out.flush();
            }
            BusinessServer toBusinessServer=getBusinessServer(to.getServer());
            if(toBusinessServer==null) {
                if(out!=null) {
                    out.print("    ");
                    out.println(to.getServer().getHostname());
                    out.flush();
                }
                addBusinessServer(to.getServer(), fromBusinessServer.canConfigureBackup());
            }

            // Add the LinuxServerGroups
            if(out!=null) {
                out.boldOn();
                out.println("Adding Linux Groups");
                out.attributesOff();
                out.flush();
            }
            List<LinuxServerGroup> fromLinuxServerGroups=new ArrayList<LinuxServerGroup>();
            List<LinuxServerGroup> toLinuxServerGroups=new SortedArrayList<LinuxServerGroup>();
            {
                for(LinuxServerGroup lsg : table.connector.linuxServerGroups.getRows()) {
                    Package pk=lsg.getLinuxGroup().getPackage();
                    if(pk!=null && pk.getBusiness().equals(this)) {
                        AOServer ao=lsg.getAOServer();
                        if(ao.equals(from)) fromLinuxServerGroups.add(lsg);
                        else if(ao.equals(to)) toLinuxServerGroups.add(lsg);
                    }
                }
            }
            for(int c=0;c<fromLinuxServerGroups.size();c++) {
                LinuxServerGroup lsg=fromLinuxServerGroups.get(c);
                if(!toLinuxServerGroups.contains(lsg)) {
                    if(out!=null) {
                        out.print("    ");
                        out.print(lsg.name);
                        out.print(" to ");
                        out.println(to.getServer().getHostname());
                        out.flush();
                    }
                    lsg.getLinuxGroup().addLinuxServerGroup(to);
                }
            }

            // Add the LinuxServerAccounts
            if(out!=null) {
                out.boldOn();
                out.println("Adding Linux Accounts");
                out.attributesOff();
                out.flush();
            }
            List<LinuxServerAccount> fromLinuxServerAccounts=new ArrayList<LinuxServerAccount>();
            List<LinuxServerAccount> toLinuxServerAccounts=new SortedArrayList<LinuxServerAccount>();
            {
                List<LinuxServerAccount> lsas=table.connector.linuxServerAccounts.getRows();
                for(int c=0;c<lsas.size();c++) {
                    LinuxServerAccount lsa=lsas.get(c);
                    Package pk=lsa.getLinuxAccount().getUsername().getPackage();
                    if(pk!=null && pk.getBusiness().equals(this)) {
                        AOServer ao=lsa.getAOServer();
                        if(ao.equals(from)) fromLinuxServerAccounts.add(lsa);
                        else if(ao.equals(to)) toLinuxServerAccounts.add(lsa);
                    }
                }
            }
            for(int c=0;c<fromLinuxServerAccounts.size();c++) {
                LinuxServerAccount lsa=fromLinuxServerAccounts.get(c);
                if(!toLinuxServerAccounts.contains(lsa)) {
                    if(out!=null) {
                        out.print("    ");
                        out.print(lsa.username);
                        out.print(" to ");
                        out.println(to.getServer().getHostname());
                        out.flush();
                    }
                    lsa.getLinuxAccount().addLinuxServerAccount(to, lsa.getHome());
                }
            }

            // Wait for Linux Account rebuild
            if(out!=null) {
                out.boldOn();
                out.println("Waiting for Linux Account rebuild");
                out.attributesOff();
                out.print("    ");
                out.println(to.getServer().hostname);
                out.flush();
            }
            to.waitForLinuxAccountRebuild();

            // Copy the home directory contents
            if(out!=null) {
                out.boldOn();
                out.println("Copying Home Directories");
                out.attributesOff();
                out.flush();
            }
            for(int c=0;c<fromLinuxServerAccounts.size();c++) {
                LinuxServerAccount lsa=fromLinuxServerAccounts.get(c);
                if(!toLinuxServerAccounts.contains(lsa)) {
                    if(out!=null) {
                        out.print("    ");
                        out.print(lsa.username);
                        out.print(" to ");
                        out.print(to.getServer().hostname);
                        out.print(": ");
                        out.flush();
                    }
                    long byteCount=lsa.copyHomeDirectory(to);
                    if(out!=null) {
                        out.print(byteCount);
                        out.println(byteCount==1?" byte":" bytes");
                        out.flush();
                    }
                }
            }

            // Copy the cron tables
            if(out!=null) {
                out.boldOn();
                out.println("Copying Cron Tables");
                out.attributesOff();
                out.flush();
            }
            for(int c=0;c<fromLinuxServerAccounts.size();c++) {
                LinuxServerAccount lsa=fromLinuxServerAccounts.get(c);
                if(!toLinuxServerAccounts.contains(lsa)) {
                    if(out!=null) {
                        out.print("    ");
                        out.print(lsa.username);
                        out.print(" to ");
                        out.print(to.getServer().hostname);
                        out.print(": ");
                        out.flush();
                    }
                    String cronTable=lsa.getCronTable();
                    lsa.getLinuxAccount().getLinuxServerAccount(to).setCronTable(cronTable);
                    if(out!=null) {
                        out.print(cronTable.length());
                        out.println(cronTable.length()==1?" byte":" bytes");
                        out.flush();
                    }
                }
            }

            // Copy the passwords
            if(out!=null) {
                out.boldOn();
                out.println("Copying Passwords");
                out.attributesOff();
                out.flush();
            }
            for(int c=0;c<fromLinuxServerAccounts.size();c++) {
                LinuxServerAccount lsa=fromLinuxServerAccounts.get(c);
                if(!toLinuxServerAccounts.contains(lsa)) {
                    if(out!=null) {
                        out.print("    ");
                        out.print(lsa.username);
                        out.print(" to ");
                        out.println(to.getServer().hostname);
                        out.flush();
                    }

                    lsa.copyPassword(lsa.getLinuxAccount().getLinuxServerAccount(to));
                }
            }

            // Move IP Addresses
            if(out!=null) {
                out.boldOn();
                out.println("Moving IP Addresses");
                out.attributesOff();
                out.flush();
            }
            List<IPAddress> ips=table.connector.ipAddresses.getRows();
            for(int c=0;c<ips.size();c++) {
                IPAddress ip=ips.get(c);
                if(
                    ip.isAlias()
                    && !ip.isWildcard()
                    && !ip.getNetDevice().getNetDeviceID().isLoopback()
                    && ip.getPackage().accounting.equals(pkey)
                ) {
                    out.print("    ");
                    out.println(ip);
                    ip.moveTo(to);
                }
            }

            // TODO: Continue development here



            // Remove the LinuxServerAccounts
            if(out!=null) {
                out.boldOn();
                out.println("Removing Linux Accounts");
                out.attributesOff();
                out.flush();
            }
            for(int c=0;c<fromLinuxServerAccounts.size();c++) {
                LinuxServerAccount lsa=fromLinuxServerAccounts.get(c);
                if(out!=null) {
                    out.print("    ");
                    out.print(lsa.username);
                    out.print(" on ");
                    out.println(from.getServer().getHostname());
                    out.flush();
                }
                lsa.remove();
            }

            // Remove the LinuxServerGroups
            if(out!=null) {
                out.boldOn();
                out.println("Removing Linux Groups");
                out.attributesOff();
                out.flush();
            }
            for(int c=0;c<fromLinuxServerGroups.size();c++) {
                LinuxServerGroup lsg=fromLinuxServerGroups.get(c);
                if(out!=null) {
                    out.print("    ");
                    out.print(lsg.name);
                    out.print(" on ");
                    out.println(from.getServer().getHostname());
                    out.flush();
                }
                lsg.remove();
            }

            // Remove access to the old server
            if(out!=null) {
                out.boldOn();
                out.println("Removing Business Privileges");
                out.attributesOff();
                out.print("    ");
                out.println(from.getServer().getHostname());
                out.flush();
            }
            fromBusinessServer.remove();
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

     void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	contractVersion = result.getString(2);
	created = result.getTimestamp(3).getTime();
	Timestamp T = result.getTimestamp(4);
	if (result.wasNull()) canceled = -1;
	else canceled = T.getTime();
	cancelReason = result.getString(5);
	parent = result.getString(6);
        can_add_backup_server=result.getBoolean(7);
	can_add_businesses=result.getBoolean(8);
        can_see_prices=result.getBoolean(9);
        disable_log=result.getInt(10);
        if(result.wasNull()) disable_log=-1;
        do_not_disable_reason=result.getString(11);
        auto_enable=result.getBoolean(12);
        bill_parent=result.getBoolean(13);
    }

     public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	contractVersion=StringUtility.intern(in.readNullUTF());
	created=in.readLong();
	canceled=in.readLong();
	cancelReason=in.readNullUTF();
	parent=StringUtility.intern(in.readNullUTF());
        can_add_backup_server=in.readBoolean();
	can_add_businesses=in.readBoolean();
        can_see_prices=in.readBoolean();
        disable_log=in.readCompressedInt();
        do_not_disable_reason=in.readNullUTF();
        auto_enable=in.readBoolean();
        bill_parent=in.readBoolean();
    }

    public void setAccounting(String accounting) {
        if(!isValidAccounting(accounting)) throw new WrappedException(new SQLException("Invalid accounting code: "+accounting));
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_BUSINESS_ACCOUNTING, this.pkey, accounting);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeUTF(pkey);
	out.writeBoolean(contractVersion!=null); if(contractVersion!=null) out.writeUTF(contractVersion);
	out.writeLong(created);
	out.writeLong(canceled);
	out.writeNullUTF(cancelReason);
        out.writeNullUTF(parent);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_102)>=0) out.writeBoolean(can_add_backup_server);
	out.writeBoolean(can_add_businesses);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_122)<=0) out.writeBoolean(false);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_103)>=0) out.writeBoolean(can_see_prices);
        out.writeCompressedInt(disable_log);
        out.writeNullUTF(do_not_disable_reason);
        out.writeBoolean(auto_enable);
        out.writeBoolean(bill_parent);
    }

    public List<Ticket> getTickets() {
	return table.connector.tickets.getTickets(this);
    }
    
    /**
     * Gets all of the encryption keys for this business.
     */
    public List<EncryptionKey> getEncryptionKeys() {
        return table.connector.encryptionKeys.getEncryptionKeys(this);
    }
}