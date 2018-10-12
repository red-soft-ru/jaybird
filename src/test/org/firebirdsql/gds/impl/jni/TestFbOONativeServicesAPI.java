package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.gds.impl.GDSType;

public class TestFbOONativeServicesAPI extends TestServicesAPI {

    public TestFbOONativeServicesAPI() {
        gdsType = GDSType.getType(FbOONativeGDSFactoryPlugin.NATIVE_TYPE_NAME);
        protocol = "jdbc:firebirdsql:fboo:native:";
        port = 3050;
    }
}
