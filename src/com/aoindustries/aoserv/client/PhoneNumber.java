package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PhoneNumber extends CachedObjectIntegerKey<PhoneNumber> {

    static final int COLUMN_PKEY=0;

    private long created;
    private String
        business,
        person,
        work,
        cell,
        home,
        fax
    ;

    public String getBusiness() {
	return business;
    }

    public String getCell() {
	return cell;
    }

    public Object getColumn(int i) {
	if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
	if(i==1) return new java.sql.Date(created);
	if(i==2) return business;
	if(i==3) return person;
	if(i==4) return work;
	if(i==5) return cell;
	if(i==6) return home;
	if(i==7) return fax;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public long getCreated() {
	return created;
    }

    public String getFax() {
	return fax;
    }

    public String getHome() {
	return home;
    }

    public String getPerson() {
	return person;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PHONE_NUMBERS;
    }

    public String getWork() {
	return work;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	created=result.getTimestamp(2).getTime();
	business=result.getString(3);
	person=result.getString(4);
	work=result.getString(5);
	cell=result.getString(6);
	home=result.getString(7);
	fax=result.getString(8);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	created=in.readLong();
	business=in.readNullUTF();
	person=in.readNullUTF();
	work=in.readNullUTF();
	cell=in.readNullUTF();
	home=in.readNullUTF();
	fax=in.readNullUTF();
    }

    String toStringImpl() {
	StringBuilder SB=new StringBuilder();
	SB.append(pkey).append('|');
	if(business!=null) SB.append(business);
	SB.append('|');
	if(person!=null) SB.append(person);
	SB.append('|');
	if(work!=null) SB.append(work);
	SB.append('|');
	if(cell!=null) SB.append(cell);
	SB.append('|');
	if(home!=null) SB.append(home);
	SB.append('|');
	if(fax!=null) SB.append(fax);
	return SB.toString();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeLong(created);
	out.writeNullUTF(business);
	out.writeNullUTF(person);
	out.writeNullUTF(work);
	out.writeNullUTF(cell);
	out.writeNullUTF(home);
	out.writeNullUTF(fax);
    }
}