package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.InputStream;
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

    int addCreditCard(
        CreditCardProcessor processor,
	Business business,
        String groupName,
        String cardInfo,
        String providerUniqueId,
        String firstName,
        String lastName,
        String companyName,
        String email,
        String phone,
        String fax,
        String customerTaxId,
        String streetAddress1,
        String streetAddress2,
        String city,
        String state,
        String postalCode,
        CountryCode countryCode,
        String principalName,
        String description
    ) {
        try {
            if(!connector.isSecure()) throw new IOException("Credit cards may only be added when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

            int pkey;
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                out.writeCompressedInt(SchemaTable.TableID.CREDIT_CARDS.ordinal());
                out.writeUTF(processor.pkey);
                out.writeUTF(business.pkey);
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
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    pkey=in.readCompressedInt();
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unknown response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
            return pkey;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public CreditCard get(Object pkey) {
	return getUniqueRow(CreditCard.COLUMN_PKEY, pkey);
    }

    public CreditCard get(int pkey) {
	return getUniqueRow(CreditCard.COLUMN_PKEY, pkey);
    }

    List<CreditCard> getCreditCards(Business business) {
        return getIndexedRows(CreditCard.COLUMN_ACCOUNTING, business.pkey);
    }

    /**
     * Gets the active credit card with the highest priority for a business.
     *
     * @param  business  the <code>Business</code>
     *
     * @return  the <code>CreditCard</code> or <code>null</code> if none found
     */
    CreditCard getMonthlyCreditCard(Business business) {
	String accounting = business.getAccounting();

	List<CreditCard> cards = getRows();
	int size = cards.size();
	for (int c = 0; c < size; c++) {
            CreditCard tcard = cards.get(c);
            if (tcard.getIsActive() && tcard.getUseMonthly() && tcard.accounting.equals(accounting)) return tcard;
	}

	return null;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CREDIT_CARDS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.DECLINE_CREDIT_CARD)) {
            if(AOSH.checkParamCount(AOSHCommand.DECLINE_CREDIT_CARD, args, 2, err)) {
                connector.simpleAOClient.declineCreditCard(
                    AOSH.parseInt(args[1], "pkey"),
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_CREDIT_CARD)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_CREDIT_CARD, args, 1, err)) {
                connector.simpleAOClient.removeCreditCard(
                    AOSH.parseInt(args[1], "pkey")
                );
            }
            return true;
	} else return false;
    }
}
