/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.ReactivateCreditCardCommand;
import com.aoindustries.aoserv.client.command.UpdateCreditCardCommand;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.Email;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;

/**
 * A <code>CreditCard</code> stores credit card information.
 *
 * @author  AO Industries, Inc.
 * @author  AO Industries, Inc.
 */
final public class CreditCard extends AOServObjectIntegerKey<CreditCard> implements BeanFactory<com.aoindustries.aoserv.client.beans.CreditCard> /*, TODO: Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
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
    final private Timestamp created;
    private UserId createdBy;
    final private String principalName;
    final private boolean useMonthly;
    final private boolean active;
    final private Timestamp deactivatedOn;
    final private String deactivateReason;
    final private String description;
    final private String encryptedCardNumber;
    final private Integer encryptionCardNumberFrom;
    final private Integer encryptionCardNumberRecipient;
    final private String encryptedExpiration;
    final private Integer encryptionExpirationFrom;
    final private Integer encryptionExpirationRecipient;

    public CreditCard(
        CreditCardService<?,?> service,
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
        Timestamp created,
        UserId createdBy,
        String principalName,
        boolean useMonthly,
        boolean active,
        Timestamp deactivatedOn,
        String deactivateReason,
        String description,
        String encryptedCardNumber,
        Integer encryptionCardNumberFrom,
        Integer encryptionCardNumberRecipient,
        String encryptedExpiration,
        Integer encryptionExpirationFrom,
        Integer encryptionExpirationRecipient
    ) {
        super(service, pkey);
        intern();
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
    protected int compareToImpl(CreditCard other) {
        int diff = accounting==other.accounting ? 0 : AOServObjectUtils.compare(accounting, other.accounting);
        if(diff!=0) return diff;
        return created.compareTo(other.created);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="primary key value")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_PROCESSOR_ID = "processor_id";
    /**
     * Gets the processor that is storing the credit card numbers.
     */
    @SchemaColumn(order=1, name=COLUMN_PROCESSOR_ID, index=IndexType.INDEXED, description="the processor that is storing the card number and expiration date")
    public CreditCardProcessor getCreditCardProcessor() throws RemoteException {
        return getService().getConnector().getCreditCardProcessors().get(processorId);
    }

    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=2, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the accounting code for the card")
    public Business getBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().get(accounting);
    }

    /**
     * Gets the application-specific grouping for this credit card.
     */
    @SchemaColumn(order=3, name="group_name", description="any application-specific grouping")
    public String getGroupName() {
        return groupName;
    }

    @SchemaColumn(order=4, name="card_info", description="the masked card number")
    public String getCardInfo() {
        return cardInfo;
    }

    /**
     * Gets the unique identifier that represents the CISP - compliant storage mechanism for the card
     * number and expiration date.
     */
    @SchemaColumn(order=5, name="provider_unique_id", description="the per-provider unique ID allowing use of this credit card for new transactions")
    public String getProviderUniqueId() {
        return providerUniqueId;
    }

    @SchemaColumn(order=6, name="first_name", description="the first name")
    public String getFirstName() {
        return firstName;
    }

    @SchemaColumn(order=7, name="last_name", description="the last name")
    public String getLastName() {
        return lastName;
    }

    @SchemaColumn(order=8, name="company_name", description="the company name")
    public String getCompanyName() {
        return companyName;
    }

    @SchemaColumn(order=9, name="email", description="the email address")
    public Email getEmail() {
        return email;
    }

    @SchemaColumn(order=10, name="phone", description="the daytime phone number")
    public String getPhone() {
        return phone;
    }

    @SchemaColumn(order=11, name="fax", description="the fax number")
    public String getFax() {
        return fax;
    }

    @SchemaColumn(order=12, name="customer_tax_id", description="the social security number or employer identification number")
    public String getCustomerTaxId() {
        return customerTaxId;
    }

    @SchemaColumn(order=13, name="street_address1", description="the first line of the street address")
    public String getStreetAddress1() {
        return streetAddress1;
    }

    @SchemaColumn(order=14, name="street_address2", description="the second line of the street address")
    public String getStreetAddress2() {
        return streetAddress2;
    }

    @SchemaColumn(order=15, name="city", description="the card holders city")
    public String getCity() {
        return city;
    }

    @SchemaColumn(order=16, name="state", description="the card holders state")
    public String getState() {
        return state;
    }

    @SchemaColumn(order=17, name="postal_code", description="the card holders postal code")
    public String getPostalCode() {
        return postalCode;
    }

    static final String COLUMN_COUNTRY_CODE = "country_code";
    @SchemaColumn(order=18, name=COLUMN_COUNTRY_CODE, index=IndexType.INDEXED, description="the two-character country code")
    public CountryCode getCountryCode() throws RemoteException {
        return getService().getConnector().getCountryCodes().get(this.countryCode);
    }

    @SchemaColumn(order=19, name="created", description="the time the card was added to the database")
    public Timestamp getCreated() {
        return created;
    }

    static final String COLUMN_CREATED_BY = "created_by";
    @SchemaColumn(order=20, name=COLUMN_CREATED_BY, index=IndexType.INDEXED, description="the business_administrator who added the card to the database")
    public BusinessAdministrator getCreatedBy() throws RemoteException {
        return getService().getConnector().getBusinessAdministrators().get(createdBy);
    }

    /**
     * Gets the application-provided principal name that stored this credit card.
     */
    @SchemaColumn(order=21, name="principal_name", description="the application-provided principal name of the person added the card to the database")
    public String getPrincipalName() {
        return principalName;
    }

    @SchemaColumn(order=22, name="use_monthly", description="if <code>true</code> the card will be used monthly")
    public boolean getUseMonthly() {
        return useMonthly;
    }

    @SchemaColumn(order=23, name="active", description="if <code>true</code> the card is currently active")
    public boolean isActive() {
        return active;
    }

    @SchemaColumn(order=24, name="deactivated_on", description="the time the card was deactivated")
    public Timestamp getDeactivatedOn() {
        return deactivatedOn;
    }

    @SchemaColumn(order=25, name="deactivate_reason", description="the reason the card was deactivated")
    public String getDeactivateReason() {
        return deactivateReason;
    }

    @SchemaColumn(order=26, name="description", description="any comment or description")
    public String getDescription() {
        return description;
    }

    @SchemaColumn(order=27, name="encrypted_card_number", description="the card number stored encrypted")
    public String getEncryptedCardNumber() {
        return encryptedCardNumber;
    }

    /* TODO
    @SchemaColumn(order=28, name="encryption_card_number_from", description="the from that was used for card number encryption")
    public EncryptionKey getEncryptionCardNumberFrom() throws RemoteException {
        if(encryptionCardNumberFrom==null) return null;
        return getService().getConnector().getEncryptionKeys().get(encryptionCardNumberFrom);
    }

    @SchemaColumn(order=29, name="encryption_card_number_recipient", description="the recipient that was used for card number encryption")
    public EncryptionKey getEncryptionCardNumberRecipient() throws RemoteException {
        if(encryptionCardNumberRecipient==null) return null;
        return getService().getConnector().getEncryptionKeys().get(encryptionCardNumberRecipient);
    }
    */
    @SchemaColumn(order=28, name="encrypted_expiration", description="the expiration stored encrypted")
    public String getEncryptedExpiration() {
        return encryptedExpiration;
    }
    /* TODO
    @SchemaColumn(order=31, name="encryption_expiration_from", description="the from that was used for expiration encryption")
    public EncryptionKey getEncryptionExpirationFrom() throws RemoteException {
        if(encryptionExpirationFrom==null) return null;
        return getService().getConnector().getEncryptionKeys().get(encryptionExpirationFrom);
    }

    @SchemaColumn(order=32, name="encryption_expiration_recipient", description="the recipient that was used for expiration encryption")
    public EncryptionKey getEncryptionExpirationRecipient() throws RemoteException {
        if(encryptionExpirationRecipient==null) return null;
        return getService().getConnector().getEncryptionKeys().get(encryptionExpirationRecipient);
    }*/
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.CreditCard getBean() {
        return new com.aoindustries.aoserv.client.beans.CreditCard(
            key,
            processorId,
            getBean(accounting),
            groupName,
            cardInfo,
            providerUniqueId,
            firstName,
            lastName,
            companyName,
            getBean(email),
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
            getBean(createdBy),
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

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusiness());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreditCardProcessor());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCountryCode());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreatedBy());
        /* TODO
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getEncryptionCardNumberFrom());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getEncryptionCardNumberRecipient());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getEncryptionExpirationFrom());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getEncryptionExpirationRecipient());
         */
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
    	return cardInfo;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Commands">
    /**
     * Updates the credit card information (not including the card number).
     */
    public void update(
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
        CountryCode countryCode,
        String description
    ) throws RemoteException {
        new UpdateCreditCardCommand(
            key,
            firstName,
            lastName,
            companyName,
            email,
            phone,
            fax,
            customerTaxId,
            streetAddress1,
            streetAddress2,
            city,
            state,
            postalCode,
            countryCode.getCode(),
            description
        ).execute(getService().getConnector());
    }

    /**
     * Reactivates a credit card.
     */
    public void reactivate() throws RemoteException {
        new ReactivateCreditCardCommand(key).execute(getService().getConnector());
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
    	getService().getConnector().requestUpdateIL(
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
    	getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.CREDIT_CARDS, pkey);
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

        getService().getConnector().requestUpdate(
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
                    getService().getConnector().tablesUpdated(invalidateList);
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
            getService().getConnector().requestUpdateIL(
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
