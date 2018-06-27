package cn.nuaa.ai.SourceCodeFormat;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class MyCodeVisitor2 extends ASTVisitor {

	@SuppressWarnings("unchecked")
	public boolean visit(MethodDeclaration node) {
		//System.out.println("MethodDeclaration - Method name: " + node.getName());// 得到方法名
		//System.out.println("MethodDeclaration - the character length of the method is:" + node.getLength());// 节点的长度，不过是以字符长度来计算的，不是以行数来计//算的
		//System.out.println("MethodDeclaration - Parameter list of Method:\t" + node.parameters());// 得到方法的参数列表
		//System.out.println("MethodDeclaration - Return Value of Method:\t" + node.getReturnType2());// 得到方法的返回值

		MyFormat.methodDeclaration.setMethodName(node.getName().toString());
		if(node.getReturnType2() == null){
			MyFormat.methodDeclaration.setMethodRetureType("void");
		}else{
			MyFormat.methodDeclaration.setMethodRetureType(node.getReturnType2().toString());
		}
		List<String> plist = new ArrayList<>();
		for(int i = 0;i < node.parameters().size();i++){
			plist.add(node.parameters().get(i).toString());
		}
		MyFormat.methodDeclaration.setMethodParameters(plist);
		
		
		//System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
		Block b = node.getBody();
		// System.out.println(b.statements());
		List<Statement> list = b.statements();
		for (int i = 0; i < list.size(); i++) {
			// 读取if语句;
			if (list.get(i).getClass().getSimpleName().equals("IfStatement")) {
				//System.out.print(list.get(i));
				//System.out.println("it's a IF statement:");
				IfStatement ifs = (IfStatement) list.get(i);
				//System.out.println("get expression: " + ifs.getExpression());
				//System.out.println("get then statement: " + ifs.getThenStatement());
				//System.out.println("get else statement: " + ifs.getElseStatement());
				//System.out.println();
			}
			// 读取while循环;
			else if (list.get(i).getClass().getSimpleName().equals("WhileStatement")) {
				//System.out.print(list.get(i));
				//System.out.println("it's a WHILE statement");
				WhileStatement ifs = (WhileStatement) list.get(i);
				//System.out.println("get expression: " + ifs.getExpression());
				//System.out.println("get body: " + ifs.getBody());
				//System.out.println();
			}
			// 读取for循环;
			else if (list.get(i).getClass().getSimpleName().equals("ForStatement")) {
				//System.out.print(list.get(i));
				//System.out.println("it's a FOR statement");
				ForStatement ifs = (ForStatement) list.get(i);
				//System.out.println("get expression: " + ifs.getExpression());
				//System.out.println("get body: " + ifs.getBody());
				//System.out.println();
			}
			// 读取变量声明;
			else if (list.get(i).getClass().getSimpleName().equals("VariableDeclarationStatement")) {
				//System.out.print(list.get(i));
				//System.out.println("it's a VariableDeclarationStatement");
				VariableDeclarationStatement ifs = (VariableDeclarationStatement) list.get(i);
				//System.out.println("get Type: " + ifs.getType());
				VariableDeclarationFragment vdf = (VariableDeclarationFragment) ifs.fragments().get(0);
				//System.out.println("get variable name: " + vdf.getName());
				//System.out.println("get variable value: " + vdf.getInitializer());
				//System.out.println();
				
		    	MyFormat.VariableDeclarationList.add(list.get(i).toString());
				MyFormat.VariableNameList.add(vdf.getName().toString());
				MyFormat.VariableTypeList.add(ifs.getType().toString());
				
				if(MyFormat.TypeMap.containsKey(ifs.getType().toString())){
					MyFormat.TypeMap.replace(ifs.getType().toString(), MyFormat.TypeMap.get(ifs.getType().toString()) + 1);
				}else{
					MyFormat.TypeMap.put(ifs.getType().toString(), 1);
				}
				
				
			}
			// 读取表达式;
			else if (list.get(i).getClass().getSimpleName().equals("ExpressionStatement")) {
				//System.out.print(list.get(i));
				//System.out.println("it's a ExpressionStatement");
				ExpressionStatement ifs = (ExpressionStatement) list.get(i);
				Expression ex = ifs.getExpression();
				// 读取表达式;
				if (ex.getClass().getSimpleName().equals("Assignment")) {
					Assignment as = (Assignment) ex;
					//System.out.println("get LeftHandSide: " + as.getLeftHandSide());
					//System.out.println("get RightHandSide: " + as.getRightHandSide());
					//System.out.println("get Operator: " + as.getOperator());
				}
				// 读取方法调用;
				else if (ex.getClass().getSimpleName().equals("MethodInvocation")) {
					MethodInvocation mi = (MethodInvocation) ex;
					//System.out.println("get Name: " + mi.getName());
					//System.out.println("get Arguments: " + mi.arguments());
					//System.out.println("get Expression: " + mi.getExpression());
					//System.out.println("get Operators: " + mi.properties());
					//System.out.println("get Type: " + ex.resolveTypeBinding());
				}
				//System.out.println();
			} else {
				//System.out.print(list.get(i).getClass().getSimpleName());
				//System.out.println();
			}
		}

		// 获取注释;
		//System.out.println("annotation: " + node.getJavadoc());

		//System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
		return true;
	}
}