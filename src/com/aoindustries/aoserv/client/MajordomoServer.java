package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * A <code>MajordomoServer</code> provides Majordomo functionality for
 * a <code>EmailDomain</code>.  Once the <code>MajordomoServer</code>
 * is established, any number of <code>MajordomoList</code>s may be
 * added to it.
 *
 * @see  EmailDomain
 * @see  MajordomoList
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoServer extends CachedObjectIntegerKey<MajordomoServer> implements Removable {

    static final int COLUMN_DOMAIN=0;
    static final String COLUMN_DOMAIN_name = "domain";

    /**
     * The directory that stores the majordomo servers.
     */
    public static final String MAJORDOMO_SERVER_DIRECTORY="/etc/mail/majordomo";

    /**
     * The username part of the email address used to directly email majordomo.
     */
    public static final String MAJORDOMO_ADDRESS="majordomo";

    /**
     * The username part of the email address used to directly email the majordomo owner.
     */
    public static final String
        OWNER_MAJORDOMO_ADDRESS="owner-majordomo",
        MAJORDOMO_OWNER_ADDRESS="majordomo-owner"
    ;

    int linux_server_account;
    int linux_server_group;
    String version;
    int majordomo_pipe_address;
    int owner_majordomo_add;
    int majordomo_owner_add;

    public int addMajordomoList(
        String listName
    ) {
        return table.connector.majordomoLists.addMajordomoList(this, listName);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_DOMAIN: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(linux_server_account);
            case 2: return Integer.valueOf(linux_server_group);
            case 3: return version;
            case 4: return Integer.valueOf(majordomo_pipe_address);
            case 5: return Integer.valueOf(owner_majordomo_add);
            case 6: return Integer.valueOf(majordomo_owner_add);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public EmailDomain getDomain() {
	EmailDomain obj=table.connector.emailDomains.get(pkey);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find EmailDomain: "+pkey));
	return obj;
    }

    public LinuxServerAccount getLinuxServerAccount() {
	LinuxServerAccount obj=table.connector.linuxServerAccounts.get(linux_server_account);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find LinuxServerAccount: "+linux_server_account));
	return obj;
    }

    public LinuxServerGroup getLinuxServerGroup() {
	LinuxServerGroup obj=table.connector.linuxServerGroups.get(linux_server_group);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find LinuxServerGroup: "+linux_server_group));
	return obj;
    }

    public EmailPipeAddress getMajordomoPipeAddress() {
	EmailPipeAddress obj=table.connector.emailPipeAddresses.get(majordomo_pipe_address);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find EmailPipeAddress: "+majordomo_pipe_address));
	return obj;
    }

    public MajordomoList getMajordomoList(String listName) {
        return table.connector.majordomoLists.getMajordomoList(this, listName);
    }

    public List<MajordomoList> getMajordomoLists() {
	return table.connector.majordomoLists.getMajordomoLists(this);
    }

    public EmailAddress getMajordomoOwnerAddress() {
	EmailAddress obj=table.connector.emailAddresses.get(majordomo_owner_add);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find EmailAddress: "+majordomo_owner_add));
	return obj;
    }

    public EmailAddress getOwnerMajordomoAddress() {
	EmailAddress obj=table.connector.emailAddresses.get(owner_majordomo_add);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find EmailAddress: "+owner_majordomo_add));
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MAJORDOMO_SERVERS;
    }

    public MajordomoVersion getVersion() {
	MajordomoVersion obj=table.connector.majordomoVersions.get(version);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find MajordomoVersion: "+version));
	return obj;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	linux_server_account=result.getInt(2);
	linux_server_group=result.getInt(3);
	version=result.getString(4);
	majordomo_pipe_address=result.getInt(5);
	owner_majordomo_add=result.getInt(6);
	majordomo_owner_add=result.getInt(7);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	linux_server_account=in.readCompressedInt();
	linux_server_group=in.readCompressedInt();
	version=in.readUTF().intern();
	majordomo_pipe_address=in.readCompressedInt();
	owner_majordomo_add=in.readCompressedInt();
	majordomo_owner_add=in.readCompressedInt();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MAJORDOMO_SERVERS,
            pkey
	);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(linux_server_account);
	out.writeCompressedInt(linux_server_group);
	out.writeUTF(version);
	out.writeCompressedInt(majordomo_pipe_address);
	out.writeCompressedInt(owner_majordomo_add);
	out.writeCompressedInt(majordomo_owner_add);
        if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeShort(0);
            out.writeShort(7);
        }
    }
}