
public class Instruction {
	private String ins;
	private int line;
	public Instruction(String ins,int line) {
		setIns(ins);
		setLine(line);
	}
	public String getIns() {
		return ins;
	}
	public void setIns(String ins) {
		this.ins=new String(ins);
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line=line;
	}
}
