/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.Row;
import com.aoindustries.util.Internable;
import com.aoindustries.util.UnionClassSet;
import com.aoindustries.util.UnionMethodSet;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.i18n.Money;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
implements
    Row,
    Serializable,
    Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * Value used when data has been filtered.
     */
    public static final String FILTERED = "*";

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

    private volatile transient AOServConnector connector;

    protected AOServObject(AOServConnector connector) {
        this.connector = connector;
    }

    /**
     * All AOServObjects are cloneable.
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

    /**
     * Every object's equality is based on being of the same class and having the same key value.
     * This default implementation checks for class compatibility and calls equals(T).
     */
    @Override
    public boolean equals(Object o) {
        if(o==null || getClass()!=o.getClass()) return false;
        AOServObject other = (AOServObject)o;
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

    // <editor-fold defaultstate="collapsed" desc="Dependencies and Dependent Objects">

    /**
     * Gets an unmodifiable set of objects this object directly depends on.
     * This should result in a directed acyclic graph - there should never be any loops in the graph.
     * This acyclic graph, however, should be an exact mirror of the acyclic graph obtained from <code>getDependentObjects</code>.
     * By default, there are no dependencies.
     *
     * @see #getDependentObjects() for the opposite direction
     *
     * @see #getConnectedVertices()
     */
    public Set<? extends AOServObject<?>> getDependencies() throws RemoteException {
        UnionClassSet<AOServObject<?>> unionSet = addDependencies(null);
        if(unionSet==null || unionSet.isEmpty()) return Collections.emptySet();
        //else return AoCollections.optimalUnmodifiableSet(unionSet);
        else return unionSet;
    }

    protected UnionClassSet<AOServObject<?>> addDependencies(UnionClassSet<AOServObject<?>> unionSet) throws RemoteException {
        return unionSet;
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

    /**
     * Gets the set of methods that return objects that are dependent on this object.
     * Methods are annotated for this purpose.
     */
    protected static Map<Class<? extends AOServObject<?>>, ? extends List<? extends UnionMethodSet.Method<? extends AOServObject<?>>>> getDependentObjectsMethods(Class<? extends AOServObject<?>> clazz) {
        Map<Class<? extends AOServObject<?>>, List<? extends UnionMethodSet.Method<? extends AOServObject<?>>>> getDependentObjectsMethods
            = new LinkedHashMap<Class<? extends AOServObject<?>>, List<? extends UnionMethodSet.Method<? extends AOServObject<?>>>>();
        for(Method method : clazz.getMethods()) {
            int modifiers = method.getModifiers();
            if(
                Modifier.isPublic(modifiers)
                && !Modifier.isStatic(modifiers)
            ) {
                boolean isDependentObjectSet = method.isAnnotationPresent(DependentObjectSet.class);
                boolean isDependentObjectSingleton = method.isAnnotationPresent(DependentObjectSingleton.class);
                if(isDependentObjectSet && isDependentObjectSingleton) throw new RuntimeException("Method may not be both @DependentObjectSet and @DependentObjectSingleton: "+clazz.getName()+'.'+method.getName());
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
                    List<? extends UnionMethodSet.Method<? extends AOServObject<?>>> methods = getDependentObjectsMethods.get(returnType);
                    if(methods==null) {
                        // Add first as singletonList
                        List<UnionMethodSet.Method<AOServObject<?>>> test = Collections.singletonList(unionMethod);
                        getDependentObjectsMethods.put(returnType, test);
                    } else {
                        // Convert to arraylist for second
                        List<UnionMethodSet.Method<? extends AOServObject<?>>> test = new ArrayList<UnionMethodSet.Method<? extends AOServObject<?>>>(methods.size()+1);
                        test.addAll(methods);
                        test.add(unionMethod);
                        getDependentObjectsMethods.put(returnType, test);
                    }
                }
            }
        }
        return getDependentObjectsMethods;
    }

    /**
     * Gets the set of objects directly dependent upon this object.
     * This should result in a directed acyclic graph - there should never be any loops in the graph.
     * This acyclic graph, however, should be an exact mirror of the acyclic graph obtained from <code>getDependencies</code>.
     * By default, there are no dependent objects.
     *
     * @see #getDependencies() for the opposite direction
     *
     * @see #getBackConnectedVertices()
     */
    public Set<? extends AOServObject<?>> getDependentObjects() throws RemoteException {
        UnionClassSet<AOServObject<?>> unionSet = addDependentObjects(null);
        if(unionSet==null || unionSet.isEmpty()) return Collections.emptySet();
        //else return AoCollections.optimalUnmodifiableSet(unionSet);
        else return unionSet;
    }

    protected UnionClassSet<AOServObject<?>> addDependentObjects(UnionClassSet<AOServObject<?>> unionSet) throws RemoteException {
        return unionSet;
    }
    // </editor-fold>

    /**
     * Gets value of the column with the provided index, by using the SchemaColumn annotation.
     */
    @Override
    final public Object getColumn(int index) {
        try {
            return AOServObjectUtils.getMethodColumns(getClass()).get(index).getMethod().invoke(this);
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
            return AOServObjectUtils.getMethodColumnMap(getClass()).get(name).getMethod().invoke(this);
        } catch(IllegalAccessException err) {
            throw new WrappedException(err);
        } catch(InvocationTargetException err) {
            throw new WrappedException(err);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="DTO Conversions">
    // TODO: These getters may be removed once all moved to AOServObject
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
}
