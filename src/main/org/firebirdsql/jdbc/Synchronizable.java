/*
 * Firebird Open Source JavaEE Connector - JDBC Driver
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
package org.firebirdsql.jdbc;

/**
 * Provides synchronization object. Instances implementing this interface
 * provide objects that are later used in <code>synchronized</code> block:
 * <pre>
 * Object syncObject = someSynchronizable.getSynchronizationObject();
 * synchronized(syncObject) {
 *     // do something...
 * }
 * </pre>
 * 
 * @author <a href="mailto:rrokytskyy@users.sourceforge.net">Roman Rokytskyy</a>
 */
public interface Synchronizable {
    
    /**
     * Get synchronization object.
     * 
     * @return object, cannot be <code>null</code>.
     */
    Object getSynchronizationObject();
}