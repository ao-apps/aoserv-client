/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  FtpGuestUser
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.ftp_guest_users)
public interface FtpGuestUserService extends AOServService<Integer,FtpGuestUser> {

    /* TODO
    void addFtpGuestUser(String username) throws IOException, SQLException {
    	connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.FTP_GUEST_USERS,
            username
        );
    }

    List<FtpGuestUser> getFtpGuestUsers(AOServer aoServer) throws IOException, SQLException {
	List<FtpGuestUser> cached=getRows();
	int size=cached.size();
        List<FtpGuestUser> matches=new ArrayList<FtpGuestUser>(size);
	for(int c=0;c<size;c++) {
            FtpGuestUser obj=cached.get(c);
            if(obj.getLinuxAccount().getLinuxServerAccount(aoServer)!=null) matches.add(obj);
	}
	return matches;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_FTP_GUEST_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_FTP_GUEST_USER, args, 1, err)) {
                connector.getSimpleAOClient().addFtpGuestUser(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_FTP_GUEST_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_FTP_GUEST_USER, args, 1, err)) {
                connector.getSimpleAOClient().removeFtpGuestUser(
                    args[1]
                );
            }
            return true;
	}
	return false;
    }
     */
}