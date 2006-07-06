package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.table.*;
import java.io.*;
import java.sql.*;
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

    final public int compareTo(AOServConnector conn, AOServObject other, SQLExpression[] sortExpressions, boolean[] sortOrders) {
        Profiler.startProfile(Profiler.FAST, AOServObject.class, "compareTo(AOServConnector,AOServObject,SQLExpression[],boolean[])", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final public int compareTo(AOServConnector conn, Comparable value, SQLExpression[] sortExpressions, boolean[] sortOrders) {
        Profiler.startProfile(Profiler.FAST, AOServObject.class, "compareTo(AOServConnector,Comparable,SQLExpression[],boolean[])", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final public int compareTo(AOServConnector conn, Object[] OA, SQLExpression[] sortExpressions, boolean[] sortOrders) {
        Profiler.startProfile(Profiler.FAST, AOServObject.class, "compareTo(AOServConnector,Object[],SQLExpression[],boolean[])", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final public boolean equals(Object O) {
        return O==null?false:equalsImpl(O);
    }

    boolean equalsImpl(Object O) {
        Profiler.startProfile(Profiler.FAST, AOServObject.class, "equalsImpl(Object)", null);
        try {
            Class class1=getClass();
            Class class2=O.getClass();
            if(class1==class2) {
                K pkey1=getKey();
                Object pkey2=((AOServObject)O).getKey();
                if(pkey1==null || pkey2==null) throw new NullPointerException("No primary key available.");
                return pkey1.equals(pkey2);
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    abstract public Object getColumn(int i);

    final public List<Object> getColumns(AOServConnector connector) {
        int len=getTableSchema(connector).getSchemaColumns(connector).size();
        List<Object> buff=new ArrayList<Object>(len);
        for(int c=0;c<len;c++) buff.add(getColumn(c));
        return buff;
    }

    final public int getColumns(AOServConnector connector, List<Object> buff) {
        int len=getTableSchema(connector).getSchemaColumns(connector).size();
        for(int c=0;c<len;c++) buff.add(getColumn(c));
        return len;
    }

    public abstract K getKey();

    final public int getTableID() {
        return getTableIDImpl();
    }

    abstract protected int getTableIDImpl();

    final public SchemaTable getTableSchema(AOServConnector connector) {
        return connector.schemaTables.get(getTableID());
    }

    final public int hashCode() {
        return hashCodeImpl();
    }

    int hashCodeImpl() {
        Profiler.startProfile(Profiler.FAST, AOServObject.class, "hashCodeImpl()", null);
        try {
            K pkey=getKey();
            if(pkey==null) throw new NullPointerException("No primary key available.");
            return pkey.hashCode();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Initializes this object from the raw database contents.
     *
     * @param  results  the <code>ResultSet</code> containing the row
     *                  to copy into this object
     */
    final public void init(ResultSet results) throws SQLException {
        initImpl(results);
    }

    /**
     * Initializes this object from the raw database contents.  This method
     * is not public with the init(ResultSet) public accessor to keep the
     * documentation name space clean.
     *
     * @param  results  the <code>ResultSet</code> containing the row
     *                  to copy into this object
     */
    abstract void initImpl(ResultSet results) throws SQLException;


    public abstract void read(CompressedDataInputStream in) throws IOException;

    protected static String readNullUTF(DataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, AOServObject.class, "readNullUTF(DataInputStream)", null);
        try {
            return in.readBoolean()?in.readUTF():null;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    final public String toString() {
        return toStringImpl();
    }

    String toStringImpl() {
        Profiler.startProfile(Profiler.FAST, AOServObject.class, "toStringImpl()", null);
        try {
            K pkey=getKey();
            if(pkey==null) return super.toString();
            return pkey.toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public abstract void write(CompressedDataOutputStream out, String version) throws IOException;

    protected static void writeNullUTF(DataOutputStream out, String S) throws IOException {
        Profiler.startProfile(Profiler.IO, AOServObject.class, "writeNullUTF(DataOutputStream,String)", null);
        try {
            out.writeBoolean(S!=null);
            if(S!=null) out.writeUTF(S);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}