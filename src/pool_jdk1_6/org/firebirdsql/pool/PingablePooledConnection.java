package org.firebirdsql.pool;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.StatementEventListener;

@Deprecated
public class PingablePooledConnection extends AbstractPingablePooledConnection {

	protected PingablePooledConnection(Connection connection,
			boolean statementPooling, int maxStatements, boolean keepStatements, PooledConnectionQueue owningQueue)
			throws SQLException {
		super(connection, statementPooling, maxStatements, keepStatements, owningQueue);
	}

	public PingablePooledConnection(Connection connection,
			String pingStatement, int pingInterval, boolean statementPooling,
			int maxStatements, boolean keepStatements, PooledConnectionQueue owningQueue) throws SQLException {
		super(connection, pingStatement, pingInterval, statementPooling, maxStatements, keepStatements, owningQueue);
	}

    public void addStatementEventListener(StatementEventListener arg0) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public void removeStatementEventListener(StatementEventListener arg0) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}