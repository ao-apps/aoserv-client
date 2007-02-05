package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  CreditCard
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardTable extends CachedTableIntegerKey<CreditCard> {

    CreditCardTable(AOServConnector connector) {
	super(connector, CreditCard.class);
    }

    int addCreditCard(
	Business business,
	byte[] cardNumber,
	String cardInfo,
	byte[] expirationMonth,
	byte[] expirationYear,
	byte[] cardholderName,
	byte[] streetAddress,
	byte[] city,
	byte[] state,
	byte[] zip,
	boolean useMonthly,
	String description
    ) {
        try {
            if(!connector.isSecure()) throw new IOException("Credit cards may only be added when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure.");

            int pkey;
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD);
                out.writeCompressedInt(SchemaTable.CREDIT_CARDS);
                out.writeUTF(business.pkey);
                out.writeCompressedInt(cardNumber.length); out.write(cardNumber);
                out.writeUTF(cardInfo);
                out.writeCompressedInt(expirationMonth.length); out.write(expirationMonth);
                out.writeCompressedInt(expirationYear.length); out.write(expirationYear);
                out.writeCompressedInt(cardholderName.length); out.write(cardholderName);
                out.writeCompressedInt(streetAddress.length); out.write(streetAddress);
                out.writeCompressedInt(city.length); out.write(city);
                out.writeCompressedInt(state==null?-1:state.length); if(state!=null) out.write(state);
                out.writeCompressedInt(zip==null?-1:zip.length); if(zip!=null) out.write(zip);
                out.writeBoolean(useMonthly);
                out.writeBoolean(description!=null); if(description!=null) out.writeUTF(description);
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

	CreditCard card = null;
	int priority = Integer.MIN_VALUE;

	List<CreditCard> cards = getRows();
	int size = cards.size();
	for (int c = 0; c < size; c++) {
            CreditCard tcard = cards.get(c);
            if (tcard.isActive() && tcard.useMonthly() && tcard.accounting.equals(accounting) && tcard.getPriority() > priority) {
                card = tcard;
                priority = tcard.getPriority();
            }
	}

	return card;
    }

    int getTableID() {
	return SchemaTable.CREDIT_CARDS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_CREDIT_CARD)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_CREDIT_CARD, args, 13, err)) {
                connector.simpleAOClient.addCreditCard(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5],
                    args[6],
                    args[7],
                    args[8],
                    args[9],
                    args[10],
                    AOSH.parseBoolean(args[12], "use_monthly"),
                    args[13]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DECLINE_CREDIT_CARD)) {
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