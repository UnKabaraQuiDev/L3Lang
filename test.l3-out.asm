section .text
	global _start
_start:
	; Setup static: t -> sd_0
	mov dword [sd_0], 2  ; compileExprCompute 2
	; Setup static: fabrice -> sd_1
	mov dword [sd_1], 2  ; compileExprCompute 2
	call sd_9  ; main

	; Exit program
	mov ebx, eax
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
sd_9:  ; main
	; Call: double
	mov eax, 2  ; compileExprCompute 2
	push eax ; adding arg: d
	call sd_7  ; double
	add esp, 4  ; removing 1 arg(s)
	mov dword [sd_0], eax ; load static LetScopeDescriptor(t 0:15) = NumLitNode(2)
	;  Printout
	mov eax, 4
	mov ebx, 1
	mov ecx, sd_2
	mov edx, sd_2_len
	int 0x80
	; Setup local: arr
	sub esp, 12
	; Return
	mov eax, dword [sd_0]  ; load static LetScopeDescriptor(t 0:15) = NumLitNode(2)
	jmp sd_9_cln
	; Cleanup & Return
sd_9_cln:
	add esp, 12  ; Removing: 3 var(s)
	ret
sd_5:  ; exit
	; Exit program
	mov ebx, [esp + 4]
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
	; Return
	jmp sd_5_cln
	; Cleanup & Return
sd_5_cln:
	ret
sd_7:  ; double
	; Return
	mov eax, 4
	mov ebx, dword [esp + 4]  ; load arg LetScopeDescriptor(d 13:18) = stack index 0
	imul eax, ebx  ; NumLitNode(4) * VarNumNode(d) -> eax
	
	jmp sd_7_cln
	; Cleanup & Return
sd_7_cln:
	ret
section .data
	sd_0 dd 0  ; int16 t at 0:15
	sd_1 dd 0  ; int16 fabrice at 1:15
	sd_2 dd "Heloo world", 0  ; int text at 2:14
	sd_2_len equ $ - sd_2 ; int length text at 2:14
	sd_3 dd 0  ; int NOOOOT at 3:14
