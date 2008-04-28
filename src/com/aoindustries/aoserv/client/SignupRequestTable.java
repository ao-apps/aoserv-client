package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @see  SignupRequest
 *
 * @author  AO Industries, Inc.
 */
final public class SignupRequestTable extends CachedTableIntegerKey<SignupRequest> {

    SignupRequestTable(AOServConnector connector) {
	super(connector, SignupRequest.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(SignupRequest.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(SignupRequest.COLUMN_TIME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SignupRequest get(Object pkey) {
	return getUniqueRow(SignupRequest.COLUMN_PKEY, pkey);
    }

    public SignupRequest get(int pkey) {
	return getUniqueRow(SignupRequest.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SIGNUP_REQUESTS;
    }
    
    /**
     * Encrypts the signup request details and adds to the master database.  The first encryption key flagged to use for signup_from is used as the from
     * and the first key flagged to use as signup_recipient as the recipient.
     */
    public int addSignupRequest(
        Business business,
        String ip_address,
        PackageDefinition package_definition,
        String business_name,
        String business_phone,
        String business_fax,
        String business_address1,
        String business_address2,
        String business_city,
        String business_state,
        CountryCode business_country,
        String business_zip,
        String ba_name,
        String ba_title,
        String ba_work_phone,
        String ba_cell_phone,
        String ba_home_phone,
        String ba_fax,
        String ba_email,
        String ba_address1,
        String ba_address2,
        String ba_city,
        String ba_state,
        CountryCode ba_country,
        String ba_zip,
        String ba_username,
        String billing_contact,
        String billing_email,
        boolean billing_use_monthly,
        boolean billing_pay_one_year,
        // Encrypted values
        String ba_password,
        String billing_cardholder_name,
        String billing_card_number,
        String billing_expiration_month,
        String billing_expiration_year,
        String billing_street_address,
        String billing_city,
        String billing_state,
        String billing_zip,
        // options
        Map<String,String> options
    ) throws IOException, SQLException {
        // Validate the encrypted parameters
        if(ba_password==null) throw new NullPointerException("ba_password is null");
        if(ba_password.indexOf('\n')!=-1) throw new IllegalArgumentException("ba_password may not contain '\n'");
        if(billing_cardholder_name==null) throw new NullPointerException("billing_cardholder_name is null");
        if(billing_cardholder_name.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_cardholder_name may not contain '\n'");
        if(billing_card_number==null) throw new NullPointerException("billing_card_number is null");
        if(billing_card_number.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_card_number may not contain '\n'");
        if(billing_expiration_month==null) throw new NullPointerException("billing_expiration_month is null");
        if(billing_expiration_month.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_expiration_month may not contain '\n'");
        if(billing_expiration_year==null) throw new NullPointerException("billing_expiration_year is null");
        if(billing_expiration_year.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_expiration_year may not contain '\n'");
        if(billing_street_address==null) throw new NullPointerException("billing_street_address is null");
        if(billing_street_address.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_street_address may not contain '\n'");
        if(billing_city==null) throw new NullPointerException("billing_city is null");
        if(billing_city.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_city may not contain '\n'");
        if(billing_state==null) throw new NullPointerException("billing_state is null");
        if(billing_state.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_state may not contain '\n'");
        if(billing_zip==null) throw new NullPointerException("billing_zip is null");
        if(billing_zip.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_zip may not contain '\n'");

        // Find the from and recipient keys
        EncryptionKey from = null;
        EncryptionKey recipient = null;
        String accounting = business.getAccounting();
        for(EncryptionKey encryptionKey : connector.getEncryptionKeys().getRows()) {
            if(encryptionKey.accounting.equals(accounting)) {
                if(from==null && encryptionKey.getSignupFrom()) from = encryptionKey;
                if(recipient==null && encryptionKey.getSignupRecipient()) recipient = encryptionKey;
                if(from!=null && recipient!=null) break;
            }
        }
        if(from==null) throw new SQLException("Unable to find signup from for accounting="+accounting);
        if(recipient==null) throw new SQLException("Unable to find signup recipient for accounting="+accounting);

        // Encrypt the message
        String plaintext =
            ba_password + "\n"
            + billing_cardholder_name + "\n"
            + billing_card_number + "\n"
            + billing_expiration_month + "\n"
            + billing_expiration_year + "\n"
            + billing_street_address + "\n"
            + billing_city + "\n"
            + billing_state + "\n"
            + billing_zip + "\n"
        ;
        String ciphertext = from.encrypt(recipient, plaintext);

        // Send the request to the master server
        IntList invalidateList;
        AOServConnection connection=connector.getConnection();
        int pkey;
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
            out.writeCompressedInt(SchemaTable.TableID.SIGNUP_REQUESTS.ordinal());
            out.writeUTF(accounting);
            out.writeUTF(ip_address);
            out.writeCompressedInt(package_definition.getPkey());
            out.writeUTF(business_name);
            out.writeUTF(business_phone);
            out.writeBoolean(business_fax!=null); if(business_fax!=null) out.writeUTF(business_fax);
            out.writeUTF(business_address1);
            out.writeBoolean(business_address2!=null); if(business_address2!=null) out.writeUTF(business_address2);
            out.writeUTF(business_city);
            out.writeBoolean(business_state!=null); if(business_state!=null) out.writeUTF(business_state);
            out.writeUTF(business_country.getCode());
            out.writeBoolean(business_zip!=null); if(business_zip!=null) out.writeUTF(business_zip);
            out.writeUTF(ba_name);
            out.writeBoolean(ba_title!=null); if(ba_title!=null) out.writeUTF(ba_title);
            out.writeUTF(ba_work_phone);
            out.writeBoolean(ba_cell_phone!=null); if(ba_cell_phone!=null) out.writeUTF(ba_cell_phone);
            out.writeBoolean(ba_home_phone!=null); if(ba_home_phone!=null) out.writeUTF(ba_home_phone);
            out.writeBoolean(ba_fax!=null); if(ba_fax!=null) out.writeUTF(ba_fax);
            out.writeUTF(ba_email);
            out.writeBoolean(ba_address1!=null); if(ba_address1!=null) out.writeUTF(ba_address1);
            out.writeBoolean(ba_address2!=null); if(ba_address2!=null) out.writeUTF(ba_address2);
            out.writeBoolean(ba_city!=null); if(ba_city!=null) out.writeUTF(ba_city);
            out.writeBoolean(ba_state!=null); if(ba_state!=null) out.writeUTF(ba_state);
            out.writeBoolean(ba_country!=null); if(ba_country!=null) out.writeUTF(ba_country.getCode());
            out.writeBoolean(ba_zip!=null); if(ba_zip!=null) out.writeUTF(ba_zip);
            out.writeUTF(ba_username);
            out.writeUTF(billing_contact);
            out.writeUTF(billing_email);
            out.writeBoolean(billing_use_monthly);
            out.writeBoolean(billing_pay_one_year);
            // Encrypted values
            out.writeCompressedInt(from.getPkey());
            out.writeCompressedInt(recipient.getPkey());
            out.writeUTF(ciphertext);
            // options
            int numOptions = options.size();
            out.writeCompressedInt(numOptions);
            int optionCount = 0;
            for(String name : options.keySet()) {
                out.writeUTF(name);
                String value = options.get(name);
                out.writeBoolean(value!=null); if(value!=null) out.writeUTF(value);
                optionCount++;
            }
            if(optionCount!=numOptions) throw new IOException("options modified while writing to master");
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) {
                pkey=in.readCompressedInt();
                invalidateList=AOServConnector.readInvalidateList(in);
            } else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: "+code);
            }
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            connector.releaseConnection(connection);
        }
        connector.tablesUpdated(invalidateList);
        return pkey;
    }
}
