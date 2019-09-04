import java.io.*;
import minipython.lexer.Lexer;
import minipython.parser.Parser;
import minipython.node.*;
import java.util.*;

public class ParserTest
{
  public static void main(String[] args)
  {
    try
    {
      Parser parser =
        new Parser(
        new Lexer(
        new PushbackReader(
        new FileReader(args[0].toString()), 1024)));

     Hashtable symtable =  new Hashtable();
     Start ast = parser.parse();
     ast.apply(new myvisitor(symtable));
     // Gia ton deutero visitor grapste thn entolh
      try{ast.apply(new myvisitor2(symtable));}catch(Exception e) {e.printStackTrace();}
      
    }
    catch (Exception e)
    {
     e.printStackTrace();
    }
  }
}

