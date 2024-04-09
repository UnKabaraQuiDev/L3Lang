section .text
	global _start
_start:
	; Setup static: t -> sd_0
	mov dword [sd_0], 2  ; compileExprCompute 2
	; Setup static: fabrice -> sd_1
	mov dword [sd_1], 2  ; compileExprCompute 2
	call sd_5  ; main

	; Exit program
	mov ebx, eax
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
sd_5:  ; main
	mov eax, dword [sd_1]  ; load static LetScopeDescriptor(fabrice 1:15) = NumLitNode(2)
	mov ebx, dword [sd_0]  ; load static LetScopeDescriptor(t 0:15) = NumLitNode(2)
	add eax, ebx  ; VarNumNode(fabrice) + VarNumNode(t) -> eax
	mov ebx, 12
	add eax, ebx  ; BinaryOpNode(+) + NumLitNode(12) -> eax
	
	mov dword [sd_0], eax ; load static LetScopeDescriptor(t 0:15) = NumLitNode(2)
	; Call: FUINCTION
	mov eax, 1000  ; compileExprCompute 1000
	push eax ; adding arg: test
	mov eax, dword [sd_0]  ; load static LetScopeDescriptor(t 0:15) = NumLitNode(2)
	push eax ; adding arg: input
	call sd_4  ; FUINCTION
	pop eax  ; removing arg: input
	pop eax  ; removing arg: test
	; Return
	mov eax, 12  ; compileExprCompute 12
	ret
sd_4:  ; FUINCTION
	; Call: FUINCTION
	mov eax, dword [esp + 8]  ; load arg LetScopeDescriptor(test 3:34) = stack index 1
	push eax ; adding arg: test
	mov eax, 1  ; compileExprCompute 1
	push eax ; adding arg: input
	call sd_4  ; FUINCTION
	pop eax  ; removing arg: input
	pop eax  ; removing arg: test
	; Exit program
	mov ebx, dword [esp + 4]  ; load arg LetScopeDescriptor(input 3:23) = stack index 0
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
section .data
	sd_0 dd 0  ; int16 t at 0:15
	sd_1 dd 0  ; int16 fabrice at 1:15
