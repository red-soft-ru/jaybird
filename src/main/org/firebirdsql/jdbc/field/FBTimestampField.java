/*
 * Firebird Open Source JavaEE Connector - JDBC Driver
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

import org.firebirdsql.gds.ng.DatatypeCoder;
import org.firebirdsql.gds.ng.fields.FieldDescriptor;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * Field implementation for {@code TIMESTAMP (WITHOUT TIME ZONE)}.
 *
 * @author <a href="mailto:rrokytskyy@users.sourceforge.net">Roman Rokytskyy</a>
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 */
class FBTimestampField extends AbstractWithoutTimeZoneField {

    FBTimestampField(FieldDescriptor fieldDescriptor, FieldDataProvider dataProvider, int requiredType)
            throws SQLException {
        super(fieldDescriptor, dataProvider, requiredType);
    }

    @Override
    public Object getObject() throws SQLException {
        return getTimestamp();
    }

    public String getString() throws SQLException {
        if (isNull()) return null;

        return String.valueOf(getTimestamp());
    }

    public Date getDate(Calendar cal) throws SQLException {
        if (isNull()) return null;

        return new java.sql.Date(getDatatypeCoder().decodeTimestampCalendar(getFieldData(), cal).getTime());
    }

    @Override
    LocalDate getLocalDate() throws SQLException {
        LocalDateTime localDateTime = getLocalDateTime();
        return localDateTime != null ? localDateTime.toLocalDate() : null;
    }

    public Time getTime(Calendar cal) throws SQLException {
        if (isNull()) return null;

        return new java.sql.Time(getDatatypeCoder().decodeTimestampCalendar(getFieldData(), cal).getTime());
    }

    @Override
    LocalTime getLocalTime() throws SQLException {
        LocalDateTime localDateTime = getLocalDateTime();
        return localDateTime != null ? localDateTime.toLocalTime() : null;
    }

    public Timestamp getTimestamp(Calendar cal) throws SQLException {
        if (isNull()) return null;

        return getDatatypeCoder().decodeTimestampCalendar(getFieldData(), cal);
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
        final long d = getDatatypeCoder().decodeInt(getFieldData());
        System.arraycopy(fieldData, 4, fieldData, 0, 4);
        final int t = getDatatypeCoder().decodeInt(getFieldData());
        return (d << 32) + t;
    }
            
    @Override
    LocalDateTime getLocalDateTime() throws SQLException {
        if (isNull()) return null;
        // TODO Push down into DatatypeCoder
        final DatatypeCoder.RawDateTimeStruct raw = getDatatypeCoder().decodeTimestampRaw(getFieldData());
        return LocalDateTime.of(raw.year, raw.month, raw.day, raw.hour, raw.minute, raw.second,
                raw.getFractionsAsNanos());
    }

    public void setString(String value) throws SQLException {
        setTimestamp(fromString(value, Timestamp::valueOf));
    }

    public void setDate(Date value, Calendar cal) throws SQLException {
        if (setWhenNull(value)) return;

        setFieldData(getDatatypeCoder().encodeTimestampCalendar(new java.sql.Timestamp(value.getTime()), cal));
    }

    public void setTime(Time value, Calendar cal) throws SQLException {
        if (setWhenNull(value)) return;

        setFieldData(getDatatypeCoder().encodeTimestampCalendar(new java.sql.Timestamp(value.getTime()), cal));
    }

    public void setTimestamp(Timestamp value, Calendar cal) throws SQLException {
        if (setWhenNull(value)) return;

        setFieldData(getDatatypeCoder().encodeTimestampCalendar(value, cal));
    }

    @Override
    void setLocalDateTime(LocalDateTime value) throws SQLException {
        if (setWhenNull(value)) return;
        // TODO Push down into DatatypeCoder
        setFieldData(getDatatypeCoder().encodeLocalDateTime(
                value.getYear(), value.getMonthValue(), value.getDayOfMonth(),
                value.getHour(), value.getMinute(), value.getSecond(), value.getNano()));
    }

    @Override
    public DatatypeCoder.RawDateTimeStruct getRawDateTimeStruct() throws SQLException {
        if (isNull()) return null;
        return getDatatypeCoder().decodeTimestampRaw(getFieldData());
    }

    @Override
    public void setRawDateTimeStruct(DatatypeCoder.RawDateTimeStruct raw) throws SQLException {
        if (setWhenNull(raw)) return;
        setFieldData(getDatatypeCoder().encodeTimestampRaw(raw));
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
        final int d = (int)(value >> 32);
        final byte[] b = new byte[8];
        System.arraycopy(getDatatypeCoder().encodeInt(d), 0, b, 0, 4);
        System.arraycopy(getDatatypeCoder().encodeInt(t), 0, b, 4, 4);
        setFieldData(b);
    }
}
