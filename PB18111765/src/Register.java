import javafx.beans.property.SimpleStringProperty;


public class Register {
	private SimpleStringProperty name;
	private SimpleStringProperty number;
	private SimpleStringProperty value;
	private int num=0;
	
	public Register(String name,String number,String value) {
		setName(name);
		setNumber(number);
		setValue(value);
	}
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num=num;
	}
	
	public String getName() {
		return name.get();
	}
	public String getNumber() {
		return number.get();
	}
	public String getValue() {
		return value.get();
	}
	public void setName(String name) {
		this.name=new SimpleStringProperty(name);
	}
	public void setNumber(String number) {
		this.number=new SimpleStringProperty(number);
	}
	public void setValue(String value) {
		this.value=new SimpleStringProperty(value);
	}
	
}
