/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.util.sort.ComparisonSortAlgorithm;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  SchemaType
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

    /*
    @Override
    protected int getMaxConnectionsPerThread() {
        return 2;
    }*/

    /**
     * Supports both Integer (num) and String (type) keys.
     */
    @Override
    public SchemaType get(Object pkey) throws IOException, SQLException {
        if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
        else if(pkey instanceof String) return get((String)pkey);
        else throw new IllegalArgumentException("Must be an Integer or a String");
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

    public <K,T extends AOServObject<K,T>> void sort(
		ComparisonSortAlgorithm<? super T> sortAlgorithm,
		T[] list,
		SQLExpression[] sortExpressions,
		boolean[] sortOrders
	) {
        sortAlgorithm.sort(
            list,
            new SQLComparator<T>(
                connector,
                sortExpressions,
                sortOrders
            )
        );
    }

    public <K,T extends AOServObject<K,T>> void sort(
		ComparisonSortAlgorithm<? super T> sortAlgorithm,
		List<T> list,
		SQLExpression[] sortExpressions,
		boolean[] sortOrders
	) {
        sortAlgorithm.sort(
            list,
            new SQLComparator<T>(
                connector,
                sortExpressions,
                sortOrders
            )
        );
    }
}