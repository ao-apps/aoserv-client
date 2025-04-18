/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.schema;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.sql.SQLUtility;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.AoservObject;
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.sql.Parser;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public final class Table extends GlobalObjectIntegerKey<Table> {

  static final int COLUMN_NAME = 1;

  /**
   * Each set of tables in the protocol used by this client version.
   */
  public enum TableId {
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
    Currency,
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
    NoticeLogBalance,
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

  private String name;
  private String sinceVersion;
  private String lastVersion;
  private String display;
  private boolean isPublic;
  private String description;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Table() {
    // Do nothing
  }

  /* Unused 2021-11-04?
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
   */

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case 0:
        return pkey;
      case COLUMN_NAME:
        return name;
      case 2:
        return sinceVersion;
      case 3:
        return lastVersion;
      case 4:
        return display;
      case 5:
        return isPublic;
      case 6:
        return description;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
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

  public AoservProtocol getSinceVersion(AoservConnector connector) throws SQLException, IOException {
    AoservProtocol obj = connector.getSchema().getAoservProtocol().get(sinceVersion);
    if (obj == null) {
      throw new SQLException("Unable to find AoservProtocol: " + sinceVersion);
    }
    return obj;
  }

  public String getLastVersion_version() {
    return lastVersion;
  }

  public AoservProtocol getLastVersion(AoservConnector connector) throws SQLException, IOException {
    if (lastVersion == null) {
      return null;
    }
    AoservProtocol obj = connector.getSchema().getAoservProtocol().get(lastVersion);
    if (obj == null) {
      throw new SQLException("Unable to find AoservProtocol: " + lastVersion);
    }
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
  public TableId getTableId() {
    return TableId.SCHEMA_TABLES;
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
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    name = in.readUTF().intern();
    sinceVersion = in.readUTF().intern();
    lastVersion = InternUtils.intern(in.readNullUTF());
    display = in.readUTF();
    isPublic = in.readBoolean();
    description = in.readUTF();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
      out.writeUTF(name);
      out.writeCompressedInt(pkey);
      out.writeUTF(display);
      out.writeBoolean(isPublic);
      out.writeUTF(description);
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
        // dataverse_editor
        out.writeNullUTF(null);
      }
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_101) >= 0) {
        out.writeUTF(sinceVersion);
      }
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_104) >= 0) {
        out.writeNullUTF(lastVersion);
      }
      if (
          protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4) >= 0
              && protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0
      ) {
        // default_order_by
        out.writeNullUTF(null);
      }
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

  public AoservTable<?, ? extends AoservObject<?, ?>> getAoservTable(AoservConnector connector) {
    return connector.getTable(pkey);
  }

  public List<Command> getAoshCommands(AoservConnector connector) throws IOException, SQLException {
    return connector.getAosh().getCommand().getAoshCommands(this);
  }

  public Column getSchemaColumn(AoservConnector connector, String name) throws IOException, SQLException {
    return connector.getSchema().getColumn().getSchemaColumn(this, name);
  }

  public Column getSchemaColumn(AoservConnector connector, int index) throws IOException, SQLException {
    return connector.getSchema().getColumn().getSchemaColumn(this, index);
  }

  public List<Column> getSchemaColumns(AoservConnector connector) throws IOException, SQLException {
    return connector.getSchema().getColumn().getSchemaColumns(this);
  }

  public List<ForeignKey> getSchemaForeignKeys(AoservConnector connector) throws IOException, SQLException {
    return connector.getSchema().getForeignKey().getSchemaForeignKeys(this);
  }

  private static final String[] descColumns = {
      "column", "type", "null", "unique", "references", "referenced_by", "description"
  };

  private static final boolean[] descRightAligns = {
      Type.alignRight(Type.STRING), // column
      Type.alignRight(Type.STRING), // type
      Type.alignRight(Type.BOOLEAN), // null
      Type.alignRight(Type.BOOLEAN), // unique
      Type.alignRight(Type.STRING), // references
      Type.alignRight(Type.STRING), // referenced_by
      Type.alignRight(Type.STRING) // description
  };

  private static String formatForeignKeys(AoservConnector connector, List<ForeignKey> fkeys, boolean foreign) throws IOException, SQLException {
    if (!fkeys.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (ForeignKey key : fkeys) {
        if (sb.length() > 0) {
          sb.append('\n');
        }
        Column other = foreign ? key.getForeignColumn(connector) : key.getColumn(connector);
        sb
            .append(Parser.quote(other.getTable(connector).getName()))
            .append('.')
            .append(Parser.quote(other.getName()));
      }
      return sb.toString();
    } else {
      return null;
    }
  }

  public void printDescription(AoservConnector connector, TerminalWriter out, boolean isInteractive) throws IOException, SQLException {
    out.println();
    out.boldOn();
    out.print("TABLE NAME");
    out.attributesOff();
    out.println();
    out.print("       ");
    out.println(name);
    if (description != null && description.length() > 0) {
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
    List<Column> columns = getSchemaColumns(connector);

    // Build the rows
    List<Object[]> rows = new ArrayList<>(columns.size());
    for (Column column : columns) {
      rows.add(new Object[]{
          column.getName(),
          column.getType(connector).getName(),
          Boolean.toString(column.isNullable()),
          Boolean.toString(column.isUnique()),
          formatForeignKeys(connector, column.getReferences(connector), true),
          formatForeignKeys(connector, column.getReferencedBy(connector), false),
          column.getDescription()
      });
    }

    // Display the results
    SQLUtility.printTable(descColumns, rows, out, isInteractive, descRightAligns);
  }
}
