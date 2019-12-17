grammar MyLange;

Identifier:Nondigit
(Nondigit|LangDigit)*;
fragment
Nondigit:[a-zA-Z_];
LangDigit:TDigit
(HexdecimalDigit)*;
fragment
TDigit:[0-9];
fragment
HexdecimalConstant:HexdecimalDigit+;
fragment
HexdecimalDigit:[0-9a-fA-F];
WhiteSpace
    :
    [ \t\r\n]+
    ->skip
    ;

INCR:Identifier'++';
DECR:Identifier'--';
MUL:'*';
PRINT:'print';
Terminal:';';
LeftParen : '(';
RightParen : ')';
MOR:'>';
LESS:'<';
EQUAL:'=';
DO:'do';
WHILE:'while';

MultiplieStatement:Identifier
                    MUL
                    (Identifier|LangDigit);

ConditionalExpression:LeftParen
                       (Identifier|LangDigit)
                       MOR|LESS|EQUAL
                       (Identifier|LangDigit)
                       RightParen;

Statement:Identifier Terminal|PRINT Identifier Terminal|CicleStatement Terminal|MultiplieStatement Terminal;

CicleStatement:DO   Terminal
                       Statement+
                        WHILE
                         ConditionalExpression;
Program:Statement*;
