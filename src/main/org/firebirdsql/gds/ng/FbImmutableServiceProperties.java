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

/**
 * Immutable implementation of {@link org.firebirdsql.gds.ng.IServiceProperties}.
 *
 * @author @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @see FbServiceProperties
 * @since 3.0
 */
public final class FbImmutableServiceProperties extends AbstractImmutableAttachProperties<IServiceProperties>
        implements IServiceProperties {

    private final String serviceName;
    private final boolean useGSSAuth;

    /**
     * Copy constructor for FbServiceProperties.
     * <p>
     * All properties defined in {@link IServiceProperties} are  copied from <code>src</code> to the new instance.
     * </p>
     *
     * @param src
     *         Source to copy from
     */
    public FbImmutableServiceProperties(IServiceProperties src) {
        super(src);
        serviceName = src.getServiceName();
        useGSSAuth = src.isUseGSSAuth();
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void setServiceName(String serviceName) {
        immutable();
    }

    @Override
    public String getAttachObjectName() {
        return getServiceName();
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
    public IServiceProperties asImmutable() {
        // Immutable already, so just return this
        return this;
    }

    @Override
    public IServiceProperties asNewMutable() {
        return new FbServiceProperties(this);
    }
}
