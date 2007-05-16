package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Stores a single sign-up request.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SignupRequest extends CachedObjectIntegerKey<SignupRequest> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ACCOUNTING=1
    ;

    String accounting;
    private long time;
    private String ip_address;
    private int package_definition;
    private String business_name;
    private String business_phone;
    private String business_fax;
    private String business_address1;
    private String business_address2;
    private String business_city;
    private String business_state;
    private String business_country;
    private String business_zip;
    private String ba_name;
    private String ba_title;
    private String ba_work_phone;
    private String ba_cell_phone;
    private String ba_home_phone;
    private String ba_fax;
    private String ba_email;
    private String ba_address1;
    private String ba_address2;
    private String ba_city;
    private String ba_state;
    private String ba_country;
    private String ba_zip;
    private String ba_username;
    private String billing_contact;
    private String billing_email;
    private boolean billing_use_monthly;
    private boolean billing_pay_one_year;
    private String encrypted_data;
    private int encryption_key;
    private String completed_by;
    private long completed_time;

    // These are not pulled from the database, but are decrypted from encrypted_data by GPG
    transient private String decryptPassphrase;
    transient private String ba_password;
    transient private String billing_cardholder_name;
    transient private String billing_card_number;
    transient private String billing_expiration_month;
    transient private String billing_expiration_year;
    transient private String billing_street_address;
    transient private String billing_city;
    transient private String billing_state;
    transient private String billing_zip;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_ACCOUNTING: return accounting;
            case 2: return time;
            case 3: return ip_address;
            case 4: return package_definition;
            case 5: return business_name;
            case 6: return business_phone;
            case 7: return business_fax;
            case 8: return business_address1;
            case 9: return business_address2;
            case 10: return business_city;
            case 11: return business_state;
            case 12: return business_country;
            case 13: return business_zip;
            case 14: return ba_name;
            case 15: return ba_title;
            case 16: return ba_work_phone;
            case 17: return ba_cell_phone;
            case 18: return ba_home_phone;
            case 19: return ba_fax;
            case 20: return ba_email;
            case 21: return ba_address1;
            case 22: return ba_address2;
            case 23: return ba_city;
            case 24: return ba_state;
            case 25: return ba_country;
            case 26: return ba_zip;
            case 27: return ba_username;
            case 28: return billing_contact;
            case 29: return billing_email;
            case 30: return billing_use_monthly;
            case 31: return billing_pay_one_year;
            case 32: return encrypted_data;
            case 33: return encryption_key;
            case 34: return completed_by;
            case 35: return completed_time;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    protected int getTableIDImpl() {
	return SchemaTable.SIGNUP_REQUESTS;
    }

    void initImpl(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getInt(pos++);
	accounting = result.getString(pos++);
        time = result.getTimestamp(pos++).getTime();
        ip_address=result.getString(pos++);
        package_definition=result.getInt(pos++);
        business_name=result.getString(pos++);
        business_phone=result.getString(pos++);
        business_fax=result.getString(pos++);
        business_address1=result.getString(pos++);
        business_address2=result.getString(pos++);
        business_city=result.getString(pos++);
        business_state=result.getString(pos++);
        business_country=result.getString(pos++);
        business_zip=result.getString(pos++);
        ba_name=result.getString(pos++);
        ba_title=result.getString(pos++);
        ba_work_phone=result.getString(pos++);
        ba_cell_phone=result.getString(pos++);
        ba_home_phone=result.getString(pos++);
        ba_fax=result.getString(pos++);
        ba_email=result.getString(pos++);
        ba_address1=result.getString(pos++);
        ba_address2=result.getString(pos++);
        ba_city=result.getString(pos++);
        ba_state=result.getString(pos++);
        ba_country=result.getString(pos++);
        ba_zip=result.getString(pos++);
        ba_username=result.getString(pos++);
        billing_contact=result.getString(pos++);
        billing_email=result.getString(pos++);
        billing_use_monthly=result.getBoolean(pos++);
        billing_pay_one_year=result.getBoolean(pos++);
        encrypted_data=result.getString(pos++);
        encryption_key=result.getInt(pos++);
        completed_by=result.getString(pos++);
        Timestamp T = result.getTimestamp(pos++);
        completed_time = T==null ? -1 : T.getTime();
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	accounting=in.readUTF();
        time=in.readLong();
        ip_address=in.readUTF();
        package_definition=in.readCompressedInt();
        business_name=in.readUTF();
        business_phone=in.readUTF();
        business_fax=readNullUTF(in);
        business_address1=in.readUTF();
        business_address2=readNullUTF(in);
        business_city=in.readUTF();
        business_state=readNullUTF(in);
        business_country=in.readUTF();
        business_zip=readNullUTF(in);
        ba_name=in.readUTF();
        ba_title=readNullUTF(in);
        ba_work_phone=in.readUTF();
        ba_cell_phone=readNullUTF(in);
        ba_home_phone=readNullUTF(in);
        ba_fax=readNullUTF(in);
        ba_email=in.readUTF();
        ba_address1=readNullUTF(in);
        ba_address2=readNullUTF(in);
        ba_city=readNullUTF(in);
        ba_state=readNullUTF(in);
        ba_country=readNullUTF(in);
        ba_zip=readNullUTF(in);
        ba_username=in.readUTF();
        billing_contact=in.readUTF();
        billing_email=in.readUTF();
        billing_use_monthly=in.readBoolean();
        billing_pay_one_year=in.readBoolean();
        encrypted_data=in.readUTF();
        encryption_key=in.readCompressedInt();
        completed_by=readNullUTF(in);
        completed_time=in.readLong();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeUTF(accounting);
        out.writeLong(time);
        out.writeUTF(ip_address);
        out.writeCompressedInt(package_definition);
        out.writeUTF(business_name);
        out.writeUTF(business_phone);
        writeNullUTF(out, business_fax);
        out.writeUTF(business_address1);
        writeNullUTF(out, business_address2);
        out.writeUTF(business_city);
        writeNullUTF(out, business_state);
        out.writeUTF(business_country);
        writeNullUTF(out, business_zip);
        out.writeUTF(ba_name);
        writeNullUTF(out, ba_title);
        out.writeUTF(ba_work_phone);
        writeNullUTF(out, ba_cell_phone);
        writeNullUTF(out, ba_home_phone);
        writeNullUTF(out, ba_fax);
        out.writeUTF(ba_email);
        writeNullUTF(out, ba_address1);
        writeNullUTF(out, ba_address2);
        writeNullUTF(out, ba_city);
        writeNullUTF(out, ba_state);
        writeNullUTF(out, ba_country);
        writeNullUTF(out, ba_zip);
        out.writeUTF(ba_username);
        out.writeUTF(billing_contact);
        out.writeUTF(billing_email);
        out.writeBoolean(billing_use_monthly);
        out.writeBoolean(billing_pay_one_year);
        out.writeUTF(encrypted_data);
        out.writeCompressedInt(encryption_key);
        writeNullUTF(out, completed_by);
        out.writeLong(completed_time);
    }

    public Business getBusiness() {
	Business accountingObject = table.connector.businesses.get(accounting);
	if (accountingObject == null) throw new WrappedException(new SQLException("Unable to find Business: " + accounting));
	return accountingObject;
    }

    public long getTime() {
        return time;
    }

    public String getIpAddress() {
        return ip_address;
    }

    public PackageDefinition getPackageDefinition() {
        PackageDefinition pd = table.connector.packageDefinitions.get(package_definition);
        if(pd == null) throw new WrappedException(new SQLException("Unable to find PackageDefinition: "+package_definition));
        return pd;
    }

    public String getBusinessName() {
        return business_name;
    }

    public String getBusinessPhone() {
        return business_phone;
    }

    public String getBusinessFax() {
        return business_fax;
    }

    public String getBusinessAddress1() {
        return business_address1;
    }

    public String getBusinessAddress2() {
        return business_address2;
    }

    public String getBusinessCity() {
        return business_city;
    }

    public String getBusinessState() {
        return business_state;
    }

    public String getBusinessCountry() {
        return business_country;
    }

    public String getBusinessZip() {
        return business_zip;
    }

    public String getBaName() {
        return ba_name;
    }

    public String getBaTitle() {
        return ba_title;
    }

    public String getBaWorkPhone() {
        return ba_work_phone;
    }

    public String getBaCellPhone() {
        return ba_cell_phone;
    }

    public String getBaHomePhone() {
        return ba_home_phone;
    }

    public String getBaFax() {
        return ba_fax;
    }

    public String getBaEmail() {
        return ba_email;
    }

    public String getBaAddress1() {
        return ba_address1;
    }

    public String getBaAddress2() {
        return ba_address2;
    }

    public String getBaCity() {
        return ba_city;
    }

    public String getBaState() {
        return ba_state;
    }

    public String getBaCountry() {
        return ba_country;
    }

    public String getBaZip() {
        return ba_zip;
    }

    public String getBaUsername() {
        return ba_username;
    }

    public String getBillingContact() {
        return billing_contact;
    }

    public String getBillingEmail() {
        return billing_email;
    }

    public boolean getBillingUseMonthly() {
        return billing_use_monthly;
    }

    public boolean getBillingPayOneYear() {
        return billing_pay_one_year;
    }

    public EncryptionKey getEncryptionKey() {
        EncryptionKey ek = table.connector.encryptionKeys.get(encryption_key);
        if(ek == null) throw new WrappedException(new SQLException("Unable to find EncryptionKey: "+encryption_key));
        return ek;
    }

    public BusinessAdministrator getCompletedBy() {
        if(completed_by==null) return null;
        // May be filtered, null is OK
        return table.connector.businessAdministrators.get(completed_by);
    }

    public long getCompletedTime() {
        return completed_time;
    }

    synchronized public String getBaPassword(String passphrase) throws IOException {
        decrypt(passphrase);
        return ba_password;
    }

    synchronized public String getBillingCardholderName(String passphrase) throws IOException {
        decrypt(passphrase);
        return billing_cardholder_name;
    }

    synchronized public String getBillingCardNumber(String passphrase) throws IOException {
        decrypt(passphrase);
        return billing_card_number;
    }

    synchronized public String getBillingExpirationMonth(String passphrase) throws IOException {
        decrypt(passphrase);
        return billing_expiration_month;
    }

    synchronized public String getBillingExpirationYear(String passphrase) throws IOException {
        decrypt(passphrase);
        return billing_expiration_year;
    }

    synchronized public String getBillingStreetAddress(String passphrase) throws IOException {
        decrypt(passphrase);
        return billing_street_address;
    }

    synchronized public String getBillingCity(String passphrase) throws IOException {
        decrypt(passphrase);
        return billing_city;
    }

    synchronized public String getBillingState(String passphrase) throws IOException {
        decrypt(passphrase);
        return billing_state;
    }

    synchronized public String getBillingZip(String passphrase) throws IOException {
        decrypt(passphrase);
        return billing_zip;
    }
    
    synchronized private void decrypt(String passphrase) throws IOException {
        // If a different passphrase is provided, don't use the cached values, clear, and re-decrypt
        if(decryptPassphrase==null || !passphrase.equals(decryptPassphrase)) {
            // Clear first just in case there is a problem in part of the decryption
            decryptPassphrase=null;
            ba_password=null;
            billing_cardholder_name=null;
            billing_card_number=null;
            billing_expiration_month=null;
            billing_expiration_year=null;
            billing_street_address=null;
            billing_city=null;
            billing_state=null;
            billing_zip=null;
            
            // Perform the decryption
            String decrypted = getEncryptionKey().decrypt(encrypted_data, passphrase);
            
            // Parse
            List<String> lines = StringUtility.splitLines(decrypted);
            
            // Store the values
            if(lines.size()==9) {
                // 9-line format
                ba_password=lines.get(0);
                billing_cardholder_name=lines.get(1);
                billing_card_number=lines.get(2);
                billing_expiration_month=lines.get(3);
                billing_expiration_year=lines.get(4);
                billing_street_address=lines.get(5);
                billing_city=lines.get(6);
                billing_state=lines.get(7);
                billing_zip=lines.get(8);
            } else {
                throw new IOException("Unexpected number of lines after decryption: "+lines.size());
            }
            decryptPassphrase=passphrase;
        }
    }
}
