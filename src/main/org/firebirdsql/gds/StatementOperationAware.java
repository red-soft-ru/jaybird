package org.firebirdsql.gds;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 01.07.12
 *          Time: 14:20
 */
public abstract class StatementOperationAware {
  private static StatementOperationAware instance;

  public static void startStatementOperation(Operation op) {
    if (instance != null)
      instance.startOperation(op);
  }

  public static void finishStatementOperation(Operation op) {
    if (instance != null)
      instance.finishOperation(op);
  }

  public static void initStatementOperationAware(StatementOperationAware s) {
    instance = s;
  }

  public abstract void startOperation(final Operation op);

  public abstract void finishOperation(final Operation op);
}
