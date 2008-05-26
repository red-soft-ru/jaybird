package org.firebirdsql.javaudf;
import org.firebirdsql.jdbc.FBSQLException;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * <p>Title: Trigger example</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Eugeney Putilin
 * @version 1.0
 */

public class TrigerExamples extends Trigger {
  public static Logger logger;
  static{
      logger=Logger.getLogger(TrigerExamples.class.getName());
  }
  public TrigerExamples() throws FBSQLException {
      super();
  }
 public static int get()throws  SQLException
 {
   return -1;
 }

  /**
   * Universal loger for table
   * for each row write to log new and/or values
   *
   * @throws SQLException
   */
 public void trace_log()throws  SQLException
 {
  String s=getTable(),s1=" table ";
  s1+=s;
  int n=getTriggerAction();
  switch (n) {
    case INSERT:
      s1 += " insert row \n";
      break;
    case UPDATE:
      s1 += " update row \n";
      break;
    case DELETE:
      s1 += " delete row \n";
      break;
   default:
     return ;
  }
  Connection c = getCurrentConnection();
  try{
    PreparedStatement ps=c.prepareStatement("select rdb$field_name from rdb$relation_fields where rdb$relation_name=?");
    try{
      ps.setString(1,s);
      ResultSet rs=ps.executeQuery();
      try{
        while (rs.next()) {
          String field_name = rs.getString(1);
          if (n == DELETE || n == UPDATE)
            s1 += "\n old." + field_name + "  =  " + getObject_Old(field_name);
          if (n == INSERT || n == UPDATE)
            s1 += "\n new." + field_name + "  =  " + getObject_New(field_name);
        }
      }finally{rs.close();}
    }finally{ps.close();}
  }finally{c.close();}
  logger.log(Level.INFO,s1);
 }
}
