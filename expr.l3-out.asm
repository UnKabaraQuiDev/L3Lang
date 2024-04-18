_start:
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
	mov dword eax, 2  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=11, column=13, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=2, value=2]))
	push dword eax  ; Push var: x
	mov dword eax, 3  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=12, column=13, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=3, value=3]))
	push dword eax  ; Push var: y
stop:  ; breakpoint at: 13:2
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	push eax
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(y, pointer=false, arrayOffset=false)): local
	push eax
	call sd_4  ; tt
	add dword esp, 0  ; Free mem from fun call
	mov eax, eax
	push eax
	call sd_3  ; func
	add dword esp, 12  ; Free mem from fun call
	mov eax, eax
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 8  ; Free mem from local scope bc of return
	jmp main_cln  ; ReturnNode
	add esp, 8
main_cln:
	ret
sd_3:  ; func
func:  ; breakpoint at: 2:2
	mov eax, [esp + 12]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	push eax
	mov ebx, [esp + 12]  ; compileLoadVarNum(VarNumNode(y, pointer=false, arrayOffset=false)): local
	push ebx
	pop eax
	pop ebx
	add eax, ebx  ; VarNumNode(x, pointer=false, arrayOffset=false) lu.pcy113.l3.lexer.TokenType[PLUS, fixed=true, string=false, charValue=+] VarNumNode(y, pointer=false, arrayOffset=false) -> eax
	push eax
	mov ebx, [esp + 8]  ; compileLoadVarNum(VarNumNode(z, pointer=false, arrayOffset=false)): local
	push ebx
	pop eax
	pop ebx
	add eax, ebx  ; BinaryOpNode(+) lu.pcy113.l3.lexer.TokenType[PLUS, fixed=true, string=false, charValue=+] VarNumNode(z, pointer=false, arrayOffset=false) -> eax
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_3_cln  ; ReturnNode
	add esp, 0
sd_3_cln:
	ret
sd_4:  ; tt
	mov dword eax, 3  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=7, column=12, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=3, value=3]))
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_4_cln  ; ReturnNode
	add esp, 0
sd_4_cln:
	ret
section .text
	global _start
	global main
	global stop
	global func
section .data
	esp_start dd 0
