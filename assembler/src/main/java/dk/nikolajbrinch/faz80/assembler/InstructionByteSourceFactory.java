package dk.nikolajbrinch.faz80.assembler;

import dk.nikolajbrinch.faz80.assembler.instructions.Adc;
import dk.nikolajbrinch.faz80.assembler.instructions.Add;
import dk.nikolajbrinch.faz80.assembler.instructions.And;
import dk.nikolajbrinch.faz80.assembler.instructions.Bit;
import dk.nikolajbrinch.faz80.assembler.instructions.Call;
import dk.nikolajbrinch.faz80.assembler.instructions.Ccf;
import dk.nikolajbrinch.faz80.assembler.instructions.Cp;
import dk.nikolajbrinch.faz80.assembler.instructions.Cpd;
import dk.nikolajbrinch.faz80.assembler.instructions.Cpdr;
import dk.nikolajbrinch.faz80.assembler.instructions.Cpi;
import dk.nikolajbrinch.faz80.assembler.instructions.Cpir;
import dk.nikolajbrinch.faz80.assembler.instructions.Cpl;
import dk.nikolajbrinch.faz80.assembler.instructions.Daa;
import dk.nikolajbrinch.faz80.assembler.instructions.Dec;
import dk.nikolajbrinch.faz80.assembler.instructions.Di;
import dk.nikolajbrinch.faz80.assembler.instructions.Djnz;
import dk.nikolajbrinch.faz80.assembler.instructions.Ei;
import dk.nikolajbrinch.faz80.assembler.instructions.Ex;
import dk.nikolajbrinch.faz80.assembler.instructions.Exx;
import dk.nikolajbrinch.faz80.assembler.instructions.Halt;
import dk.nikolajbrinch.faz80.assembler.instructions.IllegalInstructionException;
import dk.nikolajbrinch.faz80.assembler.instructions.Im;
import dk.nikolajbrinch.faz80.assembler.instructions.In;
import dk.nikolajbrinch.faz80.assembler.instructions.Inc;
import dk.nikolajbrinch.faz80.assembler.instructions.Ind;
import dk.nikolajbrinch.faz80.assembler.instructions.Indr;
import dk.nikolajbrinch.faz80.assembler.instructions.Ini;
import dk.nikolajbrinch.faz80.assembler.instructions.Inir;
import dk.nikolajbrinch.faz80.assembler.instructions.InstructionGenerator;
import dk.nikolajbrinch.faz80.assembler.instructions.Jp;
import dk.nikolajbrinch.faz80.assembler.instructions.Jr;
import dk.nikolajbrinch.faz80.assembler.instructions.Ld;
import dk.nikolajbrinch.faz80.assembler.instructions.Ldd;
import dk.nikolajbrinch.faz80.assembler.instructions.Lddr;
import dk.nikolajbrinch.faz80.assembler.instructions.Ldi;
import dk.nikolajbrinch.faz80.assembler.instructions.Ldir;
import dk.nikolajbrinch.faz80.assembler.instructions.Neg;
import dk.nikolajbrinch.faz80.assembler.instructions.Nop;
import dk.nikolajbrinch.faz80.assembler.instructions.Or;
import dk.nikolajbrinch.faz80.assembler.instructions.Otdr;
import dk.nikolajbrinch.faz80.assembler.instructions.Otir;
import dk.nikolajbrinch.faz80.assembler.instructions.Out;
import dk.nikolajbrinch.faz80.assembler.instructions.Outd;
import dk.nikolajbrinch.faz80.assembler.instructions.Outi;
import dk.nikolajbrinch.faz80.assembler.instructions.Pop;
import dk.nikolajbrinch.faz80.assembler.instructions.Push;
import dk.nikolajbrinch.faz80.assembler.instructions.Res;
import dk.nikolajbrinch.faz80.assembler.instructions.Ret;
import dk.nikolajbrinch.faz80.assembler.instructions.Reti;
import dk.nikolajbrinch.faz80.assembler.instructions.Retn;
import dk.nikolajbrinch.faz80.assembler.instructions.Rl;
import dk.nikolajbrinch.faz80.assembler.instructions.Rla;
import dk.nikolajbrinch.faz80.assembler.instructions.Rlc;
import dk.nikolajbrinch.faz80.assembler.instructions.Rlca;
import dk.nikolajbrinch.faz80.assembler.instructions.Rld;
import dk.nikolajbrinch.faz80.assembler.instructions.Rr;
import dk.nikolajbrinch.faz80.assembler.instructions.Rra;
import dk.nikolajbrinch.faz80.assembler.instructions.Rrc;
import dk.nikolajbrinch.faz80.assembler.instructions.Rrca;
import dk.nikolajbrinch.faz80.assembler.instructions.Rrd;
import dk.nikolajbrinch.faz80.assembler.instructions.Rst;
import dk.nikolajbrinch.faz80.assembler.instructions.Sbc;
import dk.nikolajbrinch.faz80.assembler.instructions.Scf;
import dk.nikolajbrinch.faz80.assembler.instructions.Set;
import dk.nikolajbrinch.faz80.assembler.instructions.Sla;
import dk.nikolajbrinch.faz80.assembler.instructions.Sll;
import dk.nikolajbrinch.faz80.assembler.instructions.Sra;
import dk.nikolajbrinch.faz80.assembler.instructions.Srl;
import dk.nikolajbrinch.faz80.assembler.instructions.Sub;
import dk.nikolajbrinch.faz80.assembler.instructions.Xor;
import dk.nikolajbrinch.faz80.assembler.operands.EvaluatedOperand;
import dk.nikolajbrinch.faz80.assembler.operands.IllegalDisplacementException;
import dk.nikolajbrinch.faz80.parser.evaluator.Address;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;
import dk.nikolajbrinch.faz80.scanner.Mnemonic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InstructionByteSourceFactory {

  private final Map<Mnemonic, InstructionGenerator> generators = new HashMap<>();

  public InstructionByteSourceFactory() {
    init();
  }

  private void init() {
    generators.put(Mnemonic.ADC, new Adc());
    generators.put(Mnemonic.ADD, new Add());
    generators.put(Mnemonic.AND, new And());
    generators.put(Mnemonic.BIT, new Bit());
    generators.put(Mnemonic.CALL, new Call());
    generators.put(Mnemonic.CCF, new Ccf());
    generators.put(Mnemonic.CP, new Cp());
    generators.put(Mnemonic.CPD, new Cpd());
    generators.put(Mnemonic.CPDR, new Cpdr());
    generators.put(Mnemonic.CPI, new Cpi());
    generators.put(Mnemonic.CPIR, new Cpir());
    generators.put(Mnemonic.CPL, new Cpl());
    generators.put(Mnemonic.DAA, new Daa());
    generators.put(Mnemonic.DEC, new Dec());
    generators.put(Mnemonic.DI, new Di());
    generators.put(Mnemonic.DJNZ, new Djnz());
    generators.put(Mnemonic.EI, new Ei());
    generators.put(Mnemonic.EX, new Ex());
    generators.put(Mnemonic.EXX, new Exx());
    generators.put(Mnemonic.HALT, new Halt());
    generators.put(Mnemonic.IM, new Im());
    generators.put(Mnemonic.IN, new In());
    generators.put(Mnemonic.INC, new Inc());
    generators.put(Mnemonic.IND, new Ind());
    generators.put(Mnemonic.INDR, new Indr());
    generators.put(Mnemonic.INI, new Ini());
    generators.put(Mnemonic.INIR, new Inir());
    generators.put(Mnemonic.JP, new Jp());
    generators.put(Mnemonic.JR, new Jr());
    generators.put(Mnemonic.LD, new Ld());
    generators.put(Mnemonic.LDD, new Ldd());
    generators.put(Mnemonic.LDDR, new Lddr());
    generators.put(Mnemonic.LDI, new Ldi());
    generators.put(Mnemonic.LDIR, new Ldir());
    generators.put(Mnemonic.NEG, new Neg());
    generators.put(Mnemonic.NOP, new Nop());
    generators.put(Mnemonic.OR, new Or());
    generators.put(Mnemonic.OTDR, new Otdr());
    generators.put(Mnemonic.OTIR, new Otir());
    generators.put(Mnemonic.OUT, new Out());
    generators.put(Mnemonic.OUTD, new Outd());
    generators.put(Mnemonic.OUTI, new Outi());
    generators.put(Mnemonic.POP, new Pop());
    generators.put(Mnemonic.PUSH, new Push());
    generators.put(Mnemonic.RES, new Res());
    generators.put(Mnemonic.RET, new Ret());
    generators.put(Mnemonic.RETI, new Reti());
    generators.put(Mnemonic.RETN, new Retn());
    generators.put(Mnemonic.RL, new Rl());
    generators.put(Mnemonic.RLA, new Rla());
    generators.put(Mnemonic.RLC, new Rlc());
    generators.put(Mnemonic.RLCA, new Rlca());
    generators.put(Mnemonic.RLD, new Rld());
    generators.put(Mnemonic.RR, new Rr());
    generators.put(Mnemonic.RRA, new Rra());
    generators.put(Mnemonic.RRC, new Rrc());
    generators.put(Mnemonic.RRCA, new Rrca());
    generators.put(Mnemonic.RRD, new Rrd());
    generators.put(Mnemonic.RST, new Rst());
    generators.put(Mnemonic.SBC, new Sbc());
    generators.put(Mnemonic.SCF, new Scf());
    generators.put(Mnemonic.SET, new Set());
    generators.put(Mnemonic.SLA, new Sla());
    generators.put(Mnemonic.SLL, new Sll());
    generators.put(Mnemonic.SRA, new Sra());
    generators.put(Mnemonic.SRL, new Srl());
    generators.put(Mnemonic.SUB, new Sub());
    generators.put(Mnemonic.XOR, new Xor());
  }

  public ByteSource generateByteSource(
      AssemblerToken mnemonic, Address currentAddress, List<EvaluatedOperand> operands) {
    InstructionGenerator generator = findGenerator(Mnemonic.find(mnemonic.text()));

    try {
      return generator.generate(currentAddress, operands);
    } catch (IllegalInstructionException e) {
      throw new IllegalInstructionException(
          String.format(
              "Illegal instruction: %s %s",
              mnemonic.text(),
              operands.stream()
                  .map(operand -> String.valueOf(operand))
                  .collect(Collectors.joining(", "))));
    } catch (IllegalDisplacementException e) {
      throw new IllegalInstructionException(
          String.format(
              "Illegal displacement for instruction: %s %s",
              mnemonic.text(),
              operands.stream()
                  .map(operand -> String.valueOf(operand))
                  .collect(Collectors.joining(", "))),
          e);
    } catch (Exception e) {
      throw new IllegalInstructionException(
          String.format(
              "Issue generating instruction: %s %s",
              mnemonic.text(),
              operands.stream()
                  .map(operand -> String.valueOf(operand))
                  .collect(Collectors.joining(", "))));
    }
  }

  private InstructionGenerator findGenerator(Mnemonic mnemonic) {
    return mnemonic != null ? generators.get(mnemonic) : null;
  }
}
