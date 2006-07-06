package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * A <code>TechnologyName</code> represents one piece of software installed in
 * the system.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyName extends GlobalObjectStringKey<TechnologyName> {

    public static final String MYSQL="MySQL";

    static final int COLUMN_NAME=0;

    private String image_filename;
    private int image_width;
    private int image_height;
    private String image_alt;
    private String home_page_url;

    public Object getColumn(int i) {
	if(i==COLUMN_NAME) return pkey;
	if(i==1) return image_filename;
	if(i==2) return image_width==-1?null:Integer.valueOf(image_width);
	if(i==3) return image_height==-1?null:Integer.valueOf(image_height);
	if(i==4) return image_alt;
	if(i==5) return home_page_url;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getHomePageURL() {
	return home_page_url;
    }

    public String getImageAlt() {
	return image_alt;
    }

    public String getImageFilename() {
	return image_filename;
    }

    public int getImageHeight() {
	return image_height;
    }

    public int getImageWidth() {
	return image_width;
    }

    public String getName() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.TECHNOLOGY_NAMES;
    }

    public List<Technology> getTechnologies(AOServConnector connector) {
	return connector.technologies.getTechnologies(this);
    }

    public TechnologyVersion getTechnologyVersion(AOServConnector connector, String version, OperatingSystemVersion osv) {
	return connector.technologyVersions.getTechnologyVersion(this, version, osv);
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	image_filename=result.getString(2);
	image_width=result.getInt(3);
	if(result.wasNull()) image_width=-1;
	image_height=result.getInt(4);
	if(result.wasNull()) image_height=-1;
	image_alt=result.getString(5);
	home_page_url=result.getString(6);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	image_filename=in.readBoolean()?in.readUTF():null;
	image_width=in.readCompressedInt();
	image_height=in.readCompressedInt();
	image_alt=in.readBoolean()?in.readUTF():null;
	home_page_url=in.readBoolean()?in.readUTF():null;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeBoolean(image_filename!=null); if(image_filename!=null) out.writeUTF(image_filename);
	out.writeCompressedInt(image_width);
	out.writeCompressedInt(image_height);
	out.writeBoolean(image_alt!=null); if(image_alt!=null) out.writeUTF(image_alt);
	out.writeBoolean(home_page_url!=null); if(home_page_url!=null) out.writeUTF(home_page_url);
    }
}