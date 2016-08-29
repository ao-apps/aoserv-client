package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Language
 *
 * @author  AO Industries, Inc.
 */
final public class LanguageTable extends GlobalTableStringKey<Language> {

    LanguageTable(AOServConnector connector) {
        super(connector, Language.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Language.COLUMN_CODE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public Language get(String code) throws IOException, SQLException {
        return getUniqueRow(Language.COLUMN_CODE, code);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LANGUAGES;
    }
}