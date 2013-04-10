package astnodes;

import java.util.LinkedList;

import org.antlr.v4.runtime.ParserRuleContext;
import tools.index.ParseTreeUtils;


public class ASTNode {

	protected String codeStr = null;		
	protected ParserRuleContext parseTreeNodeContext;
	private String nodeTypeName = "unnamed";
	private CodeLocation location = new CodeLocation();
	
	protected LinkedList<ASTNode> children;
	
	// Overload the following three to
	// allow placement of CodeItem in a tree
	
	public void addChild(ASTNode expression)
	{ 
		if(children == null)
			children = new LinkedList<ASTNode>();
		children.add(expression);
	}
	
	public int getChildCount(){ if(children == null) return 0; return children.size(); }
	public ASTNode getChild(int i){ if(children == null) return null; return children.get(i); }
	
	
	public void initializeFromContext(ParserRuleContext ctx)
	{
		parseTreeNodeContext = ctx;
	}
	
	public void setLocation(ParserRuleContext ctx)
	{
		if(ctx == null) return;
		location = new CodeLocation(ctx);
	}
	
	public void setCodeStr(String aCodeStr)
	{
		codeStr = aCodeStr;
	}
	
	public String getCodeStr()
	{
		if(codeStr != null)
			return codeStr;
		
		codeStr = ParseTreeUtils.childTokenString(parseTreeNodeContext);
		return codeStr;
	}
	
	  public String getLocationString()
	  {
		  setLocation(parseTreeNodeContext);
		  return location.toString();
	  }
	
	public String getNodeTypeName() {
		return nodeTypeName;
	}

	public void setNodeTypeName(String nodeTypeName) {
		this.nodeTypeName = nodeTypeName;
	}
	
	public void accept(ASTNodeVisitor visitor){ visitor.visit(this); }	
}