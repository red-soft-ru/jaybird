package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.common.FBJUnit4TestBase;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.TransactionParameterBuffer;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.jca.FBManagedConnectionFactory;
import org.firebirdsql.jca.FBResourceException;
import org.firebirdsql.jca.InternalConnectionManager;
import org.firebirdsql.jdbc.AbstractConnection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.resource.spi.LocalTransaction;
import javax.sql.DataSource;
import java.sql.Statement;

import static org.firebirdsql.common.FBTestProperties.*;

public class TestEmbeddedTransactions extends FBJUnit4TestBase {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    public FBManagedConnectionFactory initMcf() {
        FBManagedConnectionFactory mcf = new FBManagedConnectionFactory(GDSType.getType("EMBEDDED"));
        mcf.setDefaultConnectionManager(new InternalConnectionManager());
        mcf.setDatabase(DB_DATASOURCE_URL);
        mcf.setUserName(DB_USER);
        mcf.setPassword(DB_PASSWORD);
        mcf.setBuffersNumber(90);
        mcf.setSqlDialect("3");

        return mcf;
    }

    @Test
    public void testLocalTransactionWithZeroLockTimeout() throws Exception {
        FBManagedConnectionFactory mcf = initMcf();
        DataSource ds = (DataSource) mcf.createConnectionFactory();
        AbstractConnection c = (AbstractConnection) ds.getConnection();

        expectedException.expect(FBResourceException.class);

        try {

            Statement s = c.createStatement();
            LocalTransaction t = c.getLocalTransaction();
            TransactionParameterBuffer tpb = c.createTransactionParameterBuffer();
            tpb.addArgument(TransactionParameterBuffer.READ_COMMITTED);
            tpb.addArgument(TransactionParameterBuffer.REC_VERSION);
            tpb.addArgument(TransactionParameterBuffer.WRITE);
            tpb.addArgument(TransactionParameterBuffer.WAIT);
            tpb.addArgument(ISCConstants.isc_tpb_lock_timeout, 0);
            c.setTransactionParameters(tpb);
            t.begin();

        } finally {
            c.close();
        }
    }

    @Test
    public void testLocalTransactionWithLockTimeout() throws Exception {
        FBManagedConnectionFactory mcf = initMcf();
        DataSource ds = (DataSource) mcf.createConnectionFactory();
        AbstractConnection c = (AbstractConnection) ds.getConnection();

        try {

            Statement s = c.createStatement();
            LocalTransaction t = c.getLocalTransaction();
            TransactionParameterBuffer tpb = c.createTransactionParameterBuffer();
            tpb.addArgument(TransactionParameterBuffer.READ_COMMITTED);
            tpb.addArgument(TransactionParameterBuffer.REC_VERSION);
            tpb.addArgument(TransactionParameterBuffer.WRITE);
            tpb.addArgument(TransactionParameterBuffer.WAIT);
            tpb.addArgument(ISCConstants.isc_tpb_lock_timeout, 5);
            c.setTransactionParameters(tpb);
            t.begin();

        } finally {
            c.close();
        }
    }
}
