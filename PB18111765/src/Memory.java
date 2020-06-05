import javafx.beans.property.SimpleStringProperty;

public class Memory {
	
	private SimpleStringProperty address;
	private SimpleStringProperty value;
	private int num=0;
	
	
	public Memory(String address,String value) {
		
		setValue(value);
		setAddress(address);
		
	}
	
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num=num;
	}

	public String getAddress() {
		return address.get();
	}
	
	public String getValue() {
		return value.get();
	}
	public void setAddress(String address) {
		this.address=new SimpleStringProperty(address);
	}
	public void setValue(String value) {
		this.value=new SimpleStringProperty(value);
	}
	
	
}
