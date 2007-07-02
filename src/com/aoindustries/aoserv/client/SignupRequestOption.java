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
 * Stores an option for a sign-up request, each option has a unique name per sign-up request.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SignupRequestOption extends CachedObjectIntegerKey<SignupRequestOption> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_REQUEST=1
    ;

    int request;
    private String name;
    private String value;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_REQUEST: return request;
            case 2: return name;
            case 3: return value;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SIGNUP_REQUEST_OPTIONS;
    }

    void initImpl(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
        request = result.getInt(pos++);
        name = result.getString(pos++);
        value = result.getString(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        request=in.readCompressedInt();
        name = in.readUTF().intern();
        value = in.readNullUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(request);
        out.writeUTF(name);
        out.writeNullUTF(value);
    }

    public SignupRequest getSignupRequest() {
	SignupRequest sr = table.connector.signupRequests.get(request);
	if (sr == null) throw new WrappedException(new SQLException("Unable to find SignupRequest: " + request));
	return sr;
    }

    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
}
