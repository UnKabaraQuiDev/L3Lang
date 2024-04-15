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
	push eax  ; Setup empty pointer arr -> sd_0
	sub esp, 12
stop:  ; breakpoint at: 3:1
	mov dword eax, 1  ; compileComputeExpr(NumLitNode(1))
	mov dword ebx, 0  ; compileComputeExpr(NumLitNode(0))
	imul ebx, 4
	mov ecx, [esp_start - 0]  ; Loading pointer, index = 0
	mov ecx, [ecx]
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=5, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local pointer
	mov dword eax, 2  ; compileComputeExpr(NumLitNode(2))
	mov dword ebx, 1  ; compileComputeExpr(NumLitNode(1))
	imul ebx, 4
	mov ecx, [esp_start - 0]  ; Loading pointer, index = 0
	mov ecx, [ecx]
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=6, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local pointer
	mov dword eax, 3  ; compileComputeExpr(NumLitNode(3))
	mov dword ebx, 2  ; compileComputeExpr(NumLitNode(2))
	imul ebx, 4
	mov ecx, [esp_start - 0]  ; Loading pointer, index = 0
	mov ecx, [ecx]
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=7, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local pointer
stop1:  ; breakpoint at: 9:1
	mov dword ebx, 2  ; compileComputeExpr(NumLitNode(2))
	imul ebx, 4
	mov ecx, [esp_start - 0]  ; Loading pointer
	mov ecx, [ecx]
	add ecx, ebx
	mov eax, [ecx] ; compileLoadVarNum(VarNumNode(arr, pointer=true, arrayOffset=true)): local
	mov dword ebx, 1  ; compileComputeExpr(NumLitNode(1))
	imul ebx, 4
	mov ecx, [esp_start - 0]  ; Loading pointer
	mov ecx, [ecx]
	add ecx, ebx
	mov ebx, [ecx] ; compileLoadVarNum(VarNumNode(arr, pointer=true, arrayOffset=true)): local
	add eax, ebx  ; VarNumNode(arr, pointer=true, arrayOffset=true) + VarNumNode(arr, pointer=true, arrayOffset=true) -> eax
	mov dword ebx, 0  ; compileComputeExpr(NumLitNode(0))
	imul ebx, 4
	mov ecx, [esp_start - 0]  ; Loading pointer
	mov ecx, [ecx]
	add ecx, ebx
	mov ebx, [ecx] ; compileLoadVarNum(VarNumNode(arr, pointer=true, arrayOffset=true)): local
	add eax, ebx  ; BinaryOpNode(+) + VarNumNode(arr, pointer=true, arrayOffset=true) -> eax
	jmp main_cln  ; ReturnNode
main_cln:
	add esp, 16
	ret
section .text
	global _start
	global main
	global stop
	global stop1
section .data
	esp_start dd 0
