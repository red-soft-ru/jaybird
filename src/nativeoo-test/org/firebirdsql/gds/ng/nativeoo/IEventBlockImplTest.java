package org.firebirdsql.gds.ng.nativeoo;

import org.firebirdsql.common.FBTestProperties;
import org.firebirdsql.common.extension.GdsTypeExtension;
import org.firebirdsql.common.extension.UsesDatabaseExtension;
import org.firebirdsql.gds.EventHandle;
import org.firebirdsql.gds.ng.FbConnectionProperties;
import org.firebirdsql.gds.ng.FbDatabase;
import org.firebirdsql.gds.ng.FbStatement;
import org.firebirdsql.gds.ng.FbTransaction;
import org.firebirdsql.gds.ng.SimpleEventHandler;
import org.firebirdsql.gds.ng.fields.RowValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.firebirdsql.common.FBTestProperties.getDefaultTpb;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for OO API events implementation. See {@link org.firebirdsql.gds.ng.nativeoo.IDatabaseImpl}.
 *
 * @since 4.0
 */
class IEventBlockImplTest {

    private static final System.Logger log = System.getLogger(IEventBlockImplTest.class.getName());

    @RegisterExtension
    @Order(1)
    static final GdsTypeExtension testType = GdsTypeExtension.supportsFBOONativeOnly();

    //@formatter:off
    private static final String TABLE_DEF =
            "CREATE TABLE TEST (" +
                    "     TESTVAL INTEGER NOT NULL" +
                    ")";

    private static final String TRIGGER_DEF =
            "CREATE TRIGGER INSERT_TRIG " +
                    "     FOR TEST AFTER INSERT " +
                    "AS BEGIN " +
                    "     POST_EVENT 'TEST_EVENT_A';" +
                    "     POST_EVENT 'TEST_EVENT_B';" +
                    "END";
    //@formatter:on

    @RegisterExtension
    static final UsesDatabaseExtension.UsesDatabaseForAll usesDatabase = UsesDatabaseExtension.usesDatabaseForAll(
            TABLE_DEF,
            TRIGGER_DEF);

    private final AbstractNativeOODatabaseFactory factory =
            (AbstractNativeOODatabaseFactory) FBTestProperties.getFbDatabaseFactory();
    private final FbConnectionProperties connectionInfo = FBTestProperties.getDefaultFbConnectionProperties();

    private IDatabaseImpl db;

    @AfterEach
    void tearDown() {
        if (db != null && db.isAttached()) {
            try {
                db.close();
            } catch (SQLException ex) {
                log.log(System.Logger.Level.DEBUG, "Exception on detach", ex);
            }
        }
    }

    @Test
    void testCreateEventHandle() throws Exception {
        db = factory.connect(connectionInfo);
        db.attach();

        IEventImpl eventHandle = db.createEventHandle("TEST_EVENT", eventHandle1 -> { });

        assertTrue(eventHandle.getSize() > 0, "Event handle should have a size set");
    }

    @Test
    void testQueueEvent_andNotification() throws Exception {
        db = factory.connect(connectionInfo);
        db.attach();

        var eventHandler = new SimpleEventHandler();

        EventHandle eventHandleA = db.createEventHandle("TEST_EVENT_A", eventHandler);
        EventHandle eventHandleB = db.createEventHandle("TEST_EVENT_B", eventHandler);

        // Initial queue will return events immediately
        db.queueEvent(eventHandleA);
        db.queueEvent(eventHandleB);
        with().pollInterval(50, TimeUnit.MILLISECONDS)
                .await().atMost(1, TimeUnit.SECONDS)
                .until(eventHandler::getReceivedEventHandles, hasItems(eventHandleA, eventHandleB));

        db.countEvents(eventHandleA);
        db.countEvents(eventHandleB);

        eventHandler.clearEvents();

        db.queueEvent(eventHandleA);
        db.queueEvent(eventHandleB);

        with().pollInterval(50, TimeUnit.MILLISECONDS).pollDelay(Duration.ZERO).await()
                .during(50, TimeUnit.MILLISECONDS).until(eventHandler::getReceivedEventHandles, empty());

        FbTransaction transaction = getTransaction(db);
        FbStatement statement = db.createStatement(transaction);
        statement.prepare("INSERT INTO TEST VALUES (1)");
        statement.execute(RowValue.EMPTY_ROW_VALUE);
        transaction.commit();

        with().pollInterval(50, TimeUnit.MILLISECONDS)
                .await().atMost(5, TimeUnit.SECONDS)
                .until(eventHandler::getReceivedEventHandles, hasItems(eventHandleA, eventHandleB));

        db.countEvents(eventHandleA);
        db.countEvents(eventHandleB);
        assertEquals(1, eventHandleA.getEventCount());
        assertEquals(1, eventHandleB.getEventCount());

        // TODO Workaround for CORE-4794
        db.queueEvent(eventHandleA);
        db.queueEvent(eventHandleB);

        db.cancelEvent(eventHandleA);
        db.cancelEvent(eventHandleB);
    }

    private FbTransaction getTransaction(FbDatabase db) throws SQLException {
        return db.startTransaction(getDefaultTpb());
    }
}
