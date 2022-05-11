/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.signup;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.net.Email;
import com.aoapps.net.InetAddress;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.aoserv.client.payment.CountryCode;
import com.aoindustries.aoserv.client.pki.EncryptionKey;
import com.aoindustries.aoserv.client.reseller.Brand;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * @see  Request
 *
 * @author  AO Industries, Inc.
 */
public final class RequestTable extends CachedTableIntegerKey<Request> {

  RequestTable(AoservConnector connector) {
    super(connector, Request.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Request.COLUMN_BRAND_name, ASCENDING),
      new OrderBy(Request.COLUMN_TIME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public Request get(int pkey) throws IOException, SQLException {
    return getUniqueRow(Request.COLUMN_PKEY, pkey);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.SIGNUP_REQUESTS;
  }

  /**
   * Encrypts the signup request details and adds to the master database.  The first encryption key flagged to use for signup_from is used as the from
   * and the first key flagged to use as signup_recipient as the recipient.
   */
  public int addSignupRequest(
      final Brand brand,
      final InetAddress ipAddress,
      final PackageDefinition packageDefinition,
      final String businessName,
      final String businessPhone,
      final String businessFax,
      final String businessAddress1,
      final String businessAddress2,
      final String businessCity,
      final String businessState,
      final CountryCode businessCountry,
      final String businessZip,
      final String baName,
      final String baTitle,
      final String baWorkPhone,
      final String baCellPhone,
      final String baHomePhone,
      final String baFax,
      final Email baEmail,
      final String baAddress1,
      final String baAddress2,
      final String baCity,
      final String baState,
      final CountryCode baCountry,
      final String baZip,
      final User.Name baUsername,
      final String billingContact,
      final Email billingEmail,
      final boolean billingUseMonthly,
      final boolean billingPayOneYear,
      // Encrypted values
      String baPassword,
      String billingCardholderName,
      String billingCardNumber,
      String billingExpirationMonth,
      String billingExpirationYear,
      String billingStreetAddress,
      String billingCity,
      String billingState,
      String billingZip,
      // options
      final Map<String, String> options
  ) throws IOException, SQLException {
    // Validate the encrypted parameters
    if (baPassword == null) {
      throw new NullPointerException("ba_password is null");
    }
    if (baPassword.indexOf('\n') != -1) {
      throw new IllegalArgumentException("ba_password may not contain '\n'");
    }
    if (billingCardholderName == null) {
      throw new NullPointerException("billing_cardholder_name is null");
    }
    if (billingCardholderName.indexOf('\n') != -1) {
      throw new IllegalArgumentException("billing_cardholder_name may not contain '\n'");
    }
    if (billingCardNumber == null) {
      throw new NullPointerException("billing_card_number is null");
    }
    if (billingCardNumber.indexOf('\n') != -1) {
      throw new IllegalArgumentException("billing_card_number may not contain '\n'");
    }
    if (billingExpirationMonth == null) {
      throw new NullPointerException("billing_expiration_month is null");
    }
    if (billingExpirationMonth.indexOf('\n') != -1) {
      throw new IllegalArgumentException("billing_expiration_month may not contain '\n'");
    }
    if (billingExpirationYear == null) {
      throw new NullPointerException("billing_expiration_year is null");
    }
    if (billingExpirationYear.indexOf('\n') != -1) {
      throw new IllegalArgumentException("billing_expiration_year may not contain '\n'");
    }
    if (billingStreetAddress == null) {
      throw new NullPointerException("billing_street_address is null");
    }
    if (billingStreetAddress.indexOf('\n') != -1) {
      throw new IllegalArgumentException("billing_street_address may not contain '\n'");
    }
    if (billingCity == null) {
      throw new NullPointerException("billing_city is null");
    }
    if (billingCity.indexOf('\n') != -1) {
      throw new IllegalArgumentException("billing_city may not contain '\n'");
    }
    if (billingState == null) {
      throw new NullPointerException("billing_state is null");
    }
    if (billingState.indexOf('\n') != -1) {
      throw new IllegalArgumentException("billing_state may not contain '\n'");
    }
    if (billingZip == null) {
      throw new NullPointerException("billing_zip is null");
    }
    if (billingZip.indexOf('\n') != -1) {
      throw new IllegalArgumentException("billing_zip may not contain '\n'");
    }

    // Find the from and recipient keys
    final EncryptionKey from = brand.getSignupEncryptionFrom();
    final EncryptionKey recipient = brand.getSignupEncryptionRecipient();

    // Encrypt the message
    String plaintext =
        baPassword + "\n"
            + billingCardholderName + "\n"
            + billingCardNumber + "\n"
            + billingExpirationMonth + "\n"
            + billingExpirationYear + "\n"
            + billingStreetAddress + "\n"
            + billingCity + "\n"
            + billingState + "\n"
            + billingZip + "\n";
    final String ciphertext = from.encrypt(recipient, plaintext);

    // Send the request to the master server
    return connector.requestResult(
        true,
        AoservProtocol.CommandId.ADD,
        // Java 9: new AoservConnector.ResultRequest<>
        new AoservConnector.ResultRequest<Integer>() {
          private int pkey;
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableId.SIGNUP_REQUESTS.ordinal());
            out.writeUTF(brand.getAccount_name().toString());
            out.writeUTF(ipAddress.toString());
            out.writeCompressedInt(packageDefinition.getPkey());
            out.writeUTF(businessName);
            out.writeUTF(businessPhone);
            out.writeBoolean(businessFax != null);
            if (businessFax != null) {
              out.writeUTF(businessFax);
            }
            out.writeUTF(businessAddress1);
            out.writeBoolean(businessAddress2 != null);
            if (businessAddress2 != null) {
              out.writeUTF(businessAddress2);
            }
            out.writeUTF(businessCity);
            out.writeBoolean(businessState != null);
            if (businessState != null) {
              out.writeUTF(businessState);
            }
            out.writeUTF(businessCountry.getCode());
            out.writeBoolean(businessZip != null);
            if (businessZip != null) {
              out.writeUTF(businessZip);
            }
            out.writeUTF(baName);
            out.writeBoolean(baTitle != null);
            if (baTitle != null) {
              out.writeUTF(baTitle);
            }
            out.writeUTF(baWorkPhone);
            out.writeBoolean(baCellPhone != null);
            if (baCellPhone != null) {
              out.writeUTF(baCellPhone);
            }
            out.writeBoolean(baHomePhone != null);
            if (baHomePhone != null) {
              out.writeUTF(baHomePhone);
            }
            out.writeBoolean(baFax != null);
            if (baFax != null) {
              out.writeUTF(baFax);
            }
            out.writeUTF(baEmail.toString());
            out.writeBoolean(baAddress1 != null);
            if (baAddress1 != null) {
              out.writeUTF(baAddress1);
            }
            out.writeBoolean(baAddress2 != null);
            if (baAddress2 != null) {
              out.writeUTF(baAddress2);
            }
            out.writeBoolean(baCity != null);
            if (baCity != null) {
              out.writeUTF(baCity);
            }
            out.writeBoolean(baState != null);
            if (baState != null) {
              out.writeUTF(baState);
            }
            out.writeBoolean(baCountry != null);
            if (baCountry != null) {
              out.writeUTF(baCountry.getCode());
            }
            out.writeBoolean(baZip != null);
            if (baZip != null) {
              out.writeUTF(baZip);
            }
            out.writeUTF(baUsername.toString());
            out.writeUTF(billingContact);
            out.writeUTF(billingEmail.toString());
            out.writeBoolean(billingUseMonthly);
            out.writeBoolean(billingPayOneYear);
            // Encrypted values
            out.writeCompressedInt(from.getPkey());
            out.writeCompressedInt(recipient.getPkey());
            out.writeUTF(ciphertext);
            // options
            int numOptions = options.size();
            out.writeCompressedInt(numOptions);
            int optionCount = 0;
            for (String name : options.keySet()) {
              out.writeUTF(name);
              String value = options.get(name);
              out.writeBoolean(value != null);
              if (value != null) {
                out.writeUTF(value);
              }
              optionCount++;
            }
            if (optionCount != numOptions) {
              throw new IOException("options modified while writing to master");
            }
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              pkey = in.readCompressedInt();
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
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
}
