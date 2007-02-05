package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * An expression in used in select statements and internal sorting.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract public class SQLExpression {

    abstract public String getColumnName();

    abstract public Object getValue(AOServConnector conn, AOServObject obj);
    
    abstract public SchemaType getType();

    /**
     * Gets all of the tables referenced by this expression.
     */
    public void getReferencedTables(AOServConnector conn, List<SchemaTable> tables) {
    }
}
