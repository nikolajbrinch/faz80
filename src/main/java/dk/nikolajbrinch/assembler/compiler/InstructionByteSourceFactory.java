package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.instructions.Adc;
import dk.nikolajbrinch.assembler.compiler.instructions.Add;
import dk.nikolajbrinch.assembler.compiler.instructions.And;
import dk.nikolajbrinch.assembler.compiler.instructions.Bit;
import dk.nikolajbrinch.assembler.compiler.instructions.Call;
import dk.nikolajbrinch.assembler.compiler.instructions.Ccf;
import dk.nikolajbrinch.assembler.compiler.instructions.Cp;
import dk.nikolajbrinch.assembler.compiler.instructions.Cpd;
import dk.nikolajbrinch.assembler.compiler.instructions.Cpdr;
import dk.nikolajbrinch.assembler.compiler.instructions.Cpi;
import dk.nikolajbrinch.assembler.compiler.instructions.Cpir;
import dk.nikolajbrinch.assembler.compiler.instructions.Cpl;
import dk.nikolajbrinch.assembler.compiler.instructions.Daa;
import dk.nikolajbrinch.assembler.compiler.instructions.Dec;
import dk.nikolajbrinch.assembler.compiler.instructions.Di;
import dk.nikolajbrinch.assembler.compiler.instructions.Djnz;
import dk.nikolajbrinch.assembler.compiler.instructions.Ei;
import dk.nikolajbrinch.assembler.compiler.instructions.Ex;
import dk.nikolajbrinch.assembler.compiler.instructions.Exx;
import dk.nikolajbrinch.assembler.compiler.instructions.Halt;
import dk.nikolajbrinch.assembler.compiler.instructions.Im;
import dk.nikolajbrinch.assembler.compiler.instructions.In;
import dk.nikolajbrinch.assembler.compiler.instructions.Inc;
import dk.nikolajbrinch.assembler.compiler.instructions.Ind;
import dk.nikolajbrinch.assembler.compiler.instructions.Indr;
import dk.nikolajbrinch.assembler.compiler.instructions.Ini;
import dk.nikolajbrinch.assembler.compiler.instructions.Inir;
import dk.nikolajbrinch.assembler.compiler.instructions.InstructionGenerator;
import dk.nikolajbrinch.assembler.compiler.instructions.Jp;
import dk.nikolajbrinch.assembler.compiler.instructions.Jr;
import dk.nikolajbrinch.assembler.compiler.instructions.Ld;
import dk.nikolajbrinch.assembler.compiler.instructions.Ldd;
import dk.nikolajbrinch.assembler.compiler.instructions.Lddr;
import dk.nikolajbrinch.assembler.compiler.instructions.Ldi;
import dk.nikolajbrinch.assembler.compiler.instructions.Ldir;
import dk.nikolajbrinch.assembler.compiler.instructions.Neg;
import dk.nikolajbrinch.assembler.compiler.instructions.Nop;
import dk.nikolajbrinch.assembler.compiler.instructions.Or;
import dk.nikolajbrinch.assembler.compiler.instructions.Otdr;
import dk.nikolajbrinch.assembler.compiler.instructions.Otir;
import dk.nikolajbrinch.assembler.compiler.instructions.Out;
import dk.nikolajbrinch.assembler.compiler.instructions.Outd;
import dk.nikolajbrinch.assembler.compiler.instructions.Outi;
import dk.nikolajbrinch.assembler.compiler.instructions.Pop;
import dk.nikolajbrinch.assembler.compiler.instructions.Push;
import dk.nikolajbrinch.assembler.compiler.instructions.Res;
import dk.nikolajbrinch.assembler.compiler.instructions.Ret;
import dk.nikolajbrinch.assembler.compiler.instructions.Reti;
import dk.nikolajbrinch.assembler.compiler.instructions.Retn;
import dk.nikolajbrinch.assembler.compiler.instructions.Rl;
import dk.nikolajbrinch.assembler.compiler.instructions.Rla;
import dk.nikolajbrinch.assembler.compiler.instructions.Rlc;
import dk.nikolajbrinch.assembler.compiler.instructions.Rlca;
import dk.nikolajbrinch.assembler.compiler.instructions.Rld;
import dk.nikolajbrinch.assembler.compiler.instructions.Rr;
import dk.nikolajbrinch.assembler.compiler.instructions.Rra;
import dk.nikolajbrinch.assembler.compiler.instructions.Rrc;
import dk.nikolajbrinch.assembler.compiler.instructions.Rrca;
import dk.nikolajbrinch.assembler.compiler.instructions.Rrd;
import dk.nikolajbrinch.assembler.compiler.instructions.Rst;
import dk.nikolajbrinch.assembler.compiler.instructions.Sbc;
import dk.nikolajbrinch.assembler.compiler.instructions.Scf;
import dk.nikolajbrinch.assembler.compiler.instructions.Set;
import dk.nikolajbrinch.assembler.compiler.instructions.Sla;
import dk.nikolajbrinch.assembler.compiler.instructions.Sll;
import dk.nikolajbrinch.assembler.compiler.instructions.Sra;
import dk.nikolajbrinch.assembler.compiler.instructions.Srl;
import dk.nikolajbrinch.assembler.compiler.instructions.Sub;
import dk.nikolajbrinch.assembler.compiler.instructions.Xor;
import dk.nikolajbrinch.assembler.compiler.operands.Operand;
import dk.nikolajbrinch.assembler.scanner.AssemblerToken;
import dk.nikolajbrinch.assembler.scanner.Mnemonic;
import java.util.HashMap;
import java.util.Map;

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
      AssemblerToken mnemonic, Address currentAddress, Operand operand1, Operand operand2) {
    InstructionGenerator generator = findGenerator(Mnemonic.find(mnemonic.text()));

    return generator.generate(currentAddress, operand1, operand2);
  }

  private InstructionGenerator findGenerator(Mnemonic mnemonic) {
    return mnemonic != null ? generators.get(mnemonic) : null;
  }
}
