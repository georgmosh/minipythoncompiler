import minipython.analysis.*;
import minipython.node.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 *  FunctionsDAO.java
 *  This class illustrates a sample data base object implementation for Functions.
 *  @authors Georgios M. Moschovis (p3150113@aueb.gr), [fill in your names]
 */
public class FunctionsDAO {
    /*
     *  Local variables.
     */
    private static HashMap<String, ArrayList<AFunction>> functions;

    /**
     *  Static constructor for data container.
     */
    public static void initialize() {
        functions = new HashMap<String, ArrayList<AFunction>>();
    }

    /**
     *  Container accessor for adding function data.
     *  @param functionName The imported function name.
     *  @param node The imported function details.
     */
    public static boolean addAFunction(String functionName, AFunction node) {
        if(functions.containsKey(functionName)) {
          if(HowManyArguments(node, functionName)) return false;
          ArrayList<AFunction> functionList = functions.get(functionName);
  		  functionList.add(node);
  		  return true;
        }
        ArrayList<AFunction> functionList = new ArrayList<AFunction>();
        functionList.add(node);
        functions.put(functionName, functionList);
        return true;
    }

	private static boolean HowManyArguments(AFunction node, String functionName) {
		ArrayList<AFunction> functionList = functions.get(functionName);
		for(AFunction function: functionList) {
			if(function.getArgument().size() == node.getArgument().size()) return true;
		}
		LinkedList argumentlist = node.getArgument();
		int nonDefaultArgument = 0;
		for(int i = 0; i < argumentlist.size(); i++) {
            PArgument arg = (PArgument) argumentlist.get(i);
			if(arg instanceof ASimpleArgumentArgument) nonDefaultArgument++;
		}
		for(AFunction function: functionList) {
			if(nonDefaultArgument == function.getArgument().size()) return true;
		}
		return false;
	}

	public static boolean containsFunc(String fname, AFunctionCall node) {
		if(!functions.containsKey(fname)) return false;
		return HowManyCallArguments(node, fname);
	}

	private static boolean HowManyCallArguments(AFunctionCall node, String functionName) {
		int number = node.getExpression().size();
		ArrayList<AFunction> functionList = functions.get(functionName);
		for(AFunction function: functionList) {
			if(function.getArgument().size() == number) return true;
		}
		LinkedList parameterlist = (LinkedList)node.getExpression();
		for(AFunction function: functionList) {
			int nonDefaultArgument = 0;
			LinkedList argumentlist = function.getArgument();
			for(int i = 0; i < argumentlist.size(); i++) {
				PArgument arg = (PArgument) argumentlist.get(i);
				if(arg instanceof ASimpleArgumentArgument) nonDefaultArgument++;
			}
			if(nonDefaultArgument == number) return true;
		}
		return false;
	}

	protected static AFunction HowManyUseArguments(AFunctionCall node, String functionName) {
		int number = node.getExpression().size();
		ArrayList<AFunction> functionList = functions.get(functionName);
		for(AFunction function: functionList) {
			if(function.getArgument().size() == number) return function;
		}
		LinkedList parameterlist = (LinkedList)node.getExpression();
		for(AFunction function: functionList) {
			int nonDefaultArgument = 0;
			LinkedList argumentlist = function.getArgument();
			for(int i = 0; i < argumentlist.size(); i++) {
				PArgument arg = (PArgument) argumentlist.get(i);
				if(arg instanceof ASimpleArgumentArgument) nonDefaultArgument++;
			}
			if(nonDefaultArgument == number) return function;
		}
		return null;
	}

    /**
     *  Container accessor for getting function data.
     *  @return The functions' declaration information storage.
     */
    public HashMap<String, ArrayList<AFunction>> getFunctions() {
        return this.functions;
    }

    /**
     *  Container accessor for redirecting function data to System.out stream.
     */
    public static void outputFunctions() {
        Set<String> keys = functions.keySet();
        Iterator<String> thisFunction = keys.iterator();
        while(thisFunction.hasNext()) {
          String name = thisFunction.next().toString().trim().replaceAll(" ", "");
          ArrayList<AFunction> nextFunction = functions.get(name);
          System.out.println(name + ": " + nextFunction);
        }
    }

}
