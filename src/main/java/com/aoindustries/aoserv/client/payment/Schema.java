/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AOServTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

	private final CountryCodeTable countryCodes;
	public CountryCodeTable getCountryCodes() {return countryCodes;}

	private final CreditCardTable creditCards;
	public CreditCardTable getCreditCards() {return creditCards;}

	private final PaymentTable creditCardTransactions;
	public PaymentTable getCreditCardTransactions() {return creditCardTransactions;}

	private final PaymentTypeTable paymentTypes;
	public PaymentTypeTable getPaymentTypes() {return paymentTypes;}

	private final ProcessorTable creditCardProcessors;
	public ProcessorTable getCreditCardProcessors() {return creditCardProcessors;}

	final List<? extends AOServTable<?,?>> tables;

	public Schema(AOServConnector connector) throws IOException {
		super(connector);

		ArrayList<AOServTable<?,?>> newTables = new ArrayList<>();
		newTables.add(countryCodes = new CountryCodeTable(connector));
		newTables.add(creditCards = new CreditCardTable(connector));
		newTables.add(creditCardTransactions = new PaymentTable(connector));
		newTables.add(paymentTypes = new PaymentTypeTable(connector));
		newTables.add(creditCardProcessors = new ProcessorTable(connector));
		newTables.trimToSize();
		tables = Collections.unmodifiableList(newTables);
	}

	@Override
	public List<? extends AOServTable<?,?>> getTables() {
		return tables;
	}

	@Override
	public String getName() {
		return "payment";
	}
}
