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
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Stores the list of encryption keys for a business.  The keys themselves are GPG keys
 * and are stored by GPG.
 *
 * @see  Business
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EncryptionKey extends CachedObjectIntegerKey<EncryptionKey> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ACCOUNTING=1
    ;

    /**
     * Uses the provided public key to encrypt the data.
     */
    public static String encrypt(String signer, String recipient, String plaintext) throws IOException {
        String[] command = {
            "/usr/bin/gpg",
            "--batch",
            "--sign",
            "--encrypt",
            "--armor",
            "--default-key ", '='+signer,
            "--recipient", '='+recipient
        };
        Process P = Runtime.getRuntime().exec(command);
        try {
            Writer out = new OutputStreamWriter(P.getOutputStream());
            try {
                out.write(plaintext);
            } finally {
                out.flush();
                out.close();
            }
            
            // Read the encrypted form
            Reader in = new InputStreamReader(P.getInputStream());
            try {
                StringBuilder sb = new StringBuilder();
                char[] buff = new char[4096];
                int count;
                while((count=in.read(buff, 0, 4096))!=-1) {
                    sb.append(buff, 0, count);
                }
                return sb.toString();
            } finally {
                in.close();
            }
        } finally {
            try {
                int retCode = P.waitFor();
                if(retCode!=0) throw new IOException("Non-zero exit value from gpg: "+retCode);
            } catch(InterruptedException err) {
                throw new InterruptedIOException("Interrupted while waiting for gpg");
            }
        }
    }
    
    /**
     * Uses the provided private key to decrypt the data.
     */
    public static String decrypt(String recipient, String ciphertext, String passphrase) throws IOException {
        String[] command = {
            "/usr/bin/gpg",
            "--batch",
            "--decrypt",
            "--armor",
            "--passphrase-fd", "0"
        };
        Process P = Runtime.getRuntime().exec(command);
        try {
            Writer out = new OutputStreamWriter(P.getOutputStream());
            try {
                out.write(passphrase);
                out.write(ciphertext);
            } finally {
                out.flush();
                out.close();
            }
            
            // Read the decrypted form
            Reader in = new InputStreamReader(P.getInputStream());
            try {
                StringBuilder sb = new StringBuilder();
                char[] buff = new char[4096];
                int count;
                while((count=in.read(buff, 0, 4096))!=-1) {
                    sb.append(buff, 0, count);
                }
                return sb.toString();
            } finally {
                in.close();
            }
        } finally {
            try {
                int retCode = P.waitFor();
                if(retCode!=0) throw new IOException("Non-zero exit value from gpg: "+retCode);
            } catch(InterruptedException err) {
                throw new InterruptedIOException("Interrupted while waiting for gpg");
            }
        }
    }

    String accounting;
    private String id;
    private boolean signup_signer;
    private boolean signup_recipient;
    private boolean credit_card_signer;
    private boolean credit_card_recipient;

    public Business getBusiness() {
	Business accountingObject = table.connector.businesses.get(accounting);
	if (accountingObject == null) throw new WrappedException(new SQLException("Unable to find Business: " + accounting));
	return accountingObject;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_ACCOUNTING: return accounting;
            case 2: return id;
            case 3: return signup_signer;
            case 4: return signup_recipient;
            case 5: return credit_card_signer;
            case 6: return credit_card_recipient;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    /**
     * Gets the id of the key use for GPG.  The id must be unique per business.
     */
    public String getId() {
        return id;
    }

    /**
     * Indicates this key may be used as the signer for signup request encryption.
     */
    public boolean getSignupSigner() {
        return signup_signer;
    }
    
    /**
     * Indicates this key may be used as the recipient for signup request encryption.
     */
    public boolean getSignupRecipient() {
        return signup_recipient;
    }

    /**
     * Indicates this key may be used as the signer for encrypting credit cards.
     */
    public boolean getCreditCardSigner() {
        return credit_card_signer;
    }
    
    /**
     * Indicates this key may be used as the recipient for encrypted credit cards.
     */
    public boolean getCreditCardRecipient() {
        return credit_card_recipient;
    }

    protected int getTableIDImpl() {
	return SchemaTable.ENCRYPTION_KEYS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
	accounting = result.getString(2);
        id = result.getString(3);
        signup_signer = result.getBoolean(4);
        signup_recipient = result.getBoolean(5);
        credit_card_signer = result.getBoolean(6);
        credit_card_recipient = result.getBoolean(7);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	accounting=in.readUTF();
        id = in.readUTF();
        signup_signer=in.readBoolean();
        signup_recipient=in.readBoolean();
        credit_card_signer=in.readBoolean();
        credit_card_recipient=in.readBoolean();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeUTF(accounting);
        out.writeUTF(id);
        out.writeBoolean(signup_signer);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_25)>=0) out.writeBoolean(signup_recipient);
        out.writeBoolean(credit_card_signer);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_25)>=0) out.writeBoolean(credit_card_recipient);
    }
    
    /**
     * Encrypts a message for the provided recipient and signs with the private key.
     */
    public String encrypt(EncryptionKey recipient, String plaintext) throws IOException {
        return encrypt(id, recipient.getId(), plaintext);
    }
    
    /**
     * Uses the private key to decrypt the data and verifies the signature.
     */
    public String decrypt(String ciphertext, String passphrase) throws IOException {
        return decrypt(id, ciphertext, passphrase);
    }
}
