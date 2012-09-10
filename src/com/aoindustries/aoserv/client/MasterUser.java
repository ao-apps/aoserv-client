/*
 * Copyright 2001-2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>MasterUser</code> is a <code>BusinessAdministrator</code> who
 * has greater permissions.  Their access is secure on a per-<code>Server</code>
 * basis, and may also include full access to DNS, backups, and other
 * systems.
 *
 * @see  BusinessAdministrator
 * @see  MasterHost
 * @see  MasterServer
 *
 * @author  AO Industries, Inc.
 */
final public class MasterUser extends CachedObjectStringKey<MasterUser> {

    static final int COLUMN_USERNAME=0;
    static final String COLUMN_USERNAME_name = "username";

    private boolean
        is_active,
        can_access_accounting,
        can_access_bank_account,
        can_invalidate_tables,
        can_access_admin_web,
        is_dns_admin,
        is_router
    ;

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_USERS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey                   = result.getString(1);
        is_active              = result.getBoolean(2);
        can_access_accounting  = result.getBoolean(3);
        can_access_bank_account= result.getBoolean(4);
        can_invalidate_tables  = result.getBoolean(5);
        can_access_admin_web   = result.getBoolean(6);
        is_dns_admin           = result.getBoolean(7);
        is_router              = result.getBoolean(8);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeBoolean(is_active);
        out.writeBoolean(can_access_accounting);
        out.writeBoolean(can_access_bank_account);
        out.writeBoolean(can_invalidate_tables);
        out.writeBoolean(can_access_admin_web);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_43)<=0)     out.writeBoolean(false); // is_ticket_admin
        out.writeBoolean(is_dns_admin);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_118)<0) out.writeBoolean(false);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_65)>=0)     out.writeBoolean(is_router);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey                    = in.readUTF().intern();
        is_active               = in.readBoolean();
        can_access_accounting   = in.readBoolean();
        can_access_bank_account = in.readBoolean();
        can_invalidate_tables   = in.readBoolean();
        can_access_admin_web    = in.readBoolean();
        is_dns_admin            = in.readBoolean();
        is_router               = in.readBoolean();
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_USERNAME: return pkey;
            case 1: return is_active;
            case 2: return can_access_accounting;
            case 3: return can_access_bank_account;
            case 4: return can_invalidate_tables;
            case 5: return can_access_admin_web;
            case 6: return is_dns_admin;
            case 7: return is_router;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public BusinessAdministrator getBusinessAdministrator() throws SQLException, IOException {
	BusinessAdministrator obj=table.connector.getBusinessAdministrators().get(pkey);
	if(obj==null) throw new SQLException("Unable to find BusinessAdministrator: "+pkey);
	return obj;
    }

    public boolean isActive() {
        return is_active;
    }

    public boolean canAccessAccounting() {
	return can_access_accounting;
    }

    public boolean canAccessBankAccount() {
	return can_access_bank_account;
    }

    public boolean canInvalidateTables() {
	return can_invalidate_tables;
    }

    public boolean isWebAdmin() {
        return can_access_admin_web;
    }

    public boolean isDNSAdmin() {
        return is_dns_admin;
    }

    public boolean isRouter() {
        return is_router;
    }
}