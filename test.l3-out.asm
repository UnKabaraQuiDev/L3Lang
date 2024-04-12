_start:
	mov eax, 13  ; compileComputeExpr(NumLitNode(13))
	mov [sd_0], eax  ; Setting: b
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
	sub esp, 12  ; Setup array
	mov eax, esp  ; Setup array pointer
	add eax, 1
	push eax  ; Push var: arr
	mov eax, 1  ; compileComputeExpr(NumLitNode(1))
	mov ebx, 0  ; compileComputeExpr(NumLitNode(0))
	mov ecx, [esp + 8]  ; Loading pointer
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=5, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local
	mov eax, 2  ; compileComputeExpr(NumLitNode(2))
	mov ebx, 1  ; compileComputeExpr(NumLitNode(1))
	mov ecx, [esp + 8]  ; Loading pointer
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=6, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local
	mov eax, 3  ; compileComputeExpr(NumLitNode(3))
	mov ebx, 2  ; compileComputeExpr(NumLitNode(2))
	mov ecx, [esp + 8]  ; Loading pointer
	add ecx, ebx
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(lu.pcy113.l3.lexer.tokens.IdentifierToken[line=7, column=1, type=lu.pcy113.l3.lexer.TokenType[IDENT, fixed=false, string=false], identifier=arr])): local
	mov ebx, 1  ; compileComputeExpr(NumLitNode(1))
	mov eax, [esp + 8 + ebx]  ; compileLetTypeSet(VarNumNode(arr, pointer=true, arrayOffset=true)): local
	jmp main_cln  ; ReturnNode
main_cln:
	add esp, 16
	ret
section .text
	global _start
	global main
section .data
	sd_0 dd 0  ; b
