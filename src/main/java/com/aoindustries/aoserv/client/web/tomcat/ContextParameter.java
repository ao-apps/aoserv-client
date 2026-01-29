/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.web.tomcat;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Represents one parameter within a <code>HttpdTomcatContext</code>.
 *
 * @see  Context
 *
 * @author  AO Industries, Inc.
 */
public final class ContextParameter extends CachedObjectIntegerKey<ContextParameter> implements Removable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_TOMCAT_CONTEXT = 1;
  static final String COLUMN_TOMCAT_CONTEXT_name = "tomcat_context";
  static final String COLUMN_NAME_name = "name";

  private int tomcatContext;
  private String name;
  private String value;
  private boolean override;
  private String description;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  ContextParameter#init(java.sql.ResultSet)
   * @see  ContextParameter#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public ContextParameter() {
    // Do nothing
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
    return Collections.emptyList();
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_TOMCAT_CONTEXT:
        return tomcatContext;
      case 2:
        return name;
      case 3:
        return value;
      case 4:
        return override;
      case 5:
        return description;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Context getHttpdTomcatContext() throws SQLException, IOException {
    Context obj = table.getConnector().getWeb_tomcat().getContext().get(tomcatContext);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdTomcatContext: " + tomcatContext);
    }
    return obj;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public boolean getOverride() {
    return override;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_TOMCAT_PARAMETERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    tomcatContext = result.getInt(2);
    name = result.getString(3);
    value = result.getString(4);
    override = result.getBoolean(5);
    description = result.getString(6);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    tomcatContext = in.readCompressedInt();
    name = in.readUTF();
    value = in.readUTF();
    override = in.readBoolean();
    description = in.readNullUTF();
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.REMOVE, Table.TableId.HTTPD_TOMCAT_PARAMETERS, pkey);
  }

  public void update(
      String name,
      String value,
      boolean override,
      String description
  ) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.UPDATE_HTTPD_TOMCAT_PARAMETER,
        pkey,
        name,
        value,
        override,
        description == null ? "" : description
    );
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(tomcatContext);
    out.writeUTF(name);
    out.writeUTF(value);
    out.writeBoolean(override);
    out.writeNullUTF(description);
  }
}
