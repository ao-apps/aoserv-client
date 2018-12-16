/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2015, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.schema;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.InternUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
final public class Table extends GlobalObjectIntegerKey<Table> {

	static final int COLUMN_NAME = 1;

	/**
	 * Each set of tables in the protocol used by this client version.
	 */
	public enum TableID {
		AO_SERVER_DAEMON_HOSTS,
		AO_SERVERS,
		AOSERV_PERMISSIONS,
		AOSERV_PROTOCOLS,
		AOSH_COMMANDS,
		ARCHITECTURES,
		BACKUP_PARTITIONS,
		BACKUP_REPORTS,
		BACKUP_RETENTIONS,
		BANK_ACCOUNTS,
		BANK_TRANSACTION_TYPES,
		BANK_TRANSACTIONS,
		BANKS,
		BLACKHOLE_EMAIL_ADDRESSES,
		BRANDS,
		BUSINESS_ADMINISTRATORS,
		BUSINESS_ADMINISTRATOR_PERMISSIONS,
		BUSINESS_PROFILES,
		BUSINESSES,
		BUSINESS_SERVERS,
		COUNTRY_CODES,
		CREDIT_CARD_PROCESSORS,
		CREDIT_CARD_TRANSACTIONS,
		CREDIT_CARDS,
		CVS_REPOSITORIES,
		CYRUS_IMAPD_BINDS,
		CYRUS_IMAPD_SERVERS,
		DISABLE_LOG,
		DISTRO_FILE_TYPES,
		DISTRO_FILES,
		DISTRO_REPORT_TYPES,
		DNS_FORBIDDEN_ZONES,
		DNS_RECORDS,
		DNS_TLDS,
		DNS_TYPES,
		DNS_ZONES,
		EMAIL_ADDRESSES,
		EMAIL_ATTACHMENT_BLOCKS,
		EMAIL_ATTACHMENT_TYPES,
		EMAIL_DOMAINS,
		EMAIL_FORWARDING,
		EMAIL_LIST_ADDRESSES,
		EMAIL_LISTS,
		EMAIL_PIPE_ADDRESSES,
		EMAIL_PIPES,
		EMAIL_SMTP_RELAY_TYPES,
		EMAIL_SMTP_RELAYS,
		EMAIL_SMTP_SMART_HOST_DOMAINS,
		EMAIL_SMTP_SMART_HOSTS,
		EMAIL_SPAMASSASSIN_INTEGRATION_MODES,
		ENCRYPTION_KEYS,
		EXPENSE_CATEGORIES,
		FAILOVER_FILE_LOG,
		FAILOVER_FILE_REPLICATIONS,
		FAILOVER_FILE_SCHEDULE,
		FAILOVER_MYSQL_REPLICATIONS,
		FILE_BACKUP_SETTINGS,
		FIREWALLD_ZONES,
		FTP_GUEST_USERS,
		HTTPD_BINDS,
		HTTPD_JBOSS_SITES,
		HTTPD_JBOSS_VERSIONS,
		HTTPD_JK_CODES,
		HTTPD_JK_PROTOCOLS,
		HTTPD_SERVERS,
		HTTPD_SHARED_TOMCATS,
		HTTPD_SITE_AUTHENTICATED_LOCATIONS,
		HTTPD_SITE_BIND_HEADERS,
		RewriteRule,
		HTTPD_SITE_BINDS,
		HTTPD_SITE_URLS,
		HTTPD_SITES,
		HTTPD_STATIC_SITES,
		HTTPD_TOMCAT_CONTEXTS,
		HTTPD_TOMCAT_DATA_SOURCES,
		HTTPD_TOMCAT_PARAMETERS,
		HTTPD_TOMCAT_SITE_JK_MOUNTS,
		HTTPD_TOMCAT_SITES,
		HTTPD_TOMCAT_SHARED_SITES,
		HTTPD_TOMCAT_STD_SITES,
		HTTPD_TOMCAT_VERSIONS,
		HTTPD_WORKERS,
		IP_ADDRESSES,
		IpAddressMonitoring,
		IP_REPUTATION_LIMITER_LIMITS,
		IP_REPUTATION_LIMITER_SETS,
		IP_REPUTATION_LIMITERS,
		IP_REPUTATION_SET_HOSTS,
		IP_REPUTATION_SET_NETWORKS,
		IP_REPUTATION_SETS,
		LANGUAGES,
		LINUX_ACC_ADDRESSES,
		LINUX_ACCOUNT_TYPES,
		LINUX_ACCOUNTS,
		LINUX_GROUP_ACCOUNTS,
		LINUX_GROUP_TYPES,
		LINUX_GROUPS,
		LINUX_SERVER_ACCOUNTS,
		LINUX_SERVER_GROUPS,
		MAJORDOMO_LISTS,
		MAJORDOMO_SERVERS,
		MAJORDOMO_VERSIONS,
		MASTER_HOSTS,
		MASTER_PROCESSES,
		MASTER_SERVER_STATS,
		MASTER_SERVERS,
		MASTER_USERS,
		MONTHLY_CHARGES,
		MYSQL_DATABASES,
		MYSQL_DB_USERS,
		MYSQL_SERVER_USERS,
		MYSQL_SERVERS,
		MYSQL_USERS,
		NET_BIND_FIREWALLD_ZONES,
		NET_BINDS,
		NET_DEVICE_IDS,
		NET_DEVICES,
		NET_TCP_REDIRECTS,
		NOTICE_LOG,
		NOTICE_TYPES,
		OPERATING_SYSTEM_VERSIONS,
		OPERATING_SYSTEMS,
		PACKAGE_CATEGORIES,
		PACKAGE_DEFINITION_LIMITS,
		PACKAGE_DEFINITIONS,
		PACKAGES,
		PAYMENT_TYPES,
		PHYSICAL_SERVERS,
		POSTGRES_DATABASES,
		POSTGRES_ENCODINGS,
		POSTGRES_SERVER_USERS,
		POSTGRES_SERVERS,
		POSTGRES_USERS,
		POSTGRES_VERSIONS,
		PRIVATE_FTP_SERVERS,
		PROCESSOR_TYPES,
		PROTOCOLS,
		RACKS,
		RESELLERS,
		RESOURCES,
		SCHEMA_COLUMNS,
		SCHEMA_FOREIGN_KEYS,
		SCHEMA_TABLES,
		SCHEMA_TYPES,
		SENDMAIL_BINDS,
		SENDMAIL_SERVERS,
		SERVER_FARMS,
		SERVERS,
		SHELLS,
		SIGNUP_REQUEST_OPTIONS,
		SIGNUP_REQUESTS,
		SPAM_EMAIL_MESSAGES,
		SSL_CERTIFICATE_NAMES,
		SSL_CERTIFICATE_OTHER_USES,
		SSL_CERTIFICATES,
		SYSTEM_EMAIL_ALIASES,
		TECHNOLOGIES,
		TECHNOLOGY_CLASSES,
		TECHNOLOGY_NAMES,
		TECHNOLOGY_VERSIONS,
		TICKET_ACTION_TYPES,
		TICKET_ACTIONS,
		TICKET_ASSIGNMENTS,
		TICKET_BRAND_CATEGORIES,
		TICKET_CATEGORIES,
		TICKET_PRIORITIES,
		TICKET_STATI,
		TICKET_TYPES,
		TICKETS,
		TIME_ZONES,
		TRANSACTION_TYPES,
		TRANSACTIONS,
		US_STATES,
		USERNAMES,
		VIRTUAL_DISKS,
		VIRTUAL_SERVERS,
		WhoisHistory,
		WhoisHistoryAccount
	}

	private static final String[] descColumns={
		"column", "type", "null", "unique", "references", "referenced_by", "description"
	};

	private static final boolean[] descRightAligns={
		false, false, false, false, false, false, false
	};

	private String name;
	private String sinceVersion;
	private String lastVersion;
	private String display;
	private boolean isPublic;
	private String description;

	public Table() {
	}

	public Table(
		int id,
		String name,
		String sinceVersion,
		String lastVersion,
		String display,
		boolean isPublic,
		String description
	) {
		this.pkey = id;
		this.name = name;
		this.sinceVersion = sinceVersion;
		this.lastVersion = lastVersion;
		this.display = display;
		this.isPublic = isPublic;
		this.description = description;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case 0: return pkey;
			case COLUMN_NAME: return name;
			case 2: return sinceVersion;
			case 3: return lastVersion;
			case 4: return display;
			case 5: return isPublic;
			case 6: return description;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getId() {
		return pkey;
	}

	public String getName() {
		return name;
	}

	public String getSinceVersion_version() {
		return sinceVersion;
	}

	public AoservProtocol getSinceVersion(AOServConnector connector) throws SQLException, IOException {
		AoservProtocol obj = connector.getSchema().getAoservProtocols().get(sinceVersion);
		if(obj == null) throw new SQLException("Unable to find AOServProtocol: " + sinceVersion);
		return obj;
	}

	public String getLastVersion_version() {
		return lastVersion;
	}

	public AoservProtocol getLastVersion(AOServConnector connector) throws SQLException, IOException {
		if(lastVersion == null) return null;
		AoservProtocol obj = connector.getSchema().getAoservProtocols().get(lastVersion);
		if(obj == null) throw new SQLException("Unable to find AOServProtocol: " + lastVersion);
		return obj;
	}

	public String getDisplay() {
		return display;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public TableID getTableID() {
		return TableID.SCHEMA_TABLES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey = result.getInt(pos++);
		name = result.getString(pos++);
		sinceVersion = result.getString(pos++);
		lastVersion = result.getString(pos++);
		display = result.getString(pos++);
		isPublic = result.getBoolean(pos++);
		description = result.getString(pos++);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		name = in.readUTF().intern();
		sinceVersion = in.readUTF().intern();
		lastVersion = InternUtils.intern(in.readNullUTF());
		display = in.readUTF();
		isPublic = in.readBoolean();
		description = in.readUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
			out.writeUTF(name);
			out.writeCompressedInt(pkey);
			out.writeUTF(display);
			out.writeBoolean(isPublic);
			out.writeUTF(description);
			if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) out.writeNullUTF(null); // dataverse_editor
			if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_101) >= 0) out.writeUTF(sinceVersion);
			if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_104) >= 0) out.writeNullUTF(lastVersion);
			if(
				protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4)>=0
				&& protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0
			) out.writeNullUTF(null); // default_order_by
		} else {
			out.writeCompressedInt(pkey);
			out.writeUTF(name);
			out.writeUTF(sinceVersion);
			out.writeNullUTF(lastVersion);
			out.writeUTF(display);
			out.writeBoolean(isPublic);
			out.writeUTF(description);
		}
	}

	@Override
	public String toStringImpl() {
		return name;
	}

	public AOServTable<?,? extends AOServObject<?,?>> getAOServTable(AOServConnector connector) {
		return connector.getTable(pkey);
	}

	public List<Command> getAOSHCommands(AOServConnector connector) throws IOException, SQLException {
		return connector.getAosh().getAoshCommands().getAOSHCommands(this);
	}

	public Column getSchemaColumn(AOServConnector connector, String name) throws IOException, SQLException {
		return connector.getSchema().getSchemaColumns().getSchemaColumn(this, name);
	}

	public Column getSchemaColumn(AOServConnector connector, int index) throws IOException, SQLException {
		return connector.getSchema().getSchemaColumns().getSchemaColumn(this, index);
	}

	public List<Column> getSchemaColumns(AOServConnector connector) throws IOException, SQLException {
		return connector.getSchema().getSchemaColumns().getSchemaColumns(this);
	}

	public List<ForeignKey> getSchemaForeignKeys(AOServConnector connector) throws IOException, SQLException {
		return connector.getSchema().getSchemaForeignKeys().getSchemaForeignKeys(this);
	}

	public void printDescription(AOServConnector connector, TerminalWriter out, boolean isInteractive) throws IOException, SQLException {
		out.println();
		out.boldOn();
		out.print("TABLE NAME");
		out.attributesOff();
		out.println();
		out.print("       ");
		out.println(name);
		if(description != null && description.length() > 0) {
			out.println();
			out.boldOn();
			out.print("DESCRIPTION");
			out.attributesOff();
			out.println();
			out.print("       ");
			out.println(description);
		}
		out.println();
		out.boldOn();
		out.print("COLUMNS");
		out.attributesOff();
		out.println();
		out.println();

		// Get the list of columns
		List<Column> columns=getSchemaColumns(connector);
		int len=columns.size();

		// Build the Object[] of values
		Object[] values=new Object[len*7];
		int pos=0;
		for(int c=0;c<len;c++) {
			Column column=columns.get(c);
			values[pos++] = column.getName();
			values[pos++] = column.getType(connector).getName();
			values[pos++] = column.isNullable()?"true":"false";
			values[pos++] = column.isUnique()?"true":"false";
			List<ForeignKey> fkeys=column.getReferences(connector);
			if(!fkeys.isEmpty()) {
				StringBuilder SB=new StringBuilder();
				for(int d=0;d<fkeys.size();d++) {
					ForeignKey key=fkeys.get(d);
					if(d>0) SB.append('\n');
					Column other=key.getForeignColumn(connector);
					SB
						.append(other.getTable(connector).getName())
						.append('.')
						.append(other.getName())
					;
				}
				values[pos++]=SB.toString();
			} else values[pos++]=null;

			fkeys=column.getReferencedBy(connector);
			if(!fkeys.isEmpty()) {
				StringBuilder SB=new StringBuilder();
				for(int d=0;d<fkeys.size();d++) {
					ForeignKey key=fkeys.get(d);
					if(d>0) SB.append('\n');
					Column other=key.getColumn(connector);
					SB
						.append(other.getTable(connector).getName())
						.append('.')
						.append(other.getName())
					;
				}
				values[pos++]=SB.toString();
			} else values[pos++]=null;
			values[pos++]=column.getDescription();
		}

		// Display the results
		SQLUtility.printTable(descColumns, values, out, isInteractive, descRightAligns);
	}
}
