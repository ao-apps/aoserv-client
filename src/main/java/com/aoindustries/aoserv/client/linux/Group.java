/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.linux;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A <code>LinuxGroup</code> may exist on multiple <code>Server</code>s.
 * The information common across all servers is stored is a <code>LinuxGroup</code>.
 *
 * @see  GroupServer
 *
 * @author  AO Industries, Inc.
 */
public final class Group extends CachedObjectGroupNameKey<Group> implements Removable {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Group.class);

  /**
   * Represents a group ID that may be used by certain types of groups.  Group ids must:
   * <ul>
   *   <li>Be non-null</li>
   *   <li>Be non-empty</li>
   *   <li>Be between 1 and 32 characters</li>
   *   <li>Must start with <code>[a-z]</code></li>
   *   <li>Uses only ASCII 0x21 through 0x7f, excluding {@code space , : ( ) [ ] ' " | & ; A-Z \ / @}</li>
   *   <li>TODO: May only end on "$"?</li>
   * </ul>
   *
   * @author  AO Industries, Inc.
   */
  // TODO: Update for IEEE Std 1003.1.2001 "3.426 User Name"? https://paulgorman.org/technical/presentations/linux_username_conventions.pdf
  // TODO: Combined with "UserName" as "PosixName" (and an associated "PosixPortableFilename")?
  public static final class Name implements
      Comparable<Name>,
      Serializable,
      DtoFactory<com.aoindustries.aoserv.client.dto.LinuxGroupName>,
      Internable<Name> {

    private static final long serialVersionUID = 5758732021942097608L;

    public static final int MAX_LENGTH = 32;

    /**
     * Validates a group name.
     */
    public static ValidationResult validate(String name) {
      if (name == null) {
        return new InvalidResult(RESOURCES, "Name.validate.isNull");
      }
      int len = name.length();
      if (len == 0) {
        return new InvalidResult(RESOURCES, "Name.validate.isEmpty");
      }
      if (len > MAX_LENGTH) {
        return new InvalidResult(RESOURCES, "Name.validate.tooLong", MAX_LENGTH, len);
      }

      // The first character must be [a-z]
      char ch = name.charAt(0);
      if (ch < 'a' || ch > 'z') {
        return new InvalidResult(RESOURCES, "Name.validate.startAToZ");
      }

      // The rest may have additional characters
      for (int c = 1; c < len; c++) {
        ch = name.charAt(c);
        if (ch == ' ') {
          return new InvalidResult(RESOURCES, "Name.validate.noSpace");
        }
        if (ch <= 0x21 || ch > 0x7f) {
          return new InvalidResult(RESOURCES, "Name.validate.specialCharacter");
        }
        if (ch >= 'A' && ch <= 'Z') {
          return new InvalidResult(RESOURCES, "Name.validate.noCapital");
        }
        switch (ch) {
          case ',':
            return new InvalidResult(RESOURCES, "Name.validate.comma");
          case ':':
            return new InvalidResult(RESOURCES, "Name.validate.colon");
          case '(':
            return new InvalidResult(RESOURCES, "Name.validate.leftParen");
          case ')':
            return new InvalidResult(RESOURCES, "Name.validate.rightParen");
          case '[':
            return new InvalidResult(RESOURCES, "Name.validate.leftSquare");
          case ']':
            return new InvalidResult(RESOURCES, "Name.validate.rightSquare");
          case '\'':
            return new InvalidResult(RESOURCES, "Name.validate.apostrophe");
          case '"':
            return new InvalidResult(RESOURCES, "Name.validate.quote");
          case '|':
            return new InvalidResult(RESOURCES, "Name.validate.verticalBar");
          case '&':
            return new InvalidResult(RESOURCES, "Name.validate.ampersand");
          case ';':
            return new InvalidResult(RESOURCES, "Name.validate.semicolon");
          case '\\':
            return new InvalidResult(RESOURCES, "Name.validate.backslash");
          case '/':
            return new InvalidResult(RESOURCES, "Name.validate.slash");
          case '@':
            return new InvalidResult(RESOURCES, "Name.validate.at");
          default:
            // fall-through to continue loop
        }
      }
      return ValidResult.getInstance();
    }

    private static final ConcurrentMap<String, Name> interned = new ConcurrentHashMap<>();

    /**
     * @param name  when {@code null}, returns {@code null}
     */
    public static Name valueOf(String name) throws ValidationException {
      if (name == null) {
        return null;
      }
      //Name existing = interned.get(name);
      //return existing != null ? existing : new Name(name);
      return new Name(name, true);
    }

    private final String name;

    private Name(String name, boolean validate) throws ValidationException {
      this.name = name;
      if (validate) {
        validate();
      }
    }

    /**
     * @param  name  Does not validate, should only be used with a known valid value.
     */
    private Name(String name) {
      ValidationResult result;
      assert (result = validate(name)).isValid() : result.toString();
      this.name = name;
    }

    private void validate() throws ValidationException {
      ValidationResult result = validate(name);
      if (!result.isValid()) {
        throw new ValidationException(result);
      }
    }

    /**
     * Perform same validation as constructor on readObject.
     */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
      ois.defaultReadObject();
      try {
        validate();
      } catch (ValidationException err) {
        InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
        newErr.initCause(err);
        throw newErr;
      }
    }

    @Override
    public boolean equals(Object obj) {
      return
          (obj instanceof Name)
              && name.equals(((Name) obj).name);
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }

    @Override
    public int compareTo(Name other) {
      return this == other ? 0 : name.compareTo(other.name);
    }

    @Override
    public String toString() {
      return name;
    }

    /**
     * Interns this name much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    @Override
    public Name intern() {
      Name existing = interned.get(name);
      if (existing == null) {
        String internedId = name.intern();
        @SuppressWarnings("StringEquality") // Using identity String comparison to see if already interned
        Name addMe = (name == internedId) ? this : new Name(internedId);
        existing = interned.putIfAbsent(internedId, addMe);
        if (existing == null) {
          existing = addMe;
        }
      }
      return existing;
    }

    @Override
    public com.aoindustries.aoserv.client.dto.LinuxGroupName getDto() {
      return new com.aoindustries.aoserv.client.dto.LinuxGroupName(name);
    }
  }

  static final int COLUMN_NAME = 0;
  static final int COLUMN_PACKAGE = 1;
  static final String COLUMN_NAME_name = "name";

  /**
   * Some commonly used system and application groups.
   */
  public static final Name
      ADM,
      AOADMIN,
      AOSERV_JILTER,
      AOSERV_XEN_MIGRATION,
      APACHE,
      AUDIO,
      AVAHI_AUTOIPD,
      AWSTATS,
      BIN,
      BIRD,
      CDROM,
      CGRED,
      CHRONY,
      CLAMSCAN,
      CLAMUPDATE,
      DAEMON,
      DBUS,
      DHCPD,
      DIALOUT,
      DIP,
      DISK,
      FLOPPY,
      FTP,
      FTPONLY,
      GAMES,
      INPUT,
      KMEM,
      LOCK,
      LP,
      MAIL,
      MAILNULL,
      MAILONLY,
      MAN,
      MEM,
      MEMCACHED,
      MYSQL,
      NAMED,
      NFSNOBODY,
      NGINX,
      NOBODY,
      NOGROUP,
      POLKITD,
      POSTGRES,
      REDIS,
      ROOT,
      RPC,
      RPCUSER,
      SASLAUTH,
      SCREEN,
      SMMSP,
      SSH_KEYS,
      SSHD,
      SYS,
      SYSTEMD_BUS_PROXY,
      SYSTEMD_JOURNAL,
      SYSTEMD_NETWORK,
      TAPE,
      TCPDUMP,
      TSS,
      TTY,
      UNBOUND,
      USERS,
      UTEMPTER,
      UTMP,
      VIDEO,
      VIRUSGROUP,
      WHEEL,
      // AOServ Master
      AOSERV_MASTER,
      // AOServ Schema
      ACCOUNTING,
      BILLING,
      DISTRIBUTION,
      INFRASTRUCTURE,
      MANAGEMENT,
      MONITORING,
      RESELLER,
      // Amazon EC2 cloud-init
      CENTOS,
      // Jenkins
      JENKINS,
      // OProfile
      OPROFILE,
      // SonarQube
      SONARQUBE,
      // SystemTap
      STAPUSR,
      STAPSYS,
      STAPDEV;

  /**
   * @deprecated  Group httpd no longer used.
   */
  @Deprecated
  public static final Name HTTPD;

  static {
    try {
      ADM = Name.valueOf("adm");
      AOADMIN = Name.valueOf("aoadmin");
      AOSERV_JILTER = Name.valueOf("aoserv-jilter");
      AOSERV_XEN_MIGRATION = Name.valueOf("aoserv-xen-migration");
      APACHE = Name.valueOf("apache");
      AUDIO = Name.valueOf("audio");
      AVAHI_AUTOIPD = Name.valueOf("avahi-autoipd");
      AWSTATS = Name.valueOf("awstats");
      BIN = Name.valueOf("bin");
      BIRD = Name.valueOf("bird");
      CDROM = Name.valueOf("cdrom");
      CGRED = Name.valueOf("cgred");
      CHRONY = Name.valueOf("chrony");
      CLAMSCAN = Name.valueOf("clamscan");
      CLAMUPDATE = Name.valueOf("clamupdate");
      DAEMON = Name.valueOf("daemon");
      DBUS = Name.valueOf("dbus");
      DHCPD = Name.valueOf("dhcpd");
      DIALOUT = Name.valueOf("dialout");
      DIP = Name.valueOf("dip");
      DISK = Name.valueOf("disk");
      FLOPPY = Name.valueOf("floppy");
      FTP = Name.valueOf("ftp");
      FTPONLY = Name.valueOf("ftponly");
      GAMES = Name.valueOf("games");
      INPUT = Name.valueOf("input");
      KMEM = Name.valueOf("kmem");
      LOCK = Name.valueOf("lock");
      LP = Name.valueOf("lp");
      MAIL = Name.valueOf("mail");
      MAILNULL = Name.valueOf("mailnull");
      MAILONLY = Name.valueOf("mailonly");
      MAN = Name.valueOf("man");
      MEM = Name.valueOf("mem");
      MEMCACHED = Name.valueOf("memcached");
      MYSQL = Name.valueOf("mysql");
      NAMED = Name.valueOf("named");
      NGINX = Name.valueOf("nginx");
      NFSNOBODY = Name.valueOf("nfsnobody");
      NOBODY = Name.valueOf("nobody");
      NOGROUP = Name.valueOf("nogroup");
      POLKITD = Name.valueOf("polkitd");
      POSTGRES = Name.valueOf("postgres");
      REDIS = Name.valueOf("redis");
      ROOT = Name.valueOf("root");
      RPC = Name.valueOf("rpc");
      RPCUSER = Name.valueOf("rpcuser");
      SASLAUTH = Name.valueOf("saslauth");
      SCREEN = Name.valueOf("screen");
      SMMSP = Name.valueOf("smmsp");
      SSH_KEYS = Name.valueOf("ssh_keys");
      SSHD = Name.valueOf("sshd");
      SYS = Name.valueOf("sys");
      SYSTEMD_BUS_PROXY = Name.valueOf("systemd-bus-proxy");
      SYSTEMD_JOURNAL = Name.valueOf("systemd-journal");
      SYSTEMD_NETWORK = Name.valueOf("systemd-network");
      TAPE = Name.valueOf("tape");
      TCPDUMP = Name.valueOf("tcpdump");
      TSS = Name.valueOf("tss");
      TTY = Name.valueOf("tty");
      UNBOUND = Name.valueOf("unbound");
      USERS = Name.valueOf("users");
      UTEMPTER = Name.valueOf("utempter");
      UTMP = Name.valueOf("utmp");
      VIDEO = Name.valueOf("video");
      VIRUSGROUP = Name.valueOf("virusgroup");
      WHEEL = Name.valueOf("wheel");
      // AOServ Master
      AOSERV_MASTER = Name.valueOf("aoserv-master");
      // AOServ Schema
      ACCOUNTING = Name.valueOf("accounting");
      BILLING = Name.valueOf("billing");
      DISTRIBUTION = Name.valueOf("distribution");
      INFRASTRUCTURE = Name.valueOf("infrastructure");
      MANAGEMENT = Name.valueOf("management");
      MONITORING = Name.valueOf("monitoring");
      RESELLER = Name.valueOf("reseller");
      // Amazon EC2 cloud-init
      CENTOS = Name.valueOf("centos");
      // Jenkins
      JENKINS = Name.valueOf("jenkins");
      // OProfile
      OPROFILE = Name.valueOf("oprofile");
      // SonarQube
      SONARQUBE = Name.valueOf("sonarqube");
      // SystemTap
      STAPUSR = Name.valueOf("stapusr");
      STAPSYS = Name.valueOf("stapsys");
      STAPDEV = Name.valueOf("stapdev");
      // Group httpd no longer used.
      HTTPD = Name.valueOf("httpd");
    } catch (ValidationException e) {
      throw new AssertionError("These hard-coded values are valid", e);
    }
  }

  private Account.Name packageName;
  private String type;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Group() {
    // Do nothing
  }

  public int addLinuxAccount(User user) throws IOException, SQLException {
    return table.getConnector().getLinux().getGroupUser().addLinuxGroupAccount(this, user);
  }

  public int addLinuxServerGroup(Server aoServer) throws IOException, SQLException {
    return table.getConnector().getLinux().getGroupServer().addLinuxServerGroup(this, aoServer);
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NAME:
        return pkey;
      case COLUMN_PACKAGE:
        return packageName;
      case 2:
        return type;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public GroupType getLinuxGroupType() throws SQLException, IOException {
    GroupType typeObject = table.getConnector().getLinux().getGroupType().get(type);
    if (typeObject == null) {
      throw new SQLException("Unable to find LinuxGroupType: " + type);
    }
    return typeObject;
  }

  public GroupServer getLinuxServerGroup(Server aoServer) throws IOException, SQLException {
    return table.getConnector().getLinux().getGroupServer().getLinuxServerGroup(aoServer, pkey);
  }

  public List<GroupServer> getLinuxServerGroups() throws IOException, SQLException {
    return table.getConnector().getLinux().getGroupServer().getLinuxServerGroups(this);
  }

  public Name getName() {
    return pkey;
  }

  public Account.Name getPackage_name() {
    return packageName;
  }

  public Package getPackage() throws IOException, SQLException {
    // null OK because data may be filtered at this point, like the linux group 'mail'
    return table.getConnector().getBilling().getPackage().get(packageName);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.LINUX_GROUPS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = Name.valueOf(result.getString(1));
      packageName = Account.Name.valueOf(result.getString(2));
      type = result.getString(3);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = Name.valueOf(in.readUTF()).intern();
      packageName = Account.Name.valueOf(in.readUTF()).intern();
      type = in.readUTF().intern();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws IOException, SQLException {
    List<CannotRemoveReason<?>> reasons = new ArrayList<>();

    // Cannot be the primary group for any linux accounts
    for (GroupUser lga : table.getConnector().getLinux().getGroupUser().getRows()) {
      if (lga.isPrimary() && equals(lga.getGroup())) {
        reasons.add(new CannotRemoveReason<>("Used as primary group for Linux account " + lga.getUser().getUsername().getUsername(), lga));
      }
    }

    // All LinuxServerGroups must be removable
    for (GroupServer lsg : getLinuxServerGroups()) {
      reasons.addAll(lsg.getCannotRemoveReasons());
    }

    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.LINUX_GROUPS,
        pkey
    );
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey.toString());
    out.writeUTF(packageName.toString());
    out.writeUTF(type);
  }
}
