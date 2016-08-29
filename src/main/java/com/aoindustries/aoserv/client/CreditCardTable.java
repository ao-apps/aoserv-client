/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  CreditCard
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardTable extends CachedTableIntegerKey<CreditCard> {

	CreditCardTable(AOServConnector connector) {
		super(connector, CreditCard.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(CreditCard.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(CreditCard.COLUMN_CREATED_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addCreditCard(
		final CreditCardProcessor processor,
		final Business business,
		final String groupName,
		final String cardInfo,
		final String providerUniqueId,
		final String firstName,
		final String lastName,
		final String companyName,
		final String email,
		final String phone,
		final String fax,
		final String customerTaxId,
		final String streetAddress1,
		final String streetAddress2,
		final String city,
		final String state,
		final String postalCode,
		final CountryCode countryCode,
		final String principalName,
		final String description,
		// Encrypted values
		String card_number,
		byte expiration_month,
		short expiration_year
	) throws IOException, SQLException {
		// Validate the encrypted parameters
		if(card_number==null) throw new NullPointerException("billing_card_number is null");
		if(card_number.indexOf('\n')!=-1) throw new IllegalArgumentException("billing_card_number may not contain '\n'");

		if(!connector.isSecure()) throw new IOException("Credit cards may only be added when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

		// Encrypt if currently configured to
		final EncryptionKey encryptionFrom = processor.getEncryptionFrom();
		final EncryptionKey encryptionRecipient = processor.getEncryptionRecipient();
		final String encryptedCardNumber;
		final String encryptedExpiration;
		if(encryptionFrom!=null && encryptionRecipient!=null) {
			// Encrypt the card number and expiration
			encryptedCardNumber = encryptionFrom.encrypt(encryptionRecipient, CreditCard.randomize(card_number));
			encryptedExpiration = encryptionFrom.encrypt(encryptionRecipient, CreditCard.randomize(expiration_month+"/"+expiration_year));
		} else {
			encryptedCardNumber = null;
			encryptedExpiration = null;
		}

		return connector.requestResult(
			true,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
					out.writeCompressedInt(SchemaTable.TableID.CREDIT_CARDS.ordinal());
					out.writeUTF(processor.pkey);
					out.writeUTF(business.pkey.toString());
					out.writeNullUTF(groupName);
					out.writeUTF(cardInfo);
					out.writeUTF(providerUniqueId);
					out.writeUTF(firstName);
					out.writeUTF(lastName);
					out.writeNullUTF(companyName);
					out.writeNullUTF(email);
					out.writeNullUTF(phone);
					out.writeNullUTF(fax);
					out.writeNullUTF(customerTaxId);
					out.writeUTF(streetAddress1);
					out.writeNullUTF(streetAddress2);
					out.writeUTF(city);
					out.writeNullUTF(state);
					out.writeNullUTF(postalCode);
					out.writeUTF(countryCode.pkey);
					out.writeNullUTF(principalName);
					out.writeNullUTF(description);
					out.writeNullUTF(encryptedCardNumber);
					out.writeNullUTF(encryptedExpiration);
					out.writeCompressedInt(encryptionFrom==null ? -1 : encryptionFrom.getPkey());
					out.writeCompressedInt(encryptionRecipient==null ? -1 : encryptionRecipient.getPkey());
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unknown response code: "+code);
					}
				}

				@Override
				public Integer afterRelease() {
					connector.tablesUpdated(invalidateList);
					return pkey;
				}
			}
		);
	}

	@Override
	public CreditCard get(int pkey) throws SQLException, IOException {
		return getUniqueRow(CreditCard.COLUMN_PKEY, pkey);
	}

	List<CreditCard> getCreditCards(Business business) throws IOException, SQLException {
		return getIndexedRows(CreditCard.COLUMN_ACCOUNTING, business.pkey);
	}

	/**
	 * Gets the active credit card with the highest priority for a business.
	 *
	 * @param  business  the <code>Business</code>
	 *
	 * @return  the <code>CreditCard</code> or <code>null</code> if none found
	 */
	CreditCard getMonthlyCreditCard(Business business) throws IOException, SQLException {
		AccountingCode accounting = business.getAccounting();
		List<CreditCard> cards = getRows();
		int size = cards.size();
		for (int c = 0; c < size; c++) {
			CreditCard tcard = cards.get(c);
			if (tcard.getIsActive() && tcard.getUseMonthly() && tcard.accounting.equals(accounting)) return tcard;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.CREDIT_CARDS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.DECLINE_CREDIT_CARD)) {
			if(AOSH.checkParamCount(AOSHCommand.DECLINE_CREDIT_CARD, args, 2, err)) {
				connector.getSimpleAOClient().declineCreditCard(
					AOSH.parseInt(args[1], "pkey"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_CREDIT_CARD)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_CREDIT_CARD, args, 1, err)) {
				connector.getSimpleAOClient().removeCreditCard(
					AOSH.parseInt(args[1], "pkey")
				);
			}
			return true;
		} else return false;
	}
}
