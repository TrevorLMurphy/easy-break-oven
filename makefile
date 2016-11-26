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
	cat features/error1.ebo
error1x:
	./dpl features/error1.ebo

error2:
	cat features/error2.ebo
error2x:
	./dpl features/error2.ebo

error3:
	cat features/error3.ebo
error3x:
	./dpl features/error3.ebo

arrays:
	cat features/arrays.ebo
arraysx:
	./dpl features/arrays.ebo

conditionals:
	cat features/conditionals.ebo
conditionalsx:
	./dpl features/conditionals.ebo

operators:
	cat features/operators.ebo
operatorsx:
	./dpl features/operators.ebo

recursion:
	cat features/recursion.ebo
recursionx:
	./dpl features/recursion.ebo

iteration:
	cat features/iteration.ebo
iterationx:
	./dpl features/iteration.ebo

functions:
	cat features/functions.ebo
functionsx:
	./dpl features/functions.ebo

dictionary:
	cat features/dictionary.ebo
dictionaryx:
	./dpl features/dictionary.ebo

problem:
	cat features/problem.ebo
execute = ./dpl features/problem.ebo
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
