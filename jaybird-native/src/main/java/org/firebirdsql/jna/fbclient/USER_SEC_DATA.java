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
package org.firebirdsql.jna.fbclient;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * JNA wrapper for USER_SEC_DATA.
 * <p>
 * This file was initially autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>, a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.
 * </p>
 * <p>
 * This file was modified manually, <strong>do not automatically regenerate!</strong>
 * </p>
 * @since 3.0
 */
@Structure.FieldOrder({ "sec_flags", "uid", "gid", "protocol", "server", "user_name", "password", "group_name",
		"first_name", "middle_name", "last_name", "dba_user_name", "dba_password" })
@SuppressWarnings({ "unused", "java:S101", "java:S116", "java:S1104", "java:S2160" })
public class USER_SEC_DATA extends Structure {
	/// which fields are specified
	public short sec_flags;
	/// the user's id
	public int uid;
	/// the user's group id
	public int gid;
	/// protocol to use for connection
	public int protocol;
	/**
	 * server to administer<br>
	 * C type : ISC_SCHAR*
	 */
	public Pointer server;
	/**
	 * the user's name<br>
	 * C type : ISC_SCHAR*
	 */
	public Pointer user_name;
	/**
	 * the user's password<br>
	 * C type : ISC_SCHAR*
	 */
	public Pointer password;
	/**
	 * the group name<br>
	 * C type : ISC_SCHAR*
	 */
	public Pointer group_name;
	/**
	 * the user's first name<br>
	 * C type : ISC_SCHAR*
	 */
	public Pointer first_name;
	/**
	 * the user's middle name<br>
	 * C type : ISC_SCHAR*
	 */
	public Pointer middle_name;
	/**
	 * the user's last name<br>
	 * C type : ISC_SCHAR*
	 */
	public Pointer last_name;
	/**
	 * the dba user name<br>
	 * C type : ISC_SCHAR*
	 */
	public Pointer dba_user_name;
	/**
	 * the dba password<br>
	 * C type : ISC_SCHAR*
	 */
	public Pointer dba_password;

	public USER_SEC_DATA() {
		super();
	}

	public static class ByReference extends USER_SEC_DATA implements Structure.ByReference {
	}
	
	public static class ByValue extends USER_SEC_DATA implements Structure.ByValue {
	}
}
