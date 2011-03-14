/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;

/**
 * A <code>CreditCard</code> stores credit card information.
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCard extends AOServObjectIntegerKey implements Comparable<CreditCard>, DtoFactory<com.aoindustries.aoserv.client.dto.CreditCard> /*, TODO: Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -1401801113623262592L;

    private String processorId;
    private AccountingCode accounting;
    private String groupName;
    final private String cardInfo;
    final private String providerUniqueId;
    final private String firstName;
    final private String lastName;
    final private String companyName;
    final private Email email;
    final private String phone;
    final private String fax;
    final private String customerTaxId;
    final private String streetAddress1;
    final private String streetAddress2;
    final private String city;
    final private String state;
    final private String postalCode;
    private String countryCode;
    final private long created;
    private UserId createdBy;
    final private String principalName;
    final private boolean useMonthly;
    final private boolean active;
    final private Long deactivatedOn;
    final private String deactivateReason;
    final private String description;
    final private String encryptedCardNumber;
    final private Integer encryptionCardNumberFrom;
    final private Integer encryptionCardNumberRecipient;
    final private String encryptedExpiration;
    final private Integer encryptionExpirationFrom;
    final private Integer encryptionExpirationRecipient;

    public CreditCard(
        AOServConnector connector,
        int pkey,
        String processorId,
        AccountingCode accounting,
        String groupName,
        String cardInfo,
        String providerUniqueId,
        String firstName,
        String lastName,
        String companyName,
        Email email,
        String phone,
        String fax,
        String customerTaxId,
        String streetAddress1,
        String streetAddress2,
        String city,
        String state,
        String postalCode,
        String countryCode,
        long created,
        UserId createdBy,
        String principalName,
        boolean useMonthly,
        boolean active,
        Long deactivatedOn,
        String deactivateReason,
        String description,
        String encryptedCardNumber,
        Integer encryptionCardNumberFrom,
        Integer encryptionCardNumberRecipient,
        String encryptedExpiration,
        Integer encryptionExpirationFrom,
        Integer encryptionExpirationRecipient
    ) {
        super(connector, pkey);
        this.processorId = processorId;
        this.accounting = accounting;
        this.groupName = groupName;
        this.cardInfo = cardInfo;
        this.providerUniqueId = providerUniqueId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.customerTaxId = customerTaxId;
        this.streetAddress1 = streetAddress1;
        this.streetAddress2 = streetAddress2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
        this.created= created;
        this.createdBy = createdBy;
        this.principalName = principalName;
        this.useMonthly = useMonthly;
        this.active = active;
        this.deactivatedOn = deactivatedOn;
        this.deactivateReason = deactivateReason;
        this.description = description;
        this.encryptedCardNumber = encryptedCardNumber;
        this.encryptionCardNumberFrom = encryptionCardNumberFrom;
        this.encryptionCardNumberRecipient = encryptionCardNumberRecipient;
        this.encryptedExpiration = encryptedExpiration;
        this.encryptionExpirationFrom = encryptionExpirationFrom;
        this.encryptionExpirationRecipient = encryptionExpirationRecipient;
        intern();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        processorId = intern(processorId);
        accounting = intern(accounting);
        groupName = intern(groupName);
        countryCode = intern(countryCode);
        createdBy = intern(createdBy);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(CreditCard other) {
        try {
            int diff = accounting==other.accounting ? 0 : compare(getBusiness(), other.getBusiness());
            if(diff!=0) return diff;
            return compare(created, other.created);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="primary key value")
    public int getPkey() {
        return key;
    }

    public static final MethodColumn COLUMN_PROCESSOR = getMethodColumn(CreditCard.class, "processor");
    /**
     * Gets the processor that is storing the credit card numbers.
     */
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the processor that is storing the card number and expiration date")
    public CreditCardProcessor getProcessor() throws RemoteException {
        return getConnector().getCreditCardProcessors().get(processorId);
    }

    public static final MethodColumn COLUMN_BUSINESS = getMethodColumn(CreditCard.class, "business");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the accounting code for the card")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(accounting);
    }

    /**
     * Gets the application-specific grouping for this credit card.
     */
    @SchemaColumn(order=3, description="any application-specific grouping")
    public String getGroupName() {
        return groupName;
    }

    @SchemaColumn(order=4, description="the masked card number")
    public String getCardInfo() {
        return cardInfo;
    }

    /**
     * Gets the unique identifier that represents the CISP - compliant storage mechanism for the card
     * number and expiration date.
     */
    @SchemaColumn(order=5, description="the per-provider unique ID allowing use of this credit card for new transactions")
    public String getProviderUniqueId() {
        return providerUniqueId;
    }

    @SchemaColumn(order=6, description="the first name")
    public String getFirstName() {
        return firstName;
    }

    @SchemaColumn(order=7, description="the last name")
    public String getLastName() {
        return lastName;
    }

    @SchemaColumn(order=8, description="the company name")
    public String getCompanyName() {
        return companyName;
    }

    @SchemaColumn(order=9, description="the email address")
    public Email getEmail() {
        return email;
    }

    @SchemaColumn(order=10, description="the daytime phone number")
    public String getPhone() {
        return phone;
    }

    @SchemaColumn(order=11, description="the fax number")
    public String getFax() {
        return fax;
    }

    @SchemaColumn(order=12, description="the social security number or employer identification number")
    public String getCustomerTaxId() {
        return customerTaxId;
    }

    @SchemaColumn(order=13, description="the first line of the street address")
    public String getStreetAddress1() {
        return streetAddress1;
    }

    @SchemaColumn(order=14, description="the second line of the street address")
    public String getStreetAddress2() {
        return streetAddress2;
    }

    @SchemaColumn(order=15, description="the card holders city")
    public String getCity() {
        return city;
    }

    @SchemaColumn(order=16, description="the card holders state")
    public String getState() {
        return state;
    }

    @SchemaColumn(order=17, description="the card holders postal code")
    public String getPostalCode() {
        return postalCode;
    }

    public static final MethodColumn COLUMN_COUNTRY_CODE = getMethodColumn(CreditCard.class, "countryCode");
    @DependencySingleton
    @SchemaColumn(order=18, index=IndexType.INDEXED, description="the two-character country code")
    public CountryCode getCountryCode() throws RemoteException {
        return getConnector().getCountryCodes().get(this.countryCode);
    }

    @SchemaColumn(order=19, description="the time the card was added to the database")
    public Timestamp getCreated() {
        return new Timestamp(created);
    }

    public static final MethodColumn COLUMN_CREATED_BY = getMethodColumn(CreditCard.class, "createdBy");
    @DependencySingleton
    @SchemaColumn(order=20, index=IndexType.INDEXED, description="the business_administrator who added the card to the database")
    public BusinessAdministrator getCreatedBy() throws RemoteException {
        return getConnector().getBusinessAdministrators().get(createdBy);
    }

    /**
     * Gets the application-provided principal name that stored this credit card.
     */
    @SchemaColumn(order=21, description="the application-provided principal name of the person added the card to the database")
    public String getPrincipalName() {
        return principalName;
    }

    @SchemaColumn(order=22, description="if <code>true</code> the card will be used monthly")
    public boolean getUseMonthly() {
        return useMonthly;
    }

    @SchemaColumn(order=23, description="if <code>true</code> the card is currently active")
    public boolean isActive() {
        return active;
    }

    @SchemaColumn(order=24, description="the time the card was deactivated")
    public Timestamp getDeactivatedOn() {
        return deactivatedOn==null ? null : new Timestamp(deactivatedOn);
    }

    @SchemaColumn(order=25, description="the reason the card was deactivated")
    public String getDeactivateReason() {
        return deactivateReason;
    }

    @SchemaColumn(order=26, description="any comment or description")
    public String getDescription() {
        return description;
    }

    @SchemaColumn(order=27, description="the card number stored encrypted")
    public String getEncryptedCardNumber() {
        return encryptedCardNumber;
    }

    /* TODO
    @DependencySingleton
    @SchemaColumn(order=28, description="the from that was used for card number encryption")
    public EncryptionKey getEncryptionCardNumberFrom() throws RemoteException {
        if(encryptionCardNumberFrom==null) return null;
        return getConnector().getEncryptionKeys().get(encryptionCardNumberFrom);
    }

    @DependencySingleton
    @SchemaColumn(order=29, description="the recipient that was used for card number encryption")
    public EncryptionKey getEncryptionCardNumberRecipient() throws RemoteException {
        if(encryptionCardNumberRecipient==null) return null;
        return getConnector().getEncryptionKeys().get(encryptionCardNumberRecipient);
    }
    */
    @SchemaColumn(order=28, description="the expiration stored encrypted")
    public String getEncryptedExpiration() {
        return encryptedExpiration;
    }
    /* TODO
    @DependencySingleton
    @SchemaColumn(order=31, description="the from that was used for expiration encryption")
    public EncryptionKey getEncryptionExpirationFrom() throws RemoteException {
        if(encryptionExpirationFrom==null) return null;
        return getConnector().getEncryptionKeys().get(encryptionExpirationFrom);
    }

    @DependencySingleton
    @SchemaColumn(order=32, description="the recipient that was used for expiration encryption")
    public EncryptionKey getEncryptionExpirationRecipient() throws RemoteException {
        if(encryptionExpirationRecipient==null) return null;
        return getConnector().getEncryptionKeys().get(encryptionExpirationRecipient);
    }*/
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public CreditCard(AOServConnector connector, com.aoindustries.aoserv.client.dto.CreditCard dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getProcessorId(),
            getAccountingCode(dto.getAccounting()),
            dto.getGroupName(),
            dto.getCardInfo(),
            dto.getProviderUniqueId(),
            dto.getFirstName(),
            dto.getLastName(),
            dto.getCompanyName(),
            getEmail(dto.getEmail()),
            dto.getPhone(),
            dto.getFax(),
            dto.getCustomerTaxId(),
            dto.getStreetAddress1(),
            dto.getStreetAddress2(),
            dto.getCity(),
            dto.getState(),
            dto.getPostalCode(),
            dto.getCountryCode(),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getPrincipalName(),
            dto.isUseMonthly(),
            dto.isActive(),
            getTimeMillis(dto.getDeactivatedOn()),
            dto.getDeactivateReason(),
            dto.getDescription(),
            dto.getEncryptedCardNumber(),
            dto.getEncryptionCardNumberFrom(),
            dto.getEncryptionCardNumberRecipient(),
            dto.getEncryptedExpiration(),
            dto.getEncryptionExpirationFrom(),
            dto.getEncryptionExpirationRecipient()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.CreditCard getDto() {
        return new com.aoindustries.aoserv.client.dto.CreditCard(
            key,
            processorId,
            getDto(accounting),
            groupName,
            cardInfo,
            providerUniqueId,
            firstName,
            lastName,
            companyName,
            getDto(email),
            phone,
            fax,
            customerTaxId,
            streetAddress1,
            streetAddress2,
            city,
            state,
            postalCode,
            countryCode,
            created,
            getDto(createdBy),
            principalName,
            useMonthly,
            active,
            deactivatedOn,
            deactivateReason,
            description,
            encryptedCardNumber,
            encryptionCardNumberFrom,
            encryptionCardNumberRecipient,
            encryptedExpiration,
            encryptionExpirationFrom,
            encryptionExpirationRecipient
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
    	return cardInfo;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }
    */
    /**
     * Flags a card as declined.
     */
    /* TODO
    public void declined(String reason) throws IOException, SQLException {
    	getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.CREDIT_CARD_DECLINED,
            pkey,
            reason
    	);
    }
    */
    /**
     * Gets the default card info for a credit card number.
     *
     * @deprecated  Please use <code>com.aoindustries.creditcards.CreditCard#maskCardNumber(String)</code> instead.
     */
    /* TODO
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

    public void remove() throws IOException, SQLException {
    	getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.CREDIT_CARDS, pkey);
    }
    */
    /**
     * Updates the credit card number and expiration, including the masked card number.
     * Encrypts the data if the processors has been configured to store card encrypted
     * in the master database.
     */
    /* TODO
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

        getConnector().requestUpdate(
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
                    getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }
    */
    /**
     * Updates the credit card expiration.  Encrypts the data if the processors
     * has been configured to store card encrypted in the master database.
     */
    /* TODO
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
            getConnector().requestUpdateIL(
                true,
                AOServProtocol.CommandID.UPDATE_CREDIT_CARD_EXPIRATION,
                pkey,
                encryptedExpiration,
                encryptionFrom.getPkey(),
                encryptionRecipient.getPkey()
            );
        }
    }
    */
    /* TODO
    // These are not pulled from the database, but are decrypted by GPG
    transient private String decryptCardNumberPassphrase;
    transient private String card_number;
    transient private String decryptExpirationPassphrase;
    transient private byte expiration_month;
    transient private short expiration_year;
    */
    /**
     * Gets the card number or <code>null</code> if not stored.
     */
    /* TODO
    synchronized public String getCardNumber(String passphrase) throws IOException, SQLException {
        // If a different passphrase is provided, don't use the cached values, clear, and re-decrypt
        if(decryptCardNumberPassphrase==null || !passphrase.equals(decryptCardNumberPassphrase)) {
            // Clear first just in case there is a problem in part of the decryption
            decryptCardNumberPassphrase=null;
            card_number=null;

            if(encryptedCardNumber!=null) {
                // Perform the decryption
                card_number = derandomize(getEncryptionCardNumberRecipient().decrypt(encryptedCardNumber, passphrase));
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

            if(encryptedExpiration!=null) {
                // Perform the decryption
                String decrypted = getEncryptionExpirationRecipient().decrypt(encryptedExpiration, passphrase);
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
                if(pos==-1) throw new AssertionError("Unable to find /");
                expiration_month = Byte.parseByte(stripped.substring(0, pos));
                expiration_year = Short.parseShort(stripped.substring(pos+1));
            }
            decryptExpirationPassphrase=passphrase;
        }
    }
    */
    /**
     * Gets the expiration month or <code>-1</code> if not stored.
     */
    /* TODO
    synchronized public byte getExpirationMonth(String passphrase) throws IOException, SQLException {
        decryptExpiration(passphrase);
        return expiration_month;
    }
    */
    /**
     * Gets the expiration year or <code>-1</code> if not stored.
     */
    /* TODO
    synchronized public short getExpirationYear(String passphrase) throws IOException, SQLException {
        decryptExpiration(passphrase);
        return expiration_year;
    }
    */
    /**
     * Randomizes a value by adding a random number of random characters between each character of the original String.
     * The original string must be only comprised of 0-9, space, -, and /
     *
     * @see  #derandomize(String)
     */
    /* TODO
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
    } */

    /**
     * Derandomizes a value be stripping out all characters that are not 0-9, space, -, or /
     *
     * @see  #randomize(String)
     */
    /* TODO
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
    } */
    // </editor-fold>
}
