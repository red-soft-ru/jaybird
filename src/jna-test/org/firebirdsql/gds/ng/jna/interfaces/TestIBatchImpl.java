package org.firebirdsql.gds.ng.jna.interfaces;

import org.firebirdsql.gds.BatchParameterBuffer;
import org.firebirdsql.gds.impl.BatchParameterBufferImpl;
import org.firebirdsql.gds.impl.GDSFactory;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.gds.ng.*;
import org.firebirdsql.gds.ng.fields.RowValue;
import org.firebirdsql.gds.ng.jna.AbstractNativeDatabaseFactory;
import org.firebirdsql.gds.ng.wire.SimpleStatementListener;
import org.firebirdsql.jna.fbclient.FbInterface;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test for batch in the OO API implementation.
 *
 * {@link org.firebirdsql.jna.fbclient.FbInterface.IBatch}.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class TestIBatchImpl extends AbstractBatchTest {

    //@formatter:off
    protected String INSERT_QUERY = "INSERT INTO test_p_metadata (" +
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

    protected String TEST_QUERY =
            "SELECT " +
                    "id, simple_field, two_byte_field, three_byte_field, long_field, int_field, short_field," +
                    "float_field, double_field, smallint_numeric, integer_decimal_1, integer_numeric," +
                    "integer_decimal_2, bigint_numeric, bigint_decimal, date_field, time_field," +
                    "timestamp_field " +
            "from test_p_metadata";
    //@formatter:on

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final AbstractNativeDatabaseFactory factory =
            (AbstractNativeDatabaseFactory) GDSFactory.getDatabaseFactoryForType(GDSType.getType("NATIVE"));

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
        BatchParameterBuffer buffer = new BatchParameterBufferImpl();
        buffer.addArgument(FbInterface.IBatch.TAG_RECORD_COUNTS, 1);
        FbBatch batch = db.createBatch(transaction, INSERT_QUERY, buffer);

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
        Date testDate = Date.valueOf(LocalDate.now());
        Time testTime = Time.valueOf(LocalTime.now());
        Timestamp testTimestamp = Timestamp.valueOf(LocalDateTime.now());

        FbMessageBuilder builder = new IMessageBuilderImpl(batch);

        builder.addInteger(0, testInteger);
        builder.addVarchar(1, testVarchar);
        builder.addVarchar(2, testVarchar);
        builder.addVarchar(3, testVarchar);
        builder.addBigint(4, testBigInteger);
        builder.addInteger(5, testInteger);
        builder.addSmallint(6, testShort);
        builder.addFloat(7, testFloat);
        builder.addDouble(8, testDouble);
        builder.addNumeric(9, testSmallintNumeric);
        builder.addDecimal(10, testSmallintNumeric);
        builder.addNumeric(11, testIntNumeric);
        builder.addDecimal(12, testIntNumeric2);
        builder.addNumeric(13, testBigintNumeric);
        builder.addDecimal(14, testBigintNumeric2);
        builder.addDate(15, testDate);
        builder.addTime(16, testTime);
        builder.addTimestamp(17, testTimestamp);

        batch.add(1, builder.getData());

        FbBatchCompletionState execute = batch.execute();

        System.out.println(execute.getAllStates());

        assertThat("Expected successful batch execution", execute.getAllStates(), allOf(
                startsWith("Message Status"),
                containsString("total=1 success=1"),
                endsWith("0\n")));

        batch.getTransaction().commit();

        allocateTransaction();

        FbStatement statement = db.createStatement(transaction);
        final SimpleStatementListener statementListener = new SimpleStatementListener();
        statement.addStatementListener(statementListener);
        statement.prepare(TEST_QUERY);
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        statement.fetchRows(1);
        RowValue fieldValues = statementListener.getRows().get(0);
        byte[] fieldData = fieldValues.getFieldValue(0).getFieldData();
        assertEquals(testInteger,
                statement.getFieldDescriptor().getFieldDescriptor(0).getDatatypeCoder().decodeInt(fieldData));
        fieldData = fieldValues.getFieldValue(1).getFieldData();
        assertEquals(testVarchar,
                statement.getFieldDescriptor().getFieldDescriptor(1).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldValue(2).getFieldData();
        assertEquals(testVarchar,
                statement.getFieldDescriptor().getFieldDescriptor(2).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldValue(3).getFieldData();
        assertEquals(testVarchar,
                statement.getFieldDescriptor().getFieldDescriptor(3).getDatatypeCoder().decodeString(fieldData));
        fieldData = fieldValues.getFieldValue(4).getFieldData();
        assertEquals(testBigInteger,
                statement.getFieldDescriptor().getFieldDescriptor(4).getDatatypeCoder().decodeLong(fieldData));
        fieldData = fieldValues.getFieldValue(5).getFieldData();
        assertEquals(testInteger,
                statement.getFieldDescriptor().getFieldDescriptor(5).getDatatypeCoder().decodeInt(fieldData));
        fieldData = fieldValues.getFieldValue(6).getFieldData();
        assertEquals(testShort,
                statement.getFieldDescriptor().getFieldDescriptor(6).getDatatypeCoder().decodeShort(fieldData));
        fieldData = fieldValues.getFieldValue(7).getFieldData();
        assertEquals(testFloat,
                statement.getFieldDescriptor().getFieldDescriptor(7).getDatatypeCoder().decodeFloat(fieldData),
                0);
        fieldData = fieldValues.getFieldValue(8).getFieldData();
        assertEquals(testDouble,
                statement.getFieldDescriptor().getFieldDescriptor(8).getDatatypeCoder().decodeDouble(fieldData),
                0);
        fieldData = fieldValues.getFieldValue(9).getFieldData();
        short decodeShort = statement.getFieldDescriptor().getFieldDescriptor(9).getDatatypeCoder().decodeShort(fieldData);
        BigDecimal decimal = BigDecimal.valueOf(decodeShort, -statement.getFieldDescriptor().getFieldDescriptor(9).getScale());
        float floatValue = decimal.floatValue();
        assertEquals(testSmallintNumeric,
                floatValue,
                0.001);
        fieldData = fieldValues.getFieldValue(10).getFieldData();
        int decodeInt = statement.getFieldDescriptor().getFieldDescriptor(10).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getFieldDescriptor().getFieldDescriptor(10).getScale());
        double doubleValue = decimal.doubleValue();
        assertEquals(testSmallintNumeric,
                doubleValue,
                0.001);
        fieldData = fieldValues.getFieldValue(11).getFieldData();
        decodeInt = statement.getFieldDescriptor().getFieldDescriptor(11).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getFieldDescriptor().getFieldDescriptor(11).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testIntNumeric,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldValue(12).getFieldData();
        decodeInt = statement.getFieldDescriptor().getFieldDescriptor(12).getDatatypeCoder().decodeInt(fieldData);
        decimal = BigDecimal.valueOf(decodeInt, -statement.getFieldDescriptor().getFieldDescriptor(12).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testIntNumeric2,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldValue(13).getFieldData();
        long decodeLong = statement.getFieldDescriptor().getFieldDescriptor(13).getDatatypeCoder().decodeLong(fieldData);
        decimal = BigDecimal.valueOf(decodeLong, -statement.getFieldDescriptor().getFieldDescriptor(13).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testBigintNumeric,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldValue(14).getFieldData();
        decodeLong = statement.getFieldDescriptor().getFieldDescriptor(14).getDatatypeCoder().decodeLong(fieldData);
        decimal = BigDecimal.valueOf(decodeLong, -statement.getFieldDescriptor().getFieldDescriptor(14).getScale());
        doubleValue = decimal.doubleValue();
        assertEquals(testBigintNumeric2,
                doubleValue,
                0);
        fieldData = fieldValues.getFieldValue(15).getFieldData();
        assertEquals(testDate,
                statement.getFieldDescriptor().getFieldDescriptor(15).getDatatypeCoder().decodeDate(fieldData));
        fieldData = fieldValues.getFieldValue(16).getFieldData();
        assertEquals(testTime,
                statement.getFieldDescriptor().getFieldDescriptor(16).getDatatypeCoder().decodeTime(fieldData));
        fieldData = fieldValues.getFieldValue(17).getFieldData();
        assertEquals(testTimestamp,
                statement.getFieldDescriptor().getFieldDescriptor(17).getDatatypeCoder().decodeTimestamp(fieldData));
        int stop = 0;
    }
}
