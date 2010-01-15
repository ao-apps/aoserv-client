package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.servers)
public interface ServerService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,Server> {

    /* TODO
    public int addBackupServer(
        final String hostname,
        final ServerFarm farm,
        final Business owner,
        final String description,
        final int backup_hour,
        final OperatingSystemVersion os_version,
        final String username,
        final String password,
        final String contact_phone,
        final String contact_email
    ) throws IOException, SQLException {
        // Create the new profile
        return connector.requestResult(
            true,
            new AOServConnector.ResultRequest<Integer>() {
                int pkey;
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD_BACKUP_SERVER.ordinal());
                    out.writeUTF(hostname);
                    out.writeUTF(farm.getName());
                    out.writeUTF(owner.pkey);
                    out.writeUTF(description);
                    out.writeCompressedInt(backup_hour);
                    out.writeCompressedInt(os_version.getPkey());
                    out.writeUTF(username);
                    out.writeUTF(password);
                    out.writeUTF(contact_phone);
                    out.writeUTF(contact_email);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        pkey=in.readCompressedInt();
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
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
     * Gets a <code>Server</code> based on its hostname, accounting/name, or pkey.
     * This is compatible with the output of <code>Server.toString()</code>.
     * Accepts either a hostname (for ao_servers), accounting/name, or pkey.
     *
     * @return  the <code>Server</code> or <code>null</code> if not found
     *
     * @see  Server#toString
     */
    /* TODO
    public Server get(String server) throws SQLException, IOException {
        // Is it the exact hostname of an ao_server?
        AOServer aoServer = connector.getAoServers().get(server);
        if(aoServer!=null) return aoServer.getServer();

        // Is if a accounting/name combo?
        int slashPos = server.indexOf('/');
        if(slashPos!=-1) {
            String accounting = server.substring(0, slashPos);
            String name = server.substring(slashPos+1);
            Business bu = connector.getBusinesses().get(accounting);
            if(bu==null) return null;
            return bu.getServer(name);
        }

        // Is it an exact server pkey
        try {
            int pkey = Integer.parseInt(server);
            return connector.getServers().get(pkey);
        } catch(NumberFormatException err) {
            return null;
        }
    }
    */

    /* TODO
    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_BACKUP_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_BACKUP_SERVER, args, 3, err)) {
                out.println(
                    connector.getSimpleAOClient().addBackupServer(
                        args[1],
                        args[2],
                        args[3],
                        args[4],
                        AOSH.parseInt(args[5], "backup_hour"),
                        args[6],
                        args[7],
                        args[8],
                        args[9],
                        args[10],
                        args[11],
                        args[12]
                    )
                );
                out.flush();
            }
            return true;
        }
        return false;
    }*/
}
