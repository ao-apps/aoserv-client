package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>CreditCardProcessor</code> represents on Merchant account used for credit card processing.
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardProcessor extends CachedObjectStringKey<CreditCardProcessor> {

    static final int
        COLUMN_PROVIDER_ID=0,
        COLUMN_ACCOUNTING=1
    ;
    static final String COLUMN_ACCOUNTING_name = "accounting";
    static final String COLUMN_PROVIDER_ID_name = "provider_id";

    private String accounting;
    private String className;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private boolean enabled;
    private int weight;
    private String description;
    private int encryption_from;
    private int encryption_recipient;

    public Business getBusiness() throws SQLException, IOException {
        Business business = table.connector.getBusinesses().get(accounting);
        if (business == null) throw new SQLException("Unable to find Business: " + accounting);
        return business;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PROVIDER_ID: return pkey;
            case COLUMN_ACCOUNTING: return accounting;
            case 2: return className;
            case 3: return param1;
            case 4: return param2;
            case 5: return param3;
            case 6: return param4;
            case 7: return enabled;
            case 8: return weight;
            case 9: return description;
            case 10: return encryption_from==-1 ? null : Integer.valueOf(encryption_from);
            case 11: return encryption_recipient==-1 ? null : Integer.valueOf(encryption_recipient);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getProviderId() {
        return pkey;
    }

    public String getClassName() {
        return className;
    }
    
    public String getParam1() {
        return param1;
    }
    
    public String getParam2() {
        return param2;
    }
    
    public String getParam3() {
        return param3;
    }
    
    public String getParam4() {
        return param4;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public int getWeight() {
        return weight;
    }

    public String getDescription() {
	return description;
    }
    
    /**
     * Gets the key used for encrypting the card in storage or <code>null</code>
     * if the card is not stored in the database.
     */
    public EncryptionKey getEncryptionFrom() throws SQLException, IOException {
        if(encryption_from==-1) return null;
        EncryptionKey ek = table.connector.getEncryptionKeys().get(encryption_from);
        if(ek==null) throw new SQLException("Unable to find EncryptionKey: "+encryption_from);
        return ek;
    }

    /**
     * Gets the key used for encrypting the card in storage or <code>null</code>
     * if the card is not stored in the database.
     */
    public EncryptionKey getEncryptionRecipient() throws SQLException, IOException {
        if(encryption_recipient==-1) return null;
        EncryptionKey ek = table.connector.getEncryptionKeys().get(encryption_recipient);
        if(ek==null) throw new SQLException("Unable to find EncryptionKey: "+encryption_recipient);
        return ek;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CREDIT_CARD_PROCESSORS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getString(pos++);
    	accounting = result.getString(pos++);
        className = result.getString(pos++);
        param1 = result.getString(pos++);
        param2 = result.getString(pos++);
        param3 = result.getString(pos++);
        param4 = result.getString(pos++);
        enabled = result.getBoolean(pos++);
        weight = result.getInt(pos++);
    	description = result.getString(pos++);
        encryption_from = result.getInt(pos++);
        if(result.wasNull()) encryption_from = -1;
        encryption_recipient = result.getInt(pos++);
        if(result.wasNull()) encryption_recipient = -1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        accounting=in.readUTF().intern();
        className = in.readUTF();
        param1 = in.readNullUTF();
        param2 = in.readNullUTF();
        param3 = in.readNullUTF();
        param4 = in.readNullUTF();
        enabled = in.readBoolean();
        weight = in.readCompressedInt();
    	description = in.readNullUTF();
        encryption_from = in.readCompressedInt();
        encryption_recipient = in.readCompressedInt();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getBusiness()
        );
    }

    @SuppressWarnings("unchecked")
    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
            getBankTransactions(),
            getCreditCards(),
            getCreditCardTransactions(),
            getTransactions()
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(accounting);
        out.writeUTF(className);
        out.writeNullUTF(param1);
        out.writeNullUTF(param2);
        out.writeNullUTF(param3);
        out.writeNullUTF(param4);
        out.writeBoolean(enabled);
        out.writeCompressedInt(weight);
        out.writeNullUTF(description);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) {
            out.writeCompressedInt(encryption_from);
            out.writeCompressedInt(encryption_recipient);
        }
    }

    public List<BankTransaction> getBankTransactions() throws IOException, SQLException {
        return table.connector.getBankTransactions().getIndexedRows(BankTransaction.COLUMN_PROCESSOR, pkey);
    }

    public List<CreditCard> getCreditCards() throws IOException, SQLException {
        return table.connector.getCreditCards().getIndexedRows(CreditCard.COLUMN_PROCESSOR_ID, pkey);
    }

    public List<CreditCardTransaction> getCreditCardTransactions() throws IOException, SQLException {
        return table.connector.getCreditCardTransactions().getIndexedRows(CreditCardTransaction.COLUMN_PROCESSOR_ID, pkey);
    }

    public List<Transaction> getTransactions() throws IOException, SQLException {
        return table.connector.getTransactions().getIndexedRows(Transaction.COLUMN_PROCESSOR, pkey);
    }
}
