package lu.pcy113.l3.compiler.x86.memory;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.StructDefNode;

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
		for (int i = 0; i < 16; i++) {
			registerMap.put("xmm" + i, new String[] { "xmm" + i });
		}
	}

	private final List<String> freeRegisters = new ArrayList<>(Arrays.asList("rax", "rbx", "rcx", "rdx")), freeFPRegisters = new ArrayList<String>();
	private final Set<String> usedRegisters = new HashSet<>(), usedFPRegisters = new HashSet<>();

	private String latest = null;

	public X86MemoryStatus() {
		freeRegisters.addAll(IntStream.range(8, 16).mapToObj(a -> "r" + a).collect(Collectors.toList()));
		freeFPRegisters.addAll(IntStream.range(0, 16).mapToObj(a -> "xmm" + a).collect(Collectors.toList()));
	}

	@Override
	public String alloc() {
		if (freeRegisters.isEmpty()) {
			throw new RuntimeException("No free registers available.");
		}
		String reg = freeRegisters.remove(0);
		latest = reg;
		usedRegisters.add(reg);
		return reg;
	}

	@Override
	public String allocFP() {
		if (freeFPRegisters.isEmpty()) {
			throw new RuntimeException("No free registers available.");
		}
		String reg = freeFPRegisters.remove(0);
		latest = reg;
		usedFPRegisters.add(reg);
		return reg;
	}

	@Override
	public void free(String reg) {
		if (usedRegisters.remove(reg)) {
			freeRegisters.add(0, reg);
			latest = reg;
		} else if (usedFPRegisters.remove(reg)) {
			freeFPRegisters.add(0, reg);
			latest = reg;
		} else {
			throw new RuntimeException("Trying to free a register that is not allocated: " + reg);
		}
	}

	@Override
	public boolean hasFree() {
		return !freeRegisters.isEmpty();
	}

	@Override
	public boolean hasFreeFP() {
		return !freeRegisters.isEmpty();
	}

	@Override
	public boolean isFree(String reg) {
		return freeRegisters.contains(reg) || freeFPRegisters.contains(reg);
	}

	@Override
	public boolean alloc(String reg) throws CompilerException {
		if (!isFree(reg)) {
			return false;
		} else if (freeRegisters.contains(reg)) {
			freeRegisters.remove(reg);
			usedRegisters.add(reg);
			return true;
		} else if (freeFPRegisters.contains(reg)) {
			freeFPRegisters.remove(reg);
			usedFPRegisters.add(reg);
			return true;
		}
		throw new CompilerException("Unknown register: " + reg);
	}

	@Override
	public String getLatest() {
		return latest;
	}

	@Override
	public void setLatest(String latest) {
		this.latest = latest;
	}

	@Override
	public void freeAll() {
		freeRegisters.addAll(usedRegisters);
		freeFPRegisters.addAll(usedFPRegisters);
		usedRegisters.clear();
		usedFPRegisters.clear();
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

	private Stack<Node> stack = new Stack<>();
	private int currentStackOffset = 0;

	@Override
	public void pushStack(Node node) throws CompilerException {
		setStackOffset(node, currentStackOffset);
		stack.push(node);
		currentStackOffset += getStackSize(node);
	}

	private void setStackOffset(Node node, int offset) throws CompilerException {
		if (node instanceof LetDefNode) {
			node.getClosestContainer().getLetDefDescriptor((LetDefNode) node).setStackOffset(offset);
		} else {
			throw new CompilerException("Can't set byte offset of node: " + node + ".");
		}
	}

	private int getStackSize(Node node) throws CompilerException {
		if (node instanceof LetDefNode) {
			return ((LetDefNode) node).getType().getBytesSize();
		} else {
			throw new CompilerException("Can't get byte size of node: " + node + ".");
		}
	}

	@Override
	public Node popStack() throws CompilerException {
		currentStackOffset -= getStackSize(stack.peek());
		return stack.pop();
	}

	@Override
	public int getCurrentStackOffset() {
		return currentStackOffset;
	}

	@Override
	public void clearStack() {
		stack.clear();
		currentStackOffset = 0;
	}

	@Override
	public void dump(PrintStream out) {
		out.println("- - <" + this.getClass().getName() + "> - -");
		out.println("Free: " + freeRegisters);
		out.println("Used: " + usedRegisters);
		out.println("Free FP: " + freeFPRegisters);
		out.println("Used FP: " + usedFPRegisters);
		out.println("Latest: " + latest);
	}

}
