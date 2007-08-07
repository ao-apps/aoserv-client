package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * All of the permissions within the system.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
final public class AOServPermission extends GlobalObjectStringKey<AOServPermission> {

    static final int COLUMN_NAME=0;

    /**
     * The possible permissions.
     */
    public enum Permission {
        // business_administrators
        set_business_administrator_password,
        // credit_card_processors
        get_credit_card_processors,
        // credit_card_transactions
        add_credit_card_transaction,
        // credit_cards
        get_credit_cards,
        add_credit_card,
        delete_credit_card,
        edit_credit_card,
        // interbase_server_users
        set_interbase_server_user_password,
        // linux_server_accounts
        set_linux_server_account_password,
        // mysql_server_users
        set_mysql_server_user_password,
        // mysql_servers
        get_mysql_master_status,
        get_mysql_slave_status,
        // postgres_server_users
        set_postgres_server_user_password;
        
        /**
         * Gets the permission display value in the JVM-default locale.
         */
        public String toString() {
            return toString(Locale.getDefault());
        }
        
        /**
         * Gets the permission display value in the provided locale.
         */
        public String toString(Locale userLocale) {
            return ApplicationResourcesAccessor.getMessage(userLocale, "AOServPermission."+name()+".display");
        }
    }

    // From database
    private short sort_order;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return sort_order;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    /**
     * Gets the locale-specific short display value for this permission.
     */
    public String getDisplay(Locale userLocale) {
        return ApplicationResourcesAccessor.getMessage(userLocale, "AOServPermission."+pkey+".display");
    }

    /**
     * Gets the locale-specific description of this permission.
     */
    public String getDescription(Locale userLocale) {
        return ApplicationResourcesAccessor.getMessage(userLocale, "AOServPermission."+pkey+".description");
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.AOSERV_PERMISSIONS;
    }

    public String getName() {
        return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        sort_order = result.getShort(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        sort_order = in.readShort();
    }

    String toStringImpl() {
        return pkey;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeUTF(pkey);
        out.writeShort(sort_order);
    }
}