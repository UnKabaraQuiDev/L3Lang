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
	;  Printout
	mov eax, 4
	mov ebx, 1
	mov ecx, sd_2
	mov edx, sd_2_len
	int 0x80
	; Call: exit
	mov eax, 69  ; compileExprCompute 69
	push eax ; adding arg: code
	call sd_4  ; exit
	pop eax  ; removing arg: code
	; Return
	mov eax, 12  ; compileExprCompute 12
	ret
sd_4:  ; exit
	; Exit program
	mov ebx, [esp + 4]
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
	ret  ; Default return
section .data
	sd_0 dd 0  ; int16 t at 0:15
	sd_1 dd 0  ; int16 fabrice at 1:15
	sd_2 dd "Heloo world", 0  ; int16 text at 2:15
	sd_2_len equ $ - sd_2 ; int16 length text at 2:15
