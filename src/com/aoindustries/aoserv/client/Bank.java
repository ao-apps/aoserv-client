package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Bank extends CachedObjectStringKey<Bank> {

    static final int COLUMN_NAME=0;
    static final String COLUMN_NAME_name = "name";

    private String
        display
    ;

    Object getColumnImpl(int i) {
        if(i==COLUMN_NAME) return pkey;
        if(i==1) return display;
        throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDisplay() {
    	return display;
    }

    public String getName() {
    	return pkey;
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.BANKS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        display = result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        display=in.readUTF();
    }

    @Override
    String toStringImpl(Locale userLocale) {
    	return display;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(display);
    }

    public List<AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public List<AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }
}