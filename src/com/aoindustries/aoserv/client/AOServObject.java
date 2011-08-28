/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.io.FastObjectInput;
import com.aoindustries.io.FastObjectOutput;
import com.aoindustries.table.IndexType;
import com.aoindustries.table.Row;
import com.aoindustries.util.AoCollections;
import com.aoindustries.util.Internable;
import com.aoindustries.util.UnionMethodSet;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.i18n.Money;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * An <code>AOServObject</code> is the lowest level object
 * for all data in the system.  Each <code>AOServObject</code>
 * belongs to an <code>AOServService</code>, each service
 * belongs to an <code>AOServConnector</code>, and each
 * connector belongs to an <code>AOServConnectorFactory</code>.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServConnector
 * @see  AOServService
 */
abstract public class AOServObject<K extends Comparable<K>>
implements Row, Serializable, Cloneable {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    /**
     * Value used when data has been filtered.
     */
    public static final String FILTERED = "*";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    /**
     * null-safe intern.
     */
    protected static <V extends Internable<V>> V intern(V value) {
        return value==null ? null : value.intern();
    }

    /**
     * null-safe intern.
     */
    protected static String intern(String value) {
        return value==null ? null : value.intern();
    }

    private transient AOServConnector connector; // volatile?

    protected AOServObject(AOServConnector connector) {
        this.connector = connector;
    }

    /**
     * All AOServObjects are cloneable (with shallow clone)
     */
    @Override
    @SuppressWarnings("unchecked")
    final public AOServObject<K> clone() {
        try {
            return (AOServObject<K>)super.clone();
        } catch(CloneNotSupportedException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FastExternalizable">
    protected static void writeNullUTF(DataOutput out, String value) throws IOException {
        if(value!=null) {
            out.writeBoolean(true);
            out.writeUTF(value);
        } else {
            out.writeBoolean(false);
        }
    }
    protected static String readNullUTF(DataInput in) throws IOException {
        return in.readBoolean() ? in.readUTF() : null;
    }
    protected static void writeNullInteger(DataOutput out, Integer value) throws IOException {
        if(value!=null) {
            out.writeBoolean(true);
            out.writeInt(value);
        } else {
            out.writeBoolean(false);
        }
    }
    protected static Integer readNullInteger(DataInput in) throws IOException {
        return in.readBoolean() ? in.readInt() : null;
    }

    private static final long serialVersionUID = 7895183404281800290L;

    protected AOServObject() {
    }

    protected long getSerialVersionUID() {
        return serialVersionUID;
    }

    protected void writeExternal(FastObjectOutput fastOut) throws IOException {
        // Do nothing
    }

    protected void readExternal(FastObjectInput fastIn) throws IOException, ClassNotFoundException {
        // Do nothing
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    private static final Collator collator = Collator.getInstance(Locale.ENGLISH);
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

    /* TODO
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
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    /**
     * {@inheritDoc}
     *
     * This implementation uses the SchemaColumn annotation.
     */
    @Override
    final public Object getColumn(int ordinal) {
        try {
            return getMethodColumns(getClass()).get(ordinal).getMethod().invoke(this);
        } catch(IllegalAccessException err) {
            throw new WrappedException(err);
        } catch(InvocationTargetException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Gets value of the column with the provided name, by using the SchemaColumn annotation.
     */
    @Override
    final public Object getColumn(String name) {
        try {
            return getMethodColumnMap(getClass()).get(name).getMethod().invoke(this);
        } catch(IllegalAccessException err) {
            throw new WrappedException(err);
        } catch(InvocationTargetException err) {
            throw new WrappedException(err);
        }
    }

    private static final ConcurrentMap<Class<? extends AOServObject>,List<MethodColumn>> columns = new ConcurrentHashMap<Class<? extends AOServObject>, List<MethodColumn>>(ServiceName.values.size()*4/3+1);

    /**
     * Gets the columns for the provided class, in column index order.
     * All SchemaColumn methods must not take parameters.
     * Ensures that no column name is duplicated.
     * Ensures that there is no gap in column index numbers.
     * Ensures that at most one primary key exists.
     * Ensures that no unique index exists without a primary key defined.
     */
    protected static List<MethodColumn> getMethodColumns(Class<? extends AOServObject> clazz) {
        List<MethodColumn> methodColumns = columns.get(clazz);
        if(methodColumns==null) {
//            Class<?> getKeyReturnType;
//            try {
//                getKeyReturnType = clazz.getMethod("getKey").getReturnType();
//            } catch(NoSuchMethodException err) {
//                throw new AssertionError("getKey() method not found: "+clazz.getName());
//            }
            // Find all the properties, all column accessors must also be a bean getter
            try {
                PropertyDescriptor[] properties = Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors();
                if(properties==null) throw new AssertionError("No properties found for class: "+clazz.getName());
                // Find all columns
                ArrayList<MethodColumn> newColumns = new ArrayList<MethodColumn>();
                Set<String> columnNames = new HashSet<String>();
                Set<String> primaryKeys = new HashSet<String>();
                Set<String> uniques = new HashSet<String>();
                for(Method method : clazz.getMethods()) {
                    SchemaColumn schemaColumn = method.getAnnotation(SchemaColumn.class);
                    if(schemaColumn!=null) {
                        // The column name is based off the property name
                        String cname = null;
                        for(PropertyDescriptor property : properties) {
                            Method readMethod = property.getReadMethod();
                            if(method.equals(readMethod)) {
                                cname = property.getName();
                                break;
                            }
                        }
                        if(cname==null) throw new AssertionError("JavaBeans property not found for column: "+clazz.getName()+"."+method.getName());
                        // All SchemaColumn methods must not take parameters.
                        if(method.getParameterTypes().length>0) throw new AssertionError("Column method should not have any parameters: "+clazz.getName()+"."+method.getName());
                        if(!columnNames.add(cname)) throw new AssertionError("Column name found twice: "+clazz.getName()+"->"+cname);
                        int order = schemaColumn.order();
                        if(order<0) throw new AssertionError("Column order<0: "+clazz.getName()+"->"+order);
                        int newSize = order+1;
                        newColumns.ensureCapacity(newSize);
                        while(newColumns.size()<newSize) newColumns.add(null);
                        IndexType indexType = schemaColumn.index();
                        if(indexType==IndexType.PRIMARY_KEY) {
                            primaryKeys.add(cname);
    //                        // Ensures the primary key column return values matches the return type of the <code>getKey</code> method.
    //                        Class<?> methodReturnType = method.getReturnType();
    //                        if(getKeyReturnType!=methodReturnType) {
    //                            throw new AssertionError(
    //                                "Mismatched key return types: "
    //                                +clazz.getName()+"."+method.getName()+"()->"+methodReturnType.getName()
    //                                +" and "+clazz.getName()+".getKey()->"+getKeyReturnType.getName()
    //                            );
    //                        }
                        } else if(indexType==IndexType.UNIQUE) {
                            uniques.add(cname);
                        }
                        if(newColumns.set(order, new MethodColumn(cname, indexType, method, schemaColumn))!=null) throw new AssertionError("Column index found twice: "+clazz.getName()+"->"+order);
                    }
                }
                int size = newColumns.size();
                // Make sure each column index is used in succession
                if(size!=columnNames.size()) {
                    // Find missing column(s)
                    StringBuilder message = new StringBuilder("The following column indexes do not have a corresponding column method: "+clazz.getName()+"->");
                    boolean didOne = false;
                    for(int c=0; c<size; c++) {
                        if(newColumns.get(c)==null) {
                            if(didOne) message.append(", ");
                            else didOne = true;
                            message.append(c);
                        }
                    }
                    throw new AssertionError(message.toString());
                }
                // Ensures that at most one primary key exists.
                if(primaryKeys.size()>1) {
                    StringBuilder message = new StringBuilder("More than one primary key found: ");
                    message.append(clazz.getName()).append("->");
                    boolean didOne = false;
                    for(String cname : primaryKeys) {
                        if(didOne) message.append(", ");
                        else didOne = true;
                        message.append(cname);
                    }
                    throw new AssertionError(message.toString());
                }
                // Ensures that no unique index exists without a primary key defined.
                if(!uniques.isEmpty() && primaryKeys.isEmpty()) {
                    StringBuilder message = new StringBuilder(uniques.size()==1 ? "Unique column exists without primary key: ":"Unique columns exist without primary key: ");
                    message.append(clazz.getName()).append("->");
                    boolean didOne = false;
                    for(String cname : uniques) {
                        if(didOne) message.append(", ");
                        else didOne = true;
                        message.append(cname);
                    }
                    throw new AssertionError(message.toString());
                }
                // Make unmodifiable
                if(size==0) throw new AssertionError("No columns found");
                newColumns.trimToSize();
                List<MethodColumn> unmod = AoCollections.optimalUnmodifiableList(newColumns);
                // Put in cache
                List<MethodColumn> existingColumns = columns.putIfAbsent(clazz, unmod);
                methodColumns = existingColumns==null ? unmod : existingColumns;
            } catch(IntrospectionException exc) {
                throw new AssertionError(exc);
            }
        }
        return methodColumns;
    }

    private static final ConcurrentMap<Class<? extends AOServObject>,Map<String,MethodColumn>> columnMaps = new ConcurrentHashMap<Class<? extends AOServObject>, Map<String,MethodColumn>>(ServiceName.values.size()*4/3+1);

    /**
     * Provides map from getMethodColumns.
     */
    public static Map<String,MethodColumn> getMethodColumnMap(Class<? extends AOServObject> clazz) {
        Map<String,MethodColumn> map = columnMaps.get(clazz);
        if(map==null) {
            List<MethodColumn> list = getMethodColumns(clazz);
            Map<String,MethodColumn> newMap = new LinkedHashMap<String,MethodColumn>(list.size()*4/3+1);
            for(MethodColumn mc : list) newMap.put(mc.getName(), mc);
            newMap = AoCollections.optimalUnmodifiableMap(newMap);
            // Put in cache
            Map<String,MethodColumn> existingColumnMap = columnMaps.putIfAbsent(clazz, newMap);
            map = existingColumnMap==null ? newMap : existingColumnMap;
        }
        return map;
    }

    /**
     * Gets the method column for the provided class and name.
     */
    protected static MethodColumn getMethodColumn(Class<? extends AOServObject> clazz, String columnName) {
        MethodColumn column = getMethodColumnMap(clazz).get(columnName);
        if(column==null) throw new AssertionError("Column not found: "+clazz.getName()+"."+columnName);
        return column;
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
    protected static Hostname getHostname(com.aoindustries.aoserv.client.dto.Hostname hostname) throws ValidationException {
        if(hostname==null) return null;
        return Hostname.valueOf(hostname.getHostname());
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
    protected static LinuxID getLinuxID(com.aoindustries.aoserv.client.dto.LinuxID lid) throws ValidationException {
        if(lid==null) return null;
        return LinuxID.valueOf(lid.getId());
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
    protected static NetPort getNetPort(com.aoindustries.aoserv.client.dto.NetPort netPort) throws ValidationException {
        if(netPort==null) return null;
        return NetPort.valueOf(netPort.getPort());
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

    // <editor-fold defaultstate="collapsed" desc="i18n">
    /**
     * Gets a string representation of this object.
     *
     * @see  #toStringImpl()
     */
    @Override
    final public String toString() {
        try {
            return toStringImpl();
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * The default string representation is that of the key value.
     */
    String toStringImpl() throws RemoteException {
        return getKey().toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Connector">
    /**
     * Gets the connector that this object belongs to.
     */
    final public AOServConnector getConnector() {
        return connector;
    }

    /**
     * Returns a (possibly new) instance of this object set to a different connector.
     * <p>
     * The <code>connector</code> field is marked <code>transient</code>, and thus
     * deserialized objects will initially have a <code>null</code> connector
     * reference.  The code that deserializes the objects should call this
     * setConnector method on all objects received.
     * </p>
     * <p>
     * Also, caching layers should call setConnector on all objects in order to make
     * subsequent method invocations use the caches.  This will cause additional
     * copying within the cache layers, but the reduction of round-trips to the
     * server should payoff.
     * </p>
     *
     * @return  if the connector field is currently <code>null</code>, sets the field and
     *          returns this object.  Next, if the connector is equal to the provided connector
     *          returns this object.  Next, if the current connector returns <code>true</code> for
     *          <code>isAoServObjectConnectorSettable</code>, updates and returns this object.
     *          Otherwise, returns a clone with the connector field updated.
     */
    final public AOServObject<K> setConnector(AOServConnector connector) throws RemoteException {
        if(this.connector==null) {
            this.connector = connector;
            return this;
        } else if(this.connector==connector) {
            return this;
        } else if(this.connector.isAoServObjectConnectorSettable()) {
            this.connector = connector;
            return this;
        } else {
            AOServObject<K> newObj = clone();
            newObj.connector = connector;
            return newObj;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Key, equals, and hashCode">
    /**
     * Every object's equality is based on being of the same class and having the same key value.
     * This default implementation checks for class compatibility and calls equals(T).
     */
    @Override
    public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        @SuppressWarnings("unchecked")
        AOServObject<K> other = (AOServObject<K>)o;
        return getKey().equals(other.getKey());
    }

    /**
     * Gets the key value for this object.
     */
    public abstract K getKey();

    /**
     * The default hashcode value is the hash code of the key value.
     */
    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies and Dependent Objects">
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected static @interface DependencySet {
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected static @interface DependencySingleton {
    }

    private static final ConcurrentMap<
        Class<? extends AOServObject<?>>,
        Map<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>>
    > getDependenciesMethodsCache = new ConcurrentHashMap<
        Class<? extends AOServObject<?>>,
        Map<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>>
    >();

    /**
     * Gets the set of methods that return objects that are dependent on this object.
     * Methods are annotated for this purpose.
     */
    @SuppressWarnings("unchecked")
    protected static Map<Class<? extends AOServObject<?>>, ? extends List<UnionMethodSet.Method<AOServObject<?>>>> getDependenciesMethods(Class<? extends AOServObject<?>> clazz) {
        Map<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>> getDependenciesMethods = getDependenciesMethodsCache.get(clazz);
        if(getDependenciesMethods==null) {
            getDependenciesMethods = new LinkedHashMap<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>>();
            for(Method method : clazz.getMethods()) {
                int modifiers = method.getModifiers();
                if(
                    Modifier.isPublic(modifiers)
                    && !Modifier.isStatic(modifiers)
                ) {
                    boolean isDependencySet = method.isAnnotationPresent(DependencySet.class);
                    boolean isDependencySingleton = method.isAnnotationPresent(DependencySingleton.class);
                    if(isDependencySet && isDependencySingleton) throw new RuntimeException("Method may not be both @DependencySet and @DependencySingleton: "+clazz.getName()+'.'+method.getName());
                    if((isDependencySet || isDependencySingleton) && method.getParameterTypes().length>0) throw new RuntimeException("Method may not take any parameters: "+clazz.getName()+'.'+method.getName());
                    UnionMethodSet.Method<AOServObject<?>> unionMethod;
                    Class<? extends AOServObject<?>> returnType;
                    if(isDependencySet) {
                        // Make sure is a Set
                        Class<?> setType = method.getReturnType();
                        if(!Set.class.isAssignableFrom(setType)) throw new RuntimeException("@DependencySet method must return a Set: "+clazz.getName()+'.'+method.getName());
                        // Get the return type from the generic type parameters
                        Type[] genericTypes = ((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments();
                        if(genericTypes.length==0) throw new RuntimeException("Generic type not found for @DependencySet method: "+clazz.getName()+'.'+method.getName());
                        if(genericTypes.length>1) throw new RuntimeException("Only one generic type allowed for @DependencySet method: "+clazz.getName()+'.'+method.getName());
                        unionMethod = new UnionMethodSet.SetMethod<AOServObject<?>>(method);
                        @SuppressWarnings("unchecked")
                        Class<? extends AOServObject<?>> returnTypeFix = (Class<? extends AOServObject<?>>)((Class<?>)genericTypes[0]).asSubclass(AOServObject.class);
                        returnType = returnTypeFix;
                    } else if(isDependencySingleton) {
                        if(Set.class.isAssignableFrom(method.getReturnType())) throw new RuntimeException("@DependencySingleton method may not return a Set: "+clazz.getName()+'.'+method.getName());
                        unionMethod = new UnionMethodSet.SingletonMethod<AOServObject<?>>(method);
                        @SuppressWarnings("unchecked")
                        Class<? extends AOServObject<?>> returnTypeFix = (Class<? extends AOServObject<?>>)method.getReturnType().asSubclass(AOServObject.class);
                        returnType = returnTypeFix;
                    } else {
                        unionMethod = null;
                        returnType = null;
                    }
                    if(unionMethod!=null) {
                        List<UnionMethodSet.Method<AOServObject<?>>> methods = getDependenciesMethods.get(returnType);
                        if(methods==null) {
                            // Add first as singletonList
                            List<UnionMethodSet.Method<AOServObject<?>>> test = Collections.singletonList(unionMethod);
                            getDependenciesMethods.put(returnType, test);
                        } else {
                            // Convert to arraylist for second
                            List<UnionMethodSet.Method<AOServObject<?>>> test = new ArrayList<UnionMethodSet.Method<AOServObject<?>>>(methods.size()+1);
                            test.addAll(methods);
                            test.add(unionMethod);
                            getDependenciesMethods.put(returnType, test);
                        }
                    }
                }
            }
            // Switch to empty or singleton if possible
            if(getDependenciesMethods.isEmpty()) getDependenciesMethods = Collections.emptyMap();
            else if(getDependenciesMethods.size()==1) {
                Map.Entry<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>> entry = getDependenciesMethods.entrySet().iterator().next();
                getDependenciesMethods = (Map)Collections.singletonMap(entry.getKey(), entry.getValue());
            }
            Map<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>> existing = getDependenciesMethodsCache.putIfAbsent(clazz, getDependenciesMethods);
            if(existing!=null) getDependenciesMethods = existing;
        }
        return getDependenciesMethods;
    }

    /**
     * Gets an unmodifiable set of objects this object directly depends on.
     * This should result in a directed acyclic graph - there should never be any loops in the graph.
     * This acyclic graph, however, should be an exact mirror of the acyclic graph obtained from <code>getDependentObjects</code>.
     * By default, there are no dependencies.
     *
     * This default implementation adds all methods annotated as DependentObjectSet or DependentObjectSingleton.
     *
     * @see #getDependentObjects() for the opposite direction
     * @see DependencySet
     * @see DependencySingleton
     */
    public Set<? extends AOServObject<?>> getDependencies() throws RemoteException {
        @SuppressWarnings("unchecked")
        Class<AOServObject<?>> thisClassFixed = (Class)getClass();
        Map<Class<? extends AOServObject<?>>, ? extends List<UnionMethodSet.Method<AOServObject<?>>>> getDependenciesMethods = getDependenciesMethods(thisClassFixed);
        @SuppressWarnings("unchecked")
        Class<AOServObject<?>> returnType = (Class)AOServObject.class;
        if(getDependenciesMethods.isEmpty()) return Collections.emptySet();
        return new UnionMethodSet<AOServObject<?>>(this, returnType, getDependenciesMethods);
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected static @interface DependentObjectSet {
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected static @interface DependentObjectSingleton {
    }

    private static final ConcurrentMap<
        Class<? extends AOServObject<?>>,
        Map<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>>
    > getDependentObjectsMethodsCache = new ConcurrentHashMap<
        Class<? extends AOServObject<?>>,
        Map<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>>
    >();

    /**
     * Gets the set of methods that return objects that are dependent on this object.
     * Methods are annotated for this purpose.
     */
    @SuppressWarnings("unchecked")
    protected static Map<Class<? extends AOServObject<?>>, ? extends List<UnionMethodSet.Method<AOServObject<?>>>> getDependentObjectsMethods(Class<? extends AOServObject<?>> clazz) {
        Map<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>> getDependentObjectsMethods = getDependentObjectsMethodsCache.get(clazz);
        if(getDependentObjectsMethods==null) {
            getDependentObjectsMethods = new LinkedHashMap<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>>();
            for(Method method : clazz.getMethods()) {
                int modifiers = method.getModifiers();
                if(
                    Modifier.isPublic(modifiers)
                    && !Modifier.isStatic(modifiers)
                ) {
                    boolean isDependentObjectSet = method.isAnnotationPresent(DependentObjectSet.class);
                    boolean isDependentObjectSingleton = method.isAnnotationPresent(DependentObjectSingleton.class);
                    if(isDependentObjectSet && isDependentObjectSingleton) throw new RuntimeException("Method may not be both @DependentObjectSet and @DependentObjectSingleton: "+clazz.getName()+'.'+method.getName());
                    if((isDependentObjectSet || isDependentObjectSingleton) && method.getParameterTypes().length>0) throw new RuntimeException("Method may not take any parameters: "+clazz.getName()+'.'+method.getName());
                    UnionMethodSet.Method<AOServObject<?>> unionMethod;
                    Class<? extends AOServObject<?>> returnType;
                    if(isDependentObjectSet) {
                        // Make sure is a Set
                        Class<?> setType = method.getReturnType();
                        if(!Set.class.isAssignableFrom(setType)) throw new RuntimeException("@DependentObjectSet method must return a Set: "+clazz.getName()+'.'+method.getName());
                        // Get the return type from the generic type parameters
                        Type[] genericTypes = ((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments();
                        if(genericTypes.length==0) throw new RuntimeException("Generic type not found for @DependentObjectSet method: "+clazz.getName()+'.'+method.getName());
                        if(genericTypes.length>1) throw new RuntimeException("Only one generic type allowed for @DependentObjectSet method: "+clazz.getName()+'.'+method.getName());
                        unionMethod = new UnionMethodSet.SetMethod<AOServObject<?>>(method);
                        @SuppressWarnings("unchecked")
                        Class<? extends AOServObject<?>> returnTypeFix = (Class<? extends AOServObject<?>>)((Class<?>)genericTypes[0]).asSubclass(AOServObject.class);
                        returnType = returnTypeFix;
                    } else if(isDependentObjectSingleton) {
                        if(Set.class.isAssignableFrom(method.getReturnType())) throw new RuntimeException("@DependentObjectSingleton method may not return a Set: "+clazz.getName()+'.'+method.getName());
                        unionMethod = new UnionMethodSet.SingletonMethod<AOServObject<?>>(method);
                        @SuppressWarnings("unchecked")
                        Class<? extends AOServObject<?>> returnTypeFix = (Class<? extends AOServObject<?>>)method.getReturnType().asSubclass(AOServObject.class);
                        returnType = returnTypeFix;
                    } else {
                        unionMethod = null;
                        returnType = null;
                    }
                    if(unionMethod!=null) {
                        List<UnionMethodSet.Method<AOServObject<?>>> methods = getDependentObjectsMethods.get(returnType);
                        if(methods==null) {
                            // Add first as singletonList
                            List<UnionMethodSet.Method<AOServObject<?>>> test = Collections.singletonList(unionMethod);
                            getDependentObjectsMethods.put(returnType, test);
                        } else {
                            // Convert to arraylist for second
                            List<UnionMethodSet.Method<AOServObject<?>>> test = new ArrayList<UnionMethodSet.Method<AOServObject<?>>>(methods.size()+1);
                            test.addAll(methods);
                            test.add(unionMethod);
                            getDependentObjectsMethods.put(returnType, test);
                        }
                    }
                }
            }
            // Switch to empty or singleton if possible
            if(getDependentObjectsMethods.isEmpty()) getDependentObjectsMethods = Collections.emptyMap();
            else if(getDependentObjectsMethods.size()==1) {
                Map.Entry<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>> entry = getDependentObjectsMethods.entrySet().iterator().next();
                getDependentObjectsMethods = (Map)Collections.singletonMap(entry.getKey(), entry.getValue());
            }
            Map<Class<? extends AOServObject<?>>, List<UnionMethodSet.Method<AOServObject<?>>>> existing = getDependentObjectsMethodsCache.putIfAbsent(clazz, getDependentObjectsMethods);
            if(existing!=null) getDependentObjectsMethods = existing;
        }
        return getDependentObjectsMethods;
    }

    /**
     * Gets the set of objects directly dependent upon this object.
     * This should result in a directed acyclic graph - there should never be any loops in the graph.
     * This acyclic graph, however, should be an exact mirror of the acyclic graph obtained from <code>getDependencies</code>.
     *
     * This default implementation adds all methods annotated as DependentObjectSet or DependentObjectSingleton.
     *
     * @see #getDependencies() for the opposite direction
     * @see DependentObjectSet
     * @see DependentObjectSingleton
     */
    public Set<? extends AOServObject<?>> getDependentObjects() throws RemoteException {
        @SuppressWarnings("unchecked")
        Class<AOServObject<?>> thisClassFixed = (Class)getClass();
        Map<Class<? extends AOServObject<?>>, ? extends List<UnionMethodSet.Method<AOServObject<?>>>> getDependentObjectsMethods = getDependentObjectsMethods(thisClassFixed);
        @SuppressWarnings("unchecked")
        Class<AOServObject<?>> returnType = (Class)AOServObject.class;
        if(getDependentObjectsMethods.isEmpty()) return Collections.emptySet();
        return new UnionMethodSet<AOServObject<?>>(this, returnType, getDependentObjectsMethods);
    }
    // </editor-fold>
}
