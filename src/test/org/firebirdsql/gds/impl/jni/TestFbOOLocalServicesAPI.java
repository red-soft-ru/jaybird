package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.common.rules.GdsTypeRule;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.gds.impl.nativeoo.FbOOLocalGDSFactoryPlugin;
import org.junit.ClassRule;

public class TestFbOOLocalServicesAPI extends TestServicesAPI {

    @ClassRule
    public static final GdsTypeRule testType = GdsTypeRule.supportsFBOONativeOnly();

    public TestFbOOLocalServicesAPI() {
        gdsType = GDSType.getType(FbOOLocalGDSFactoryPlugin.LOCAL_TYPE_NAME);
        protocol = "jdbc:firebirdsql:fboo:local:";
    }
}
