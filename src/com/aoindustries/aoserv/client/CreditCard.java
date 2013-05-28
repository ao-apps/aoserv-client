/*
 * Copyright 2000-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.IntList;
import com.aoindustries.util.InternUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A <code>CreditCard</code> stores credit card information.
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCard extends CachedObjectIntegerKey<CreditCard> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_PROCESSOR_ID=1,
        COLUMN_ACCOUNTING=2
    ;
    static final String COLUMN_ACCOUNTING_name = "accounting";
    static final String COLUMN_CREATED_name = "created";

    /**
     * Randomizes a value by adding a random number of random characters between each character of the original String.
     * The original string must be only comprised of 0-9, space, -, and /
     * 
     * @see  #derandomize(String)
     */
    static String randomize(String original) {
        Random random = AOServConnector.getRandom();
        StringBuilder randomized = new StringBuilder();
        for(int c=0, len=original.length(); c<=len; c++) {
            int randChars = random.nextInt(20);
            for(int d=0;d<randChars;d++) {
                int randVal = random.nextInt(256-32-10-3); // Skipping 0-31, 32 (space), 45 (-), 47 (/), 48-57 (0-9)
                // Offset past the first 33
                randVal += 33;
                // Offset past the -
                if(randVal>=45) randVal++;
                // Offset past the / and 0-9
                if(randVal>=47) randVal += 11;
                randomized.append((char)randVal);
            }
            if(c<len) randomized.append(original.charAt(c));
        }
        return randomized.toString();
    }

    /**
     * Derandomizes a value be stripping out all characters that are not 0-9, space, -, or /
     * 
     * @see  #randomize(String)
     */
    static String derandomize(String randomized) {
        // Strip all characters except 0-9, space, and -
        StringBuilder stripped = new StringBuilder(randomized.length());
        for(int c=0, len=randomized.length();c<len;c++) {
            char ch = randomized.charAt(c);
            if(
                (ch>='0' && ch<='9')
                || ch==' '
                || ch=='-'
                || ch=='/'
            ) stripped.append(ch);
        }
        return stripped.toString();
    }

    private String processorId;
    AccountingCode accounting;
    private String groupName;
    private String cardInfo;
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
    private String principalName;
    private boolean useMonthly;
    private boolean isActive;
    private long deactivatedOn;
    private String deactivateReason;
    private String description;
    private String encrypted_card_number;
    private int encryption_card_number_from;
    private int encryption_card_number_recipient;
    private String encrypted_expiration;
    private int encryption_expiration_from;
    private int encryption_expiration_recipient;

    // These are not pulled from the database, but are decrypted by GPG
    transient private String decryptCardNumberPassphrase;
    transient private String card_number;
    transient private String decryptExpirationPassphrase;
    transient private byte expiration_month;
    transient private short expiration_year;

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    /**
     * Flags a card as declined.
     */
    public void declined(String reason) throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.CREDIT_CARD_DECLINED,
            pkey,
            reason
    	);
    }

    /**
     * Gets the processor that is storing the credit card numbers.
     */
    public CreditCardProcessor getCreditCardProcessor() throws SQLException, IOException {
        CreditCardProcessor ccp = table.connector.getCreditCardProcessors().get(processorId);
        if(ccp==null) throw new SQLException("Unable to find CreditCardProcessor: "+processorId);
        return ccp;
    }

    public Business getBusiness() throws SQLException, IOException {
        Business business = table.connector.getBusinesses().get(accounting);
        if (business == null) throw new SQLException("Unable to find Business: " + accounting);
        return business;
    }

    /**
     * Gets the application-specific grouping for this credit card.
     */
    public String getGroupName() {
        return groupName;
    }

    public String getCardInfo() {
	return cardInfo;
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
     * @deprecated  Please use <code>com.aoindustries.creditcards.CreditCard#maskCardNumber(String)</code> instead.
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

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_PROCESSOR_ID: return processorId;
            case COLUMN_ACCOUNTING: return accounting;
            case 3: return groupName;
            case 4: return cardInfo;
            case 5: return providerUniqueId;
            case 6: return firstName;
            case 7: return lastName;
            case 8: return companyName;
            case 9: return email;
            case 10: return phone;
            case 11: return fax;
            case 12: return customerTaxId;
            case 13: return streetAddress1;
            case 14: return streetAddress2;
            case 15: return city;
            case 16: return state;
            case 17: return postalCode;
            case 18: return countryCode;
            case 19: return getCreated();
            case 20: return createdBy;
            case 21: return principalName;
            case 22: return useMonthly?Boolean.TRUE:Boolean.FALSE;
            case 23: return isActive?Boolean.TRUE:Boolean.FALSE;
            case 24: return getDeactivatedOn();
            case 25: return deactivateReason;
            case 26: return description;
            case 27: return encrypted_card_number;
            case 28: return encryption_card_number_from;
            case 29: return encryption_card_number_recipient;
            case 30: return encrypted_expiration;
            case 31: return encryption_expiration_from;
            case 32: return encryption_expiration_recipient;
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

    public CountryCode getCountryCode() throws SQLException, IOException {
        CountryCode countryCodeObj = table.connector.getCountryCodes().get(this.countryCode);
        if (countryCodeObj == null) throw new SQLException("Unable to find CountryCode: " + this.countryCode);
        return countryCodeObj;
    }

    public Timestamp getCreated() {
	return new Timestamp(created);
    }

    public BusinessAdministrator getCreatedBy() throws SQLException, IOException {
        BusinessAdministrator business_administrator = table.connector.getUsernames().get(createdBy).getBusinessAdministrator();
        if (business_administrator == null) throw new SQLException("Unable to find BusinessAdministrator: " + createdBy);
        return business_administrator;
    }

    /**
     * Gets the application-provided principal name that stored this credit card.
     */
    public String getPrincipalName() {
        return principalName;
    }

    public Timestamp getDeactivatedOn() {
	return deactivatedOn==-1 ? null : new Timestamp(deactivatedOn);
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

    public EncryptionKey getEncryptionCardNumberFrom() throws SQLException, IOException {
        if(encryption_card_number_from==-1) return null;
        EncryptionKey ek = table.connector.getEncryptionKeys().get(encryption_card_number_from);
        if(ek == null) throw new SQLException("Unable to find EncryptionKey: "+encryption_card_number_from);
        return ek;
    }

    public EncryptionKey getEncryptionCardNumberRecipient() throws SQLException, IOException {
        if(encryption_card_number_recipient==-1) return null;
        EncryptionKey er = table.connector.getEncryptionKeys().get(encryption_card_number_recipient);
        if(er == null) throw new SQLException("Unable to find EncryptionKey: "+encryption_card_number_recipient);
        return er;
    }

    public EncryptionKey getEncryptionExpirationFrom() throws SQLException, IOException {
        if(encryption_expiration_from==-1) return null;
        EncryptionKey ek = table.connector.getEncryptionKeys().get(encryption_expiration_from);
        if(ek == null) throw new SQLException("Unable to find EncryptionKey: "+encryption_expiration_from);
        return ek;
    }

    public EncryptionKey getEncryptionExpirationRecipient() throws SQLException, IOException {
        if(encryption_expiration_recipient==-1) return null;
        EncryptionKey er = table.connector.getEncryptionKeys().get(encryption_expiration_recipient);
        if(er == null) throw new SQLException("Unable to find EncryptionKey: "+encryption_expiration_recipient);
        return er;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CREDIT_CARDS;
    }

    public void init(ResultSet result) throws SQLException {
        try {
            int pos = 1;
            pkey = result.getInt(pos++);
            processorId = result.getString(pos++);
            accounting = AccountingCode.valueOf(result.getString(pos++));
            groupName = result.getString(pos++);
            cardInfo = result.getString(pos++);
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
            principalName = result.getString(pos++);
            useMonthly = result.getBoolean(pos++);
            isActive = result.getBoolean(pos++);
            Timestamp time = result.getTimestamp(pos++);
            deactivatedOn = time == null ? -1 : time.getTime();
            deactivateReason = result.getString(pos++);
            description = result.getString(pos++);
            encrypted_card_number = result.getString(pos++);
            encryption_card_number_from = result.getInt(pos++);
            if(result.wasNull()) encryption_card_number_from = -1;
            encryption_card_number_recipient = result.getInt(pos++);
            if(result.wasNull()) encryption_card_number_recipient = -1;
            encrypted_expiration = result.getString(pos++);
            encryption_expiration_from = result.getInt(pos++);
            if(result.wasNull()) encryption_expiration_from = -1;
            encryption_expiration_recipient = result.getInt(pos++);
            if(result.wasNull()) encryption_expiration_recipient = -1;
        } catch(ValidationException e) {
            SQLException exc = new SQLException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    public boolean getIsActive() {
	return isActive;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        try {
            pkey=in.readCompressedInt();
            processorId=in.readUTF().intern();
            accounting=AccountingCode.valueOf(in.readUTF()).intern();
            groupName=in.readNullUTF();
            cardInfo=in.readUTF();
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
            state=InternUtils.intern(in.readNullUTF());
            postalCode=in.readNullUTF();
            countryCode=in.readUTF().intern();
            created=in.readLong();
            createdBy=in.readUTF().intern();
            principalName=in.readNullUTF();
            useMonthly=in.readBoolean();
            isActive=in.readBoolean();
            deactivatedOn=in.readLong();
            deactivateReason=in.readNullUTF();
            description=in.readNullUTF();
            encrypted_card_number=in.readNullUTF();
            encryption_card_number_from=in.readCompressedInt();
            encryption_card_number_recipient=in.readCompressedInt();
            encrypted_expiration=in.readNullUTF();
            encryption_expiration_from=in.readCompressedInt();
            encryption_expiration_recipient=in.readCompressedInt();
        } catch(ValidationException e) {
            IOException exc = new IOException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.CREDIT_CARDS, pkey);
    }

    @Override
    String toStringImpl() {
	return cardInfo;
    }

    public boolean getUseMonthly() {
	return useMonthly;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_29)>=0) out.writeUTF(processorId);
	out.writeUTF(accounting.toString());
	if(version.compareTo(AOServProtocol.Version.VERSION_1_28)<=0) out.writeCompressedInt(0);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_29)>=0) out.writeNullUTF(groupName);
	out.writeUTF(cardInfo);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_28)<=0) {
            out.writeCompressedInt(0);
            out.writeCompressedInt(0);
            out.writeCompressedInt(0);
            out.writeCompressedInt(0);
            out.writeCompressedInt(0);
            if(state==null) out.writeCompressedInt(-1);
            else out.writeCompressedInt(0);
            out.writeCompressedInt(0);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_29)>=0) {
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
        if(version.compareTo(AOServProtocol.Version.VERSION_1_29)>=0) out.writeNullUTF(principalName);
	out.writeBoolean(useMonthly);
	out.writeBoolean(isActive);
	out.writeLong(deactivatedOn);
	out.writeNullUTF(deactivateReason);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_28)<=0) out.writeCompressedInt(Integer.MAX_VALUE - pkey);
	out.writeNullUTF(description);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) {
            out.writeNullUTF(encrypted_card_number);
            out.writeCompressedInt(encryption_card_number_from);
            out.writeCompressedInt(encryption_card_number_recipient);
            out.writeNullUTF(encrypted_expiration);
            out.writeCompressedInt(encryption_expiration_from);
            out.writeCompressedInt(encryption_expiration_recipient);
        }
    }
    
    /**
     * Updates the credit card information (not including the card number).
     */
    public void update(
        String firstName,
        String lastName,
        String companyName,
        String email,
        String phone,
        String fax,
        String customerTaxId,
        String streetAddress1,
        String streetAddress2,
        String city,
        String state,
        String postalCode,
        CountryCode countryCode,
        String description
    ) throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.UPDATE_CREDIT_CARD,
            pkey,
            firstName,
            lastName,
            companyName==null ? "" : companyName,
            email==null ? "" : email,
            phone==null ? "" : phone,
            fax==null ? "" : fax,
            customerTaxId==null ? "" : customerTaxId,
            streetAddress1,
            streetAddress2==null ? "" : streetAddress2,
            city,
            state==null ? "" : state,
            postalCode==null ? "" : postalCode,
            countryCode.getCode(),
            description==null ? "" : description
        );
    }

    /**
     * Updates the credit card number and expiration, including the masked card number.
     * Encrypts the data if the processors has been configured to store card encrypted
     * in the master database.
     */
    public void updateCardNumberAndExpiration(
        final String maskedCardNumber,
        String cardNumber,
        byte expirationMonth,
        short expirationYear
    ) throws IOException, SQLException {
        CreditCardProcessor processor = getCreditCardProcessor();
        final EncryptionKey encryptionFrom = processor.getEncryptionFrom();
        final EncryptionKey encryptionRecipient = processor.getEncryptionRecipient();
        final String encryptedCardNumber;
        final String encryptedExpiration;
        if(encryptionFrom!=null && encryptionRecipient!=null) {
            // Encrypt the card number and expiration
            encryptedCardNumber = encryptionFrom.encrypt(encryptionRecipient, randomize(cardNumber));
            encryptedExpiration = encryptionFrom.encrypt(encryptionRecipient, randomize(expirationMonth+"/"+expirationYear));
        } else {
            encryptedCardNumber = null;
            encryptedExpiration = null;
        }

        table.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.UPDATE_CREDIT_CARD_NUMBER_AND_EXPIRATION.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeUTF(maskedCardNumber);
                    out.writeNullUTF(encryptedCardNumber);
                    out.writeNullUTF(encryptedExpiration);
                    out.writeCompressedInt(encryptionFrom==null ? -1 : encryptionFrom.getPkey());
                    out.writeCompressedInt(encryptionRecipient==null ? -1 : encryptionRecipient.getPkey());
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public void afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    /**
     * Updates the credit card expiration.  Encrypts the data if the processors
     * has been configured to store card encrypted in the master database.
     */
    public void updateCardExpiration(
        byte expirationMonth,
        short expirationYear
    ) throws IOException, SQLException {
        CreditCardProcessor processor = getCreditCardProcessor();
        EncryptionKey encryptionFrom = processor.getEncryptionFrom();
        EncryptionKey encryptionRecipient = processor.getEncryptionRecipient();
        if(encryptionFrom!=null && encryptionRecipient!=null) {
            // Encrypt the expiration
            String encryptedExpiration = encryptionFrom.encrypt(encryptionRecipient, randomize(expirationMonth+"/"+expirationYear));
            table.connector.requestUpdateIL(
                true,
                AOServProtocol.CommandID.UPDATE_CREDIT_CARD_EXPIRATION,
                pkey,
                encryptedExpiration,
                encryptionFrom.getPkey(),
                encryptionRecipient.getPkey()
            );
        }
    }

    /**
     * Reactivates a credit card.
     */
    public void reactivate() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REACTIVATE_CREDIT_CARD,
            pkey
        );
    }

    /**
     * Gets the card number or <code>null</code> if not stored.
     */
    synchronized public String getCardNumber(String passphrase) throws IOException, SQLException {
        // If a different passphrase is provided, don't use the cached values, clear, and re-decrypt
        if(decryptCardNumberPassphrase==null || !passphrase.equals(decryptCardNumberPassphrase)) {
            // Clear first just in case there is a problem in part of the decryption
            decryptCardNumberPassphrase=null;
            card_number=null;

            if(encrypted_card_number!=null) {
                // Perform the decryption
                card_number = derandomize(getEncryptionCardNumberRecipient().decrypt(encrypted_card_number, passphrase));
            }
            decryptCardNumberPassphrase=passphrase;
        }
        return card_number;
    }

    synchronized private void decryptExpiration(String passphrase) throws IOException, SQLException {
        // If a different passphrase is provided, don't use the cached values, clear, and re-decrypt
        if(decryptExpirationPassphrase==null || !passphrase.equals(decryptExpirationPassphrase)) {
            // Clear first just in case there is a problem in part of the decryption
            decryptExpirationPassphrase=null;
            expiration_month=-1;
            expiration_year=-1;

            if(encrypted_expiration!=null) {
                // Perform the decryption
                String decrypted = getEncryptionExpirationRecipient().decrypt(encrypted_expiration, passphrase);
                // Strip all characters except 0-9, and /
                StringBuilder stripped = new StringBuilder(decrypted.length());
                for(int c=0, len=decrypted.length();c<len;c++) {
                    char ch = decrypted.charAt(c);
                    if(
                        (ch>='0' && ch<='0')
                        || ch=='/'
                    ) stripped.append(ch);
                }
                int pos = stripped.indexOf("/");
                if(pos==-1) throw new IOException("Unable to find /");
                expiration_month = Byte.parseByte(stripped.substring(0, pos));
                expiration_year = Short.parseShort(stripped.substring(pos+1));
            }
            decryptExpirationPassphrase=passphrase;
        }
    }

    /**
     * Gets the expiration month or <code>-1</code> if not stored.
     */
    synchronized public byte getExpirationMonth(String passphrase) throws IOException, SQLException {
        decryptExpiration(passphrase);
        return expiration_month;
    }

    /**
     * Gets the expiration year or <code>-1</code> if not stored.
     */
    synchronized public short getExpirationYear(String passphrase) throws IOException, SQLException {
        decryptExpiration(passphrase);
        return expiration_year;
    }
}
