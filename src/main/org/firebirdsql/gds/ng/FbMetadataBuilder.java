package org.firebirdsql.gds.ng;

import org.firebirdsql.gds.ng.jna.FbException;

/**
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public interface FbMetadataBuilder {

    FbMessageMetadata getMessageMetadata() throws FbException;

    int addField() throws FbException;

    void addSmallint(int index) throws FbException;

    void addInteger(int index) throws FbException;

    void addBigint(int index) throws FbException;

    void addFloat(int index) throws FbException;

    void addDouble(int index) throws FbException;

    void addDecfloat16(int index) throws FbException;

    void addDecfloat34(int index) throws FbException;

    void addBlob(int index)  throws FbException;

    void addBoolean(int index)  throws FbException;

    void addDate(int index)  throws FbException;

    void addTime(int index)  throws FbException;

    void addTimestamp(int index)  throws FbException;

    void addChar(int index, int length)  throws FbException;

    void addVarchar(int index, int length)  throws FbException;

    void addChar(int index, int length, int charSet)  throws FbException;

    void addVarchar(int index, int length, int charSet)  throws FbException;
}
