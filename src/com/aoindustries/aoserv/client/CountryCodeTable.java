package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  CountryCode
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class CountryCodeTable extends GlobalTableStringKey<CountryCode> {

    CountryCodeTable(AOServConnector connector) {
	super(connector, CountryCode.class);
    }

    public CountryCode get(Object code) {
	return getUniqueRow(CountryCode.COLUMN_CODE, code);
    }

    public List<CountryCode> getCountryCodesByPriority(int prioritySize, int[] priorityCounter) {
        Map<String,int[]> counts = new HashMap<String,int[]>();

        // Add the business_profiles
        Set<String> finishedBusinesses=new HashSet<String>();
        for(BusinessProfile profile : connector.businessProfiles.getRows()) {
            String accounting = profile.accounting;
            if(!finishedBusinesses.contains(accounting)) {
                finishedBusinesses.add(accounting);
                String code = profile.country;
                int[] counter = counts.get(code);
                if(counter == null) counts.put(code, counter = new int[1]);
                counter[0]++;
            }
        }
        
        // Find the biggest ones
        List<String> biggest = new ArrayList<String>(prioritySize);
        Iterator<String> I = counts.keySet().iterator();
        while(I.hasNext()) {
            String code = I.next();
            int count = counts.get(code)[0];
            int c=0;
            for(; c<biggest.size(); c++) {
                String ccCode = biggest.get(c);
                int[] ccCounter = counts.get(ccCode);
                int ccCount = ccCounter==null ? 0 : ccCounter[0];
                if(
                    count>ccCount
                    || (
                        count==ccCount
                        && code.compareToIgnoreCase(ccCode)<=0
                    )
                ) {
                    break;
                }
            }
            if(c<prioritySize) {
                if(biggest.size()>=prioritySize) biggest.remove(prioritySize-1);
                biggest.add(Math.min(c, biggest.size()), code);
            }
        }

        // Package the results
        List<CountryCode> ccs=getRows();
        List<CountryCode> results=new ArrayList<CountryCode>(ccs.size() + biggest.size());
        for(String code : biggest) results.add(get(code));
        results.addAll(ccs);

        // Return the results
        if(priorityCounter!=null && priorityCounter.length>=1) priorityCounter[0]=biggest.size();
        return results;
    }

    int getTableID() {
	return SchemaTable.COUNTRY_CODES;
    }
}