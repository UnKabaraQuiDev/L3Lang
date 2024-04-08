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

	; Exit program
	mov eax, 18
	mov ebx, 4
	div ebx ; NumLitNode(18) % NumLitNode(4) -> ebx
	mov ebx, edx

	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
section .data
	sd_0 dd 0  ; t16 t at 0:14
	sd_1 dd 0  ; t16 fabrice at 1:14
