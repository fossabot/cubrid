/*
 * Copyright (C) 2008 Search Solution Corporation.
 * Copyright (c) 2016 CUBRID Corporation.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package com.cubrid.jsp.jdbc;

import com.cubrid.jsp.data.DBType;
import cubrid.jdbc.jci.CUBRIDCommandType;
import cubrid.sql.CUBRIDOID;
import cubrid.sql.CUBRIDTimestamptz;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Title: CUBRID JDBC Driver Description:
 *
 * @version 2.0
 */
public class CUBRIDServerSidePreparedStatement extends CUBRIDServerSideStatement
        implements PreparedStatement {
    private static final Logger LOG = Logger.getGlobal();

    private String sql;
    private int autoGeneratedKeys;

    CUBRIDServerSidePreparedStatement(
            CUBRIDServerSideConnection con,
            String sql,
            int type,
            int concurrency,
            int holdable,
            int autoGeneratedKeys)
            throws SQLException {
        super(con, type, concurrency, holdable);
        this.sql = sql;
        this.autoGeneratedKeys = autoGeneratedKeys;

        try {
            prepareInternal(sql);
        } catch (IOException e) {
            throw CUBRIDServerSideJDBCErrorManager.createCUBRIDException(
                    CUBRIDServerSideJDBCErrorCode.ER_COMMUNICATION, e);
        }
    }

    // ==============================================================
    // The following is JDBC Interface Implementations
    // ==============================================================

    @Override
    public int[] executeBatch() throws SQLException {
        // TODO: not implemented yet
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        try {
            // 1) complete resultset
            completeResultSet();

            // TODO: is it right?
            // 2) check SQL Type (SELECT, CALL, GET_STATS, EVALUATE)
            if (statementHandler.getSQLType() == false
                    && statementHandler.getStatementType()
                            != CUBRIDCommandType.CUBRID_STMT_CALL_SP) {
                // statementHandler.close()?
                statementHandler = null;
                throw CUBRIDServerSideJDBCErrorManager.createCUBRIDException(
                        CUBRIDServerSideJDBCErrorCode.ER_INVALID_QUERY_TYPE_FOR_EXECUTEQUERY, null);
            }

            // 3) execute
            executeInternal(false);

            // 4) result set
            getMoreResults();
        } catch (IOException e) {
            throw CUBRIDServerSideJDBCErrorManager.createCUBRIDException(
                    CUBRIDServerSideJDBCErrorCode.ER_COMMUNICATION, e);
        }

        return currentResultSet;
    }

    @Override
    public int executeUpdate() throws SQLException {
        try {
            // 1) complete resultset
            completeResultSet();

            // 2) check SQL Type (SELECT, CALL, GET_STATS, EVALUATE)
            if (statementHandler.getSQLType() == true) {
                // statementHandler.close()?
                statementHandler = null;
                throw CUBRIDServerSideJDBCErrorManager.createCUBRIDException(
                        CUBRIDServerSideJDBCErrorCode.ER_INVALID_QUERY_TYPE_FOR_EXECUTEUPDATE,
                        null);
            }

            // 3) execute
            executeInternal(false);

            // 4) result set
            getMoreResults();

            // 5) make auto generated keys resultset
            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS
                    && statementHandler.getStatementType()
                            == CUBRIDCommandType.CUBRID_STMT_INSERT) {
                makeAutoGeneratedKeysResultSet();
            }

            if (statementHandler.getStatementType() != CUBRIDCommandType.CUBRID_STMT_CALL_SP) {
                completeResultSet();
            }

            return getUpdateCount();

        } catch (IOException e) {
            throw CUBRIDServerSideJDBCErrorManager.createCUBRIDException(
                    CUBRIDServerSideJDBCErrorCode.ER_COMMUNICATION, e);
        }
    }

    @Override
    public boolean execute() throws SQLException {
        try {
            // 1) complete resultset
            completeResultSet();

            // 2) set autoGeneratedKeys
            if (this.autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS
                    && statementHandler.getStatementType()
                            == CUBRIDCommandType.CUBRID_STMT_INSERT) {
                statementHandler.setAutoGeneratedKeys(true);
            }

            // 3) execute
            executeInternal(true);

            // 4) result set
            boolean result = getMoreResults();

            // 5) make auto generated keys resultset
            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS
                    && statementHandler.getStatementType()
                            == CUBRIDCommandType.CUBRID_STMT_INSERT) {
                makeAutoGeneratedKeysResultSet();
            }

            if (statementHandler.getResultSize() == 1) {
                int cmdType = statementHandler.getStatementType();
                if (cmdType != CUBRIDCommandType.CUBRID_STMT_CALL_SP
                        && cmdType != CUBRIDCommandType.CUBRID_STMT_SELECT) {
                    completeResultSet();
                }
            }

            return result;
        } catch (IOException e) {
            throw CUBRIDServerSideJDBCErrorManager.createCUBRIDException(
                    CUBRIDServerSideJDBCErrorCode.ER_COMMUNICATION, e);
        }
    }

    /**
     * Executes an SQL <code>INSERT</code> statement in <code>this PreparedStatement</code> object
     * and returns a <code>CUBRIDOID</code> object that represents the OID of the object inserted by
     * the statement.
     *
     * @return a <code>CUBRIDOID</code> object that represents the OID of the object inserted by the
     *     statement.
     * @exception SQLException if <code>this</code> object is closed.
     * @exception SQLException if the statement in <code>this PreparedStatement</code> object is not
     *     an SQL <code>INSERT</code> statement.
     * @exception SQLException if a database access error occurs
     */
    @Override
    public CUBRIDOID executeInsert(String sql) throws SQLException {
        return super.executeInsert(sql);
    }

    @Override
    public void clearParameters() throws SQLException {
        getStatementHandler().clearBindParameters();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_NULL, null);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        int data = (x ? 1 : 0);
        getStatementHandler().bindValue(parameterIndex, DBType.DB_INT, data);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        short data = x;
        getStatementHandler().bindValue(parameterIndex, DBType.DB_SHORT, data);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_SHORT, x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_INT, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_BIGINT, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_FLOAT, x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_DOUBLE, x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_NUMERIC, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_STRING, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_VARBIT, x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_DATE, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_TIME, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_TIMESTAMP, x);
    }

    public void setTimestamptz(int parameterIndex, CUBRIDTimestamptz x) throws SQLException {
        getStatementHandler().bindValue(parameterIndex, DBType.DB_TIMESTAMPTZ, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // TODO: not implemented yet
        throw new SQLException(new UnsupportedOperationException());
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        throw new SQLException(new UnsupportedOperationException());
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // TODO: not implemented yet
        throw new SQLException(new UnsupportedOperationException());
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
            throws SQLException {
        if (x instanceof Blob) {
            setBlob(parameterIndex, (Blob) x);
        } else if (x instanceof Clob) {
            setClob(parameterIndex, (Clob) x);
        } else {
            if (x != null
                    && (targetSqlType == java.sql.Types.NUMERIC
                            || targetSqlType == java.sql.Types.DECIMAL)) {
                Number n = null;
                try {
                    n = (Number) x;
                } catch (Exception e) {
                    // TODO: not implemented yet
                    throw new SQLException(new UnsupportedOperationException());
                }
                if (n != null) {
                    // TODO: not implemented yet
                    throw new SQLException(new UnsupportedOperationException());
                }
            } else {
                // TODO: not implemented yet
                throw new SQLException(new UnsupportedOperationException());
            }
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        setObject(parameterIndex, x);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        if (x instanceof Blob) {
            setBlob(parameterIndex, (Blob) x);
            return;
        } else if (x instanceof Clob) {
            setClob(parameterIndex, (Clob) x);
            return;
        } else {
            // TODO: not implemented yet
            throw new SQLException(new UnsupportedOperationException());
        }
    }

    @Override
    public void addBatch() throws SQLException {
        // TODO: not implemented yet
        throw new SQLException(new UnsupportedOperationException());
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length)
            throws SQLException {
        // TODO: not implemented yet
        throw new SQLException(new UnsupportedOperationException());
    }

    @Override
    public void setRef(int i, Ref x) throws SQLException {
        throw new SQLException(new UnsupportedOperationException());
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        // TODO: blob is not implemented yet
        throw new SQLException(new UnsupportedOperationException());
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        // TODO: clob is not implemented yet
        throw new SQLException(new UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        // TODO: blob is not implemented yet
        throw new SQLException(new UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length)
            throws SQLException {
        // TODO: blob is not implemented yet
        throw new SQLException(new UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        // TODO: clob is not implemented yet
        throw new SQLException(new UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        // TODO: clob is not implemented yet
        throw new SQLException(new UnsupportedOperationException());
    }

    @Override
    public void setArray(int i, Array x) throws SQLException {
        throw new SQLException(new UnsupportedOperationException());
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new CUBRIDServerSideResultSetMetaData(statementHandler);
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        setDate(parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        setTime(parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        setTimestamp(parameterIndex, x);
    }

    public void setTimestamptz(int parameterIndex, CUBRIDTimestamptz x, Calendar cal)
            throws SQLException {
        setTimestamptz(parameterIndex, x);
    }

    @Override
    public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
        setNull(paramIndex, sqlType);
    }

    /* TODO: OID is not implmented yet */
    /*
    public void setOID(int parameterIndex, CUBRIDOID x) throws SQLException {
        // TODO: CUBRIDOID is not implemented yet
    }
    */

    /* TODO: Collection is not implmented yet */
    /*
    public void setCollection(int parameterIndex, Object[] x) throws SQLException {
        getStatementHandler().bindCollection(parameterIndex, x);
    }
    */

    @Override
    public void close() throws SQLException {
        super.close();
    }

    @Override
    public void clearBatch() throws SQLException {
        batchStrings.clear();
    }

    // 3.0
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new SQLException(new UnsupportedOperationException());
    }

    @Override
    public void setURL(int index, URL x) throws SQLException {
        throw new SQLException(new UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length)
            throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length)
            throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length)
            throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }

    /* JDK 1.6 */
    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLException(new java.lang.UnsupportedOperationException());
    }
}
