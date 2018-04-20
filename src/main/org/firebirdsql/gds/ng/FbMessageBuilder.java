package org.firebirdsql.gds.ng;

import org.firebirdsql.gds.ng.jna.FbException;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public interface FbMessageBuilder {

    void addSmallint(int index, short value) throws FbException;

    void addInteger(int index, int value) throws FbException;

    void addBigint(int index, long value) throws FbException;

    void addFloat(int index, float value) throws FbException;

    void addDouble(int index, double value) throws FbException;

    void addDecfloat16(int index, BigDecimal value) throws FbException;

    void addDecfloat34(int index, BigDecimal value) throws FbException;

    void addBlob(int index, long blobId) throws FbException;

    void addBoolean(int index, boolean value) throws FbException;

    void addDate(int index, Date value) throws FbException;

    void addTime(int index, Time value) throws FbException;

    void addTimestamp(int index, Timestamp value) throws FbException;

    void addChar(int index, String value) throws FbException;

    void addVarchar(int index, String value) throws FbException;

    byte[] getData() throws FbException;

    void clear() throws FbException;

    void addStreamData(byte[] data) throws IOException;

    byte[] getStreamData() throws FbException;

    void clearStream() throws FbException;
}
