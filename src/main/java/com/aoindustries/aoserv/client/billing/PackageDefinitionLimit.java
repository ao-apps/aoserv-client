/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Money;
import com.aoapps.lang.math.SafeMath;
import com.aoapps.lang.util.InternUtils;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>PackageDefinitionLimit</code> stores one limit that is part of a <code>PackageDefinition</code>.
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinitionLimit extends CachedObjectIntegerKey<PackageDefinitionLimit> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_PACKAGE_DEFINITION = 1;
  static final String COLUMN_RESOURCE_name = "resource";
  static final String COLUMN_PACKAGE_DEFINITION_name = "package_definition";

  /**
   * Indicates a particular value is unlimited.
   */
  public static final int UNLIMITED = -1;

  private int packageDefinition;
  private String resource;
  private int softLimit;
  private int hardLimit;
  private Money additionalRate;
  private String additionalTransactionType;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public PackageDefinitionLimit() {
    // Do nothing
  }

  @SuppressWarnings("deprecation")
  public PackageDefinitionLimit(
      PackageDefinition packageDefinition,
      Resource resource,
      int softLimit,
      int hardLimit,
      Money additionalRate,
      TransactionType additionalTransactionType
  ) {
    this.pkey = -1;
    this.packageDefinition = packageDefinition.getPkey();
    this.resource = resource.getName();
    this.softLimit = softLimit;
    this.hardLimit = hardLimit;
    this.additionalRate = additionalRate;
    this.additionalTransactionType = additionalTransactionType == null ? null : additionalTransactionType.getName();

    // The table is set from the connector of the package definition
    setTable(packageDefinition.getTable().getConnector().getBilling().getPackageDefinitionLimit());
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_PACKAGE_DEFINITION:
        return packageDefinition;
      case 2:
        return resource;
      case 3:
        return softLimit == UNLIMITED ? null : softLimit;
      case 4:
        return hardLimit == UNLIMITED ? null : hardLimit;
      case 5:
        return additionalRate;
      case 6:
        return additionalTransactionType;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getPackageDefinition_id() {
    return packageDefinition;
  }

  public PackageDefinition getPackageDefinition() throws IOException, SQLException {
    PackageDefinition pd = table.getConnector().getBilling().getPackageDefinition().get(packageDefinition);
    if (pd == null) {
      throw new SQLException("Unable to find PackageDefinition: " + packageDefinition);
    }
    return pd;
  }

  public String getResource_name() {
    return resource;
  }

  public Resource getResource() throws SQLException, IOException {
    Resource r = table.getConnector().getBilling().getResource().get(resource);
    if (r == null) {
      throw new SQLException("Unable to find Resource: " + resource);
    }
    return r;
  }

  /**
   * Gets the soft limit or {@code null} of there is none.
   */
  public int getSoftLimit() {
    return softLimit;
  }

  /**
   * Gets the soft limit and unit or {@code null} if there is none.
   */
  public String getSoftLimitDisplayUnit() throws IOException, SQLException {
    return softLimit == -1 ? null : getResource().getDisplayUnit(softLimit);
  }

  /**
   * Gets the hard limit or {@code null} of there is none.
   */
  public int getHardLimit() {
    return hardLimit;
  }

  /**
   * Gets the hard limit and unit or {@code null} if there is none.
   */
  public String getHardLimitDisplayUnit() throws IOException, SQLException {
    return hardLimit == -1 ? null : getResource().getDisplayUnit(hardLimit);
  }

  /**
   * Gets the additional rate or {@code null} if there is none.
   */
  public Money getAdditionalRate() {
    return additionalRate;
  }

  /**
   * Gets the additional rate per unit or {@code null} if there is none.
   */
  public String getAdditionalRatePerUnit() throws IOException, SQLException {
    return additionalRate == null ? null : getResource().getPerUnit(additionalRate);
  }

  public String getAdditionalTransactionType_name() {
    return additionalTransactionType;
  }

  public TransactionType getAdditionalTransactionType() throws SQLException, IOException {
    if (additionalTransactionType == null) {
      return null;
    }
    TransactionType tt = table.getConnector().getBilling().getTransactionType().get(additionalTransactionType);
    if (tt == null) {
      throw new SQLException("Unable to find TransactionType: " + additionalTransactionType);
    }
    return tt;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.PACKAGE_DEFINITION_LIMITS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt("id");
    packageDefinition = result.getInt("package_definition");
    resource = result.getString("resource");
    softLimit = result.getInt("soft_limit");
    if (result.wasNull()) {
      softLimit = UNLIMITED;
    }
    hardLimit = result.getInt("hard_limit");
    if (result.wasNull()) {
      hardLimit = UNLIMITED;
    }
    additionalRate = MoneyUtil.getMoney(result, "additionalRate.currency", "additionalRate.value");
    additionalTransactionType = result.getString("additional_transaction_type");
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    packageDefinition = in.readCompressedInt();
    resource = in.readUTF().intern();
    softLimit = in.readCompressedInt();
    hardLimit = in.readCompressedInt();
    additionalRate = MoneyUtil.readNullMoney(in);
    additionalTransactionType = InternUtils.intern(in.readNullUTF());
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(packageDefinition);
    out.writeUTF(resource);
    out.writeCompressedInt(softLimit);
    out.writeCompressedInt(hardLimit);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      if (additionalRate != null && additionalRate.getCurrency() == Currency.USD && additionalRate.getScale() == 2) {
        out.writeCompressedInt(SafeMath.castInt(additionalRate.getUnscaledValue()));
      } else {
        out.writeCompressedInt(-1);
      }
    } else {
      MoneyUtil.writeNullMoney(additionalRate, out);
    }
    out.writeNullUTF(additionalTransactionType);
  }
}
