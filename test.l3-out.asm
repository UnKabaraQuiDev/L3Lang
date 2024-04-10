section .text
	global _start
	global sd_4
_start:
	; Setup static: ststarr -> sd_0
	call sd_4  ; main

	; Exit program
	mov ebx, eax
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
sd_4:  ; main
	; Setup local: arr
	sub esp, 12
	mov eax, 2
	mov ecx, 0
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	add ebx, esp  ; Adding offset for arr
	mov [ebx + 4], eax ; load local LetScopeDescriptor(arr -> sd_3 8:10) = NumLitNode(2)
	mov eax, 1
	mov [esp + 4], eax ; load local pointer LetScopeDescriptor(arr -> sd_3 8:10) = NumLitNode(1)
	; Return
	; Compute offset into ebx
	push eax  ; Pushing to stack in case offset calc uses eax
	mov ecx, 1
	imul ebx, ecx, 4  ; Bc stack var size = 4B
	pop eax  ; Poping from stack to get value back
	add ebx, esp  ; Add pointer to offset ebx
	mov eax, [ebx + 0]  ; load local 1 LetScopeDescriptor(arr -> sd_3 8:10) = ArrayInitNode(3, true)
	jmp sd_4_cln
	; Cleanup & Return
sd_4_cln:
	add esp, 12  ; Removing: 3 var(s)
	ret
sd_2:  ; double
	mov eax, dword [esp + 4]  ; load arg 2 LetScopeDescriptor(t -> sd_1 2:19) = stack index 0
	mov ebx, 1
	add eax, ebx  ; VarNumNode(t, pointer=false, arrayOffset=false) + NumLitNode(1) -> eax
	
	mov [esp + 4], eax ; load local LetScopeDescriptor(t -> sd_1 2:19) = BinaryOpNode(+)
	; Return
	mov eax, dword [esp + 4]  ; load arg 2 LetScopeDescriptor(t -> sd_1 2:19) = stack index 0
	jmp sd_2_cln
	; Cleanup & Return
sd_2_cln:
	ret
section .data
	sd_0 dd 2 dup (0)  ; int16 ststarr at 0:16
	push eax  ; Pushing array start onto stack
