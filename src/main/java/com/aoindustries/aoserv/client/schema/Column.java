/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.schema;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.util.InternUtils;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.AoservObject;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Meta-data for every field of every <code>AoservObject</code> is available as
 * a <code>SchemaColumn</code>.   This allows <code>AoservObject</code>s to be
 * treated in a uniform manner, while still accessing all of their attributes.
 *
 * @see  Table
 * @see  AoservObject
 *
 * @author  AO Industries, Inc.
 */
public final class Column extends GlobalObjectIntegerKey<Column> {

  static final int COLUMN_ID = 0;

  private String table;
  private String name;
  private String sinceVersion;
  private String lastVersion;
  private short index;
  private String type;
  private boolean isNullable;
  private boolean isUnique;
  private boolean isPublic;
  private String description;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Column() {
    // Do nothing
  }

  /* Unused 2021-11-04?
  public Column(
    int id,
    String table,
    String name,
    String sinceVersion,
    String lastVersion,
    short index,
    String type,
    boolean isNullable,
    boolean isUnique,
    boolean isPublic,
    String description
  ) {
    this.pkey = id;
    this.table = table;
    this.name = name;
    this.sinceVersion = sinceVersion;
    this.lastVersion = lastVersion;
    this.index = index;
    this.type = type;
    this.isNullable = isNullable;
    this.isUnique = isUnique;
    this.isPublic = isPublic;
    this.description = description;
  }
   */

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ID:
        return pkey;
      case 1:
        return table;
      case 2:
        return name;
      case 3:
        return sinceVersion;
      case 4:
        return lastVersion;
      case 5:
        return index;
      case 6:
        return type;
      case 7:
        return isNullable;
      case 8:
        return isUnique;
      case 9:
        return isPublic;
      case 10:
        return description;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getId() {
    return pkey;
  }

  public String getTable_name() {
    return table;
  }

  public Table getTable(AoservConnector connector) throws SQLException, IOException {
    Table obj = connector.getSchema().getTable().get(table);
    if (obj == null) {
      throw new SQLException("Unable to find SchemaTable: " + table);
    }
    return obj;
  }

  public String getName() {
    return name;
  }

  public String getSinceVersion_version() {
    return sinceVersion;
  }

  public AoservProtocol getSinceVersion(AoservConnector connector) throws SQLException, IOException {
    AoservProtocol obj = connector.getSchema().getAoservProtocol().get(sinceVersion);
    if (obj == null) {
      throw new SQLException("Unable to find AoservProtocol: " + sinceVersion);
    }
    return obj;
  }

  public String getLastVersion_version() {
    return lastVersion;
  }

  public AoservProtocol getLastVersion(AoservConnector connector) throws SQLException, IOException {
    if (lastVersion == null) {
      return null;
    }
    AoservProtocol obj = connector.getSchema().getAoservProtocol().get(lastVersion);
    if (obj == null) {
      throw new SQLException("Unable to find AoservProtocol: " + lastVersion);
    }
    return obj;
  }

  public short getIndex() {
    return index;
  }

  public String getType_name() {
    return type;
  }

  public Type getType(AoservConnector connector) throws SQLException, IOException {
    Type obj = connector.getSchema().getType().get(type);
    if (obj == null) {
      throw new SQLException("Unable to find SchemaType: " + type);
    }
    return obj;
  }

  public boolean isNullable() {
    return isNullable;
  }

  public boolean isUnique() {
    return isUnique;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.SCHEMA_COLUMNS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey = result.getInt(pos++);
    table = result.getString(pos++);
    name = result.getString(pos++);
    sinceVersion = result.getString(pos++);
    lastVersion = result.getString(pos++);
    index = result.getShort(pos++);
    type = result.getString(pos++);
    isNullable = result.getBoolean(pos++);
    isUnique = result.getBoolean(pos++);
    isPublic = result.getBoolean(pos++);
    description = result.getString(pos++);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    table = in.readUTF().intern();
    name = in.readUTF().intern();
    sinceVersion = in.readUTF().intern();
    lastVersion = InternUtils.intern(in.readNullUTF());
    index = in.readShort();
    type = in.readUTF().intern();
    isNullable = in.readBoolean();
    isUnique = in.readBoolean();
    isPublic = in.readBoolean();
    description = in.readUTF();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(table);
    out.writeUTF(name);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_18) >= 0) {
      out.writeUTF(sinceVersion);
      out.writeNullUTF(lastVersion);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
      out.writeCompressedInt(index);
    } else {
      out.writeShort(index);
    }
    out.writeUTF(type);
    out.writeBoolean(isNullable);
    out.writeBoolean(isUnique);
    out.writeBoolean(isPublic);
    out.writeUTF(description);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_101) >= 0) {
        out.writeUTF(sinceVersion);
      }
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_104) >= 0) {
        out.writeNullUTF(lastVersion);
      }
    }
  }

  @Override
  public String toStringImpl() {
    return table + '.' + name;
  }

  public List<ForeignKey> getReferencedBy(AoservConnector connector) throws IOException, SQLException {
    return connector.getSchema().getForeignKey().getSchemaForeignKeysReferencing(this);
  }

  public List<ForeignKey> getReferences(AoservConnector connector) throws IOException, SQLException {
    return connector.getSchema().getForeignKey().getSchemaForeignKeysReferencedBy(this);
  }
}
