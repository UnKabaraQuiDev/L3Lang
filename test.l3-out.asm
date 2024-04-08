section .text
	global _start
_start:
	; Setup static: t -> var_1
	mov ebx, 3
	mov eax, 2
	mov ebx, 3
	imul ebx, eax
	sub ebx, eax
	mov dword [var_1], ebx
	; Setup static: fabrice -> var_2
	mov ebx, 2
	mov eax, dword [var_1]
	imul ebx, eax
	mov dword [var_2], ebx
	; Exit program
	mov ebx, 2
	mov eax, dword [var_2]
	imul ebx, eax
	mov ebx, ebx
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
section .data
	var_1 dd 0  ; t16 t at 0:14
	var_2 dd 0  ; t16 fabrice at 1:14
