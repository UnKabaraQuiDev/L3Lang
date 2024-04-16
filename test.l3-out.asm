_start:
	mov eax, esp
	sub eax, 8  ; Add offset for main fun call
	mov [esp_start], eax
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
	mov eax, esp
	sub eax, 16
	push eax  ; Setup empty pointer arr -> sd_2
	sub esp, 12
stop:  ; breakpoint at: 8:1
	mov dword eax, 1  ; compileComputeExpr(NumLitNode(1))
	mov dword ebx, 0  ; compileComputeExpr(NumLitNode(0))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer, index = 0, size = 16
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=10, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local pointer
	mov dword eax, 2  ; compileComputeExpr(NumLitNode(2))
	mov dword ebx, 1  ; compileComputeExpr(NumLitNode(1))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer, index = 0, size = 16
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=11, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local pointer
	mov dword eax, 3  ; compileComputeExpr(NumLitNode(3))
	mov dword ebx, 2  ; compileComputeExpr(NumLitNode(2))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer, index = 0, size = 16
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=12, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local pointer
stop1:  ; breakpoint at: 14:1
	mov dword ebx, 1  ; compileComputeExpr(NumLitNode(1))
	imul ebx, 4
	mov ecx, [esp + 12]  ; Loading pointer, index = 0, size = 16
	add ecx, ebx
	mov eax, [ecx] ; compileLoadVarNum(VarNumNode(arr, pointer=true, arrayOffset=true)): local
	push eax
	call sd_1  ; test
	add dword esp, 0  ; Free mem from fun call
	mov ebx, eax
	push ebx
	pop ebx
	pop eax
	imul eax, ebx  ; VarNumNode(arr, pointer=true, arrayOffset=true) * FunCallNode(test, def) -> eax
	jmp main_cln  ; ReturnNode
main_cln:
	add esp, 16
	ret
sd_1:  ; test
	mov dword eax, 10  ; compileComputeExpr(NumLitNode(10))
	push dword eax  ; Push var: te
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(te, pointer=false, arrayOffset=false)): local
	jmp sd_1_cln  ; ReturnNode
sd_1_cln:
	add esp, 4
	ret
section .text
	global _start
	global main
	global stop
	global stop1
section .data
	esp_start dd 0
