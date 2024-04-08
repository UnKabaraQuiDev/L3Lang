section .text
	global _start
_start:
	; Setup static: t -> var_1
	mov ebx, 2
	mov ebx, 3
	mov eax, 2
	imul eax, ebx
	mov eax, eax
	imul eax, ebx
	mov ebx, eax
	mov eax, 3
	add eax, ebx
	mov dword [var_1], eax
	; Setup static: fabrice -> var_2
	mov eax, 2
	imul eax, ebx
	mov dword [var_2], eax
	; Exit program
	mov ebx, dword [var_1]
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
section .data
	var_1 dd 0  ; t16 t at 0:14
	var_2 dd 0  ; t16 fabrice at 1:14
