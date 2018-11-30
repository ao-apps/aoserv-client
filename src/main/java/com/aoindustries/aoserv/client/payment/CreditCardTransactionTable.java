/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.payment;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Business;
import com.aoindustries.aoserv.client.account.BusinessAdministrator;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  CreditCardTransaction
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardTransactionTable extends CachedTableIntegerKey<CreditCardTransaction> {

	public CreditCardTransactionTable(AOServConnector connector) {
		super(connector, CreditCardTransaction.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(CreditCardTransaction.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(CreditCardTransaction.COLUMN_AUTHORIZATION_TIME_name, ASCENDING),
		new OrderBy(CreditCardTransaction.COLUMN_PKEY_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addCreditCardTransaction(
		final CreditCardProcessor processor,
		final Business business,
		final String groupName,
		final boolean testMode,
		final int duplicateWindow,
		final String orderNumber,
		final String currencyCode,
		final BigDecimal amount,
		final BigDecimal taxAmount,
		final boolean taxExempt,
		final BigDecimal shippingAmount,
		final BigDecimal dutyAmount,
		final String shippingFirstName,
		final String shippingLastName,
		final String shippingCompanyName,
		final String shippingStreetAddress1,
		final String shippingStreetAddress2,
		final String shippingCity,
		final String shippingState,
		final String shippingPostalCode,
		final String shippingCountryCode,
		final boolean emailCustomer,
		final String merchantEmail,
		final String invoiceNumber,
		final String purchaseOrderNumber,
		final String description,
		final BusinessAdministrator creditCardCreatedBy,
		final String creditCardPrincipalName,
		final Business creditCardAccounting,
		final String creditCardGroupName,
		final String creditCardProviderUniqueId,
		final String creditCardMaskedCardNumber,
		final String creditCardFirstName,
		final String creditCardLastName,
		final String creditCardCompanyName,
		final String creditCardEmail,
		final String creditCardPhone,
		final String creditCardFax,
		final String creditCardCustomerTaxId,
		final String creditCardStreetAddress1,
		final String creditCardStreetAddress2,
		final String creditCardCity,
		final String creditCardState,
		final String creditCardPostalCode,
		final String creditCardCountryCode,
		final String creditCardComments,
		final long authorizationTime,
		final String authorizationPrincipalName
	) throws IOException, SQLException {
		if(!connector.isSecure()) throw new IOException("Credit card transactions may only be added when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.CREDIT_CARD_TRANSACTIONS.ordinal());
					out.writeUTF(processor.getProviderId());
					out.writeUTF(business.getAccounting().toString());
					out.writeNullUTF(groupName);
					out.writeBoolean(testMode);
					out.writeCompressedInt(duplicateWindow);
					out.writeNullUTF(orderNumber);
					out.writeUTF(currencyCode);
					out.writeUTF(amount.toString());
					out.writeNullUTF(ObjectUtils.toString(taxAmount));
					out.writeBoolean(taxExempt);
					out.writeNullUTF(ObjectUtils.toString(shippingAmount));
					out.writeNullUTF(ObjectUtils.toString(dutyAmount));
					out.writeNullUTF(shippingFirstName);
					out.writeNullUTF(shippingLastName);
					out.writeNullUTF(shippingCompanyName);
					out.writeNullUTF(shippingStreetAddress1);
					out.writeNullUTF(shippingStreetAddress2);
					out.writeNullUTF(shippingCity);
					out.writeNullUTF(shippingState);
					out.writeNullUTF(shippingPostalCode);
					out.writeNullUTF(shippingCountryCode);
					out.writeBoolean(emailCustomer);
					out.writeNullUTF(merchantEmail);
					out.writeNullUTF(invoiceNumber);
					out.writeNullUTF(purchaseOrderNumber);
					out.writeNullUTF(description);
					out.writeUTF(creditCardCreatedBy.getUsername_userId().toString());
					out.writeNullUTF(creditCardPrincipalName);
					out.writeUTF(creditCardAccounting.getAccounting().toString());
					out.writeNullUTF(creditCardGroupName);
					out.writeNullUTF(creditCardProviderUniqueId);
					out.writeUTF(creditCardMaskedCardNumber);
					out.writeUTF(creditCardFirstName);
					out.writeUTF(creditCardLastName);
					out.writeNullUTF(creditCardCompanyName);
					out.writeNullUTF(creditCardEmail);
					out.writeNullUTF(creditCardPhone);
					out.writeNullUTF(creditCardFax);
					out.writeNullUTF(creditCardCustomerTaxId);
					out.writeUTF(creditCardStreetAddress1);
					out.writeNullUTF(creditCardStreetAddress2);
					out.writeUTF(creditCardCity);
					out.writeNullUTF(creditCardState);
					out.writeNullUTF(creditCardPostalCode);
					out.writeUTF(creditCardCountryCode);
					out.writeNullUTF(creditCardComments);
					out.writeLong(authorizationTime);
					out.writeNullUTF(authorizationPrincipalName);
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
	public CreditCardTransaction get(int pkey) throws IOException, SQLException {
		return getUniqueRow(CreditCardTransaction.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.CREDIT_CARD_TRANSACTIONS;
	}

	public CreditCardTransaction getLastCreditCardTransaction(Business bu) throws IOException, SQLException {
		AccountingCode accounting = bu.getAccounting();
		// Sorted by accounting, time, so we search for first match from the bottom
		// TODO: We could do a binary search on accounting code and time to make this faster
		List<CreditCardTransaction> ccts = getRows();
		for(int c=ccts.size()-1; c>=0; c--) {
			CreditCardTransaction cct = ccts.get(c);
			if(cct.accounting.equals(accounting)) return cct;
		}
		return null;
	}
}
