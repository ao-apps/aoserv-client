/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Updates the credit card number and expiration, including the masked card number.
 * Encrypts the data if the processors has been configured to store card encrypted
 * in the master database.
 *
 * @author  AO Industries, Inc.
 */
final public class UpdateCardNumberAndExpirationCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1L;

    final private int creditCard;
    final private String cardNumber;
    final private byte expirationMonth;
    final private short expirationYear;

    public UpdateCardNumberAndExpirationCommand(
        @Param(name="creditCard") CreditCard creditCard,
        @Param(name="cardNumber") String cardNumber,
        @Param(name="expirationMonth") byte expirationMonth,
        @Param(name="expirationYear") short expirationYear
    ) {
        this.creditCard = creditCard.getPkey();
        this.cardNumber = cardNumber;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getCreditCard() {
        return creditCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public byte getExpirationMonth() {
        return expirationMonth;
    }

    public short getExpirationYear() {
        return expirationYear;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO: String maskedCardNumber = CreditCard.maskCreditCardNumber(cardNumber);
        // TODO
        /*
        CreditCardProcessor processor = getCreditCardProcessor();
        final EncryptionKey encryptionFrom = processor.getEncryptionFrom();
        final EncryptionKey encryptionRecipient = processor.getEncryptionRecipient();
        final String encryptedCardNumber;
        final String encryptedExpiration;
        if(encryptionFrom!=null && encryptionRecipient!=null) {
            // Encrypt the card number and expiration
            encryptedCardNumber = encryptionFrom.encrypt(encryptionRecipient, randomize(cardNumber));
            encryptedExpiration = encryptionFrom.encrypt(encryptionRecipient, randomize(expirationMonth+"/"+expirationYear));
        } else {
            encryptedCardNumber = null;
            encryptedExpiration = null;
        }

        getConnector().requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.UPDATE_CREDIT_CARD_NUMBER_AND_EXPIRATION.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeUTF(maskedCardNumber);
                    out.writeNullUTF(encryptedCardNumber);
                    out.writeNullUTF(encryptedExpiration);
                    out.writeCompressedInt(encryptionFrom==null ? -1 : encryptionFrom.getPkey());
                    out.writeCompressedInt(encryptionRecipient==null ? -1 : encryptionRecipient.getPkey());
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public void afterRelease() {
                    getConnector().tablesUpdated(invalidateList);
                }
            }
        );
         *
         */
        return errors;
    }
}
