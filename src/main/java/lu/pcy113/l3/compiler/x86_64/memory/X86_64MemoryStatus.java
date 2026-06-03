package lu.pcy113.l3.compiler.x86_64.memory;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.memory.MemoryStatus;

public class X86_64MemoryStatus implements MemoryStatus {

	private static final Map<String, String[]> registerMap = new HashMap<>();

	static {
		X86_64MemoryStatus.registerMap.put("rax", new String[] { "rax", "eax", "ax", "al" });
		X86_64MemoryStatus.registerMap.put("rbx", new String[] { "rbx", "ebx", "bx", "bl" });
		X86_64MemoryStatus.registerMap.put("rcx", new String[] { "rcx", "ecx", "cx", "cl" });
		X86_64MemoryStatus.registerMap.put("rdx", new String[] { "rdx", "edx", "dx", "dl" });
		X86_64MemoryStatus.registerMap.put("rsi", new String[] { "rsi", "esi", "si", "sil" });
		X86_64MemoryStatus.registerMap.put("rdi", new String[] { "rdi", "edi", "di", "dil" });
		X86_64MemoryStatus.registerMap.put("rsp", new String[] { "rsp", "esp", "sp", "spl" });
		X86_64MemoryStatus.registerMap.put("rbp", new String[] { "rbp", "ebp", "bp", "bpl" });
		for (int i = 8; i < 16; i++) {
			X86_64MemoryStatus.registerMap.put("r" + i,
					new String[] { "r" + i, "r" + i + "d", "r" + i + "w", "r" + i + "b" });
		}
		for (int i = 0; i < 16; i++) {
			X86_64MemoryStatus.registerMap.put("xmm" + i, new String[] { "xmm" + i });
		}
	}

	private final List<String> freeRegisters = new ArrayList<>(Arrays.asList("rax", "rbx", "rcx", "rdx")),
			freeFPRegisters = new ArrayList<>();
	private final Set<String> usedRegisters = new HashSet<>(), usedFPRegisters = new HashSet<>();

	private String latest = null;

	public X86_64MemoryStatus() {
		this.freeRegisters.addAll(IntStream.range(8, 16).mapToObj(a -> "r" + a).collect(Collectors.toList()));
		this.freeFPRegisters.addAll(IntStream.range(0, 16).mapToObj(a -> "xmm" + a).collect(Collectors.toList()));
	}

	@Override
	public String alloc() {
		if (this.freeRegisters.isEmpty()) {
			throw new RuntimeException("No free registers available.");
		}
		final String reg = this.freeRegisters.remove(0);
		this.latest = reg;
		this.usedRegisters.add(reg);
		return reg;
	}

	@Override
	public String allocFP() {
		if (this.freeFPRegisters.isEmpty()) {
			throw new RuntimeException("No free registers available.");
		}
		final String reg = this.freeFPRegisters.remove(0);
		this.latest = reg;
		this.usedFPRegisters.add(reg);
		return reg;
	}

	@Override
	public void free(final String reg) {
		if (this.usedRegisters.remove(reg)) {
			this.freeRegisters.add(0, reg);
			this.latest = reg;
		} else if (this.usedFPRegisters.remove(reg)) {
			this.freeFPRegisters.add(0, reg);
			this.latest = reg;
		} else {
			throw new RuntimeException("Trying to free a register that is not allocated: " + reg);
		}
	}

	@Override
	public boolean hasFree() {
		return !this.freeRegisters.isEmpty();
	}

	@Override
	public boolean hasFreeFP() {
		return !this.freeRegisters.isEmpty();
	}

	@Override
	public boolean isFree(final String reg) {
		return this.freeRegisters.contains(reg) || this.freeFPRegisters.contains(reg);
	}

	@Override
	public boolean alloc(final String reg) {
		if (!this.isFree(reg)) {
			return false;
		} else if (this.freeRegisters.contains(reg)) {
			this.freeRegisters.remove(reg);
			this.usedRegisters.add(reg);
			return true;
		} else if (this.freeFPRegisters.contains(reg)) {
			this.freeFPRegisters.remove(reg);
			this.usedFPRegisters.add(reg);
			return true;
		}
		throw new L3Exception("Unknown register: " + reg);
	}

	@Override
	public String getLatest() {
		return this.latest;
	}

	@Override
	public void setLatest(final String latest) {
		this.latest = latest;
	}

	@Override
	public void freeAll() {
		this.freeRegisters.addAll(this.usedRegisters);
		this.freeFPRegisters.addAll(this.usedFPRegisters);
		this.usedRegisters.clear();
		this.usedFPRegisters.clear();
	}

	@Override
	public String getAsSize(final String reg, final int bytes) {
		if (!X86_64MemoryStatus.registerMap.containsKey(reg)) {
			throw new IllegalArgumentException("Unknown register: " + reg);
		}

		final String[] sizes = X86_64MemoryStatus.registerMap.get(reg);
		return switch (bytes) {
		case 8 -> sizes[0];
		case 4 -> sizes[1];
		case 2 -> sizes[2];
		case 1 -> sizes[3];
		default -> throw new IllegalArgumentException(
				"Invalid size: " + bytes + ". Supported sizes are 1, 2, 4, and 8 bytes.");
		};
	}

	@Override
	public void dump(final PrintStream out) {
		out.println("- - <" + this.getClass().getName() + "> - -");
		out.println("Free: " + this.freeRegisters);
		out.println("Used: " + this.usedRegisters);
		out.println("Free FP: " + this.freeFPRegisters);
		out.println("Used FP: " + this.usedFPRegisters);
		out.println("Latest: " + this.latest);
	}

}