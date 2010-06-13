/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  CreditCard
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.credit_cards)
public interface CreditCardService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,CreditCard> {

    /* TODO
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

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
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
                    out.writeNullUTF(encryptedCardNumber);
                    out.writeNullUTF(encryptedExpiration);
                    out.writeCompressedInt(encryptionFrom==null ? -1 : encryptionFrom.getPkey());
                    out.writeCompressedInt(encryptionRecipient==null ? -1 : encryptionRecipient.getPkey());
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
     */

    /**
     * Gets the active credit card with the highest priority for a business.
     *
     * @param  business  the <code>Business</code>
     *
     * @return  the <code>CreditCard</code> or <code>null</code> if none found
     */
    /* TODO
    CreditCard getMonthlyCreditCard(Business business) throws IOException, SQLException {
	String accounting = business.getAccounting();

	List<CreditCard> cards = getRows();
	int size = cards.size();
	for (int c = 0; c < size; c++) {
            CreditCard tcard = cards.get(c);
            if (tcard.getIsActive() && tcard.getUseMonthly() && tcard.accounting.equals(accounting)) return tcard;
	}

	return null;
    }
     */

    /* TODO
    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
     */
}
