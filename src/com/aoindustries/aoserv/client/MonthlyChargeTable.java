package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  MonthlyCharge
 *
 * @author  AO Industries, Inc.
 */
final public class MonthlyChargeTable extends CachedTableIntegerKey<MonthlyCharge> {

    MonthlyChargeTable(AOServConnector connector) {
        super(connector, MonthlyCharge.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MonthlyCharge.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(MonthlyCharge.COLUMN_SOURCE_ACCOUNTING_name, ASCENDING),
        new OrderBy(MonthlyCharge.COLUMN_TYPE_name, ASCENDING),
        new OrderBy(MonthlyCharge.COLUMN_CREATED_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public MonthlyCharge get(int pkey) throws SQLException, IOException {
        return getUniqueRow(MonthlyCharge.COLUMN_PKEY, pkey);
    }

    /**
     * Gets the list of all <code>monthly_charges</code> in the database.  In addition
     * to the monthly charges in the database, the package charges, additional email users,
     * additional ip addresses, and additional shell users are added to the billing.
     *
     * @return  the <code>MonthlyCharge</code> objects.
     */
    List<MonthlyCharge> getMonthlyCharges(BusinessAdministrator business_administrator, Business bu) throws IOException, SQLException {
        // Add the entries in the monthly_charges table that apply to this business
        List<MonthlyCharge> charges;
        {
            List<MonthlyCharge> cached=super.getRows();
            int len=cached.size();
            charges=new ArrayList<MonthlyCharge>(len);
            for(int c=0;c<len;c++) {
                MonthlyCharge mc=cached.get(c);
                if(
                    bu==null
                    || bu.equals(mc.getBusiness())
                ) charges.add(mc);
            }
        }

        // Resource constants used later
        final Resource httpdResource=connector.getResources().get(Resource.HTTPD);
        if(httpdResource==null) throw new AssertionError("httpdResource is null");
        final Resource javavmResource=connector.getResources().get(Resource.JAVAVM);
        if(javavmResource==null) throw new AssertionError("javavmResource is null");
        final Resource ipResource=connector.getResources().get(Resource.IP);
        if(ipResource==null) throw new AssertionError("ipResource is null");
        final Resource mysqlReplicationResource=connector.getResources().get(Resource.MYSQL_REPLICATION);
        if(mysqlReplicationResource==null) throw new AssertionError("mysqlReplicationResource is null");
        final Resource emailResource=connector.getResources().get(Resource.EMAIL);
        if(emailResource==null) throw new AssertionError("emailResource is null");
        final Resource siteResource=connector.getResources().get(Resource.SITE);
        if(siteResource==null) throw new AssertionError("siteResource is null");
        final Resource userResource=connector.getResources().get(Resource.USER);
        if(userResource==null) throw new AssertionError("userResource is null");

        // Preprocess resources counts
        Map<Business,Integer> emailsPerBusiness=new HashMap<Business,Integer>();
        Map<Business,Integer> usersPerBusiness=new HashMap<Business,Integer>();
        {
            for(LinuxServerAccount lsa : connector.getLinuxServerAccounts().getRows()) {
                String username=lsa.username;
                if(!username.equals(LinuxAccount.MAIL)) {
                    Map<Business,Integer> map;
                    LinuxAccount la=lsa.getLinuxAccount();
                    if(la.getType().getName().equals(LinuxAccountType.EMAIL)) map=emailsPerBusiness;
                    else map=usersPerBusiness;

                    Business laBu=la.getUsername().getBusiness();
                    Integer I=map.get(laBu);
                    if(I==null) map.put(laBu, I=Integer.valueOf(1));
                    else map.put(laBu, I=Integer.valueOf(I.intValue()+1));
                }
            }
        }
        Map<Business,Integer> javavmsPerBusiness=new HashMap<Business,Integer>();
        {
            // HttpdSharedTomcats
            for(HttpdSharedTomcat hst : connector.getHttpdSharedTomcats().getRows()) {
                LinuxServerGroup lsg=hst.getLinuxServerGroup();
                LinuxGroup lg=lsg.getLinuxGroup();
                Business pack=lg.getBusiness();
                Integer I=javavmsPerBusiness.get(pack);
                if(I==null) javavmsPerBusiness.put(pack, I=Integer.valueOf(1));
                else javavmsPerBusiness.put(pack, I=Integer.valueOf(I.intValue()+1));
            }
            // HttpdJBossSites
            for(HttpdJBossSite hjs : connector.getHttpdJBossSites().getRows()) {
                HttpdTomcatSite hts=hjs.getHttpdTomcatSite();
                HttpdSite hs=hts.getHttpdSite();
                Business pack=hs.getBusiness();
                Integer I=javavmsPerBusiness.get(pack);
                if(I==null) javavmsPerBusiness.put(pack, I=Integer.valueOf(1));
                else javavmsPerBusiness.put(pack, I=Integer.valueOf(I.intValue()+1));
            }
            // HttpdTomcatStdSites
            for(HttpdTomcatStdSite htss : connector.getHttpdTomcatStdSites().getRows()) {
                HttpdTomcatSite hts=htss.getHttpdTomcatSite();
                HttpdSite hs=hts.getHttpdSite();
                Business pack=hs.getBusiness();
                Integer I=javavmsPerBusiness.get(pack);
                if(I==null) javavmsPerBusiness.put(pack, I=Integer.valueOf(1));
                else javavmsPerBusiness.put(pack, I=Integer.valueOf(I.intValue()+1));
            }
        }

    	for(Business business : connector.getBusinesses().getRows()) {
            // Only bill when active
            if(business.getCanceled()==-1) {
                Business acctBusiness=business.getAccountingBusiness();
                if(
                    bu==null
                    || bu.equals(acctBusiness)
                ) {
                    // Add the package billing to the top level business account
                    PackageDefinition packageDefinition=business.getPackageDefinition();
                    BigDecimal rate = packageDefinition.getMonthlyRate();
                    if(rate.compareTo(BigDecimal.ZERO)!=0) {
                        charges.add(
                            new MonthlyCharge(
                                this,
                                acctBusiness,
                                business,
                                packageDefinition.getMonthlyRateTransactionType(),
                                null,
                                1000,
                                rate.multiply(BigDecimal.valueOf(100)).intValueExact(),
                                business_administrator
                            )
                        );
                    }

                    // TODO: Add aoserv_daemon
                    // TODO: Add aoserv_master
                    // TODO: Add bandwidth
                    // TODO: Add consulting
                    // TODO: Add disk
                    // TODO: Add failover
                    // TODO: Add hardware_*

                    // Add the httpd_servers
                    {
                        PackageDefinitionLimit limit=packageDefinition.getLimit(httpdResource);
                        if(limit==null || limit.getSoftLimit()!=PackageDefinitionLimit.UNLIMITED) {
                            List<HttpdServer> hss=business.getHttpdServers();
                            if(!hss.isEmpty()) {
                                if(limit==null) throw new SQLException("HttpdServers exist, but no limit defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                if(hss.size()>limit.getSoftLimit()) {
                                    BigDecimal addRate=limit.getAdditionalRate();
                                    if(addRate==null) throw new SQLException("Additional HttpdServers exist, but no additional rate defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    TransactionType addType=limit.getAdditionalTransactionType();
                                    if(addType==null) throw new SQLException("Additional HttpdServers exist, but no additional TransactionType defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    charges.add(
                                        new MonthlyCharge(
                                            this,
                                            acctBusiness,
                                            business,
                                            addType,
                                            "Additional HTTP Servers ("+limit.getSoftLimit()+" included with package, have "+hss.size()+")",
                                            (hss.size()-limit.getSoftLimit())*1000,
                                            addRate.multiply(BigDecimal.valueOf(100)).intValueExact(),
                                            business_administrator
                                        )
                                    );
                                }
                            }
                        }
                    }

                    // Add the ip addresses
                    {
                        PackageDefinitionLimit limit=packageDefinition.getLimit(ipResource);
                        if(limit==null || limit.getSoftLimit()!=PackageDefinitionLimit.UNLIMITED) {
                            List<IPAddress> ips=business.getIPAddresses();
                            if(!ips.isEmpty()) {
                                if(limit==null) throw new SQLException("IPAddresses exist, but no limit defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                if(ips.size()>limit.getSoftLimit()) {
                                    BigDecimal addRate=limit.getAdditionalRate();
                                    if(addRate==null) throw new SQLException("Additional IPAddresses exist, but no additional rate defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    TransactionType addType=limit.getAdditionalTransactionType();
                                    if(addType==null) throw new SQLException("Additional IPAddresses exist, but no additional TransactionType defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    charges.add(
                                        new MonthlyCharge(
                                            this,
                                            acctBusiness,
                                            business,
                                            addType,
                                            "Additional IP Addresses ("+limit.getSoftLimit()+" included with package, have "+ips.size()+")",
                                            (ips.size()-limit.getSoftLimit())*1000,
                                            addRate.multiply(BigDecimal.valueOf(100)).intValueExact(),
                                            business_administrator
                                        )
                                    );
                                }
                            }
                        }
                    }

                    // Add javavm
                    {
                        PackageDefinitionLimit limit=packageDefinition.getLimit(javavmResource);
                        if(limit==null || limit.getSoftLimit()!=PackageDefinitionLimit.UNLIMITED) {
                            Integer I=javavmsPerBusiness.get(business);
                            if(I!=null) {
                                int javavmCount=I.intValue();
                                if(javavmCount>0) {
                                    if(limit==null) throw new SQLException("Java virtual machines exist, but no limit defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    if(javavmCount>limit.getSoftLimit()) {
                                        BigDecimal addRate=limit.getAdditionalRate();
                                        if(addRate==null) throw new SQLException("Additional Java virtual machines exist, but no additional rate defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                        TransactionType addType=limit.getAdditionalTransactionType();
                                        if(addType==null) throw new SQLException("Additional Java virtual machines exist, but no additional TransactionType defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                        charges.add(
                                            new MonthlyCharge(
                                                this,
                                                acctBusiness,
                                                business,
                                                addType,
                                                "Additional Java Virtual Machines ("+limit.getSoftLimit()+" included with package, have "+javavmCount+")",
                                                (javavmCount-limit.getSoftLimit())*1000,
                                                addRate.multiply(BigDecimal.valueOf(100)).intValueExact(),
                                                business_administrator
                                            )
                                        );
                                    }
                                }
                            }
                        }
                    }
                    
                    // Add the mysql_replications
                    {
                        PackageDefinitionLimit limit=packageDefinition.getLimit(mysqlReplicationResource);
                        if(limit==null || limit.getSoftLimit()!=PackageDefinitionLimit.UNLIMITED) {
                            List<FailoverMySQLReplication> fmrs = business.getFailoverMySQLReplications();
                            if(!fmrs.isEmpty()) {
                                if(limit==null) throw new SQLException("FailoverMySQLReplications exist, but no limit defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                if(fmrs.size()>limit.getSoftLimit()) {
                                    BigDecimal addRate=limit.getAdditionalRate();
                                    if(addRate==null) throw new SQLException("Additional FailoverMySQLReplications exist, but no additional rate defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    TransactionType addType=limit.getAdditionalTransactionType();
                                    if(addType==null) throw new SQLException("Additional FailoverMySQLReplications exist, but no additional TransactionType defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    charges.add(
                                        new MonthlyCharge(
                                            this,
                                            acctBusiness,
                                            business,
                                            addType,
                                            "Additional MySQL Replications ("+limit.getSoftLimit()+" included with package, have "+fmrs.size()+")",
                                            (fmrs.size()-limit.getSoftLimit())*1000,
                                            addRate.multiply(BigDecimal.valueOf(100)).intValueExact(),
                                            business_administrator
                                        )
                                    );
                                }
                            }
                        }
                    }

                    // Add Email accounts
                    {
                        PackageDefinitionLimit limit=packageDefinition.getLimit(emailResource);
                        if(limit==null || limit.getSoftLimit()!=PackageDefinitionLimit.UNLIMITED) {
                            Integer I=emailsPerBusiness.get(business);
                            if(I!=null) {
                                int emailCount=I.intValue();
                                if(emailCount>0) {
                                    if(limit==null) throw new SQLException("Email inboxes exist, but no limit defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    if(emailCount>limit.getSoftLimit()) {
                                        BigDecimal addRate=limit.getAdditionalRate();
                                        if(addRate==null) throw new SQLException("Additional Email inboxes exist, but no additional rate defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                        TransactionType addType=limit.getAdditionalTransactionType();
                                        if(addType==null) throw new SQLException("Additional Email inboxes exist, but no additional TransactionType defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                        charges.add(
                                            new MonthlyCharge(
                                                this,
                                                acctBusiness,
                                                business,
                                                addType,
                                                "Additional Email Inboxes ("+limit.getSoftLimit()+" included with package, have "+emailCount+")",
                                                (emailCount-limit.getSoftLimit())*1000,
                                                addRate.multiply(BigDecimal.valueOf(100)).intValueExact(),
                                                business_administrator
                                            )
                                        );
                                    }
                                }
                            }
                        }
                    }

                    // TODO: Add rack
                    
                    // Add sites
                    {
                        PackageDefinitionLimit limit=packageDefinition.getLimit(siteResource);
                        if(limit==null || limit.getSoftLimit()!=PackageDefinitionLimit.UNLIMITED) {
                            List<HttpdSite> hss=business.getHttpdSites();
                            if(!hss.isEmpty()) {
                                if(limit==null) throw new SQLException("HttpdSites exist, but no limit defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                if(hss.size()>limit.getSoftLimit()) {
                                    BigDecimal addRate=limit.getAdditionalRate();
                                    if(addRate==null) throw new SQLException("Additional HttpdSites exist, but no additional rate defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    TransactionType addType=limit.getAdditionalTransactionType();
                                    if(addType==null) throw new SQLException("Additional HttpdSites exist, but no additional TransactionType defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    charges.add(
                                        new MonthlyCharge(
                                            this,
                                            acctBusiness,
                                            business,
                                            addType,
                                            "Additional Web Sites ("+limit.getSoftLimit()+" included with package, have "+hss.size()+")",
                                            (hss.size()-limit.getSoftLimit())*1000,
                                            addRate.multiply(BigDecimal.valueOf(100)).intValueExact(),
                                            business_administrator
                                        )
                                    );
                                }
                            }
                        }
                    }

                    // TODO: Add sysadmin

                    // Add user accounts
                    {
                        PackageDefinitionLimit limit=packageDefinition.getLimit(userResource);
                        if(limit==null || limit.getSoftLimit()!=PackageDefinitionLimit.UNLIMITED) {
                            Integer I=usersPerBusiness.get(business);
                            if(I!=null) {
                                int userCount=I.intValue();
                                if(userCount>0) {
                                    if(limit==null) throw new SQLException("Shell accounts exist, but no limit defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                    if(userCount>limit.getSoftLimit()) {
                                        BigDecimal addRate=limit.getAdditionalRate();
                                        if(addRate==null) throw new SQLException("Additional Shell accounts exist, but no additional rate defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                        TransactionType addType=limit.getAdditionalTransactionType();
                                        if(addType==null) throw new SQLException("Additional Shell accounts exist, but no additional TransactionType defined for Business="+business.pkey+", PackageDefinition="+packageDefinition.pkey);
                                        charges.add(
                                            new MonthlyCharge(
                                                this,
                                                acctBusiness,
                                                business,
                                                addType,
                                                "Additional Shell Accounts ("+limit.getSoftLimit()+" included with package, have "+userCount+")",
                                                (userCount-limit.getSoftLimit())*1000,
                                                addRate.multiply(BigDecimal.valueOf(100)).intValueExact(),
                                                business_administrator
                                            )
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Convert and return
	return charges;
    }

    List<MonthlyCharge> getMonthlyCharges(Business bu) throws SQLException, IOException {
        return getMonthlyCharges(connector.getThisBusinessAdministrator(), bu);
    }

    @Override
    public List<MonthlyCharge> getRows() throws SQLException, IOException {
        return getMonthlyCharges(connector.getThisBusinessAdministrator(), null);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MONTHLY_CHARGES;
    }

    @Override
    final public List<MonthlyCharge> getIndexedRows(int col, Object value) {
        throw new UnsupportedOperationException("Indexed rows are not supported on MonthlyChargeTable");
    }
}