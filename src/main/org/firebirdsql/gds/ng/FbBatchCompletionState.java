package org.firebirdsql.gds.ng;

import java.sql.SQLException;

/**
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public interface FbBatchCompletionState {

    int EXECUTE_FAILED = -1;
    int SUCCESS_NO_INFO = -2;
    int NO_MORE_ERRORS = -1;

    int getSize() throws SQLException;

    int getState(int index) throws SQLException;

    String getError(int index) throws SQLException;

    String printAllStates() throws SQLException;

    int[] getAllStates() throws SQLException;
}
