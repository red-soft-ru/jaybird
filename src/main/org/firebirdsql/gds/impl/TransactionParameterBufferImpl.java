/*
 * $Id$
 *
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
package org.firebirdsql.gds.impl;

import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.TransactionParameterBuffer;
import org.firebirdsql.gds.impl.argument.ArgumentType;

import java.io.Serial;

/**
 * Implementation of the {@link org.firebirdsql.gds.TransactionParameterBuffer} interface.
 */
public final class TransactionParameterBufferImpl extends ParameterBufferBase implements TransactionParameterBuffer {

    @Serial
    private static final long serialVersionUID = 7800513617882155482L;

    public TransactionParameterBufferImpl() {
        super(TpbMetaData.TPB_VERSION_3);
    }

    @Override
    public TransactionParameterBuffer deepCopy() {
        var result = new TransactionParameterBufferImpl();
        copyTo(result);
        return result;
    }

    @Override
    public void copyTo(TransactionParameterBuffer destination) {
        if (destination instanceof TransactionParameterBufferImpl tpbImpl) {
            tpbImpl.getArgumentsList().addAll(this.getArgumentsList());
        } else {
            TransactionParameterBuffer.super.copyTo(destination);
        }
    }

    public enum TpbMetaData implements ParameterBufferMetaData {
        TPB_VERSION_3(ISCConstants.isc_tpb_version3);

        private final int tpbVersion;

        TpbMetaData(int tpbVersion) {
            this.tpbVersion = tpbVersion;
        }

        @Override
        public final int getType() {
            return tpbVersion;
        }

        @Override
        public final ArgumentType getStringArgumentType(int tag) {
            return ArgumentType.TraditionalDpb;
        }

        @Override
        public final ArgumentType getByteArrayArgumentType(int tag) {
            return ArgumentType.TraditionalDpb;
        }

        @Override
        public final ArgumentType getIntegerArgumentType(int tag) {
            return ArgumentType.TraditionalDpb;
        }

        @Override
        public final ArgumentType getSingleArgumentType(int tag) {
            return ArgumentType.SingleTpb;
        }
    }
}
