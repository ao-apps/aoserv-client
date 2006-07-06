package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * A <code>CreditCard</code> stores credit card information in an encrypted
 * format.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCard extends CachedObjectIntegerKey<CreditCard> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ACCOUNTING=1
    ;

    String accounting;
    private byte[] cardNumber;
    private String cardInfo;
    private byte[]
        expirationMonth,
        expirationYear,
        cardholderName,
        streetAddress,
        city,
        state,
        zip;
    private long created;
    private String createdBy;
    private boolean useMonthly, isActive;
    private long deactivatedOn;
    private String deactivateReason;
    private int priority;
    private String description;

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    /**
     * Flags a card as declined.
     */
    public void declined(String reason) {
	table.connector.requestUpdateIL(
            AOServProtocol.CREDIT_CARD_DECLINED,
            pkey,
            reason
	);
    }

    public Business getBusiness() {
        Business business = table.connector.businesses.get(accounting);
        if (business == null) throw new WrappedException(new SQLException("Unable to find Business: " + accounting));
        return business;
    }

    public byte[] getCardholderName() {
	return cardholderName;
    }

    public String getCardInfo() {
	return cardInfo;
    }

    /**
     * Gets the default card info for a credit card number.
     */
    public static String getCardInfo(String cardNumber) {
        String nums = "";
        int len = cardNumber.length();
        for (int c = (len - 1); c >= 0; c--) {
            char ch = cardNumber.charAt(c);
            if (ch >= '0' && ch <= '9') {
                nums = ch + nums;
                if (nums.length() >= 4) return nums;
            }
        }
        return nums;
    }

    public byte[] getCardNumber() {
	return cardNumber;
    }

    public byte[] getCity() {
	return city;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_ACCOUNTING: return accounting;
            case 2: return new String(cardNumber);
            case 3: return cardInfo;
            case 4: return new String(expirationMonth);
            case 5: return new String(expirationYear);
            case 6: return new String(cardholderName);
            case 7: return new String(streetAddress);
            case 8: return new String(city);
            case 9: return state==null?null:new String(state);
            case 10: return zip==null?null:new String(zip);
            case 11: return new java.sql.Date(created);
            case 12: return createdBy;
            case 13: return useMonthly?Boolean.TRUE:Boolean.FALSE;
            case 14: return isActive?Boolean.TRUE:Boolean.FALSE;
            case 15: return deactivatedOn==-1?null:new java.sql.Date(deactivatedOn);
            case 16: return deactivateReason;
            case 17: return Integer.valueOf(priority);
            case 18: return description;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getCreated() {
	return created;
    }

    public BusinessAdministrator getCreatedBy() {
        BusinessAdministrator business_administrator = table.connector.usernames.get(createdBy).getBusinessAdministrator();
        if (business_administrator == null) throw new WrappedException(new SQLException("Unable to find BusinessAdministrator: " + createdBy));
        return business_administrator;
    }

    public long getDeactivatedOn() {
	return deactivatedOn;
    }

    public String getDeactivateReason() {
	return deactivateReason;
    }

    public String getDescription() {
	return description;
    }

    public byte[] getExpirationMonth() {
	return expirationMonth;
    }

    public byte[] getExpirationYear() {
	return expirationYear;
    }

    public int getPriority() {
	return priority;
    }

    public byte[] getState() {
	return state;
    }

    public byte[] getStreetAddress() {
	return streetAddress;
    }

    protected int getTableIDImpl() {
	return SchemaTable.CREDIT_CARDS;
    }

    public byte[] getZIP() {
	return zip;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getInt(1);
	accounting = result.getString(2);
	cardNumber = result.getString(3).getBytes();
	cardInfo = result.getString(4);
	expirationMonth = result.getString(5).getBytes();
	expirationYear = result.getString(6).getBytes();
	cardholderName = result.getString(7).getBytes();
	streetAddress = result.getString(8).getBytes();
	city = result.getString(9).getBytes();
	String S=result.getString(10);
	state = S==null?null:S.getBytes();
	zip = result.getString(11).getBytes();
	created = result.getTimestamp(12).getTime();
	createdBy = result.getString(13);
	useMonthly = result.getBoolean(14);
	isActive = result.getBoolean(15);
	Timestamp time = result.getTimestamp(16);
	deactivatedOn = time == null ? -1 : time.getTime();
	deactivateReason = result.getString(17);
	priority = result.getInt(18);
	description = result.getString(19);
    }

    public boolean isActive() {
	return isActive;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	accounting=in.readUTF();
	in.readFully(cardNumber=new byte[in.readCompressedInt()]);
	cardInfo=in.readUTF();
	in.readFully(expirationMonth=new byte[in.readCompressedInt()]);
	in.readFully(expirationYear=new byte[in.readCompressedInt()]);
	in.readFully(cardholderName=new byte[in.readCompressedInt()]);
	in.readFully(streetAddress=new byte[in.readCompressedInt()]);
	in.readFully(city=new byte[in.readCompressedInt()]);
	int len=in.readCompressedInt();
	state=len>=0?new byte[len]:null;
	if(state!=null) in.readFully(state);
	in.readFully(zip=new byte[in.readCompressedInt()]);
	created=in.readLong();
	createdBy=in.readUTF();
	useMonthly=in.readBoolean();
	isActive=in.readBoolean();
	deactivatedOn=in.readLong();
	deactivateReason=readNullUTF(in);
	priority=in.readCompressedInt();
	description=readNullUTF(in);
    }

    public void remove() {
	table.connector.requestUpdateIL(AOServProtocol.REMOVE, SchemaTable.CREDIT_CARDS, pkey);
    }

    String toStringImpl() {
	return cardInfo;
    }

    public boolean useMonthly() {
	return useMonthly;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(accounting);
	out.writeCompressedInt(cardNumber.length); out.write(cardNumber);
	out.writeUTF(cardInfo);
	out.writeCompressedInt(expirationMonth.length); out.write(expirationMonth);
	out.writeCompressedInt(expirationYear.length); out.write(expirationYear);
	out.writeCompressedInt(cardholderName.length); out.write(cardholderName);
	out.writeCompressedInt(streetAddress.length); out.write(streetAddress);
	out.writeCompressedInt(city.length); out.write(city);
	if(state==null) out.writeCompressedInt(-1);
	else {
            out.writeCompressedInt(state.length);
            out.write(state);
	}
	out.writeCompressedInt(zip.length); out.write(zip);
	out.writeLong(created);
	out.writeUTF(createdBy);
	out.writeBoolean(useMonthly);
	out.writeBoolean(isActive);
	out.writeLong(deactivatedOn);
	writeNullUTF(out, deactivateReason);
	out.writeCompressedInt(priority);
	writeNullUTF(out, description);
    }
}