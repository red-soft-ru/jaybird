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
package org.firebirdsql.jdbc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class FBImmutableConnectionProperties extends AbstractFBConnectionProperties implements FirebirdConnectionProperties, Serializable, Cloneable {

    private static final long serialVersionUID = 611228537520889118L;

    /**
     * Copy constructor for FBImmutableConnectionProperties.
     * <p>
     * All properties defined in {@link org.firebirdsql.jdbc.AbstractFBConnectionProperties} are
     * copied from <code>src</code> to the new instance.
     * </p>
     *
     * @param src Source to copy from
     */
    public FBImmutableConnectionProperties(AbstractFBConnectionProperties src) {
        super(src);
    }

    @Override
    protected void dirtied() {

    }

    public int hashCode() {
        return Objects.hash(type, database);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof FBImmutableConnectionProperties)) {
            return false;
        }

        FBImmutableConnectionProperties that = (FBImmutableConnectionProperties) obj;

        boolean result = this.properties.equals(that.properties);
        result &= this.extraDatabaseParameters.equals(that.extraDatabaseParameters);
        result &= Objects.equals(this.type, that.type);
        result &= Objects.equals(this.database, that.database);
        result &= Objects.equals(this.server, that.server);
        result &= Objects.equals(this.sessionTimeZone, that.sessionTimeZone);
        result &= Objects.equals(this.tpbMapping, that.tpbMapping);
        result &= this.port == that.port;
        result &= this.connectionDialect == that.connectionDialect;
        result &= this.socketBufferSize == that.socketBufferSize;
        result &= this.soTimeout == that.soTimeout;
        result &= this.connectTimeout == that.connectTimeout;
        result &= this.defaultTransactionIsolation == that.defaultTransactionIsolation;
        result &= this.customMapping.equals(that.customMapping);
        // If one or both are null we are identical (see also JDBC-249)
        result &= (this.mapper == null || that.mapper == null) || this.mapper.equals(that.mapper);

        return result;
    }

    public Object clone() {
        try {
            FBImmutableConnectionProperties clone = (FBImmutableConnectionProperties) super.clone();

            clone.properties = new HashMap<>(properties);
            clone.customMapping = new HashMap<>(customMapping);
            clone.mapper = mapper != null ? (FBTpbMapper) mapper.clone() : null;
            clone.extraDatabaseParameters = extraDatabaseParameters.deepCopy();

            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new Error("Assertion failure: clone not supported"); // Can't happen
        }
    }

}
