/* Generated By:JavaCC: Do not edit this line. OQLExprParserTokenManager.java */
package org.shaolin.javacc.sql.parser;

public class OQLExprParserTokenManager implements OQLExprParserConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x70000L) != 0L)
         {
            jjmatchedKind = 26;
            return 15;
         }
         if ((active0 & 0x100000000L) != 0L)
            return 4;
         return -1;
      case 1:
         if ((active0 & 0x50000L) != 0L)
         {
            jjmatchedKind = 26;
            jjmatchedPos = 1;
            return 15;
         }
         if ((active0 & 0x20000L) != 0L)
            return 15;
         return -1;
      case 2:
         if ((active0 & 0x50000L) != 0L)
         {
            jjmatchedKind = 26;
            jjmatchedPos = 2;
            return 15;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 33:
         return jjMoveStringLiteralDfa1_0(0x8000L);
      case 40:
         return jjStopAtPos(0, 29);
      case 41:
         return jjStopAtPos(0, 30);
      case 42:
         return jjStopAtPos(0, 7);
      case 43:
         return jjStopAtPos(0, 5);
      case 45:
         return jjStopAtPos(0, 6);
      case 46:
         return jjStartNfaWithStates_0(0, 32, 4);
      case 47:
         return jjStopAtPos(0, 8);
      case 58:
         return jjStopAtPos(0, 31);
      case 60:
         jjmatchedKind = 12;
         return jjMoveStringLiteralDfa1_0(0x2000L);
      case 61:
         return jjStopAtPos(0, 14);
      case 62:
         jjmatchedKind = 10;
         return jjMoveStringLiteralDfa1_0(0x800L);
      case 64:
         return jjStopAtPos(0, 37);
      case 91:
         return jjStopAtPos(0, 33);
      case 93:
         return jjStopAtPos(0, 34);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x20000L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x10000L);
      case 110:
         return jjMoveStringLiteralDfa1_0(0x40000L);
      case 123:
         return jjStopAtPos(0, 35);
      case 124:
         return jjMoveStringLiteralDfa1_0(0x200L);
      case 125:
         return jjStopAtPos(0, 36);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 61:
         if ((active0 & 0x800L) != 0L)
            return jjStopAtPos(1, 11);
         else if ((active0 & 0x2000L) != 0L)
            return jjStopAtPos(1, 13);
         else if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(1, 15);
         break;
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x10000L);
      case 115:
         if ((active0 & 0x20000L) != 0L)
            return jjStartNfaWithStates_0(1, 17, 15);
         break;
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0x40000L);
      case 124:
         if ((active0 & 0x200L) != 0L)
            return jjStopAtPos(1, 9);
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 107:
         return jjMoveStringLiteralDfa3_0(active0, 0x10000L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(3, 16, 15);
         break;
      case 108:
         if ((active0 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(3, 18, 15);
         break;
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec2 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec3 = {
   0x1ff00000fffffffeL, 0xffffffffffffc000L, 0xffffffffL, 0x600000000000000L
};
static final long[] jjbitVec4 = {
   0x0L, 0x0L, 0x0L, 0xff7fffffff7fffffL
};
static final long[] jjbitVec5 = {
   0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec6 = {
   0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffL, 0x0L
};
static final long[] jjbitVec7 = {
   0xffffffffffffffffL, 0xffffffffffffffffL, 0x0L, 0x0L
};
static final long[] jjbitVec8 = {
   0x3fffffffffffL, 0x0L, 0x0L, 0x0L
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 35;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 6);
                  else if (curChar == 36)
                  {
                     if (kind > 26)
                        kind = 26;
                     jjCheckNAdd(15);
                  }
                  else if (curChar == 39)
                     jjCheckNAddStates(7, 9);
                  else if (curChar == 46)
                     jjCheckNAdd(4);
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 19)
                        kind = 19;
                     jjCheckNAddTwoStates(1, 2);
                  }
                  else if (curChar == 48)
                  {
                     if (kind > 19)
                        kind = 19;
                     jjCheckNAddStates(10, 12);
                  }
                  break;
               case 1:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddTwoStates(1, 2);
                  break;
               case 3:
                  if (curChar == 46)
                     jjCheckNAdd(4);
                  break;
               case 4:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 23)
                     kind = 23;
                  jjCheckNAddStates(13, 15);
                  break;
               case 6:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(7);
                  break;
               case 7:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 23)
                     kind = 23;
                  jjCheckNAddTwoStates(7, 8);
                  break;
               case 9:
               case 11:
                  if (curChar == 39)
                     jjCheckNAddStates(7, 9);
                  break;
               case 10:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAddStates(7, 9);
                  break;
               case 12:
                  if (curChar == 39)
                     jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 13:
                  if (curChar == 39 && kind > 25)
                     kind = 25;
                  break;
               case 14:
                  if (curChar != 36)
                     break;
                  if (kind > 26)
                     kind = 26;
                  jjCheckNAdd(15);
                  break;
               case 15:
                  if ((0x3ff001000000000L & l) == 0L)
                     break;
                  if (kind > 26)
                     kind = 26;
                  jjCheckNAdd(15);
                  break;
               case 16:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 6);
                  break;
               case 17:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(17, 18);
                  break;
               case 18:
                  if (curChar != 46)
                     break;
                  if (kind > 23)
                     kind = 23;
                  jjCheckNAddStates(16, 18);
                  break;
               case 19:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 23)
                     kind = 23;
                  jjCheckNAddStates(16, 18);
                  break;
               case 21:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(22);
                  break;
               case 22:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 23)
                     kind = 23;
                  jjCheckNAddTwoStates(22, 8);
                  break;
               case 23:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(23, 24);
                  break;
               case 25:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(26);
                  break;
               case 26:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 23)
                     kind = 23;
                  jjCheckNAddTwoStates(26, 8);
                  break;
               case 27:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(19, 21);
                  break;
               case 29:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(30);
                  break;
               case 30:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(30, 8);
                  break;
               case 31:
                  if (curChar != 48)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddStates(10, 12);
                  break;
               case 33:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddTwoStates(33, 2);
                  break;
               case 34:
                  if ((0xff000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddTwoStates(34, 2);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 15:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 26)
                     kind = 26;
                  jjCheckNAdd(15);
                  break;
               case 2:
                  if ((0x100000001000L & l) != 0L && kind > 19)
                     kind = 19;
                  break;
               case 5:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(22, 23);
                  break;
               case 8:
                  if ((0x5000000050L & l) != 0L && kind > 23)
                     kind = 23;
                  break;
               case 10:
                  jjAddStates(7, 9);
                  break;
               case 20:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(24, 25);
                  break;
               case 24:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(26, 27);
                  break;
               case 28:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(28, 29);
                  break;
               case 32:
                  if ((0x100000001000000L & l) != 0L)
                     jjCheckNAdd(33);
                  break;
               case 33:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddTwoStates(33, 2);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 15:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 26)
                     kind = 26;
                  jjCheckNAdd(15);
                  break;
               case 10:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjAddStates(7, 9);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 35 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   17, 18, 23, 24, 27, 28, 8, 10, 12, 13, 32, 34, 2, 4, 5, 8, 
   19, 20, 8, 27, 28, 8, 6, 7, 21, 22, 25, 26, 29, 30, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      default : 
         if ((jjbitVec0[i1] & l1) != 0L)
            return true;
         return false;
   }
}
private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec4[i2] & l2) != 0L);
      case 48:
         return ((jjbitVec5[i2] & l2) != 0L);
      case 49:
         return ((jjbitVec6[i2] & l2) != 0L);
      case 51:
         return ((jjbitVec7[i2] & l2) != 0L);
      case 61:
         return ((jjbitVec8[i2] & l2) != 0L);
      default : 
         if ((jjbitVec3[i1] & l1) != 0L)
            return true;
         return false;
   }
}
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, "\53", "\55", "\52", "\57", "\174\174", "\76", 
"\76\75", "\74", "\74\75", "\75", "\41\75", "\154\151\153\145", "\151\163", 
"\156\165\154\154", null, null, null, null, null, null, null, null, null, null, "\50", "\51", 
"\72", "\56", "\133", "\135", "\173", "\175", "\100", };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x3fe68fffe1L, 
};
static final long[] jjtoSkip = {
   0x1eL, 
};
protected JavaCharStream input_stream;
private final int[] jjrounds = new int[35];
private final int[] jjstateSet = new int[70];
protected char curChar;
public OQLExprParserTokenManager(JavaCharStream stream){
   if (JavaCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public OQLExprParserTokenManager(JavaCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(JavaCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 35; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(JavaCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100003200L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
