_start:
	mov eax, 13  ; compileComputeExpr(NumLitNode(13))
	mov [sd_0], eax  ; Setting: b
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
	push esp   ; Setup array pointer
	sub dword [esp], 24
	sub esp, 20  ; Setup array
	mov eax, 116  ; compileComputeExpr(NumLitNode(116))
	mov [esp + 0], eax
	mov eax, 101  ; compileComputeExpr(NumLitNode(101))
	mov [esp + 4], eax
	mov eax, 115  ; compileComputeExpr(NumLitNode(115))
	mov [esp + 8], eax
	mov eax, 116  ; compileComputeExpr(NumLitNode(116))
	mov [esp + 12], eax
	mov eax, 0  ; compileComputeExpr(NumLitNode(0))
	mov [esp + 16], eax
	; Exit program
	mov ebx, 2  ; compileComputeExpr(NumLitNode(2))
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
	push esp   ; Setup array pointer
	sub dword [esp], 16
	sub esp, 12  ; Setup array
stop:  ; breakpoint at: 12:1
	mov eax, 2  ; compileComputeExpr(NumLitNode(2))
	mov ebx, 0  ; compileComputeExpr(NumLitNode(0))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer, stack = 40, index = 3
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=13, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local pointer
	mov eax, 2  ; compileComputeExpr(NumLitNode(2))
	mov ebx, 1  ; compileComputeExpr(NumLitNode(1))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer, stack = 40, index = 3
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=14, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local pointer
	mov eax, 3  ; compileComputeExpr(NumLitNode(3))
	mov ebx, 2  ; compileComputeExpr(NumLitNode(2))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer, stack = 40, index = 3
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=15, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local pointer
	mov eax, 69  ; compileComputeExpr(NumLitNode(69))
	push eax  ; Push var: x
	mov eax, [esp + 36] ; compileLoadVarNum(VarNumNode(arr, pointer=false, arrayOffset=false)): local
	push eax
	mov eax, 3  ; compileComputeExpr(NumLitNode(3))
	push eax
	call sd_3  ; double
	add dword esp, 8
	mov eax, eax
	jmp main_cln  ; ReturnNode
main_cln:
	add esp, 44
	ret
sd_3:  ; double
	mov eax, [esp + 16] ; compileLoadVarNum(VarNumNode(t, pointer=true, arrayOffset=false)): local
	mov ebx, [esp + 4]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local; STACK_POS = 8
	imul eax, ebx  ; VarNumNode(t, pointer=true, arrayOffset=false) * VarNumNode(x, pointer=false, arrayOffset=false) -> eax
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
