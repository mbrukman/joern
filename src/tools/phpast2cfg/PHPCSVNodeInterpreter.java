package tools.phpast2cfg;

import inputModules.csv.KeyedCSV.KeyedCSVRow;
import inputModules.csv.KeyedCSV.exceptions.InvalidCSVFile;
import inputModules.csv.csv2ast.ASTUnderConstruction;
import inputModules.csv.csv2ast.CSVRowInterpreter;
import ast.ASTNode;
import ast.CodeLocation;
import ast.expressions.Identifier;
import ast.expressions.IdentifierList;
import ast.functionDef.FunctionDef;
import ast.functionDef.ParameterList;
import ast.logical.statements.CompoundStatement;
import ast.php.declarations.PHPClassDef;
import ast.php.functionDef.Closure;
import ast.php.functionDef.ClosureUses;
import ast.php.functionDef.ClosureVar;
import ast.php.functionDef.Method;
import ast.php.functionDef.PHPParameter;
import ast.php.functionDef.TopLevelFunctionDef;
import ast.statements.blockstarters.DoStatement;
import ast.statements.blockstarters.ForStatement;
import ast.statements.blockstarters.IfStatement;
import ast.statements.blockstarters.WhileStatement;

public class PHPCSVNodeInterpreter implements CSVRowInterpreter
{

	@Override
	public long handle(KeyedCSVRow row, ASTUnderConstruction ast)
			throws InvalidCSVFile
	{
		long retval = -1;
		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		switch (type)
		{
			// special nodes
			case PHPCSVNodeTypes.TYPE_NAME:
				retval = handleName(row, ast);
				break;
			case PHPCSVNodeTypes.TYPE_CLOSURE_VAR:
				retval = handleClosureVar(row, ast);
				break;
			
			// declaration nodes
			case PHPCSVNodeTypes.TYPE_TOPLEVEL:
				retval = handleTopLevelFunction(row, ast);
				break;
			case PHPCSVNodeTypes.TYPE_FUNC_DECL:
				retval = handleFunction(row, ast);
				break;
			case PHPCSVNodeTypes.TYPE_CLOSURE:
				retval = handleClosure(row, ast);
				break;
			case PHPCSVNodeTypes.TYPE_METHOD:
				retval = handleMethod(row, ast);
				break;
			case PHPCSVNodeTypes.TYPE_CLASS:
				retval = handleClass(row, ast);
				break;

			// nodes with exactly 2 children
			case PHPCSVNodeTypes.TYPE_WHILE:
				retval = handleWhile(row, ast);
				break;
			case PHPCSVNodeTypes.TYPE_DO_WHILE:
				retval = handleDo(row, ast);
				break;

			// nodes with exactly 3 children
			case PHPCSVNodeTypes.TYPE_PARAM:
				retval = handleParameter(row, ast);
				break;

			// nodes with exactly 4 children
			case PHPCSVNodeTypes.TYPE_FOR:
				retval = handleFor(row, ast);
				break;

			// nodes with an arbitrary number of children
			case PHPCSVNodeTypes.TYPE_STMT_LIST:
				retval = handleCompound(row, ast);
				break;
			case PHPCSVNodeTypes.TYPE_IF:
				retval = handleIf(row, ast);
				break;
			case PHPCSVNodeTypes.TYPE_PARAM_LIST:
				retval = handleParameterList(row, ast);
				break;
			case PHPCSVNodeTypes.TYPE_CLOSURE_USES:
				retval = handleClosureUses(row, ast);
				break;
			case PHPCSVNodeTypes.TYPE_NAME_LIST:
				retval = handleIdentifierList(row, ast);
				break;

			default:
				retval = defaultHandler(row, ast);
		}

		return retval;
	}

	private long defaultHandler(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		ASTNode newNode = new ASTNode();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String code = row.getFieldForKey(PHPCSVNodeTypes.CODE);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setCodeStr(code);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}

	
	/* special nodes */
	
	private long handleName(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		Identifier newNode = new Identifier();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	private long handleClosureVar(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		ClosureVar newNode = new ClosureVar();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	
	/* declaration nodes */

	private static long handleTopLevelFunction(KeyedCSVRow row,
			ASTUnderConstruction ast) throws InvalidCSVFile
	{
		TopLevelFunctionDef newNode = new TopLevelFunctionDef();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);
		String endlineno = row.getFieldForKey(PHPCSVNodeTypes.ENDLINENO);
		String name = row.getFieldForKey(PHPCSVNodeTypes.NAME);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		codeloc.endLine = Integer.parseInt(endlineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);
		if (flags.contains(PHPCSVNodeTypes.FLAG_TOPLEVEL_FILE))
			newNode.setName("<" + name + ">");
		else if (flags.contains(PHPCSVNodeTypes.FLAG_TOPLEVEL_CLASS))
			newNode.setName("[" + name + "]");
		else
			throw new InvalidCSVFile("While trying to handle row "
					+ row.toString() + ": " + "Invalid toplevel flags " + flags
					+ ".");

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}

	private static long handleFunction(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		FunctionDef newNode = new FunctionDef();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);
		String endlineno = row.getFieldForKey(PHPCSVNodeTypes.ENDLINENO);
		String name = row.getFieldForKey(PHPCSVNodeTypes.NAME);
		String doccomment = row.getFieldForKey(PHPCSVNodeTypes.DOCCOMMENT);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		codeloc.endLine = Integer.parseInt(endlineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);
		newNode.setName(name);
		newNode.setDocComment(doccomment);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	private static long handleClosure(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		Closure newNode = new Closure();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);
		String endlineno = row.getFieldForKey(PHPCSVNodeTypes.ENDLINENO);
		String name = row.getFieldForKey(PHPCSVNodeTypes.NAME);
		String doccomment = row.getFieldForKey(PHPCSVNodeTypes.DOCCOMMENT);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		codeloc.endLine = Integer.parseInt(endlineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);
		newNode.setName(name);
		newNode.setDocComment(doccomment);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}

	private static long handleMethod(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		Method newNode = new Method();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);
		String endlineno = row.getFieldForKey(PHPCSVNodeTypes.ENDLINENO);
		String name = row.getFieldForKey(PHPCSVNodeTypes.NAME);
		String doccomment = row.getFieldForKey(PHPCSVNodeTypes.DOCCOMMENT);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		codeloc.endLine = Integer.parseInt(endlineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);
		newNode.setName(name);
		newNode.setDocComment(doccomment);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	private static long handleClass(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		PHPClassDef newNode = new PHPClassDef();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);
		String endlineno = row.getFieldForKey(PHPCSVNodeTypes.ENDLINENO);
		String name = row.getFieldForKey(PHPCSVNodeTypes.NAME);
		String doccomment = row.getFieldForKey(PHPCSVNodeTypes.DOCCOMMENT);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		codeloc.endLine = Integer.parseInt(endlineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);
		newNode.setName(name);
		newNode.setDocComment(doccomment);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	
	/* nodes with exactly 2 children */
	
	private long handleWhile(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		WhileStatement newNode = new WhileStatement();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	private long handleDo(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		DoStatement newNode = new DoStatement();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}


	/* nodes with exactly 3 children */

	private long handleParameter(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		PHPParameter newNode = new PHPParameter();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	
	/* nodes with exactly 4 children */
	
	private long handleFor(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		ForStatement newNode = new ForStatement();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	
	/* nodes with an arbitrary number of children */

	private long handleCompound(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		CompoundStatement newNode = new CompoundStatement();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}

	private long handleIf(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		IfStatement newNode = new IfStatement();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	private long handleParameterList(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		ParameterList newNode = new ParameterList();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	private long handleClosureUses(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		ClosureUses newNode = new ClosureUses();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
	
	private long handleIdentifierList(KeyedCSVRow row, ASTUnderConstruction ast)
	{
		IdentifierList newNode = new IdentifierList();

		String type = row.getFieldForKey(PHPCSVNodeTypes.TYPE);
		String flags = row.getFieldForKey(PHPCSVNodeTypes.FLAGS);
		String lineno = row.getFieldForKey(PHPCSVNodeTypes.LINENO);
		String childnum = row.getFieldForKey(PHPCSVNodeTypes.CHILDNUM);

		newNode.setProperty(PHPCSVNodeTypes.TYPE.getName(), type);
		newNode.setFlags(flags);
		CodeLocation codeloc = new CodeLocation();
		codeloc.startLine = Integer.parseInt(lineno);
		newNode.setLocation(codeloc);
		newNode.setProperty(PHPCSVNodeTypes.CHILDNUM.getName(), childnum);

		long id = Long.parseLong(row.getFieldForKey(PHPCSVNodeTypes.NODE_ID));
		ast.addNodeWithId(newNode, id);

		return id;
	}
}