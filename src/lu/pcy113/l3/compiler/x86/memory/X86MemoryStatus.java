package lu.pcy113.l3.compiler.x86.memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lu.pcy113.l3.compiler.memory.MemoryStatus;

public class X86MemoryStatus implements MemoryStatus {

	private static final Map<String, String[]> registerMap = new HashMap<>();

	static {
		registerMap.put("rax", new String[] { "rax", "eax", "ax", "al" });
		registerMap.put("rbx", new String[] { "rbx", "ebx", "bx", "bl" });
		registerMap.put("rcx", new String[] { "rcx", "ecx", "cx", "cl" });
		registerMap.put("rdx", new String[] { "rdx", "edx", "dx", "dl" });
		registerMap.put("rsi", new String[] { "rsi", "esi", "si", "sil" });
		registerMap.put("rdi", new String[] { "rdi", "edi", "di", "dil" });
		registerMap.put("rsp", new String[] { "rsp", "esp", "sp", "spl" });
		registerMap.put("rbp", new String[] { "rbp", "ebp", "bp", "bpl" });
		for (int i = 8; i < 16; i++) {
			registerMap.put("r" + i, new String[] { "r" + i, "r" + i + "d", "r" + i + "w", "r" + i + "b" });
		}
	}

	private final List<String> freeRegisters = new ArrayList<>(Arrays.asList("rax", "rbx", "ecx", "edx"));
	private final Set<String> usedRegisters = new HashSet<>();

	public X86MemoryStatus() {
		freeRegisters.addAll(IntStream.range(8, 16).mapToObj(a -> "r" + a).collect(Collectors.toList()));
	}

	@Override
	public String alloc() {
		if (freeRegisters.isEmpty()) {
			throw new RuntimeException("No free registers available.");
		}
		String reg = freeRegisters.remove(0);
		usedRegisters.add(reg);
		return reg;
	}

	@Override
	public void free(String reg) {
		if (usedRegisters.remove(reg)) {
			freeRegisters.add(0, reg);
		} else {
			throw new RuntimeException("Trying to free a register that is not allocated: " + reg);
		}
	}

	@Override
	public boolean hasFree() {
		return !freeRegisters.isEmpty();
	}

	@Override
	public boolean isFree(String reg) {
		return freeRegisters.contains(reg);
	}

	@Override
	public boolean alloc(String reg) {
		if (!isFree(reg)) {
			return false;
		} else {
			freeRegisters.remove(reg);
			usedRegisters.add(reg);
			return true;
		}
	}

	@Override
	public void freeAll() {
		freeRegisters.addAll(usedRegisters);
		usedRegisters.clear();
	}

	@Override
	public String getAsSize(String reg, int bytes) {
		if (!registerMap.containsKey(reg)) {
			throw new IllegalArgumentException("Unknown register: " + reg);
		}

		String[] sizes = registerMap.get(reg);
		switch (bytes) {
		case 8:
			return sizes[0];
		case 4:
			return sizes[1];
		case 2:
			return sizes[2];
		case 1:
			return sizes[3];
		default:
			throw new IllegalArgumentException("Invalid size: " + bytes + ". Supported sizes are 1, 2, 4, and 8 bytes.");
		}
	}

}
