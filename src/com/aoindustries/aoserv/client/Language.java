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
import java.util.Locale;

/**
 * @author  AO Industries, Inc.
 */
final public class Language extends GlobalObjectStringKey<Language> {

    static final int COLUMN_CODE = 0;
    static final String COLUMN_CODE_name = "code";

    public static final String
        EN="en",
        JA="ja"
    ;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_CODE: return pkey;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResourcesAccessor.getMessage(userLocale, "Language."+pkey+".toString");
    }

    public String getCode() {
        return pkey;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LANGUAGES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readUTF().intern();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
    }
}