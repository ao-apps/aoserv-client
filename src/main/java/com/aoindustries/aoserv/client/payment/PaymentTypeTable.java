/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.payment;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableStringKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  PaymentType
 *
 * @author  AO Industries, Inc.
 */
final public class PaymentTypeTable extends GlobalTableStringKey<PaymentType> {

	public PaymentTypeTable(AOServConnector connector) {
		super(connector, PaymentType.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PaymentType.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public PaymentType get(String name) throws IOException, SQLException {
		return getUniqueRow(PaymentType.COLUMN_NAME, name);
	}

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
		if (paymentType == null) throw new SQLException("Unable to find payment_type");
		return paymentType;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.PAYMENT_TYPES;
	}
}
