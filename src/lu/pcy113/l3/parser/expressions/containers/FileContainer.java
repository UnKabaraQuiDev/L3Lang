package lu.pcy113.l3.parser.expressions.containers;

public class FileContainer extends ExprContainer {
	
	private String fileName;
	
	public FileContainer(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {return fileName;}
	
	@Override
	public String toString() {
		return "name="+fileName;
	}

}
