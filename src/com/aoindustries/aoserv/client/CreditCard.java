package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * A <code>CreditCard</code> stores credit card information.
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCard extends CachedObjectIntegerKey<CreditCard> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ACCOUNTING=1
    ;

    String accounting;
    private String cardInfo;
    private String processorId;
    private String providerUniqueId;
    private String firstName;
    private String lastName;
    private String companyName;
    private String email;
    private String phone;
    private String fax;
    private String customerTaxId;
    private String streetAddress1;
    private String streetAddress2;
    private String city;
    private String state;
    private String postalCode;
    private String countryCode;
    private long created;
    private String createdBy;
    private boolean useMonthly;
    private boolean isActive;
    private long deactivatedOn;
    private String deactivateReason;
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

    public String getCardInfo() {
	return cardInfo;
    }

    /**
     * Gets the processor that is storing the credit card numbers.
     */
    public CreditCardProcessor getCreditCardProcessor() {
        CreditCardProcessor ccp = table.connector.creditCardProcessors.get(processorId);
        if(ccp==null) throw new WrappedException(new SQLException("Unable to find CreditCardProcessor: "+processorId));
        return ccp;
    }

    /**
     * Gets the unique identifier that represents the CISP - compliant storage mechanism for the card
     * number and expiration date.
     */
    public String getProviderUniqueId() {
	return providerUniqueId;
    }

    /**
     * Gets the default card info for a credit card number.
     *
     * @deprecated  Please use <code>com.aoindustries.creditcards.CreditCard#maskCardNumber instead.
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

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_ACCOUNTING: return accounting;
            case 2: return cardInfo;
            case 3: return processorId;
            case 4: return providerUniqueId;
            case 5: return firstName;
            case 6: return lastName;
            case 7: return companyName;
            case 8: return email;
            case 9: return phone;
            case 10: return fax;
            case 11: return customerTaxId;
            case 12: return streetAddress1;
            case 13: return streetAddress2;
            case 14: return city;
            case 15: return state;
            case 16: return postalCode;
            case 17: return countryCode;
            case 18: return new java.sql.Date(created);
            case 19: return createdBy;
            case 20: return useMonthly?Boolean.TRUE:Boolean.FALSE;
            case 21: return isActive?Boolean.TRUE:Boolean.FALSE;
            case 22: return deactivatedOn==-1?null:new java.sql.Date(deactivatedOn);
            case 23: return deactivateReason;
            case 24: return description;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    public String getCompanyName() {
        return companyName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getFax() {
        return fax;
    }
    
    public String getCustomerTaxId() {
        return customerTaxId;
    }
    
    public String getStreetAddress1() {
        return streetAddress1;
    }
    
    public String getStreetAddress2() {
        return streetAddress2;
    }

    public String getCity() {
        return city;
    }
    
    public String getState() {
        return state;
    }
    
    public String getPostalCode() {
        return postalCode;
    }

    public CountryCode getCountryCode() {
        CountryCode countryCode = table.connector.countryCodes.get(this.countryCode);
        if (countryCode == null) throw new WrappedException(new SQLException("Unable to find CountryCode: " + this.countryCode));
        return countryCode;
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

    public String getDeactivatedOnString() {
	return deactivatedOn==-1 ? null : SQLUtility.getDate(deactivatedOn);
    }

    public String getDeactivateReason() {
	return deactivateReason;
    }

    public String getDescription() {
	return description;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CREDIT_CARDS;
    }

    void initImpl(ResultSet result) throws SQLException {
        int pos = 1;
	pkey = result.getInt(pos++);
	accounting = result.getString(pos++);
	cardInfo = result.getString(pos++);
        processorId = result.getString(pos++);
        providerUniqueId = result.getString(pos++);
        firstName = result.getString(pos++);
        lastName = result.getString(pos++);
        companyName = result.getString(pos++);
        email = result.getString(pos++);
        phone = result.getString(pos++);
        fax = result.getString(pos++);
        customerTaxId = result.getString(pos++);
        streetAddress1 = result.getString(pos++);
        streetAddress2 = result.getString(pos++);
        city = result.getString(pos++);
        state = result.getString(pos++);
        postalCode = result.getString(pos++);
        countryCode = result.getString(pos++);
	created = result.getTimestamp(pos++).getTime();
	createdBy = result.getString(pos++);
	useMonthly = result.getBoolean(pos++);
	isActive = result.getBoolean(pos++);
	Timestamp time = result.getTimestamp(pos++);
	deactivatedOn = time == null ? -1 : time.getTime();
	deactivateReason = result.getString(pos++);
	description = result.getString(pos++);
    }

    public boolean getIsActive() {
	return isActive;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	accounting=in.readUTF().intern();
        cardInfo=in.readUTF();
        processorId=in.readUTF().intern();
        providerUniqueId=in.readUTF();
        firstName=in.readUTF();
        lastName=in.readUTF();
        companyName=in.readNullUTF();
        email=in.readNullUTF();
        phone=in.readNullUTF();
        fax=in.readNullUTF();
        customerTaxId=in.readNullUTF();
        streetAddress1=in.readUTF();
        streetAddress2=in.readNullUTF();
        city=in.readUTF();
        state=StringUtility.intern(in.readNullUTF());
        postalCode=in.readNullUTF();
        countryCode=in.readUTF().intern();
	created=in.readLong();
	createdBy=in.readUTF().intern();
	useMonthly=in.readBoolean();
	isActive=in.readBoolean();
	deactivatedOn=in.readLong();
	deactivateReason=in.readNullUTF();
	description=in.readNullUTF();
    }

    public void remove() {
	table.connector.requestUpdateIL(AOServProtocol.REMOVE, SchemaTable.TableID.CREDIT_CARDS, pkey);
    }

    String toStringImpl() {
	return cardInfo;
    }

    public boolean getUseMonthly() {
	return useMonthly;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(accounting);
	if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_28)<=0) out.writeCompressedInt(0);
	out.writeUTF(cardInfo);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_28)<=0) {
            out.writeCompressedInt(0);
            out.writeCompressedInt(0);
            out.writeCompressedInt(0);
            out.writeCompressedInt(0);
            out.writeCompressedInt(0);
            if(state==null) out.writeCompressedInt(-1);
            else out.writeCompressedInt(0);
            out.writeCompressedInt(0);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_29)>=0) {
            out.writeUTF(processorId);
            out.writeUTF(providerUniqueId);
            out.writeUTF(firstName);
            out.writeUTF(lastName);
            out.writeNullUTF(companyName);
            out.writeNullUTF(email);
            out.writeNullUTF(phone);
            out.writeNullUTF(fax);
            out.writeNullUTF(customerTaxId);
            out.writeUTF(streetAddress1);
            out.writeNullUTF(streetAddress2);
            out.writeUTF(city);
            out.writeNullUTF(state);
            out.writeNullUTF(postalCode);
            out.writeUTF(countryCode);
        }
	out.writeLong(created);
	out.writeUTF(createdBy);
	out.writeBoolean(useMonthly);
	out.writeBoolean(isActive);
	out.writeLong(deactivatedOn);
	out.writeNullUTF(deactivateReason);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_28)<=0) out.writeCompressedInt(Integer.MAX_VALUE - pkey);
	out.writeNullUTF(description);
    }
}
