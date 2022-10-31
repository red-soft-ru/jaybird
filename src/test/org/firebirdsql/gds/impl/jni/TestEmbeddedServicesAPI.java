package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.common.rules.GdsTypeRule;
import org.firebirdsql.gds.impl.GDSType;
import org.junit.ClassRule;

public class TestEmbeddedServicesAPI extends TestServicesAPI {

    @ClassRule
    public static final GdsTypeRule testTypes = GdsTypeRule.supports(EmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME);
    public TestEmbeddedServicesAPI() {
        gdsType = GDSType.getType(EmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME);
        protocol = "jdbc:firebirdsql:embedded:";
    }
}
