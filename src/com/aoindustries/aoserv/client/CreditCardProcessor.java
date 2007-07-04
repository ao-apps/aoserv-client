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
 * A <code>CreditCardProcessor</code> represents on Merchant account used for credit card processing.
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardProcessor extends CachedObjectStringKey<CreditCardProcessor> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ACCOUNTING=1
    ;

    private String accounting;
    private String className;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private boolean enabled;
    private int weight;
    private String description;

    public Business getBusiness() {
        Business business = table.connector.businesses.get(accounting);
        if (business == null) throw new WrappedException(new SQLException("Unable to find Business: " + accounting));
        return business;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_ACCOUNTING: return accounting;
            case 2: return className;
            case 3: return param1;
            case 4: return param2;
            case 5: return param3;
            case 6: return param4;
            case 7: return enabled;
            case 8: return weight;
            case 9: return description;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getProviderId() {
        return pkey;
    }

    public String getClassName() {
        return className;
    }
    
    public String getParam1() {
        return param1;
    }
    
    public String getParam2() {
        return param2;
    }
    
    public String getParam3() {
        return param3;
    }
    
    public String getParam4() {
        return param4;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public int getWeight() {
        return weight;
    }

    public String getDescription() {
	return description;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CREDIT_CARD_PROCESSORS;
    }

    void initImpl(ResultSet result) throws SQLException {
        int pos = 1;
	pkey = result.getString(pos++);
	accounting = result.getString(pos++);
        className = result.getString(pos++);
        param1 = result.getString(pos++);
        param2 = result.getString(pos++);
        param3 = result.getString(pos++);
        param4 = result.getString(pos++);
        enabled = result.getBoolean(pos++);
        weight = result.getInt(pos++);
	description = result.getString(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	accounting=in.readUTF().intern();
        className = in.readUTF();
        param1 = in.readNullUTF();
        param2 = in.readNullUTF();
        param3 = in.readNullUTF();
        param4 = in.readNullUTF();
        enabled = in.readBoolean();
        weight = in.readCompressedInt();
	description=in.readNullUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(accounting);
        out.writeUTF(className);
        out.writeNullUTF(param1);
        out.writeNullUTF(param2);
        out.writeNullUTF(param3);
        out.writeNullUTF(param4);
        out.writeBoolean(enabled);
        out.writeCompressedInt(weight);
        out.writeNullUTF(description);
    }
}
