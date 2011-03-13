package com.aoindustries.aoserv.client;

/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.Column;
import com.aoindustries.table.IndexType;
import java.lang.reflect.Method;

/**
 * @author  AO Industries, Inc.
 */
final public class MethodColumn extends Column {

    private final Method method;
    private final SchemaColumn schemaColumn;

    public MethodColumn(String columnName, IndexType indexType, Method method, SchemaColumn schemaColumn) {
        super(columnName, indexType);
        this.method = method;
        this.schemaColumn = schemaColumn;
    }

    public Method getMethod() {
        return method;
    }

    public SchemaColumn getSchemaColumn() {
        return schemaColumn;
    }
}
