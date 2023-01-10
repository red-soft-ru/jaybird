/*
 * Firebird Open Source JDBC Driver
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
 * can be obtained from a source control history command.
 *
 * All rights reserved.
 */
package org.firebirdsql.jaybird.xca;

import org.firebirdsql.gds.ISCConstants;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Helper class for the exception handling in XCA framework. JCA specification
 * required resource adapter to report an error if it is certain that no other
 * operations can be executed over that particular managed connection.
 * <p>
 * In case of Firebird, few errors belong to the so-called "fatal errors", after
 * which client application cannot continue its job. For example, when socket
 * connection to the server is broken, any subsequent operation will fail. XCA
 * container should remove the connection from the pool in order to allow process
 * to recover (when Firebird server is restarted).
 * </p>
 *
 * @author Roman Rokytskyy
 */
final class FatalGDSErrorHelper {

    private FatalGDSErrorHelper() {
        // no instances
    }

    /**
     * Check whether the specified exception is fatal from the XCA point of view.
     *
     * @param ex
     *         exception to check.
     * @return {@code true} if the exception that happened is fatal
     */
    static boolean isFatal(SQLException ex) {
        int errorCode = ex.getErrorCode();
        return Arrays.binarySearch(FATAL_ERRORS, errorCode) >= 0;
    }

    /**
     * The constant array {@code FATAL_ERRORS} holds an ORDERED list of isc error codes that indicate that the
     * connection is no longer usable. This is used in the XCA framework to determine if a SQLException should result
     * in a ConnectionErrorOccurred notification to the Connection Manager to destroy the connection. It is essential
     * that this list be ordered so determining if a code is in it can proceed reliably.
     * <p>
     * This list has been kindly reviewed by Ann Harrison, 12/13/2002
     * </p>
     */
    private static final int[] FATAL_ERRORS = new int[] {
            ISCConstants.isc_network_error,
            ISCConstants.isc_net_read_err,
            ISCConstants.isc_net_write_err,
//        ISCConstants.isc_bad_db_format,   //probably not a firebird db
//        ISCConstants.isc_bad_db_handle,   //couldn't get a connection
//        ISCConstants.isc_bad_dpb_content, //couldn't get a connection
//        ISCConstants.isc_bad_dpb_form,    //couldn't get a connection
//        ISCConstants.isc_bug_check,
//        ISCConstants.isc_db_corrupt,
            ISCConstants.isc_io_error,
//        ISCConstants.isc_metadata_corrupt,
//        
//        ISCConstants.isc_open_trans,  //could not forcibly close tx on server shutdown.
//        
//        ISCConstants.isc_port_len,    //user sent buffer too short or long for data
//                                      //expected.  Should never occur
//        
            ISCConstants.isc_req_sync,    //client asked for data when server expected
            //data or vice versa. Should never happen
//        
//        ISCConstants.isc_req_wrong_db,//In a multi-database application, a prepared
//                                      //request has been opened against the wrong
//                                      //database.  Not fatal, but also very
//                                      //unlikely. I'm leaving it in because if we
//                                      //get this, something is horribly wrong.
//      
//        ISCConstants.isc_sys_request, //A system service call failed.  Probably fatal.
//                                      //isc_stream_eof, Part of the scrolling cursors stuff, not
//                                      //fatal, simply indicates that you've got to the end of the
//                                      //cursor.
//
//        ISCConstants.isc_unavailable,
//        ISCConstants.isc_wrong_ods,
//        ISCConstants.isc_badblk,
//        ISCConstants.isc_relbadblk,
//        ISCConstants.isc_blktoobig,
//        ISCConstants.isc_bufexh,
//        ISCConstants.isc_bufinuse,
//        ISCConstants.isc_bdbincon,
//        ISCConstants.isc_badodsver,
//        ISCConstants.isc_dirtypage,
//        ISCConstants.isc_doubleloc,
//        ISCConstants.isc_nodnotfnd,
//        ISCConstants.isc_dupnodfnd,
//        ISCConstants.isc_locnotmar,
//        ISCConstants.isc_badpagtyp,
//        ISCConstants.isc_corrupt,
//        ISCConstants.isc_badpage,
//        ISCConstants.isc_badindex,
//        ISCConstants.isc_badhndcnt,
//        ISCConstants.isc_connect_reject, //no connection to close
//        ISCConstants.isc_no_lock_mgr,    //no connection to close
//        ISCConstants.isc_blocking_signal,
//        ISCConstants.isc_lockmanerr,
//        ISCConstants.isc_bad_detach,     //detach failed...fatal, but there's nothing we can do.
//        ISCConstants.isc_buf_invalid,
//        ISCConstants.isc_bad_lock_level,  //PC_ENGINE only, handles record locking
//                                          //issues from the attempt to make
//                                          //InterBase just like Dbase.
//
//        ISCConstants.isc_shutdown,
//        ISCConstants.isc_io_create_err,
//        ISCConstants.isc_io_open_err,
//        ISCConstants.isc_io_close_err,
//        ISCConstants.isc_io_read_err,
//        ISCConstants.isc_io_write_err,
//        ISCConstants.isc_io_delete_err,
//        ISCConstants.isc_io_access_err,
//        ISCConstants.isc_lost_db_connection,
//        ISCConstants.isc_bad_protocol,
//        ISCConstants.isc_file_in_use
    };

    static {
        Arrays.sort(FATAL_ERRORS);
    }
}
