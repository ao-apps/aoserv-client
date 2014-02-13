package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;

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

    SocketConnection(TCPConnector connector) throws InterruptedIOException, IOException {
        super(connector);
        socket=connector.getSocket();
        boolean successful = false;
        try {
            isClosed=false;
            out=new CompressedDataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            in=new CompressedDataInputStream(new BufferedInputStream(socket.getInputStream()));

            out.writeUTF(AOServProtocol.Version.CURRENT_VERSION.getVersion());
            out.writeBoolean(connector.daemonServer!=null);
            if(connector.daemonServer!=null) out.writeUTF(connector.daemonServer);
            out.writeUTF(connector.connectAs);
            out.writeUTF(connector.authenticateAs);
            out.writeUTF(connector.password);
            boolean hasConnectorId;
            long connectorId;
            synchronized(connector.idLock) {
                connectorId = connector.id;
                if(connectorId==-1) {
                    // Hold the idLock when need a connector ID
                    out.writeLong(-1);
                    out.flush();
                    if(Thread.interrupted()) throw new InterruptedIOException();
                    if(!in.readBoolean()) throw new IOException(in.readUTF());
                    connectorId = connector.id=in.readLong();
                    hasConnectorId = false;
                } else {
                    hasConnectorId = true;
                }
            }
            if(hasConnectorId) {
                // Finish connecting outside the idLock when already have a connector ID
                out.writeLong(connectorId);
                out.flush();
                if(Thread.interrupted()) throw new InterruptedIOException();
                if(!in.readBoolean()) throw new IOException(in.readUTF());
            }
            successful = true;
        } finally {
            if(!successful) close();
        }
    }

    void close() {
        if(in!=null) {
            try {
                in.close();
            } catch(IOException err) {
                connector.logger.log(Level.WARNING, null, err);
            }
        }
        if(out!=null) {
            try {
                out.writeCompressedInt(AOServProtocol.CommandID.QUIT.ordinal());
                out.flush();
            } catch(SocketException err) {
                // Normal when the other side has terminated the connection
            } catch(IOException err) {
                connector.logger.log(Level.WARNING, null, err);
            }
            try {
                out.close();
            } catch(IOException err) {
                connector.logger.log(Level.WARNING, null, err);
            }
        }
        if(socket!=null) {
            try {
                socket.close();
            } catch(IOException err) {
                connector.logger.log(Level.WARNING, null, err);
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