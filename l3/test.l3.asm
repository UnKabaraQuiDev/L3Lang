_start:
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
	sub esp, 1  ; Alloc for: size: 1 + 0, LetTypeDef: LetTypeDefNode(bb, TypeNode(generic=true, type=INT_8, pointer=false), index=0, size=4)
	mov byte eax, 2  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=4, column=15, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=2, value=2]))
	mov byte [esp+0], eax  ; Push var: bb
	; return node (expr compute)
	mov byte eax, [esp + 1]  ; compileLoadVarNum(VarNumNode(bb, pointer=false, arrayOffset=false)): local
	; return node (free)
	add esp, 1  ; Free mem from local scope bc of return
	jmp main_cln  ; ReturnNode
	add esp, 1
main_cln:
	ret
section .text
	global _start
	global main
section .data
	esp_start dd 0
