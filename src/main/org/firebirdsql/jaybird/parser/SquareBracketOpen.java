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
 * Signals an opening square bracket ({@code [} in the token stream.
 * <p>
 * Expected occurrence is in definition of array dimensions or when dereferencing an array element.
 * </p>
 *
 * @author Mark Rotteveel
 * @since 5
 */
final class SquareBracketOpen extends AbstractSymbolToken implements OpenToken {

    public SquareBracketOpen(int position) {
        super(position);
    }

    @Override
    public boolean closedBy(CloseToken closeToken) {
        return closeToken instanceof SquareBracketClose;
    }

    @Override
    public String text() {
        return "[";
    }

}
