/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2009, 2016, 2017, 2018, 2019, 2020, 2022  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  PackageDefinitionLimit
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinitionLimitTable extends CachedTableIntegerKey<PackageDefinitionLimit> {

  PackageDefinitionLimitTable(AoservConnector connector) {
    super(connector, PackageDefinitionLimit.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION_name + '.' + PackageDefinition.COLUMN_ACCOUNTING_name, ASCENDING),
      new OrderBy(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION_name + '.' + PackageDefinition.COLUMN_CATEGORY_name, ASCENDING),
      new OrderBy(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION_name + '.' + PackageDefinition.COLUMN_monthlyRate_name, ASCENDING),
      new OrderBy(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION_name + '.' + PackageDefinition.COLUMN_NAME_name, ASCENDING),
      new OrderBy(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION_name + '.' + PackageDefinition.COLUMN_VERSION_name, ASCENDING),
      new OrderBy(PackageDefinitionLimit.COLUMN_RESOURCE_name + '.' + Resource.COLUMN_NAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  List<PackageDefinitionLimit> getPackageDefinitionLimits(PackageDefinition packageDefinition) throws IOException, SQLException {
    return getIndexedRows(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION, packageDefinition.getPkey());
  }

  @Override
  public PackageDefinitionLimit get(int pkey) throws IOException, SQLException {
    return getUniqueRow(PackageDefinitionLimit.COLUMN_PKEY, pkey);
  }

  PackageDefinitionLimit getPackageDefinitionLimit(PackageDefinition packageDefinition, Resource resource) throws IOException, SQLException {
    if (packageDefinition == null) {
      throw new AssertionError("packageDefinition is null");
    }
    if (resource == null) {
      throw new AssertionError("resource is null");
    }
    String resourceName = resource.getName();
    // Use the index first
    for (PackageDefinitionLimit limit : getPackageDefinitionLimits(packageDefinition)) {
      if (limit.getResource_name().equals(resourceName)) {
        return limit;
      }
    }
    return null;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.PACKAGE_DEFINITION_LIMITS;
  }
}
