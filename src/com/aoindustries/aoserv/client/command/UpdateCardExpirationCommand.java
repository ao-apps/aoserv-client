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
 * Updates the credit card expiration.  Encrypts the data if the processors
 * has been configured to store card encrypted in the master database.
 *
 * @author  AO Industries, Inc.
 */
final public class UpdateCardExpirationCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1L;

    final private int creditCard;
    final private byte expirationMonth;
    final private short expirationYear;

    public UpdateCardExpirationCommand(
        @Param(name="creditCard") CreditCard creditCard,
        @Param(name="expirationMonth") byte expirationMonth,
        @Param(name="expirationYear") short expirationYear
    ) {
        this.creditCard = creditCard.getPkey();
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

    public byte getExpirationMonth() {
        return expirationMonth;
    }

    public short getExpirationYear() {
        return expirationYear;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        /*
        CreditCardProcessor processor = getCreditCardProcessor();
        EncryptionKey encryptionFrom = processor.getEncryptionFrom();
        EncryptionKey encryptionRecipient = processor.getEncryptionRecipient();
        if(encryptionFrom!=null && encryptionRecipient!=null) {
            // Encrypt the expiration
            String encryptedExpiration = encryptionFrom.encrypt(encryptionRecipient, randomize(expirationMonth+"/"+expirationYear));
            getConnector().requestUpdateIL(
                true,
                AOServProtocol.CommandID.UPDATE_CREDIT_CARD_EXPIRATION,
                pkey,
                encryptedExpiration,
                encryptionFrom.getPkey(),
                encryptionRecipient.getPkey()
            );
        }*/
        return errors;
    }
}
