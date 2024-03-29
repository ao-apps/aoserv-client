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

package com.aoindustries.aoserv.client.backup;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class SchemaBeanInfo extends SimpleBeanInfo {

  private static final PropertyDescriptor[] properties;

  static {
    try {
      properties = new PropertyDescriptor[]{
          new PropertyDescriptor("BackupPartition",         Schema.class, "getBackupPartition",         null),
          new PropertyDescriptor("BackupReport",            Schema.class, "getBackupReport",            null),
          new PropertyDescriptor("BackupRetention",         Schema.class, "getBackupRetention",         null),
          new PropertyDescriptor("FileReplication",         Schema.class, "getFileReplication",         null),
          new PropertyDescriptor("FileReplicationLog",      Schema.class, "getFileReplicationLog",      null),
          new PropertyDescriptor("FileReplicationSchedule", Schema.class, "getFileReplicationSchedule", null),
          new PropertyDescriptor("FileReplicationSetting",  Schema.class, "getFileReplicationSetting",  null),
          new PropertyDescriptor("MysqlReplication",        Schema.class, "getMysqlReplication",        null),
      };
    } catch (IntrospectionException err) {
      throw new ExceptionInInitializerError(err);
    }
  }

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField") // Not copying array for performance
  public PropertyDescriptor[] getPropertyDescriptors() {
    return properties;
  }

  /**
   * Include base class.
   */
  @Override
  public BeanInfo[] getAdditionalBeanInfo() {
    try {
      return new BeanInfo[]{
          Introspector.getBeanInfo(Schema.class.getSuperclass())
      };
    } catch (IntrospectionException err) {
      throw new AssertionError(err);
    }
  }
}
