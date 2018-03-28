package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.jna.fbclient.FbInterface.IMaster;

public class FbInterfaceImpl {
    public static IMaster getMasterInterface() {
        return FbClientDatabaseFactory.getInstance().getClientLibrary().fb_get_master_interface();
    }
}
