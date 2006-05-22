package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Contact information associated with a <code>Business</code>.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessProfile extends CachedObjectIntegerKey<BusinessProfile> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ACCOUNTING=1
    ;

    String accounting;
    private int priority;

    private String name;

    private boolean isPrivate;
    private String phone, fax, address1, address2, city, state;
    String country;
    private String zip;

    private boolean sendInvoice;

    private long created;

    private String billingContact, billingEmail, technicalContact, technicalEmail;

    public String getAddress1() {
	return address1;
    }

    public String getAddress2() {
	return address2;
    }

    public String getBillingContact() {
	return billingContact;
    }

    public List<String> getBillingEmail() {
	return StringUtility.splitStringCommaSpace(billingEmail);
    }

    public Business getBusiness() {
	Business business=table.connector.businesses.get(accounting);
        if (business == null) throw new WrappedException(new SQLException("Unable to find Business: " + accounting));
        return business;
    }

    public String getCity() {
	return city;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_ACCOUNTING: return accounting;
            case 2: return Integer.valueOf(priority);
            case 3: return name;
            case 4: return isPrivate?Boolean.TRUE:Boolean.FALSE;
            case 5: return phone;
            case 6: return fax;
            case 7: return address1;
            case 8: return address2;
            case 9: return city;
            case 10: return state;
            case 11: return country;
            case 12: return zip;
            case 13: return sendInvoice?Boolean.TRUE:Boolean.FALSE;
            case 14: return new java.sql.Date(created);
            case 15: return billingContact;
            case 16: return billingEmail;
            case 17: return technicalContact;
            case 18: return technicalEmail;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public CountryCode getCountry() {
        CountryCode countryCode = table.connector.countryCodes.get(country);
        if (countryCode == null) throw new WrappedException(new SQLException("CountryCode not found: " + country));
        return countryCode;
    }

    public long getCreated() {
	return created;
    }

    public String getFax() {
	return fax;
    }

    public String getName() {
	return name;
    }

    public String getPhone() {
	return phone;
    }

    public int getPriority() {
	return priority;
    }

    public String getState() {
	return state;
    }

    protected int getTableIDImpl() {
	return SchemaTable.BUSINESS_PROFILES;
    }

    public String getTechnicalContact() {
	return technicalContact;
    }

    public List<String> getTechnicalEmail() {
	return StringUtility.splitStringCommaSpace(technicalEmail);
    }

    public String getZIP() {
	return zip;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getInt(1);
	accounting = result.getString(2);
	priority = result.getInt(3);
	name = result.getString(4);
	isPrivate = result.getBoolean(5);
	phone = result.getString(6);
	fax = result.getString(7);
	address1 = result.getString(8);
	address2 = result.getString(9);
	city = result.getString(10);
	state = result.getString(11);
	country = result.getString(12);
	zip = result.getString(13);
	sendInvoice = result.getBoolean(14);
	created = result.getTimestamp(15).getTime();
	billingContact = result.getString(16);
	billingEmail = result.getString(17);
	technicalContact = result.getString(18);
	technicalEmail = result.getString(19);
    }

    public boolean isPrivate() {
	return isPrivate;
    }

    private static String makeEmailList(String[] addresses) {
	StringBuilder SB=new StringBuilder();
	int len=addresses.length;
	for(int c=0;c<len;c++) {
            if(c>0) SB.append(' ');
            SB.append(addresses[c]);
	}
	return SB.toString();
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	accounting=in.readUTF();
	priority=in.readCompressedInt();
	name=in.readUTF();
	isPrivate=in.readBoolean();
	phone=in.readUTF();
	fax=in.readBoolean()?in.readUTF():null;
	address1=in.readUTF();
	address2=in.readBoolean()?in.readUTF():null;
	city=in.readUTF();
	state=in.readBoolean()?in.readUTF():null;
	country=in.readUTF();
	zip=in.readBoolean()?in.readUTF():null;
	sendInvoice=in.readBoolean();
	created=in.readLong();
	billingContact=in.readUTF();
	billingEmail=in.readUTF();
	technicalContact=in.readUTF();
	technicalEmail=in.readUTF();
    }

    public boolean sendInvoice() {
	return sendInvoice;
    }

    String toStringImpl() {
	return name + " ("+priority+')';
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(accounting);
	out.writeCompressedInt(priority);
	out.writeUTF(name);
	out.writeBoolean(isPrivate);
	out.writeUTF(phone);
	writeNullUTF(out, fax);
	out.writeUTF(address1);
	writeNullUTF(out, address2);
	out.writeUTF(city);
	writeNullUTF(out, state);
	out.writeUTF(country);
	writeNullUTF(out, zip);
	out.writeBoolean(sendInvoice);
	out.writeLong(created);
	out.writeUTF(billingContact);
	out.writeUTF(billingEmail);
	out.writeUTF(technicalContact);
	out.writeUTF(technicalEmail);
    }
}