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
package org.firebirdsql.gds.ng;

import org.firebirdsql.gds.DatabaseParameterBuffer;

/**
 * Immutable implementation of {@link org.firebirdsql.gds.ng.IConnectionProperties}.
 *
 * @author @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @see FbConnectionProperties
 * @since 3.0
 */
public final class FbImmutableConnectionProperties extends AbstractImmutableAttachProperties<IConnectionProperties>
        implements IConnectionProperties {

    private final String databaseName;
    private final short connectionDialect;
    private final int pageCacheSize;
    private final boolean resultSetDefaultHoldable;
    private final boolean columnLabelForName;
    private final DatabaseParameterBuffer extraDatabaseParameters;
    private final boolean useGSSAuth;

    /**
     * Copy constructor for FbConnectionProperties.
     * <p>
     * All properties defined in {@link org.firebirdsql.gds.ng.IConnectionProperties} are
     * copied from <code>src</code> to the new instance.
     * </p>
     *
     * @param src
     *         Source to copy from
     */
    public FbImmutableConnectionProperties(IConnectionProperties src) {
        super(src);
        databaseName = src.getDatabaseName();
        connectionDialect = src.getConnectionDialect();
        pageCacheSize = src.getPageCacheSize();
        resultSetDefaultHoldable = src.isResultSetDefaultHoldable();
        columnLabelForName = src.isColumnLabelForName();
        extraDatabaseParameters = src.getExtraDatabaseParameters().deepCopy();
        useGSSAuth = src.isUseGSSAuth();

    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public void setDatabaseName(final String databaseName) {
        immutable();
    }

    @Override
    public String getAttachObjectName() {
        return getDatabaseName();
    }

    @Override
    public boolean isUseGSSAuth() {
        return useGSSAuth;
    }

    @Override
    public void setUseGSSAuth(boolean useGSSAuth) {
        immutable();
    }

    @Override
    public short getConnectionDialect() {
        return connectionDialect;
    }

    @Override
    public void setConnectionDialect(final short connectionDialect) {
        immutable();
    }

    @Override
    public int getPageCacheSize() {
        return pageCacheSize;
    }

    @Override
    public void setPageCacheSize(final int pageCacheSize) {
        immutable();
    }

    @Override
    public void setResultSetDefaultHoldable(final boolean holdable) {
        immutable();
    }

    @Override
    public boolean isResultSetDefaultHoldable() {
        return resultSetDefaultHoldable;
    }

    @Override
    public void setColumnLabelForName(final boolean columnLabelForName) {
        immutable();
    }

    @Override
    public boolean isColumnLabelForName() {
        return columnLabelForName;
    }

    @Override
    public DatabaseParameterBuffer getExtraDatabaseParameters() {
        return extraDatabaseParameters.deepCopy();
    }

    @Override
    public IConnectionProperties asImmutable() {
        // Immutable already, so just return this
        return this;
    }

    @Override
    public IConnectionProperties asNewMutable() {
        return new FbConnectionProperties(this);
    }
}
