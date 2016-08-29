/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>LinuxServerGroup</code> adds a <code>LinuxGroup</code>
 * to an <code>AOServer</code>, so that <code>LinuxServerAccount</code> with
 * access to the group may use the group on the server.
 *
 * @see  LinuxGroup
 * @see  LinuxServerAccount
 * @see  AOServer
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxServerGroup extends CachedObjectIntegerKey<LinuxServerGroup> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_NAME=1,
		COLUMN_AO_SERVER=2
	;
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_AO_SERVER_name = "ao_server";

	String name;
	int ao_server;
	int gid;
	private long created;

	public List<LinuxServerAccount> getAlternateLinuxServerAccounts() throws SQLException, IOException {
		return table.connector.getLinuxServerAccounts().getAlternateLinuxServerAccounts(this);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_NAME: return name;
			case COLUMN_AO_SERVER: return ao_server;
			case 3: return gid;
			case 4: return getCreated();
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public LinuxID getGid() throws SQLException {
		LinuxID obj=table.connector.getLinuxIDs().get(gid);
		if(obj==null) throw new SQLException("Unable to find LinuxID: "+gid);
		return obj;
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
	}

	public LinuxGroup getLinuxGroup() throws SQLException, IOException {
		LinuxGroup group = table.connector.getLinuxGroups().get(name);
		if (group == null) throw new SQLException("Unable to find LinuxGroup: " + name);
		return group;
	}

	public AOServer getAOServer() throws SQLException, IOException {
		AOServer ao=table.connector.getAoServers().get(ao_server);
		if(ao==null) throw new SQLException("Unable to find AOServer: "+ao_server);
		return ao;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_SERVER_GROUPS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		name = result.getString(2);
		ao_server = result.getInt(3);
		gid = result.getInt(4);
		created = result.getTimestamp(5).getTime();
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		name=in.readUTF().intern();
		ao_server=in.readCompressedInt();
		gid=in.readCompressedInt();
		created=in.readLong();
	}

	@Override
	public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason> reasons=new ArrayList<>();

		AOServer ao=getAOServer();

		for(CvsRepository cr : ao.getCvsRepositories()) {
			if(cr.linux_server_group==pkey) reasons.add(new CannotRemoveReason<>("Used by CVS repository "+cr.getPath()+" on "+cr.getLinuxServerGroup().getAOServer().getHostname(), cr));
		}

		for(EmailList el : table.connector.getEmailLists().getRows()) {
			if(el.linux_server_group==pkey) reasons.add(new CannotRemoveReason<>("Used by email list "+el.getPath()+" on "+el.getLinuxServerGroup().getAOServer().getHostname(), el));
		}

		for(HttpdServer hs : ao.getHttpdServers()) {
			if(hs.linux_server_group==pkey) reasons.add(new CannotRemoveReason<>("Used by Apache server #"+hs.getNumber()+" on "+hs.getAOServer().getHostname(), hs));
		}

		for(HttpdSharedTomcat hst : ao.getHttpdSharedTomcats()) {
			if(hst.linux_server_group==pkey) reasons.add(new CannotRemoveReason<>("Used by Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getHostname(), hst));
		}

		// httpd_sites
		for(HttpdSite site : ao.getHttpdSites()) {
			if(site.linuxGroup.equals(name)) reasons.add(new CannotRemoveReason<>("Used by website "+site.getInstallDirectory()+" on "+site.getAOServer().getHostname(), site));
		}

		for(MajordomoServer ms : ao.getMajordomoServers()) {
			if(ms.linux_server_group==pkey) {
				EmailDomain ed=ms.getDomain();
				reasons.add(new CannotRemoveReason<>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ms));
			}
		}

		/*for(PrivateFTPServer pfs : ao.getPrivateFTPServers()) {
			if(pfs.pub_linux_server_group==pkey) reasons.add(new CannotRemoveReason<PrivateFTPServer>("Used by private FTP server "+pfs.getRoot()+" on "+pfs.getLinuxServerGroup().getAOServer().getHostname(), pfs));
		}*/

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.LINUX_SERVER_GROUPS,
			pkey
		);
	}

	@Override
	String toStringImpl() {
		return name;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name);
		out.writeCompressedInt(ao_server);
		out.writeCompressedInt(gid);
		out.writeLong(created);
	}
}
