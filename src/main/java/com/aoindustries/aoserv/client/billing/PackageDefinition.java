/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.billing;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Money;
import com.aoapps.lang.math.SafeMath;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>PackageDefinition</code> stores one unique set of resources, limits, and prices.
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinition extends CachedObjectIntegerKey<PackageDefinition> implements Removable {

  static final int COLUMN_PKEY = 0;
  static final String COLUMN_ACCOUNTING_name = "accounting";
  static final String COLUMN_CATEGORY_name = "category";
  static final String COLUMN_monthlyRate_name = "monthlyRate";
  static final String COLUMN_NAME_name = "name";
  static final String COLUMN_VERSION_name = "version";

  private Account.Name accounting;
  private String category;
  private String name;
  private String version;
  private String display;
  private String description;
  private Money setupFee;
  private String setupFeeTransactionType;
  private Money monthlyRate;
  private String monthlyRateTransactionType;
  private boolean active;
  private boolean approved;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public PackageDefinition() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return accounting;
      case 2:
        return category;
      case 3:
        return name;
      case 4:
        return version;
      case 5:
        return display;
      case 6:
        return description;
      case 7:
        return setupFee;
      case 8:
        return setupFeeTransactionType;
      case 9:
        return monthlyRate;
      case 10:
        return monthlyRateTransactionType;
      case 11:
        return active;
      case 12:
        return approved;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Account.Name getAccount_name() {
    return accounting;
  }

  /**
   * May be null if filtered.
   */
  public Account getAccount() throws IOException, SQLException {
    return table.getConnector().getAccount().getAccount().get(accounting);
  }

  public String getPackageCategory_name() {
    return category;
  }

  public PackageCategory getPackageCategory() throws SQLException, IOException {
    PackageCategory pc = table.getConnector().getBilling().getPackageCategory().get(category);
    if (pc == null) {
      throw new SQLException("Unable to find PackageCategory: " + category);
    }
    return pc;
  }

  /**
   * Gets the list of packages using this definition.
   */
  public List<Package> getPackages() throws IOException, SQLException {
    return table.getConnector().getBilling().getPackage().getPackages(this);
  }

  public PackageDefinitionLimit getLimit(Resource resource) throws IOException, SQLException {
    if (resource == null) {
      throw new AssertionError("resource is null");
    }
    return table.getConnector().getBilling().getPackageDefinitionLimit().getPackageDefinitionLimit(this, resource);
  }

  public List<PackageDefinitionLimit> getLimits() throws IOException, SQLException {
    return table.getConnector().getBilling().getPackageDefinitionLimit().getPackageDefinitionLimits(this);
  }

  public void setLimits(final PackageDefinitionLimit[] limits) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.SET_PACKAGE_DEFINITION_LIMITS,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeCompressedInt(limits.length);
            for (PackageDefinitionLimit limit : limits) {
              out.writeUTF(limit.getResource_name());
              out.writeCompressedInt(limit.getSoftLimit());
              out.writeCompressedInt(limit.getHardLimit());
              MoneyUtil.writeNullMoney(limit.getAdditionalRate(), out);
              out.writeNullUTF(limit.getAdditionalTransactionType_name());
            }
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unknown response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            table.getConnector().tablesUpdated(invalidateList);
          }
        }
    );
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public String getDisplay() {
    return display;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Gets the setup fee or {@code null} for none.
   */
  public Money getSetupFee() {
    return setupFee;
  }

  public TransactionType getSetupFeeTransactionType() throws SQLException, IOException {
    if (setupFeeTransactionType == null) {
      return null;
    }
    TransactionType tt = table.getConnector().getBilling().getTransactionType().get(setupFeeTransactionType);
    if (tt == null) {
      throw new SQLException("Unable to find TransactionType: " + setupFeeTransactionType);
    }
    return tt;
  }

  /**
   * Gets the monthly rate for the base package or {@code null} when unavailable.
   */
  public Money getMonthlyRate() {
    return monthlyRate;
  }

  public TransactionType getMonthlyRateTransactionType() throws SQLException, IOException {
    if (monthlyRateTransactionType == null) {
      return null;
    }
    TransactionType tt = table.getConnector().getBilling().getTransactionType().get(monthlyRateTransactionType);
    if (tt == null) {
      throw new SQLException("Unable to find TransactionType: " + monthlyRateTransactionType);
    }
    return tt;
  }

  public boolean isActive() {
    return active;
  }

  public int copy() throws IOException, SQLException {
    return table.getConnector().requestIntQueryInvalidating(true, AoservProtocol.CommandId.COPY_PACKAGE_DEFINITION, pkey);
  }

  public void setActive(boolean active) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_PACKAGE_DEFINITION_ACTIVE, pkey, active);
  }

  public boolean isApproved() {
    return approved;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.PACKAGE_DEFINITIONS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt("id");
      accounting = Account.Name.valueOf(result.getString("accounting"));
      category = result.getString("category");
      name = result.getString("name");
      version = result.getString("version");
      display = result.getString("display");
      description = result.getString("description");
      setupFee = MoneyUtil.getMoney(result, "setupFee.currency", "setupFee.value");
      setupFeeTransactionType = result.getString("setup_fee_transaction_type");
      monthlyRate = MoneyUtil.getMoney(result, "monthlyRate.currency", "monthlyRate.value");
      monthlyRateTransactionType = result.getString("monthly_rate_transaction_type");
      active = result.getBoolean("active");
      approved = result.getBoolean("approved");
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      accounting = Account.Name.valueOf(in.readUTF()).intern();
      category = in.readUTF().intern();
      name = in.readUTF();
      version = in.readUTF();
      display = in.readUTF();
      description = in.readUTF();
      setupFee = MoneyUtil.readNullMoney(in);
      setupFeeTransactionType = InternUtils.intern(in.readNullUTF());
      monthlyRate = MoneyUtil.readNullMoney(in);
      monthlyRateTransactionType = InternUtils.intern(in.readNullUTF());
      active = in.readBoolean();
      approved = in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public String toStringImpl() {
    return display;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(accounting.toString());
    out.writeUTF(category);
    out.writeUTF(name);
    out.writeUTF(version);
    out.writeUTF(display);
    out.writeUTF(description);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      if (setupFee != null && setupFee.getCurrency() == Currency.USD && setupFee.getScale() == 2) {
        out.writeCompressedInt(SafeMath.castInt(setupFee.getUnscaledValue()));
      } else {
        out.writeCompressedInt(-1);
      }
    } else {
      MoneyUtil.writeNullMoney(setupFee, out);
    }
    out.writeNullUTF(setupFeeTransactionType);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      if (monthlyRate != null && monthlyRate.getCurrency() == Currency.USD && monthlyRate.getScale() == 2) {
        out.writeCompressedInt(SafeMath.castInt(monthlyRate.getUnscaledValue()));
      } else {
        out.writeCompressedInt(-1);
      }
    } else {
      MoneyUtil.writeNullMoney(monthlyRate, out);
    }
    out.writeNullUTF(monthlyRateTransactionType);
    out.writeBoolean(active);
    out.writeBoolean(approved);
  }

  @Override
  public List<CannotRemoveReason<Package>> getCannotRemoveReasons() throws IOException, SQLException {
    List<CannotRemoveReason<Package>> reasons = new ArrayList<>(1);
    List<Package> packs = getPackages();
    if (!packs.isEmpty()) {
      reasons.add(new CannotRemoveReason<>("Used by " + packs.size() + " " + (packs.size() == 1 ? "package" : "packages"), packs));
    }
    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.PACKAGE_DEFINITIONS,
        pkey
    );
  }

  public void update(
      final Account business,
      final PackageCategory category,
      final String name,
      final String version,
      final String display,
      final String description,
      final Money setupFee,
      final TransactionType setupFeeTransactionType,
      final Money monthlyRate,
      final TransactionType monthlyRateTransactionType
  ) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.UPDATE_PACKAGE_DEFINITION,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeUTF(business.getName().toString());
            out.writeUTF(category.getName());
            out.writeUTF(name);
            out.writeUTF(version);
            out.writeUTF(display);
            out.writeUTF(description);
            MoneyUtil.writeNullMoney(setupFee, out);
            out.writeBoolean(setupFeeTransactionType != null);
            if (setupFeeTransactionType != null) {
              out.writeUTF(setupFeeTransactionType.getName());
            }
            MoneyUtil.writeMoney(monthlyRate, out);
            out.writeUTF(monthlyRateTransactionType.getName());
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unknown response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            table.getConnector().tablesUpdated(invalidateList);
          }
        }
    );
  }
}
