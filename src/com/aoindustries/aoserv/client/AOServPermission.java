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
    public static final String
        // business_administrators
        SET_BUSINESS_ADMINISTRATOR_PASSWORD="set_business_administrator_password",
        // interbase_server_users
        SET_INTERBASE_SERVER_USER_PASSWORD="set_interbase_server_user_password",
        // linux_server_accounts
        SET_LINUX_SERVER_ACCOUNT_PASSWORD="set_linux_server_account_password",
        // mysql_server_users
        SET_MYSQL_SERVER_USER_PASSWORD="set_mysql_server_user_password",
        // mysql_servers
        GET_MYSQL_SLAVE_STATUS="get_mysql_slave_status",
        // postgres_server_users
        SET_POSTGRES_SERVER_USER_PASSWORD="set_postgres_server_user_password"
    ;

    // From database
    private short sort_order;

    // Used internally
    private String displayKey;
    private String descriptionKey;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return sort_order;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    /**
     * Gets the key into aoserv-client ApplicationResources.properties language bundle for the short display value for this permission.
     */
    public String getDisplayKey() {
        return displayKey;
    }

    /**
     * Gets the key into aoserv-client ApplicationResources.properties language bundle for the description of this permission.
     */
    public String getDescriptionKey() {
        return descriptionKey;
    }

    protected int getTableIDImpl() {
        return SchemaTable.AOSERV_PERMISSIONS;
    }

    public String getName() {
        return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        sort_order = result.getShort(2);
        displayKey = "AOServPermission."+pkey+".display";
        descriptionKey = "AOServPermission."+pkey+".description";
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF();
        sort_order = in.readShort();
        displayKey = "AOServPermission."+pkey+".display";
        descriptionKey = "AOServPermission."+pkey+".description";
    }

    String toStringImpl() {
        return pkey;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeUTF(pkey);
        out.writeShort(sort_order);
    }
}