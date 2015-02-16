package de.nokroeger.uni.xml.xmltree.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class XmlTreeRenderer {

	private static Options cliOptions;
	private static CommandLine cmd;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		createCliOptions();
		CommandLineParser parser = new BasicParser();
		try {
			cmd = parser.parse(cliOptions, args);
			
			if (cmd.hasOption("h")){
				new HelpFormatter().printHelp("xmlrender", cliOptions);	
			}
			else{
				File gvFile = xmlToGraphviz(getInputFile());
				graphvizToImage(gvFile);
				gvFile.delete();
			}
		} catch (ParseException e) {
			System.out.println("Invalid arguments, here is what I understand: ");
			new HelpFormatter().printHelp("xmlrender", cliOptions);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static void createCliOptions(){
		
		cliOptions = new Options();
		
		Option inputFile = new Option("i", "input", true, "Input XML file");
		inputFile.setRequired(true);
		Option outputFile = new Option ("o", "output", true, "Output file, default: {inputfile}.png");
		outputFile.setRequired(false);
		Option outputFormat = new Option ("f", "format", true, "Output format, default: png");
		outputFormat.setRequired(false);
		Option layoutAlgorithm = new Option("l", "layout", true, "Layout Algorithm, default: dot");
		layoutAlgorithm.setRequired(false);
		Option help = new Option("h", "help", false, "Print help");
		help.setRequired(false);
		
		cliOptions.addOption(inputFile);
		cliOptions.addOption(outputFormat);
		cliOptions.addOption(outputFile);
		cliOptions.addOption(layoutAlgorithm);
		cliOptions.addOption(help);
	}
	
	private static File getInputFile() throws FileNotFoundException, MissingArgumentException{
		if (cmd.hasOption("input")){
			File inputFile = new File(cmd.getOptionValue("input"));
			if (inputFile.exists() && inputFile.canRead()){
				return inputFile;
			}
			else{
				throw new FileNotFoundException("Could not find input file: "+inputFile.getAbsolutePath());
			}
		}
		else{
			throw new MissingArgumentException("Invalid arguments: input file (-i, --input) is mandatory and must be non-empty");
		}
	}
	
	private static String getOutputFormat(){
		return cmd.getOptionValue("format", "png");
	}
	
	private static String getLayoutAlgorithm(){
		return cmd.getOptionValue("layout", "dot");
	}
	
	private static File getOutputFile() throws MissingArgumentException, IOException{
		File outputFile = new File(cmd.getOptionValue("output", getInputFile().getAbsoluteFile().getParent()+"/"+getInputFile().getName().substring(0, getInputFile().getName().lastIndexOf("."))+"."+getOutputFormat()));
		if (outputFile.canWrite() || (!outputFile.exists() && outputFile.getParentFile().isDirectory() && outputFile.getParentFile().canWrite())){
			return outputFile;
		}
		else{
			throw new IOException("Output file "+outputFile.getAbsolutePath()+" is not writable.");
		}
	}
	
	private static File xmlToGraphviz(File xmlFile) throws TransformerFactoryConfigurationError, TransformerException{
		StreamSource stylesheet = new StreamSource(XmlTreeRenderer.class.getResourceAsStream("/xml2gv.xslt"));
		Transformer xml2gvTransformer = TransformerFactory.newInstance().newTransformer(stylesheet);
		File gvFile = new File(xmlFile.getAbsolutePath()+"."+getLayoutAlgorithm());
		gvFile.deleteOnExit();
		StreamResult gvStream = new StreamResult(gvFile);
		xml2gvTransformer.transform(new StreamSource(xmlFile), gvStream);
		return gvFile;
	}
	
	private static File graphvizToImage(File gvFile) throws MissingArgumentException, IOException{
		try {
			ProcessBuilder layoutProcessBuilder = new ProcessBuilder(getLayoutAlgorithm(), "-T", getOutputFormat(), "-o", getOutputFile().getAbsolutePath(), gvFile.getAbsolutePath());
			File output = new File("gv.out");
			layoutProcessBuilder.redirectOutput(output);
			layoutProcessBuilder.redirectError(output);
			Process layouter = layoutProcessBuilder.start();
			int result = layouter.waitFor();
			if (result != 0){			
				throw new RuntimeException("Could not layout graph.");
			}
			else{
				output.delete();
			}
			return getOutputFile();
		} catch (MissingArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getOutputFile();
	}

}

