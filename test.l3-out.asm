section .text
	global _start
_start:
	; Exit program
	mov eax, 1 ; Syscall exit
	mov ebx, 12 ; Syscall exit code
	int 0x80   ; Syscall call
section .data:
	var_1 dw 0  ; t16 t at 0:14
