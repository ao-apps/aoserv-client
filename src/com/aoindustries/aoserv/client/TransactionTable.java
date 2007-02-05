package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  Transaction
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TransactionTable extends AOServTable<Integer,Transaction> {

    private Map<String,Integer> accountBalances=new HashMap<String,Integer>();
    private Map<String,Integer> confirmedAccountBalances=new HashMap<String,Integer>();

    TransactionTable(AOServConnector connector) {
	super(connector, Transaction.class);
    }

    int addTransaction(
        Business business,
        Business sourceBusiness,
        BusinessAdministrator business_administrator,
        String type,
        String description,
        int quantity,
        int rate,
        PaymentType paymentType,
        String paymentInfo,
	byte payment_confirmed
    ) {
        try {
            int transid;
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD);
                out.writeCompressedInt(SchemaTable.TRANSACTIONS);
                out.writeUTF(business.pkey);
                out.writeUTF(sourceBusiness.pkey);
                out.writeUTF(business_administrator.pkey);
                out.writeUTF(type);
                out.writeUTF(description);
                out.writeCompressedInt(quantity);
                out.writeCompressedInt(rate);
                out.writeBoolean(paymentType!=null); if(paymentType!=null) out.writeUTF(paymentType.pkey);
                AOServObject.writeNullUTF(out, paymentInfo);
                out.writeByte(payment_confirmed);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    transid=in.readCompressedInt();
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
            return transid;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public void clearCache() {
        synchronized(this) {
            accountBalances.clear();
            confirmedAccountBalances.clear();
        }
        super.clearCache();
    }

    int getAccountBalance(String accounting) {
	synchronized(this) {
	    Integer O=accountBalances.get(accounting);
	    if(O!=null) return O.intValue();
	    int balance=connector.requestIntQuery(AOServProtocol.GET_ACCOUNT_BALANCE, accounting);
	    accountBalances.put(accounting, Integer.valueOf(balance));
	    return balance;
	}
    }

    int getAccountBalance(String accounting, long before) {
        return connector.requestIntQuery(AOServProtocol.GET_ACCOUNT_BALANCE_BEFORE, accounting, before);
    }

    int getConfirmedAccountBalance(String accounting) {
	synchronized(this) {
	    Integer O=confirmedAccountBalances.get(accounting);
	    if(O!=null) return O.intValue();
	    int balance=connector.requestIntQuery(AOServProtocol.GET_CONFIRMED_ACCOUNT_BALANCE, accounting);
	    confirmedAccountBalances.put(accounting, Integer.valueOf(balance));
	    return balance;
	}
    }

    int getConfirmedAccountBalance(String accounting, long before) {
	return connector.requestIntQuery(AOServProtocol.GET_CONFIRMED_ACCOUNT_BALANCE_BEFORE, accounting, before);
    }

    public List<Transaction> getPendingPayments() {
        return getObjects(AOServProtocol.GET_PENDING_PAYMENTS);
    }

    public List<Transaction> getRows() {
        List<Transaction> list=new ArrayList<Transaction>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.TRANSACTIONS);
        return list;
    }

    int getTableID() {
	return SchemaTable.TRANSACTIONS;
    }

    public Transaction get(Object transid) {
        return get(((Integer)transid).intValue());
    }

    public Transaction get(int transid) {
        return getObject(AOServProtocol.GET_OBJECT, SchemaTable.TRANSACTIONS, transid);
    }

    List<Transaction> getTransactions(TransactionSearchCriteria search) {
        return getObjects(AOServProtocol.GET_TRANSACTIONS_SEARCH, search);
    }

    List<Transaction> getTransactions(String accounting) {
        return getObjects(AOServProtocol.GET_TRANSACTIONS_BUSINESS, accounting);
    }

    List<Transaction> getTransactions(BusinessAdministrator ba) {
        return getObjects(AOServProtocol.GET_TRANSACTIONS_BUSINESS_ADMINISTRATOR, ba.pkey);
    }

    final public List<Transaction> getIndexedRows(int col, Object value) {
        if(col==Transaction.COLUMN_TRANSID) {
            Transaction tr=get(value);
            if(tr==null) return Collections.emptyList();
            else return Collections.singletonList(tr);
        }
        if(col==Transaction.COLUMN_ACCOUNTING) return getTransactions((String)value);
        throw new IllegalArgumentException("Not an indexed column: "+col);
    }

    protected Transaction getUniqueRowImpl(int col, Object value) {
        if(col!=Transaction.COLUMN_TRANSID) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_TRANSACTION)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_TRANSACTION, args, 10, err)) {
                byte pc;
                if(args[10].equals("Y")) pc=Transaction.CONFIRMED;
                else if(args[10].equals("W")) pc=Transaction.WAITING_CONFIRMATION;
                else if(args[10].equals("N")) pc=Transaction.NOT_CONFIRMED;
                else throw new IllegalArgumentException("Unknown value for payment_confirmed, should be one of Y, W, or N: "+args[10]);
                int transid=connector.simpleAOClient.addTransaction(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5],
                    AOSH.parseMillis(args[6], "quantity"),
                    AOSH.parsePennies(args[7], "rate"),
                    args[8],
                    args[9],
                    pc
                );
                out.println(transid);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.APPROVE_TRANSACTION)) {
            if(AOSH.checkParamCount(AOSHCommand.APPROVE_TRANSACTION, args, 5, err)) {
                connector.simpleAOClient.approveTransaction(
                    AOSH.parseInt(args[1], "transid"),
                    args[2],
                    args[3],
                    args[4],
                    args[5]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DECLINE_TRANSACTION)) {
            if(AOSH.checkParamCount(AOSHCommand.DECLINE_TRANSACTION, args, 5, err)) {
                connector.simpleAOClient.declineTransaction(
                    AOSH.parseInt(args[1], "transid"),
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
	}
	return false;
    }
}
