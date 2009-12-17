package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Incoming email addressed to an <code>EmailPipe</code> is piped
 * into a native process.  This process may then take any action
 * desired for mail delivery or handling.
 *
 * @see  EmailAddress
 *
 * @author  AO Industries, Inc.
 */
final public class EmailPipe extends CachedObjectIntegerKey<EmailPipe> implements Removable, Disablable {

    static final int
        COLUMN_PKEY = 0,
        COLUMN_AO_SERVER = 1,
        COLUMN_ACCOUNTING = 3,
        COLUMN_DISABLE_LOG = 4
    ;
    static final String COLUMN_AO_SERVER_name = "ao_server";
    static final String COLUMN_PATH_name = "path";

    int ao_server;
    private String path;
    String accounting;
    int disable_log;

    public int addEmailAddress(EmailAddress address) throws IOException, SQLException {
        return table.connector.getEmailPipeAddresses().addEmailPipeAddress(address, this);
    }

    public boolean canDisable() {
        return disable_log==-1;
    }
    
    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getBusiness().disable_log==-1;
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.EMAIL_PIPES, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.EMAIL_PIPES, pkey);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 2: return path;
            case COLUMN_ACCOUNTING: return accounting;
            case COLUMN_DISABLE_LOG: return disable_log==-1?null:Integer.valueOf(disable_log);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public DisableLog getDisableLog() throws SQLException, IOException {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.getDisableLogs().get(disable_log);
        if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    public Business getBusiness() throws IOException, SQLException {
        Business bu = table.connector.getBusinesses().get(accounting);
        if(bu == null) throw new SQLException("Unable to find Business: " + bu);
        return bu;
    }

    public String getPath() {
        return path;
    }

    public AOServer getAOServer() throws SQLException, IOException {
        AOServer ao=table.connector.getAoServers().get(ao_server);
        if(ao==null) throw new SQLException("Unable to find AOServer: "+ao_server);
        return ao;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.EMAIL_PIPES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        ao_server = result.getInt(2);
        path = result.getString(3);
        accounting = result.getString(4);
        disable_log=result.getInt(5);
        if(result.wasNull()) disable_log=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        ao_server=in.readCompressedInt();
        path=in.readUTF();
        accounting=in.readUTF().intern();
        disable_log=in.readCompressedInt();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getAOServer(),
            getBusiness(),
            getDisableLog()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
            getEmailPipeAddresses()
        );
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) {
        return Collections.emptyList();
    }

    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.EMAIL_PIPES,
            pkey
    	);
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return ao_server+':'+path;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(ao_server);
        out.writeUTF(path);
        out.writeUTF(accounting);
        out.writeCompressedInt(disable_log);
    }

    public List<EmailPipeAddress> getEmailPipeAddresses() throws IOException, SQLException {
        return table.connector.getEmailPipeAddresses().getIndexedRows(EmailPipeAddress.COLUMN_EMAIL_PIPE, pkey);
    }
}