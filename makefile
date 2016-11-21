# ALL
all: Lexeme.class Lexer.class Parser.class Environment.class Evaluator.class Main.class
	chmod +x dpl

# Compilation
Lexeme.class: src/Lexeme.java
	javac -d . -classpath . src/Lexeme.java

Lexer.class: src/Lexer.java
	javac -d . -classpath . src/Lexer.java

Parser.class: src/Parser.java
	javac -d . -classpath . src/Parser.java

Environment.class: src/Environment.java
	javac -d . -classpath . src/Environment.java

Evaluator.class: src/Evaluator.java
	javac -d . -classpath . src/Evaluator.java

Main.class: src/Main.java
	javac -d . -classpath . src/Main.java

# Remove class files
clean:
	rm -f *.class

# Tests
error1:
	cat features/error1.murph
error1x:
	./dpl features/error1.murph

error2:
	cat features/error2.murph
error2x:
	./dpl features/error2.murph

error3:
	cat features/error3.murph
error3x:
	./dpl features/error3.murph

arrays:
	cat features/arrays.murph
arraysx:
	./dpl features/arrays.murph

conditionals:
	cat features/conditionals.murph
conditionalsx:
	./dpl features/conditionals.murph

recursion:
	cat features/recursion.murph
recursionx:
	./dpl features/recursion.murph

iteration:
	cat features/iteration.murph
iterationx:
	./dpl features/iteration.murph

functions:
	cat features/functions.murph
functionsx:
	./dpl features/functions.murph

dictionary:
	cat features/dictionary.murph
dictionaryx:
	./dpl features/dictionary.murph

problem:
	cat features/problem.murph
execute = ./dpl features/problem.murph
problemx:
	cat features/testInput0 | $(execute)
problemx-1:
	cat features/testInput1 | $(execute)
problemx-2:
	cat features/testInput2 | $(execute)
problemx-3:
	cat features/testInput3 | $(execute)
problemx-4:
	cat features/testInput4 | $(execute)
