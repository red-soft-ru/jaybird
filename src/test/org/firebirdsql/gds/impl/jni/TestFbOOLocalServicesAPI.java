package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.gds.impl.GDSType;

public class TestFbOOLocalServicesAPI extends TestServicesAPI {

    public TestFbOOLocalServicesAPI() {
        gdsType = GDSType.getType(FbOOLocalGDSFactoryPlugin.LOCAL_TYPE_NAME);
        protocol = "jdbc:firebirdsql:fboo:local:";
    }
}
