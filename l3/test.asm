_start:
	mov dword eax, 4  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=5, column=28, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=4, value=4]))
	mov [sd_1], eax  ; Setting: SIZE_INT8
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
mem:  ; breakpoint at: 9:2
	sub esp, 12  ; Alloc for: size: 4 + 8, LetTypeDef: LetTypeDefNode(TypeNode(generic=true, type=TYPE, pointer=true, pointed=TypeNode(generic=true, type=INT_8, pointer=false)), arr, index=0, size=4)
	lea eax, [esp+8]
	sub eax, 8
	mov dword [esp+8], eax  ; Setup empty pointer arr -> sd_2
mem1:  ; breakpoint at: 13:2
	mov dword eax, 2  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=15, column=11, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=2, value=2]))
	push eax  ; Pushing result before setting
	mov dword ebx, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=15, column=6, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	imul ebx, 4
	mov dword ecx, [esp + 12]  ; Loading pointer 2, index = 0, size = 16
	add ecx, ebx  ; compileLoadComputeExpr(VarNumNode(arr, pointer=true, arrayOffset=true)): local
	pop eax
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(VarNumNode(arr, pointer=true, arrayOffset=true)))
	mov dword eax, 3  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=16, column=11, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=3, value=3]))
	push eax  ; Pushing result before setting
	mov dword ebx, 1  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=16, column=6, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=1, value=1]))
	imul ebx, 4
	mov dword ecx, [esp + 12]  ; Loading pointer 2, index = 0, size = 16
	add ecx, ebx  ; compileLoadComputeExpr(VarNumNode(arr, pointer=true, arrayOffset=true)): local
	pop eax
	mov [ecx], eax  ; compileLetTypeSet(LetTypeSetNode(VarNumNode(arr, pointer=true, arrayOffset=true)))
mem2:  ; breakpoint at: 18:2
	sub esp, 36  ; Alloc for: size: 4 + 32, LetTypeDef: LetTypeDefNode(TypeNode(generic=true, type=TYPE, pointer=true, pointed=TypeNode(generic=true, type=INT_8, pointer=false)), string, index=0, size=4)
	lea eax, [esp+32]
	sub eax, 32
	mov dword [esp+32], eax  ; Setup empty pointer string -> sd_3
	mov esi, sd_3  ; From
	mov edi, [esp+32]  ; To
	cld
	mov ecx, 8
	rep movsd
	mov eax, [esp + 32] ; compileLoadVarNum(VarNumNode(string, pointer=false, arrayOffset=false)): local
	push eax
	call sd_15  ; println
	add dword esp, 4  ; Free mem from fun call
	mov ebx, [sd_1]  ; compileLoadVarNum(VarNumNode(SIZE_INT8, pointer=false, arrayOffset=false)): static
	push ebx
	mov eax, [esp + 48] ; compileLoadVarNum(VarNumNode(arr, pointer=false, arrayOffset=false)): local
	push eax
	pop eax
	pop ebx
	add eax, ebx  ; VarNumNode(arr, pointer=false, arrayOffset=false) lu.pcy113.l3.lexer.TokenType[PLUS, fixed=true, string=false, charValue=+] VarNumNode(SIZE_INT8, pointer=false, arrayOffset=false) -> eax
	mov eax, [eax]  ; Loading BinaryOpNode(+)
	; return node: TypeNode(generic=true, type=INT, pointer=false)
	add esp, 48  ; Free mem from local scope bc of return
	jmp main_cln  ; ReturnNode
	add esp, 48
main_cln:
	ret
	sub esp, 4  ; Alloc for: size: 4 + 0, LetTypeDef: LetTypeDefNode(TypeNode(generic=true, type=INT_8, pointer=false), SIZE_INT8, index=0, size=4)
	mov dword eax, 4  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=5, column=28, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=4, value=4]))
	mov dword [esp+0], eax  ; Push var: SIZE_INT8
sd_8:  ; memcpy
	mov ecx, [esp + 4]  ; Length
	mov edi, [esp + 8]  ; To
	mov esi, [esp + 12]  ; From
.copy_loop:
	mov eax, [esi]
	mov [edi], eax
	add esi, 4
	add edi, 4
	loop .copy_loop
	; return node: TypeNode(generic=true, type=VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_8_cln  ; ReturnNode
	add esp, 0
sd_8_cln:
	ret
sd_11:  ; strlen
	mov eax, [esp + 4]  ; Copy the address of the string into eax
	xor ecx, ecx  ; Clear ecx (counter register)
.loop:
	cmp dword [eax], 0  ; Compare the byte at the current address with null terminator
	je .done  ; If null terminator is found, exit loop
	add dword eax, 4  ; Move to the next byte in the string
	inc ecx  ; Increment the counter
	jmp .loop  ; Repeat the loop
.done:
	sub esp, 4  ; Alloc for: size: 4 + 0, LetTypeDef: LetTypeDefNode(TypeNode(generic=true, type=INT_16, pointer=false), length, index=0, size=4)
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=29, column=19, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	mov dword [esp+0], eax  ; Push var: length
	mov [esp], ecx  ; Move strlen to var
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(length, pointer=false, arrayOffset=false)): local
	; return node: TypeNode(generic=true, type=INT_16, pointer=false)
	add esp, 4  ; Free mem from local scope bc of return
	jmp sd_11_cln  ; ReturnNode
	add esp, 4
sd_11_cln:
	ret
sd_13:  ; print
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
	; return node: TypeNode(generic=true, type=VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_13_cln  ; ReturnNode
	add esp, 0
sd_13_cln:
	ret
sd_15:  ; println
	mov eax, [esp + 4] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_13  ; print
	add dword esp, 4  ; Free mem from fun call
	call sd_16  ; println
	add dword esp, 0  ; Free mem from fun call
	; return node: TypeNode(generic=true, type=VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_15_cln  ; ReturnNode
	add esp, 0
sd_15_cln:
	ret
sd_16:  ; println
	sub esp, 4
	mov ecx, esp
	mov dword [ecx], 10  ; Newline
	; Print write
	mov eax, 4  ; Write
	mov ebx, 1  ; Stdout
	mov edx, 1  ; Length
	int 0x80  ; Syscall
	add esp, 4
	; return node: TypeNode(generic=true, type=VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_16_cln  ; ReturnNode
	add esp, 0
sd_16_cln:
	ret
sd_19:  ; print
	sub esp, 48  ; Alloc for: size: 4 + 44, LetTypeDef: LetTypeDefNode(TypeNode(generic=true, type=TYPE, pointer=true, pointed=TypeNode(generic=true, type=INT_8, pointer=false)), str, index=0, size=4)
	lea eax, [esp+44]
	sub eax, 44
	mov dword [esp+44], eax  ; Setup empty pointer str -> sd_18
	mov eax, [esp + 52]  ; compileLoadVarNum(VarNumNode(number, pointer=false, arrayOffset=false)): local
	push eax
	mov eax, [esp + 48] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_24  ; stringify
	add dword esp, 8  ; Free mem from fun call
	mov eax, [esp + 44] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_13  ; print
	add dword esp, 4  ; Free mem from fun call
	; return node: TypeNode(generic=true, type=VOID, pointer=false)
	add esp, 48  ; Free mem from local scope bc of return
	jmp sd_19_cln  ; ReturnNode
	add esp, 48
sd_19_cln:
	ret
sd_21:  ; println
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(number, pointer=false, arrayOffset=false)): local
	push eax
	call sd_19  ; print
	add dword esp, 4  ; Free mem from fun call
	call sd_16  ; println
	add dword esp, 0  ; Free mem from fun call
	; return node: TypeNode(generic=true, type=VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_21_cln  ; ReturnNode
	add esp, 0
sd_21_cln:
	ret
sd_24:  ; stringify
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
	; return node: TypeNode(generic=true, type=VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_24_cln  ; ReturnNode
	add esp, 0
sd_24_cln:
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
	sd_1 dd 0  ; SIZE_INT8
	sd_3 dd 116, 101, 115, 116, 32, 58, 40, 0  ; string at 20:11
