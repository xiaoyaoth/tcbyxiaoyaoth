package Parse;
import ErrorMsg.ErrorMsg;

%% 

%implements Lexer
%function nextToken
%type java_cup.runtime.Symbol
%char
%line
%state COMMENT
%state STRING
%state STRING_SPECIAL

ALPHA=[A-Za-z]
DIGIT=[0-9]
NONNEWLINE_WHITE_SPACE_CHAR=[\ \t\b\012]
WHITE_SPACE_CHAR=[{LINETERMINATOR}|\ \t\b\012]
LINETERMINATOR = [\r|\n|\r\n]
MEANINGCHANGE = [@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_]
COMMENT_TEXT=([^/*\n]|[^*\n]"/"[^*\n]|[^/\n]"*"[^/\n]|"*"[^/\n]|"/"[^*\n])*


%{
private void newline() {
  errorMsg.newline(yychar);
}

private void err(int pos, String s) {
  errorMsg.error(pos,s);
}

private void err(String s) {
  err(yychar,s);
}

private java_cup.runtime.Symbol tok(int kind, Object value) {
    return new java_cup.runtime.Symbol(kind, yychar, yychar+yylength(), value);
}

private java_cup.runtime.Symbol tok(int kind) {
    return tok(kind, null);
}

private java_cup.runtime.Symbol tok(int kind, Object value, int pos) {
    return new java_cup.runtime.Symbol(kind, pos, yychar+yylength(), value);
}


private ErrorMsg errorMsg;

private int comment_count = 0;
private int string_count = 0;
private String temp = "";
private int string_pos = 0;

Yylex(java.io.InputStream s, ErrorMsg e) {
  this(s);
  errorMsg=e;
}

%}

%eofval{
	{
	 if (comment_count!=0) err("comment symbol don't match ! ");
	 if (string_count!=0) err("string presentation error ! ");
	 return tok(sym.EOF, null);
        }
%eofval}       


%%
<YYINITIAL> " "	{}
<YYINITIAL,COMMENT> \r\n {newline();}
<YYINITIAL,COMMENT> \n {newline();}
<YYINITIAL> ","	{return tok(sym.COMMA, null);}
<YYINITIAL> ":" {return tok(sym.COLON);}
<YYINITIAL> ";" {return tok(sym.SEMICOLON);}
<YYINITIAL> "(" {return tok(sym.LPAREN);}
<YYINITIAL> ")" {return tok(sym.RPAREN);}
<YYINITIAL> "[" {return tok(sym.LBRACK);}
<YYINITIAL> "]" {return tok(sym.RBRACK);}
<YYINITIAL> "{" {return tok(sym.LBRACE);}
<YYINITIAL> "}" {return tok(sym.RBRACE);}
<YYINITIAL> "." {return tok(sym.DOT);}
<YYINITIAL> "+" {return tok(sym.PLUS);}
<YYINITIAL> "-" {return tok(sym.MINUS);}
<YYINITIAL> "*" {return tok(sym.TIMES);}
<YYINITIAL> "/" {return tok(sym.DIVIDE);}
<YYINITIAL> "=" {return tok(sym.EQ);}
<YYINITIAL> "<>" {return tok(sym.NEQ);}
<YYINITIAL> "<"  {return tok(sym.LT);}
<YYINITIAL> "<=" {return tok(sym.LE);}
<YYINITIAL> ">"  {return tok(sym.GT);}
<YYINITIAL> ">=" {return tok(sym.GE);}
<YYINITIAL> "&"  {return tok(sym.AND);}
<YYINITIAL> "|"  {return tok(sym.OR);}
<YYINITIAL> ":=" {return tok(sym.ASSIGN);}
<YYINITIAL> "array" {return tok(sym.ARRAY);}
<YYINITIAL> "break" {return tok(sym.BREAK);}
<YYINITIAL> "do" {return tok(sym.DO);}
<YYINITIAL> "else" {return tok(sym.ELSE);}
<YYINITIAL> "end" {return tok(sym.END);}
<YYINITIAL> "for" {return tok(sym.FOR);}
<YYINITIAL> "function" {return tok(sym.FUNCTION);}
<YYINITIAL> "if" {return tok(sym.IF);}
<YYINITIAL> "in" {return tok(sym.IN);}
<YYINITIAL> "let" {return tok(sym.LET);}
<YYINITIAL> "nil" {return tok(sym.NIL);}
<YYINITIAL> "of" {return tok(sym.OF);}
<YYINITIAL> "then" {return tok(sym.THEN);}
<YYINITIAL> "to" {return tok(sym.TO);}
<YYINITIAL> "type" {return tok(sym.TYPE);}
<YYINITIAL> "var" {return tok(sym.VAR);}
<YYINITIAL> "while" {return tok(sym.WHILE);}

<YYINITIAL> {NONNEWLINE_WHITE_SPACE_CHAR}+ { }

<YYINITIAL> "/*" { yybegin(COMMENT); comment_count = comment_count + 1; }

<COMMENT> "/*" { comment_count = comment_count + 1; }
<COMMENT> "*/" { 
	comment_count = comment_count - 1; 
	errorMsg.Assert(comment_count >= 0);
	if (comment_count == 0) {
    		yybegin(YYINITIAL);
	}
}

<COMMENT> {COMMENT_TEXT} { }

<YYINITIAL> {DIGIT}+ { 
	return tok(sym.INT,new Integer(yytext()));
}	

<YYINITIAL> {DIGIT}+"e"[+|-]{DIGIT}+ {
	return tok(sym.INT, new Double(yytext()));
}

<YYINITIAL> {ALPHA}({ALPHA}|{DIGIT}|_)* {
	return tok(sym.ID,yytext());
}	



<STRING> \\{DIGIT}{DIGIT}{DIGIT} {
    String str =  yytext().substring(1,yytext().length());
	int ascii = Integer.parseInt(str);
	Character ch = new Character((char)ascii);
	String str1 = ch.toString();
	if(ascii<256 && ascii> 0 ){
		temp = temp + str1;}
	else
		err(yychar,"not ascii");
}

<YYINITIAL> \\"^"{MEANINGCHANGE} {
	String str = yytext().substring(2, yytext().length());
	
}



<YYINITIAL> \" {
	System.out.println("begin string")
	temp = "";
	string_pos = yychar;
	yybegin(STRING);
}

<STRING> \" {
	string_count--;
	yybegin(YYINITIAL);
	return tok(sym.STRING, temp, string_pos);
}

<STRING> [^\n\r\"\\]+ {
	temp = temp+yytext();}
<STRING> \\t {
	temp = temp+yytext();
}
<STRING> \\n {
	temp = temp+yytext();}
<STRING> \\r {
	temp = temp+yytext();}
<STRING> \\\" {temp = temp+yytext();}
<STRING> \\\\ {temp = temp+yytext();}
<STRING> \\ {yybegin(STRING_SPECIAL);}
<STRING> \r\n {}

<STRING_SPECIAL> WHITE_SPACE_CHAR {}
<STRING_SPECIAL> \\ {yybegin(STRING);}
<STRING_SPECIAL> \" {err("should not match here!");}
<STRING_SPECIAL> [^] {temp = temp+yytext();}
	
<YYINITIAL,COMMENT,STRING,STRING_SPECIAL> . {
        System.out.println("Illegal character: <" + yytext() + ">");
}
