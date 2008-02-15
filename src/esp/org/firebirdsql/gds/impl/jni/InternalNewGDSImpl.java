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
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

public class InternalNewGDSImpl extends InternalGDSImpl {
  private static Logger log = LoggerFactory.getLogger(InternalNewGDSImpl.class,false);
  protected static final byte[] DESCRIBE_DATABASE_INFO_BLOCK = new byte[] {
          ISCConstants.isc_info_db_sql_dialect,
          ISCConstants.frb_info_att_charset,
          ISCConstants.isc_info_isc_version,
          ISCConstants.isc_info_ods_version,
          ISCConstants.isc_info_ods_minor_version,
          ISCConstants.isc_info_end};
  public void iscAttachDatabase(String file_name, IscDbHandle db_handle,
          DatabaseParameterBuffer databaseParameterBuffer)throws GDSException
      {
      if (db_handle == null)
          {
          throw new GDSException(ISCConstants.isc_bad_db_handle);
          }
          isc_tr_handle_impl tr = (isc_tr_handle_impl) createIscTrHandle();
          isc_db_handle_impl db = (isc_db_handle_impl) db_handle;

          native_isc_get_curret_attachment_and_transactional(null,db);
          parseAttachDatabaseInfo(iscDatabaseInfo(db_handle,DESCRIBE_DATABASE_INFO_BLOCK, 1024), db_handle,databaseParameterBuffer);
      }
      public void iscDetachDatabase(IscDbHandle db_handle)throws GDSException
      {
      }
	  
		public void native_isc_attach_database(byte[] bytes, IscDbHandle iscDbHandle, byte[] bytes1) {
	    }

	    public void native_isc_create_database(byte[] bytes, IscDbHandle iscDbHandle, byte[] bytes1) {
	    }

	    public int native_isc_que_events(IscDbHandle iscDbHandle, EventHandleImp eventHandleImp, EventHandler eventHandler) throws GDSException {
	        return 0;
	    }

	    public long native_isc_event_block(EventHandleImp eventHandleImp, String string) throws GDSException {
	        return 0;
	    }

	    public void native_isc_event_counts(EventHandleImp eventHandleImp) throws GDSException {
	    }

	    public void native_isc_cancel_events(IscDbHandle iscDbHandle, EventHandleImp eventHandleImp) throws GDSException {
	    }
	  
      /**
       * Parse database info returned after attach. This method assumes that it is
       * not truncated.
       *
       * @param info
       *            information returned by isc_database_info call
       * @param handle
       *            isc_db_handle to set connection parameters
       * @throws GDSException
       *             if something went wrong :))
       */
      protected void parseAttachDatabaseInfo(byte[] info, IscDbHandle handle,DatabaseParameterBuffer databaseParameterBuffer)
              throws GDSException {
          boolean debug = log != null && log.isDebugEnabled();
          if (debug)
              log.debug("parseDatabaseInfo: first 2 bytes are "
                      + iscVaxInteger(info, 0, 2) + " or: " + info[0] + ", "
                      + info[1]);
          int value = 0;
          int len = 0;
          int i = 0;
          isc_db_handle_impl db = (isc_db_handle_impl) handle;
          while (info[i] != ISCConstants.isc_info_end) {
              switch (info[i++]) {
                  case ISCConstants.isc_info_db_sql_dialect:
                      len = iscVaxInteger(info, i, 2);
                      i += 2;
                      value = iscVaxInteger(info, i, len);
                      i += len;
                      db.setDialect(value);
                      break;
                  case ISCConstants.isc_info_isc_version:
                      len = iscVaxInteger(info, i, 2);
                      i += 2;
                      // This +/-2 offset is to skip count and version string
                      // length
                      byte[] vers = new byte[len - 2];
                      System.arraycopy(info, i + 2, vers, 0, len - 2);
                      String versS = new String(vers);
                      i += len;
                      db.setVersion(versS);
                      break;
                  case ISCConstants.isc_info_ods_version:
                      len = iscVaxInteger(info, i, 2);
                      i += 2;
                      value = iscVaxInteger(info, i, len);
                      i += len;
                      db.setODSMajorVersion(value);
                      break;
                  case ISCConstants.isc_info_ods_minor_version:
                      len = iscVaxInteger(info, i, 2);
                      i += 2;
                      value = iscVaxInteger(info, i, len);
                      i += len;
                      db.setODSMinorVersion(value);
                      break;
                  case ISCConstants.frb_info_att_charset:
                      len = iscVaxInteger(info, i, 2);
                      i += 2;
                      int charSetID=iscVaxInteger(info, i, len);
                      i += len;
                      String charSet=getCharSetNameByID(charSetID);
                      if(charSet!=null)
                      databaseParameterBuffer.addArgument(ISCConstants.isc_dpb_lc_ctype,charSet);
                      break;
                  case ISCConstants.isc_info_truncated:
                      return;
                  default:
                      throw new GDSException(ISCConstants.isc_dsql_sqlda_err);
              }
          }
      }
      public static String getCharSetNameByID(int charSetID)
      {
        switch (charSetID) {
          case 1:
            return "OCTETS";
          case 2:
            return "ASCII";
          case 3:
            return "UNICODE_FSS";
          case 4:
            return "UTF8";
          case 5:
            return "SJIS_0208";
          case 6:
            return "EUCJ_0208";
          case 9:
            return "DOS737";
          case 10:
            return "DOS437";
          case 11:
            return "DOS850";
          case 12:
            return "DOS865";
          case 13:
            return "DOS860";
          case 14:
            return "DOS863";
          case 15:
            return "DOS775";
          case 16:
            return "DOS858";
          case 17:
            return "DOS862";
          case 18:
            return "DOS864";
          case 19:
            return "NEXT";
          case 21:
            return "ISO8859_1";
          case 22:
            return "ISO8859_2";
          case 23:
            return "ISO8859_3";
          case 34:
            return "ISO8859_4";
          case 35:
            return "ISO8859_5";
          case 36:
            return "ISO8859_6";
          case 37:
            return "ISO8859_7";
          case 38:
            return "ISO8859_8";
          case 39:
            return "ISO8859_9";
          case 40:
            return "ISO8859_13";
          case 44:
            return "KSC_5601";
          case 45:
            return "DOS852";
          case 46:
            return "DOS857";
          case 47:
            return "DOS861";
          case 48:
            return "DOS866";
          case 49:
            return "DOS869";
          case 50:
            return "CYRL";
          case 51:
            return "WIN1250";
          case 52:
            return "WIN1251";
          case 53:
            return "WIN1252";
          case 54:
            return "WIN1253";
          case 55:
            return "WIN1254";
          case 56:
            return "BIG_5";
          case 57:
            return "GB_2312";
          case 58:
            return "WIN1255";
          case 59:
            return "WIN1256";
          case 60:
            return "WIN1257";
          case 63:
            return "KOI8R";
          case 64:
            return "KOI8U";
          default:
            return null;
        }
      }
}
