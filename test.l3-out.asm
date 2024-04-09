section .text
	global _start
_start:
	; Setup static: t -> sd_0
	mov eax, 2
	mov ebx, 3
	imul eax, ebx ; NumLitNode(2) * NumLitNode(3) -> eax
	mov ebx, 2
	imul eax, ebx ; BinaryOpNode(*) * NumLitNode(2) -> ebx
	mov ebx, eax

	mov eax, 3
	add eax, ebx ; NumLitNode(3) + BinaryOpNode(*) -> eax
	mov ebx, 3
	add eax, ebx ; BinaryOpNode(+) + NumLitNode(3) -> dword [sd_0]
	mov dword [sd_0], eax

	; Setup static: fabrice -> sd_1
	mov eax, dword [sd_0]
	mov ebx, 4
	div ebx ; VarNumNode(t) % NumLitNode(4) -> dword [sd_1]
	mov dword [sd_1], edx

	call sd_5  ; main

	; Exit program
	mov ebx, eax
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
sd_5:  ; main, is leaf: false
	mov eax, 13
	push eax
	call sd_3  ; FUINCTION
	mov eax, 12
	ret
sd_3:  ; FUINCTION, is leaf: false
	; Exit program
	mov eax, dword [esp + 4]
	mov ebx, dword [sd_1]
	add eax, ebx ; VarNumNode(input) + VarNumNode(fabrice) -> ebx
	mov ebx, eax

	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
section .data
	sd_0 dd 0  ; int16 t at 0:15
	sd_1 dd 0  ; int16 fabrice at 1:15
