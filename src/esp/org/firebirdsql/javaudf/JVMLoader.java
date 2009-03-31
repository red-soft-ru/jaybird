package org.firebirdsql.javaudf;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class JVMLoader {
  public native static void setJVM();
  static {
    System.loadLibrary("JavaUDF");
    setJVM();
  }
}