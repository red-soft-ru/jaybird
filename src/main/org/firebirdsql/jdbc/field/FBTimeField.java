/*
 * Firebird Open Source JDBC Driver
 *
 * Distributable under LGPL license.
 * You may obtain a copy of the License at http://www.gnu.org/copyleft/lgpl.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGPL License for more details.
 *
 * This file was created by members of the firebird development team.
 * All individual contributions remain the Copyright (C) of those
 * individuals.  Contributors to this file are either listed here or
 * can be obtained from a source control history command.
 *
 * All rights reserved.
 */
package org.firebirdsql.jdbc.field;

import org.firebirdsql.gds.ng.fields.FieldDescriptor;
import org.firebirdsql.jaybird.util.FbDatetimeConversion;
import org.jspecify.annotations.NullMarked;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Field implementation for {@code TIME (WITHOUT TIME ZONE)}.
 *
 * @author Roman Rokytskyy
 * @author Mark Rotteveel
 */
@SuppressWarnings("RedundantThrows")
final class FBTimeField extends AbstractWithoutTimeZoneField {

    private static final LocalDate LOCAL_DATE_EPOCH = LocalDate.of(1970, 1, 1);

    @NullMarked
    FBTimeField(FieldDescriptor fieldDescriptor, FieldDataProvider dataProvider, int requiredType) throws SQLException {
        super(fieldDescriptor, dataProvider, requiredType);
    }

    @Override
    public Object getObject() throws SQLException {
        return getTime();
    }

    @Override
    public String getString() throws SQLException {
        return convertForGet(getLocalTime(), FbDatetimeConversion::formatSqlTime, String.class);
    }

    @Override
    LocalTime getLocalTime() throws SQLException {
        return getDatatypeCoder().decodeLocalTime(getFieldData());
    }

    public int getInt() throws SQLException {
        final byte[] fieldData = getFieldData();
        if (fieldData ==null) return INT_NULL_VALUE;
        final int i = getDatatypeCoder().decodeInt(getFieldData());
        return i;
    }

    public long getLong() throws SQLException {
        final byte[] fieldData = getFieldData();
        if (fieldData ==null) return LONG_NULL_VALUE;
        final int i = getDatatypeCoder().decodeInt(getFieldData());
        return i;
    }

    @Override
    LocalDateTime getLocalDateTime() throws SQLException {
        LocalTime localTime = getLocalTime();
        return localTime != null ? localTime.atDate(LOCAL_DATE_EPOCH) : null;
    }
    //--- setXXX methods

    @Override
    public void setString(String value) throws SQLException {
        setLocalTime(fromString(value, FbDatetimeConversion::parseSqlTime));
    }

    @Override
    void setLocalDateTime(LocalDateTime value) throws SQLException {
        setLocalTime(value != null ? value.toLocalTime() : null);
    }

    @Override
    void setLocalTime(LocalTime value) throws SQLException {
        setFieldData(getDatatypeCoder().encodeLocalTime(value));
    }

    public void setInteger(int value) throws SQLException {
        if (value == INT_NULL_VALUE) {
            setNull();
            return;
        }

        setFieldData(getDatatypeCoder().encodeInt(value));
    }

    public void setLong(long value) throws SQLException {
        if (value == LONG_NULL_VALUE) {
            setNull();
            return;
        }
        final int t = (int)value;
        setFieldData(getDatatypeCoder().encodeInt(t));
    }
}
