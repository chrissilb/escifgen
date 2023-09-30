package de.gwasch.code.escframework;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import de.gwasch.code.escframework.components.utils.CodeGenerator;

@Mojo(name = "ifgen")
public class InterfaceGenerationMojo extends AbstractMojo {

	private final static String DEFAULT_INPUT_FOLDER = "src/main/java";
//	private final static String DEFAULT_OUTPUT_FOLDER = "src/main/resources";
	private final static String DEFAULT_OUTPUT_FOLDER = "target/generated-sources";

	@Parameter(property = "systeminfo.inputFolder", defaultValue = DEFAULT_INPUT_FOLDER)
	private String inputFolder;

	@Parameter(property = "systeminfo.basePackageName")
	private String basePackageName;

	@Parameter(property = "systeminfo.outputFolder", defaultValue = DEFAULT_OUTPUT_FOLDER)
	private String outputFolder;
	
    @Parameter( readonly = true, defaultValue = "${project}" )
    private MavenProject project;

	private void generate() {
		
		if (basePackageName == null) {
			basePackageName = project.getGroupId();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream pos = new PrintStream(baos);
		System.setOut(pos);
				
		try {
			CodeGenerator generator = new CodeGenerator(inputFolder, basePackageName, outputFolder);
			generator.generateInterfaces();
			
			String[] lines = baos.toString().split(System.lineSeparator());
			for (String line : lines) {
				getLog().info(line);
			}
		}
		catch(Throwable t) {
			String[] lines = baos.toString().split(System.lineSeparator());
			for (String line : lines) {
				getLog().info(line);
			}
			
			getLog().error(t);
		}
		
		project.addCompileSourceRoot(outputFolder);
	}

	public void execute() {
		generate();

	}

	public static void main(String[] args) throws Exception {

		InterfaceGenerationMojo genPlugin = new InterfaceGenerationMojo();

		if (args.length > 1) {
			genPlugin.inputFolder = args[0];
		} 
		else {
			genPlugin.inputFolder = DEFAULT_INPUT_FOLDER;
		}
		if (args.length > 2) {
			genPlugin.basePackageName = args[1];
		} 
		if (args.length > 3) {
			genPlugin.outputFolder = args[2];
		} 
		else {
			genPlugin.outputFolder = DEFAULT_OUTPUT_FOLDER;
		}

		genPlugin.generate();
	}
}