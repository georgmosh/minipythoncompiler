import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class myvisitor extends DepthFirstAdapter
{
	private Hashtable symtable;
	private int lineNumber;
	private String variableName;
	private String methodName;

	myvisitor(Hashtable symtable)
	{
		this.symtable = symtable;
		FunctionsDAO.initialize();
	}

	public void inAFunction(AFunction node){
		String fName = node.getId().toString().trim().replaceAll(" ", "");
		this.methodName = fName;
		lineNumber = ((TId) node.getId()).getLine();
    	if(!FunctionsDAO.addAFunction(fName, node)) {
      		error(ErrorType.FUNCTION_DEFINED);
    	}
	  }

		//Edw kanoume return giati den yparxei logos na mpei i anadromi kai na elegksei
		//afou o elegxos tis dilwsis ginetai sto FunctionsDAO.
	  public void caseAFunction(AFunction node){
			inAFunction(node);
			return;
  	}

	public void outAAssignStatement(AAssignStatement node)
    {
		String variable = node.getId().toString().trim().replaceAll(" ", "");
		this.variableName = variable;
		lineNumber = node.getId().getLine();

	/*	if(symtable.containsKey(variable)) {
			error(ErrorType.VARIABLE_DEFINED);
		} else {*/
			try {
				String type = (String) getOut(node.getExpression());

				symtable.put(variable, type);
			} catch(NullPointerException npe) {
		//	}
		}
	}

	public void outAValueExpression(AValueExpression node)
    {
		String type = getType(node);
		setOut(node, type);
    }

	public void outAIdentifierExpression(AIdentifierExpression node)
    {
		String type = (String) symtable.get(node.getId().toString().trim().replaceAll(" ", ""));
		String idName = node.getId().toString();
		this.variableName = idName;
		if(type == null) {
			error(ErrorType.VARIABLE_NOT_DEFINED);
		}
		setOut(node, type);
	}

	public void outAAdditionExpression(AAdditionExpression node)
    {
		if(node.getLExp() instanceof AFunccallExpression || node.getRExp() instanceof AFunccallExpression) {
			return;
		}
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
		if(node.getLExp() instanceof AFunccallExpression || node.getRExp() instanceof AFunccallExpression) {
			return;
		}
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
		if(node.getLExp() instanceof AFunccallExpression || node.getRExp() instanceof AFunccallExpression) {
			return;
		}
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
		if(node.getLExp() instanceof AFunccallExpression || node.getRExp() instanceof AFunccallExpression) {
			return;
		}
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
		if(node.getLExp() instanceof AFunccallExpression || node.getRExp() instanceof AFunccallExpression) {
			return;
		}
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

	private String getType(PExpression e) {
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
				break;*/
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
        }
    }

}
