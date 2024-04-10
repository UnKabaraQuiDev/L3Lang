section .text
	global _start
	global sd_3
_start:
	call sd_3  ; main

	; Exit program
	mov ebx, eax
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
sd_3:  ; main
	; Setup local: arr
	sub esp, 12
	mov eax, 68  ; compileExprCompute 68
	mov ecx, 0  ; compileExprCompute 0
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	add ebx, esp  ; Adding offset for arr
	mov dword [ebx + 0], eax ; load local LetScopeDescriptor(arr -> sd_2 8:10) = NumLitNode(68)
	mov eax, 69  ; compileExprCompute 69
	push eax  ; Pushing to stack in case offset calc uses eax
	; Call: double
	mov eax, 0  ; compileExprCompute 0
	push eax ; adding arg: t
	call sd_1  ; double
	add esp, 4  ; removing 1 arg(s)
	mov ecx, eax
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	pop eax  ; Poping from stack to get value back
	add ebx, esp  ; Adding offset for arr
	mov dword [ebx + 0], eax ; load local LetScopeDescriptor(arr -> sd_2 8:10) = NumLitNode(69)
	mov eax, 70  ; compileExprCompute 70
	push eax  ; Pushing to stack in case offset calc uses eax
	; Call: double
	mov eax, 1  ; compileExprCompute 1
	push eax ; adding arg: t
	call sd_1  ; double
	add esp, 4  ; removing 1 arg(s)
	mov ecx, eax
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	pop eax  ; Poping from stack to get value back
	add ebx, esp  ; Adding offset for arr
	mov dword [ebx + 0], eax ; load local LetScopeDescriptor(arr -> sd_2 8:10) = NumLitNode(70)
	; Return
	; Compute offset into ebx
	push eax  ; Pushing to stack in case offset calc uses eax
	mov ecx, 0  ; compileExprCompute 0
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	pop eax  ; Poping from stack to get value back
	add ebx, esp  ; Add pointer to offset ebx
	mov eax, [ebx + 0]  ; load local 1 LetScopeDescriptor(arr -> sd_2 8:10) = ArrayInitNode(3, true)
	jmp sd_3_cln
	; Cleanup & Return
sd_3_cln:
	add esp, 12  ; Removing: 3 var(s)
	ret
sd_1:  ; double
	mov eax, dword [esp + 4]  ; load arg 2 LetScopeDescriptor(t -> sd_0 0:19) = stack index 0
	mov ebx, 1
	add eax, ebx  ; VarNumNode(t, false) + NumLitNode(1) -> eax
	
	mov dword [esp + 4], eax ; load local LetScopeDescriptor(t -> sd_0 0:19) = BinaryOpNode(+)
	; Return
	mov eax, dword [esp + 4]  ; load arg 2 LetScopeDescriptor(t -> sd_0 0:19) = stack index 0
	jmp sd_1_cln
	; Cleanup & Return
sd_1_cln:
	ret
section .data
