package org.firebirdsql.nativeoo.gds.ng;

import org.firebirdsql.gds.ConnectionParameterBuffer;
import org.firebirdsql.gds.ParameterTagMapping;
import org.firebirdsql.gds.ng.AbstractConnection;
import org.firebirdsql.gds.ng.AbstractParameterConverter;
import org.firebirdsql.gds.ng.WireCrypt;
import org.firebirdsql.jdbc.FirebirdConnectionProperties;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class IParameterConverterImpl extends AbstractParameterConverter<NativeDatabaseConnection, IServiceConnectionImpl> {

    @Override
    protected void populateAuthenticationProperties(final AbstractConnection connection,
                                                    final ConnectionParameterBuffer pb) throws SQLException {
        FirebirdConnectionProperties props = connection.getAttachProperties();
        ParameterTagMapping tagMapping = pb.getTagMapping();
        if (props.getUserName() != null) {
            pb.addArgument(tagMapping.getUserNameTag(), props.getUserName());
        }
        if (props.getPassword() != null) {
            pb.addArgument(tagMapping.getPasswordTag(), props.getPassword());
        }
        if (props.getEffectiveLogin() != null) {
            pb.addArgument(tagMapping.getEffectiveLoginTag(), props.getEffectiveLogin());
        }

        Map<String, String> configMap = new HashMap<>();

        if (WireCrypt.fromString(props.getWireCrypt()) != WireCrypt.DEFAULT) {
            configMap.put("WireCrypt", props.getWireCrypt());
        }

        String authPlugins = props.getAuthPlugins();
        if (authPlugins != null && !authPlugins.isEmpty()) {
            configMap.put("AuthClient", authPlugins);
        }

        if (!configMap.isEmpty()) {
            String configString = buildConfigString(configMap);
            pb.addArgument(tagMapping.getConfigTag(), configString);
        }
    }

    private String buildConfigString(Map<String, String> configMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> configEntry : configMap.entrySet()) {
            builder.append(configEntry.getKey())
                    .append('=')
                    .append(configEntry.getValue())
                    .append('\n');
        }
        return builder.toString();
    }
}
