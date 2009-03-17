package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.Streamable;
import com.aoindustries.table.Row;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An <code>AOServObject</code> is the lowest level object
 * for all data in the system.  Each <code>AOServObject</code>
 * belongs to a <code>AOServTable</code>, and each table
 * contains <code>AOServObject</code>s.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServTable
 */
abstract public class AOServObject<K,T extends AOServObject<K,T>> implements Row, Streamable {

    protected AOServObject() {
    }

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

    @Override
    final public boolean equals(Object O) {
        return O==null?false:equalsImpl(O);
    }

    boolean equalsImpl(Object O) {
        Class class1=getClass();
        Class class2=O.getClass();
        if(class1==class2) {
            K pkey1=getKey();
            Object pkey2=((AOServObject)O).getKey();
            if(pkey1==null || pkey2==null) throw new NullPointerException("No primary key available.");
            return pkey1.equals(pkey2);
        }
        return false;
    }

    final public Object getColumn(int i) {
        try {
            return getColumnImpl(i);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    abstract Object getColumnImpl(int i) throws IOException, SQLException;

    final public List<Object> getColumns(AOServConnector connector) throws IOException, SQLException {
        int len=getTableSchema(connector).getSchemaColumns(connector).size();
        List<Object> buff=new ArrayList<Object>(len);
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
        return connector.schemaTables.get(getTableID());
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

    public abstract void read(CompressedDataInputStream in) throws IOException;

    @Override
    final public String toString() {
        try {
            return toStringImpl();
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

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
    final public void write(CompressedDataOutputStream out, String version) throws IOException {
        write(out, AOServProtocol.Version.getVersion(version));
    }

    public abstract void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException;
}