/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.pki;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
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
 * @see  Account
 *
 * @author  AO Industries, Inc.
 */
public final class EncryptionKey extends CachedObjectIntegerKey<EncryptionKey> {

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
	@SuppressWarnings("ThrowFromFinallyBlock")
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
		Process p = Runtime.getRuntime().exec(command);
		try {
			try (Writer out = new OutputStreamWriter(p.getOutputStream())) {
				out.write(plaintext);
				out.flush();
			}

			// Read the encrypted form
			try(Reader in = new InputStreamReader(p.getInputStream())) {
				StringBuilder sb = new StringBuilder();
				char[] buff = new char[4096];
				int count;
				while((count=in.read(buff, 0, 4096))!=-1) {
					sb.append(buff, 0, count);
				}
				return sb.toString();
			}
		} finally {
			// Read the standard error
			CharArrayWriter cout = new CharArrayWriter();
			try (Reader errIn = new InputStreamReader(p.getErrorStream())) {
				char[] buff = new char[4096];
				int ret;
				while((ret=errIn.read(buff, 0, 4096))!=-1) {
					cout.write(buff, 0, ret);
				}
			}
			try {
				int retCode = p.waitFor();
				if(retCode != 0) {
					throw new IOException(
						"Non-zero exit value from gpg: " + retCode + ", standard error was: " + cout.toString()
					);
				}
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException("Interrupted while waiting for gpg");
				ioErr.initCause(err);
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				throw ioErr;
			}
		}
	}

	/**
	 * Uses the provided private key to decrypt the data.
	 */
	@SuppressWarnings("ThrowFromFinallyBlock")
	public static String decrypt(String recipient, String ciphertext, String passphrase) throws IOException {
		String[] command = {
			"/usr/bin/gpg",
			"--batch",
			"--decrypt",
			"--armor",
			"--passphrase-fd", "0"
		};
		Process p = Runtime.getRuntime().exec(command);
		try {
			try (Writer out = new OutputStreamWriter(p.getOutputStream())) {
				out.write(passphrase);
				out.write(ciphertext);
				out.flush();
			}

			// Read the decrypted form
			try (Reader in = new InputStreamReader(p.getInputStream())) {
				StringBuilder sb = new StringBuilder();
				char[] buff = new char[4096];
				int count;
				while((count=in.read(buff, 0, 4096))!=-1) {
					sb.append(buff, 0, count);
				}
				return sb.toString();
			}
		} finally {
			try {
				int retCode = p.waitFor();
				if(retCode != 0) {
					throw new IOException("Non-zero exit value from gpg: " + retCode);
				}
			} catch(InterruptedException err) {
				InterruptedIOException ioErr = new InterruptedIOException("Interrupted while waiting for gpg");
				ioErr.initCause(err);
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				throw ioErr;
			}
		}
	}

	private Account.Name accounting;
	private String id;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public EncryptionKey() {
		// Do nothing
	}

	public Account.Name getAccount_name() {
		return accounting;
	}

	public Account getAccount() throws SQLException, IOException {
		Account obj = table.getConnector().getAccount().getAccount().get(accounting);
		if (obj == null) throw new SQLException("Unable to find Account: " + accounting);
		return obj;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_ACCOUNTING: return accounting;
			case 2: return id;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	/**
	 * Gets the id of the key use for GPG.  The id must be unique per business.
	 */
	public String getId() {
		return id;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.ENCRYPTION_KEYS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			accounting = Account.Name.valueOf(result.getString(2));
			id = result.getString(3);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey=in.readCompressedInt();
			accounting = Account.Name.valueOf(in.readUTF()).intern();
			id = in.readUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(accounting.toString());
		out.writeUTF(id);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43)<=0) out.writeBoolean(false); // signup_from / signup_signer
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_25)>=0 && protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43)<=0) out.writeBoolean(false); // signup_recipient
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) out.writeBoolean(false); // credit_card_signer
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_25)>=0 && protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) out.writeBoolean(false); // credit_card_recipient
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
