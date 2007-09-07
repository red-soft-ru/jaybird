/*
 * Firebird Open Source J2ee connector - jdbc driver
 *
 * Distributable under LGPL license.
 * You may obtain a copy of the License at http://www.gnu.org/copyleft/lgpl.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGPL License for more details.
 *
 * This file was created by members of the firebird development team.
 * All individual contributions remain the Copyright (C) of those
 * individuals.  Contributors to this file are either listed here or
 * can be obtained from a CVS history command.
 *
 * All rights reserved.
 */
package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.gds.*;
import org.firebirdsql.gds.impl.AbstractIscTrHandle;

public class InternalCurrentGDSImpl extends InternalNewGDSImpl {
      public void iscStartTransaction(IscTrHandle tr_handle,
              IscDbHandle db_handle, TransactionParameterBuffer tpb)
              throws GDSException {
          isc_tr_handle_impl tr = (isc_tr_handle_impl) tr_handle;
          isc_db_handle_impl db = (isc_db_handle_impl) db_handle;

          if (tr == null) {
              throw new GDSException(ISCConstants.isc_bad_trans_handle);
          }

          if (db == null) {
              throw new GDSException(ISCConstants.isc_bad_db_handle);
          }

          synchronized(db_handle)
              {
              if (tr.getState() != AbstractIscTrHandle.NOTRANSACTION)
                      throw new GDSException(ISCConstants.isc_tra_state);

              tr.setState(AbstractIscTrHandle.TRANSACTIONSTARTING);
              native_isc_get_curret_attachment_and_transactional(tr_handle,db_handle);
              tr.setDbHandle((isc_db_handle_impl)db_handle);

              tr.setState(AbstractIscTrHandle.TRANSACTIONSTARTED);
              }
          }

    /**
     * /{@inheritDoc}
     */
    public boolean equals(Object obj) {
        return obj!=null&&getClass().getName().equals(obj.getClass().getName());
    }
    /*
          public void iscCommitTransaction(IscTrHandle tr_handle) throws GDSException {

          }
          public void iscCommitRetaining(IscTrHandle tr_handle) throws GDSException
          {}
          public void iscPrepareTransaction(IscTrHandle tr_handle) throws GDSException
          {}
          public void iscPrepareTransaction2(IscTrHandle tr_handle, byte[] bytes) throws GDSException
          {}
          public void iscRollbackRetaining(IscTrHandle tr_handle) throws GDSException
          {}
          public void iscRollbackTransaction(IscTrHandle tr_handle) throws GDSException
          {}
          */

}
