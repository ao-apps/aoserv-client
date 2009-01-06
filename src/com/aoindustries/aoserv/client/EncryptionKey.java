package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.WrappedException;
import java.io.CharArrayWriter;
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
    static final String COLUMN_ACCOUNTING_name = "accounting";
    static final String COLUMN_ID_name = "id";

    /*
     * Test call
    public static void main(String[] args) {
        try {
            System.out.print(
                encrypt(
                    "AO Industries, Inc. (Development) <webmaster@freedom.aoindustries.com>",
                    "AO Industries, Inc. (Accounting) <accounting@aoindustries.com>",
                    "Test message"
                )
            );
        } catch(IOException err) {
            err.printStackTrace();
        }
    }
     */

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
            "--default-key", '='+signer,
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
            // Read the standard error
            CharArrayWriter cout = new CharArrayWriter();
            Reader errIn = new InputStreamReader(P.getErrorStream());
            try {
                char[] buff = new char[4096];
                int ret;
                while((ret=errIn.read(buff, 0, 4096))!=-1) cout.write(buff, 0, ret);
            } finally {
                errIn.close();
            }
            try {
                int retCode = P.waitFor();
                if(retCode!=0) throw new IOException("Non-zero exit value from gpg: "+retCode+", standard error was: "+cout.toString());
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
    private boolean signup_from;
    private boolean signup_recipient;

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
            case 3: return signup_from;
            case 4: return signup_recipient;
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
     * Indicates this key may be used as the from (signer) for signup request encryption.
     */
    public boolean getSignupFrom() {
        return signup_from;
    }
    
    /**
     * Indicates this key may be used as the recipient for signup request encryption.
     */
    public boolean getSignupRecipient() {
        return signup_recipient;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.ENCRYPTION_KEYS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
	accounting = result.getString(2);
        id = result.getString(3);
        signup_from = result.getBoolean(4);
        signup_recipient = result.getBoolean(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	accounting=in.readUTF().intern();
        id = in.readUTF();
        signup_from=in.readBoolean();
        signup_recipient=in.readBoolean();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
	out.writeUTF(accounting);
        out.writeUTF(id);
        out.writeBoolean(signup_from); // Used to be signup_signer
        if(version.compareTo(AOServProtocol.Version.VERSION_1_25)>=0) out.writeBoolean(signup_recipient);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) out.writeBoolean(false); // credit_card_signer
        if(
            version.compareTo(AOServProtocol.Version.VERSION_1_25)>=0
            && version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0
        ) out.writeBoolean(false); // credit_card_recipient
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
