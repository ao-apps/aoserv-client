/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  PaymentType
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.payment_types)
public interface PaymentTypeService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,PaymentType> {

    /* TODO
    public PaymentType getCreditCardType(String card_number) throws SQLException, IOException {
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
	return paymentType;
    }
     */
}