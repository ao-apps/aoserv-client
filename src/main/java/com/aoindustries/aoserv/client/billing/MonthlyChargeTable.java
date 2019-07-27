/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.backup.MysqlReplication;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.linux.UserType;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.web.HttpdServer;
import com.aoindustries.aoserv.client.web.Site;
import com.aoindustries.aoserv.client.web.tomcat.PrivateTomcatSite;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcat;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
		// new OrderBy(MonthlyCharge.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(MonthlyCharge.COLUMN_PACKAGE_name, ASCENDING),
		new OrderBy(MonthlyCharge.COLUMN_TYPE_name, ASCENDING),
		new OrderBy(MonthlyCharge.COLUMN_CREATED_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
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
	private List<MonthlyCharge> getModifiableMonthlyCharges(Administrator administrator, Account account) throws IOException, SQLException {
		// Add the entries in the monthly_charges table that apply to this business
		List<MonthlyCharge> charges;
		{
			List<MonthlyCharge> cached=super.getRows();
			int len=cached.size();
			charges=new ArrayList<>(len);
			for(int c=0;c<len;c++) {
				MonthlyCharge mc=cached.get(c);
				if(
					account==null
					|| account.getName().equals(mc.getPackage().getAccount_name())
				) charges.add(mc);
			}
		}

		// Resource constants used later
		final Resource httpdResource=connector.getBilling().getResource().get(Resource.HTTPD);
		if(httpdResource==null) throw new AssertionError("httpdResource is null");
		final Resource javavmResource=connector.getBilling().getResource().get(Resource.JAVAVM);
		if(javavmResource==null) throw new AssertionError("javavmResource is null");
		final Resource ipResource=connector.getBilling().getResource().get(Resource.IP);
		if(ipResource==null) throw new AssertionError("ipResource is null");
		final Resource mysqlReplicationResource=connector.getBilling().getResource().get(Resource.MYSQL_REPLICATION);
		if(mysqlReplicationResource==null) throw new AssertionError("mysqlReplicationResource is null");
		final Resource emailResource=connector.getBilling().getResource().get(Resource.EMAIL);
		if(emailResource==null) throw new AssertionError("emailResource is null");
		final Resource siteResource=connector.getBilling().getResource().get(Resource.SITE);
		if(siteResource==null) throw new AssertionError("siteResource is null");
		final Resource userResource=connector.getBilling().getResource().get(Resource.USER);
		if(userResource==null) throw new AssertionError("userResource is null");

		// Preprocess resources counts
		Map<Package,Integer> emailsPerPackage=new HashMap<>();
		Map<Package,Integer> usersPerPackage=new HashMap<>();
		{
			for(UserServer lsa : connector.getLinux().getUserServer().getRows()) {
				com.aoindustries.aoserv.client.linux.User.Name username=lsa.getLinuxAccount_username_id();
				if(!username.equals(com.aoindustries.aoserv.client.linux.User.MAIL)) {
					Map<Package,Integer> map;
					com.aoindustries.aoserv.client.linux.User la=lsa.getLinuxAccount();
					if(la.getType().getName().equals(UserType.EMAIL)) map=emailsPerPackage;
					else map=usersPerPackage;

					Package pack=la.getUsername().getPackage();
					Integer I=map.get(pack);
					if(I==null) map.put(pack, I=1);
					else map.put(pack, I=I+1);
				}
			}
		}
		Map<Package,Integer> javavmsPerPackage=new HashMap<>();
		{
			// HttpdSharedTomcats
			for(SharedTomcat hst : connector.getWeb_tomcat().getSharedTomcat().getRows()) {
				if(!hst.isDisabled()) {
					GroupServer lsg=hst.getLinuxServerGroup();
					Group lg=lsg.getLinuxGroup();
					Package pack=lg.getPackage();
					Integer I=javavmsPerPackage.get(pack);
					if(I==null) javavmsPerPackage.put(pack, I=1);
					else javavmsPerPackage.put(pack, I=I+1);
				}
			}
			// HttpdJBossSites
			for(com.aoindustries.aoserv.client.web.jboss.Site hjs : connector.getWeb_jboss().getSite().getRows()) {
				com.aoindustries.aoserv.client.web.tomcat.Site hts=hjs.getHttpdTomcatSite();
				Site hs=hts.getHttpdSite();
				if(!hs.isDisabled()) {
					Package pack=hs.getPackage();
					Integer I=javavmsPerPackage.get(pack);
					if(I==null) javavmsPerPackage.put(pack, I=1);
					else javavmsPerPackage.put(pack, I=I+1);
				}
			}
			// HttpdTomcatStdSites
			for(PrivateTomcatSite htss : connector.getWeb_tomcat().getPrivateTomcatSite().getRows()) {
				com.aoindustries.aoserv.client.web.tomcat.Site hts=htss.getHttpdTomcatSite();
				Site hs=hts.getHttpdSite();
				if(!hs.isDisabled()) {
					Package pack=hs.getPackage();
					Integer I=javavmsPerPackage.get(pack);
					if(I==null) javavmsPerPackage.put(pack, I=1);
					else javavmsPerPackage.put(pack, I=I+1);
				}
			}
		}

		for(Package pack : connector.getBilling().getPackage().getRows()) {
			Account packAccount = pack.getAccount();
			// Only bill when active
			if(packAccount.getCanceled() == null) {
				Account billingAccount = packAccount.getBillingAccount();
				if(
					account==null
					|| account.equals(billingAccount)
				) {
					// Add the package billing to the top level business account
					PackageDefinition packageDefinition=pack.getPackageDefinition();
					Money rate = packageDefinition.getMonthlyRate();
					if(rate.getUnscaledValue() != 0) {
						charges.add(
							new MonthlyCharge(
								this,
								billingAccount,
								pack,
								pack.getPackageDefinition().getMonthlyRateTransactionType(),
								null,
								1000,
								rate,
								administrator
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
								if(limit==null) throw new SQLException("HttpdServers exist, but no limit defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
								if(hss.size()>limit.getSoftLimit()) {
									Money addRate = limit.getAdditionalRate();
									if(addRate==null) throw new SQLException("Additional HttpdServers exist, but no additional rate defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									TransactionType addType=limit.getAdditionalTransactionType();
									if(addType==null) throw new SQLException("Additional HttpdServers exist, but no additional TransactionType defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									charges.add(
										new MonthlyCharge(
											this,
											billingAccount,
											pack,
											addType,
											"Additional HTTP Servers ("+limit.getSoftLimit()+" included with package, have "+hss.size()+")",
											(hss.size()-limit.getSoftLimit())*1000,
											addRate,
											administrator
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
							List<IpAddress> ips=pack.getIPAddresses();
							if(!ips.isEmpty()) {
								if(limit==null) throw new SQLException("IPAddresses exist, but no limit defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
								if(ips.size()>limit.getSoftLimit()) {
									Money addRate = limit.getAdditionalRate();
									if(addRate==null) throw new SQLException("Additional IPAddresses exist, but no additional rate defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									TransactionType addType=limit.getAdditionalTransactionType();
									if(addType==null) throw new SQLException("Additional IPAddresses exist, but no additional TransactionType defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									charges.add(
										new MonthlyCharge(
											this,
											billingAccount,
											pack,
											addType,
											"Additional IP Addresses ("+limit.getSoftLimit()+" included with package, have "+ips.size()+")",
											(ips.size()-limit.getSoftLimit())*1000,
											addRate,
											administrator
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
								int javavmCount=I;
								if(javavmCount>0) {
									if(limit==null) throw new SQLException("Java virtual machines exist, but no limit defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									if(javavmCount>limit.getSoftLimit()) {
										Money addRate = limit.getAdditionalRate();
										if(addRate==null) throw new SQLException("Additional Java virtual machines exist, but no additional rate defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
										TransactionType addType=limit.getAdditionalTransactionType();
										if(addType==null) throw new SQLException("Additional Java virtual machines exist, but no additional TransactionType defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
										charges.add(
											new MonthlyCharge(
												this,
												billingAccount,
												pack,
												addType,
												"Additional Java Virtual Machines ("+limit.getSoftLimit()+" included with package, have "+javavmCount+")",
												(javavmCount-limit.getSoftLimit())*1000,
												addRate,
												administrator
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
							List<MysqlReplication> fmrs = pack.getFailoverMySQLReplications();
							if(!fmrs.isEmpty()) {
								if(limit==null) throw new SQLException("FailoverMySQLReplications exist, but no limit defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
								if(fmrs.size()>limit.getSoftLimit()) {
									Money addRate = limit.getAdditionalRate();
									if(addRate==null) throw new SQLException("Additional FailoverMySQLReplications exist, but no additional rate defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									TransactionType addType=limit.getAdditionalTransactionType();
									if(addType==null) throw new SQLException("Additional FailoverMySQLReplications exist, but no additional TransactionType defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									charges.add(
										new MonthlyCharge(
											this,
											billingAccount,
											pack,
											addType,
											"Additional MySQL Replications ("+limit.getSoftLimit()+" included with package, have "+fmrs.size()+")",
											(fmrs.size()-limit.getSoftLimit())*1000,
											addRate,
											administrator
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
							Integer I=emailsPerPackage.get(pack);
							if(I!=null) {
								int emailCount=I;
								if(emailCount>0) {
									if(limit==null) throw new SQLException("Email inboxes exist, but no limit defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									if(emailCount>limit.getSoftLimit()) {
										Money addRate = limit.getAdditionalRate();
										if(addRate==null) throw new SQLException("Additional Email inboxes exist, but no additional rate defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
										TransactionType addType=limit.getAdditionalTransactionType();
										if(addType==null) throw new SQLException("Additional Email inboxes exist, but no additional TransactionType defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
										charges.add(
											new MonthlyCharge(
												this,
												billingAccount,
												pack,
												addType,
												"Additional Email Inboxes ("+limit.getSoftLimit()+" included with package, have "+emailCount+")",
												(emailCount-limit.getSoftLimit())*1000,
												addRate,
												administrator
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
							List<Site> hss=pack.getHttpdSites();
							if(!hss.isEmpty()) {
								if(limit==null) throw new SQLException("HttpdSites exist, but no limit defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
								if(hss.size()>limit.getSoftLimit()) {
									Money addRate = limit.getAdditionalRate();
									if(addRate==null) throw new SQLException("Additional HttpdSites exist, but no additional rate defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									TransactionType addType=limit.getAdditionalTransactionType();
									if(addType==null) throw new SQLException("Additional HttpdSites exist, but no additional TransactionType defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									charges.add(
										new MonthlyCharge(
											this,
											billingAccount,
											pack,
											addType,
											"Additional Web Sites ("+limit.getSoftLimit()+" included with package, have "+hss.size()+")",
											(hss.size()-limit.getSoftLimit())*1000,
											addRate,
											administrator
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
								int userCount=I;
								if(userCount>0) {
									if(limit==null) throw new SQLException("Shell accounts exist, but no limit defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
									if(userCount>limit.getSoftLimit()) {
										Money addRate = limit.getAdditionalRate();
										if(addRate==null) throw new SQLException("Additional Shell accounts exist, but no additional rate defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
										TransactionType addType=limit.getAdditionalTransactionType();
										if(addType==null) throw new SQLException("Additional Shell accounts exist, but no additional TransactionType defined for Package="+pack.getPkey()+", PackageDefinition="+packageDefinition.getPkey());
										charges.add(
											new MonthlyCharge(
												this,
												billingAccount,
												pack,
												addType,
												"Additional Shell Accounts ("+limit.getSoftLimit()+" included with package, have "+userCount+")",
												(userCount-limit.getSoftLimit())*1000,
												addRate,
												administrator
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

		// Sort values
		sortIfNeeded(charges);

		// return
		return charges;
	}

	/**
	 * Gets the list of all <code>monthly_charges</code> in the database.  In addition
	 * to the monthly charges in the database, the package charges, additional email users,
	 * additional ip addresses, and additional shell users are added to the billing.
	 *
	 * @return  the <code>MonthlyCharge</code> objects.
	 */
	public List<MonthlyCharge> getMonthlyCharges(Administrator administrator, Account account) throws IOException, SQLException {
		return Collections.unmodifiableList(getModifiableMonthlyCharges(administrator, account));
	}

	public List<MonthlyCharge> getMonthlyCharges(Account account) throws SQLException, IOException {
		return getMonthlyCharges(connector.getCurrentAdministrator(), account);
	}

	@Override
	public List<MonthlyCharge> getRows() throws SQLException, IOException {
		return getMonthlyCharges(connector.getCurrentAdministrator(), null);
	}

	@Override
	public List<MonthlyCharge> getRowsCopy() throws IOException, SQLException {
		return getModifiableMonthlyCharges(connector.getCurrentAdministrator(), null);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MONTHLY_CHARGES;
	}

	@Override
	public List<MonthlyCharge> getIndexedRows(int col, Object value) {
		throw new UnsupportedOperationException("Indexed rows are not supported on MonthlyChargeTable");
	}
}
