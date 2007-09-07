package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.impl.*;

/*
 * <p>Title:Firebird Open Source support Java users function and external procedure</p>
 *
 * Distributable under LGPL license.
 * You may obtain a copy of the License at http://www.gnu.org/copyleft/lgpl.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGPL License for more details.
 *
 *  The Original Code was created by Eugeney Putilin
 *  for the Firebird Open Source RDBMS project.
 *
 *  Copyright (c) 2004 Eugeney Putilin <evgeneyputilin@mail.ru>
 *  and all contributors signed below.
 *
 * All rights reserved.
 *  Contributor(s): ______________________________________.
 *
 * CallJavaMethod is class for wrapper Java methods call
 *
 */

public class GDSInternalHelper extends GDSHelper {

    private static final InternalCurrentGDSImpl gds_int = 
        (InternalCurrentGDSImpl) ((new InternalCurrentGDSFactoryPlugin()).getGDS());

    public GDSInternalHelper() throws GDSException {
        super(gds_int, new DatabaseParameterBufferImp(),
                (AbstractIscDbHandle) gds_int.createIscDbHandle(), null);
        
        setCurrentTrHandle((AbstractIscTrHandle) gds_int.createIscTrHandle());
        
        gds_int.native_isc_get_curret_attachment_and_transactional(
            getCurrentTrHandle(), getCurrentDbHandle());
    }

}
