/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.DomainLabel;
import com.aoindustries.aoserv.client.validator.DomainLabels;
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.Email;
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.HashedPassword;
import com.aoindustries.aoserv.client.validator.HostAddress;
import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.MacAddress;
import com.aoindustries.aoserv.client.validator.MySQLDatabaseName;
import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.aoserv.client.validator.MySQLUserId;
import com.aoindustries.aoserv.client.validator.PostgresDatabaseName;
import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.Streamable;
import com.aoindustries.table.Row;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * An <code>AOServObject</code> is the lowest level object
 * for all data in the system.  Each <code>AOServObject</code>
 * belongs to a <code>AOServTable</code>, and each table
 * contains <code>AOServObject</code>s.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServTable
 */
abstract public class AOServObject<K,T extends AOServObject<K,T>> implements Row, Streamable {

	protected AOServObject() {
	}

	// <editor-fold defaultstate="collapsed" desc="Ordering">
	private static final Collator collator = Collator.getInstance(Locale.ROOT);
	public static int compareIgnoreCaseConsistentWithEquals(String S1, String S2) {
		if(S1==S2) return 0;
		int diff = collator.compare(S1, S2);
		if(diff!=0) return diff;
		return S1.compareTo(S2);
	}

	public static int compare(int i1, int i2) {
		return i1<i2 ? -1 : i1==i2 ? 0 : 1;
	}

	public static int compare(short s1, short s2) {
		return s1<s2 ? -1 : s1==s2 ? 0 : 1;
	}

	public static int compare(long l1, long l2) {
		return l1<l2 ? -1 : l1==l2 ? 0 : 1;
	}

	/**
	 * Compares two objects allowing for nulls, sorts non-null before null.
	 */
	public static <T extends Comparable<T>> int compare(T obj1, T obj2) {
		if(obj1!=null) {
			return obj2!=null ? obj1.compareTo(obj2) : -1;
		} else {
			return obj2!=null ? 1 : 0;
		}
	}

	// TODO: Remove in AOServ 2
	final public int compareTo(AOServConnector conn, AOServObject other, SQLExpression[] sortExpressions, boolean[] sortOrders) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
		int len=sortExpressions.length;
		for(int c=0;c<len;c++) {
			SQLExpression expr=sortExpressions[c];
			SchemaType type=expr.getType();
			int diff=type.compareTo(
				expr.getValue(conn, this),
				expr.getValue(conn, other)
			);
			if(diff!=0) return sortOrders[c]?diff:-diff;
		}
		return 0;
	}

	// TODO: Remove in AOServ 2
	final public int compareTo(AOServConnector conn, Comparable value, SQLExpression[] sortExpressions, boolean[] sortOrders) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
		int len=sortExpressions.length;
		for(int c=0;c<len;c++) {
			SQLExpression expr=sortExpressions[c];
			SchemaType type=expr.getType();
			int diff=type.compareTo(
				expr.getValue(conn, this),
				value
			);
			if(diff!=0) return sortOrders[c]?diff:-diff;
		}
		return 0;
	}

	// TODO: Remove in AOServ 2
	final public int compareTo(AOServConnector conn, Object[] OA, SQLExpression[] sortExpressions, boolean[] sortOrders) throws IllegalArgumentException, SQLException, UnknownHostException, IOException {
		int len=sortExpressions.length;
		if(len!=OA.length) throw new IllegalArgumentException("Array length mismatch when comparing AOServObject to Object[]: sortExpressions.length="+len+", OA.length="+OA.length);

		for(int c=0;c<len;c++) {
			SQLExpression expr=sortExpressions[c];
			SchemaType type=expr.getType();
			int diff=type.compareTo(
				expr.getValue(conn, this),
				OA[c]
			);
			if(diff!=0) return sortOrders[c]?diff:-diff;
		}
		return 0;
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="DTO">
	/**
	 * null-safe getDto.
	 */
	protected static <B> B getDto(DtoFactory<B> dtoFactory) {
		return dtoFactory==null ? null : dtoFactory.getDto();
	}

	/**
	 * null-safe getDto for Money.
	 */
	protected static com.aoindustries.aoserv.client.dto.Money getDto(Money money) {
		return money==null ? null : new com.aoindustries.aoserv.client.dto.Money(money.getCurrency().getCurrencyCode(), money.getValue());
	}

	/**
	 * null-safe accounting code conversion.
	 */
	protected static AccountingCode getAccountingCode(com.aoindustries.aoserv.client.dto.AccountingCode accounting) throws ValidationException {
		if(accounting==null) return null;
		return AccountingCode.valueOf(accounting.getAccounting());
	}

	/**
	 * null-safe domain label conversion.
	 */
	protected static DomainLabel getDomainLabel(com.aoindustries.aoserv.client.dto.DomainLabel domainLabel) throws ValidationException {
		if(domainLabel==null) return null;
		return DomainLabel.valueOf(domainLabel.getLabel());
	}

	/**
	 * null-safe conversion from java.util.Date to java.sql.Date, will cast when possible.
	 */
	/*
	protected static java.sql.Date getDate(Date date) {
		if(date==null) return null;
		if(date instanceof java.sql.Date) return (java.sql.Date)date;
		return new java.sql.Date(date.getTime());
	}*/

	/**
	 * null-safe domain labels conversion.
	 */
	protected static DomainLabels getDomainLabels(com.aoindustries.aoserv.client.dto.DomainLabels domainLabels) throws ValidationException {
		if(domainLabels==null) return null;
		return DomainLabels.valueOf(domainLabels.getLabels());
	}

	/**
	 * null-safe domain name conversion.
	 */
	protected static DomainName getDomainName(com.aoindustries.aoserv.client.dto.DomainName domainName) throws ValidationException {
		if(domainName==null) return null;
		return DomainName.valueOf(domainName.getDomain());
	}

	/**
	 * null-safe email conversion.
	 */
	protected static Email getEmail(com.aoindustries.aoserv.client.dto.Email email) throws ValidationException {
		if(email==null) return null;
		return Email.valueOf(email.getLocalPart(), DomainName.valueOf(email.getDomain().getDomain()));
	}

	/**
	 * null-safe GECOS conversion.
	 */
	protected static Gecos getGecos(com.aoindustries.aoserv.client.dto.Gecos gecos) throws ValidationException {
		if(gecos==null) return null;
		return Gecos.valueOf(gecos.getValue());
	}

	/**
	 * null-safe group id conversion.
	 */
	protected static GroupId getGroupId(com.aoindustries.aoserv.client.dto.GroupId gid) throws ValidationException {
		if(gid==null) return null;
		return GroupId.valueOf(gid.getId());
	}

	/**
	 * null-safe hashed password conversion.
	 */
	protected static HashedPassword getHashedPassword(com.aoindustries.aoserv.client.dto.HashedPassword hashedPassword) throws ValidationException {
		if(hashedPassword==null) return null;
		return HashedPassword.valueOf(hashedPassword.getHashedPassword());
	}

	/**
	 * null-safe hostname conversion.
	 */
	protected static HostAddress getHostname(com.aoindustries.aoserv.client.dto.HostAddress hostname) throws ValidationException {
		if(hostname==null) return null;
		return HostAddress.valueOf(hostname.getAddress());
	}

	/**
	 * null-safe inet address conversion.
	 */
	protected static InetAddress getInetAddress(com.aoindustries.aoserv.client.dto.InetAddress inetAddress) throws ValidationException {
		if(inetAddress==null) return null;
		return InetAddress.valueOf(inetAddress.getAddress());
	}

	/**
	 * null-safe Linux id conversion.
	 */
	protected static com.aoindustries.aoserv.client.validator.LinuxId getLinuxID(com.aoindustries.aoserv.client.dto.LinuxId lid) throws ValidationException {
		if(lid==null) return null;
		return com.aoindustries.aoserv.client.validator.LinuxId.valueOf(lid.getId());
	}

	/**
	 * null-safe MAC address conversion.
	 */
	protected static MacAddress getMacAddress(com.aoindustries.aoserv.client.dto.MacAddress macAddress) throws ValidationException {
		if(macAddress==null) return null;
		return MacAddress.valueOf(macAddress.getAddress());
	}

	/**
	 * null-safe MySQL database name conversion.
	 */
	protected static MySQLDatabaseName getMySQLDatabaseName(com.aoindustries.aoserv.client.dto.MySQLDatabaseName databaseName) throws ValidationException {
		if(databaseName==null) return null;
		return MySQLDatabaseName.valueOf(databaseName.getName());
	}

	/**
	 * null-safe MySQL server name conversion.
	 */
	protected static MySQLServerName getMySQLServerName(com.aoindustries.aoserv.client.dto.MySQLServerName serverName) throws ValidationException {
		if(serverName==null) return null;
		return MySQLServerName.valueOf(serverName.getName());
	}

	/**
	 * null-safe MySQL user id conversion.
	 */
	protected static MySQLUserId getMySQLUserId(com.aoindustries.aoserv.client.dto.MySQLUserId mysqlUserId) throws ValidationException {
		if(mysqlUserId==null) return null;
		return MySQLUserId.valueOf(mysqlUserId.getId());
	}

	/**
	 * null-safe net port conversion.
	 */
	protected static com.aoindustries.aoserv.client.validator.NetPort getNetPort(com.aoindustries.aoserv.client.dto.NetPort netPort) throws ValidationException {
		if(netPort==null) return null;
		return com.aoindustries.aoserv.client.validator.NetPort.valueOf(netPort.getPort());
	}

	/**
	 * null-safe PostgreSQL database name conversion.
	 */
	protected static PostgresDatabaseName getPostgresDatabaseName(com.aoindustries.aoserv.client.dto.PostgresDatabaseName databaseName) throws ValidationException {
		if(databaseName==null) return null;
		return PostgresDatabaseName.valueOf(databaseName.getName());
	}

	/**
	 * null-safe PostgreSQL server name conversion.
	 */
	protected static PostgresServerName getPostgresServerName(com.aoindustries.aoserv.client.dto.PostgresServerName serverName) throws ValidationException {
		if(serverName==null) return null;
		return PostgresServerName.valueOf(serverName.getName());
	}

	/**
	 * null-safe PostgreSQL user id conversion.
	 */
	protected static PostgresUserId getPostgresUserId(com.aoindustries.aoserv.client.dto.PostgresUserId postgresUserId) throws ValidationException {
		if(postgresUserId==null) return null;
		return PostgresUserId.valueOf(postgresUserId.getId());
	}

	/**
	 * null-safe conversion from Date to Long.
	 */
	protected static Long getTimeMillis(Date date) {
		if(date==null) return null;
		return date.getTime();
	}

	/**
	 * null-safe conversion from Calendar to Long.
	 */
	protected static Long getTimeMillis(Calendar datetime) {
		if(datetime==null) return null;
		return datetime.getTimeInMillis();
	}

	/**
	 * null-safe Unix path conversion.
	 */
	protected static UnixPath getUnixPath(com.aoindustries.aoserv.client.dto.UnixPath unixPath) throws ValidationException {
		if(unixPath==null) return null;
		return UnixPath.valueOf(unixPath.getPath());
	}

	/**
	 * null-safe user id conversion.
	 */
	protected static UserId getUserId(com.aoindustries.aoserv.client.dto.UserId userId) throws ValidationException {
		if(userId==null) return null;
		return UserId.valueOf(userId.getId());
	}

	/**
	 * null-safe money conversion.
	 */
	protected static Money getMoney(com.aoindustries.aoserv.client.dto.Money money) {
		if(money==null) return null;
		return new Money(Currency.getInstance(money.getCurrency()), money.getValue());
	}
	// </editor-fold>

	@Override
	final public boolean equals(Object O) {
		return O==null?false:equalsImpl(O);
	}

	boolean equalsImpl(Object O) {
		Class<?> class1=getClass();
		Class<?> class2=O.getClass();
		if(class1==class2) {
			K pkey1=getKey();
			Object pkey2=((AOServObject)O).getKey();
			if(pkey1==null || pkey2==null) throw new NullPointerException("No primary key available.");
			return pkey1.equals(pkey2);
		}
		return false;
	}

	@Override
	final public Object getColumn(int i) {
		try {
			return getColumnImpl(i);
		} catch(IOException | SQLException err) {
			throw new WrappedException(err);
		}
	}

	abstract Object getColumnImpl(int i) throws IOException, SQLException;

	final public List<Object> getColumns(AOServConnector connector) throws IOException, SQLException {
		int len=getTableSchema(connector).getSchemaColumns(connector).size();
		List<Object> buff=new ArrayList<>(len);
		for(int c=0;c<len;c++) buff.add(getColumn(c));
		return buff;
	}

	final public int getColumns(AOServConnector connector, List<Object> buff) throws IOException, SQLException {
		int len=getTableSchema(connector).getSchemaColumns(connector).size();
		for(int c=0;c<len;c++) buff.add(getColumn(c));
		return len;
	}

	public abstract K getKey();

	public abstract SchemaTable.TableID getTableID();

	final public SchemaTable getTableSchema(AOServConnector connector) throws IOException, SQLException {
		return connector.getSchemaTables().get(getTableID());
	}

	@Override
	final public int hashCode() {
		return hashCodeImpl();
	}

	int hashCodeImpl() {
		K pkey=getKey();
		if(pkey==null) throw new NullPointerException("No primary key available.");
		return pkey.hashCode();
	}

	/**
	 * Initializes this object from the raw database contents.
	 *
	 * @param  results  the <code>ResultSet</code> containing the row
	 *                  to copy into this object
	 */
	public abstract void init(ResultSet results) throws SQLException;

	@Override
	public abstract void read(CompressedDataInputStream in) throws IOException;

	/**
	 * Gets a string representation of this object in the current thread locale.
	 *
	 * @see  #toString(java.util.Locale)
	 */
	@Override
	final public String toString() {
		try {
			return toStringImpl();
		} catch(IOException | SQLException err) {
			throw new WrappedException(err);
		}
	}

	/**
	 * The default string representation is that of the key value.  If there
	 * is no key value then it uses the representation of <code>Object.toString()</code>.
	 */
	String toStringImpl() throws IOException, SQLException {
		K pkey=getKey();
		if(pkey==null) return super.toString();
		return pkey.toString();
	}

	/**
	 * @deprecated  This is maintained only for compatibility with the <code>Streamable</code> interface.
	 * 
	 * @see  #write(CompressedDataOutputStream,AOServProtocol.Version)
	 */
	@Deprecated
	@Override
	final public void write(CompressedDataOutputStream out, String version) throws IOException {
		write(out, AOServProtocol.Version.getVersion(version));
	}

	public abstract void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException;
}
