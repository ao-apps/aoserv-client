package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Associates a permission with a business administrator.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessAdministratorPermission extends CachedObjectIntegerKey<BusinessAdministratorPermission> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_USERNAME=1
    ;

    String username;
    String permission;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_USERNAME: return username;
            case 2: return permission;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public BusinessAdministrator getBusinessAdministrator() {
        BusinessAdministrator ba = table.connector.businessAdministrators.get(username);
        if(ba==null) throw new WrappedException(new SQLException("Unable to find BusinessAdministrator: "+username));
        return ba;
    }
    
    public AOServPermission getAOServPermission() {
        AOServPermission ap = table.connector.aoservPermissions.get(permission);
        if(ap==null) throw new WrappedException(new SQLException("Unable to find AOServPermission: "+permission));
        return ap;
    }

    protected int getTableIDImpl() {
	return SchemaTable.BUSINESS_ADMINISTRATOR_PERMISSIONS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
        username=result.getString(2);
	permission=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
        username=in.readUTF();
        permission=in.readUTF();
    }
    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(username);
	out.writeUTF(permission);
    }
}
