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
	sub dword [esp], 16
	sub esp, 12  ; Setup array
	mov eax, 1  ; compileComputeExpr(NumLitNode(1))
	mov ebx, 0  ; compileComputeExpr(NumLitNode(0))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=8, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local
	mov eax, 2  ; compileComputeExpr(NumLitNode(2))
	mov ebx, 1  ; compileComputeExpr(NumLitNode(1))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=9, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local
	mov eax, 3  ; compileComputeExpr(NumLitNode(3))
	mov ebx, 2  ; compileComputeExpr(NumLitNode(2))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=10, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local
stop:  ; breakpoint at: 11:1
	lea eax, [esp + 8]  ; Loading pointer
	push eax
	mov eax, 3  ; compileComputeExpr(NumLitNode(3))
	push eax
	call sd_3  ; double
	add dword esp, 8
	mov eax, eax
	jmp main_cln  ; ReturnNode
main_cln:
	add esp, 16
	ret
sd_3:  ; double
	mov ebx, 0  ; compileComputeExpr(NumLitNode(0))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer
	add ecx, ebx
	mov eax, [ecx] ; compileLoadVarNum(VarNumNode(t, pointer=true, arrayOffset=true)): local
	mov ebx, [esp + 4]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local; STACK_POS = 8
	imul eax, ebx  ; VarNumNode(t, pointer=true, arrayOffset=true) * VarNumNode(x, pointer=false, arrayOffset=false) -> eax
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
