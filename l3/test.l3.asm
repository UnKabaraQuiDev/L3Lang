_start:
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
	; return node (expr compute)
	mov dword eax, 256  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=4, column=9, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=256, value=256]))
	; return node (free)
	add esp, 0  ; Free mem from local scope bc of return
	jmp main_cln  ; ReturnNode
	add esp, 0
main_cln:
	ret
section .text
	global _start
	global main
section .data
	esp_start dd 0
