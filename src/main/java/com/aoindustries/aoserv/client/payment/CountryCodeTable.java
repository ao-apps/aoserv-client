/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.payment;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableStringKey;
import com.aoindustries.aoserv.client.account.Profile;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @see  CountryCode
 *
 * @author  AO Industries, Inc.
 */
final public class CountryCodeTable extends GlobalTableStringKey<CountryCode> {

	public CountryCodeTable(AOServConnector connector) {
		super(connector, CountryCode.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(CountryCode.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public CountryCode get(String code) throws IOException, SQLException {
		return getUniqueRow(CountryCode.COLUMN_CODE, code);
	}

	public List<CountryCode> getCountryCodesByPriority(int prioritySize, int[] priorityCounter) throws IOException, SQLException {
		Map<String,int[]> counts = new HashMap<>();

		// Add the business_profiles
		Set<AccountingCode> finishedBusinesses=new HashSet<>();
		for(Profile profile : connector.getBusinessProfiles().getRows()) {
			AccountingCode accounting = profile.getBusiness_accounting();
			if(!finishedBusinesses.contains(accounting)) {
				finishedBusinesses.add(accounting);
				String code = profile.getCountry_code();
				int[] counter = counts.get(code);
				if(counter == null) counts.put(code, counter = new int[1]);
				counter[0]++;
			}
		}

		// Find the biggest ones
		List<String> biggest = new ArrayList<>(prioritySize);
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
		List<CountryCode> results=new ArrayList<>(ccs.size() + biggest.size());
		for(String code : biggest) results.add(get(code));
		results.addAll(ccs);

		// Return the results
		if(priorityCounter!=null && priorityCounter.length>=1) priorityCounter[0]=biggest.size();
		return results;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.COUNTRY_CODES;
	}
}
