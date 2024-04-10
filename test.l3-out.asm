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
	mov eax, 55
	push eax
	; Setup local: arr
	sub esp, 12
	mov eax, 68
	mov ecx, 0
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	add ebx, esp  ; Adding offset for arr
	mov dword [ebx + 0], eax ; load local LetScopeDescriptor(arr -> sd_4 10:10) = NumLitNode(68)
	mov eax, dword [esp + 12]  ; load local 2 LetScopeDescriptor(a -> sd_3 8:9) = NumLitNode(55)
	lea eax, dword [esp + 12]  ; load local 2 LetScopeDescriptor(a -> sd_3 8:9) = NumLitNode(55)
	mov dword [esp + 0], eax ; load local LetScopeDescriptor(arr -> sd_4 10:10) = LocalizingNode
	; Return
	; Compute offset into ebx
	push eax  ; Pushing to stack in case offset calc uses eax
	mov ecx, 0
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	pop eax  ; Poping from stack to get value back
	add ebx, esp  ; Add pointer to offset ebx
	mov eax, [ebx + 0]  ; load local 1 LetScopeDescriptor(arr -> sd_4 10:10) = ArrayInitNode(3, true)
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
