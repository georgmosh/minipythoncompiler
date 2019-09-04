import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class myvisitor2 extends DepthFirstAdapter
{
	private Hashtable symtable;
	private int lineNumber;
	private String variableName;
	private String methodName;

	myvisitor2(Hashtable symtable)
	{
		this.symtable = symtable;
	}

	//Edw kanoume return giati den yparxei logos na mpei i anadromi kai na elegksei
	//afou o elegxos tis dilwsis ginetai ston prwto adapter.
	public void caseAFunction(AFunction node) {
		return;
	}

	//Edw ksekinaei to prwto kommati tis shmasiologikhs analyshs twn synarthsewn
	//Se ayto to shmeio, eisagoyme sto symbol table tis parametrous mazi me tis times toys,
	//poy tis phrame apo tin klhsh sto programma. Afoy exoymt rimes,
	//mporoume na broyme ton typo toys. Telos, kanoyme mia anadromi sthn return statement tis apothikeumenis
	//dilwsis synartisis gia na anakalypsoyme ton typo tis synartisis.
	public void inAFunctionCall(AFunctionCall node) {
		methodName = node.getId().toString().trim().replaceAll(" ", "");
		lineNumber = ((TId) node.getId()).getLine();

		if(!FunctionsDAO.containsFunc(methodName, node)) error(ErrorType.FUNCTION_NOT_DEFINED);

		LinkedList parameters = node.getExpression();
		AFunction f = FunctionsDAO.HowManyUseArguments(node, methodName);
		LinkedList arguments = f.getArgument();

		Iterator paramI = parameters.iterator();
		Iterator argI = arguments.iterator();

		while(argI.hasNext()) {
			PArgument arg = (PArgument) argI.next();
			PExpression param = null;
			PValue aargVal = null;
			String argName = "";
			String paramType = "";

			if(arg instanceof ASimpleArgumentArgument) {
				ASimpleArgumentArgument argsim = (ASimpleArgumentArgument) arg;
				argName =  argsim.getId().toString().trim().replaceAll(" ", "");
				param = (PExpression) paramI.next();
				paramType = (String) getType(param);
			} else if(arg instanceof AArgWithValArgument){
				AArgWithValArgument argval = (AArgWithValArgument) arg;
				argName = argval.getId().toString().trim().replaceAll(" ", "");
				aargVal =  argval.getValue();
				paramType = getType(aargVal);
			}

			symtable.put(argName, paramType);
		}

		f.getStatement().apply(this);
	}

	//Xari stin anadromiki synarthsh poy kalesame parapanw eimaste se thesi na kseroyme
	//ton typo tis synartisis.
	public void outAFunctionCall(AFunctionCall node) {
		String name = node.getId().toString().trim().replaceAll(" ", "");

		AFunction function = FunctionsDAO.HowManyUseArguments(node, name);
		String functionType = "";

		if(!(function.getStatement() instanceof AReturnStatement)) {
			functionType = "void";
		} else {
			functionType = (String) getOut(function.getStatement());
		}

		setOut(node, functionType);

	}

	public void outAValueExpression(AValueExpression node)
    {
		String type = getType(node);
		setOut(node, type);
	}

	public void outAFunccallExpression(AFunccallExpression node)
    {
		String expressionType = (String) getOut(node.getFunctionCall());
		setOut(node, expressionType);
    }

	public void outAIdentifierExpression(AIdentifierExpression node)
    {
		String idName = node.getId().toString().trim().replaceAll(" ", "");
		String type = (String) symtable.get(idName);
		this.variableName = idName;

		if(type == null) {
			error(ErrorType.VARIABLE_NOT_DEFINED);
		}
		setOut(node, type);
	}

	public void outAAdditionExpression(AAdditionExpression node)
    {
		String left = (String) getOut(node.getLExp());
		String right = (String) getOut(node.getRExp());
		if(!(checkTypesForAddition(left, right))) {
			error(ErrorType.ADDITION_WITH_DIFFERENT_TYPES);
		} else {
			setOut(node, getOut(node.getLExp()));
			setOut(node.getLExp(), null);
			setOut(node.getRExp(), null);
		}
	}

	private boolean checkTypesForAddition(String left, String right) {
		if(left.equalsIgnoreCase("integer")) {
			if(!(left.equalsIgnoreCase(right) || right.equalsIgnoreCase("decimal"))) {
				return false;
			} else {
				return true;
			}
		} else if(left.equalsIgnoreCase("string")) {
			if(!left.equalsIgnoreCase(right)) {
				return false;
			} else {
				return true;
			}
		} else if(left.equalsIgnoreCase("decimal")) {
			if(!(left.equalsIgnoreCase(right) || right.equalsIgnoreCase("integer"))) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public void outASubstractionExpression(ASubstractionExpression node)
    {
		String left = (String) getOut(node.getLExp());
		String right = (String) getOut(node.getRExp());
		if(!(left.equalsIgnoreCase("integer") || left.equalsIgnoreCase("decimal")) ||
			!(right.equalsIgnoreCase("integer") || right.equalsIgnoreCase("decimal"))) {
			error(ErrorType.NON_INTEGER_DECIMAL_SUBSTRACTION);
		} else {
			setOut(node, getOut(node.getLExp()));
			setOut(node.getLExp(), null);
			setOut(node.getRExp(), null);
		}
	}

	public void outAMultiplicationExpression(AMultiplicationExpression node)
    {
		String left = (String) getOut(node.getLExp());
		String right = (String) getOut(node.getRExp());
		if(!(left.equalsIgnoreCase("integer") || left.equalsIgnoreCase("decimal")) ||
			!(right.equalsIgnoreCase("integer") || right.equalsIgnoreCase("decimal"))) {
			error(ErrorType.NON_INTEGER_DECIMAL_MULTIPLICATION);
		} else {
			setOut(node, left);
			setOut(node.getLExp(), null);
			setOut(node.getRExp(), null);
		}
	}

	public void outADivisionExpression(ADivisionExpression node)
    {
		String left = (String) getOut(node.getLExp());
		String right = (String) getOut(node.getRExp());
		if(!(left.equalsIgnoreCase("integer") || left.equalsIgnoreCase("decimal")) ||
			!(right.equalsIgnoreCase("integer") || right.equalsIgnoreCase("decimal"))) {
			error(ErrorType.NON_INTEGER_DECIMAL_DIVISION);
		} else {
			setOut(node, getOut(node.getLExp()));
			setOut(node.getLExp(), null);
			setOut(node.getRExp(), null);
		}
	}

	public void outAExponentiationExpression(AExponentiationExpression node)
    {
		String left = (String) getOut(node.getLExp());
		String right = (String) getOut(node.getRExp());
		if(!(left.equalsIgnoreCase("integer") || left.equalsIgnoreCase("decimal")) ||
			!(right.equalsIgnoreCase("integer") || right.equalsIgnoreCase("decimal"))) {
			error(ErrorType.NON_INTEGER_DECIMAL_EXPONENTIATION);
		} else {
			setOut(node, getOut(node.getLExp()));
			setOut(node.getLExp(), null);
			setOut(node.getRExp(), null);
		}
	}

	public void outAReturnStatement(AReturnStatement node)
    {
		String expressionType = (String) getOut(node.getExpression());
		setOut(node, expressionType);
	}

	private String getType(PExpression e) {
		if(e instanceof AValueExpression) {
			AValueExpression a = ((AValueExpression) e);
			if(a.getValue() instanceof ANumberValue) {
				return "Integer";
			} else if(a.getValue() instanceof AStringValue) {
				return "String";
			} else if(a.getValue() instanceof ADecimalValue) {
				return "decimal";
			} else {
				return "Characters";
			}
		} else if(e instanceof AIdentifierExpression) {
			AIdentifierExpression id = (AIdentifierExpression) e;
			String idName = id.getId().toString().trim().replaceAll(" ", "");

			if(symtable.containsKey(idName)) {
				return (String) symtable.get(idName);
			} else {
				error(ErrorType.VARIABLE_NOT_DEFINED);
			}
		} else if(e instanceof AAdditionExpression) {
			AAdditionExpression expadd = (AAdditionExpression)e;
			PExpression expleft = expadd.getLExp(), expright = expadd.getRExp();
			String left = getType(expleft), right = getType(expright);
			return left;
		} else if(e instanceof ASubstractionExpression) {
			ASubstractionExpression expsub = (ASubstractionExpression)e;
			PExpression expleft = expsub.getLExp(), expright = expsub.getRExp();
			String left = getType(expleft), right = getType(expright);
			if(left.equalsIgnoreCase("decimal") || right.equalsIgnoreCase("decimal"))
				return "decimal";
			else return left;
		} else if(e instanceof AMultiplicationExpression) {
			AMultiplicationExpression expmul = (AMultiplicationExpression)e;
			PExpression expleft = expmul.getLExp(), expright = expmul.getRExp();
			String left = getType(expleft), right = getType(expright);
			if(left.equalsIgnoreCase("decimal") || right.equalsIgnoreCase("decimal"))
				return "decimal";
			else return left;
		} else if(e instanceof ADivisionExpression) {
			ADivisionExpression expdiv = (ADivisionExpression)e;
			PExpression expleft = expdiv.getLExp(), expright = expdiv.getRExp();
			String left = getType(expleft), right = getType(expright);
			if(left.equalsIgnoreCase("decimal") || right.equalsIgnoreCase("decimal"))
				return "decimal";
			else return left;
		} else if(e instanceof AExponentiationExpression) {
			AExponentiationExpression expexp = (AExponentiationExpression)e;
			PExpression expleft = expexp.getLExp(), expright = expexp.getRExp();
			String left = getType(expleft), right = getType(expright);
			if(left.equalsIgnoreCase("decimal") || right.equalsIgnoreCase("decimal"))
				return "decimal";
			else return left;
		} else if (e instanceof AFunccallExpression) {
			AFunccallExpression expfce = (AFunccallExpression)e; expfce.apply(this);
			String funID = (expfce.toString().trim().replaceAll(" +", " ").split(" "))[0];
			AFunctionCall expfc = (AFunctionCall)expfce.getFunctionCall();
			AFunction expfun = FunctionsDAO.HowManyUseArguments(expfc, funID);
			PStatement expps = expfun.getStatement();
			if(!(expps instanceof AReturnStatement)) this.error(ErrorType.FUNCTION_NOT_RETURNS);
			else {
				AReturnStatement expres = (AReturnStatement)expps;
				PExpression expret = expres.getExpression();
				return getType(expret);
			}
		} else {
			System.out.println("Not permitted argument of: " + e.getClass());
			error(ErrorType.ARGUMENT_NOT_PERMITTED);
		}
		return "";
	}

	private String getType(PValue v) {
		if(v instanceof ANumberValue) {
			return "Integer";
		} else if(v instanceof AStringValue) {
			return "String";
		} else if(v instanceof ADecimalValue) {
			return "decimal";
		} else {
			return "Characters";
		}
	}


	private void error(ErrorType error) {
        switch(error) {
			case ADDITION_WITH_DIFFERENT_TYPES:
				System.err.printf("Error in line %d: Addition is allowed only for numbers or strings!\n",
					this.lineNumber + 1);
				System.exit(1);
				break;
          /*  case VARIABLE_DEFINED:
                System.err.printf("Variable %s in line: %d already defined\n",
                    this.variableName, this.lineNumber + 1);
                System.exit(1);
				break; */
			case VARIABLE_NOT_DEFINED:
				System.err.printf("Variable %s in line: %d is not defined and can't be used here!\n",
					this.variableName, this.lineNumber + 1);
				System.exit(1);
				break;
            case NON_INTEGER_DECIMAL_SUBSTRACTION:
            case NON_INTEGER_DECIMAL_MULTIPLICATION:
            case NON_INTEGER_DECIMAL_DIVISION:
                System.err.printf("Error in line: %d Substraction/Multiplication/Division is only permitted for decimals or integers\n",
                    this.lineNumber + 1);
                System.exit(1);
                break;
            case FUNCTION_DEFINED:
                System.err.printf("Method %s in line: %d already defined\n",
                    this.methodName, this.lineNumber + 1);
                System.exit(1);
                break;
			case FUNCTION_NOT_DEFINED:
				System.out.println("Line " + this.lineNumber + ": " +" Function " + this.methodName +" is not defined and can't be used");
				System.exit(1);
				break;
			case FUNCTION_NOT_RETURNS:
			System.out.println("Line " + this.lineNumber + ": " +" Function " + this.methodName +" does not return something");
			System.exit(1);
			break;
			case ARGUMENT_NOT_PERMITTED:
				System.exit(1);
				break;
        }
    }
}
