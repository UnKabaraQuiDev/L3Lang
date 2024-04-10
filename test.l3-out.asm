section .text
	global _start
_start:
	call sd_4  ; main

	; Exit program
	mov ebx, eax
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
sd_4:  ; main
	; Setup local: a
	mov eax, 1  ; compileExprCompute 1
	push eax
	; Setup local: b
	mov eax, 2  ; compileExprCompute 2
	push eax
	; Call: double
	call sd_1  ; double
	add esp, 0  ; removing 0 arg(s)
	; Call: double
	call sd_1  ; double
	add esp, 0  ; removing 0 arg(s)
	mov dword [esp + 4], eax ; load local LetScopeDescriptor(a -> sd_2 6:9) = NumLitNode(1)
	; Return
	mov eax, dword [esp + 4]  ; load local 2 LetScopeDescriptor(a -> sd_2 6:9) = NumLitNode(1)
	jmp sd_4_cln
	; Cleanup & Return
sd_4_cln:
	add esp, 8  ; Removing: 2 var(s)
	ret
sd_1:  ; double
	; Setup local: b
	mov eax, 13  ; compileExprCompute 13
	push eax
	; Return
	mov eax, dword [esp + 0]  ; load local 2 LetScopeDescriptor(b -> sd_0 1:9) = NumLitNode(13)
	jmp sd_1_cln
	; Cleanup & Return
sd_1_cln:
	add esp, 4  ; Removing: 1 var(s)
	ret
section .data
