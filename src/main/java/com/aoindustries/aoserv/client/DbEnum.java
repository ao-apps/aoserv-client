/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2026  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.schema.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry of converters from Java {@link Enum} to/from the underlying PostgreSQL enum value.
 * All enum types used for implementations of {@link Type#ENUM} must be registered.
 *
 * @author  AO Industries, Inc.
 */
public final class DbEnum {

  /** Make no instances. */
  private DbEnum() {
    throw new AssertionError();
  }

  @FunctionalInterface
  public static interface ToDbValue<E extends Enum<?>> {
    /**
     * Gets the string representation of the given enum matching the underlying PostgreSQL enum value.
     *
     * @param  value  The enum value, never {@code null}
     *                (ToDbValue is not resolved or called for {@code null} values).
     *
     * @return  The database value.  This does not directly match the value from the database, but will match the
     *          underlying database value at the time of this client schema version.  In other words, the underlying
     *          database may change, but this value will stay constant within a single schema version.
     */
    public String toDbValue(E value);
  }

  @FunctionalInterface
  public static interface FromDbValue<E extends Enum<?>> {
    /**
     * Gets the {@link Enum} for the given database value.
     *
     * @param  dbValue  The database value, never {@code null}
     *                  (FromDbValue is not resolved or called for {@code null} values).
     *
     * @throws IllegalArgumentException when unable to map the database value to the {@link Enum}.
     */
    public E fromDbValue(String dbValue) throws IllegalArgumentException;
  }

  private static final Map<Class<? extends Enum<?>>, ToDbValue<? extends Enum<?>>> toDbValues = new ConcurrentHashMap<>();

  private static final Map<Class<? extends Enum<?>>, FromDbValue<? extends Enum<?>>> fromDbValues = new ConcurrentHashMap<>();

  /**
   * Registers a new type with provided {@link ToDbValue} and {@link FromDbValue}.
   */
  public static <E extends Enum<?>> void register(Class<E> enumClass, ToDbValue<E> toDbValue, FromDbValue<E> fromDbValue) {
    toDbValues.put(enumClass, toDbValue);
    fromDbValues.put(enumClass, fromDbValue);
  }

  /**
   * Registers a new type with default {@link ToDbValue} of {@link Enum#name()} and {@link FromDbValue} of {@link Enum#valueOf(java.lang.Class, java.lang.String)}.
   */
  @SuppressWarnings("unchecked")
  public static <E extends Enum<?>> void register(Class<E> enumClass) {
    register(enumClass, Enum::name, (FromDbValue) (dbValue -> Enum.valueOf((Class) enumClass, dbValue)));
  }

  /**
   * Maps an {@link Enum} value to the matching database value.
   *
   * <p>This does not directly match the value from the database, but will match the underlying database value at the
   * time of this client schema version.  In other words, the underlying database may change, but this value will stay
   * constant within a single schema version.</p>
   *
   * @param  value  The Java enum value, may be {@code null}
   *
   * @return  The Java enum value corresponding to the give database value or {@code null} when {@code dbValue == null}.
   *
   * @throws IllegalStateException if {@link ToDbValue} not registered or {@link ToDbValue} returns unexpected {@code null}
   */
  @SuppressWarnings("unchecked")
  public static <E extends Enum<?>> String toDbValue(E value) throws IllegalStateException {
    if (value == null) {
      return null;
    }
    Class<E> enumClass = (Class) value.getClass();
    ToDbValue<E> toDbValue;
    {
      Class<?> searchClass = enumClass;
      do {
        toDbValue = (ToDbValue) toDbValues.get((Class) searchClass);
        if (toDbValue != null) {
          break;
        }
        searchClass = searchClass.getSuperclass();
      } while (searchClass != Enum.class && searchClass != null);
    }
    if (toDbValue == null) {
      throw new IllegalStateException("No ToDbValue registered for " + enumClass.getName());
    }
    String dbValue = toDbValue.toDbValue(value);
    if (dbValue == null) {
      throw new IllegalStateException("ToDbValue returned unexpected null: toDbValue = "
          + toDbValue.getClass().getName() + ", dbValue = " + dbValue);
    }
    return dbValue;
  }

  /**
   * Maps a database value to the matching {@link Enum}.
   *
   * <p>This does not directly match the value from the database, but will match the underlying database value at the
   * time of this client schema version.  In other words, the underlying database may change, but this value will stay
   * constant within a single schema version.</p>
   *
   * @param  dbValue  The database value, may be {@code null}
   *
   * @return  The Java enum value corresponding to the give database value or {@code null} when {@code dbValue == null}.
   *
   * @throws IllegalStateException if {@link FromDbValue} not registered or {@link FromDbValue} returns unexpected {@code null}
   * @throws IllegalArgumentException when unable to map the database value to the {@link Enum}.
   */
  public static <E extends Enum<?>> E fromDbValue(Class<E> enumClass, String dbValue)
      throws IllegalStateException, IllegalArgumentException {
    if (dbValue == null) {
      return null;
    }
    @SuppressWarnings("unchecked")
    FromDbValue<E> fromDbValue = (FromDbValue) fromDbValues.get(enumClass);
    if (fromDbValue == null) {
      throw new IllegalStateException("No FromDbValue registered for " + enumClass.getName());
    }
    E value = fromDbValue.fromDbValue(dbValue);
    if (value == null) {
      throw new IllegalStateException("FromDbValue returned unexpected null: fromDbValue = " + fromDbValue.getClass().getName()
          + ", dbValue = " + dbValue);
    }
    return value;
  }
}
