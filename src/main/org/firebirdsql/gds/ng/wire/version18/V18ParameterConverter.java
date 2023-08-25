package org.firebirdsql.gds.ng.wire.version18;

import org.firebirdsql.gds.*;
import org.firebirdsql.gds.ng.IConnectionProperties;
import org.firebirdsql.gds.ng.wire.WireDatabaseConnection;
import org.firebirdsql.gds.ng.wire.version13.V13ParameterConverter;
import org.firebirdsql.jaybird.fb.constants.DpbItems;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.firebirdsql.jaybird.fb.constants.DpbItems.isc_dpb_session_time_zone;
import static org.firebirdsql.jaybird.props.PropertyConstants.SESSION_TIME_ZONE_SERVER;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.ParameterConverter} for the version 18 protocol.
 * <p>
 * Adds support for the new authentication model of the V18 protocol.
 * </p>
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 5.0
 */
public class V18ParameterConverter extends V13ParameterConverter {

    private static final Pattern GMT_WITH_OFFSET = Pattern.compile("^GMT([+-]\\d{2}:\\d{2})$");

    @Override
    protected void populateDefaultProperties(final WireDatabaseConnection connection,
                                             final DatabaseParameterBuffer dpb) throws SQLException {
        super.populateDefaultProperties(connection, dpb);

        if (dpb.hasArgument(DpbItems.isc_dpb_session_time_zone)) {
            dpb.removeArgument(isc_dpb_session_time_zone);
            IConnectionProperties props = connection.getAttachProperties();
            String sessionTimeZone = props.getSessionTimeZone();
            if (sessionTimeZone.startsWith("GMT") && sessionTimeZone.length() > 3) {
                Matcher matcher = GMT_WITH_OFFSET.matcher(sessionTimeZone);
                if (matcher.matches()) {
                    sessionTimeZone = matcher.group(1);
                }
            }
            if (sessionTimeZone != null && !SESSION_TIME_ZONE_SERVER.equalsIgnoreCase(sessionTimeZone)) {
                dpb.addArgument(isc_dpb_session_time_zone, sessionTimeZone);
            }
        }
    }
}
