package de.unistuttgart.iste.refactoringbot.refactorings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.JSONObject;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.unistuttgart.iste.refactoringbot.Refactoring;

/**
 * This class is used for executing the add override annotation refactoring.
 *
 * @author Timo Pfaff
 */
public class AddOverrideAnnotation extends VoidVisitorAdapter implements Refactoring {
	int line;
	String methodName;
	
	@Override
	public void visit(MethodDeclaration declaration, Object arg) {

		if (line == declaration.getName().getBegin().get().line) {
			methodName = declaration.getNameAsString();
			declaration.addMarkerAnnotation("Override");
		}

	}

	@Override
	public void performRefactoring(JSONObject issue, String projectPath) throws FileNotFoundException {
		String project = issue.getString("project");
		String component = issue.getString("component");
		String path = component.substring(project.length() + 1, component.length());
		line = issue.getInt("line");
		FileInputStream in = new FileInputStream(projectPath + path);
		CompilationUnit compilationUnit = LexicalPreservingPrinter.setup(JavaParser.parse(in));
		System.out.println(LexicalPreservingPrinter.print(compilationUnit));
		this.visit(compilationUnit, null);
		System.out.println(LexicalPreservingPrinter.print(compilationUnit));

		/**
		 * Actually apply changes to the File
		 */

		PrintWriter out = new PrintWriter(projectPath + path);
		out.println(LexicalPreservingPrinter.print(compilationUnit));
		out.close();

	}

	@Override
	public String getCommitMessage() {
		return "Add override annotation to method " + methodName;
	}

}
