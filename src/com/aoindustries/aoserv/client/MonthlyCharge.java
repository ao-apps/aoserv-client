package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * Miscellaneous monthly charges may be applied to a
 * <code>Business</code>.  These currently include
 * the recurring charges that are not fully automated.
 * Once all of the accounting data is available in other
 * places of the system, the use of this table will
 * decrease and possibly disappear.
 *
 * @see  Business
 * @see  Transaction
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MonthlyCharge extends CachedObjectIntegerKey<MonthlyCharge> {

    static final int COLUMN_PKEY=0;
    static final String COLUMN_ACCOUNTING_name = "accounting";
    static final String COLUMN_PACKAGE_name = "package";
    static final String COLUMN_TYPE_name = "type";
    static final String COLUMN_CREATED_name = "created";

    String accounting;
    String packageName;
    private String type;
    private String description;
    private int quantity;
    private int rate;
    private long created;
    private String created_by;
    private boolean active;

    public MonthlyCharge() {
    }

    MonthlyCharge(
	MonthlyChargeTable table,
        Business business,
	Package packageObject,
	TransactionType typeObject,
        String description,
	int quantity,
	int rate,
	BusinessAdministrator createdByObject
    ) {
	setTable(table);
	this.pkey=-1;
        this.accounting = business.getAccounting();
	this.packageName = packageObject.getName();
	this.type = typeObject.getName();
	this.description=description;
	this.quantity = quantity;
	this.rate = rate;
	this.created = System.currentTimeMillis();
	this.created_by = createdByObject.pkey;
	this.active=true;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey==-1?null:Integer.valueOf(pkey);
            case 1: return accounting;
            case 2: return packageName;
            case 3: return type;
            case 4: return description;
            case 5: return Integer.valueOf(quantity);
            case 6: return Integer.valueOf(rate);
            case 7: return new java.sql.Date(created);
            case 8: return created_by;
            case 9: return active?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getCreated() {
	return created;
    }

    public BusinessAdministrator getCreatedBy() {
        BusinessAdministrator createdByObject = table.connector.usernames.get(created_by).getBusinessAdministrator();
        if (createdByObject == null) throw new WrappedException(new SQLException("Unable to find BusinessAdministrator: " + created_by));
        return createdByObject;
    }

    public Business getBusiness() {
        Business bu=table.connector.businesses.get(accounting);
        if(bu==null) throw new WrappedException(new SQLException("Unable to find Business: "+accounting));
        return bu;
    }

    public String getDescription() {
	return description == null ? getType().getDescription() : description;
    }

    public Package getPackage() {
	Package packageObject = table.connector.packages.get(packageName);
  	if (packageObject == null) throw new WrappedException(new SQLException("Unable to find Package: " + packageName));
	return packageObject;
    }

    public int getPennies() {
        int pennies=quantity*rate/100;
        int fraction=pennies%10;
        pennies/=10;
        if(fraction>=5) pennies++;
        else if(fraction<=-5) pennies--;
        return pennies;
    }

    public int getQuantity() {
	return quantity;
    }

    public int getRate() {
	return rate;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MONTHLY_CHARGES;
    }

    public TransactionType getType() {
        TransactionType typeObject = table.connector.transactionTypes.get(type);
        if (typeObject == null) throw new WrappedException(new SQLException("Unable to find TransactionType: " + type));
        return typeObject;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getInt(1);
        accounting = result.getString(2);
	packageName = result.getString(3);
	type = result.getString(4);
	description = result.getString(5);
	quantity = SQLUtility.getMillis(result.getString(6));
	rate = SQLUtility.getPennies(result.getString(7));
	created = result.getTimestamp(8).getTime();
	created_by = result.getString(9);
	active = result.getBoolean(10);
    }

    public boolean isActive() {
	return active;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
        accounting=in.readUTF().intern();
	packageName=in.readUTF().intern();
	type=in.readUTF().intern();
	description=in.readBoolean()?in.readUTF():null;
	quantity=in.readCompressedInt();
	rate=in.readCompressedInt();
	created=in.readLong();
	created_by=in.readUTF().intern();
	active=in.readBoolean();
    }

    String toStringImpl() {
	return packageName+'|'+type+'|'+SQLUtility.getMilliDecimal(quantity)+"x$"+SQLUtility.getDecimal(rate);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
        out.writeUTF(accounting);
	out.writeUTF(packageName);
	out.writeUTF(type);
	out.writeNullUTF(description);
	out.writeCompressedInt(quantity);
	out.writeCompressedInt(rate);
	out.writeLong(created);
	out.writeUTF(created_by);
	out.writeBoolean(active);
    }
}