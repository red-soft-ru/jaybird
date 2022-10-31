package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.common.rules.GdsTypeRule;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.gds.impl.nativeoo.FbOONativeGDSFactoryPlugin;
import org.junit.ClassRule;

public class TestFbOONativeServicesAPI extends TestServicesAPI {

    @ClassRule
    public static final GdsTypeRule testType = GdsTypeRule.supportsFBOONativeOnly();

    public TestFbOONativeServicesAPI() {
        gdsType = GDSType.getType(FbOONativeGDSFactoryPlugin.NATIVE_TYPE_NAME);
        protocol = "jdbc:firebirdsql:fboo:native:";
        port = 3050;
    }
}
