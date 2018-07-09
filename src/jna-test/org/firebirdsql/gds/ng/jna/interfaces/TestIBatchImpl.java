package org.firebirdsql.gds.ng.jna.interfaces;

import org.firebirdsql.gds.impl.GDSFactory;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.gds.ng.AbstractBatchTest;
import org.firebirdsql.gds.ng.FbDatabase;
import org.firebirdsql.gds.ng.jna.AbstractNativeDatabaseFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.SQLException;

/**
 * Test for batch in the OO API implementation.
 *
 * {@link org.firebirdsql.jna.fbclient.FbInterface.IBatch}.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class TestIBatchImpl extends AbstractBatchTest {

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
}
