package cn.nuaa.ai.ast;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;  
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;  
  
public class MyVisitor extends ASTVisitor {  
  
    @Override  
    public boolean visit(FieldDeclaration node) {
    	
        for (Object obj: node.fragments()) {  
            VariableDeclarationFragment v = (VariableDeclarationFragment)obj;  
            System.out.println("Field:\t" + v.getName()); 
        }  
          
        return true;  
    }  
  
	@Override  
    public boolean visit(MethodDeclaration node) {
		//方法1;
		System.out.println("Method Name: " + node.getStructuralProperty(MethodDeclaration.NAME_PROPERTY));
    	System.out.println("Method Parameters: " + node.getStructuralProperty(MethodDeclaration.PARAMETERS_PROPERTY));
    	System.out.println("Method Return Type: " + node.getStructuralProperty(MethodDeclaration.RETURN_TYPE2_PROPERTY));
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");	
		//方法2;
        System.out.println("Method Name: " + node.getName());
        System.out.println("Method Parameters: " + node.parameters());
        System.out.println("Method Return Type: " + node.getReturnType2());        
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
        
        int length = node.getBody().statements().size();
        for(int i = 0; i < length; i++){
	        String statementClassName = node.getBody().statements().get(i).getClass().getSimpleName();
	        System.out.println(statementClassName);
	        if(statementClassName.equals("VariableDeclarationStatement")){
	        	System.out.println("VariableDeclarationStatement Type: "+((VariableDeclarationStatement)(node.getBody().statements().get(i))).getType());
	        }else if(statementClassName.equals("ExpressionStatement")){
	        	//System.out.println("ExpressionStatement: "+((ExpressionStatement)(node.getBody().statements().get(i))).getExpression().getStructuralProperty(ExpressionStatement.EXPRESSION_PROPERTY));
	        }else if(statementClassName.equals("ForStatement")){
	        	
	        }else if(statementClassName.equals("WhileStatement")){
	        	
	        }else if(statementClassName.equals("IfStatement")){
	        	
	        }
        }
        
        return true;  
    }  
  
    @Override  
    public boolean visit(TypeDeclaration node) {
        System.out.println("Class:\t" + node.getName());
        //System.out.println("Class:\t" + node.bodyDeclarations().get(0)); 
        return true;  
    }
}