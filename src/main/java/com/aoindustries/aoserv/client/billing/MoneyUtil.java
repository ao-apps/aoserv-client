/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

/**
 * Helper functions for {@link Money}.
 *
 * @author  AO Industries, Inc.
 */
public class MoneyUtil {

	private MoneyUtil() {
	}

	/**
	 * null-safe getDto for Money.
	 */
	public static com.aoindustries.aoserv.client.dto.Money getDto(Money money) {
		return money == null ? null : new com.aoindustries.aoserv.client.dto.Money(money.getCurrency().getCurrencyCode(), money.getValue());
	}

	/**
	 * null-safe money conversion.
	 */
	public static Money getMoney(com.aoindustries.aoserv.client.dto.Money money) {
		if(money == null) return null;
		return new Money(Currency.getInstance(money.getCurrency()), money.getValue());
	}

	/**
	 * null-safe money conversion.
	 */
	public static Money getMoney(Currency currency, BigDecimal value) {
		if(value == null) return null;
		return new Money(currency, value);
	}

    /**
     * Gets a {@link Money} type from two columns of a {@link ResultSet}.  Supports
     * {@code null}.  If value is non-null then currency must also be non-null.
     */
    public static Money getMoney(ResultSet result, String currencyColumnLabel, String valueColumnLabel) throws SQLException {
        BigDecimal value = result.getBigDecimal(valueColumnLabel);
        if(value == null) return null;
        String currencyCode = result.getString(currencyColumnLabel);
        if(currencyCode == null) throw new SQLException(currencyColumnLabel + " == null && " + valueColumnLabel+" != null");
        return new Money(Currency.getInstance(currencyCode), value);
    }

	public static void writeMoney(Money money, StreamableOutput out) throws IOException {
		out.writeUTF(money.getCurrency().getCurrencyCode());
		out.writeLong(money.getUnscaledValue());
		out.writeCompressedInt(money.getScale());
	}

	public static void writeNullMoney(Money money, StreamableOutput out) throws IOException {
		if(money != null) {
			out.writeBoolean(true);
			writeMoney(money, out);
		} else {
			out.writeBoolean(false);
		}
	}

	public static Money readMoney(StreamableInput in) throws IOException {
		return new Money(Currency.getInstance(in.readUTF()), in.readLong(), in.readCompressedInt());
	}

	public static Money readNullMoney(StreamableInput in) throws IOException {
		return in.readBoolean() ? readMoney(in) : null;
	}
}
