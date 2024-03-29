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
 * Signals a quoted identifier (or - for dialect 1 - a string literal) in the token stream.
 *
 * @author Mark Rotteveel
 * @since 5
 */
final class QuotedIdentifierToken extends AbstractToken {

    QuotedIdentifierToken(int pos, CharSequence src, int start, int end) {
        super(pos, src, start, end);
    }

    public QuotedIdentifierToken(int pos, CharSequence tokenText) {
        super(pos, tokenText);
    }

    /**
     * Unescaped, unquoted name represented by this quoted identifier
     *
     * @return Unescaped and unquoted name (e.g. for {@code "name"} returns {@code name},
     * and for {@code "with""double"} returns {@code with"double})
     */
    public String name() {
        // exclude enclosing quotes
        String name = subSequence(1, length() - 1).toString();

        return name.indexOf('"') == -1
                ? name
                // unescape double quotes
                : name.replace("\"\"", "\"");
    }

    @Override
    public boolean isValidIdentifier() {
        // Could contain characters not valid in UNICODE_FSS, we're ignoring that
        return true;
    }
}
