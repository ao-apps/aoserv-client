/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client;

import java.util.List;

/**
 * A parent container of {@link Schema}.
 *
 * @author  AO Industries, Inc.
 */
public interface SchemaParent {

  /**
   * Gets an unmodifiable list of all of the schemas.
   */
  // TODO: Java 1.8: default implementation returning empty list
  List<? extends Schema> getSchemas();
}
