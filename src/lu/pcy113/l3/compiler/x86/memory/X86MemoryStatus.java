package lu.pcy113.l3.compiler.x86.memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lu.pcy113.l3.compiler.memory.MemoryStatus;

public class X86MemoryStatus implements MemoryStatus {

	private final List<String> freeRegisters = new ArrayList<>(Arrays.asList("rax", "rbx", "ecx", "edx"));
	private final Set<String> usedRegisters = new HashSet<>();

	public X86MemoryStatus() {
		freeRegisters.addAll(IntStream.range(0, 16).mapToObj(a -> "r" + a).collect(Collectors.toList()));
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

}
