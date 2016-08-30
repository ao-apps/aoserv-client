/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  TechnologyVersion
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyVersionTable extends GlobalTableIntegerKey<TechnologyVersion> {

	/**
	 * Fields used in an <code>order by</code> search.
	 */
	public static final int NONE = 0, NAME = 1, VERSION = 2, UPDATED = 3;

	/**
	 * Labels for the different sort orders.
	 */
	static final String[] orderLabels = { "None", "Name", "Version", "Updated" };

	public static final int NUM_ORDER_LABELS=4;

	private static long maximumUpdatedTime=-1;

	TechnologyVersionTable(AOServConnector connector) {
		super(connector, TechnologyVersion.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(TechnologyVersion.COLUMN_NAME_name, ASCENDING),
		new OrderBy(TechnologyVersion.COLUMN_VERSION_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(TechnologyVersionTable.class) {
			maximumUpdatedTime=-1;
		}
	}

	@Override
	public TechnologyVersion get(int pkey) throws IOException, SQLException {
		return getUniqueRow(TechnologyVersion.COLUMN_PKEY, pkey);
	}

	public long getMaximumUpdatedTime() throws IOException, SQLException {
		synchronized(TechnologyVersionTable.class) {
			if(maximumUpdatedTime==-1) {
				List<TechnologyVersion> versions=getRows();
				int size=versions.size();
				long max=-1;
				for(int c=0;c<size;c++) {
					TechnologyVersion version=versions.get(c);
					long mod=version.updated;
					if(mod>max) max=mod;
				}
				maximumUpdatedTime=max;
			}
			return maximumUpdatedTime;
		}
	}

	public static String getOrderLabel(int index) {
		return orderLabels[index];
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TECHNOLOGY_VERSIONS;
	}

	TechnologyVersion getTechnologyVersion(TechnologyName techName, String version, OperatingSystemVersion osv) throws IOException, SQLException {
		String name=techName.getName();
		int osvPKey=osv.pkey;
		List<TechnologyVersion> table=getRows();
		int size=table.size();
		for(int c=0;c<size;c++) {
			TechnologyVersion tv=table.get(c);
			if(tv.name.equals(name) && tv.version.equals(version) && tv.operating_system_version==osvPKey) return tv;
		}
		return null;
	}

	/**
	 * Searches the list of all technologies (names, versions, and classes) in the database.
	 *
	 * @param  name     if not <code>null</code> only the technologies that have every word in their name are selected
	 * @param  classes  a <code>ArrayList</code> of <code>TechnologyClass</code>es.
	 *                  Only the technologies that have any class are selected.
	 *                  If empty, all are selected.
	 * @param  version  if not <code>null</code> only the technologies that have every word in their version are selected
	 * @param  orderBy  the column that the results will be ordered by
	 *
	 * @return  a <code>TechnologyVersion[]</code> of all the matches
	 */
	public List<TechnologyVersion> getTechnologyVersions(OperatingSystemVersion osv, String name, List<TechnologyClass> classes, String version, int orderBy) throws IOException, SQLException {
		// Prepare names
		String[] nameWords = null;
		if (name != null) {
			nameWords=StringUtility.splitString(name);
			int len = nameWords.length;
			for (int c = 0; c < len; c++) nameWords[c]=nameWords[c].toLowerCase();
		}

		// Version
		String[] versionWords = null;
		if (version != null) {
			versionWords = StringUtility.splitString(version);
			int len = versionWords.length;
			for (int c = 0; c < len; c++) versionWords[c]=versionWords[c].toLowerCase();
		}

		List<TechnologyVersion> table=getRows();
		List<TechnologyVersion> matches=new ArrayList<>();
		int tableSize=table.size();
		for(int c=0;c<tableSize;c++) {
			TechnologyVersion TV=table.get(c);

			// Check the osv
			if(osv==null || osv.equals(TV.getOperatingSystemVersion(connector))) {
				// Check the name
				boolean found=true;
				if(nameWords!=null) {
					String S=TV.name.toLowerCase();
					for (String nameWord : nameWords) {
						if (!S.contains(nameWord)) {
							found=false;
							break;
						}
					}
				}
				if(found) {
					// Check the version
					if(versionWords!=null) {
						String S=TV.version.toLowerCase();
						for (String versionWord : versionWords) {
							if (!S.contains(versionWord)) {
								found=false;
								break;
							}
						}
					}
					if(found) {
						// Check the classes
						if(classes.size()>0) {
							List<Technology> rowClasses=TV.getTechnologyName(connector).getTechnologies(connector);
							found=false;
						Loop:
							for (TechnologyClass matchClass : classes) {
								for (Technology rowClass : rowClasses) {
									if (rowClass.getTechnologyClass(connector).equals(matchClass)) {
										found=true;
										break Loop;
									}
								}
							}
						}

						// Insert sorted to the correct place
						// This is a simple sort algorithm, not the fastest
						if(found) {
							int len=matches.size();
							int addAt=-1;
							for(int d=0;d<len && addAt==-1;d++) {
								TechnologyVersion compVersion=matches.get(d);
								if (orderBy == NAME) {
									if(TV.name.compareToIgnoreCase(compVersion.name)<0) addAt=d;
								} else if (orderBy == VERSION) {
									if(TV.version.compareToIgnoreCase(compVersion.version)<0) addAt=d;
								} else if (orderBy == UPDATED) {
									if(TV.updated>compVersion.updated) addAt=d;
								} else throw new IllegalArgumentException("Invalid value for orderBy: " + orderBy);
							}
							matches.add(addAt==-1?len:addAt, TV);
						}
					}
				}
			}
		}
		// Convert and return the matches
		return matches;
	}
}