/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2009, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.master.i18n;

import com.aoapps.hodgepodge.i18n.EditableResourceBundle;
import com.aoapps.hodgepodge.i18n.EditableResourceBundleSet;
import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Development-only editable resource bundle.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public final class ApplicationResources extends EditableResourceBundle {

  static final EditableResourceBundleSet bundleSet = new EditableResourceBundleSet(
      ApplicationResources.class,
      Locale.ROOT,
      Locale.JAPANESE
  );

  static File getSourceFile(String filename) {
    try {
      return new File(System.getProperty("user.home") + "/maven2/ao/aoserv/client/src/main/resources/com/aoindustries/aoserv/client/master/i18n", filename);
    } catch (SecurityException e) {
      Logger.getLogger(ApplicationResources.class.getName()).log(
          Level.WARNING,
          "Unable to locate source file: " + filename,
          e
      );
      return null;
    }
  }

  /**
   * Loads the editable resource bundle.
   */
  public ApplicationResources() {
    super(Locale.ROOT, bundleSet, getSourceFile("ApplicationResources.properties"));
  }
}
