/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.HashedPassword;
import com.aoindustries.aoserv.client.validator.MySQLDatabaseName;
import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.aoserv.client.validator.MySQLUserId;
import com.aoindustries.aoserv.client.validator.PostgresDatabaseName;
import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.dto.DtoFactory;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.DomainLabel;
import com.aoindustries.net.DomainLabels;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.Email;
import com.aoindustries.net.HostAddress;
import com.aoindustries.net.InetAddress;
import com.aoindustries.net.MacAddress;
import com.aoindustries.table.Row;
import com.aoindustries.util.ComparatorUtils;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.i18n.Money;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;

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
abstract public class AOServObject<K,T extends AOServObject<K,T>> implements Row, AOServStreamable {

	protected AOServObject() {
	}

	// <editor-fold defaultstate="collapsed" desc="Ordering">
	/**
	 * @deprecated  This method moved to {@link ComparatorUtils#compareIgnoreCaseConsistentWithEquals(java.lang.String, java.lang.String)}
	 */
	@Deprecated
	public static int compareIgnoreCaseConsistentWithEquals(String s1, String s2) {
		return ComparatorUtils.compareIgnoreCaseConsistentWithEquals(s1, s2);
	}

	/**
	 * @deprecated  This method moved to {@link ComparatorUtils#compare(int, int)}
	 */
	@Deprecated
	public static int compare(int i1, int i2) {
		return ComparatorUtils.compare(i1, i2);
	}

	/**
	 * @deprecated  This method moved to {@link ComparatorUtils#compare(short, short)}
	 */
	@Deprecated
	public static int compare(short s1, short s2) {
		return ComparatorUtils.compare(s1, s2);
	}

	/**
	 * @deprecated  This method moved to {@link ComparatorUtils#compare(long, long)}
	 */
	@Deprecated
	public static int compare(long l1, long l2) {
		return ComparatorUtils.compare(l1, l2);
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
	protected static DomainLabel getDomainLabel(com.aoindustries.net.dto.DomainLabel domainLabel) throws ValidationException {
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
	protected static DomainLabels getDomainLabels(com.aoindustries.net.dto.DomainLabels domainLabels) throws ValidationException {
		if(domainLabels==null) return null;
		return DomainLabels.valueOf(domainLabels.getLabels());
	}

	/**
	 * null-safe domain name conversion.
	 */
	protected static DomainName getDomainName(com.aoindustries.net.dto.DomainName domainName) throws ValidationException {
		if(domainName==null) return null;
		return DomainName.valueOf(domainName.getDomain());
	}

	/**
	 * null-safe email conversion.
	 */
	protected static Email getEmail(com.aoindustries.net.dto.Email email) throws ValidationException {
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
	protected static HostAddress getHostname(com.aoindustries.net.dto.HostAddress hostname) throws ValidationException {
		if(hostname==null) return null;
		return HostAddress.valueOf(hostname.getAddress());
	}

	/**
	 * null-safe inet address conversion.
	 */
	protected static InetAddress getInetAddress(com.aoindustries.net.dto.InetAddress inetAddress) throws ValidationException {
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
	protected static MacAddress getMacAddress(com.aoindustries.net.dto.MacAddress macAddress) throws ValidationException {
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
	 * null-safe port conversion.
	 */
	protected static com.aoindustries.net.Port getPort(com.aoindustries.net.dto.Port port) throws ValidationException {
		if(port == null) return null;
		return com.aoindustries.net.Port.valueOf(
			port.getPort(),
			com.aoindustries.net.Protocol.valueOf(port.getProtocol())
		);
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
	 * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
	 * 
	 * @see  #write(CompressedDataOutputStream,AOServProtocol.Version)
	 */
	@Deprecated
	@Override
	final public void write(CompressedDataOutputStream out, String version) throws IOException {
		write(out, AOServProtocol.Version.getVersion(version));
	}

	@Override
	public abstract void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException;
}
