package Parse;
import ErrorMsg.ErrorMsg;


class Yylex implements Lexer {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

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
private ErrorMsg errorMsg;
private int comment_count = 0;
StringBuffer string = new StringBuffer();
Yylex(java.io.InputStream s, ErrorMsg e) {
  this(s);
  errorMsg=e;
}
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int STRING = 2;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int STRING_SPECIAL = 3;
	private final int yy_state_dtrans[] = {
		0,
		52,
		88,
		92
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NOT_ACCEPT,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NOT_ACCEPT,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NOT_ACCEPT,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NOT_ACCEPT,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NOT_ACCEPT,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NOT_ACCEPT,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NOT_ACCEPT,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NOT_ACCEPT,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NOT_ACCEPT,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NOT_ACCEPT,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NOT_ACCEPT,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NOT_ACCEPT,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NOT_ACCEPT,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NOT_ACCEPT,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NOT_ACCEPT,
		/* 111 */ YY_NOT_ACCEPT,
		/* 112 */ YY_NOT_ACCEPT,
		/* 113 */ YY_NOT_ACCEPT,
		/* 114 */ YY_NOT_ACCEPT,
		/* 115 */ YY_NOT_ACCEPT,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"44:8,43:2,3,44:2,2,44:18,1,44,49,44:3,21,44,7,8,16,14,4,15,13,17,45:10,5,6," +
"19,18,20,44:2,57,46,58,46,54,46:2,51,52,46:6,56,46,59,55,53,46:2,50,46:3,9," +
"48,10,44,47,44,23,26,36,29,27,34,46,39,38,46,28,31,46,33,30,40,46,24,32,37," +
"35,41,42,46,25,46,11,22,12,44:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,139,
"0,1,2:2,1,3,1:11,4,1,5,6,1:2,7,2,1,8,1:7,9:13,1,9:4,10,1:2,11,1,12,1:11,13," +
"1,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,3" +
"8,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,6" +
"3,64,65,66,67,68,69,70,71,9,72,73,74,75,76,77,9,78,79")[0];

	private int yy_nxt[][] = unpackFromString(80,60,
"1,2,69,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,136:2,137,11" +
"6,136,71,76,117,136,118,119,136:2,80,84,136:2,120,138,24,25,26,136,25,72,27" +
",136:10,-1:61,24,-1,24,-1:39,24,-1:34,29,-1:57,30,-1:61,31,-1,32,-1:57,33,-" +
"1:64,136,128,136:18,-1:2,129,136,129,-1:2,136:10,-1:45,26,-1:37,136:20,-1:2" +
",129,136,129,-1:2,136:10,1,73,78,70,73:12,77,81,73:42,-1,55,-1:2,55:44,-1:2" +
",55:10,-1:24,60,-1:8,61,-1:3,62,-1:10,63,64,-1:13,28,-1:79,136:7,34,136:12," +
"-1:2,129,136,129,-1:2,136:10,-1:45,75,-1:15,73:2,-1,73:12,83,86,73:42,-1:51" +
",94,-1:53,79,-1:37,136:11,35,136:8,-1:2,129,136,129,-1:2,136:10,-1,73:2,-1," +
"73:12,82,53,73:42,-1,73:2,28,73:12,83,86,73:42,-1:45,47,-1:37,136:2,122,136" +
":4,36,136:8,123,136:3,-1:2,129,136,129,-1:2,136:10,-1,73:2,-1,73:12,54,85,7" +
"3:42,-1,73:2,-1,73:12,82,86,73:42,-1,73:2,-1,73:12,82,-1,73:42,-1:23,136:10" +
",37,38,136:8,-1:2,129,136,129,-1:2,136:10,-1,73:2,-1,73:12,83,85,73:42,-1,7" +
"3:2,-1,73:12,-1,85,73:42,-1:23,136:6,39,136:13,-1:2,129,136,129,-1:2,136:10" +
",1,55,90,56,55:44,57,58,55:10,-1:23,136:14,40,136:5,-1:2,129,136,129,-1:2,1" +
"36:10,-1:3,59,-1:79,136:8,41,136:11,-1:2,129,136,129,-1:2,136:10,1,65:47,66" +
",67,74,65:9,-1:23,136,42,136:18,-1:2,129,136,129,-1:2,136:10,-1:52,96,-1:30" +
",136,43,136:18,-1:2,129,136,129,-1:2,136:10,-1:53,98,-1:29,136:4,44,136:15," +
"-1:2,129,136,129,-1:2,136:10,-1:54,100,-1:28,136:4,45,136:15,-1:2,129,136,1" +
"29,-1:2,136:10,-1:47,102,-1:35,136:10,46,136:9,-1:2,129,136,129,-1:2,136:10" +
",-1:55,104,-1:27,136:2,48,136:17,-1:2,129,136,129,-1:2,136:10,-1:56,106,-1:" +
"26,136:5,49,136:14,-1:2,129,136,129,-1:2,136:10,-1:57,108,-1:25,136:4,50,13" +
"6:15,-1:2,129,136,129,-1:2,136:10,-1:58,110,-1:24,136:10,51,136:9,-1:2,129," +
"136,129,-1:2,136:10,-1:54,111,-1:52,112,-1:70,113,-1:52,114,-1:65,115,-1:61" +
",68,-1:23,136:8,121,136,87,136:9,-1:2,129,136,129,-1:2,136:10,-1:23,136:4,8" +
"9,136:15,-1:2,129,136,129,-1:2,136:10,-1:23,136:15,91,136:4,-1:2,129,136,12" +
"9,-1:2,136:10,-1:23,136:7,93,136:4,131,136:7,-1:2,129,136,129,-1:2,136:10,-" +
"1:23,95,136:19,-1:2,129,136,129,-1:2,136:10,-1:23,136:9,97,136:10,-1:2,129," +
"136,129,-1:2,136:10,-1:23,136:17,99,136:2,-1:2,129,136,129,-1:2,136:10,-1:2" +
"3,136:4,101,136:15,-1:2,129,136,129,-1:2,136:10,-1:23,103,136:19,-1:2,129,1" +
"36,129,-1:2,136:10,-1:23,105,136:19,-1:2,129,136,129,-1:2,136:10,-1:23,136:" +
"8,107,136:11,-1:2,129,136,129,-1:2,136:10,-1:23,136:7,109,136:12,-1:2,129,1" +
"36,129,-1:2,136:10,-1:23,136,124,136:18,-1:2,129,136,129,-1:2,136:10,-1:23," +
"136:4,125,136:15,-1:2,129,136,129,-1:2,136:10,-1:23,136:10,133,136:9,-1:2,1" +
"29,136,129,-1:2,136:10,-1:23,136:15,126,136:4,-1:2,129,136,129,-1:2,136:10," +
"-1:23,136:13,134,136:6,-1:2,129,136,129,-1:2,136:10,-1:23,136:14,135,136:5," +
"-1:2,129,136,129,-1:2,136:10,-1:23,136:15,127,136:4,-1:2,129,136,129,-1:2,1" +
"36:10,-1:23,136,130,136:18,-1:2,129,136,129,-1:2,136:10,-1:23,136:16,132,13" +
"6:3,-1:2,129,136,129,-1:2,136:10");

	public java_cup.runtime.Symbol nextToken ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

	{
	 if (yy_state == COMMENT) err("comment symbol don't match ! ");
	 if (yy_state == STRING) err("string presentation error ! ");
	 return tok(sym.EOF, null);
        }
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{}
					case -3:
						break;
					case 3:
						{newline();}
					case -4:
						break;
					case 4:
						{return tok(sym.COMMA, null);}
					case -5:
						break;
					case 5:
						{return tok(sym.COLON);}
					case -6:
						break;
					case 6:
						{return tok(sym.SEMICOLON);}
					case -7:
						break;
					case 7:
						{return tok(sym.LPAREN);}
					case -8:
						break;
					case 8:
						{return tok(sym.RPAREN);}
					case -9:
						break;
					case 9:
						{return tok(sym.LBRACK);}
					case -10:
						break;
					case 10:
						{return tok(sym.RBRACK);}
					case -11:
						break;
					case 11:
						{return tok(sym.LBRACE);}
					case -12:
						break;
					case 12:
						{return tok(sym.RBRACE);}
					case -13:
						break;
					case 13:
						{return tok(sym.DOT);}
					case -14:
						break;
					case 14:
						{return tok(sym.PLUS);}
					case -15:
						break;
					case 15:
						{return tok(sym.MINUS);}
					case -16:
						break;
					case 16:
						{return tok(sym.TIMES);}
					case -17:
						break;
					case 17:
						{return tok(sym.DIVIDE);}
					case -18:
						break;
					case 18:
						{return tok(sym.EQ);}
					case -19:
						break;
					case 19:
						{return tok(sym.LT);}
					case -20:
						break;
					case 20:
						{return tok(sym.GT);}
					case -21:
						break;
					case 21:
						{return tok(sym.AND);}
					case -22:
						break;
					case 22:
						{return tok(sym.OR);}
					case -23:
						break;
					case 23:
						{
	return tok(sym.ID,yytext());
}
					case -24:
						break;
					case 24:
						{ }
					case -25:
						break;
					case 25:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
}
					case -26:
						break;
					case 26:
						{ 
	return tok(sym.INT,new Integer(yytext()));
}
					case -27:
						break;
					case 27:
						{
	string.setLength(0);
	yybegin(STRING);
}
					case -28:
						break;
					case 28:
						{newline();}
					case -29:
						break;
					case 29:
						{return tok(sym.ASSIGN);}
					case -30:
						break;
					case 30:
						{ yybegin(COMMENT); comment_count = comment_count + 1; }
					case -31:
						break;
					case 31:
						{return tok(sym.LE);}
					case -32:
						break;
					case 32:
						{return tok(sym.NEQ);}
					case -33:
						break;
					case 33:
						{return tok(sym.GE);}
					case -34:
						break;
					case 34:
						{return tok(sym.DO);}
					case -35:
						break;
					case 35:
						{return tok(sym.OF);}
					case -36:
						break;
					case 36:
						{return tok(sym.TO);}
					case -37:
						break;
					case 37:
						{return tok(sym.IN);}
					case -38:
						break;
					case 38:
						{return tok(sym.IF);}
					case -39:
						break;
					case 39:
						{return tok(sym.END);}
					case -40:
						break;
					case 40:
						{return tok(sym.LET);}
					case -41:
						break;
					case 41:
						{return tok(sym.NIL);}
					case -42:
						break;
					case 42:
						{return tok(sym.FOR);}
					case -43:
						break;
					case 43:
						{return tok(sym.VAR);}
					case -44:
						break;
					case 44:
						{return tok(sym.ELSE);}
					case -45:
						break;
					case 45:
						{return tok(sym.TYPE);}
					case -46:
						break;
					case 46:
						{return tok(sym.THEN);}
					case -47:
						break;
					case 47:
						{
    String str =  yytext().substring(1,yytext().length());
	int ascii = Integer.parseInt(str);
	Character ch = new Character((char)ascii);
	String str1 = ch.toString();
	if(ascii<128){
		return tok(sym.STRING,new String(str1));}
	else
		err(yychar,"not ascii");
}
					case -48:
						break;
					case 48:
						{return tok(sym.ARRAY);}
					case -49:
						break;
					case 49:
						{return tok(sym.BREAK);}
					case -50:
						break;
					case 50:
						{return tok(sym.WHILE);}
					case -51:
						break;
					case 51:
						{return tok(sym.FUNCTION);}
					case -52:
						break;
					case 52:
						{ }
					case -53:
						break;
					case 53:
						{ 
	comment_count = comment_count - 1; 
	errorMsg.Assert(comment_count >= 0);
	if (comment_count == 0) {
    		yybegin(YYINITIAL);
	}
}
					case -54:
						break;
					case 54:
						{ comment_count = comment_count + 1; }
					case -55:
						break;
					case 55:
						{string.append(yytext());}
					case -56:
						break;
					case 56:
						{err("line terminate in wrong place!");}
					case -57:
						break;
					case 57:
						{yybegin(STRING_SPECIAL);}
					case -58:
						break;
					case 58:
						{yybegin(YYINITIAL);return tok(sym.STRING, string.toString());}
					case -59:
						break;
					case 59:
						{err("line terminate in wrong place!");}
					case -60:
						break;
					case 60:
						{string.append('\r');}
					case -61:
						break;
					case 61:
						{string.append('\n');}
					case -62:
						break;
					case 62:
						{string.append('\t');}
					case -63:
						break;
					case 63:
						{string.append('\\');}
					case -64:
						break;
					case 64:
						{string.append('\"');}
					case -65:
						break;
					case 65:
						{string.append(yytext());}
					case -66:
						break;
					case 66:
						{yybegin(STRING);}
					case -67:
						break;
					case 67:
						{err("should not match here!");}
					case -68:
						break;
					case 68:
						{}
					case -69:
						break;
					case 70:
						{newline();}
					case -70:
						break;
					case 71:
						{
	return tok(sym.ID,yytext());
}
					case -71:
						break;
					case 72:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
}
					case -72:
						break;
					case 73:
						{ }
					case -73:
						break;
					case 74:
						{string.append(yytext());}
					case -74:
						break;
					case 76:
						{
	return tok(sym.ID,yytext());
}
					case -75:
						break;
					case 77:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
}
					case -76:
						break;
					case 78:
						{ }
					case -77:
						break;
					case 80:
						{
	return tok(sym.ID,yytext());
}
					case -78:
						break;
					case 81:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
}
					case -79:
						break;
					case 82:
						{ }
					case -80:
						break;
					case 84:
						{
	return tok(sym.ID,yytext());
}
					case -81:
						break;
					case 85:
						{ }
					case -82:
						break;
					case 87:
						{
	return tok(sym.ID,yytext());
}
					case -83:
						break;
					case 89:
						{
	return tok(sym.ID,yytext());
}
					case -84:
						break;
					case 91:
						{
	return tok(sym.ID,yytext());
}
					case -85:
						break;
					case 93:
						{
	return tok(sym.ID,yytext());
}
					case -86:
						break;
					case 95:
						{
	return tok(sym.ID,yytext());
}
					case -87:
						break;
					case 97:
						{
	return tok(sym.ID,yytext());
}
					case -88:
						break;
					case 99:
						{
	return tok(sym.ID,yytext());
}
					case -89:
						break;
					case 101:
						{
	return tok(sym.ID,yytext());
}
					case -90:
						break;
					case 103:
						{
	return tok(sym.ID,yytext());
}
					case -91:
						break;
					case 105:
						{
	return tok(sym.ID,yytext());
}
					case -92:
						break;
					case 107:
						{
	return tok(sym.ID,yytext());
}
					case -93:
						break;
					case 109:
						{
	return tok(sym.ID,yytext());
}
					case -94:
						break;
					case 116:
						{
	return tok(sym.ID,yytext());
}
					case -95:
						break;
					case 117:
						{
	return tok(sym.ID,yytext());
}
					case -96:
						break;
					case 118:
						{
	return tok(sym.ID,yytext());
}
					case -97:
						break;
					case 119:
						{
	return tok(sym.ID,yytext());
}
					case -98:
						break;
					case 120:
						{
	return tok(sym.ID,yytext());
}
					case -99:
						break;
					case 121:
						{
	return tok(sym.ID,yytext());
}
					case -100:
						break;
					case 122:
						{
	return tok(sym.ID,yytext());
}
					case -101:
						break;
					case 123:
						{
	return tok(sym.ID,yytext());
}
					case -102:
						break;
					case 124:
						{
	return tok(sym.ID,yytext());
}
					case -103:
						break;
					case 125:
						{
	return tok(sym.ID,yytext());
}
					case -104:
						break;
					case 126:
						{
	return tok(sym.ID,yytext());
}
					case -105:
						break;
					case 127:
						{
	return tok(sym.ID,yytext());
}
					case -106:
						break;
					case 128:
						{
	return tok(sym.ID,yytext());
}
					case -107:
						break;
					case 129:
						{
	return tok(sym.ID,yytext());
}
					case -108:
						break;
					case 130:
						{
	return tok(sym.ID,yytext());
}
					case -109:
						break;
					case 131:
						{
	return tok(sym.ID,yytext());
}
					case -110:
						break;
					case 132:
						{
	return tok(sym.ID,yytext());
}
					case -111:
						break;
					case 133:
						{
	return tok(sym.ID,yytext());
}
					case -112:
						break;
					case 134:
						{
	return tok(sym.ID,yytext());
}
					case -113:
						break;
					case 135:
						{
	return tok(sym.ID,yytext());
}
					case -114:
						break;
					case 136:
						{
	return tok(sym.ID,yytext());
}
					case -115:
						break;
					case 137:
						{
	return tok(sym.ID,yytext());
}
					case -116:
						break;
					case 138:
						{
	return tok(sym.ID,yytext());
}
					case -117:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
