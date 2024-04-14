_start:
	mov dword eax, 13  ; compileComputeExpr(NumLitNode(13))
	mov [sd_0], eax  ; Setting: b
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
	push esp   ; Setup array pointer
	sub dword [esp], 24
	sub esp, 20  ; Setup array
	mov dword eax, 116  ; compileComputeExpr(NumLitNode(116))
	mov [esp + 0], eax
	mov dword eax, 101  ; compileComputeExpr(NumLitNode(101))
	mov [esp + 4], eax
	mov dword eax, 115  ; compileComputeExpr(NumLitNode(115))
	mov [esp + 8], eax
	mov dword eax, 116  ; compileComputeExpr(NumLitNode(116))
	mov [esp + 12], eax
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(0))
	mov [esp + 16], eax
stop:  ; breakpoint at: 38:1
	push esp   ; Setup array pointer
	sub dword [esp], 20
	sub esp, 16  ; Setup array
	mov dword eax, 115  ; compileComputeExpr(NumLitNode(115))
	mov [esp + 0], eax
	mov dword eax, 116  ; compileComputeExpr(NumLitNode(116))
	mov [esp + 4], eax
	mov dword eax, 112  ; compileComputeExpr(NumLitNode(112))
	mov [esp + 8], eax
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(0))
	mov [esp + 12], eax
	mov eax, [esp + 16]  ; Load pointer into reg
	push eax
	call sd_5  ; println
	add dword esp, 24  ; Free mem from fun call
	push esp   ; Setup array pointer
	sub dword [esp], 20
	sub esp, 16  ; Setup array
	mov dword eax, 115  ; compileComputeExpr(NumLitNode(115))
	mov [esp + 0], eax
	mov dword eax, 116  ; compileComputeExpr(NumLitNode(116))
	mov [esp + 4], eax
	mov dword eax, 112  ; compileComputeExpr(NumLitNode(112))
	mov [esp + 8], eax
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(0))
	mov [esp + 12], eax
	mov eax, [esp + 16]  ; Load pointer into reg
	push eax
	call sd_3  ; strlen
	add dword esp, 24  ; Free mem from fun call
	mov eax, eax
	jmp main_cln  ; ReturnNode
main_cln:
	add esp, 24
	ret
sd_3:  ; strlen
	mov eax, [esp + 4]  ; Copy the address of the string into eax
	xor ecx, ecx  ; Clear ecx (counter register)
.loop:
	cmp dword [eax], 0  ; Compare the byte at the current address with null terminator
	je .done  ; If null terminator is found, exit loop
	add dword eax, 4  ; Move to the next byte in the string
	inc ecx  ; Increment the counter
	jmp .loop  ; Repeat the loop
.done:
	mov dword eax, 0  ; compileComputeExpr(NumLitNode(0))
	push dword eax  ; Push var: length
	mov [esp], ecx  ; Move strlen to var
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(length, pointer=false, arrayOffset=false)): local; STACK_POS = 8
	jmp sd_3_cln  ; ReturnNode
sd_3_cln:
	add esp, 4
	ret
sd_5:  ; println
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
	jmp sd_5_cln  ; ReturnNode
sd_5_cln:
	add esp, 0
	ret
section .text
	global _start
	global main
	global stop
section .data
	sd_0 dd 0  ; b
