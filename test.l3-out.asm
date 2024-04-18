_start:
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
	mov dword eax, 5  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=98, column=14, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=5, value=5]))
	push dword eax  ; Push var: a
sec_1:  ; If container at: 102:2
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=102, column=5, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	cmp eax, 0
	jne .sec_1_0
	mov dword eax, 1  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=104, column=12, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=1, value=1]))
	cmp eax, 0
	jne .sec_1_1
	jmp .sec_1_end  ; Jump to end if Else is not present
.sec_1_0:  ; If node at: 102:2
	mov eax, esp
	sub eax, 20
	push eax  ; Setup array pointer
	sub esp, 16
	sub esp, 12
	mov dword [esp + 8], var_1  ; From
	mov dword [esp + 4], eax  ; To
	mov dword [esp + 0], 4  ; Length
	call sd_16
	add esp, 12
	mov eax, [esp + 16]  ; Loading StringLitNode pointer
	push eax
	call sd_3  ; println
	add dword esp, 24  ; Free mem from fun call
.sec_1_0_cln:
	add esp, 0  ; Free mem
	jmp .sec_1_finally  ; Jump to final
.sec_1_1:  ; If node at: 104:9
	mov eax, esp
	sub eax, 20
	push eax  ; Setup array pointer
	sub esp, 16
	sub esp, 12
	mov dword [esp + 8], var_2  ; From
	mov dword [esp + 4], eax  ; To
	mov dword [esp + 0], 4  ; Length
	call sd_16
	add esp, 12
	mov eax, [esp + 16]  ; Loading StringLitNode pointer
	push eax
	call sd_3  ; println
	add dword esp, 24  ; Free mem from fun call
.sec_1_1_cln:
	add esp, 0  ; Free mem
	jmp .sec_1_finally  ; Jump to final
.sec_1_finally:  ; Finally node at: 106:4
	mov eax, esp
	sub eax, 104
	push eax  ; Setup array pointer
	sub esp, 100
	sub esp, 12
	mov dword [esp + 8], var_3  ; From
	mov dword [esp + 4], eax  ; To
	mov dword [esp + 0], 25  ; Length
	call sd_16
	add esp, 12
	mov eax, [esp + 100]  ; Loading StringLitNode pointer
	push eax
	call sd_3  ; println
	add dword esp, 108  ; Free mem from fun call
.sec_1_finally_cln:
	add esp, 0  ; Free mem
.sec_1_end:
sec_2:  ; While at: 110:2
	mov dword ebx, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=110, column=12, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	push ebx
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(a, pointer=false, arrayOffset=false)): local
	push eax
	pop eax
	pop ebx
	cmp eax, ebx
	setg al
	cmp eax, 0
	je .sec_2_else
.sec_2:  ; While condition
	mov dword ebx, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=110, column=12, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	push ebx
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(a, pointer=false, arrayOffset=false)): local
	push eax
	pop eax
	pop ebx
	cmp eax, ebx
	setg al
	cmp eax, 0
	je .sec_2_finally
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(a, pointer=false, arrayOffset=false)): local
	push eax
	call sd_7  ; print
	add dword esp, 4  ; Free mem from fun call
	mov dword ebx, 1  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=112, column=10, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=1, value=1]))
	push ebx
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(a, pointer=false, arrayOffset=false)): local
	push eax
	pop eax
	pop ebx
	sub eax, ebx  ; VarNumNode(a, pointer=false, arrayOffset=false) lu.pcy113.l3.lexer.TokenType[MINUS, fixed=true, string=false, charValue=-] NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=112, column=10, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=1, value=1]) -> eax
	push eax
	lea dword ecx, [esp + 4] ; compileLoadComputeExpr(VarNumNode(a, pointer=false, arrayOffset=false)): local
	pop eax
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(VarNumNode(a, pointer=false, arrayOffset=false)))
.sec_2_cln:
	add esp, 0  ; Free mem
	jmp .sec_2
.sec_2_else:
	mov eax, esp
	sub eax, 24
	push eax  ; Setup array pointer
	sub esp, 20
	sub esp, 12
	mov dword [esp + 8], var_4  ; From
	mov dword [esp + 4], eax  ; To
	mov dword [esp + 0], 5  ; Length
	call sd_16
	add esp, 12
	mov eax, [esp + 20]  ; Loading StringLitNode pointer
	push eax
	call sd_3  ; println
	add dword esp, 28  ; Free mem from fun call
jmp .sec_2_end  ; After else
.sec_2_finally:
	call sd_4  ; println
	add dword esp, 0  ; Free mem from fun call
jmp .sec_2_end  ; After finally
.sec_2_end:
sec_3:  ; For at: 119:2
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=119, column=18, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	push dword eax  ; Push var: x
.sec_3:
	mov dword ebx, 10  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=119, column=25, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=10, value=10]))
	push ebx
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	push eax
	pop eax
	pop ebx
	cmp eax, ebx
	setl al
	cmp eax, 0
	je .sec_3_end
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	push eax
	call sd_7  ; print
	add dword esp, 4  ; Free mem from fun call
	mov dword ebx, 1  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=119, column=35, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=1, value=1]))
	push ebx
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(x, pointer=false, arrayOffset=false)): local
	push eax
	pop eax
	pop ebx
	add eax, ebx  ; VarNumNode(x, pointer=false, arrayOffset=false) lu.pcy113.l3.lexer.TokenType[PLUS, fixed=true, string=false, charValue=+] NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=119, column=35, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=1, value=1]) -> eax
	push eax
	lea dword ecx, [esp + 4] ; compileLoadComputeExpr(VarNumNode(x, pointer=false, arrayOffset=false)): local
	pop eax
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(VarNumNode(x, pointer=false, arrayOffset=false)))
	jmp .sec_3
.sec_3_cln:
	add esp, 0  ; Free mem
.sec_3_end:
	call sd_4  ; println
	add dword esp, 0  ; Free mem from fun call
	mov dword eax, 3  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=125, column=12, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=3, value=3]))
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 8  ; Free mem from local scope bc of return
	jmp main_cln  ; ReturnNode
	add esp, 8
main_cln:
	ret
sd_1:  ; print
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
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_1_cln  ; ReturnNode
	add esp, 0
sd_1_cln:
	ret
sd_3:  ; println
	mov eax, [esp + 4] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_1  ; print
	add dword esp, 4  ; Free mem from fun call
	call sd_4  ; println
	add dword esp, 0  ; Free mem from fun call
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_3_cln  ; ReturnNode
	add esp, 0
sd_3_cln:
	ret
sd_4:  ; println
	sub esp, 4
	mov ecx, esp
	mov dword [ecx], 10  ; Newline
	; Print write
	mov eax, 4  ; Write
	mov ebx, 1  ; Stdout
	mov edx, 1  ; Length
	int 0x80  ; Syscall
	add esp, 4
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_4_cln  ; ReturnNode
	add esp, 0
sd_4_cln:
	ret
sd_7:  ; print
	lea eax, [esp]
	sub eax, 48
	push eax  ; Setup empty pointer str -> sd_6
	sub esp, 44
	mov eax, [esp + 52]  ; compileLoadVarNum(VarNumNode(number, pointer=false, arrayOffset=false)): local
	push eax
	mov eax, [esp + 48] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_12  ; stringify
	add dword esp, 8  ; Free mem from fun call
	mov eax, [esp + 44] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_1  ; print
	add dword esp, 4  ; Free mem from fun call
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 48  ; Free mem from local scope bc of return
	jmp sd_7_cln  ; ReturnNode
	add esp, 48
sd_7_cln:
	ret
sd_9:  ; println
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(number, pointer=false, arrayOffset=false)): local
	push eax
	call sd_7  ; print
	add dword esp, 4  ; Free mem from fun call
	call sd_4  ; println
	add dword esp, 0  ; Free mem from fun call
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_9_cln  ; ReturnNode
	add esp, 0
sd_9_cln:
	ret
sd_12:  ; stringify
stringify:  ; breakpoint at: 44:2
	lea esi, [esp + 8]  ; Load address of the number
	mov edi, [esp + 4]  ; Load address of the string
.convert_loop:
	mov eax, dword [esi]  ; Load the 32-bit number
	mov edx, 0  ; Clear EDX for division
	mov ecx, 10  ; Set divisor to 10
	div ecx  ; Divide EAX by 10
	add dl, '0'  ; Convert remainder to ASCII
	mov [edi], dl  ; Store ASCII digit in buffer
	add edi, 4  ; Move buffer pointer back
	test eax, eax  ; Check if quotient is zero
	jnz .convert_loop  ; If not, continue conversion
	mov dword [edi], 0
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_12_cln  ; ReturnNode
	add esp, 0
sd_12_cln:
	ret
sd_16:  ; memcpy
	mov ecx, [esp + 4]  ; Length
	mov edi, [esp + 8]  ; To
	mov esi, [esp + 12]  ; From
.copy_loop:
	mov eax, [esi]
	mov [edi], eax
	add esi, 4
	add edi, 4
	loop .copy_loop
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_16_cln  ; ReturnNode
	add esp, 0
sd_16_cln:
	ret
sd_19:  ; strlen
	mov eax, [esp + 4]  ; Copy the address of the string into eax
	xor ecx, ecx  ; Clear ecx (counter register)
.loop:
	cmp dword [eax], 0  ; Compare the byte at the current address with null terminator
	je .done  ; If null terminator is found, exit loop
	add dword eax, 4  ; Move to the next byte in the string
	inc ecx  ; Increment the counter
	jmp .loop  ; Repeat the loop
.done:
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=90, column=19, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	push dword eax  ; Push var: length
	mov [esp], ecx  ; Move strlen to var
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(length, pointer=false, arrayOffset=false)): local
	; return node: TypeNode(generic=true, INT_16, pointer=false)
	add esp, 4  ; Free mem from local scope bc of return
	jmp sd_19_cln  ; ReturnNode
	add esp, 4
sd_19_cln:
	ret
section .text
	global _start
	global main
	global stringify
section .data
	esp_start dd 0
	var_1 dd 99, 100, 101, 0  ; 103:13
	var_2 dd 49, 50, 51, 0  ; 105:13
	var_3 dd 119, 104, 121, 32, 99, 97, 110, 32, 105, 32, 101, 118, 101, 110, 32, 100, 111, 32, 116, 104, 105, 115, 32, 63, 0  ; 107:13
	var_4 dd 101, 108, 115, 101, 0  ; 114:13
