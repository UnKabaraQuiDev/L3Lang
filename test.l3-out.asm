_start:
	mov eax, 13  ; compileComputeExpr(NumLitNode(13))
	mov [sd_0], eax  ; Setting: b
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
stop:  ; breakpoint at: 7:1
	mov eax, 2  ; compileComputeExpr(NumLitNode(2))
	push eax  ; Push var: x
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local; STACK_POS = 4
	push eax
	mov eax, 3  ; compileComputeExpr(NumLitNode(3))
	push eax
	call sd_3  ; double
	add dword esp, 8
	mov eax, eax
	jmp main_cln  ; ReturnNode
main_cln:
	add esp, 4
	ret
sd_3:  ; double
	mov eax, [esp + 8]  ; compileLoadVarNum(VarNumNode(t, pointer=false, arrayOffset=false)): local; STACK_POS = 8
	mov ebx, [esp + 4]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local; STACK_POS = 8
	imul eax, ebx  ; VarNumNode(t, pointer=false, arrayOffset=false) * VarNumNode(x, pointer=false, arrayOffset=false) -> eax
	jmp sd_3_cln  ; ReturnNode
sd_3_cln:
	add esp, 0
	ret
section .text
	global _start
	global main
	global stop
section .data
	sd_0 dd 0  ; b
