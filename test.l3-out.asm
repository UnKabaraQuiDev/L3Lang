section .text
	global _start
_start:
	; Setup static: t -> var_1
	mov eax, 2
	mov ebx, 3
	imul eax, ebx ; Node@NumLitNode(2) * Node@NumLitNode(3) -> eax
	mov ebx, 2
	imul eax, ebx ; Node@BinaryOpNode(Node@NumLitNode(2)*Node@NumLitNode(3)) * Node@NumLitNode(2) -> ebx
	mov ebx, eax

	mov eax, 3
	add eax, ebx ; Node@NumLitNode(3) + Node@BinaryOpNode(Node@BinaryOpNode(Node@NumLitNode(2)*Node@NumLitNode(3))*Node@NumLitNode(2)) -> eax
	mov ebx, 3
	add eax, ebx ; Node@BinaryOpNode(Node@NumLitNode(3)+Node@BinaryOpNode(Node@BinaryOpNode(Node@NumLitNode(2)*Node@NumLitNode(3))*Node@NumLitNode(2))) + Node@NumLitNode(3) -> dword [var_1]
	mov dword [var_1], eax

	; Setup static: fabrice -> var_2
	mov eax, dword [var_1]
	mov ebx, 4
	idivl ebx ; Node@VarNumNode(t) % Node@NumLitNode(4) -> dword [var_2]
	mov dword [var_2], edx

	; Exit program
	mov eax, 18
	mov ebx, 4
	idivl ebx ; Node@NumLitNode(18) % Node@NumLitNode(4) -> ebx
	mov ebx, edx

	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
section .data
	var_1 dd 0  ; t16 t at 0:14
	var_2 dd 0  ; t16 fabrice at 1:14
