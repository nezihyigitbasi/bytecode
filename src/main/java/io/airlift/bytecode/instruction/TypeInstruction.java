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
package io.airlift.bytecode.instruction;

import com.google.common.collect.ImmutableList;
import io.airlift.bytecode.BytecodeNode;
import io.airlift.bytecode.BytecodeVisitor;
import io.airlift.bytecode.MethodGenerationContext;
import io.airlift.bytecode.OpCode;
import io.airlift.bytecode.ParameterizedType;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static io.airlift.bytecode.ArrayOpCode.getArrayOpCode;
import static io.airlift.bytecode.OpCode.ANEWARRAY;
import static io.airlift.bytecode.OpCode.CHECKCAST;
import static io.airlift.bytecode.OpCode.INSTANCEOF;
import static io.airlift.bytecode.OpCode.NEW;
import static io.airlift.bytecode.OpCode.NEWARRAY;
import static io.airlift.bytecode.ParameterizedType.type;

@SuppressWarnings("UnusedDeclaration")
public class TypeInstruction
        implements InstructionNode
{
    public static InstructionNode newObject(Class<?> type)
    {
        return new TypeInstruction(NEW, type(type));
    }

    public static InstructionNode newObject(ParameterizedType type)
    {
        return new TypeInstruction(NEW, type);
    }

    public static InstructionNode newPrimitiveArray(ParameterizedType type)
    {
        return new TypeInstruction(NEWARRAY, type);
    }

    public static InstructionNode newObjectArray(Class<?> type)
    {
        return new TypeInstruction(ANEWARRAY, type(type));
    }

    public static InstructionNode newObjectArray(ParameterizedType type)
    {
        return new TypeInstruction(ANEWARRAY, type);
    }

    public static InstructionNode instanceOf(Class<?> type)
    {
        return new TypeInstruction(INSTANCEOF, type(type));
    }

    public static InstructionNode instanceOf(ParameterizedType type)
    {
        return new TypeInstruction(INSTANCEOF, type);
    }

    public static InstructionNode cast(Class<?> type)
    {
        return new TypeInstruction(CHECKCAST, type(type));
    }

    public static InstructionNode cast(ParameterizedType type)
    {
        return new TypeInstruction(CHECKCAST, type);
    }

    private final OpCode opCode;
    private final ParameterizedType type;

    public TypeInstruction(OpCode opCode, ParameterizedType type)
    {
        this.opCode = opCode;
        this.type = type;
    }

    @Override
    public void accept(MethodVisitor visitor, MethodGenerationContext generationContext)
    {
        if (opCode == NEWARRAY) {
            checkState(type.isPrimitive(), "need primitive type for NEWARRAY");
            visitor.visitIntInsn(opCode.getOpCode(), getPrimitiveArrayType(type));
        }
        else {
            visitor.visitTypeInsn(opCode.getOpCode(), type.getClassName());
        }
    }

    private static int getPrimitiveArrayType(ParameterizedType type)
    {
        return getArrayOpCode(type).getAtype();
    }

    @Override
    public List<BytecodeNode> getChildNodes()
    {
        return ImmutableList.of();
    }

    @Override
    public <T> T accept(BytecodeNode parent, BytecodeVisitor<T> visitor)
    {
        return visitor.visitInstruction(parent, this);
    }

    @Override
    public String toString()
    {
        return opCode + " " + type;
    }
}
