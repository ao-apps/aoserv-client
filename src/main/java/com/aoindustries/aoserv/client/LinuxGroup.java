/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>LinuxGroup</code> may exist on multiple <code>Server</code>s.
 * The information common across all servers is stored is a <code>LinuxGroup</code>.
 *
 * @see  LinuxServerGroup
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroup extends CachedObjectGroupIdKey<LinuxGroup> implements Removable {

	static final int
		COLUMN_NAME=0,
		COLUMN_PACKAGE=1
	;
	static final String COLUMN_NAME_name = "name";

	/**
	 * Some commonly used system and application groups.
	 */
	public static final GroupId
		ADM,
		AOADMIN,
		APACHE,
		AUDIO,
		AVAHI_AUTOIPD,
		AWSTATS,
		BIN,
		CDROM,
		CGRED,
		CHRONY,
		DAEMON,
		DBUS,
		DHCPD,
		DIALOUT,
		DIP,
		DISK,
		FLOPPY,
		FTP,
		FTPONLY,
		GAMES,
		INPUT,
		KMEM,
		LOCK,
		LP,
		MAIL,
		MAILONLY,
		MAN,
		MEM,
		MYSQL,
		NAMED,
		NFSNOBODY,
		NOBODY,
		NOGROUP,
		POLKITD,
		POSTGRES,
		PROFTPD_JAILED,
		ROOT,
		RPC,
		RPCUSER,
		SCREEN,
		SSH_KEYS,
		SSHD,
		SYS,
		SYSTEMD_BUS_PROXY,
		SYSTEMD_JOURNAL,
		SYSTEMD_NETWORK,
		TAPE,
		TCPDUMP,
		TSS,
		TTY,
		UNBOUND,
		USERS,
		UTEMPTER,
		UTMP,
		VIDEO,
		WHEEL
	;

	/**
	 * @deprecated  Group httpd no longer used.
	 */
	@Deprecated
	public static final GroupId HTTPD;

	static {
		try {
			ADM = GroupId.valueOf("adm");
			AOADMIN = GroupId.valueOf("aoadmin");
			APACHE = GroupId.valueOf("apache");
			AUDIO = GroupId.valueOf("audio");
			AVAHI_AUTOIPD = GroupId.valueOf("avahi-autoipd");
			AWSTATS = GroupId.valueOf("awstats");
			BIN = GroupId.valueOf("bin");
			CDROM = GroupId.valueOf("cdrom");
			CGRED = GroupId.valueOf("cgred");
			CHRONY = GroupId.valueOf("chrony");
			DAEMON = GroupId.valueOf("daemon");
			DBUS = GroupId.valueOf("dbus");
			DHCPD = GroupId.valueOf("dhcpd");
			DIALOUT = GroupId.valueOf("dialout");
			DIP = GroupId.valueOf("dip");
			DISK = GroupId.valueOf("disk");
			FLOPPY = GroupId.valueOf("floppy");
			FTP = GroupId.valueOf("ftp");
			FTPONLY = GroupId.valueOf("ftponly");
			GAMES = GroupId.valueOf("games");
			INPUT = GroupId.valueOf("input");
			KMEM = GroupId.valueOf("kmem");
			LOCK = GroupId.valueOf("lock");
			LP = GroupId.valueOf("lp");
			MAIL = GroupId.valueOf("mail");
			MAILONLY = GroupId.valueOf("mailonly");
			MAN = GroupId.valueOf("man");
			MEM = GroupId.valueOf("mem");
			MYSQL = GroupId.valueOf("mysql");
			NAMED = GroupId.valueOf("named");
			NFSNOBODY = GroupId.valueOf("nfsnobody");
			NOBODY = GroupId.valueOf("nobody");
			NOGROUP = GroupId.valueOf("nogroup");
			POLKITD = GroupId.valueOf("polkitd");
			POSTGRES = GroupId.valueOf("postgres");
			PROFTPD_JAILED = GroupId.valueOf("proftpd_jailed");
			ROOT = GroupId.valueOf("root");
			RPC = GroupId.valueOf("rpc");
			RPCUSER = GroupId.valueOf("rpcuser");
			SCREEN = GroupId.valueOf("screen");
			SSH_KEYS = GroupId.valueOf("ssh_keys");
			SSHD = GroupId.valueOf("sshd");
			SYS = GroupId.valueOf("sys");
			SYSTEMD_BUS_PROXY = GroupId.valueOf("systemd-bus-proxy");
			SYSTEMD_JOURNAL = GroupId.valueOf("systemd-journal");
			SYSTEMD_NETWORK = GroupId.valueOf("systemd-network");
			TAPE = GroupId.valueOf("tape");
			TCPDUMP = GroupId.valueOf("tcpdump");
			TSS = GroupId.valueOf("tss");
			TTY = GroupId.valueOf("tty");
			UNBOUND = GroupId.valueOf("unbound");
			USERS = GroupId.valueOf("users");
			UTEMPTER = GroupId.valueOf("utempter");
			UTMP = GroupId.valueOf("utmp");
			VIDEO = GroupId.valueOf("video");
			WHEEL = GroupId.valueOf("wheel");
			// Unused ones
			HTTPD = GroupId.valueOf("httpd");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	/**
	 * The max values for automatic gid selection in groupadd.
	 *
	 * @see  AOServer#getGidMin()
	 */
	public static final int GID_MAX = 60000;

	AccountingCode packageName;
	private String type;

	public int addLinuxAccount(LinuxAccount account) throws IOException, SQLException {
		return table.connector.getLinuxGroupAccounts().addLinuxGroupAccount(this, account);
	}

	public int addLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
		return table.connector.getLinuxServerGroups().addLinuxServerGroup(this, aoServer);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_NAME: return pkey;
			case COLUMN_PACKAGE: return packageName;
			case 2: return type;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public LinuxGroupType getLinuxGroupType() throws SQLException, IOException {
		LinuxGroupType typeObject = table.connector.getLinuxGroupTypes().get(type);
		if (typeObject == null) throw new SQLException("Unable to find LinuxGroupType: " + type);
		return typeObject;
	}

	public LinuxServerGroup getLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
		return table.connector.getLinuxServerGroups().getLinuxServerGroup(aoServer, pkey);
	}

	public List<LinuxServerGroup> getLinuxServerGroups() throws IOException, SQLException {
		return table.connector.getLinuxServerGroups().getLinuxServerGroups(this);
	}

	public GroupId getName() {
		return pkey;
	}

	public Package getPackage() throws IOException, SQLException {
		// null OK because data may be filtered at this point, like the linux group 'mail'
		return table.connector.getPackages().get(packageName);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_GROUPS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = GroupId.valueOf(result.getString(1));
			packageName = AccountingCode.valueOf(result.getString(2));
			type = result.getString(3);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = GroupId.valueOf(in.readUTF()).intern();
			packageName = AccountingCode.valueOf(in.readUTF()).intern();
			type=in.readUTF().intern();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws IOException, SQLException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();

		// Cannot be the primary group for any linux accounts
		for(LinuxGroupAccount lga : table.connector.getLinuxGroupAccounts().getRows()) {
			if(lga.isPrimary() && equals(lga.getLinuxGroup())) {
				reasons.add(new CannotRemoveReason<>("Used as primary group for Linux account "+lga.getLinuxAccount().getUsername().getUsername(), lga));
			}
		}

		// All LinuxServerGroups must be removable
		for(LinuxServerGroup lsg : getLinuxServerGroups()) reasons.addAll(lsg.getCannotRemoveReasons());

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.LINUX_GROUPS,
			pkey
		);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey.toString());
		out.writeUTF(packageName.toString());
		out.writeUTF(type);
	}
}
