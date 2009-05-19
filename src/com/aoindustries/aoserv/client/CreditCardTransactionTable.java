package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
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

    CreditCardTransactionTable(AOServConnector connector) {
	super(connector, CreditCardTransaction.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(CreditCardTransaction.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(CreditCardTransaction.COLUMN_AUTHORIZATION_TIME_name, ASCENDING),
        new OrderBy(CreditCardTransaction.COLUMN_PKEY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addCreditCardTransaction(
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
            new AOServConnector.ResultRequest<Integer>() {
                int pkey;
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.CREDIT_CARD_TRANSACTIONS.ordinal());
                    out.writeUTF(processor.pkey);
                    out.writeUTF(business.pkey);
                    out.writeNullUTF(groupName);
                    out.writeBoolean(testMode);
                    out.writeCompressedInt(duplicateWindow);
                    out.writeNullUTF(orderNumber);
                    out.writeUTF(currencyCode);
                    out.writeUTF(amount.toString());
                    out.writeNullUTF(taxAmount==null ? null : taxAmount.toString());
                    out.writeBoolean(taxExempt);
                    out.writeNullUTF(shippingAmount==null ? null : shippingAmount.toString());
                    out.writeNullUTF(dutyAmount==null ? null : dutyAmount.toString());
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
                    out.writeUTF(creditCardCreatedBy.pkey);
                    out.writeNullUTF(creditCardPrincipalName);
                    out.writeUTF(creditCardAccounting.getAccounting());
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

                public Integer afterRelease() {
                    connector.tablesUpdated(invalidateList);
                    return pkey;
                }
            }
        );
    }

    public CreditCardTransaction get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(CreditCardTransaction.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CREDIT_CARD_TRANSACTIONS;
    }
    
    CreditCardTransaction getLastCreditCardTransaction(Business bu) throws IOException, SQLException {
        String accounting = bu.pkey;
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
