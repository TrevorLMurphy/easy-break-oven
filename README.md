# Easy Break Oven

Easily broken.

## Running Code

Executing `make all` will compile all the source files and allow you to run
source code.

A script has been provided that will run easy break oven source code.

Just run something like:

```
./dpl yourcode.ebo
```

.ebo is the file extension for the easy break oven language

## Types

There are four types in the Easy Break Oven language:

1. Strings
2. Integers
3. Booleans
4. null

Strings look like this: `"A really cool string"`

Integers look like this: `54`

Booleans look like this: `true ` or `false`

Null looks like this: `null`

## Variables

Variables are defined using the "let" keyword and must be assigned a value immediately:

```
let alpha = 67;
```

Then they can be assigned new values by using just their name:

```
alpha = alpha + 1;
```


## Functions

Functions are first class objects. They can be assigned to variables and manipulated.

An example of a function definition:

```
func newFunction() {
    println("HEY");
}
```

Using this function as a variable:

```
let alpha = newFunction;
alpha();
```

There is also support for anonymous functions:

```
func useLambda() {
    return func () {
        println("Oh baby it's a triple!");
    }
}
```

## Built-in Functions

1. print() - prints a value on the current line
2. println() - prints a value on the current line then adds a new line
3. length(object) - returns the length of a string or array
4. add(array, item) - add an object to an array; objects get added to the back of the array
5. remove(array, index) - remove an item from the index of an array

## Arrays

Arrays are defined like this:

```
let array = [43, "string", 23];
```

As you can see, an array can hold multiple types.

## Conditionals

Here's an example of a conditional block:

```
if (4 > 5) {
    println("Nope");
} else if (4 < 2) {
    println("Still nope");
} else {
    println("This will print!");
}
```

## Operators

#### Conditional Operators
1. '==' Check equality
2. '!=' Check non equality
3. '>' Greater than
4. '<' Less than
5. '>=' Greater than or equal to
6. '<=' Less than or equal to

#### Normal Operators
1. '+' Addition
2. '-' Subtraction
3. '*' Multiplication
4. '/' Division

*Keep in mind that if you string multiple operations together,
the result will be right-associative.*

## Yeah But Why Is It Called Easy Break Oven?

Think of any way to possibly break the language...you probably just broke it. Thanks.
