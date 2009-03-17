package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.sort.AutoSort;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  SchemaType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaTypeTable extends GlobalTableIntegerKey<SchemaType> {

    SchemaTypeTable(AOServConnector connector) {
	super(connector, SchemaType.class);
    }

    @Override
    OrderBy[] getDefaultOrderBy() {
        return null;
    }

    @Override
    protected int getMaxConnectionsPerThread() {
        return 2;
    }

    public SchemaType get(Object pkey) {
        try {
            if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
            else if(pkey instanceof String) return get((String)pkey);
            else throw new IllegalArgumentException("Must be an Integer or a String");
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaType get(int num) throws IOException, SQLException {
        return getRows().get(num);
    }

    public SchemaType get(String type) throws IOException, SQLException {
        return getUniqueRow(SchemaType.COLUMN_TYPE, type);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.SCHEMA_TYPES;
    }

    public <T extends AOServObject> void sort(T[] list, SQLExpression[] sortExpressions, boolean[] sortOrders) {
        AutoSort.sortStatic(
            list,
            new SQLComparator<T>(
                connector,
                sortExpressions,
                sortOrders
            )
        );
    }

    public <T extends AOServObject> void sort(List<T> list, SQLExpression[] sortExpressions, boolean[] sortOrders) {
        AutoSort.sortStatic(
            list,
            new SQLComparator<T>(
                connector,
                sortExpressions,
                sortOrders
            )
        );
    }
}