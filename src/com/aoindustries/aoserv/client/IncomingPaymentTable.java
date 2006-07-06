package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  IncomingPayment
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class IncomingPaymentTable extends CachedTableIntegerKey<IncomingPayment> {

    IncomingPaymentTable(AOServConnector connector) {
	super(connector, IncomingPayment.class);
    }

    void addIncomingPayment(
	Transaction transaction,
	byte[] encryptedCardName,
	byte[] encryptedCardNumber,
	byte[] encryptedExpirationMonth,
	byte[] encryptedExpirationYear
    ) {
	connector.requestUpdateIL(
            AOServProtocol.ADD,
            SchemaTable.INCOMING_PAYMENTS,
            transaction.transid,
            encryptedCardName,
            encryptedCardNumber,
            encryptedExpirationMonth,
            encryptedExpirationYear
	);
    }

    public IncomingPayment get(Object pkey) {
	return getUniqueRow(IncomingPayment.COLUMN_TRANSID, pkey);
    }

    public IncomingPayment get(int pkey) {
	return getUniqueRow(IncomingPayment.COLUMN_TRANSID, pkey);
    }

    int getTableID() {
	return SchemaTable.INCOMING_PAYMENTS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_INCOMING_PAYMENT)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_INCOMING_PAYMENT, args, 5, err)) {
                connector.simpleAOClient.addIncomingPayment(
                    AOSH.parseInt(args[1], "transid"),
                    args[2],
                    args[3],
                    args[4],
                    args[5]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_INCOMING_PAYMENT)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_INCOMING_PAYMENT, args, 1, err)) {
                connector.simpleAOClient.removeIncomingPayment(
                    AOSH.parseInt(args[1], "transid")
                );
            }
            return true;
	}
	return false;
    }
}