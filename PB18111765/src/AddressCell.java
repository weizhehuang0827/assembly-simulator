
public class AddressCell {
	private String label;
	private int address;
	
	public AddressCell(String label,int address) {
		setLabel(label);
		setAddress(address);
		
	}
	public int getAddress() {
		return address;
	}
	public void setAddress(int address) {
		this.address=address;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label=new String(label);
	}
}
