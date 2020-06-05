Introduction:

[1].Implemented Functions

(1) There are many buttons:

*In Assembly Simulator

Load:	Load the 01 machine code in Code TextArea to the memory.
	Note:Please press this button before run or step the code.
	         if the code is invalid ,the wrong message will appear in Message TextArea.
Step:	Execute one instruction
Run:	Execute all instructions.
	Note:these two buttons will run the code in memory.
Editor:	Open the Assembly Editor.
File:	Open file chooser and can load the content of file you choose to code window.
Reset:	Reset the pc,mem,registers with 0.
Reach:	You can enter the memory address in "Memory to reach:" TextField and then press the "Reach" button
           	ScrollBar in Memory TableView will automatically scroll to the memory you entered to let you see it and
           	"Value in Mem:"TextField will show the value in that address.
            	Please enter the memory address like this :0x00000044.
             	if the input is invalid ,the wrong message will appear in Message TextArea.

*In Assembly Editor

File:	the same as the button in Assembly Simulator.
Compile and Load: 	Compile the assembly language to the 01 machine code.
		If successfully,the 01 machine code will be loaded to the code window in Assembly Simulator.
		If it fails,the wrong message will appear in Message TextArea and it will point out the wrong line.

(2)If you click on the items in Tableview ,some messages will appear in Message TextArea.

(3)Message TextArea will show the messages about your operation

(4)The two TableView timely show the changes of memory and registers.

(5)PC represents the pc value at that time.

(6)Ins Running represents the instruction which has run just now.

[2].Other Message

(1) Because MIPS has a large number of instructions and my time is limited, my Assembly Simulator only choose six main instructions( add,addi,lw,sw,beq,j).Other instructions are not supported.
     
(2) The program starts from 0x0000000 by default.

(3)For debugging convenience,the actual available memory is from 0x0000000 to 0x000003fc.
     And In fact,I have tried the Math.pow(2,32) memory ,which successfully cause my computer to crash.

(4) In Assembly Editor,data segment starts with .data and code segment starts with .text ,which is similar as MIPS.
     And all immediate numbers and numbers in data segment can be represnted like 0b11,0x8000,12,-8.
     And nagative number is also supported.
     Some Notes is that .data and .text must have one row exclusive and datas should follow the .word

(5) Some test samples are provided.Try making some changes to test correctness and robustness.
     Both in two code window some valid blank space is allowed.And some valid input will be discovered and wrong message can be seen in Message TextArea.