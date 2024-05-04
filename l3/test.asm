_start:
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
	sub esp, 36  ; Alloc for: size: 4 + 32, LetTypeDef: LetTypeDefNode(string, TypeNode(generic=true, type=TYPE, pointer=true, pointed=TypeNode(generic=true, type=INT_8, pointer=false)), index=0, size=4)
	lea eax, [esp+32]
	sub eax, 32
	mov dword [esp+32], eax  ; Setup empty pointer string -> sd_4
	mov esi, sd_4  ; From
	mov edi, [esp+32]  ; To
	cld
	mov ecx, 8
	rep movsd
	mov eax, [esp + 32] ; compileLoadVarNum(VarNumNode(string, pointer=false, arrayOffset=false)): local
	push eax
	call sd_17  ; println
	add dword esp, 4  ; Free mem from fun call
init:  ; breakpoint at: 20:2
	sub esp, 8  ; Alloc for: size: 8 + 0, LetTypeDef: LetTypeDefNode(person, TypeNode(generic=false, type=IDENT, pointer=false, ident=Person), index=0, size=4)
	mov dword eax, 6  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=22, column=33, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=6, value=6]))
	mov [esp + 0], eax
	sub esp, 4  ; Alloc for: size: 4 + 0, LetTypeDef: ArrayInitNode(0, false)
	lea eax, [esp+0]
	sub eax, 0
	mov dword [esp+0], eax  ; Setup empty pointer otf -> var_1
	mov esi, var_1  ; From
	mov edi, [esp+0]  ; To
	cld
	mov ecx, 0
	rep movsd
	mov [esp + 4], eax
return:  ; breakpoint at: 24:2
	mov eax, [esp + 0] ; compileLoadComputeExpr(VarNumNode(person.age, pointer=false, arrayOffset=false)): local
	; return node: TypeNode(generic=true, type=INT, pointer=false)
	add esp, 44  ; Free mem from local scope bc of return
	jmp main_cln  ; ReturnNode
	add esp, 44
main_cln:
	ret
sd_10:  ; memcpy
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
	jmp sd_10_cln  ; ReturnNode
	add esp, 0
sd_10_cln:
	ret
sd_13:  ; strlen
	mov eax, [esp + 4]  ; Copy the address of the string into eax
	xor ecx, ecx  ; Clear ecx (counter register)
.loop:
	cmp dword [eax], 0  ; Compare the byte at the current address with null terminator
	je .done  ; If null terminator is found, exit loop
	add dword eax, 4  ; Move to the next byte in the string
	inc ecx  ; Increment the counter
	jmp .loop  ; Repeat the loop
.done:
	sub esp, 4  ; Alloc for: size: 4 + 0, LetTypeDef: LetTypeDefNode(length, TypeNode(generic=true, type=INT_16, pointer=false), index=0, size=4)
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(lu.pcy113.l3.lexer.tokens.NumericLiteralToken[line=29, column=19, type=lu.pcy113.l3.lexer.TokenType[NUM_LIT, fixed=false, string=false], literal=0, value=0]))
	mov dword [esp+0], eax  ; Push var: length
	mov [esp], ecx  ; Move strlen to var
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(length, pointer=false, arrayOffset=false)): local
	; return node: TypeNode(generic=true, type=INT_16, pointer=false)
	add esp, 4  ; Free mem from local scope bc of return
	jmp sd_13_cln  ; ReturnNode
	add esp, 4
sd_13_cln:
	ret
sd_15:  ; print
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
	jmp sd_15_cln  ; ReturnNode
	add esp, 0
sd_15_cln:
	ret
sd_17:  ; println
	mov eax, [esp + 4] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_15  ; print
	add dword esp, 4  ; Free mem from fun call
	call sd_18  ; println
	add dword esp, 0  ; Free mem from fun call
	; return node: TypeNode(generic=true, type=VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_17_cln  ; ReturnNode
	add esp, 0
sd_17_cln:
	ret
sd_18:  ; println
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
	jmp sd_18_cln  ; ReturnNode
	add esp, 0
sd_18_cln:
	ret
sd_21:  ; print
	sub esp, 48  ; Alloc for: size: 4 + 44, LetTypeDef: LetTypeDefNode(str, TypeNode(generic=true, type=TYPE, pointer=true, pointed=TypeNode(generic=true, type=INT_8, pointer=false)), index=0, size=4)
	lea eax, [esp+44]
	sub eax, 44
	mov dword [esp+44], eax  ; Setup empty pointer str -> sd_20
	mov eax, [esp + 52]  ; compileLoadVarNum(VarNumNode(number, pointer=false, arrayOffset=false)): local
	push eax
	mov eax, [esp + 48] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_26  ; stringify
	add dword esp, 8  ; Free mem from fun call
	mov eax, [esp + 44] ; compileLoadVarNum(VarNumNode(str, pointer=false, arrayOffset=false)): local
	push eax
	call sd_15  ; print
	add dword esp, 4  ; Free mem from fun call
	; return node: TypeNode(generic=true, type=VOID, pointer=false)
	add esp, 48  ; Free mem from local scope bc of return
	jmp sd_21_cln  ; ReturnNode
	add esp, 48
sd_21_cln:
	ret
sd_23:  ; println
	mov eax, [esp + 4]  ; compileLoadVarNum(VarNumNode(number, pointer=false, arrayOffset=false)): local
	push eax
	call sd_21  ; print
	add dword esp, 4  ; Free mem from fun call
	call sd_18  ; println
	add dword esp, 0  ; Free mem from fun call
	; return node: TypeNode(generic=true, type=VOID, pointer=false)
	add esp, 0  ; Free mem from local scope bc of return
	jmp sd_23_cln  ; ReturnNode
	add esp, 0
sd_23_cln:
	ret
sd_26:  ; stringify
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
	jmp sd_26_cln  ; ReturnNode
	add esp, 0
sd_26_cln:
	ret
section .text
	global _start
	global main
	global init
	global return
	global stringify
section .data
	esp_start dd 0
	sd_4 dd 116, 101, 115, 116, 32, 58, 40, 0  ; string at 12:11
	var_1 dd 2, 6  ; otf arr
