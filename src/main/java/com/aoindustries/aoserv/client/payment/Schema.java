/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.payment;

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.AoservTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

  private final CountryCodeTable CountryCode;

  public CountryCodeTable getCountryCode() {
    return CountryCode;
  }

  private final CreditCardTable CreditCard;

  public CreditCardTable getCreditCard() {
    return CreditCard;
  }

  private final PaymentTable Payment;

  public PaymentTable getPayment() {
    return Payment;
  }

  private final PaymentTypeTable PaymentType;

  public PaymentTypeTable getPaymentType() {
    return PaymentType;
  }

  private final ProcessorTable Processor;

  public ProcessorTable getProcessor() {
    return Processor;
  }

  private final List<? extends AoservTable<?, ?>> tables;

  public Schema(AoservConnector connector) {
    super(connector);

    ArrayList<AoservTable<?, ?>> newTables = new ArrayList<>();
    newTables.add(CountryCode = new CountryCodeTable(connector));
    newTables.add(CreditCard = new CreditCardTable(connector));
    newTables.add(Payment = new PaymentTable(connector));
    newTables.add(PaymentType = new PaymentTypeTable(connector));
    newTables.add(Processor = new ProcessorTable(connector));
    newTables.trimToSize();
    tables = Collections.unmodifiableList(newTables);
  }

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
  public List<? extends AoservTable<?, ?>> getTables() {
    return tables;
  }

  @Override
  public String getName() {
    return "payment";
  }
}
