_start:
	call main  ; Call main
	; Exit program
	mov ebx, eax  ; Move return to ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
main:  ; main
	mov eax, dword [heap_ptr]
	push eax  ; Setup array pointer
	sub esp, 12
	mov dword [esp + 8], sd_10  ; From
	mov dword [esp + 4], eax  ; To
	mov dword [esp + 0], 5  ; Length
	call sd_9
	add esp, 12
	mov eax, dword [heap_ptr]  ; Load pointer into reg
	add dword [heap_ptr], 20
stop:  ; breakpoint at: 50:1
	mov eax, [esp + 0] ; compileLoadVarNum(VarNumNode(a, pointer=false, arrayOffset=false)): local, stack = 4, index = 0, rindex = 0, is arg = false
	push eax
	call sd_5  ; println
	add dword esp, 4  ; Free mem from fun call
	mov eax, dword [heap_ptr]
	push eax  ; Setup array pointer
	sub esp, 12
	mov dword [esp + 8], var_1  ; From
	mov dword [esp + 4], eax  ; To
	mov dword [esp + 0], 2  ; Length
	call sd_9
	add esp, 12
	mov eax, dword [heap_ptr]  ; Load pointer into reg
	add dword [heap_ptr], 8
	push eax
	call sd_5  ; println
	add dword esp, 8  ; Free mem from fun call
	mov eax, sd_0  ; compileLoadVarNum(VarNumNode(string, pointer=false, arrayOffset=false)): static
	push eax
	call sd_3  ; strlen
	add dword esp, 4  ; Free mem from fun call
	mov eax, eax
	jmp main_cln  ; ReturnNode
main_cln:
	add esp, 4
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
	mov eax, [esp + 0]  ; compileLoadVarNum(VarNumNode(length, pointer=false, arrayOffset=false)): local; STACK_POS = 8, index = 1
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
sd_9:  ; memcpy
	mov ecx, [esp + 4]  ; Length
	mov edi, [esp + 8]  ; To
	mov esi, [esp + 12]  ; From
.copy_loop:
	mov eax, [esi]
	mov [edi], eax
	add esi, 4
	add edi, 4
	loop .copy_loop
	jmp sd_9_cln  ; ReturnNode
sd_9_cln:
	add esp, 0
	ret
section .text
	global _start
	global main
	global stop
section .data
	heap_space resb 1024
	heap_ptr dd heap_space
	sd_0 dd 115, 116, 114, 105, 110, 103, 0  ; string
	sd_10 dd 116, 101, 115, 116, 0  ; a
	var_1 dd 110, 0  ; null
