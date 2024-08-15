grammar LsonParser;

array
    :
    ;

expr
    : B
    | B C
     | B C A
    ;
bc:B C;

A:'A';
B:'B';
C:'C';
