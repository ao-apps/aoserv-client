package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  MonthlyCharge
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MonthlyChargeTable extends CachedTableIntegerKey<MonthlyCharge> {

    MonthlyChargeTable(AOServConnector connector) {
        super(connector, MonthlyCharge.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MonthlyCharge.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(MonthlyCharge.COLUMN_PACKAGE_name, ASCENDING),
        new OrderBy(MonthlyCharge.COLUMN_TYPE_name, ASCENDING),
        new OrderBy(MonthlyCharge.COLUMN_CREATED_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public MonthlyCharge get(Object pkey) {
	return getUniqueRow(MonthlyCharge.COLUMN_PKEY, pkey);
    }

    public MonthlyCharge get(int pkey) {
	return getUniqueRow(MonthlyCharge.COLUMN_PKEY, pkey);
    }

    /**
     * Gets the list of all <code>monthly_charges</code> in the database.  In addition
     * to the monthly charges in the database, the package charges, additional email users,
     * additional ip addresses, and additional shell users are added to the billing.
     *
     * @return  the <code>MonthlyCharge</code> objects.
     */
    List<MonthlyCharge> getMonthlyCharges(BusinessAdministrator business_administrator, Business bu) {
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
                    || bu.equals(mc.getPackage().getBusiness())
                ) charges.add(mc);
            }
        }

        // Resource constants used later
        final Resource httpdResource=connector.resources.get(Resource.HTTPD);
        if(httpdResource==null) throw new AssertionError("httpdResource is null");
        final Resource javavmResource=connector.resources.get(Resource.JAVAVM);
        if(javavmResource==null) throw new AssertionError("javavmResource is null");
        final Resource ipResource=connector.resources.get(Resource.IP);
        if(ipResource==null) throw new AssertionError("ipResource is null");
        final Resource mysqlReplicationResource=connector.resources.get(Resource.MYSQL_REPLICATION);
        if(mysqlReplicationResource==null) throw new AssertionError("mysqlReplicationResource is null");
        final Resource popResource=connector.resources.get(Resource.POP);
        if(popResource==null) throw new AssertionError("popResource is null");
        final Resource siteResource=connector.resources.get(Resource.SITE);
        if(siteResource==null) throw new AssertionError("siteResource is null");
        final Resource userResource=connector.resources.get(Resource.USER);
        if(userResource==null) throw new AssertionError("userResource is null");

        // Preprocess resources counts
        Map<Package,Integer> popsPerPackage=new HashMap<Package,Integer>();
        Map<Package,Integer> usersPerPackage=new HashMap<Package,Integer>();
        {
            for(LinuxServerAccount lsa : connector.linuxServerAccounts.getRows()) {
                String username=lsa.username;
                if(!username.equals(LinuxAccount.MAIL)) {
                    Map<Package,Integer> map;
                    LinuxAccount la=lsa.getLinuxAccount();
                    if(la.getType().getName().equals(LinuxAccountType.EMAIL)) map=popsPerPackage;
                    else map=usersPerPackage;

                    Package pack=la.getUsername().getPackage();
                    Integer I=map.get(pack);
                    if(I==null) map.put(pack, I=Integer.valueOf(1));
                    else map.put(pack, I=Integer.valueOf(I.intValue()+1));
                }
            }
        }
        Map<Package,Integer> javavmsPerPackage=new HashMap<Package,Integer>();
        {
            // HttpdSharedTomcats
            for(HttpdSharedTomcat hst : connector.httpdSharedTomcats.getRows()) {
                LinuxServerGroup lsg=hst.getLinuxServerGroup();
                LinuxGroup lg=lsg.getLinuxGroup();
                Package pack=lg.getPackage();
                Integer I=javavmsPerPackage.get(pack);
                if(I==null) javavmsPerPackage.put(pack, I=Integer.valueOf(1));
                else javavmsPerPackage.put(pack, I=Integer.valueOf(I.intValue()+1));
            }
            // HttpdJBossSites
            for(HttpdJBossSite hjs : connector.httpdJBossSites.getRows()) {
                HttpdTomcatSite hts=hjs.getHttpdTomcatSite();
                HttpdSite hs=hts.getHttpdSite();
                Package pack=hs.getPackage();
                Integer I=javavmsPerPackage.get(pack);
                if(I==null) javavmsPerPackage.put(pack, I=Integer.valueOf(1));
                else javavmsPerPackage.put(pack, I=Integer.valueOf(I.intValue()+1));
            }
            // HttpdTomcatStdSites
            for(HttpdTomcatStdSite htss : connector.httpdTomcatStdSites.getRows()) {
                HttpdTomcatSite hts=htss.getHttpdTomcatSite();
                HttpdSite hs=hts.getHttpdSite();
                Package pack=hs.getPackage();
                Integer I=javavmsPerPackage.get(pack);
                if(I==null) javavmsPerPackage.put(pack, I=Integer.valueOf(1));
                else javavmsPerPackage.put(pack, I=Integer.valueOf(I.intValue()+1));
            }
        }

	for(Package pack : connector.packages.getRows()) {
            Business business=pack.getBusiness();
            // Only bill when active
            if(business.getCanceled()==-1) {
                Business acctBusiness=business.getAccountingBusiness();
                if(
                    bu==null
                    || bu.equals(acctBusiness)
                ) {
                    // Add the package billing to the top level business account
                    PackageDefinition packageDefinition=pack.getPackageDefinition();
                    int rate=packageDefinition.getMonthlyRate();
                    if(rate!=0) {
                        charges.add(
                            new MonthlyCharge(
                                this,
                                acctBusiness,
                                pack,
                                pack.getPackageDefinition().getMonthlyRateTransactionType(),
                                null,
                                1000,
                                rate,
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
                            List<HttpdServer> hss=pack.getHttpdServers();
                            if(!hss.isEmpty()) {
                                if(limit==null) throw new WrappedException(new SQLException("HttpdServers exist, but no limit defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                if(hss.size()>limit.getSoftLimit()) {
                                    int addRate=limit.getAdditionalRate();
                                    if(addRate<0) throw new WrappedException(new SQLException("Additional HttpdServers exist, but no additional rate defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    TransactionType addType=limit.getAdditionalTransactionType();
                                    if(addType==null) throw new WrappedException(new SQLException("Additional HttpdServers exist, but no additional TransactionType defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    charges.add(
                                        new MonthlyCharge(
                                            this,
                                            acctBusiness,
                                            pack,
                                            addType,
                                            "Additional HTTP Servers ("+limit.getSoftLimit()+" included with package, have "+hss.size()+")",
                                            (hss.size()-limit.getSoftLimit())*1000,
                                            addRate,
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
                            List<IPAddress> ips=pack.getIPAddresses();
                            if(!ips.isEmpty()) {
                                if(limit==null) throw new WrappedException(new SQLException("IPAddresses exist, but no limit defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                if(ips.size()>limit.getSoftLimit()) {
                                    int addRate=limit.getAdditionalRate();
                                    if(addRate<0) throw new WrappedException(new SQLException("Additional IPAddresses exist, but no additional rate defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    TransactionType addType=limit.getAdditionalTransactionType();
                                    if(addType==null) throw new WrappedException(new SQLException("Additional IPAddresses exist, but no additional TransactionType defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    charges.add(
                                        new MonthlyCharge(
                                            this,
                                            acctBusiness,
                                            pack,
                                            addType,
                                            "Additional IP Addresses ("+limit.getSoftLimit()+" included with package, have "+ips.size()+")",
                                            (ips.size()-limit.getSoftLimit())*1000,
                                            addRate,
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
                            Integer I=javavmsPerPackage.get(pack);
                            if(I!=null) {
                                int javavmCount=I.intValue();
                                if(javavmCount>0) {
                                    if(limit==null) throw new WrappedException(new SQLException("Java virtual machines exist, but no limit defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    if(javavmCount>limit.getSoftLimit()) {
                                        int addRate=limit.getAdditionalRate();
                                        if(addRate<0) throw new WrappedException(new SQLException("Additional Java virtual machines exist, but no additional rate defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                        TransactionType addType=limit.getAdditionalTransactionType();
                                        if(addType==null) throw new WrappedException(new SQLException("Additional Java virtual machines exist, but no additional TransactionType defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                        charges.add(
                                            new MonthlyCharge(
                                                this,
                                                acctBusiness,
                                                pack,
                                                addType,
                                                "Additional Java Virtual Machines ("+limit.getSoftLimit()+" included with package, have "+javavmCount+")",
                                                (javavmCount-limit.getSoftLimit())*1000,
                                                addRate,
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
                            List<FailoverMySQLReplication> fmrs = pack.getFailoverMySQLReplications();
                            if(!fmrs.isEmpty()) {
                                if(limit==null) throw new WrappedException(new SQLException("FailoverMySQLReplications exist, but no limit defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                if(fmrs.size()>limit.getSoftLimit()) {
                                    int addRate=limit.getAdditionalRate();
                                    if(addRate<0) throw new WrappedException(new SQLException("Additional FailoverMySQLReplications exist, but no additional rate defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    TransactionType addType=limit.getAdditionalTransactionType();
                                    if(addType==null) throw new WrappedException(new SQLException("Additional FailoverMySQLReplications exist, but no additional TransactionType defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    charges.add(
                                        new MonthlyCharge(
                                            this,
                                            acctBusiness,
                                            pack,
                                            addType,
                                            "Additional MySQL Replications ("+limit.getSoftLimit()+" included with package, have "+fmrs.size()+")",
                                            (fmrs.size()-limit.getSoftLimit())*1000,
                                            addRate,
                                            business_administrator
                                        )
                                    );
                                }
                            }
                        }
                    }

                    // Add POP accounts
                    {
                        PackageDefinitionLimit limit=packageDefinition.getLimit(popResource);
                        if(limit==null || limit.getSoftLimit()!=PackageDefinitionLimit.UNLIMITED) {
                            Integer I=popsPerPackage.get(pack);
                            if(I!=null) {
                                int popCount=I.intValue();
                                if(popCount>0) {
                                    if(limit==null) throw new WrappedException(new SQLException("Email inboxes exist, but no limit defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    if(popCount>limit.getSoftLimit()) {
                                        int addRate=limit.getAdditionalRate();
                                        if(addRate<0) throw new WrappedException(new SQLException("Additional Email inboxes exist, but no additional rate defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                        TransactionType addType=limit.getAdditionalTransactionType();
                                        if(addType==null) throw new WrappedException(new SQLException("Additional Email inboxes exist, but no additional TransactionType defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                        charges.add(
                                            new MonthlyCharge(
                                                this,
                                                acctBusiness,
                                                pack,
                                                addType,
                                                "Additional Email Inboxes ("+limit.getSoftLimit()+" included with package, have "+popCount+")",
                                                (popCount-limit.getSoftLimit())*1000,
                                                addRate,
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
                            List<HttpdSite> hss=pack.getHttpdSites();
                            if(!hss.isEmpty()) {
                                if(limit==null) throw new WrappedException(new SQLException("HttpdSites exist, but no limit defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                if(hss.size()>limit.getSoftLimit()) {
                                    int addRate=limit.getAdditionalRate();
                                    if(addRate<0) throw new WrappedException(new SQLException("Additional HttpdSites exist, but no additional rate defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    TransactionType addType=limit.getAdditionalTransactionType();
                                    if(addType==null) throw new WrappedException(new SQLException("Additional HttpdSites exist, but no additional TransactionType defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    charges.add(
                                        new MonthlyCharge(
                                            this,
                                            acctBusiness,
                                            pack,
                                            addType,
                                            "Additional Web Sites ("+limit.getSoftLimit()+" included with package, have "+hss.size()+")",
                                            (hss.size()-limit.getSoftLimit())*1000,
                                            addRate,
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
                            Integer I=usersPerPackage.get(pack);
                            if(I!=null) {
                                int userCount=I.intValue();
                                if(userCount>0) {
                                    if(limit==null) throw new WrappedException(new SQLException("Shell accounts exist, but no limit defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                    if(userCount>limit.getSoftLimit()) {
                                        int addRate=limit.getAdditionalRate();
                                        if(addRate<0) throw new WrappedException(new SQLException("Additional Shell accounts exist, but no additional rate defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                        TransactionType addType=limit.getAdditionalTransactionType();
                                        if(addType==null) throw new WrappedException(new SQLException("Additional Shell accounts exist, but no additional TransactionType defined for Package="+pack.pkey+", PackageDefinition="+packageDefinition.pkey));
                                        charges.add(
                                            new MonthlyCharge(
                                                this,
                                                acctBusiness,
                                                pack,
                                                addType,
                                                "Additional Shell Accounts ("+limit.getSoftLimit()+" included with package, have "+userCount+")",
                                                (userCount-limit.getSoftLimit())*1000,
                                                addRate,
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

    List<MonthlyCharge> getMonthlyCharges(Business bu) {
        return getMonthlyCharges(connector.getThisBusinessAdministrator(), bu);
    }

    public List<MonthlyCharge> getRows() {
        return getMonthlyCharges(connector.getThisBusinessAdministrator(), null);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MONTHLY_CHARGES;
    }

    final public List<MonthlyCharge> getIndexedRows(int col, Object value) {
        throw new UnsupportedOperationException("Indexed rows are not supported on MonthlyChargeTable");
    }
}