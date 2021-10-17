package com.cubrid.jsp.data;
import com.cubrid.jsp.jdbc.CUBRIDServerSideOID;
import cubrid.jdbc.jci.CUBRIDCommandType;
import cubrid.sql.CUBRIDOID;

public class QueryResultInfo {
    public int stmtType;
    public int tupleCount;
    public SOID insOid;
    public long queryId;   /* Query Identifier for select */

    public QueryResultInfo (CUBRIDUnpacker unpacker) {
        stmtType = unpacker.unpackInt();
        tupleCount = unpacker.unpackInt();

        insOid = new SOID (unpacker);
        queryId = unpacker.unpackBigint();
    }

    public boolean isReusltSet() {
        if (stmtType == CUBRIDCommandType.CUBRID_STMT_SELECT
                || stmtType == CUBRIDCommandType.CUBRID_STMT_CALL
                || stmtType == CUBRIDCommandType.CUBRID_STMT_GET_STATS
                || stmtType == CUBRIDCommandType.CUBRID_STMT_EVALUATE) {
            return true;
        } else {
            return false;
        }
    }

    public int getResultCount () {
        return tupleCount;
    }

    public SOID getCUBRIDOID () {
        return insOid;
    }
}
