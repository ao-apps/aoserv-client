/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.account;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.FastExternalizable;
import com.aoapps.lang.io.FastObjectInput;
import com.aoapps.lang.io.FastObjectOutput;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import com.aoapps.net.Email;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.Shell;
import com.aoindustries.aoserv.client.linux.User.Gecos;
import com.aoindustries.aoserv.client.linux.UserType;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Each <code>Username</code> is unique across all systems and must
 * be allocated to a <code>Package</code> before use in any of the
 * account types.
 *
 * @see  Administrator
 * @see  User
 * @see  User
 * @see  User
 *
 * @author  AO Industries, Inc.
 */
public final class User extends CachedObjectUserNameKey<User> implements PasswordProtected, Removable, Disablable {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, User.class);

  /**
   * Represents the most general form of a user name.  This is has the fewest constraints; other types of accounts
   * constrain this further.  User names must:
   * <ul>
   *   <li>Be non-null</li>
   *   <li>Be non-empty</li>
   *   <li>Be between 1 and 255 characters</li>
   *   <li>Must start with <code>[a-z]</code></li>
   *   <li>Uses only ASCII 0x21 through 0x7f, excluding {@code space , : ( ) [ ] ' " | & ; A-Z \ /}</li>
   *   <li>
   *     If contains any @ symbol, must also be a valid email address.  Please note that the
   *     reverse is not implied - email addresses may exist that are not valid user ids.
   *   </li>
   * </ul>
   * <p>
   * TODO: Should we allow Unicode here, since we now have a more restrictive {@link com.aoindustries.aoserv.client.linux.User.Name} for shell accounts?
   * </p>
   *
   * @see  com.aoindustries.aoserv.client.linux.User.Name
   * @see  com.aoindustries.aoserv.client.mysql.User.Name
   * @see  com.aoindustries.aoserv.client.postgresql.User.Name
   *
   * @author  AO Industries, Inc.
   */
  public static class Name implements
      Comparable<Name>,
      FastExternalizable,
      DtoFactory<com.aoindustries.aoserv.client.dto.UserName>,
      Internable<Name> {

    public static final int MAX_LENGTH = 255;

    /**
     * Validates a {@link User} name.
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
      boolean hasAt = false;
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
            hasAt = true;
            break;
          default:
            // fall-through
        }
      }

      if (hasAt) {
        // Must also be a valid email address
        ValidationResult result = Email.validate(name);
        if (!result.isValid()) {
          return result;
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

    /*
    public static Name valueOfInterned(String name) throws ValidationException {
      Name existing = interned.get(name);
      return existing != null ? existing : new Name(name).intern();
    }*/

    protected String name;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    protected Name(String name, boolean validate) throws ValidationException {
      this.name = name;
      if (validate) {
        validate();
      }
    }

    /**
     * @param  name  Does not validate, should only be used with a known valid value.
     */
    protected Name(String name) {
      ValidationResult result;
      assert (result = validate(name)).isValid() : result.toString();
      this.name = name;
    }

    protected void validate() throws ValidationException {
      ValidationResult result = validate(name);
      if (!result.isValid()) {
        throw new ValidationException(result);
      }
    }

    @Override
    public final boolean equals(Object obj) {
      return
          obj instanceof Name
              && name.equals(((Name) obj).name);
    }

    @Override
    public final int hashCode() {
      return name.hashCode();
    }

    @Override
    public final int compareTo(Name other) {
      return this == other ? 0 : name.compareTo(other.name);
    }

    @Override
    public final String toString() {
      return name;
    }

    /**
     * Interns this name much in the same fashion as <code>String.intern()</code>.
     * <p>
     * Because this has subtypes, two {@link Name} that are {@link #equals(java.lang.Object)}
     * may not necessarily return the same instance object after interning.  Thus,
     * unless you know objects are of the same class, {@link #equals(java.lang.Object)} should
     * still be used for equality check instead of the {@code obj1 == obj2} shortcut.
     * </p>
     * <p>
     * To more efficiently check post-interned equivalence, one could also do
     * {@code obj1 == obj2 || (obj1.getClass() != obj2.getClass() && obj1.equals(obj2))},
     * but is it worth it?
     * </p>
     * <p>
     * And then if we abuse the fact that interned user ids have an interned name, one
     * could check equivalence of post-interned user ids as {@code obj1.getId() == obj2.getId()},
     * but once again, is it worth it?  Just call {@link #equals(java.lang.Object)}.
     * </p>
     *
     * @see  String#intern()
     */
    @Override
    public Name intern() {
      Name existing = interned.get(name);
      if (existing == null) {
        String internedId = name.intern();
        @SuppressWarnings("StringEquality")
        Name addMe = (name == internedId) ? this : new Name(internedId);
        existing = interned.putIfAbsent(internedId, addMe);
        if (existing == null) {
          existing = addMe;
        }
      }
      return existing;
    }

    @Override
    public com.aoindustries.aoserv.client.dto.UserName getDto() {
      return new com.aoindustries.aoserv.client.dto.UserName(name);
    }

    // <editor-fold defaultstate="collapsed" desc="FastExternalizable">
    private static final long serialVersionUID = -837866431257794645L;

    /**
     * @deprecated  Only required for implementation, do not use directly.
     *
     * @see  FastExternalizable
     */
    @Deprecated // Java 9: (forRemoval = false)
    public Name() {
      // Do nothing
    }

    @Override
    public long getSerialVersionUID() {
      return serialVersionUID;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      FastObjectOutput fastOut = FastObjectOutput.wrap(out);
      try {
        fastOut.writeFastUTF(name);
      } finally {
        fastOut.unwrap();
      }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      if (name != null) {
        throw new IllegalStateException();
      }
      FastObjectInput fastIn = FastObjectInput.wrap(in);
      try {
        name = fastIn.readFastUTF();
      } finally {
        fastIn.unwrap();
      }
      try {
        validate();
      } catch (ValidationException err) {
        InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
        newErr.initCause(err);
        throw newErr;
      }
    }
    // </editor-fold>
  }

  static final int COLUMN_USERNAME = 0;
  static final int COLUMN_PACKAGE = 1;
  static final String COLUMN_USERNAME_name = "username";

  private Account.Name packageName;
  private int disableLog;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public User() {
    // Do nothing
  }

  public void addAdministrator(
      String name,
      String title,
      Date birthday,
      boolean isPrivate,
      String workPhone,
      String homePhone,
      String cellPhone,
      String fax,
      Email email,
      String address1,
      String address2,
      String city,
      String state,
      String country,
      String zip,
      boolean enableEmailSupport
  ) throws IOException, SQLException {
    table.getConnector().getAccount().getAdministrator().addAdministrator(
        this,
        name,
        title,
        birthday,
        isPrivate,
        workPhone,
        homePhone,
        cellPhone,
        fax,
        email,
        address1,
        address2,
        city,
        state,
        country,
        zip,
        enableEmailSupport
    );
  }

  public void addLinuxAccount(
      Group primaryGroup,
      Gecos name,
      Gecos officeLocation,
      Gecos officePhone,
      Gecos homePhone,
      UserType typeObject,
      Shell shellObject
  ) throws IOException, SQLException {
    addLinuxAccount(
        primaryGroup.getName(),
        name,
        officeLocation,
        officePhone,
        homePhone,
        typeObject.getName(),
        shellObject.getPath()
    );
  }

  public void addLinuxAccount(
      Group.Name primaryGroup,
      Gecos name,
      Gecos officeLocation,
      Gecos officePhone,
      Gecos homePhone,
      String type,
      PosixPath shell
  ) throws IOException, SQLException {
    table.getConnector().getLinux().getUser().addLinuxAccount(
        this,
        primaryGroup,
        name,
        officeLocation,
        officePhone,
        homePhone,
        type,
        shell
    );
  }

  public void addMysqlUser() throws IOException, SQLException {
    try {
      table.getConnector().getMysql().getUser().addMysqlUser(com.aoindustries.aoserv.client.mysql.User.Name.valueOf(pkey.toString()));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  public void addPostgresUser() throws IOException, SQLException {
    try {
      table.getConnector().getPostgresql().getUser().addPostgresUser(com.aoindustries.aoserv.client.postgresql.User.Name.valueOf(pkey.toString()));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public int arePasswordsSet() throws IOException, SQLException {
    // Build the array of objects
    List<PasswordProtected> pps = new ArrayList<>();
    Administrator ba = getAdministrator();
    if (ba != null) {
      pps.add(ba);
    }
    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount();
    if (la != null) {
      pps.add(la);
    }
    com.aoindustries.aoserv.client.mysql.User mu = getMysqlUser();
    if (mu != null) {
      pps.add(mu);
    }
    com.aoindustries.aoserv.client.postgresql.User pu = getPostgresUser();
    if (pu != null) {
      pps.add(pu);
    }
    return User.groupPasswordsSet(pps);
  }

  @Override
  public boolean canDisable() throws IOException, SQLException {
    if (disableLog != -1) {
      return false;
    }
    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount();
    if (la != null && !la.isDisabled()) {
      return false;
    }
    com.aoindustries.aoserv.client.mysql.User mu = getMysqlUser();
    if (mu != null && !mu.isDisabled()) {
      return false;
    }
    com.aoindustries.aoserv.client.postgresql.User pu = getPostgresUser();
    return pu == null || pu.isDisabled();
  }

  @Override
  public boolean canEnable() throws SQLException, IOException {
    DisableLog dl = getDisableLog();
    if (dl == null) {
      return false;
    } else {
      return dl.canEnable() && !getPackage().isDisabled();
    }
  }

  /**
   * Checks the strength of a password as used by this <code>Username</code>.
   */
  @Override
  public List<PasswordChecker.Result> checkPassword(String password) throws IOException, SQLException {
    Administrator ba = getAdministrator();
    if (ba != null) {
      List<PasswordChecker.Result> results = ba.checkPassword(password);
      if (PasswordChecker.hasResults(results)) {
        return results;
      }
    }

    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount();
    if (la != null) {
      List<PasswordChecker.Result> results = la.checkPassword(password);
      if (PasswordChecker.hasResults(results)) {
        return results;
      }
    }

    com.aoindustries.aoserv.client.mysql.User mu = getMysqlUser();
    if (mu != null) {
      List<PasswordChecker.Result> results = mu.checkPassword(password);
      if (PasswordChecker.hasResults(results)) {
        return results;
      }
    }

    com.aoindustries.aoserv.client.postgresql.User pu = getPostgresUser();
    if (pu != null) {
      List<PasswordChecker.Result> results = pu.checkPassword(password);
      if (PasswordChecker.hasResults(results)) {
        return results;
      }
    }

    return PasswordChecker.getAllGoodResults();
  }

  @Override
  public void disable(DisableLog dl) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.DISABLE, Table.TableId.USERNAMES, dl.getPkey(), pkey);
  }

  @Override
  public void enable() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.ENABLE, Table.TableId.USERNAMES, pkey);
  }

  // TODO: See where used, and favor direct lookup in other tables:
  public Administrator getAdministrator() throws IOException, SQLException {
    return table.getConnector().getAccount().getAdministrator().get(pkey);
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_USERNAME:
        return pkey;
      case COLUMN_PACKAGE:
        return packageName;
      case 2:
        return disableLog == -1 ? null : disableLog;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public boolean isDisabled() {
    return disableLog != -1;
  }

  @Override
  public DisableLog getDisableLog() throws SQLException, IOException {
    if (disableLog == -1) {
      return null;
    }
    DisableLog obj = table.getConnector().getAccount().getDisableLog().get(disableLog);
    if (obj == null) {
      throw new SQLException("Unable to find DisableLog: " + disableLog);
    }
    return obj;
  }

  // TODO: See where used, and favor direct lookup in other tables:
  public com.aoindustries.aoserv.client.linux.User getLinuxAccount() throws IOException, SQLException {
    String username = pkey.toString();
    if (com.aoindustries.aoserv.client.linux.User.Name.validate(username).isValid()) {
      try {
        return table.getConnector().getLinux().getUser().get(com.aoindustries.aoserv.client.linux.User.Name.valueOf(username));
      } catch (ValidationException e) {
        throw new AssertionError("Already validated", e);
      }
    } else {
      return null;
    }
  }

  // TODO: See where used, and favor direct lookup in other tables:
  public com.aoindustries.aoserv.client.mysql.User getMysqlUser() throws IOException, SQLException {
    String username = pkey.toString();
    if (com.aoindustries.aoserv.client.mysql.User.Name.validate(username).isValid()) {
      try {
        return table.getConnector().getMysql().getUser().get(com.aoindustries.aoserv.client.mysql.User.Name.valueOf(username));
      } catch (ValidationException e) {
        throw new AssertionError("Already validated", e);
      }
    } else {
      return null;
    }
  }

  public Account.Name getPackage_name() {
    return packageName;
  }

  public Package getPackage() throws SQLException, IOException {
    Package packageObject = table.getConnector().getBilling().getPackage().get(packageName);
    if (packageObject == null) {
      throw new SQLException("Unable to find Package: " + packageName);
    }
    return packageObject;
  }

  // TODO: See where used, and favor direct lookup in other tables:
  public com.aoindustries.aoserv.client.postgresql.User getPostgresUser() throws IOException, SQLException {
    String username = pkey.toString();
    if (com.aoindustries.aoserv.client.postgresql.User.Name.validate(username).isValid()) {
      try {
        return table.getConnector().getPostgresql().getUser().get(com.aoindustries.aoserv.client.postgresql.User.Name.valueOf(username));
      } catch (ValidationException e) {
        throw new AssertionError("Already validated", e);
      }
    } else {
      return null;
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.USERNAMES;
  }

  public User.Name getUsername() {
    return pkey;
  }

  public static int groupPasswordsSet(List<? extends PasswordProtected> pps) throws IOException, SQLException {
    int totalAll = 0;
    for (int c = 0; c < pps.size(); c++) {
      int result = pps.get(c).arePasswordsSet();
      if (result == PasswordProtected.SOME) {
        return PasswordProtected.SOME;
      }
      if (result == PasswordProtected.ALL) {
        totalAll++;
      }
    }
    return totalAll == pps.size() ? PasswordProtected.ALL : totalAll == 0 ? PasswordProtected.NONE : PasswordProtected.SOME;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = User.Name.valueOf(result.getString(1));
      packageName = Account.Name.valueOf(result.getString(2));
      disableLog = result.getInt(3);
      if (result.wasNull()) {
        disableLog = -1;
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  public boolean isUsed() throws IOException, SQLException {
    return
        getLinuxAccount() != null
            || getAdministrator() != null
            || getMysqlUser() != null
            || getPostgresUser() != null;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = User.Name.valueOf(in.readUTF()).intern();
      packageName = Account.Name.valueOf(in.readUTF()).intern();
      disableLog = in.readCompressedInt();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
    List<CannotRemoveReason<?>> reasons = new ArrayList<>();

    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount();
    if (la != null) {
      reasons.add(new CannotRemoveReason<>("Used by Linux account: " + la.getUsername().getUsername(), la));
    }
    Administrator ba = getAdministrator();
    if (ba != null) {
      reasons.add(new CannotRemoveReason<>("Used by Administrator: " + ba.getUsername().getUsername(), ba));
    }
    com.aoindustries.aoserv.client.mysql.User mu = getMysqlUser();
    if (mu != null) {
      reasons.add(new CannotRemoveReason<>("Used by MySQL user: " + mu.getUsername().getUsername(), mu));
    }
    com.aoindustries.aoserv.client.postgresql.User pu = getPostgresUser();
    if (pu != null) {
      reasons.add(new CannotRemoveReason<>("Used by PostgreSQL user: " + pu.getUsername().getUsername(), pu));
    }

    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.USERNAMES,
        pkey
    );
  }

  @Override
  public void setPassword(String password) throws SQLException, IOException {
    Administrator ba = getAdministrator();
    if (ba != null) {
      ba.setPassword(password);
    }

    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount();
    if (la != null) {
      la.setPassword(password);
    }

    com.aoindustries.aoserv.client.mysql.User mu = getMysqlUser();
    if (mu != null) {
      mu.setPassword(password);
    }

    com.aoindustries.aoserv.client.postgresql.User pu = getPostgresUser();
    if (pu != null) {
      pu.setPassword(password);
    }
  }

  @Override
  public boolean canSetPassword() throws IOException, SQLException {
    if (disableLog != -1) {
      return false;
    }

    Administrator ba = getAdministrator();
    if (ba != null && !ba.canSetPassword()) {
      return false;
    }

    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount();
    if (la != null && !la.canSetPassword()) {
      return false;
    }

    com.aoindustries.aoserv.client.mysql.User mu = getMysqlUser();
    if (mu != null && !mu.canSetPassword()) {
      return false;
    }

    com.aoindustries.aoserv.client.postgresql.User pu = getPostgresUser();
    if (pu != null && !pu.canSetPassword()) {
      return false;
    }

    return ba != null || la != null || mu != null || pu != null;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey.toString());
    out.writeUTF(packageName.toString());
    out.writeCompressedInt(disableLog);
  }
}
