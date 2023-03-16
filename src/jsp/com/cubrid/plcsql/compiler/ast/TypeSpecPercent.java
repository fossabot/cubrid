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

public class TypeSpecPercent extends TypeSpec {

    private TypeSpec resolvedType;

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitTypeSpecPercent(this);
    }

    public final String table;
    public final String column;

    public TypeSpecPercent(String table, String column) {
        super("%TODO-TypeSpecPercent%"); // name unknown yet
        this.table = table;
        this.column = column;
    }

    public void setResolvedType(TypeSpec resolvedType) {
        this.resolvedType = resolvedType;
    }

    @Override
    public String toJavaSignature() {
        if (resolvedType == null) {
            // assert false;    // TODO: restore these two lines
            // throw new RuntimeException("unreachable");
            return super.toJavaCode();
        } else {
            return resolvedType.toJavaSignature();
        }
    }

    @Override
    public String toJavaCode() {
        if (resolvedType == null) {
            // assert false;    // TODO: restore these two lines
            // throw new RuntimeException("unreachable");
            return super.toJavaCode();
        } else {
            return resolvedType.toJavaCode();
        }
    }
}
