section .text
	global _start
	global sd_5
_start:
	; Setup static: ststarr -> sd_0
	call sd_5  ; main

	; Exit program
	mov ebx, eax
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
sd_5:  ; main
	; Setup local: a
	mov eax, 0  ; compileExprCompute 0
	push eax
	; Setup local: arr
	sub esp, 12
	mov eax, 68  ; compileExprCompute 68
	mov ecx, 0  ; compileExprCompute 0
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	add ebx, esp  ; Adding offset for arr
	mov dword [ebx + 0], eax ; load local LetScopeDescriptor(arr -> sd_4 11:10) = NumLitNode(68)
	mov eax, 69  ; compileExprCompute 69
	push eax  ; Pushing to stack in case offset calc uses eax
	; Call: double
	mov eax, 0  ; compileExprCompute 0
	push eax ; adding arg: t
	call sd_2  ; double
	add esp, 4  ; removing 1 arg(s)
	mov ecx, eax
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	pop eax  ; Poping from stack to get value back
	add ebx, esp  ; Adding offset for arr
	mov dword [ebx + 0], eax ; load local LetScopeDescriptor(arr -> sd_4 11:10) = NumLitNode(69)
	mov eax, 70  ; compileExprCompute 70
	push eax  ; Pushing to stack in case offset calc uses eax
	; Call: double
	mov eax, 1  ; compileExprCompute 1
	push eax ; adding arg: t
	call sd_2  ; double
	add esp, 4  ; removing 1 arg(s)
	mov ecx, eax
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	pop eax  ; Poping from stack to get value back
	add ebx, esp  ; Adding offset for arr
	mov dword [ebx + 0], eax ; load local LetScopeDescriptor(arr -> sd_4 11:10) = NumLitNode(70)
	mov eax, 155  ; compileExprCompute 155
	mov ecx, 1  ; compileExprCompute 1
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	add ebx, sd_0  ; Adding offset for arr (static)
	mov dword [ebx], eax ; load static LetScopeDescriptor(ststarr -> sd_0 0:16) = NumLitNode(155)
	; Compute offset into ebx
	push eax  ; Pushing to stack in case offset calc uses eax
	mov ecx, 1  ; compileExprCompute 1
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	pop eax  ; Poping from stack to get value back
	lea ecx, dword [sd_0]  ; Load static address
	add ebx, ecx  ; Add static address to offset ebx
	mov eax, [ebx]  ; load static LetScopeDescriptor(ststarr -> sd_0 0:16) = ArrayInitNode(2, true)
	mov ebx, 1
	sub eax, ebx  ; VarNumNode(ststarr, pointer=true, arrayOffset=true) - NumLitNode(1) -> eax
	
	mov ecx, 2  ; compileExprCompute 2
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	add ebx, sd_0  ; Adding offset for arr (static)
	mov dword [ebx], eax ; load static LetScopeDescriptor(ststarr -> sd_0 0:16) = BinaryOpNode(-)
	mov eax, 11  ; compileExprCompute 11
	mov dword [esp + 0], eax ; load local pointer LetScopeDescriptor(arr -> sd_4 11:10) = NumLitNode(11)
	; Return
	; Compute offset into ebx
	push eax  ; Pushing to stack in case offset calc uses eax
	mov ecx, 0  ; compileExprCompute 0
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	pop eax  ; Poping from stack to get value back
	add ebx, esp  ; Add pointer to offset ebx
	mov eax, [ebx + 0]  ; load local 1 LetScopeDescriptor(arr -> sd_4 11:10) = ArrayInitNode(3, true)
	jmp sd_5_cln
	; Cleanup & Return
sd_5_cln:
	add esp, 16  ; Removing: 4 var(s)
	ret
sd_2:  ; double
	mov eax, dword [esp + 4]  ; load arg 2 LetScopeDescriptor(t -> sd_1 2:19) = stack index 0
	mov ebx, 1
	add eax, ebx  ; VarNumNode(t, pointer=false, arrayOffset=false) + NumLitNode(1) -> eax
	
	mov dword [esp + 4], eax ; load local LetScopeDescriptor(t -> sd_1 2:19) = BinaryOpNode(+)
	; Return
	mov eax, dword [esp + 4]  ; load arg 2 LetScopeDescriptor(t -> sd_1 2:19) = stack index 0
	jmp sd_2_cln
	; Cleanup & Return
sd_2_cln:
	ret
section .data
	sd_0 dd 2 dup (0)  ; int16 ststarr at 0:16
