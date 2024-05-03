_start:
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
mem:  ; breakpoint at: 7:2
	lea eax, [esp]
	sub eax, 12
	push eax  ; Setup empty pointer arr -> sd_1
	sub esp, 8
mem1:  ; breakpoint at: 11:2
	mov dword eax, 2  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=13, column=11, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=2, value=2]))
	push eax  ; Pushing result before setting
	mov dword ebx, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=13, column=6, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	imul ebx, 4
	lea dword ecx, [esp + 12]  ; Loading pointer, index = 0, size = 16
	add ecx, ebx  ; compileLoadComputeExpr(VarNumNode(arr, pointer=true, arrayOffset=true)): local
	pop eax
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(VarNumNode(arr, pointer=true, arrayOffset=true)))
	mov dword eax, 3  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=14, column=11, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=3, value=3]))
	push eax  ; Pushing result before setting
	mov dword ebx, 1  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=14, column=6, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=1, value=1]))
	imul ebx, 4
	lea dword ecx, [esp + 12]  ; Loading pointer, index = 0, size = 16
	add ecx, ebx  ; compileLoadComputeExpr(VarNumNode(arr, pointer=true, arrayOffset=true)): local
	pop eax
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(VarNumNode(arr, pointer=true, arrayOffset=true)))
mem2:  ; breakpoint at: 16:2
	mov dword ebx, 1  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=18, column=16, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=1, value=1]))
	imul ebx, 4
	mov ecx, [esp + 8]  ; Loading pointer, index = 12, size = 12
	add ecx, ebx
	mov eax, [ecx] ; compileLoadVarNum(VarNumNode(arr, pointer=true, arrayOffset=true)): local
	; return node: TypeNode(generic=true, INT, pointer=false)
	add esp, 12  ; Free mem from local scope bc of return
	jmp main_cln  ; ReturnNode
	add esp, 12
main_cln:
	ret
sd_6:  ; memcpy
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
	jmp sd_6_cln  ; ReturnNode
	add esp, 0
sd_6_cln:
	ret
sd_9:  ; strlen
	mov eax, [esp + 4]  ; Copy the address of the string into eax
	xor ecx, ecx  ; Clear ecx (counter register)
.loop:
	cmp dword [eax], 0  ; Compare the byte at the current address with null terminator
	je .done  ; If null terminator is found, exit loop
	add dword eax, 4  ; Move to the next byte in the string
	inc ecx  ; Increment the counter
	jmp .loop  ; Repeat the loop
.done:
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=29, column=19, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	push dword eax  ; Push var: length
	mov [esp], ecx  ; Move strlen to var
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(length, pointer=false, arrayOffset=false)): local
	; return node: TypeNode(generic=true, INT_16, pointer=false)
	add esp, 4  ; Free mem from local scope bc of return
	jmp sd_9_cln  ; ReturnNode
	add esp, 4
sd_9_cln:
	ret
sd_11:  ; print
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
	jmp sd_11_cln  ; ReturnNode
	add esp, 0
sd_11_cln:
	ret
sd_13:  ; println
	mov eax, [esp + 4] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_11  ; print
	add dword esp, 4  ; Free mem from fun call
	call sd_14  ; println
	add dword esp, 0  ; Free mem from fun call
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_13_cln  ; ReturnNode
	add esp, 0
sd_13_cln:
	ret
sd_14:  ; println
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
	jmp sd_14_cln  ; ReturnNode
	add esp, 0
sd_14_cln:
	ret
sd_17:  ; print
	lea eax, [esp]
	sub eax, 48
	push eax  ; Setup empty pointer str -> sd_16
	sub esp, 44
	mov eax, [esp + 52]  ; compileLoadVarNum(VarNumNode(number, pointer=false, arrayOffset=false)): local
	push eax
	mov eax, [esp + 48] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_22  ; stringify
	add dword esp, 8  ; Free mem from fun call
	mov eax, [esp + 44] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_11  ; print
	add dword esp, 4  ; Free mem from fun call
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 48  ; Free mem from local scope bc of return
	jmp sd_17_cln  ; ReturnNode
	add esp, 48
sd_17_cln:
	ret
sd_19:  ; println
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(number, pointer=false, arrayOffset=false)): local
	push eax
	call sd_17  ; print
	add dword esp, 4  ; Free mem from fun call
	call sd_14  ; println
	add dword esp, 0  ; Free mem from fun call
	; return node: TypeNode(generic=true, VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_19_cln  ; ReturnNode
	add esp, 0
sd_19_cln:
	ret
sd_22:  ; stringify
stringify:  ; breakpoint at: 78:2
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
	jmp sd_22_cln  ; ReturnNode
	add esp, 0
sd_22_cln:
	ret
section .text
	global _start
	global main
	global mem
	global mem1
	global mem2
	global stringify
section .data
	esp_start dd 0
