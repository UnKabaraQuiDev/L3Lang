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
	sub eax, 84
	push eax  ; Setup array pointer
	sub esp, 80
	sub esp, 12
	mov dword [esp + 8], sd_11  ; From
	mov dword [esp + 4], eax  ; To
	mov dword [esp + 0], 20  ; Length
	call sd_5
	add esp, 12
	mov dword eax, 5  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=60, column=14, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=5, value=5]))
	push dword eax  ; Push var: x
stop:  ; breakpoint at: 62:2
_sec_1:  ; While at: 64:2
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	push eax
	mov dword ebx, 5  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=64, column=23, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=5, value=5]))
	push ebx
	pop ebx
	pop eax
	cmp eax, ebx
	sete al
	mov ebx, eax
	push ebx
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	push eax
	mov dword ebx, 4  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=64, column=13, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=4, value=4]))
	push ebx
	pop ebx
	pop eax
	cmp eax, ebx
	sete al
	push eax
	pop eax
	pop ebx
	add eax, ebx  ; ComparisonOpNode(==) lu.pcy113.l3.lexer.TokenType[OR, fixed=true, string=true, stringValue=||] ComparisonOpNode(==) -> eax
	push eax
	mov dword ebx, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.Token[64:28, type=lu.pcy113.l3.lexer.TokenType[FALSE, fixed=true, string=true, stringValue=false]]))
	push ebx
	pop eax
	pop ebx
	add eax, ebx  ; LogicalOpNode(||) lu.pcy113.l3.lexer.TokenType[OR, fixed=true, string=true, stringValue=||] NumLitNode(lu.pcy113.l3.lexer.tokens.Token[64:28, type=lu.pcy113.l3.lexer.TokenType[FALSE, fixed=true, string=true, stringValue=false]]) -> eax
	cmp eax, 0
	je _sec_1_end
stop1:  ; breakpoint at: 65:3
	mov eax, [esp + 84] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_1  ; println
	add dword esp, 4  ; Free mem from fun call
stop2:  ; breakpoint at: 67:3
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	push eax
	mov dword ebx, 1  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=68, column=11, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=1, value=1]))
	push ebx
	pop ebx
	pop eax
	sub eax, ebx  ; VarNumNode(x, pointer=false, arrayOffset=false) lu.pcy113.l3.lexer.TokenType[MINUS, fixed=true, string=false, charValue=-] NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=68, column=11, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=1, value=1]) -> eax
	push eax
	lea dword ecx, [esp + 4] ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	pop eax
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(VarNumNode(x, pointer=false, arrayOffset=false)))
	jmp _sec_1
_sec_1_cln:
	add esp, 0  ; Free mem
_sec_1_end:
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.Token[71:6, type=lu.pcy113.l3.lexer.TokenType[FALSE, fixed=true, string=true, stringValue=false]]))
	push eax
	lea dword ecx, [esp + 4] ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	pop eax
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(VarNumNode(x, pointer=false, arrayOffset=false)))
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	jmp main_cln  ; ReturnNode
main_cln:
	add esp, 88
	ret
sd_1:  ; println
	mov ecx, [esp + 4]  ; Copy the address of the string into eax
.loop:
	cmp dword [ecx], 0  ; Compare the byte at the current address with null terminator
	je .done  ; If null terminator is found, exit loop
	; Print write
	mov eax, 4  ; Write
	mov ebx, 1  ; Stdout
	mov edx, 1  ; Length
	int 0x80  ; Syscall
	add dword ecx, 4  ; Move to the next byte in the string
	jmp .loop  ; Repeat the loop
.done:
	sub esp, 4
	mov ecx, esp
	mov dword [ecx], 10  ; Newline
	; Print write
	mov eax, 4  ; Write
	mov ebx, 1  ; Stdout
	mov edx, 1  ; Length
	int 0x80  ; Syscall
	add esp, 4
	jmp sd_1_cln  ; ReturnNode
sd_1_cln:
	add esp, 0
	ret
sd_5:  ; memcpy
	mov ecx, [esp + 4]  ; Length
	mov edi, [esp + 8]  ; To
	mov esi, [esp + 12]  ; From
.copy_loop:
	mov eax, [esi]
	mov [edi], eax
	add esi, 4
	add edi, 4
	loop .copy_loop
	jmp sd_5_cln  ; ReturnNode
sd_5_cln:
	add esp, 0
	ret
sd_8:  ; strlen
	mov eax, [esp + 4]  ; Copy the address of the string into eax
	xor ecx, ecx  ; Clear ecx (counter register)
.loop:
	cmp dword [eax], 0  ; Compare the byte at the current address with null terminator
	je .done  ; If null terminator is found, exit loop
	add dword eax, 4  ; Move to the next byte in the string
	inc ecx  ; Increment the counter
	jmp .loop  ; Repeat the loop
.done:
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=45, column=19, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	push dword eax  ; Push var: length
	mov [esp], ecx  ; Move strlen to var
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(length, pointer=false, arrayOffset=false)): local
	jmp sd_8_cln  ; ReturnNode
sd_8_cln:
	add esp, 4
	ret
sd_10:  ; test
	mov dword eax, 10  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=53, column=14, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=10, value=10]))
	push dword eax  ; Push var: te
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(te, pointer=false, arrayOffset=false)): local
	jmp sd_10_cln  ; ReturnNode
sd_10_cln:
	add esp, 4
	ret
section .text
	global _start
	global main
	global stop
	global stop1
	global stop2
section .data
	esp_start dd 0
	sd_11 dd 112, 114, 105, 110, 116, 108, 110, 32, 116, 104, 105, 115, 32, 115, 116, 114, 105, 110, 103, 0  ; str at 58:11
