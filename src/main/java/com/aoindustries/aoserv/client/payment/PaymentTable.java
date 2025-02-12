/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Money;
import com.aoapps.net.Email;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

/**
 * @see  Payment
 *
 * @author  AO Industries, Inc.
 */
public final class PaymentTable extends CachedTableIntegerKey<Payment> {

  PaymentTable(AoservConnector connector) {
    super(connector, Payment.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Payment.COLUMN_ACCOUNTING_name, ASCENDING),
      new OrderBy(Payment.COLUMN_AUTHORIZATION_TIME_name, ASCENDING),
      new OrderBy(Payment.COLUMN_PKEY_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addPayment(
      final Processor processor,
      final Account account,
      final String groupName,
      final boolean testMode,
      final int duplicateWindow,
      final String orderNumber,
      final Money amount,
      final Money taxAmount,
      final boolean taxExempt,
      final Money shippingAmount,
      final Money dutyAmount,
      final String shippingFirstName,
      final String shippingLastName,
      final String shippingCompanyName,
      final String shippingStreetAddress1,
      final String shippingStreetAddress2,
      final String shippingCity,
      final String shippingState,
      final String shippingPostalCode,
      final String shippingCountryCode,
      final boolean emailCustomer,
      final Email merchantEmail,
      final String invoiceNumber,
      final String purchaseOrderNumber,
      final String description,
      final Administrator creditCardCreatedBy,
      final String creditCardPrincipalName,
      final Account creditCardAccounting,
      final String creditCardGroupName,
      final String creditCardProviderUniqueId,
      final String creditCardMaskedCardNumber,
      final Byte creditCard_expirationMonth,
      final Short creditCard_expirationYear,
      final String creditCardFirstName,
      final String creditCardLastName,
      final String creditCardCompanyName,
      final Email creditCardEmail,
      final String creditCardPhone,
      final String creditCardFax,
      final String creditCardCustomerId,
      final String creditCardCustomerTaxId,
      final String creditCardStreetAddress1,
      final String creditCardStreetAddress2,
      final String creditCardCity,
      final String creditCardState,
      final String creditCardPostalCode,
      final String creditCardCountryCode,
      final String creditCardComments,
      final long authorizationTime,
      final String authorizationPrincipalName
  ) throws IOException, SQLException {
    if (!connector.isSecure()) {
      throw new IOException("Credit card transactions may only be added when using secure protocols.  Currently using the " + connector.getProtocol() + " protocol, which is not secure.");
    }

    final Currency currency = amount.getCurrency();
    if (taxAmount != null && taxAmount.getCurrency() != currency) {
      throw new SQLException("Currency mismatch: amount.currency = " + currency + ", taxAmount.currency = " + taxAmount.getCurrency());
    }
    if (shippingAmount != null && shippingAmount.getCurrency() != currency) {
      throw new SQLException("Currency mismatch: amount.currency = " + currency + ", shippingAmount.currency = " + shippingAmount.getCurrency());
    }
    if (dutyAmount != null && dutyAmount.getCurrency() != currency) {
      throw new SQLException("Currency mismatch: amount.currency = " + currency + ", dutyAmount.currency = " + dutyAmount.getCurrency());
    }
    return connector.requestResult(
        true,
        AoservProtocol.CommandId.ADD,
        new AoservConnector.ResultRequest<>() {
          private int pkey;
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableId.CREDIT_CARD_TRANSACTIONS.ordinal());
            out.writeUTF(processor.getProviderId());
            out.writeUTF(account.getName().toString());
            out.writeNullUTF(groupName);
            out.writeBoolean(testMode);
            out.writeCompressedInt(duplicateWindow);
            out.writeNullUTF(orderNumber);
            out.writeUTF(currency.getCurrencyCode());
            out.writeLong(amount.getUnscaledValue());
            out.writeCompressedInt(amount.getScale());
            if (taxAmount != null) {
              out.writeBoolean(true);
              out.writeLong(taxAmount.getUnscaledValue());
              out.writeCompressedInt(taxAmount.getScale());
            } else {
              out.writeBoolean(false);
            }
            out.writeBoolean(taxExempt);
            if (shippingAmount != null) {
              out.writeBoolean(true);
              out.writeLong(shippingAmount.getUnscaledValue());
              out.writeCompressedInt(shippingAmount.getScale());
            } else {
              out.writeBoolean(false);
            }
            if (dutyAmount != null) {
              out.writeBoolean(true);
              out.writeLong(dutyAmount.getUnscaledValue());
              out.writeCompressedInt(dutyAmount.getScale());
            } else {
              out.writeBoolean(false);
            }
            out.writeNullUTF(shippingFirstName);
            out.writeNullUTF(shippingLastName);
            out.writeNullUTF(shippingCompanyName);
            out.writeNullUTF(shippingStreetAddress1);
            out.writeNullUTF(shippingStreetAddress2);
            out.writeNullUTF(shippingCity);
            out.writeNullUTF(shippingState);
            out.writeNullUTF(shippingPostalCode);
            out.writeNullUTF(shippingCountryCode);
            out.writeBoolean(emailCustomer);
            out.writeNullUTF(Objects.toString(merchantEmail, null));
            out.writeNullUTF(invoiceNumber);
            out.writeNullUTF(purchaseOrderNumber);
            out.writeNullUTF(description);
            out.writeUTF(creditCardCreatedBy.getUsername_userId().toString());
            out.writeNullUTF(creditCardPrincipalName);
            out.writeUTF(creditCardAccounting.getName().toString());
            out.writeNullUTF(creditCardGroupName);
            out.writeNullUTF(creditCardProviderUniqueId);
            out.writeUTF(creditCardMaskedCardNumber);
            out.writeNullByte(creditCard_expirationMonth);
            out.writeNullShort(creditCard_expirationYear);
            out.writeUTF(creditCardFirstName);
            out.writeUTF(creditCardLastName);
            out.writeNullUTF(creditCardCompanyName);
            out.writeNullUTF(Objects.toString(creditCardEmail, null));
            out.writeNullUTF(creditCardPhone);
            out.writeNullUTF(creditCardFax);
            out.writeNullUTF(creditCardCustomerId);
            out.writeNullUTF(creditCardCustomerTaxId);
            out.writeUTF(creditCardStreetAddress1);
            out.writeNullUTF(creditCardStreetAddress2);
            out.writeUTF(creditCardCity);
            out.writeNullUTF(creditCardState);
            out.writeNullUTF(creditCardPostalCode);
            out.writeUTF(creditCardCountryCode);
            out.writeNullUTF(creditCardComments);
            out.writeLong(authorizationTime);
            out.writeNullUTF(authorizationPrincipalName);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              pkey = in.readCompressedInt();
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unknown response code: " + code);
            }
          }

          @Override
          public Integer afterRelease() {
            connector.tablesUpdated(invalidateList);
            return pkey;
          }
        }
    );
  }

  @Override
  public Payment get(int pkey) throws IOException, SQLException {
    return getUniqueRow(Payment.COLUMN_PKEY, pkey);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.CREDIT_CARD_TRANSACTIONS;
  }

  public Payment getLastCreditCardTransaction(Account bu) throws IOException, SQLException {
    Account.Name accounting = bu.getName();
    // Sorted by accounting, time, so we search for first match from the bottom
    // TODO: We could do a binary search on accounting code and time to make this faster
    List<Payment> ccts = getRows();
    for (int c = ccts.size() - 1; c >= 0; c--) {
      Payment cct = ccts.get(c);
      if (cct.getAccount_name().equals(accounting)) {
        return cct;
      }
    }
    return null;
  }
}
