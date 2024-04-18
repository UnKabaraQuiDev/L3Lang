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
	mov dword eax, 2  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=94, column=14, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=2, value=2]))
	push dword eax  ; Push var: a
stop1:  ; breakpoint at: 96:2
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(a, pointer=false, arrayOffset=false)): local
	push eax
	call sd_4  ; println
	add dword esp, 4  ; Free mem from fun call
stop:  ; breakpoint at: 100:2
	mov eax, esp
	sub eax, 20
	push eax  ; Setup array pointer
	sub esp, 16
	sub esp, 12
	mov dword [esp + 8], var_1  ; From
	mov dword [esp + 4], eax  ; To
	mov dword [esp + 0], 4  ; Length
	call sd_11
	add esp, 12
	mov eax, [esp + 16]  ; Loading StringLitNode pointer
	push eax
	call sd_1  ; println
	add dword esp, 20  ; Free mem from fun call
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(a, pointer=false, arrayOffset=false)): local
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp main_cln  ; ReturnNode
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp main_cln  ; ReturnNode
main_cln:
	add esp, 4
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
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_1_cln  ; ReturnNode
sd_1_cln:
	add esp, 0
	ret
sd_4:  ; println
println:  ; breakpoint at: 20:2
	lea eax, [esp]
	sub eax, 12
	push eax  ; Setup empty pointer str -> sd_3
	sub esp, 8
	mov eax, [esp + 16]  ; compileLoadVarNum(VarNumNode(number, pointer=false, arrayOffset=false)): local
	push eax
	mov eax, [esp + 8] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_7  ; stringify
	add dword esp, 8  ; Free mem from fun call
println2:  ; breakpoint at: 25:2
	mov eax, [esp + 8] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_1  ; println
	add dword esp, 4  ; Free mem from fun call
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 12  ; Free mem from local scope bc of return
	jmp sd_4_cln  ; ReturnNode
sd_4_cln:
	add esp, 12
	ret
sd_7:  ; stringify
stringify:  ; breakpoint at: 31:2
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
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_7_cln  ; ReturnNode
sd_7_cln:
	add esp, 0
	ret
sd_11:  ; memcpy
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
	jmp sd_11_cln  ; ReturnNode
sd_11_cln:
	add esp, 0
	ret
sd_14:  ; strlen
	mov eax, [esp + 4]  ; Copy the address of the string into eax
	xor ecx, ecx  ; Clear ecx (counter register)
.loop:
	cmp dword [eax], 0  ; Compare the byte at the current address with null terminator
	je .done  ; If null terminator is found, exit loop
	add dword eax, 4  ; Move to the next byte in the string
	inc ecx  ; Increment the counter
	jmp .loop  ; Repeat the loop
.done:
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=76, column=19, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	push dword eax  ; Push var: length
	mov [esp], ecx  ; Move strlen to var
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(length, pointer=false, arrayOffset=false)): local
	; return node: TypeNode(generic=true, INT_16, pointer=false)
	add esp, 4  ; Free mem from local scope bc of return
	jmp sd_14_cln  ; ReturnNode
	; return node: TypeNode(generic=true, INT_16, pointer=false)
	add esp, 4  ; Free mem from local scope bc of return
	jmp sd_14_cln  ; ReturnNode
sd_14_cln:
	add esp, 4
	ret
sd_15:  ; test
	mov eax, esp
	sub eax, 28
	push eax  ; Setup array pointer
	sub esp, 24
	sub esp, 12
	mov dword [esp + 8], var_2  ; From
	mov dword [esp + 4], eax  ; To
	mov dword [esp + 0], 6  ; Length
	call sd_11
	add esp, 12
	mov eax, [esp + 24]  ; Loading StringLitNode pointer
	push eax
	call sd_1  ; println
	add dword esp, 28  ; Free mem from fun call
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=85, column=12, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_15_cln  ; ReturnNode
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_15_cln  ; ReturnNode
sd_15_cln:
	add esp, 0
	ret
sd_17:  ; test
	mov eax, esp
	sub eax, 28
	push eax  ; Setup array pointer
	sub esp, 24
	sub esp, 12
	mov dword [esp + 8], var_3  ; From
	mov dword [esp + 4], eax  ; To
	mov dword [esp + 0], 6  ; Length
	call sd_11
	add esp, 12
	mov eax, [esp + 24]  ; Loading StringLitNode pointer
	push eax
	call sd_1  ; println
	add dword esp, 28  ; Free mem from fun call
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=90, column=12, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_17_cln  ; ReturnNode
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_17_cln  ; ReturnNode
sd_17_cln:
	add esp, 0
	ret
section .text
	global _start
	global main
	global stop1
	global stop
	global println
	global println2
	global stringify
section .data
	esp_start dd 0
	var_1 dd 115, 115, 115, 0  ; 102:12
	var_2 dd 116, 101, 115, 116, 49, 0  ; 84:12
	var_3 dd 116, 101, 115, 116, 50, 0  ; 89:12
