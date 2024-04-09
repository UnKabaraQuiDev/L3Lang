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
	; Call: printout
	call sd_4  ; printout
	; Call: exit
	mov eax, 69  ; compileExprCompute 69
	push eax ; adding arg: code
	call sd_3  ; exit
	pop eax  ; removing arg: code
	; Return
	mov eax, 12  ; compileExprCompute 12
	ret
sd_3:  ; exit
	; Exit program
	mov ebx, [esp + 4]
	mov eax, 1 ; Syscall exit
	int 0x80   ; Syscall call
	ret  ; Default return
sd_4:  ; printout
	; Prepare the arguments for the write system call
	mov eax, 4           ; System call number for write (syscall number)
	mov ebx, 1           ; File descriptor 1: stdout
	mov ecx, buffer      ; Pointer to the buffer to be printed
	mov edx, buffer_len  ; Length of the buffer in bytes
	int 0x80             ; Invoke the kernel
	ret  ; Default return
section .data
	sd_0 dd 0  ; int16 t at 0:15
	sd_1 dd 0  ; int16 fabrice at 1:15
	buffer db "Hello, World!", 0 ; Null terminated string buffer
	buffer_len equ $ - buffer      ; Calculate the length of the buffer
