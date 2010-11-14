/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  Transaction
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.transactions)
public interface TransactionService extends AOServService<Integer,Transaction> {

    /* TODO
    int addTransaction(
        final Business business,
        final Business sourceBusiness,
        final BusinessAdministrator business_administrator,
        final String type,
        final String description,
        final int quantity,
        final int rate,
        final PaymentType paymentType,
        final String paymentInfo,
        final CreditCardProcessor processor,
    	final byte payment_confirmed
    ) throws IOException, SQLException {
        return connector.requestResult(
            false,
            new AOServConnector.ResultRequest<Integer>() {
                int transid;
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.TRANSACTIONS.ordinal());
                    out.writeUTF(business.pkey);
                    out.writeUTF(sourceBusiness.pkey);
                    out.writeUTF(business_administrator.pkey);
                    out.writeUTF(type);
                    out.writeUTF(description);
                    out.writeCompressedInt(quantity);
                    out.writeCompressedInt(rate);
                    out.writeBoolean(paymentType!=null); if(paymentType!=null) out.writeUTF(paymentType.pkey);
                    out.writeNullUTF(paymentInfo);
                    out.writeNullUTF(processor==null ? null : processor.getProviderId());
                    out.writeByte(payment_confirmed);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        transid=in.readCompressedInt();
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public Integer afterRelease() {
                    connector.tablesUpdated(invalidateList);
                    return transid;
                }
            }
        );
    }
     */

    /* TODO
    BigDecimal getAccountBalance(String accounting, long before) throws IOException, SQLException {
        BigDecimal total = BigDecimal.valueOf(0, 2);
        for(Transaction tr : getTransactions(accounting)) {
            if(tr.getPaymentConfirmation()!=Transaction.NOT_CONFIRMED && tr.getTime()<before) total = total.add(tr.getAmount());
        }
        return total;
    }

    BigDecimal getConfirmedAccountBalance(String accounting) throws IOException, SQLException {
        BigDecimal total = BigDecimal.valueOf(0, 2);
        for(Transaction tr : getTransactions(accounting)) {
            if(tr.getPaymentConfirmation()==Transaction.CONFIRMED) total = total.add(tr.getAmount());
        }
        return total;
    }

    BigDecimal getConfirmedAccountBalance(String accounting, long before) throws IOException, SQLException {
        BigDecimal total = BigDecimal.valueOf(0, 2);
        for(Transaction tr : getTransactions(accounting)) {
            if(tr.getPaymentConfirmation()==Transaction.CONFIRMED && tr.getTime()<before) total = total.add(tr.getAmount());
        }
        return total;
    }

    public List<Transaction> getPendingPayments() throws IOException, SQLException {
        List<Transaction> payments = getIndexedRows(Transaction.COLUMN_TYPE, TransactionType.PAYMENT);
        List<Transaction> pending = new ArrayList<Transaction>(payments.size());
        for(Transaction payment : payments) if(payment.getPaymentConfirmation()==Transaction.WAITING_CONFIRMATION) pending.add(payment);
        return Collections.unmodifiableList(pending);
    }
     */

    /* TODO
    List<Transaction> getTransactions(TransactionSearchCriteria criteria) throws IOException, SQLException {
        List<Transaction> matches = new ArrayList<Transaction>();
        // Uses the indexes when possible
        List<Transaction> trs =
            criteria.getBusiness()!=null ? getIndexedRows(Transaction.COLUMN_ACCOUNTING, criteria.getBusiness())
            : criteria.getSourceBusiness()!=null ? getIndexedRows(Transaction.COLUMN_SOURCE_ACCOUNTING, criteria.getSourceBusiness())
            : criteria.getType()!=null ? getIndexedRows(Transaction.COLUMN_TYPE, criteria.getType())
            : criteria.getBusinessAdministrator()!=null ? getIndexedRows(Transaction.COLUMN_USERNAME, criteria.getBusinessAdministrator())
            : criteria.getPaymentType()!=null ? getIndexedRows(Transaction.COLUMN_PAYMENT_TYPE, criteria.getPaymentType())
            : getRows()
        ;
        for(Transaction tr : trs) {
            if(
                (criteria.getAfter()==TransactionSearchCriteria.ANY || tr.getTime()>=criteria.getAfter())
                && (criteria.getBefore()==TransactionSearchCriteria.ANY || tr.getTime()<criteria.getBefore())
                && (criteria.getTransID()==TransactionSearchCriteria.ANY || tr.pkey==criteria.getTransID())
                && (criteria.getBusiness()==null || tr.accounting.equals(criteria.getBusiness()))
                && (criteria.getSourceBusiness()==null || tr.source_accounting.equals(criteria.getSourceBusiness()))
                && (criteria.getType()==null || tr.type.equals(criteria.getType()))
                && (criteria.getBusinessAdministrator()==null || tr.username.equals(criteria.getBusinessAdministrator()))
                && (criteria.getPaymentType()==null || tr.payment_type.equals(criteria.getPaymentType()))
                && (criteria.getPaymentConfirmed()==TransactionSearchCriteria.ANY || tr.payment_confirmed==criteria.getPaymentConfirmed())
            ) {
                boolean wordsMatch = true;

                // payment_info words
                if(criteria.getPaymentInfo()!=null && criteria.getPaymentInfo().length()>0) {
                    String paymentInfo = tr.getPaymentInfo();
                    if(paymentInfo==null) wordsMatch = false;
                    else {
                        String lowerPaymentInfo = paymentInfo.toLowerCase(Locale.ENGLISH);
                        for(String word : StringUtility.splitString(criteria.getPaymentInfo())) {
                            if(!lowerPaymentInfo.contains(word.toLowerCase(Locale.ENGLISH))) {
                                wordsMatch = false;
                                break;
                            }
                        }
                    }
                }
                if(wordsMatch) {
                    // description words
                    if(criteria.getDescription()!=null && criteria.getDescription().length()>0) {
                        String lowerDescription = tr.getDescription().toLowerCase(Locale.ENGLISH);
                        for(String word : StringUtility.splitString(criteria.getDescription())) {
                            if(!lowerDescription.contains(word.toLowerCase(Locale.ENGLISH))) {
                                wordsMatch = false;
                                break;
                            }
                        }
                    }
                    if(wordsMatch) matches.add(tr);
                }
            }
        }
        return Collections.unmodifiableList(matches);
    }
     */

    /* TODO
    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_TRANSACTION)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_TRANSACTION, args, 11, err)) {
                byte pc;
                if(args[11].equals("Y")) pc=Transaction.CONFIRMED;
                else if(args[11].equals("W")) pc=Transaction.WAITING_CONFIRMATION;
                else if(args[11].equals("N")) pc=Transaction.NOT_CONFIRMED;
                else throw new IllegalArgumentException("Unknown value for payment_confirmed, should be one of Y, W, or N: "+args[11]);
                int transid=connector.getSimpleAOClient().addTransaction(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5],
                    AOSH.parseMillis(args[6], "quantity"),
                    AOSH.parsePennies(args[7], "rate"),
                    args[8],
                    args[9],
                    args[10],
                    pc
                );
                out.println(transid);
                out.flush();
            }
            return true;
        }
        return false;
    }
     */
}
