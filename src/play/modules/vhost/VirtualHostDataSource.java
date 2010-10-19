package play.modules.vhost;

import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

public class VirtualHostDataSource implements DataSource
{
  private boolean                   autoCommit = false;
  private DataSource                target;
  private ThreadLocal<VHConnection> connection = new ThreadLocal<VHConnection>();

  public VirtualHostDataSource(DataSource ds)
  {
    this.target = ds;
  }

  public Connection getConnection() throws SQLException
  {
    VHConnection result = connection.get();
    if (result == null) {
      result = new VHConnection(this, target.getConnection());
      if (result.target.getAutoCommit() != autoCommit) result.target.setAutoCommit(autoCommit);
      connection.set(result);
      return result;
    }
    return new VHConnection(this, result.target);
  }

  // Not supported
  public Connection getConnection(String username, String password) throws SQLException
  {
    return null;
  }

  public PrintWriter getLogWriter() throws SQLException
  {
    return target.getLogWriter();
  }

  public int getLoginTimeout() throws SQLException
  {
    return target.getLoginTimeout();
  }

  public void setLogWriter(PrintWriter out) throws SQLException
  {
    target.setLogWriter(out);
  }

  public void setLoginTimeout(int seconds) throws SQLException
  {
    target.setLoginTimeout(seconds);
  }

  private static class VHConnection implements Connection
  {
    private Connection            target;
    private VirtualHostDataSource dataSource;

    public VHConnection(VirtualHostDataSource dataSource, Connection target)
    {
      this.dataSource = dataSource;
      this.target = target;
    }

    public void clearWarnings() throws SQLException
    {
      target.clearWarnings();
    }

    public void close() throws SQLException
    {
      if (this == dataSource.connection.get()) {
        target.close();
        dataSource.connection.set(null);
      }
    }

    public boolean isClosed() throws SQLException
    {
      return target.isClosed();
    }

    public boolean isReadOnly() throws SQLException
    {
      return target.isReadOnly();
    }

    public void setReadOnly(boolean readOnly) throws SQLException
    {
      target.setReadOnly(readOnly);
    }

    public boolean getAutoCommit() throws SQLException
    {
      return target.getAutoCommit();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException
    {
      if (this != dataSource.connection.get()) return;
      target.setAutoCommit(autoCommit);
    }

    public void commit() throws SQLException
    {
      if (this != dataSource.connection.get()) return;
      target.commit();
    }

    public void rollback() throws SQLException
    {
      if (this != dataSource.connection.get()) return;
      target.rollback();
    }

    public void rollback(Savepoint savepoint) throws SQLException
    {
      if (this != dataSource.connection.get()) return;
      target.rollback(savepoint);
    }

    public Savepoint setSavepoint() throws SQLException
    {
      return target.setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException
    {
      return target.setSavepoint(name);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException
    {
      target.releaseSavepoint(savepoint);
    }

    public Statement createStatement() throws SQLException
    {
      return target.createStatement();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
    {
      return target.createStatement(resultSetType, resultSetConcurrency);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
      return target.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public String getCatalog() throws SQLException
    {
      return target.getCatalog();
    }

    public int getHoldability() throws SQLException
    {
      return target.getHoldability();
    }

    public DatabaseMetaData getMetaData() throws SQLException
    {
      return target.getMetaData();
    }

    public int getTransactionIsolation() throws SQLException
    {
      return target.getTransactionIsolation();
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException
    {
      return target.getTypeMap();
    }

    public SQLWarning getWarnings() throws SQLException
    {
      return target.getWarnings();
    }

    public String nativeSQL(String sql) throws SQLException
    {
      return target.nativeSQL(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException
    {
      return target.prepareCall(sql);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
      return target.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
      return target.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException
    {
      return target.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
    {
      return target.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
    {
      return target.prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
    {
      return target.prepareStatement(sql, columnNames);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
      return target.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
      return target.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public void setCatalog(String catalog) throws SQLException
    {
      target.setCatalog(catalog);
    }

    public void setHoldability(int holdability) throws SQLException
    {
      target.setHoldability(holdability);
    }

    public void setTransactionIsolation(int level) throws SQLException
    {
      target.setTransactionIsolation(level);
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException
    {
      target.setTypeMap(map);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
      // TODO Auto-generated method stub
      return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException
    {
      // TODO Auto-generated method stub
      return null;
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException
    {
      // TODO Auto-generated method stub
      return null;
    }

    public Blob createBlob() throws SQLException
    {
      // TODO Auto-generated method stub
      return null;
    }

    public Clob createClob() throws SQLException
    {
      // TODO Auto-generated method stub
      return null;
    }

    public NClob createNClob() throws SQLException
    {
      // TODO Auto-generated method stub
      return null;
    }

    public SQLXML createSQLXML() throws SQLException
    {
      // TODO Auto-generated method stub
      return null;
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException
    {
      // TODO Auto-generated method stub
      return null;
    }

    public Properties getClientInfo() throws SQLException
    {
      // TODO Auto-generated method stub
      return null;
    }

    public String getClientInfo(String name) throws SQLException
    {
      // TODO Auto-generated method stub
      return null;
    }

    public boolean isValid(int timeout) throws SQLException
    {
      // TODO Auto-generated method stub
      return false;
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException
    {
      // TODO Auto-generated method stub
      
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException
    {
      // TODO Auto-generated method stub
      
    }

  }

  public boolean isWrapperFor(Class<?> ds) throws SQLException
  {
    return target.getClass().equals(ds.getClass());
  }

  public <T> T unwrap(Class<T> ds) throws SQLException
  {
    return null;
  }

}
