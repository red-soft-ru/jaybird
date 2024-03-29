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
package org.firebirdsql.jaybird.parser;

/**
 * Token that is always a single symbol.
 *
 * @author Mark Rotteveel
 * @since 5
 */
abstract class AbstractSymbolToken implements Token {

    private final int pos;

    AbstractSymbolToken(int pos) {
        this.pos = pos;
    }

    @Override
    public final int position() {
        return pos;
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractSymbolToken that = (AbstractSymbolToken) o;

        return pos == that.pos;
    }

    @Override
    public int hashCode() {
        return pos;
    }

}
