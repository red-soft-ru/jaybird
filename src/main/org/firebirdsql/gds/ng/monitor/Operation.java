/*
 * Public Firebird Java API.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    1. Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firebirdsql.gds.ng.monitor;

import org.firebirdsql.gds.ng.FbAttachment;
import org.firebirdsql.gds.ng.FbTransaction;

import java.sql.SQLException;

/**
 * An operation of the driver.
 * <p>
 * In current implementations, this represents either a statement execution or a fetch.
 * </p>
 * <p>
 * <b>Note</b>: This is an experimental feature. The implementation or API may be removed or changed at any time.
 * </p>
 *
 * @author Vasiliy Yashkov
 * @author Mark Rotteveel
 * @since 4
 */
public interface Operation {

    /**
     * @return The type of operation.
     */
    Operation.Type getType();

    /**
     * Cancel this operation.
     *
     * @throws SQLException
     *         If the cancellation failed or if this operation is no longer cancellable.
     */
    void cancel() throws SQLException;

    /**
     * @return The attachment associated with this operation.
     */
    FbAttachment getAttachment();

    /**
     * @return The transaction associated with this operation.
     */
    FbTransaction getTransaction();

    /**
     * Type of operation.
     */
    enum Type {
        STATEMENT_EXECUTE,
        /**
         * Synchronous fetch from statement cursor.
         */
        STATEMENT_FETCH,
        /**
         * Asynchronous fetch from statement cursor: sending request for rows.
         *
         * @since 6
         */
        STATEMENT_ASYNC_FETCH_START,
        /**
         * Asynchronous fetch from statement cursor: receiving response with rows.
         * <p>
         * This operation is not cancellable.
         * </p>
         *
         * @since 6
         */
        STATEMENT_ASYNC_FETCH_COMPLETE(false),
        ;

        private final boolean cancellable;

        Type() {
            this(true);
        }

        Type(boolean cancellable) {
            this.cancellable = cancellable;
        }

        public boolean isCancellable() {
            return cancellable;
        }
    }
}
