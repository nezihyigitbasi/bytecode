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
package io.airlift.bytecode.expression;

import io.airlift.bytecode.BytecodeBlock;
import io.airlift.bytecode.BytecodeNode;
import io.airlift.bytecode.Scope;
import io.airlift.bytecode.Variable;
import org.testng.annotations.Test;

import java.awt.Point;
import java.util.function.Function;

import static io.airlift.bytecode.ParameterizedType.type;
import static io.airlift.bytecode.expression.BytecodeExpressionAssertions.assertBytecodeNode;
import static io.airlift.bytecode.expression.BytecodeExpressions.constantInt;
import static io.airlift.bytecode.expression.BytecodeExpressions.newInstance;
import static org.testng.Assert.assertEquals;

public class TestSetVariableBytecodeExpression
{
    @Test
    public void testGetField()
            throws Exception
    {
        Function<Scope, BytecodeNode> nodeGenerator = scope -> {
            Variable point = scope.declareVariable(Point.class, "point");
            BytecodeExpression setPoint = point.set(newInstance(Point.class, constantInt(3), constantInt(7)));

            assertEquals(setPoint.toString(), "point = new Point(3, 7);");

            return new BytecodeBlock()
                    .append(setPoint)
                    .append(point.ret());
        };

        assertBytecodeNode(nodeGenerator, type(Point.class), new Point(3, 7));
    }
}
