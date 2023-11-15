package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ByteFunction extends BiFunction<Operand, Operand, ByteSource> {

}
