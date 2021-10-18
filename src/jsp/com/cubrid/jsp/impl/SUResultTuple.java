package com.cubrid.jsp.impl;

import com.cubrid.jsp.data.CUBRIDUnpacker;
import com.cubrid.jsp.data.SOID;
import com.cubrid.jsp.exception.TypeMismatchException;
import com.cubrid.jsp.value.Value;

public class SUResultTuple {
    private int index;
    private SOID oid;
    private Object attributes[];

    SUResultTuple(int tupleIndex, int attributeNumber) {
        index = tupleIndex;
        attributes = new Object[attributeNumber];
    }

    public SUResultTuple(CUBRIDUnpacker unpacker) throws TypeMismatchException {
        index = unpacker.unpackInt();
        int attributeLength = unpacker.unpackInt();

        attributes = new Object[attributeLength];
        for (int i = 0; i < attributeLength; i++) {
            int paramType = unpacker.unpackInt();
            Value v = unpacker.unpackValue(paramType);
            attributes[i] = v;
        }

        oid = unpacker.unpackOID();
    }

    void close() {
        for (int i = 0; attributes != null && i < attributes.length; i++) {
            attributes[i] = null;
        }
        attributes = null;
        oid = null;
    }

    Object getAttribute(int tIndex) {
        /*
         * if (tIndex < 0 || attributes == null || tIndex >= attributes.length)
         * return null;
         */
        return attributes[tIndex];
    }

    SOID getOID() {
        return oid;
    }

    boolean oidIsIncluded() {
        if (oid == null) return false;
        return true;
    }

    void setAttribute(int tIndex, Object data) {
        /*
         * if (wasNull == null || attributes == null || tIndex < 0 || tIndex >
         * wasNull.length - 1 || tIndex > attributes.length - 1) { return; }
         * wasNull[tIndex] = (data == null) ? true : false;
         */

        attributes[tIndex] = data;
    }

    void setOID(SOID o) {
        oid = o;
    }

    int tupleNumber() {
        return index;
    }
}
