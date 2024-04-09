section .text
	global _start
_start:
	; Setup static: t -> sd_0
	mov dword [sd_0], 2  ; compileExprCompute 2
	; Setup static: fabrice -> sd_1
	mov dword [sd_1], 2  ; compileExprCompute 2
	call sd_7  ; main

	; Exit program
	mov ebx, eax
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
sd_7:  ; main
	; Call: double
	mov eax, 2  ; compileExprCompute 2
	push eax ; adding arg: d
	call sd_6  ; double
	add esp, 4  ; removing 1 arg(s)
	mov dword [sd_0], eax ; load static LetScopeDescriptor(t 0:15) = NumLitNode(2)
	;  Printout
	mov eax, 4
	mov ebx, 1
	mov ecx, sd_2
	mov edx, sd_2_len
	int 0x80
	; Return
	mov eax, dword [sd_0]  ; load static LetScopeDescriptor(t 0:15) = NumLitNode(2)
	ret
sd_4:  ; exit
	; Exit program
	mov ebx, [esp + 4]
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
	ret  ; Default return
sd_6:  ; double
	; Return
	mov eax, 3
	mov ebx, dword [esp + 4]  ; load arg LetScopeDescriptor(d 21:18) = stack index 0
	imul eax, ebx  ; NumLitNode(3) * VarNumNode(d) -> eax
	
	ret
section .data
	sd_0 dd 0  ; int16 t at 0:15
	sd_1 dd 0  ; int16 fabrice at 1:15
	sd_2 dd "Heloo world", 0  ; int16 text at 2:15
	sd_2_len equ $ - sd_2 ; int16 length text at 2:15
