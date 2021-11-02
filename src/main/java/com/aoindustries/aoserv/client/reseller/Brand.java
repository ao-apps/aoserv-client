/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.reseller;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.net.HostAddress;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.CachedObjectAccountNameKey;
import com.aoindustries.aoserv.client.email.Address;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.pki.EncryptionKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A brand has separate website, packages, nameservers, and support.
 *
 * @see  Account
 * @see  Reseller
 *
 * @author  AO Industries, Inc.
 */
public final class Brand extends CachedObjectAccountNameKey<Brand> {

	static final int COLUMN_ACCOUNTING = 0;
	static final String COLUMN_ACCOUNTING_name = "accounting";

	private DomainName nameserver1;
	private DomainName nameserver2;
	private DomainName nameserver3;
	private DomainName nameserver4;
	private int smtp_linux_server_account;
	private HostAddress smtp_host;
	private String smtp_password;
	private int imap_linux_server_account;
	private HostAddress imap_host;
	private String imap_password;
	private int support_email_address;
	private String support_email_display;
	private int signup_email_address;
	private String signup_email_display;
	private int ticket_encryption_from;
	private int ticket_encryption_recipient;
	private int signup_encryption_from;
	private int signup_encryption_recipient;
	private String support_toll_free;
	private String support_day_phone;
	private String support_emergency_phone1;
	private String support_emergency_phone2;
	private String support_fax;
	private String support_mailing_address1;
	private String support_mailing_address2;
	private String support_mailing_address3;
	private String support_mailing_address4;
	private boolean english_enabled;
	private boolean japanese_enabled;
	private String aoweb_struts_http_url_base;
	private String aoweb_struts_https_url_base;
	private String aoweb_struts_google_verify_content;
	private boolean aoweb_struts_noindex;
	private String aoweb_struts_google_analytics_new_tracking_code;
	private String aoweb_struts_signup_admin_address;
	private int aoweb_struts_vnc_bind;
	private String aoweb_struts_keystore_type;
	private String aoweb_struts_keystore_password;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ACCOUNTING : return pkey;
			case 1: return nameserver1;
			case 2: return nameserver2;
			case 3: return nameserver3;
			case 4: return nameserver4;
			case 5: return smtp_linux_server_account;
			case 6: return smtp_host;
			case 7: return smtp_password;
			case 8: return imap_linux_server_account;
			case 9: return imap_host;
			case 10: return imap_password;
			case 11: return support_email_address;
			case 12: return support_email_display;
			case 13: return signup_email_address;
			case 14: return signup_email_display;
			case 15: return ticket_encryption_from;
			case 16: return ticket_encryption_recipient;
			case 17: return signup_encryption_from;
			case 18: return signup_encryption_recipient;
			case 19: return support_toll_free;
			case 20: return support_day_phone;
			case 21: return support_emergency_phone1;
			case 22: return support_emergency_phone2;
			case 23: return support_fax;
			case 24: return support_mailing_address1;
			case 25: return support_mailing_address2;
			case 26: return support_mailing_address3;
			case 27: return support_mailing_address4;
			case 28: return english_enabled;
			case 29: return japanese_enabled;
			case 30: return aoweb_struts_http_url_base;
			case 31: return aoweb_struts_https_url_base;
			case 32: return aoweb_struts_google_verify_content;
			case 33: return aoweb_struts_noindex;
			case 34: return aoweb_struts_google_analytics_new_tracking_code;
			case 35: return aoweb_struts_signup_admin_address;
			case 36: return aoweb_struts_vnc_bind;
			case 37: return aoweb_struts_keystore_type;
			case 38: return aoweb_struts_keystore_password;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public Account.Name getAccount_name() {
		return pkey;
	}

	public Account getAccount() throws IOException, SQLException {
		Account obj = table.getConnector().getAccount().getAccount().get(pkey);
		if(obj == null) throw new SQLException("Unable to find Account: " + pkey);
		return obj;
	}

	public DomainName getNameserver1() {
		return nameserver1;
	}

	public DomainName getNameserver2() {
		return nameserver2;
	}

	public DomainName getNameserver3() {
		return nameserver3;
	}

	public DomainName getNameserver4() {
		return nameserver4;
	}

	public UserServer getSmtpLinuxServerAccount() throws IOException, SQLException {
		UserServer lsa = table.getConnector().getLinux().getUserServer().get(smtp_linux_server_account);
		if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+smtp_linux_server_account);
		return lsa;
	}

	/**
	 * Gets the host that should be used for SMTP.  Will use the hostname
	 * of the SmtpLinuxServerAccount's {@link Server} if smtp_host is null.
	 */
	public HostAddress getSmtpHost() throws IOException, SQLException {
		return smtp_host!=null ? smtp_host : HostAddress.valueOf(getSmtpLinuxServerAccount().getServer().getHostname());
	}

	public String getSmtpPassword() {
		return smtp_password;
	}

	public UserServer getImapLinuxServerAccount() throws SQLException, IOException {
		UserServer lsa = table.getConnector().getLinux().getUserServer().get(imap_linux_server_account);
		if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+imap_linux_server_account);
		return lsa;
	}

	/**
	 * Gets the host that should be used for IMAP.  Will use the hostname
	 * of the ImapLinuxServerAccount's {@link Server} if imap_host is null.
	 */
	public HostAddress getImapHost() throws IOException, SQLException {
		return imap_host!=null ? imap_host : HostAddress.valueOf(getImapLinuxServerAccount().getServer().getHostname());
	}

	public String getImapPassword() {
		return imap_password;
	}

	public Address getSupportEmailAddress() throws IOException, SQLException {
		Address ea = table.getConnector().getEmail().getAddress().get(support_email_address);
		if(ea==null) throw new SQLException("Unable to find EmailAddress: "+support_email_address);
		return ea;
	}

	public String getSupportEmailDisplay() {
		return support_email_display;
	}

	public Address getSignupEmailAddress() throws IOException, SQLException {
		Address ea = table.getConnector().getEmail().getAddress().get(signup_email_address);
		if(ea==null) throw new SQLException("Unable to find EmailAddress: "+signup_email_address);
		return ea;
	}

	public String getSignupEmailDisplay() {
		return signup_email_display;
	}

	public EncryptionKey getTicketEncryptionFrom() throws IOException, SQLException {
		EncryptionKey ek = table.getConnector().getPki().getEncryptionKey().get(ticket_encryption_from);
		if(ek==null) throw new SQLException("Unable to find EncryptionKey: "+ticket_encryption_from);
		return ek;
	}

	public EncryptionKey getTicketEncryptionRecipient() throws IOException, SQLException {
		EncryptionKey ek = table.getConnector().getPki().getEncryptionKey().get(ticket_encryption_recipient);
		if(ek==null) throw new SQLException("Unable to find EncryptionKey: "+ticket_encryption_recipient);
		return ek;
	}

	public EncryptionKey getSignupEncryptionFrom() throws IOException, SQLException {
		EncryptionKey ek = table.getConnector().getPki().getEncryptionKey().get(signup_encryption_from);
		if(ek==null) throw new SQLException("Unable to find EncryptionKey: "+signup_encryption_from);
		return ek;
	}

	public EncryptionKey getSignupEncryptionRecipient() throws IOException, SQLException {
		EncryptionKey ek = table.getConnector().getPki().getEncryptionKey().get(signup_encryption_recipient);
		if(ek==null) throw new SQLException("Unable to find EncryptionKey: "+signup_encryption_recipient);
		return ek;
	}

	public String getSupportTollFree() {
		return support_toll_free;
	}

	public String getSupportDayPhone() {
		return support_day_phone;
	}

	public String getSupportEmergencyPhone1() {
		return support_emergency_phone1;
	}

	public String getSupportEmergencyPhone2() {
		return support_emergency_phone2;
	}

	public String getSupportFax() {
		return support_fax;
	}

	public String getSupportMailingAddress1() {
		return support_mailing_address1;
	}

	public String getSupportMailingAddress2() {
		return support_mailing_address2;
	}

	public String getSupportMailingAddress3() {
		return support_mailing_address3;
	}

	public String getSupportMailingAddress4() {
		return support_mailing_address4;
	}

	public boolean getEnglishEnabled() {
		return english_enabled;
	}

	public boolean getJapaneseEnabled() {
		return japanese_enabled;
	}

	public String getAowebStrutsHttpUrlBase() {
		return aoweb_struts_http_url_base;
	}

	public URL getAowebStrutsHttpURL() throws MalformedURLException {
		return new URL(aoweb_struts_http_url_base);
	}

	public String getAowebStrutsHttpsUrlBase() {
		return aoweb_struts_https_url_base;
	}

	public URL getAowebStrutsHttpsURL() throws MalformedURLException {
		return new URL(aoweb_struts_https_url_base);
	}

	public String getAowebStrutsGoogleVerifyContent() {
		return aoweb_struts_google_verify_content;
	}

	public boolean getAowebStrutsNoindex() {
		return aoweb_struts_noindex;
	}

	public String getAowebStrutsGoogleAnalyticsNewTrackingCode() {
		return aoweb_struts_google_analytics_new_tracking_code;
	}

	public String getAowebStrutsSignupAdminAddress() {
		return aoweb_struts_signup_admin_address;
	}

	public Bind getAowebStrutsVncBind() throws IOException, SQLException {
		Bind nb = table.getConnector().getNet().getBind().get(aoweb_struts_vnc_bind);
		if(nb==null) throw new SQLException("Unable to find NetBind: "+aoweb_struts_vnc_bind);
		return nb;
	}

	public String getAowebStrutsKeystoreType() {
		return aoweb_struts_keystore_type;
	}

	public String getAowebStrutsKeystorePassword() {
		return aoweb_struts_keystore_password;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BRANDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = Account.Name.valueOf(result.getString(pos++));
			nameserver1 = DomainName.valueOf(result.getString(pos++));
			nameserver2 = DomainName.valueOf(result.getString(pos++));
			nameserver3 = DomainName.valueOf(result.getString(pos++));
			nameserver4 = DomainName.valueOf(result.getString(pos++));
			smtp_linux_server_account = result.getInt(pos++);
			smtp_host = HostAddress.valueOf(result.getString(pos++));
			smtp_password = result.getString(pos++);
			imap_linux_server_account = result.getInt(pos++);
			imap_host = HostAddress.valueOf(result.getString(pos++));
			imap_password = result.getString(pos++);
			support_email_address = result.getInt(pos++);
			support_email_display = result.getString(pos++);
			signup_email_address = result.getInt(pos++);
			signup_email_display = result.getString(pos++);
			ticket_encryption_from = result.getInt(pos++);
			ticket_encryption_recipient = result.getInt(pos++);
			signup_encryption_from = result.getInt(pos++);
			signup_encryption_recipient = result.getInt(pos++);
			support_toll_free = result.getString(pos++);
			support_day_phone = result.getString(pos++);
			support_emergency_phone1 = result.getString(pos++);
			support_emergency_phone2 = result.getString(pos++);
			support_fax = result.getString(pos++);
			support_mailing_address1 = result.getString(pos++);
			support_mailing_address2 = result.getString(pos++);
			support_mailing_address3 = result.getString(pos++);
			support_mailing_address4 = result.getString(pos++);
			english_enabled = result.getBoolean(pos++);
			japanese_enabled = result.getBoolean(pos++);
			aoweb_struts_http_url_base = result.getString(pos++);
			aoweb_struts_https_url_base = result.getString(pos++);
			aoweb_struts_google_verify_content = result.getString(pos++);
			aoweb_struts_noindex = result.getBoolean(pos++);
			aoweb_struts_google_analytics_new_tracking_code = result.getString(pos++);
			aoweb_struts_signup_admin_address = result.getString(pos++);
			aoweb_struts_vnc_bind = result.getInt(pos++);
			aoweb_struts_keystore_type = result.getString(pos++);
			aoweb_struts_keystore_password = result.getString(pos++);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey=Account.Name.valueOf(in.readUTF()).intern();
			nameserver1 = DomainName.valueOf(in.readUTF());
			nameserver2 = DomainName.valueOf(in.readUTF());
			nameserver3 = DomainName.valueOf(in.readNullUTF());
			nameserver4 = DomainName.valueOf(in.readNullUTF());
			smtp_linux_server_account = in.readCompressedInt();
			smtp_host = HostAddress.valueOf(in.readNullUTF());
			smtp_password = in.readUTF();
			imap_linux_server_account = in.readCompressedInt();
			imap_host = HostAddress.valueOf(in.readNullUTF());
			imap_password = in.readUTF();
			support_email_address = in.readCompressedInt();
			support_email_display = in.readUTF();
			signup_email_address = in.readCompressedInt();
			signup_email_display = in.readUTF();
			ticket_encryption_from = in.readCompressedInt();
			ticket_encryption_recipient = in.readCompressedInt();
			signup_encryption_from = in.readCompressedInt();
			signup_encryption_recipient = in.readCompressedInt();
			support_toll_free = in.readNullUTF();
			support_day_phone = in.readNullUTF();
			support_emergency_phone1 = in.readNullUTF();
			support_emergency_phone2 = in.readNullUTF();
			support_fax = in.readNullUTF();
			support_mailing_address1 = in.readNullUTF();
			support_mailing_address2 = in.readNullUTF();
			support_mailing_address3 = in.readNullUTF();
			support_mailing_address4 = in.readNullUTF();
			english_enabled = in.readBoolean();
			japanese_enabled = in.readBoolean();
			aoweb_struts_http_url_base = in.readUTF();
			aoweb_struts_https_url_base = in.readUTF();
			aoweb_struts_google_verify_content = in.readNullUTF();
			aoweb_struts_noindex = in.readBoolean();
			aoweb_struts_google_analytics_new_tracking_code = in.readNullUTF();
			aoweb_struts_signup_admin_address = in.readUTF();
			aoweb_struts_vnc_bind = in.readCompressedInt();
			aoweb_struts_keystore_type = in.readUTF();
			aoweb_struts_keystore_password = in.readUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey.toString());
		out.writeUTF(nameserver1.toString());
		out.writeUTF(nameserver2.toString());
		out.writeNullUTF(Objects.toString(nameserver3, null));
		out.writeNullUTF(Objects.toString(nameserver4, null));
		out.writeCompressedInt(smtp_linux_server_account);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_46)>=0) out.writeNullUTF(Objects.toString(smtp_host, null));
		out.writeUTF(smtp_password);
		out.writeCompressedInt(imap_linux_server_account);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_46)>=0) out.writeNullUTF(Objects.toString(imap_host, null));
		out.writeUTF(imap_password);
		out.writeCompressedInt(support_email_address);
		out.writeUTF(support_email_display);
		out.writeCompressedInt(signup_email_address);
		out.writeUTF(signup_email_display);
		out.writeCompressedInt(ticket_encryption_from);
		out.writeCompressedInt(ticket_encryption_recipient);
		out.writeCompressedInt(signup_encryption_from);
		out.writeCompressedInt(signup_encryption_recipient);
		out.writeNullUTF(support_toll_free);
		out.writeNullUTF(support_day_phone);
		out.writeNullUTF(support_emergency_phone1);
		out.writeNullUTF(support_emergency_phone2);
		out.writeNullUTF(support_fax);
		out.writeNullUTF(support_mailing_address1);
		out.writeNullUTF(support_mailing_address2);
		out.writeNullUTF(support_mailing_address3);
		out.writeNullUTF(support_mailing_address4);
		out.writeBoolean(english_enabled);
		out.writeBoolean(japanese_enabled);
		out.writeUTF(aoweb_struts_http_url_base);
		out.writeUTF(aoweb_struts_https_url_base);
		out.writeNullUTF(aoweb_struts_google_verify_content);
		out.writeBoolean(aoweb_struts_noindex);
		out.writeNullUTF(aoweb_struts_google_analytics_new_tracking_code);
		out.writeUTF(aoweb_struts_signup_admin_address);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_52)>=0) out.writeCompressedInt(aoweb_struts_vnc_bind);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_53)>=0) {
			out.writeUTF(aoweb_struts_keystore_type);
			out.writeUTF(aoweb_struts_keystore_password);
		}
	}

	/**
	 * Gets the Reseller for this Brand or {@code null} if not a reseller.
	 */
	public Reseller getReseller() throws IOException, SQLException {
		return table.getConnector().getReseller().getReseller().getReseller(this);
	}

	public List<BrandCategory> getTicketBrandCategories() throws IOException, SQLException {
		return table.getConnector().getReseller().getBrandCategory().getTicketBrandCategories(this);
	}

	/**
	 * Gets the immediate parent of this brand or {@code null} if none available.
	 */
	public Brand getParentBrand() throws IOException, SQLException {
		Account account = getAccount();
		Account parent = account.getParent();
		while(parent != null) {
			Brand parentBrand = parent.getBrand();
			if(parentBrand != null) return parentBrand;
		}
		return null;
	}

	/**
	 * The children of the brand are any brands that have their closest parent
	 * business (that is a brand) equal to this one.
	 */
	public List<Brand> getChildBrands() throws IOException, SQLException {
		List<Brand> children = new ArrayList<>();
		for(Brand brand : table.getConnector().getReseller().getBrand().getRows()) {
			if(!brand.equals(this) && this.equals(brand.getParentBrand())) children.add(brand);
		}
		return children;
	}
}
