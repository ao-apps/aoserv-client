/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.net.Email;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.pki.EncryptionKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @see  CreditCard
 *
 * @author  AO Industries, Inc.
 */
public final class CreditCardTable extends CachedTableIntegerKey<CreditCard> {

  CreditCardTable(AOServConnector connector) {
    super(connector, CreditCard.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(CreditCard.COLUMN_ACCOUNTING_name, ASCENDING),
      new OrderBy(CreditCard.COLUMN_CREATED_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addCreditCard(
      final Processor processor,
      final Account business,
      final String groupName,
      final String cardInfo,
      final byte expirationMonth,
      final short expirationYear,
      final String providerUniqueId,
      final String firstName,
      final String lastName,
      final String companyName,
      final Email email,
      final String phone,
      final String fax,
      final String customerId,
      final String customerTaxId,
      final String streetAddress1,
      final String streetAddress2,
      final String city,
      final String state,
      final String postalCode,
      final CountryCode countryCode,
      final String principalName,
      final String description,
      // Encrypted values
      String card_number
  ) throws IOException, SQLException {
    // Validate the encrypted parameters
    if (card_number == null) {
      throw new NullPointerException("billing_card_number is null");
    }
    if (card_number.indexOf('\n') != -1) {
      throw new IllegalArgumentException("billing_card_number may not contain '\n'");
    }

    if (!connector.isSecure()) {
      throw new IOException("Credit cards may only be added when using secure protocols.  Currently using the " + connector.getProtocol() + " protocol, which is not secure.");
    }

    // Encrypt if currently configured to
    final EncryptionKey encryptionFrom = processor.getEncryptionFrom();
    final EncryptionKey encryptionRecipient = processor.getEncryptionRecipient();
    final String encryptedCardNumber;
    if (encryptionFrom != null && encryptionRecipient != null) {
      // Encrypt the card number
      encryptedCardNumber = encryptionFrom.encrypt(encryptionRecipient, CreditCard.randomize(card_number));
    } else {
      encryptedCardNumber = null;
    }

    return connector.requestResult(
        true,
        AoservProtocol.CommandID.ADD,
        // Java 9: new AOServConnector.ResultRequest<>
        new AOServConnector.ResultRequest<Integer>() {
          private int pkey;
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableID.CREDIT_CARDS.ordinal());
            out.writeUTF(processor.getProviderId());
            out.writeUTF(business.getName().toString());
            out.writeNullUTF(groupName);
            out.writeUTF(cardInfo);
            out.writeByte(expirationMonth);
            out.writeShort(expirationYear);
            out.writeUTF(providerUniqueId);
            out.writeUTF(firstName);
            out.writeUTF(lastName);
            out.writeNullUTF(companyName);
            out.writeNullUTF(Objects.toString(email, null));
            out.writeNullUTF(phone);
            out.writeNullUTF(fax);
            out.writeNullUTF(customerId);
            out.writeNullUTF(customerTaxId);
            out.writeUTF(streetAddress1);
            out.writeNullUTF(streetAddress2);
            out.writeUTF(city);
            out.writeNullUTF(state);
            out.writeNullUTF(postalCode);
            out.writeUTF(countryCode.getCode());
            out.writeNullUTF(principalName);
            out.writeNullUTF(description);
            out.writeNullUTF(encryptedCardNumber);
            out.writeCompressedInt(encryptionFrom == null ? -1 : encryptionFrom.getPkey());
            out.writeCompressedInt(encryptionRecipient == null ? -1 : encryptionRecipient.getPkey());
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              pkey = in.readCompressedInt();
              invalidateList = AOServConnector.readInvalidateList(in);
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
  public CreditCard get(int pkey) throws SQLException, IOException {
    return getUniqueRow(CreditCard.COLUMN_PKEY, pkey);
  }

  public List<CreditCard> getCreditCards(Account business) throws IOException, SQLException {
    return getIndexedRows(CreditCard.COLUMN_ACCOUNTING, business.getName());
  }

  List<CreditCard> getCreditCards(Processor processor) throws IOException, SQLException {
    return getIndexedRows(CreditCard.COLUMN_PROCESSOR_ID, processor.getProviderId());
  }

  /**
   * Gets the active credit card with the highest priority for a business.
   *
   * @param  business  the {@link Account}
   *
   * @return  the <code>CreditCard</code> or {@code null} if none found
   */
  public CreditCard getMonthlyCreditCard(Account business) throws IOException, SQLException {
    Account.Name accounting = business.getName();
    List<CreditCard> cards = getRows();
    int size = cards.size();
    for (int c = 0; c < size; c++) {
      CreditCard tcard = cards.get(c);
      if (tcard.getIsActive() && tcard.getUseMonthly() && tcard.getAccount_name().equals(accounting)) {
        return tcard;
      }
    }
    return null;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.CREDIT_CARDS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.DECLINE_CREDIT_CARD)) {
      if (AOSH.checkParamCount(Command.DECLINE_CREDIT_CARD, args, 2, err)) {
        connector.getSimpleAOClient().declineCreditCard(
            AOSH.parseInt(args[1], "pkey"),
            args[2]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_CREDIT_CARD)) {
      if (AOSH.checkParamCount(Command.REMOVE_CREDIT_CARD, args, 1, err)) {
        connector.getSimpleAOClient().removeCreditCard(
            AOSH.parseInt(args[1], "pkey")
        );
      }
      return true;
    } else {
      return false;
    }
  }
}
