package org.firebirdsql.gds.ng.nativeoo;

import org.firebirdsql.common.FBTestProperties;
import org.firebirdsql.common.extension.GdsTypeExtension;
import org.firebirdsql.gds.BatchParameterBuffer;
import org.firebirdsql.gds.BlobParameterBuffer;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.impl.*;
import org.firebirdsql.gds.ng.*;
import org.firebirdsql.gds.ng.fields.RowValue;
import org.firebirdsql.gds.ng.wire.SimpleStatementListener;
import org.firebirdsql.jaybird.fb.constants.BatchItems;
import org.firebirdsql.jaybird.fb.constants.BpbItems;
import org.firebirdsql.jdbc.FBBlob;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for batch in the OO API implementation.
 *
 * {@link org.firebirdsql.gds.ng.nativeoo.IBatchImpl}.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
class IBatchImplTest extends AbstractBatchTest {

    @RegisterExtension
    @Order(1)
    public static final GdsTypeExtension testType = GdsTypeExtension.supportsFBOONativeOnly();

    private final AbstractNativeOODatabaseFactory factory =
            (AbstractNativeOODatabaseFactory) FBTestProperties.getFbDatabaseFactory();

    //@formatter:off
    protected String INSERT_QUERY_WITHOUT_BLOBS = "INSERT INTO test_p_metadata (" +
            "  id, " +
            "  simple_field, " +
            "  two_byte_field, " +
            "  three_byte_field, " +
            "  long_field, " +
            "  int_field, " +
            "  short_field, " +
            "  float_field, " +
            "  double_field, " +
            "  smallint_numeric, " +
            "  integer_decimal_1, " +
            "  integer_numeric, " +
            "  integer_decimal_2, " +
            "  bigint_numeric, " +
            "  bigint_decimal, " +
            "  date_field, " +
            "  time_field, " +
            "  timestamp_field " +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    protected String INSERT_QUERY_WITH_BLOBS = "INSERT INTO test_p_metadata (" +
            "  id, " +
            "  simple_field, " +
            "  two_byte_field, " +
            "  three_byte_field, " +
            "  long_field, " +
            "  int_field, " +
            "  short_field, " +
            "  float_field, " +
            "  double_field, " +
            "  smallint_numeric, " +
            "  integer_decimal_1, " +
            "  integer_numeric, " +
            "  integer_decimal_2, " +
            "  bigint_numeric, " +
            "  bigint_decimal, " +
            "  date_field, " +
            "  time_field, " +
            "  timestamp_field, " +
            "  blob_field, " +
            "  blob_text_field, " +
            "  blob_minus_one " +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    protected String INSERT_QUERY_ONLY_BLOBS = "INSERT INTO test_p_metadata (" +
            "  blob_field, " +
            "  blob_text_field, " +
            "  blob_minus_one " +
            ") VALUES (?, ?, ?)";

    protected String SELECT_QUERY_WITHOUT_BLOBS =
            "SELECT " +
            "id, simple_field, two_byte_field, three_byte_field, long_field, int_field, short_field," +
            "float_field, double_field, smallint_numeric, integer_decimal_1, integer_numeric," +
            "integer_decimal_2, bigint_numeric, bigint_decimal, date_field, time_field," +
            "timestamp_field " +
            " from test_p_metadata";

    protected String SELECT_QUERY_WITH_BLOBS =
            "SELECT " +
            "id, simple_field, two_byte_field, three_byte_field, long_field, int_field, short_field," +
            "float_field, double_field, smallint_numeric, integer_decimal_1, integer_numeric," +
            "integer_decimal_2, bigint_numeric, bigint_decimal, date_field, time_field," +
            "timestamp_field, blob_field, blob_text_field, blob_minus_one " +
            " from test_p_metadata";

    protected String SELECT_QUERY_ONLY_BLOBS =
            "SELECT " +
            "blob_field, blob_text_field, blob_minus_one " +
            " from test_p_metadata";
    //@formatter:on

    @Override
    protected Class<? extends FbDatabase> getExpectedDatabaseType() {
        return IDatabaseImpl.class;
    }

    @Override
    protected FbDatabase createDatabase() throws SQLException {
        return factory.connect(connectionInfo);
    }

    @Test
    public void testSingleExecuteBatchWithoutBlobs() throws SQLException {
        allocateTransaction();
        BatchParameterBuffer buffer = new BatchParameterBufferImp();
        buffer.addArgument(BatchItems.TAG_RECORD_COUNTS, 1);
        IBatchImpl batch = (IBatchImpl) db.createBatch(transaction, INSERT_QUERY_WITHOUT_BLOBS, buffer);

        int testInteger = 42;
        String testVarchar = "test varchar";
        long testBigInteger = 123456789234L;
        short testShort = 24;
        float testFloat = 42.42f;
        double testDouble = 42.4242d;
        double testSmallintNumeric = 42.4d;
        double testIntNumeric = 42.42d;
        double testIntNumeric2 = 42.424d;
        double testBigintNumeric = 4242.4242d;
        double testBigintNumeric2 = 4242.424242424d;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date testDate = Date.valueOf(dateFormat.format(cal.getTime()));
        Time testTime = Time.valueOf(timeFormat.format(cal.getTime()));
        Timestamp testTimestamp = Timestamp.valueOf(timestampFormat.format(cal.getTime()));

        batch.setInt(1, testInteger);
        batch.setString(2, testVarchar);
        batch.setString(3, testVarchar);
        batch.setString(4, testVarchar);
        batch.setLong(5, testBigInteger);
        batch.setInt(6, testInteger);
        batch.setShort(7, testShort);
        batch.setFloat(8, testFloat);
        batch.setDouble(9, testDouble);
        batch.setDouble(10, testSmallintNumeric);
        batch.setDouble(11, testSmallintNumeric);
        batch.setDouble(12, testIntNumeric);
        batch.setDouble(13, testIntNumeric2);
        batch.setDouble(14, testBigintNumeric);
        batch.setDouble(15, testBigintNumeric2);
        batch.setDate(16, testDate);
        batch.setTime(17, testTime);
        batch.setTimestamp(18, testTimestamp);

        batch.addBatch();

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.printAllStates());

        assertThat("Expected successful batch execution", execute.printAllStates(), allOf(
                startsWith("Message Status"),
                containsString("total=1 success=1"),
                endsWith("0\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(SELECT_QUERY_WITHOUT_BLOBS);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);
        RowValue fieldValues = statementListener.getRows().get(0);
        byte[] fieldData = fieldValues.getFieldData(0);
        assertEquals(testInteger,
                statement.getRowDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeInt(fieldData));
        fieldData = fieldValues.getFieldData(1);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(1).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(2);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(2).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(3);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(3).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(4);
        assertEquals(testBigInteger,
                statement.getRowDescriptor().getFieldDescriptor(4).getDatatypeCoder().decodeLong(fieldData));
        fieldData = fieldValues.getFieldData(5);
        assertEquals(testInteger,
                statement.getRowDescriptor().getFieldDescriptor(5).getDatatypeCoder().decodeInt(fieldData));
        fieldData = fieldValues.getFieldData(6);
        assertEquals(testShort,
                statement.getRowDescriptor().getFieldDescriptor(6).getDatatypeCoder().decodeShort(fieldData));
        fieldData = fieldValues.getFieldData(7);
        assertEquals(testFloat,
                statement.getRowDescriptor().getFieldDescriptor(7).getDatatypeCoder().decodeFloat(fieldData),
                0);
        fieldData = fieldValues.getFieldData(8);
        assertEquals(testDouble,
                statement.getRowDescriptor().getFieldDescriptor(8).getDatatypeCoder().decodeDouble(fieldData),
                0);
        fieldData = fieldValues.getFieldData(9);
        short decodeShort = statement.getRowDescriptor().getFieldDescriptor(9).getDatatypeCoder().decodeShort(fieldData);
        BigDecimal decimal = BigDecimal.valueOf(decodeShort, -statement.getRowDescriptor().getFieldDescriptor(9).getScale());
        float floatValue = decimal.floatValue();
        assertEquals(testSmallintNumeric,
                floatValue,
                0.001);
        fieldData = fieldValues.getFieldData(10);
        int decodeInt = statement.getRowDescriptor().getFieldDescriptor(10).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(10).getScale());
        double doubleValue = decimal.doubleValue();
        assertEquals(testSmallintNumeric,
                doubleValue,
                0.001);
        fieldData = fieldValues.getFieldData(11);
        decodeInt = statement.getRowDescriptor().getFieldDescriptor(11).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(11).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testIntNumeric,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(12);
        decodeInt = statement.getRowDescriptor().getFieldDescriptor(12).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(12).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testIntNumeric2,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(13);
        long decodeLong = statement.getRowDescriptor().getFieldDescriptor(13).getDatatypeCoder().decodeLong(fieldData);
        decimal = BigDecimal.valueOf(decodeLong, -statement.getRowDescriptor().getFieldDescriptor(13).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testBigintNumeric,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(14);
        decodeLong = statement.getRowDescriptor().getFieldDescriptor(14).getDatatypeCoder().decodeLong(fieldData);
        decimal = BigDecimal.valueOf(decodeLong, -statement.getRowDescriptor().getFieldDescriptor(14).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testBigintNumeric2,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(15);
        assertEquals(testDate.toLocalDate(),
                statement.getRowDescriptor().getFieldDescriptor(15).getDatatypeCoder().decodeLocalDate(fieldData));
        fieldData = fieldValues.getFieldData(16);
        assertEquals(testTime.toLocalTime(),
                statement.getRowDescriptor().getFieldDescriptor(16).getDatatypeCoder().decodeLocalTime(fieldData));
        fieldData = fieldValues.getFieldData(17);
        assertEquals(testTimestamp.toLocalDateTime(),
                statement.getRowDescriptor().getFieldDescriptor(17).getDatatypeCoder().decodeLocalDateTime(fieldData));

    }

    @Test
    public void testSingleExecuteBatchWithBlobs() throws Exception {
        allocateTransaction();
        BatchParameterBuffer buffer = new BatchParameterBufferImp();
        buffer.addArgument(BatchItems.TAG_RECORD_COUNTS, 1);
        // continue batch processing in case of errors in some messages
        buffer.addArgument(BatchItems.TAG_MULTIERROR, 1);
        // enable blobs processing - IDs generated by firebird engine
        buffer.addArgument(BatchItems.TAG_BLOB_POLICY, BatchItems.BLOB_ID_ENGINE);
        IBatchImpl batch = (IBatchImpl) db.createBatch(transaction, INSERT_QUERY_WITH_BLOBS, buffer);

        int testInteger = 42;
        String testVarchar = "test varchar";
        long testBigInteger = 123456789234L;
        short testShort = 24;
        float testFloat = 42.42f;
        double testDouble = 42.4242d;
        double testSmallintNumeric = 42.4d;
        double testIntNumeric = 42.42d;
        double testIntNumeric2 = 42.424d;
        double testBigintNumeric = 4242.4242d;
        double testBigintNumeric2 = 4242.424242424d;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date testDate = Date.valueOf(dateFormat.format(cal.getTime()));
        Time testTime = Time.valueOf(timeFormat.format(cal.getTime()));
        Timestamp testTimestamp = Timestamp.valueOf(timestampFormat.format(cal.getTime()));

        batch.setInt(1, testInteger);
        batch.setString(2, testVarchar);
        batch.setString(3, testVarchar);
        batch.setString(4, testVarchar);
        batch.setLong(5, testBigInteger);
        batch.setInt(6, testInteger);
        batch.setShort(7, testShort);
        batch.setFloat(8, testFloat);
        batch.setDouble(9, testDouble);
        batch.setDouble(10, testSmallintNumeric);
        batch.setDouble(11, testSmallintNumeric);
        batch.setDouble(12, testIntNumeric);
        batch.setDouble(13, testIntNumeric2);
        batch.setDouble(14, testBigintNumeric);
        batch.setDouble(15, testBigintNumeric2);
        batch.setDate(16, testDate);
        batch.setTime(17, testTime);
        batch.setTimestamp(18, testTimestamp);

        long blobID = 0;
        FbBlob blob19 = batch.addBlob(19, INSERT_QUERY_WITH_BLOBS.getBytes(), blobID, null);
        FbBlob blob20 = batch.addBlob(20, INSERT_QUERY_WITH_BLOBS.getBytes(), blobID, null);
        FbBlob blob21 = batch.addBlob(21, INSERT_QUERY_WITH_BLOBS.getBytes(), blobID, null);
        batch.appendBlobData("\n".getBytes());
        batch.appendBlobData(INSERT_QUERY_ONLY_BLOBS.getBytes());

        batch.addBatch();

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.printAllStates());

        assertThat("Expected successful batch execution", execute.printAllStates(), allOf(
                startsWith("Message Status"),
                containsString("total=1 success=1"),
                endsWith("0\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(SELECT_QUERY_WITH_BLOBS);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);
        RowValue fieldValues = statementListener.getRows().get(0);
        byte[] fieldData = fieldValues.getFieldData(0);
        assertEquals(testInteger,
                statement.getRowDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeInt(fieldData));
        fieldData = fieldValues.getFieldData(1);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(1).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(2);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(2).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(3);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(3).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(4);
        assertEquals(testBigInteger,
                statement.getRowDescriptor().getFieldDescriptor(4).getDatatypeCoder().decodeLong(fieldData));
        fieldData = fieldValues.getFieldData(5);
        assertEquals(testInteger,
                statement.getRowDescriptor().getFieldDescriptor(5).getDatatypeCoder().decodeInt(fieldData));
        fieldData = fieldValues.getFieldData(6);
        assertEquals(testShort,
                statement.getRowDescriptor().getFieldDescriptor(6).getDatatypeCoder().decodeShort(fieldData));
        fieldData = fieldValues.getFieldData(7);
        assertEquals(testFloat,
                statement.getRowDescriptor().getFieldDescriptor(7).getDatatypeCoder().decodeFloat(fieldData),
                0);
        fieldData = fieldValues.getFieldData(8);
        assertEquals(testDouble,
                statement.getRowDescriptor().getFieldDescriptor(8).getDatatypeCoder().decodeDouble(fieldData),
                0);
        fieldData = fieldValues.getFieldData(9);
        short decodeShort = statement.getRowDescriptor().getFieldDescriptor(9).getDatatypeCoder().decodeShort(fieldData);
        BigDecimal decimal = BigDecimal.valueOf(decodeShort, -statement.getRowDescriptor().getFieldDescriptor(9).getScale());
        float floatValue = decimal.floatValue();
        assertEquals(testSmallintNumeric,
                floatValue,
                0.001);
        fieldData = fieldValues.getFieldData(10);
        int decodeInt = statement.getRowDescriptor().getFieldDescriptor(10).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(10).getScale());
        double doubleValue = decimal.doubleValue();
        assertEquals(testSmallintNumeric,
                doubleValue,
                0.001);
        fieldData = fieldValues.getFieldData(11);
        decodeInt = statement.getRowDescriptor().getFieldDescriptor(11).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(11).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testIntNumeric,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(12);
        decodeInt = statement.getRowDescriptor().getFieldDescriptor(12).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(12).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testIntNumeric2,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(13);
        long decodeLong = statement.getRowDescriptor().getFieldDescriptor(13).getDatatypeCoder().decodeLong(fieldData);
        decimal = BigDecimal.valueOf(decodeLong, -statement.getRowDescriptor().getFieldDescriptor(13).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testBigintNumeric,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(14);
        decodeLong = statement.getRowDescriptor().getFieldDescriptor(14).getDatatypeCoder().decodeLong(fieldData);
        decimal = BigDecimal.valueOf(decodeLong, -statement.getRowDescriptor().getFieldDescriptor(14).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testBigintNumeric2,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(15);
        assertEquals(testDate.toLocalDate(),
                statement.getRowDescriptor().getFieldDescriptor(15).getDatatypeCoder().decodeLocalDate(fieldData));
        fieldData = fieldValues.getFieldData(16);
        assertEquals(testTime.toLocalTime(),
                statement.getRowDescriptor().getFieldDescriptor(16).getDatatypeCoder().decodeLocalTime(fieldData));
        fieldData = fieldValues.getFieldData(17);
        assertEquals(testTimestamp.toLocalDateTime(),
                statement.getRowDescriptor().getFieldDescriptor(17).getDatatypeCoder().decodeLocalDateTime(fieldData));
        fieldData = fieldValues.getFieldData(18);
        blobID = statement.getRowDescriptor().getFieldDescriptor(18).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, INSERT_QUERY_WITH_BLOBS.getBytes());
        fieldData = fieldValues.getFieldData(19);
        blobID = statement.getRowDescriptor().getFieldDescriptor(19).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, INSERT_QUERY_WITH_BLOBS.getBytes());
        fieldData = fieldValues.getFieldData(20);
        blobID = statement.getRowDescriptor().getFieldDescriptor(20).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, new String(INSERT_QUERY_WITH_BLOBS + "\n" + INSERT_QUERY_ONLY_BLOBS).getBytes());
    }

    @Test
    public void testMultipleMessagesBatchWithoutBlobs() throws SQLException {
        allocateTransaction();
        BatchParameterBuffer buffer = new BatchParameterBufferImp();
        buffer.addArgument(BatchItems.TAG_RECORD_COUNTS, 1);
        IBatchImpl batch = (IBatchImpl) db.createBatch(transaction, INSERT_QUERY_WITHOUT_BLOBS, buffer);

        int testInteger = 42;
        String testVarchar = "test varchar";
        long testBigInteger = 123456789234L;
        short testShort = 24;
        float testFloat = 42.42f;
        double testDouble = 42.4242d;
        double testSmallintNumeric = 42.4d;
        double testIntNumeric = 42.42d;
        double testIntNumeric2 = 42.424d;
        double testBigintNumeric = 4242.4242d;
        double testBigintNumeric2 = 4242.424242424d;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date testDate = Date.valueOf(dateFormat.format(cal.getTime()));
        Time testTime = Time.valueOf(timeFormat.format(cal.getTime()));
        Timestamp testTimestamp = Timestamp.valueOf(timestampFormat.format(cal.getTime()));

        batch.setInt(1, testInteger);
        batch.setString(2, testVarchar);
        batch.setString(3, testVarchar);
        batch.setString(4, testVarchar);
        batch.setLong(5, testBigInteger);
        batch.setInt(6, testInteger);
        batch.setShort(7, testShort);
        batch.setFloat(8, testFloat);
        batch.setDouble(9, testDouble);
        batch.setDouble(10, testSmallintNumeric);
        batch.setDouble(11, testSmallintNumeric);
        batch.setDouble(12, testIntNumeric);
        batch.setDouble(13, testIntNumeric2);
        batch.setDouble(14, testBigintNumeric);
        batch.setDouble(15, testBigintNumeric2);
        batch.setDate(16, testDate);
        batch.setTime(17, testTime);
        batch.setTimestamp(18, testTimestamp);
        batch.addBatch();

        batch.setInt(1, ++testInteger);
        batch.setString(2, testVarchar);
        batch.setString(3, testVarchar);
        batch.setString(4, testVarchar);
        batch.setLong(5, testBigInteger);
        batch.setInt(6, testInteger);
        batch.setShort(7, testShort);
        batch.setFloat(8, testFloat);
        batch.setDouble(9, testDouble);
        batch.setDouble(10, testSmallintNumeric);
        batch.setDouble(11, testSmallintNumeric);
        batch.setDouble(12, testIntNumeric);
        batch.setDouble(13, testIntNumeric2);
        batch.setDouble(14, testBigintNumeric);
        batch.setDouble(15, testBigintNumeric2);
        batch.setDate(16, testDate);
        batch.setTime(17, testTime);
        batch.setTimestamp(18, testTimestamp);
        batch.addBatch();

        batch.setInt(1, ++testInteger);
        batch.setString(2, testVarchar);
        batch.setString(3, testVarchar);
        batch.setString(4, testVarchar);
        batch.setLong(5, testBigInteger);
        batch.setInt(6, testInteger);
        batch.setShort(7, testShort);
        batch.setFloat(8, testFloat);
        batch.setDouble(9, testDouble);
        batch.setDouble(10, testSmallintNumeric);
        batch.setDouble(11, testSmallintNumeric);
        batch.setDouble(12, testIntNumeric);
        batch.setDouble(13, testIntNumeric2);
        batch.setDouble(14, testBigintNumeric);
        batch.setDouble(15, testBigintNumeric2);
        batch.setDate(16, testDate);
        batch.setTime(17, testTime);
        batch.setTimestamp(18, testTimestamp);
        batch.addBatch();

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.printAllStates());

        assertThat("Expected successful batch execution", execute.printAllStates(), allOf(
                startsWith("Message Status"),
                containsString("total=3 success=3"),
                endsWith("0\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(SELECT_QUERY_WITHOUT_BLOBS);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);
        statement.fetchRows(1);
        statement.fetchRows(1);
        RowValue fieldValues = statementListener.getRows().get(2);
        byte[] fieldData = fieldValues.getFieldData(0);
        assertEquals(testInteger,
                statement.getRowDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeInt(fieldData));
        fieldData = fieldValues.getFieldData(1);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(1).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(2);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(2).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(3);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(3).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(4);
        assertEquals(testBigInteger,
                statement.getRowDescriptor().getFieldDescriptor(4).getDatatypeCoder().decodeLong(fieldData));
        fieldData = fieldValues.getFieldData(5);
        assertEquals(testInteger,
                statement.getRowDescriptor().getFieldDescriptor(5).getDatatypeCoder().decodeInt(fieldData));
        fieldData = fieldValues.getFieldData(6);
        assertEquals(testShort,
                statement.getRowDescriptor().getFieldDescriptor(6).getDatatypeCoder().decodeShort(fieldData));
        fieldData = fieldValues.getFieldData(7);
        assertEquals(testFloat,
                statement.getRowDescriptor().getFieldDescriptor(7).getDatatypeCoder().decodeFloat(fieldData),
                0);
        fieldData = fieldValues.getFieldData(8);
        assertEquals(testDouble,
                statement.getRowDescriptor().getFieldDescriptor(8).getDatatypeCoder().decodeDouble(fieldData),
                0);
        fieldData = fieldValues.getFieldData(9);
        short decodeShort = statement.getRowDescriptor().getFieldDescriptor(9).getDatatypeCoder().decodeShort(fieldData);
        BigDecimal decimal = BigDecimal.valueOf(decodeShort, -statement.getRowDescriptor().getFieldDescriptor(9).getScale());
        float floatValue = decimal.floatValue();
        assertEquals(testSmallintNumeric,
                floatValue,
                0.001);
        fieldData = fieldValues.getFieldData(10);
        int decodeInt = statement.getRowDescriptor().getFieldDescriptor(10).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(10).getScale());
        double doubleValue = decimal.doubleValue();
        assertEquals(testSmallintNumeric,
                doubleValue,
                0.001);
        fieldData = fieldValues.getFieldData(11);
        decodeInt = statement.getRowDescriptor().getFieldDescriptor(11).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(11).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testIntNumeric,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(12);
        decodeInt = statement.getRowDescriptor().getFieldDescriptor(12).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(12).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testIntNumeric2,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(13);
        long decodeLong = statement.getRowDescriptor().getFieldDescriptor(13).getDatatypeCoder().decodeLong(fieldData);
        decimal = BigDecimal.valueOf(decodeLong, -statement.getRowDescriptor().getFieldDescriptor(13).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testBigintNumeric,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(14);
        decodeLong = statement.getRowDescriptor().getFieldDescriptor(14).getDatatypeCoder().decodeLong(fieldData);
        decimal = BigDecimal.valueOf(decodeLong, -statement.getRowDescriptor().getFieldDescriptor(14).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testBigintNumeric2,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(15);
        assertEquals(testDate.toLocalDate(),
                statement.getRowDescriptor().getFieldDescriptor(15).getDatatypeCoder().decodeLocalDate(fieldData));
        fieldData = fieldValues.getFieldData(16);
        assertEquals(testTime.toLocalTime(),
                statement.getRowDescriptor().getFieldDescriptor(16).getDatatypeCoder().decodeLocalTime(fieldData));
        fieldData = fieldValues.getFieldData(17);
        assertEquals(testTimestamp.toLocalDateTime(),
                statement.getRowDescriptor().getFieldDescriptor(17).getDatatypeCoder().decodeLocalDateTime(fieldData));
    }

    @Test
    public void testMultipleMessagesBatchWithBlobs() throws Exception {
        allocateTransaction();
        BatchParameterBuffer buffer = new BatchParameterBufferImp();
        buffer.addArgument(BatchItems.TAG_RECORD_COUNTS, 1);
        // continue batch processing in case of errors in some messages
        buffer.addArgument(BatchItems.TAG_MULTIERROR, 1);
        // enable blobs processing - IDs generated by firebird engine
        buffer.addArgument(BatchItems.TAG_BLOB_POLICY, BatchItems.BLOB_ID_ENGINE);
        IBatchImpl batch = (IBatchImpl) db.createBatch(transaction, INSERT_QUERY_WITH_BLOBS, buffer);

        int testInteger = 42;
        String testVarchar = "test varchar";
        long testBigInteger = 123456789234L;
        short testShort = 24;
        float testFloat = 42.42f;
        double testDouble = 42.4242d;
        double testSmallintNumeric = 42.4d;
        double testIntNumeric = 42.42d;
        double testIntNumeric2 = 42.424d;
        double testBigintNumeric = 4242.4242d;
        double testBigintNumeric2 = 4242.424242424d;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date testDate = Date.valueOf(dateFormat.format(cal.getTime()));
        Time testTime = Time.valueOf(timeFormat.format(cal.getTime()));
        Timestamp testTimestamp = Timestamp.valueOf(timestampFormat.format(cal.getTime()));

        FbMessageBuilder builder = new IMessageBuilderImpl(batch);

        batch.setInt(1, testInteger);
        batch.setString(2, testVarchar);
        batch.setString(3, testVarchar);
        batch.setString(4, testVarchar);
        batch.setLong(5, testBigInteger);
        batch.setInt(6, testInteger);
        batch.setShort(7, testShort);
        batch.setFloat(8, testFloat);
        batch.setDouble(9, testDouble);
        batch.setDouble(10, testSmallintNumeric);
        batch.setDouble(11, testSmallintNumeric);
        batch.setDouble(12, testIntNumeric);
        batch.setDouble(13, testIntNumeric2);
        batch.setDouble(14, testBigintNumeric);
        batch.setDouble(15, testBigintNumeric2);
        batch.setDate(16, testDate);
        batch.setTime(17, testTime);
        batch.setTimestamp(18, testTimestamp);

        long blobID = 0;
        FbBlob blob19 = batch.addBlob(19, INSERT_QUERY_WITH_BLOBS.getBytes(), blobID, null);
        FbBlob blob20 = batch.addBlob(20, INSERT_QUERY_WITH_BLOBS.getBytes(), blobID, null);
        FbBlob blob21 = batch.addBlob(21, INSERT_QUERY_WITH_BLOBS.getBytes(), blobID, null);
        batch.addBatch();

        batch.setInt(1, ++testInteger);
        batch.setString(2, testVarchar);
        batch.setString(3, testVarchar);
        batch.setString(4, testVarchar);
        batch.setLong(5, testBigInteger);
        batch.setInt(6, testInteger);
        batch.setShort(7, testShort);
        batch.setFloat(8, testFloat);
        batch.setDouble(9, testDouble);
        batch.setDouble(10, testSmallintNumeric);
        batch.setDouble(11, testSmallintNumeric);
        batch.setDouble(12, testIntNumeric);
        batch.setDouble(13, testIntNumeric2);
        batch.setDouble(14, testBigintNumeric);
        batch.setDouble(15, testBigintNumeric2);
        batch.setDate(16, testDate);
        batch.setTime(17, testTime);
        batch.setTimestamp(18, testTimestamp);

        byte[] testBytes = (INSERT_QUERY_WITH_BLOBS + INSERT_QUERY_WITH_BLOBS).getBytes();
        blob19 = batch.addBlob(19, testBytes, null);
        blob20 = batch.addBlob(20, testBytes, null);
        blob21 = batch.addBlob(21, testBytes, null);
        batch.addBatch();

        batch.setInt(1, ++testInteger);
        batch.setString(2, testVarchar);
        batch.setString(3, testVarchar);
        batch.setString(4, testVarchar);
        batch.setLong(5, testBigInteger);
        batch.setInt(6, testInteger);
        batch.setShort(7, testShort);
        batch.setFloat(8, testFloat);
        batch.setDouble(9, testDouble);
        batch.setDouble(10, testSmallintNumeric);
        batch.setDouble(11, testSmallintNumeric);
        batch.setDouble(12, testIntNumeric);
        batch.setDouble(13, testIntNumeric2);
        batch.setDouble(14, testBigintNumeric);
        batch.setDouble(15, testBigintNumeric2);
        batch.setDate(16, testDate);
        batch.setTime(17, testTime);
        batch.setTimestamp(18, testTimestamp);
        testBytes = (INSERT_QUERY_WITH_BLOBS + INSERT_QUERY_WITH_BLOBS + INSERT_QUERY_WITH_BLOBS).getBytes();
        blob19 = batch.addBlob(19, testBytes, null);
        blob20 = batch.addBlob(20, testBytes, null);
        blob21 = batch.addBlob(21, testBytes, null);
        batch.addBatch();

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.printAllStates());

        assertThat("Expected successful batch execution", execute.printAllStates(), allOf(
                startsWith("Message Status"),
                containsString("total=3 success=3"),
                endsWith("0\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(SELECT_QUERY_WITH_BLOBS);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);
        statement.fetchRows(1);
        statement.fetchRows(1);
        RowValue fieldValues = statementListener.getRows().get(2);
        byte[] fieldData = fieldValues.getFieldData(0);
        assertEquals(testInteger,
                statement.getRowDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeInt(fieldData));
        fieldData = fieldValues.getFieldData(1);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(1).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(2);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(2).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(3);
        assertEquals(testVarchar,
                statement.getRowDescriptor().getFieldDescriptor(3).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldData(4);
        assertEquals(testBigInteger,
                statement.getRowDescriptor().getFieldDescriptor(4).getDatatypeCoder().decodeLong(fieldData));
        fieldData = fieldValues.getFieldData(5);
        assertEquals(testInteger,
                statement.getRowDescriptor().getFieldDescriptor(5).getDatatypeCoder().decodeInt(fieldData));
        fieldData = fieldValues.getFieldData(6);
        assertEquals(testShort,
                statement.getRowDescriptor().getFieldDescriptor(6).getDatatypeCoder().decodeShort(fieldData));
        fieldData = fieldValues.getFieldData(7);
        assertEquals(testFloat,
                statement.getRowDescriptor().getFieldDescriptor(7).getDatatypeCoder().decodeFloat(fieldData),
                0);
        fieldData = fieldValues.getFieldData(8);
        assertEquals(testDouble,
                statement.getRowDescriptor().getFieldDescriptor(8).getDatatypeCoder().decodeDouble(fieldData),
                0);
        fieldData = fieldValues.getFieldData(9);
        short decodeShort = statement.getRowDescriptor().getFieldDescriptor(9).getDatatypeCoder().decodeShort(fieldData);
        BigDecimal decimal = BigDecimal.valueOf(decodeShort, -statement.getRowDescriptor().getFieldDescriptor(9).getScale());
        float floatValue = decimal.floatValue();
        assertEquals(testSmallintNumeric,
                floatValue,
                0.001);
        fieldData = fieldValues.getFieldData(10);
        int decodeInt = statement.getRowDescriptor().getFieldDescriptor(10).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(10).getScale());
        double doubleValue = decimal.doubleValue();
        assertEquals(testSmallintNumeric,
                doubleValue,
                0.001);
        fieldData = fieldValues.getFieldData(11);
        decodeInt = statement.getRowDescriptor().getFieldDescriptor(11).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(11).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testIntNumeric,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(12);
        decodeInt = statement.getRowDescriptor().getFieldDescriptor(12).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getRowDescriptor().getFieldDescriptor(12).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testIntNumeric2,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(13);
        long decodeLong = statement.getRowDescriptor().getFieldDescriptor(13).getDatatypeCoder().decodeLong(fieldData);
        decimal = BigDecimal.valueOf(decodeLong, -statement.getRowDescriptor().getFieldDescriptor(13).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testBigintNumeric,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(14);
        decodeLong = statement.getRowDescriptor().getFieldDescriptor(14).getDatatypeCoder().decodeLong(fieldData);
        decimal = BigDecimal.valueOf(decodeLong, -statement.getRowDescriptor().getFieldDescriptor(14).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testBigintNumeric2,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldData(15);
        assertEquals(testDate.toLocalDate(),
                statement.getRowDescriptor().getFieldDescriptor(15).getDatatypeCoder().decodeLocalDate(fieldData));
        fieldData = fieldValues.getFieldData(16);
        assertEquals(testTime.toLocalTime(),
                statement.getRowDescriptor().getFieldDescriptor(16).getDatatypeCoder().decodeLocalTime(fieldData));
        fieldData = fieldValues.getFieldData(17);
        assertEquals(testTimestamp.toLocalDateTime(),
                statement.getRowDescriptor().getFieldDescriptor(17).getDatatypeCoder().decodeLocalDateTime(fieldData));
        fieldData = fieldValues.getFieldData(18);
        blobID = statement.getRowDescriptor().getFieldDescriptor(18).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, testBytes);
        fieldData = fieldValues.getFieldData(19);
        blobID = statement.getRowDescriptor().getFieldDescriptor(19).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, testBytes);
        fieldData = fieldValues.getFieldData(20);
        blobID = statement.getRowDescriptor().getFieldDescriptor(20).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, testBytes);
    }

    @Test
    public void testBatchWithBlobStream() throws Exception {
        allocateTransaction();
        BatchParameterBuffer buffer = new BatchParameterBufferImp();
        // Blobs are placed in a stream
        buffer.addArgument(BatchItems.TAG_BLOB_POLICY, BatchItems.BLOB_STREAM);
        IBatchImpl batch = (IBatchImpl) db.createBatch(transaction, INSERT_QUERY_ONLY_BLOBS, buffer);

        GDSHelper h = new GDSHelper(db);
        h.setCurrentTransaction(batch.getTransaction());

        FBBlob.Config config = FBBlob.createConfig(ISCConstants.BLOB_SUB_TYPE_BINARY,
                db.getConnectionProperties(), db.getDatatypeCoder());

        FBBlob b1 = new FBBlob(h, 1, null, config);
        FBBlob b2 = new FBBlob(h, 2, null, config);
        FBBlob b3 = new FBBlob(h, 3, null, config);

        batch.addBlob(1, b1);
        batch.addBlob(2, b2);
        batch.addBlob(3, b3);

        // blobs
        String d1 = "1111111111111111111";
        String d2 = "22222222222222222222";
        String d3 = "333333333333333333333333333333333333333333333333333333333333333";

        batch.appendBlobData(d1.getBytes(), b1.getBlobId());
        batch.appendBlobData(d2.getBytes(), b2.getBlobId());
        batch.appendBlobData(d3.getBytes(), b3.getBlobId());

        batch.addBatch();

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.printAllStates());

        assertThat("Expected successful batch execution", execute.printAllStates(), allOf(
                startsWith("Summary"),
                containsString("total=1 success=0 success(but no update info)=1"),
                endsWith("\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(SELECT_QUERY_ONLY_BLOBS);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);
        RowValue fieldValues = statementListener.getRows().get(0);
        byte[] fieldData = fieldValues.getFieldData(0);
        long blobID = statement.getRowDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, d1.getBytes());
        fieldData = fieldValues.getFieldData(1);
        blobID = statement.getRowDescriptor().getFieldDescriptor(1).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, d2.getBytes());
        fieldData = fieldValues.getFieldData(2);
        blobID = statement.getRowDescriptor().getFieldDescriptor(2).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, d3.getBytes());
    }

    @Test
    public void testBatchWithSegmentedBlobs() throws Exception {
        allocateTransaction();
        BatchParameterBuffer buffer = new BatchParameterBufferImp();
        // Blobs are placed in a stream
        buffer.addArgument(BatchItems.TAG_BLOB_POLICY, BatchItems.BLOB_STREAM);
        FbBatch batch = db.createBatch(transaction, INSERT_QUERY_ONLY_BLOBS, buffer);

        GDSHelper h = new GDSHelper(db);
        h.setCurrentTransaction(batch.getTransaction());

        // Create blobs
        FBBlob.Config config = FBBlob.createConfig(ISCConstants.BLOB_SUB_TYPE_BINARY,
                db.getConnectionProperties(), db.getDatatypeCoder());

        FBBlob b1 = new FBBlob(h, 4242, null, config);
        FBBlob b2 = new FBBlob(h, 242, null, config);
        FBBlob b3 = new FBBlob(h, 42, null, config);

        // blobs
        String blobSegment1 = INSERT_QUERY_WITHOUT_BLOBS;
        String blobSegment2 = INSERT_QUERY_WITH_BLOBS;
        String blobSegment3 = INSERT_QUERY_ONLY_BLOBS;

        BlobParameterBuffer bpb = new BlobParameterBufferImp();
        bpb.addArgument(BpbItems.isc_bpb_type, BpbItems.TypeValues.isc_bpb_type_segmented);

        batch.addSegmentedBlob(1, bpb, b1);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addSegmentedBlob(2, bpb, b2);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addSegmentedBlob(3, bpb, b3);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addBatch();

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.printAllStates());

        assertThat("Expected successful batch execution", execute.printAllStates(), allOf(
                startsWith("Summary"),
                containsString("total=1 success=0 success(but no update info)=1"),
                endsWith("\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(SELECT_QUERY_ONLY_BLOBS);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);

        String allSegments = blobSegment1 + "\n" + blobSegment2 + "\n" + blobSegment3;

        RowValue fieldValues = statementListener.getRows().get(0);
        byte[] fieldData = fieldValues.getFieldData(0);
        long blobID = statement.getRowDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, allSegments.getBytes());
        fieldData = fieldValues.getFieldData(1);
        blobID = statement.getRowDescriptor().getFieldDescriptor(1).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, allSegments.getBytes());
        fieldData = fieldValues.getFieldData(2);
        blobID = statement.getRowDescriptor().getFieldDescriptor(2).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, allSegments.getBytes());
    }

    @Test
    public void testMultipleMessagesBatchWithSegmentedBlobs() throws Exception {
        allocateTransaction();
        BatchParameterBuffer buffer = new BatchParameterBufferImp();
        // Blobs are placed in a stream
        buffer.addArgument(BatchItems.TAG_BLOB_POLICY, BatchItems.BLOB_STREAM);
        FbBatch batch = db.createBatch(transaction, INSERT_QUERY_ONLY_BLOBS, buffer);

        GDSHelper h = new GDSHelper(db);
        h.setCurrentTransaction(batch.getTransaction());

        // Create blobs
        FBBlob.Config config = FBBlob.createConfig(ISCConstants.BLOB_SUB_TYPE_BINARY,
                db.getConnectionProperties(), db.getDatatypeCoder());

        FBBlob b1 = new FBBlob(h, 4242, null, config);
        FBBlob b2 = new FBBlob(h, 242, null, config);
        FBBlob b3 = new FBBlob(h, 42, null, config);

        // blobs
        String blobSegment1 = INSERT_QUERY_WITHOUT_BLOBS;
        String blobSegment2 = INSERT_QUERY_WITH_BLOBS;
        String blobSegment3 = INSERT_QUERY_ONLY_BLOBS;

        BlobParameterBuffer bpb = new BlobParameterBufferImp();
        bpb.addArgument(BpbItems.isc_bpb_type, BpbItems.TypeValues.isc_bpb_type_segmented);

        batch.addSegmentedBlob(1, bpb, b1);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addSegmentedBlob(2, bpb, b2);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addSegmentedBlob(3, bpb, b3);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addBatch();

        // Create blobs
        FBBlob b4 = new FBBlob(h, 34242, null, config);
        FBBlob b5 = new FBBlob(h, 3242, null, config);
        FBBlob b6 = new FBBlob(h, 342, null, config);

        batch.addSegmentedBlob(1, bpb, b4);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addSegmentedBlob(2, bpb, b5);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addSegmentedBlob(3, bpb, b6);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addBatch();

        // Create blobs
        FBBlob b7 = new FBBlob(h, 14242, null, config);
        FBBlob b8 = new FBBlob(h, 1242, null, config);
        FBBlob b9 = new FBBlob(h, 142, null, config);

        batch.addSegmentedBlob(1, bpb, b7);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addSegmentedBlob(2, bpb, b8);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addSegmentedBlob(3, bpb, b9);
        batch.addBlobSegment(blobSegment1.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment2.getBytes(), false);
        batch.addBlobSegment("\n".getBytes(), false);
        batch.addBlobSegment(blobSegment3.getBytes(), true);

        batch.addBatch();

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.printAllStates());

        assertThat("Expected successful batch execution", execute.printAllStates(), allOf(
                startsWith("Summary"),
                containsString("total=3 success=0 success(but no update info)=3"),
                endsWith("\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(SELECT_QUERY_ONLY_BLOBS);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);
        statement.fetchRows(1);

        String allSegments = blobSegment1 + "\n" + blobSegment2 + "\n" + blobSegment3;

        RowValue fieldValues = statementListener.getRows().get(1);
        byte[] fieldData = fieldValues.getFieldData(0);
        long blobID = statement.getRowDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, allSegments.getBytes());
        fieldData = fieldValues.getFieldData(1);
        blobID = statement.getRowDescriptor().getFieldDescriptor(1).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, allSegments.getBytes());
        fieldData = fieldValues.getFieldData(2);
        blobID = statement.getRowDescriptor().getFieldDescriptor(2).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, allSegments.getBytes());
    }

    @Test
    public void testBatchWithRegisteredBlobs() throws Exception {
        allocateTransaction();
        BatchParameterBuffer buffer = new BatchParameterBufferImp();
        // Blobs are placed in a stream
        buffer.addArgument(BatchItems.TAG_BLOB_POLICY, BatchItems.BLOB_STREAM);
        FbBatch batch = db.createBatch(transaction, INSERT_QUERY_ONLY_BLOBS, buffer);

        GDSHelper h = new GDSHelper(db);
        h.setCurrentTransaction(batch.getTransaction());

        // Create blobs
        final FBBlob.Config config = FBBlob.createConfig(ISCConstants.BLOB_SUB_TYPE_BINARY,
                db.getConnectionProperties(),
                db.getDatatypeCoder());

        FBBlob b1 = new FBBlob(h, 4242, null, config);
        FBBlob b2 = new FBBlob(h, 242, null, config);
        FBBlob b3 = new FBBlob(h, 42,null, config);

        FbBlob regBlob1 = h.createBlob(config);
        FbBlob regBlob2 = h.createBlob(config);
        FbBlob regBlob3 = h.createBlob(config);

        regBlob1.putSegment(INSERT_QUERY_WITH_BLOBS.getBytes());
        regBlob1.close();
        regBlob2.putSegment(INSERT_QUERY_WITHOUT_BLOBS.getBytes());
        regBlob2.close();
        regBlob3.putSegment(INSERT_QUERY_ONLY_BLOBS.getBytes());
        regBlob3.close();

        // Register blobs
        batch.registerBlob(1, regBlob1.getBlobId(), b1);
        batch.registerBlob(2, regBlob2.getBlobId(), b2);
        batch.registerBlob(3, regBlob3.getBlobId(), b3);

        batch.addBatch();

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.printAllStates());

        assertThat("Expected successful batch execution", execute.printAllStates(), allOf(
                startsWith("Summary"),
                containsString("total=1 success=0 success(but no update info)=1"),
                endsWith("\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(SELECT_QUERY_ONLY_BLOBS);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);

        RowValue fieldValues = statementListener.getRows().get(0);
        byte[] fieldData = fieldValues.getFieldData(0);
        long blobID = statement.getRowDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, INSERT_QUERY_WITH_BLOBS.getBytes());
        fieldData = fieldValues.getFieldData(1);
        blobID = statement.getRowDescriptor().getFieldDescriptor(1).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, INSERT_QUERY_WITHOUT_BLOBS.getBytes());
        fieldData = fieldValues.getFieldData(2);
        blobID = statement.getRowDescriptor().getFieldDescriptor(2).getDatatypeCoder().decodeLong(fieldData);
        checkBlob(blobID, INSERT_QUERY_ONLY_BLOBS.getBytes());
    }

    @Test
    public void testBatchWithNulls() throws Exception {
        allocateTransaction();
        BatchParameterBuffer buffer = new BatchParameterBufferImp();
        buffer.addArgument(BatchItems.TAG_RECORD_COUNTS, 1);
        IBatchImpl batch = (IBatchImpl) db.createBatch(transaction, INSERT_QUERY_WITHOUT_BLOBS, buffer);

        int testInteger = 42;

        batch.setInt(1, testInteger);
        batch.setNull(2, Types.VARCHAR);
        batch.setNull(3, Types.VARCHAR);
        batch.setNull(4, Types.VARCHAR);
        batch.setNull(5, Types.BIGINT);
        batch.setNull(6, Types.INTEGER);
        batch.setNull(7, Types.SMALLINT);
        batch.setNull(8, Types.FLOAT);
        batch.setNull(9, Types.DOUBLE);
        batch.setNull(10, Types.DOUBLE);
        batch.setNull(11, Types.DOUBLE);
        batch.setNull(12, Types.DOUBLE);
        batch.setNull(13, Types.DOUBLE);
        batch.setNull(14, Types.DOUBLE);
        batch.setNull(15, Types.DOUBLE);
        batch.setNull(16, Types.DATE);
        batch.setNull(17, Types.TIME);
        batch.setNull(18, Types.TIMESTAMP);

        batch.addBatch();

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.printAllStates());

        assertThat("Expected successful batch execution", execute.printAllStates(), allOf(
                startsWith("Message Status"),
                containsString("total=1 success=1"),
                endsWith("0\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(SELECT_QUERY_WITHOUT_BLOBS);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);
        RowValue fieldValues = statementListener.getRows().get(0);
        byte[] fieldData = fieldValues.getFieldData(0);
        assertEquals(testInteger,
                statement.getRowDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeInt(fieldData));
        for (int i = 1; i < statement.getRowDescriptor().getCount(); i++) {
            fieldData = fieldValues.getFieldData(i);
            assertEquals(null, fieldData);
        }
    }

    @Test
    public void testBatchWithBlobNulls() throws Exception {
        allocateTransaction();
        BatchParameterBuffer buffer = new BatchParameterBufferImp();
        buffer.addArgument(BatchItems.TAG_RECORD_COUNTS, 1);
        buffer.addArgument(BatchItems.TAG_MULTIERROR, 1);
        buffer.addArgument(BatchItems.TAG_BLOB_POLICY, BatchItems.BLOB_ID_ENGINE);
        IBatchImpl batch = (IBatchImpl) db.createBatch(transaction, INSERT_QUERY_WITH_BLOBS, buffer);

        int testInteger = 42;

        batch.setInt(1, testInteger);
        batch.setNull(2, Types.VARCHAR);
        batch.setNull(3, Types.VARCHAR);
        batch.setNull(4, Types.VARCHAR);
        batch.setNull(5, Types.BIGINT);
        batch.setNull(6, Types.INTEGER);
        batch.setNull(7, Types.SMALLINT);
        batch.setNull(8, Types.FLOAT);
        batch.setNull(9, Types.DOUBLE);
        batch.setNull(10, Types.DOUBLE);
        batch.setNull(11, Types.DOUBLE);
        batch.setNull(12, Types.DOUBLE);
        batch.setNull(13, Types.DOUBLE);
        batch.setNull(14, Types.DOUBLE);
        batch.setNull(15, Types.DOUBLE);
        batch.setNull(16, Types.DATE);
        batch.setNull(17, Types.TIME);
        batch.setNull(18, Types.TIMESTAMP);
        batch.setNull(19, Types.LONGVARBINARY);
        batch.setNull(20, Types.LONGVARCHAR);
        batch.setNull(21, Types.LONGVARBINARY);

        batch.addBatch();

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.printAllStates());

        assertThat("Expected successful batch execution", execute.printAllStates(), allOf(
                startsWith("Message Status"),
                containsString("total=1 success=1"),
                endsWith("0\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(SELECT_QUERY_WITH_BLOBS);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);
        RowValue fieldValues = statementListener.getRows().get(0);
        byte[] fieldData = fieldValues.getFieldData(0);
        assertEquals(testInteger,
                statement.getRowDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeInt(fieldData));
        for (int i = 1; i < statement.getRowDescriptor().getCount(); i++) {
            fieldData = fieldValues.getFieldData(i);
            assertEquals(null, fieldData);
        }
    }

    private void checkBlob(long blobID, byte[] originalContent) throws Exception {
        // Use sufficiently large value so that multiple segments are used
        final int requiredSize = (originalContent == null ? 10 : originalContent.length);
        final FbBlob blob = db.createBlobForInput(transaction, blobID);
        blob.open();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(requiredSize);
        while (!blob.isEof()) {
            bos.write(blob.getSegment(blob.getMaximumSegmentSize()));
        }
        blob.close();
        byte[] result = bos.toByteArray();
        assertEquals(originalContent.length, result.length, "Unexpected length read from blob");
        assertTrue(validateBlobContent(result, originalContent, requiredSize), "Unexpected blob content");
    }

    /**
     * Checks if the blob content is of the required size and matches the expected content based on baseContent.
     *
     * @param blobContent Blob content
     * @param baseContent Base content
     * @param requiredSize Required size
     * @return <code>true</code> content matches, <code>false</code> otherwise
     */
    private boolean validateBlobContent(byte[] blobContent, byte[] baseContent, int requiredSize) {
        if (blobContent.length != requiredSize) return false;
        for (int index = 0; index < blobContent.length; index++) {
            if (blobContent[index] != baseContent[index % baseContent.length]) return false;
        }
        return true;
    }
}
