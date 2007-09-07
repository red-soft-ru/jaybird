package org.firebirdsql.javaudf;

import java.sql.SQLException;

public class ActiveTransactionsRs extends TestRS {
    final int[] activeTransactions;
    public ActiveTransactionsRs(int[] activeTransactions) {
        this.activeTransactions = activeTransactions;
    }

    public boolean next() throws SQLException {
        return i++ < activeTransactions.length;
    }

    public Object getObject(int columnIndex) throws SQLException {
        return new Integer(activeTransactions[i-1]);
    }
}
