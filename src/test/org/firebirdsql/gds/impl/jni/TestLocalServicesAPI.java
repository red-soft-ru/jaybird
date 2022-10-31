package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.common.rules.GdsTypeRule;
import org.firebirdsql.gds.impl.GDSType;
import org.junit.ClassRule;

public class TestLocalServicesAPI extends TestServicesAPI {

    @ClassRule
    public static final GdsTypeRule testTypes = GdsTypeRule.supports(LocalGDSFactoryPlugin.LOCAL_TYPE_NAME);

    public TestLocalServicesAPI() {
        gdsType = GDSType.getType(LocalGDSFactoryPlugin.LOCAL_TYPE_NAME);
        protocol = "jdbc:firebirdsql:local:";
    }
}
