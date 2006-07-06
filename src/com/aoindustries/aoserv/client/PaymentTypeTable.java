package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  PaymentType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PaymentTypeTable extends GlobalTableStringKey<PaymentType> {

    PaymentTypeTable(AOServConnector connector) {
	super(connector, PaymentType.class);
    }

    public PaymentType get(Object pkey) {
	return getUniqueRow(PaymentType.COLUMN_NAME, pkey);
    }

    public PaymentType getCreditCardType(String card_number) {
	// Build the list of numbers
	StringBuilder numbers = new StringBuilder();

	// A card number should only contain 0-9, -, or space and needs at least
	int len = card_number.length();
	for (int c = 0; c < len; c++) {
            char ch = card_number.charAt(c);
            if (ch >= '0' && ch <= '9') numbers.append(ch);
            else if (ch != '-' && ch != ' ') throw new IllegalArgumentException("Invalid character in card number: " + ch);
	}

	// Get card type
	PaymentType paymentType=null;
	if (
            numbers.length() >= 2
            && numbers.charAt(0) == '3'
            && (numbers.charAt(1) == '4' || numbers.charAt(1) == '7')
        ) paymentType = get(PaymentType.AMEX);
	else if (
            numbers.length() >= 4
            && numbers.charAt(0) == '6'
            && numbers.charAt(1) == '0'
            && numbers.charAt(2) == '1'
            && numbers.charAt(3) == '1'
        ) paymentType = get(PaymentType.DISCOVER);
	else if (
            numbers.length() >= 2
            && numbers.charAt(0) == '5'
            && numbers.charAt(1) >= '1'
            && numbers.charAt(1) <= '5'
        ) paymentType = get(PaymentType.MASTERCARD);
	else if (numbers.length() >= 1 && numbers.charAt(0) == '4') paymentType = get(PaymentType.VISA);
	else throw new IllegalArgumentException("Unable to determine card type.");
	if (paymentType == null) throw new WrappedException(new SQLException("Unable to find payment_type"));
	return paymentType;
    }

    int getTableID() {
	return SchemaTable.PAYMENT_TYPES;
    }
}