# Easy Break Oven
Easily broken.

## Types

There are three types in the Easy Break Oven language:

1. Strings
2. Integers
3. Booleans

Strings look like this: `"A really cool string"`

Integers look like this: `54`

Booleans look like this: `true ` or `false`

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

## Built-in Functions

1. print() - prints a value on the current line
2. println() - prints a value on the current line then adds a new line
3. length(object) - returns the length of a string or array.
4. add(array, itemadd an object to an array; objects get added to the back of the array.
5. remove(array, index) - remove an object

