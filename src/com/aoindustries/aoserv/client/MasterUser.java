package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;

/**
 * A <code>MasterUser</code> is a <code>BusinessAdministrator</code> who
 * has greater permissions.  Their access is secure on a per-<code>Server</code>
 * basis, and may also include full access to DNS, tickets, backups, and other
 * systems.
 *
 * @see  BusinessAdministrator
 * @see  MasterHost
 * @see  MasterServer
 *
 * @version  1.0a
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
        is_ticket_admin,
        is_dns_admin
    ;

    public boolean canAccessAccounting() {
	return can_access_accounting;
    }

    public boolean canAccessBankAccount() {
	return can_access_bank_account;
    }

    public boolean canInvalidateTables() {
	return can_invalidate_tables;
    }

    public BusinessAdministrator getBusinessAdministrator() {
	BusinessAdministrator obj=table.connector.businessAdministrators.get(pkey);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find BusinessAdministrator: "+pkey));
	return obj;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_USERNAME: return pkey;
            case 1: return is_active?Boolean.TRUE:Boolean.FALSE;
            case 2: return can_access_accounting?Boolean.TRUE:Boolean.FALSE;
            case 3: return can_access_bank_account?Boolean.TRUE:Boolean.FALSE;
            case 4: return can_invalidate_tables?Boolean.TRUE:Boolean.FALSE;
            case 5: return can_access_admin_web?Boolean.TRUE:Boolean.FALSE;
            case 6: return is_ticket_admin?Boolean.TRUE:Boolean.FALSE;
            case 7: return is_dns_admin?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_USERS;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getString(1);
	is_active=result.getBoolean(2);
	can_access_accounting=result.getBoolean(3);
	can_access_bank_account=result.getBoolean(4);
	can_invalidate_tables=result.getBoolean(5);
	can_access_admin_web=result.getBoolean(6);
	is_ticket_admin=result.getBoolean(7);
	is_dns_admin=result.getBoolean(8);
    }

    public boolean isActive() {
	return is_active;
    }

    public boolean isDNSAdmin() {
	return is_dns_admin;
    }

    public boolean isTicketAdmin() {
	return is_ticket_admin;
    }

    public boolean isWebAdmin() {
	return can_access_admin_web;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	is_active=in.readBoolean();
	can_access_accounting=in.readBoolean();
	can_access_bank_account=in.readBoolean();
	can_invalidate_tables=in.readBoolean();
	can_access_admin_web=in.readBoolean();
	is_ticket_admin=in.readBoolean();
	is_dns_admin=in.readBoolean();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
	out.writeBoolean(is_active);
	out.writeBoolean(can_access_accounting);
	out.writeBoolean(can_access_bank_account);
	out.writeBoolean(can_invalidate_tables);
	out.writeBoolean(can_access_admin_web);
	out.writeBoolean(is_ticket_admin);
	out.writeBoolean(is_dns_admin);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_118)<0) out.writeBoolean(false);
    }
}