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

import org.firebirdsql.gds.*;
import org.firebirdsql.gds.impl.DatabaseParameterBufferImp;
import org.firebirdsql.gds.impl.ServiceParameterBufferImp;
import org.firebirdsql.jdbc.FirebirdConnectionProperties;

import java.sql.SQLException;

import static org.firebirdsql.gds.ISCConstants.*;

/**
 * Abstract class for behavior common to {@code ParameterConverter} implementations.
 *
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @since 3.0
 */
public abstract class AbstractParameterConverter<D extends AbstractConnection<FirebirdConnectionProperties, ?>, S extends AbstractConnection<FirebirdConnectionProperties, ?>>
        implements ParameterConverter<D, S> {

    protected DatabaseParameterBuffer createDatabaseParameterBuffer(final D connection) {
        return new DatabaseParameterBufferImp(DatabaseParameterBufferImp.DpbMetaData.DPB_VERSION_1,
                connection.getEncoding());
    }

    protected ServiceParameterBuffer createServiceParameterBuffer(final S connection) {
        return new ServiceParameterBufferImp(ServiceParameterBufferImp.SpbMetaData.SPB_VERSION_2_ATTACH,
                connection.getEncoding());
    }

    @Override
    public final DatabaseParameterBuffer toDatabaseParameterBuffer(final D connection) throws SQLException {
        DatabaseParameterBuffer dpb = createDatabaseParameterBuffer(connection);

        // Map standard properties
        populateDefaultProperties(connection, dpb);

        // Map non-standard properties
        populateNonStandardProperties(connection, dpb);

        return dpb;
    }

    /**
     * Populates the database parameter buffer with the standard Firebird properties explicitly supported through
     * {@code IConnectionProperties}.
     *
     * @param connection
     *         Database connection
     * @param dpb
     *         Database parameter buffer to populate
     * @throws SQLException
     *         For errors generating authentication information
     */
    protected void populateDefaultProperties(final D connection, final DatabaseParameterBuffer dpb) throws SQLException {
        dpb.addArgument(isc_dpb_lc_ctype, connection.getEncodingDefinition().getFirebirdEncodingName());
        FirebirdConnectionProperties props = connection.getAttachProperties();
        if (props.getPageCacheSize() != FirebirdConnectionProperties.DEFAULT_BUFFERS_NUMBER) {
            dpb.addArgument(isc_dpb_num_buffers, props.getPageCacheSize());
        }
        populateAuthenticationProperties(connection, dpb);
        if (props.getRoleName() != null) {
            dpb.addArgument(isc_dpb_sql_role_name, props.getRoleName());
        }
        dpb.addArgument(isc_dpb_sql_dialect, props.getConnectionDialect());
        if (props.getConnectTimeout() != FirebirdConnectionProperties.DEFAULT_CONNECT_TIMEOUT) {
            dpb.addArgument(isc_dpb_connect_timeout, props.getConnectTimeout());
        }
    }

    /**
     * Populates the authentication properties of the parameter buffer.
     *
     * @param connection
     *         Database connection
     * @param pb
     *         Parameter buffer to populate
     * @throws SQLException
     *         For errors generating authentication information
     */
    protected abstract void populateAuthenticationProperties(AbstractConnection connection,
            ConnectionParameterBuffer pb) throws SQLException;

    /**
     * Populates the database parameter buffer with the non-standard properties (in
     * {@link org.firebirdsql.jdbc.FirebirdConnectionProperties#getExtraDatabaseParameters()}).
     *
     *  @param connection
     *         Database connection
     * @param dpb
     *         Database parameter buffer to populate
     */
    protected void populateNonStandardProperties(D connection, final DatabaseParameterBuffer dpb) {
        for (Parameter parameter : connection.getAttachProperties().getExtraDatabaseParameters()) {
            parameter.copyTo(dpb, dpb.getDefaultEncoding());
        }
    }

    @Override
    public final ServiceParameterBuffer toServiceParameterBuffer(final S connection) throws SQLException {
        final ServiceParameterBuffer spb = createServiceParameterBuffer(connection);

        // Map standard properties
        populateDefaultProperties(connection, spb);

        // Map non-standard properties
        populateNonStandardProperties(connection, spb);

        return spb;
    }

    /**
     * Populates the database parameter buffer with the standard Firebird properties explicitly supported through
     * {@code IConnectionProperties}.
     *
     * @param connection
     *         Service connection
     * @param spb
     *         Service parameter buffer to populate
     * @throws SQLException
     *         For errors generating authentication information
     */
    protected void populateDefaultProperties(final S connection, final ServiceParameterBuffer spb) throws SQLException {
        populateAuthenticationProperties(connection, spb);
        FirebirdConnectionProperties props = connection.getAttachProperties();
        if (props.getRoleName() != null) {
            spb.addArgument(isc_spb_sql_role_name, props.getRoleName());
        }
        if (props.getConnectTimeout() != FirebirdConnectionProperties.DEFAULT_CONNECT_TIMEOUT) {
            spb.addArgument(isc_spb_connect_timeout, props.getConnectTimeout());
        }
    }

    /**
     * Populates the service parameter buffer with the non-standard properties.
     *
     *  @param connection
     *         Service connection
     * @param spb
     *         Database parameter buffer to populate
     */
    protected void populateNonStandardProperties(S connection, final ServiceParameterBuffer spb) {

        FirebirdConnectionProperties properties = connection.getAttachProperties();
        ParameterTagMapping tagMapping = spb.getTagMapping();
        if (properties.getNonStandardProperty("isc_spb_multi_factor_auth") != null) {
            spb.addArgument(isc_spb_multi_factor_auth, properties.getNonStandardProperty("isc_spb_multi_factor_auth"));
        }
        if (properties.getNonStandardProperty("isc_spb_trusted_auth") != null) {
            spb.addArgument(isc_spb_trusted_auth, properties.getNonStandardProperty("isc_spb_trusted_auth"));
        }
        if (properties.getNonStandardProperty("isc_spb_process_name") != null) {
            spb.addArgument(isc_spb_process_name, properties.getNonStandardProperty("isc_spb_process_name"));
        }
        if (properties.getNonStandardProperty("isc_spb_config") != null) {
            spb.addArgument(tagMapping.getConfigTag(), properties.getNonStandardProperty("isc_spb_config"));
        }
        if (properties.getNonStandardProperty("isc_spb_expected_db") != null) {
            spb.addArgument(isc_spb_expected_db, properties.getNonStandardProperty("isc_spb_expected_db"));
        }
        if (properties.getNonStandardProperty("isc_spb_trusted_role") != null) {
            spb.addArgument(isc_spb_trusted_role, properties.getNonStandardProperty("isc_spb_trusted_role"));
        }
        if (properties.getNonStandardProperty("isc_spb_process_id") != null) {
            spb.addArgument(isc_spb_process_id, Integer.parseInt(properties.getNonStandardProperty("isc_spb_process_id")));
        }
        if (properties.getNonStandardProperty("isc_spb_remote_protocol") != null) {
            spb.addArgument(isc_spb_remote_protocol, properties.getNonStandardProperty("isc_spb_remote_protocol"));
        }
        if (properties.getNonStandardProperty("provider_id") != null) {
            spb.addArgument(isc_spb_provider_id, properties.getNonStandardProperty("provider_id"));
        }
    }
}
