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

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.IscDbHandle;
import org.firebirdsql.gds.IscTrHandle;

public abstract class InternalGDSImpl extends JniGDSImpl {
  protected InternalGDSImpl() {
    super();
    nativeInitilize();
  }

  protected String getServerUrl(String file_name) throws GDSException { return file_name; }

  public native void native_isc_get_curret_attachment_and_transactional(IscTrHandle tr_handle,IscDbHandle db_handle) throws GDSException;

  public native Object native_isc_get_trigger_field(String name,int is_new, IscDbHandle db_handle) throws GDSException;

  public native void native_isc_set_trigger_field(String name,int is_new,Object field, IscDbHandle db_handle) throws GDSException;

  public native int native_isc_get_trigger_action() throws GDSException;

  public native String native_isc_get_trigger_table_name() throws GDSException;

  protected native void nativeInitilize();

}
