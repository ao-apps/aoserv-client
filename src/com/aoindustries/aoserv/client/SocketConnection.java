package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.net.*;

/**
 * A <code>SocketConnection</code> is a single, persistant, un-secured
 * connection to the server.
 *
 * @see  TCPConnector
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SocketConnection extends AOServConnection {

    /**
     * Keeps a flag of the connection status.
     */
    private boolean isClosed=true;

    /**
     * The socket to the server.
     */
    private final Socket socket;

    /**
     * The output stream to the server.
     */
    private final CompressedDataOutputStream out;

    /**
     * The input stream from the server.
     */
    private final CompressedDataInputStream in;

    SocketConnection(TCPConnector connector) throws IOException {
	super(connector);
	socket=connector.getSocket();
	isClosed=false;
	out=new CompressedDataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

	out.writeUTF(AOServProtocol.CURRENT_VERSION);
        out.writeBoolean(connector.daemonServer!=null);
        if(connector.daemonServer!=null) out.writeUTF(connector.daemonServer);
	out.writeUTF(connector.connectAs);
	out.writeUTF(connector.authenticateAs);
	out.writeUTF(connector.password);
	out.writeLong(connector.id);
	out.flush();

	in=new CompressedDataInputStream(new BufferedInputStream(socket.getInputStream()));
	if(!in.readBoolean()) {
            String message=in.readUTF();
            close();
            throw new IOException(message);
	}
	if(connector.id==-1) connector.id=in.readLong();
    }

    void close() {
	if(in!=null) {
            try {
                in.close();
            } catch(IOException err) {
                connector.errorHandler.reportWarning(err, null);
            }
	}
	if(out!=null) {
            try {
                out.writeCompressedInt(AOServProtocol.QUIT);
                out.flush();
            } catch(SocketException err) {
                // Normal when the other side has terminated the connection
            } catch(IOException err) {
                connector.errorHandler.reportWarning(err, null);
            }
            try {
                out.close();
            } catch(IOException err) {
                connector.errorHandler.reportWarning(err, null);
            }
	}
	if(socket!=null) {
            try {
                socket.close();
            } catch(IOException err) {
                connector.errorHandler.reportWarning(err, null);
            }
	}
	isClosed=true;
    }

    CompressedDataInputStream getInputStream() {
	return in;
    }

    InetAddress getLocalInetAddress() throws IOException {
        return socket.getLocalAddress();
    }

    CompressedDataOutputStream getOutputStream() {
	return out;
    }

    boolean isClosed() {
	return isClosed;
    }
}