/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.airlift.bytecode;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;

import java.util.Map;

import static io.airlift.bytecode.ArrayOpCode.AType.T_BOOLEAN;
import static io.airlift.bytecode.ArrayOpCode.AType.T_BYTE;
import static io.airlift.bytecode.ArrayOpCode.AType.T_CHAR;
import static io.airlift.bytecode.ArrayOpCode.AType.T_DOUBLE;
import static io.airlift.bytecode.ArrayOpCode.AType.T_FLOAT;
import static io.airlift.bytecode.ArrayOpCode.AType.T_INT;
import static io.airlift.bytecode.ArrayOpCode.AType.T_LONG;
import static io.airlift.bytecode.ArrayOpCode.AType.T_SHORT;
import static io.airlift.bytecode.OpCode.AALOAD;
import static io.airlift.bytecode.OpCode.AASTORE;
import static io.airlift.bytecode.OpCode.BALOAD;
import static io.airlift.bytecode.OpCode.BASTORE;
import static io.airlift.bytecode.OpCode.CALOAD;
import static io.airlift.bytecode.OpCode.CASTORE;
import static io.airlift.bytecode.OpCode.DALOAD;
import static io.airlift.bytecode.OpCode.DASTORE;
import static io.airlift.bytecode.OpCode.FALOAD;
import static io.airlift.bytecode.OpCode.FASTORE;
import static io.airlift.bytecode.OpCode.IALOAD;
import static io.airlift.bytecode.OpCode.IASTORE;
import static io.airlift.bytecode.OpCode.LALOAD;
import static io.airlift.bytecode.OpCode.LASTORE;
import static io.airlift.bytecode.OpCode.SALOAD;
import static io.airlift.bytecode.OpCode.SASTORE;
import static java.util.Objects.requireNonNull;

public enum ArrayOpCode
{
    NOT_PRIMITIVE(null, AALOAD, AASTORE, null),
    BYTE(byte.class, BALOAD, BASTORE, T_BYTE),
    BOOLEAN(boolean.class, BALOAD, BASTORE, T_BOOLEAN),
    CHAR(char.class, CALOAD, CASTORE, T_CHAR),
    SHORT(short.class, SALOAD, SASTORE, T_SHORT),
    INT(int.class, IALOAD, IASTORE, T_INT),
    LONG(long.class, LALOAD, LASTORE, T_LONG),
    FLOAT(float.class, FALOAD, FASTORE, T_FLOAT),
    DOUBLE(double.class, DALOAD, DASTORE, T_DOUBLE);

    private final OpCode load;
    private final OpCode store;
    private final AType atype;
    private final Class<?> type;

    private static final Map<Class<?>, ArrayOpCode> arrayOpCodeMap = initializeArrayOpCodeMap();

    ArrayOpCode(@Nullable Class<?> clazz, OpCode load, OpCode store, @Nullable AType atype)
    {
        this.type = clazz;
        this.load = requireNonNull(load, "load is null");
        this.store = requireNonNull(store, "store is null");
        this.atype = atype;
    }

    public OpCode getLoad()
    {
        return load;
    }

    public OpCode getStore()
    {
        return store;
    }

    public int getAtype()
    {
        return atype.getType();
    }

    public Class<?> getType()
    {
        return type;
    }

    public static ArrayOpCode getArrayOpCode(ParameterizedType type)
    {
        if (!type.isPrimitive()) {
            return NOT_PRIMITIVE;
        }
        ArrayOpCode arrayOpCode = arrayOpCodeMap.get(type.getPrimitiveType());
        if (arrayOpCode == null) {
            throw new IllegalArgumentException("unsupported primitive type " + type);
        }
        return arrayOpCode;
    }

    static Map<Class<?>, ArrayOpCode> initializeArrayOpCodeMap()
    {
        ImmutableMap.Builder<Class<?>, ArrayOpCode> builder = ImmutableMap.builder();
        for (ArrayOpCode arrayOpCode : values()) {
            if (arrayOpCode.getType() != null) {
                builder.put(arrayOpCode.getType(), arrayOpCode);
            }
        }
        return builder.build();
    }

    enum AType
    {
        T_BOOLEAN(4),
        T_CHAR(5),
        T_FLOAT(6),
        T_DOUBLE(7),
        T_BYTE(8),
        T_SHORT(9),
        T_INT(10),
        T_LONG(11);

        private final int type;

        AType(int type)
        {
            this.type = type;
        }

        int getType()
        {
            return type;
        }
    }
}
