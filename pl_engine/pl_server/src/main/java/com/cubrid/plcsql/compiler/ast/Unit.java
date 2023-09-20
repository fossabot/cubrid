/*
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

package com.cubrid.plcsql.compiler.ast;

import com.cubrid.plcsql.compiler.visitor.AstVisitor;
import java.sql.*;
import java.util.Set;
import org.antlr.v4.runtime.ParserRuleContext;

public class Unit extends AstNode {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitUnit(this);
    }

    public final boolean autonomousTransaction;
    public final boolean connectionRequired;
    public final Set<String> imports;
    public final DeclRoutine routine;

    public Unit(
            ParserRuleContext ctx,
            boolean autonomousTransaction,
            boolean connectionRequired,
            Set<String> imports,
            DeclRoutine routine) {
        super(ctx);

        assert routine.scope.level == 1;

        this.autonomousTransaction = autonomousTransaction;
        this.connectionRequired = connectionRequired;
        this.imports = imports;
        this.routine = routine;
    }

    public String getJavaSignature() {

        String ret;
        if (routine.paramList == null) {
            ret = String.format("%s.%s()", getClassName(), routine.name);
        } else {
            boolean first = true;
            StringBuffer sbuf = new StringBuffer();
            for (DeclParam dp : routine.paramList.nodes) {
                if (first) {
                    first = false;
                } else {
                    sbuf.append(", ");
                }

                sbuf.append(dp.toJavaSignature());
            }

            ret = String.format("%s.%s(%s)", getClassName(), routine.name, sbuf.toString());
        }

        if (routine.isProcedure()) {
            return ret;
        } else {
            return (ret + " return " + routine.retType.toJavaSignature());
        }
    }

    public String getClassName() {

        if (className == null) {
            String kindStr = routine.isProcedure() ? "Proc" : "Func";
            className = String.format("%s_%s", kindStr, routine.name);
        }

        return className;
    }

    public String[] getImportsArray() {
        if (imports.size() == 0) {
            return new String[] {"// no imports"};
        } else {
            return imports.toArray(dummyStrArr);
        }
    }

    // ------------------------------------------
    // Private
    // ------------------------------------------

    private static final String[] dummyStrArr = new String[0];

    private String className;
}
