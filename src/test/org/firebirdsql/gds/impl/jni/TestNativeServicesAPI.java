package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.common.rules.GdsTypeRule;
import org.firebirdsql.gds.impl.GDSType;
import org.junit.ClassRule;

public class TestNativeServicesAPI extends TestServicesAPI {

    @ClassRule
    public static final GdsTypeRule testTypes = GdsTypeRule.supports(NativeGDSFactoryPlugin.NATIVE_TYPE_NAME);
    public TestNativeServicesAPI() {
        gdsType = GDSType.getType(NativeGDSFactoryPlugin.NATIVE_TYPE_NAME);
        protocol = "jdbc:firebirdsql:native:";
        port = 3050;
    }
}
