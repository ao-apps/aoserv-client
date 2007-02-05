package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * When a credit card payment is made, the sensitive data is encrypted
 * and temporarily stored in the database as an <code>IncomingPayment</code>.
 * Once the payment is confirmed, the database entry is removed.
 *
 * @see  Transaction#addIncomingPayment
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class IncomingPayment extends CachedObjectIntegerKey<IncomingPayment> implements Removable {
    
    static final int COLUMN_TRANSID=0;

    private byte[] encryptedCardName, encryptedCardNumber, encryptedExpirationMonth, encryptedExpirationYear;

    public Object getColumn(int i) {
	if(i==COLUMN_TRANSID) return Integer.valueOf(pkey);
	if(i==1) return new String(encryptedCardName);
	if(i==2) return new String(encryptedCardNumber);
	if(i==3) return new String(encryptedExpirationMonth);
	if(i==4) return new String(encryptedExpirationYear);
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public byte[] getEncryptedCardName() {
	return encryptedCardName;
    }

    public byte[] getEncryptedCardNumber() {
	return encryptedCardNumber;
    }

    public byte[] getEncryptedExpirationMonth() {
	return encryptedExpirationMonth;
    }

    public byte[] getEncryptedExpirationYear() {
	return encryptedExpirationYear;
    }

    protected int getTableIDImpl() {
	return SchemaTable.INCOMING_PAYMENTS;
    }

    public Transaction getTransaction() {
	Transaction transaction = table.connector.transactions.get(pkey);
	if (transaction == null) throw new WrappedException(new SQLException("Unable to find Transaction: " + pkey));
	return transaction;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getInt(1);
	encryptedCardName = result.getString(2).getBytes();
	encryptedCardNumber = result.getString(3).getBytes();
	encryptedExpirationMonth = result.getString(4).getBytes();
	encryptedExpirationYear = result.getString(5).getBytes();
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	in.readFully(encryptedCardName=new byte[in.readCompressedInt()]);
	in.readFully(encryptedCardNumber=new byte[in.readCompressedInt()]);
	in.readFully(encryptedExpirationMonth=new byte[in.readCompressedInt()]);
	in.readFully(encryptedExpirationYear=new byte[in.readCompressedInt()]);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.INCOMING_PAYMENTS,
            pkey
	);
    }

    String toStringImpl() {
        Transaction trans=getTransaction();
        return pkey+"|"+trans.accounting+"|$"+SQLUtility.getDecimal(-trans.getRate());
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(encryptedCardName.length);
	out.write(encryptedCardName);
	out.writeCompressedInt(encryptedCardNumber.length);
	out.write(encryptedCardNumber);
	out.writeCompressedInt(encryptedExpirationMonth.length);
	out.write(encryptedExpirationMonth);
	out.writeCompressedInt(encryptedExpirationYear.length);
	out.write(encryptedExpirationYear);
    }
}