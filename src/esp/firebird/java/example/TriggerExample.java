package firebird.java.example;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example of a static method that is called from a trigger. Most of the code
 * is assembling a message for the log.
 *
 * @author Eugeney Putilin, Copyright (c) 2005
 * @version 1.0
 */
public class TriggerExample {

    public static final Logger logger = Logger.getLogger(TriggerExample.class.getName());

    /**
     * Universal loger for table for each row write to log new and/or values
     *
     * @throws SQLException
     */
    public static void traceLog() throws SQLException {
        StringBuffer message = new StringBuffer();

        String tableName = Trigger.getTableName();
        message.append(" Table ").append(tableName);

        int triggerAction = Trigger.getTriggerAction();
        switch (triggerAction) {
            case Trigger.ACTION_INSERT:
                message.append(" on INSERT : ");
                break;

            case Trigger.ACTION_UPDATE:
                message.append(" on UPDATE : ");
                break;

            case Trigger.ACTION_DELETE:
                message.append(" on DELETE : ");
                break;

            default:
                return;
        }

        // extract new and old values from the trigger context
        Connection c = Trigger.getCurrentConnection();
        try {
            PreparedStatement ps = c.prepareStatement("SELECT rdb$field_name "
                    + "FROM rdb$relation_fields "
                    + "WHERE rdb$relation_name = ?");
            try {
                ps.setString(1, tableName);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String fieldName = rs.getString(1);

                    switch (triggerAction) {
                        case Trigger.ACTION_INSERT:
                            message.append("new.");
                            appendValue(message, fieldName,
                                    Trigger.getNewValue(fieldName));
                            break;

                        case Trigger.ACTION_UPDATE:
                            message.append("new.");
                            appendValue(message, fieldName,
                                    Trigger.getNewValue(fieldName));

                            message.append(", old.");
                            appendValue(message, fieldName,
                                    Trigger.getOldValue(fieldName));
                            break;

                        case Trigger.ACTION_DELETE:
                            message.append("old.");
                            appendValue(message, fieldName,
                                    Trigger.getOldValue(fieldName));
                            break;
                    }

                    if (!rs.isLast())
                        message.append("; ");
                }
            } finally {
                ps.close();
            }
        } finally {
            c.close();
        }

        logger.log(Level.INFO, message.toString());
    }

    private static void appendValue(StringBuffer message, String fieldName, Object value) throws SQLException {
        message.append(fieldName);
        message.append(" = ");
        message.append(value);
    }
    public static int traceLog2() throws Exception
    {
        throw new Exception("from traceLog");
    }
}
