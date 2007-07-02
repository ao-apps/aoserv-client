package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * An <code>InterBaseDBGroup</code> represents one set of databases that, on the filesystem level,
 * are accessible by one <code>LinuxServerGroup</code>.
 *
 * @see  InterBaseDatabase
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseDBGroup extends CachedObjectIntegerKey<InterBaseDBGroup> implements Removable {

    static final int COLUMN_PKEY=0;

    /**
     * The path that DB groups are stored in.
     */
    public static final String DB_GROUP_PATH="/var/lib/interbase";

    /**
     * Names that are disallowed or db groups and not deleted by the system.
     */
    public static final String
        IBSERVER="ibserver",
        LOST_AND_FOUND="lost+found"
    ;

    String name;
    int linux_server_group;

    public int addInterBaseDatabase(String name, InterBaseServerUser datdba) {
        return table.connector.interBaseDatabases.addInterBaseDatabase(this, name, datdba);
    }

    public String generateInterBaseDatabaseName(
        String template_base,
        String template_added
    ) {
        return table.connector.interBaseDatabases.generateInterBaseDatabaseName(
            this,
            template_base,
            template_added
        );
    }

    public Object getColumn(int i) {
	if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
	if(i==1) return name;
	if(i==2) return Integer.valueOf(linux_server_group);
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getName() {
        return name;
    }

    public InterBaseDatabase getInterBaseDatabase(String name) {
        return table.connector.interBaseDatabases.getInterBaseDatabase(this, name);
    }

    public List<InterBaseDatabase> getInterBaseDatabases() {
        return table.connector.interBaseDatabases.getInterBaseDatabases(this);
    }

    public String getPath() {
        return DB_GROUP_PATH+'/'+name;
    }

    public LinuxServerGroup getLinuxServerGroup() {
        LinuxServerGroup lsg=table.connector.linuxServerGroups.get(linux_server_group);
        if(lsg==null) throw new WrappedException(new SQLException("Unable to find LinuxServerGroup: "+linux_server_group));
        return lsg;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.INTERBASE_DB_GROUPS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        name=result.getString(2);
        linux_server_group=result.getInt(3);
    }

    public boolean isInterBaseDatabaseNameAvailable(String name) {
        return table.connector.interBaseDatabases.isInterBaseDatabaseNameAvailable(this, name);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        name=in.readUTF();
        linux_server_group=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        for(InterBaseDatabase id : getInterBaseDatabases()) {
            reasons.add(new CannotRemoveReason<InterBaseDatabase>("Used by InterBase database "+id.getPath()+" on "+id.getInterBaseDBGroup().getLinuxServerGroup().getAOServer().getServer().getHostname(), id));
        }

        return reasons;
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.TableID.INTERBASE_DB_GROUPS,
            pkey
	);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(name);
        out.writeCompressedInt(linux_server_group);
    }
}