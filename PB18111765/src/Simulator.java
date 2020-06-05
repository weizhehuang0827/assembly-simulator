import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javafx.application.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class Simulator extends Application{
	private ObservableList<Register> Registers = FXCollections.observableArrayList();
	private ArrayList<Register> registerList = new ArrayList<Register>();
	private TableView<Register> RegistersTable = new TableView<Register>();
	private ObservableList<Memory> Memories = FXCollections.observableArrayList();
	private ArrayList<Memory> memoryList = new ArrayList<Memory>();
	private TableView<Memory> MemoriesTable = new TableView<Memory>();
	private TextArea textAreaCode = new TextArea();
	private TextField PC=new TextField();
	private TextField insRunning =new TextField();
	private TextField memToReach=new TextField();
	private TextField valueInMem=new TextField();
	private TextArea textAreaMessage =new TextArea();
	private TextArea textAreaAssembly=new TextArea();
	private TextArea textAreaInfo=new TextArea();
	private Stage assemblyStage = new Stage();
	private ArrayList<String> ins = new ArrayList<String>();
	private FileChooser fileChooser=new FileChooser();
	private int pc=0;
	private final int Max=100;
	//supported instructions include  add,addi,lw,sw,beq,j
	
	
	public void start(Stage primaryStage) {
		VBox vBoxLeft = new VBox();
		
		textAreaCode.setPrefColumnCount(20);
		textAreaCode.setPrefHeight(400);
		HBox hBoxLeft = new HBox();
		Button btStep = new Button("Step");
		Button btRun = new Button("Run");
		Button btLoad =new Button("Load");
		hBoxLeft.getChildren().addAll(btLoad,btStep,btRun);
		hBoxLeft.setAlignment(Pos.CENTER);
		hBoxLeft.setSpacing(30);
		vBoxLeft.getChildren().addAll(new Label("Code:"),textAreaCode,hBoxLeft);
		vBoxLeft.setSpacing(20);
		
		VBox vBoxCenter = new VBox();
		GridPane gridPaneCenter = new GridPane();
		GridPane gridPaneCenter2 = new GridPane();
		
		
		gridPaneCenter.setAlignment(Pos.CENTER);
		gridPaneCenter2.setAlignment(Pos.CENTER);
		gridPaneCenter.add(new Label("PC:"),0,0);
		gridPaneCenter.add(PC,1,0);
		gridPaneCenter.add(new Label("Ins Running:"), 0,1);
		gridPaneCenter.add(insRunning, 1, 1);
		gridPaneCenter.add(new Label("Memory to reach:"),0,2);
		gridPaneCenter.add(memToReach, 1, 2);
		gridPaneCenter2.add(new Label("Value in Mem:"),0,0);
		gridPaneCenter2.add(valueInMem,1,0);
		vBoxCenter.setSpacing(20);
		
	
		TableColumn<Memory,String> addressColumn = new TableColumn<Memory,String>("Address");
		addressColumn.setCellValueFactory(new PropertyValueFactory<Memory, String>("address"));
		TableColumn<Memory,String> memValueColumn = new TableColumn<Memory,String>("Value");
		memValueColumn.setCellValueFactory(new PropertyValueFactory<Memory,String>("value"));

		MemoriesTable.getColumns().add(addressColumn);
		MemoriesTable.getColumns().add(memValueColumn);
		
		initializeMemoryTable();

		MemoriesTable.setItems(Memories);
		MemoriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		
		vBoxCenter.getChildren().addAll(gridPaneCenter,gridPaneCenter2,MemoriesTable);
		
		VBox vBoxTop = new VBox();
		Button btEditor = new Button("Editor");
		Button btReset = new Button("Reset");
		Button btReach=new Button("Reach");
		Button btChooseFile=new Button("File");
		HBox hBoxTop = new HBox();
		hBoxTop.getChildren().addAll(btEditor,btChooseFile,btReset,btReach);
		hBoxTop.setAlignment(Pos.CENTER);
		hBoxTop.setSpacing(40);
		vBoxTop.setAlignment(Pos.CENTER);
		vBoxTop.getChildren().add(hBoxTop);
		
		VBox vBoxRight = new VBox();
		
		
		TableColumn<Register,String> nameColumn = new TableColumn<Register,String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<Register, String>("name"));
		TableColumn<Register,String> numberColumn = new TableColumn<Register,String>("Number");
		numberColumn.setCellValueFactory(new PropertyValueFactory<Register,String>("number"));
		TableColumn<Register,String> valueColumn = new TableColumn<Register,String>("Value");
		valueColumn.setCellValueFactory(new PropertyValueFactory<Register,String>("value"));

		RegistersTable.getColumns().add(nameColumn);
		RegistersTable.getColumns().add(numberColumn);
		RegistersTable.getColumns().add(valueColumn);
		
		initializeRegisterTable();
		
		RegistersTable.setItems(Registers);
		
		vBoxRight.setSpacing(20);
		vBoxRight.getChildren().addAll(new Label("Registers:"),RegistersTable);
		
		
		VBox vBoxBottom = new VBox();
		
		vBoxBottom.getChildren().addAll(new Label("Messages:"),textAreaMessage);
		vBoxBottom.setSpacing(20);
		
		btLoad.setOnAction(e->loadToMem());
		btStep.setOnAction(e->{
			stepCode(1);
		});
		btRun.setOnAction(e->runCode());
		btReset.setOnAction(e->{
			reset();
			textAreaMessage.setText("");
		});
		btReach.setOnAction(e->reachMemory());
		
		
		MemoriesTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldMem, newMem) -> {
            if (newMem != null) {
                textAreaMessage.setText("Selected Address:	"+newMem.getAddress()+"\n"
                		+"Value In Address:	"+newMem.getValue()+"\n"
                		+"The value in decimalism:	"+newMem.getNum()+"\n");
            }
        });
		RegistersTable.getSelectionModel().selectedItemProperty().addListener((observableValue,oldRegister,newRegister)->{
			if(newRegister!=null) {
				textAreaMessage.setText("Selected Register:	"+newRegister.getName()+"\n"
                		+"The Register Number:	"+newRegister.getNumber()+"\n"
                		+"The value in Register:  "+newRegister.getValue()+"\n"
                		+"The value in decimalism:	"+newRegister.getNum()+"\n");
			}
		});
		
		fileChooser.setTitle("Choose File");
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("Text Files", "*.txt"),
		         new ExtensionFilter("All Files", "*.*"));
		
		btChooseFile.setOnAction(e->loadFile(primaryStage));
		
		valueInMem.setEditable(false);
		textAreaMessage.setEditable(false);
		textAreaInfo.setEditable(false);
		
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(vBoxLeft);
		borderPane.setCenter(vBoxCenter);
		borderPane.setRight(vBoxRight);
		borderPane.setTop(vBoxTop);
		borderPane.setBottom(vBoxBottom);
		
		Scene scene = new Scene(borderPane,1000,720);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Assembly Simulator");
		primaryStage.show();
		
		textAreaAssembly.setPrefColumnCount(40);
		textAreaAssembly.setPrefHeight(500);
		VBox vBoxLeft2=new VBox();
		vBoxLeft2.setAlignment(Pos.CENTER);
		vBoxLeft2.setSpacing(20);
		HBox hBoxLeft2=new HBox();
		Button btFile =new Button("File");
		Button btCompile =new Button("Compile and Load");
		hBoxLeft2.getChildren().addAll(btFile);
		vBoxLeft2.getChildren().addAll(hBoxLeft2,new Label("Code:"),textAreaAssembly,btCompile);
		
		VBox vBoxBottom2 = new VBox();
		vBoxLeft2.setSpacing(20);
		vBoxBottom2.getChildren().addAll(new Label("Messages:"),textAreaInfo);
		
		BorderPane borderPane2 = new BorderPane();
		borderPane2.setLeft(vBoxLeft2);
		borderPane2.setBottom(vBoxBottom2);
		Scene scene2=new Scene(borderPane2,800,680);
		
		assemblyStage.setScene(scene2);
		assemblyStage.setTitle("Assembly Editor");
		btEditor.setOnAction(e->{
			assemblyStage.show();
		});
		
		btCompile.setOnAction(e->compile());
		btFile.setOnAction(e->loadAssemblyFile());
		
	}
	
	
	private int getRegisterNum(String str) {//无符号的2进制字符串转10进制数
		int n=0;
		for(int i=0;i<str.length();i++) {
			n=2*n+Integer.parseInt(str.substring(i,i+1));
		}
		return n;
	}
	
	private int getImm(String str) {//有符号的2进制字符串转10进制数
		int n=0;
		if(str.charAt(0)-'0'==0) {
			for(int i=0;i<str.length();i++) {
				n=2*n+Integer.parseInt(str.substring(i,i+1));
			}
			return n;
		}
		else {
			for(int i=0;i<str.length();i++) {
				n=2*n+1-Integer.parseInt(str.substring(i,i+1));
			}
			return -(n+1);
		}
	}
	
	private String getInsRunning(String str) {//2进制字符串转16进制字符串
		StringBuffer temp=new StringBuffer();
		for(int i=0;i+4<=str.length();i=i+4) {
			temp.append(Integer.toHexString(getRegisterNum(str.substring(i,i+4))));
		}
		return temp.toString();
		
	}
	
	private String getHexString(int n) {//10进制数转16进制字符串
		StringBuilder temp=new StringBuilder("00000000");
		String temp1=Integer.toHexString(n);
		temp.replace(8-temp1.length(), 8, temp1);
		return temp.toString();
	}
	
	private void loadToMem() {
		reset();
		String text=textAreaCode.getText();
		String[] str=text.split("\n");
		int n;
		int count=0;
		
		for(int i=0;i<str.length;i++) {
			String temp=str[i].trim();
			if((n=temp.length())==32) {
				for(int j=0;j<n;j++) {
					if(temp.charAt(j)!='1'&&temp.charAt(j)!='0')
					{
						int k=i+1;
						textAreaMessage.setText("Run Error! There are other characters "
						+ "in the machine code besides 0 and 1!\n Please check the "+k+"line!\n");
						reset();
						return;
					}
				}
				
				ins.add(str[i].trim());
				memoryList.get(count).setValue("0x"+getInsRunning(str[i].trim()));
				memoryList.get(count).setNum(getImm(str[i].trim()));
				
				count++;
			}
			else if(n>0) {
				int k=i+1;
				textAreaMessage.setText("Run Error ! The number of digits for instruction "
						+ "is wrong!\n Please check the "+k+"line!\n");
				reset();
				return ;
			}
		}
		textAreaMessage.setText("Successfully Load the code to memory!\n");
	}
	
	private Boolean stepCode(int flag) {
		
		if((pc>>>2)>=ins.size()) {
			textAreaMessage.setText("Successfully Run!\n");
			return false;
		}
		String opCode=new String(ins.get(pc>>>2).substring(0,6));
		int rs=getRegisterNum(ins.get(pc>>>2).substring(6,11));
		int rt=getRegisterNum(ins.get(pc>>>2).substring(11,16));
		int rd=getRegisterNum(ins.get(pc>>>2).substring(16,21));
		int imm=getImm(ins.get(pc>>>2).substring(16,32));
		int branchOffset=imm<<2;
		int jOffset=getImm(ins.get(pc>>>2).substring(6,32))<<2;
		int pcPre=pc;
		
		if(opCode.equals("000000")) {//add
			if(ins.get(pc>>2).substring(26,32).equals("100000")) {
				int value=registerList.get(rs).getNum()+registerList.get(rt).getNum();
				registerList.get(rd).setNum(value);
				registerList.get(rd).setValue("0x"+getHexString(value));
				pc=pc+4;
			}
			else {
				textAreaMessage.setText("RunError!There are some unsupported type of opcodes"+
			 "000000"+"!\n");
						reset();
						return false;
			}
		}
		else if(opCode.equals("001000")) {//addi
			int value=registerList.get(rs).getNum()+imm;
			registerList.get(rt).setNum(value);
			registerList.get(rt).setValue("0x"+getHexString(value));
			pc=pc+4;
		}
		else if(opCode.equals("100011")) {//lw
			int addr=(registerList.get(rs).getNum()+imm)>>2;
			int value= memoryList.get(addr).getNum();
			registerList.get(rt).setNum(value);
			registerList.get(rt).setValue("0x"+getHexString(value));
			pc=pc+4;
		}
		else if(opCode.equals("101011")) {//sw
			int value = registerList.get(rt).getNum();
			int addr = (registerList.get(rs).getNum()+imm)>>2;
			memoryList.get(addr).setNum(value);
			memoryList.get(addr).setValue("0x"+getHexString(value));
			pc=pc+4;
		}
		else if(opCode.equals("000100")) {//beq
			int compare1=registerList.get(rs).getNum();
			int compare2=registerList.get(rt).getNum();
			if(compare1==compare2) {
				pc=pc+4+branchOffset;
			}
			else {
				pc=pc+4;
			}
		}
		else if(opCode.equals("000010")) {//j
			pc=(((pc+4)>>>28)<<28)+jOffset;
		}
		else {
			textAreaMessage.setText("RunError!There are some unsupported type of opcodes "
					+ "!\n");
			reset();
			return false;
		}
		registerList.get(0).setNum(0);
		registerList.get(0).setValue("0x00000000");
		PC.setText("0x"+getHexString(pc));
		insRunning.setText("0x"+getInsRunning(ins.get(pcPre>>>2)));
		Memories.clear();
		Memories.addAll(memoryList);
		Registers.clear();
		Registers.addAll(registerList);

		if(flag==1) {
			textAreaMessage.setText("Successfully step over!\n");
		}
		return true;
	}
	
	private void runCode() {
		int i;
		for(i=0;i<Max;i++) {

			if(!stepCode(0)) {
				return;
			}
		}
		
		textAreaMessage.setText("Run Error!\n"+"The program enter a dead cycle!\n");
		
	}
	
	private void initializeRegisterTable() {}{
		Registers.clear();
		registerList.clear();
		for(int i=0;i<32;i++) {
			String s=new String();
			if(i==0) {
				s="$zero";
			}
			else if(i==1) {
				s="$at";
			}
			else if(i==2||i==3) {
				int temp=i-2;
				s="$v"+temp;
			}
			else if(i<=7) {
				int temp=i-4;
				s="$a"+temp;
			}
			else if(i<=15) {
				int temp=i-8;
				s="$t"+temp;
			}
			else if(i<=23) {
				int temp=i-16;
				s="$s"+temp;
			}
			else if(i<=25) {
				int temp=i-16;
				s="$t"+temp;
			}
			else if(i<=27) {
				int temp=i-26;
				s="$k"+temp;
			}
			else {
				switch(i) {
					case 28:s="$gp";break;
					case 29:s="$sp";break;
					case 30:s="$fp";break;
					case 31:s="$ra";break;
				}
				
			}
			Register r=new Register(s,""+i,"0x00000000");
			registerList.add(r);
		}
		Registers.addAll(registerList);
	}
	
	private void initializeMemoryTable() {
		Memories.clear();
		memoryList.clear();
		for(int i=0;i<256*4;i=i+4) {

			Memory m=new Memory("0x"+getHexString(i),"0x00000000");
			memoryList.add(m);
		}
		Memories.addAll(memoryList);
		
		
	}
	
	private void reset() {
		pc=0;
		ins.clear();
		PC.setText("0x00000000");
		insRunning.setText("0x00000000");
		memToReach.setText("");
		valueInMem.setText("");
		initializeMemoryTable();
		initializeRegisterTable();
		for(int i=0;i<32;i++) {
			registerList.get(i).setNum(0);
			registerList.get(i).setValue("0x00000000");
		}
		Registers.clear();
		Registers.addAll(registerList);
	}
	
	private void reachMemory() {
		String mem=new String(memToReach.getText());
		String address;
		if(mem.length()!=10) {
			textAreaMessage.setText("The digit number of your input is wrong!\n");
			valueInMem.setText("");
			return;
		}
		else if(!mem.substring(0,2).equals("0x")) {
			textAreaMessage.setText("Your input is invalid!\n");
			valueInMem.setText("");
			return;
		}
		else {
			address=mem.substring(2);
			for(int i=0;i<address.length();i++) {
				if(!Character.isLetterOrDigit(address.charAt(i))) {
					textAreaMessage.setText("Your input is invalid!\n");
					valueInMem.setText("");
					return;
				}
			}
			if(Long.parseLong(address,16)%4!=0) {
				textAreaMessage.setText("Your input is invalid!\n"
						+ "Make sure the words are aligned!\n");
				valueInMem.setText("");
				return;
			}
			if(Long.parseLong(address,16)>=256*4) {
				textAreaMessage.setText("Your input is invalid!\n"
						+ "This number is too big!\n"
						+"The input shuold be smaller than 1024!\n");
				valueInMem.setText("");
				return;
			}
			valueInMem.setText(memoryList.get(Integer.parseInt(address,16)>>>2).getValue());
			textAreaMessage.setText("Successfully reach the memory!\n"
					+"And also successfully scroll to the destination!\n");
			MemoriesTable.scrollTo(Integer.parseInt(address,16)>>>2);
		}
	}
	
	private void loadFile(Stage fileStage) {
		File fileToLoad=fileChooser.showOpenDialog(fileStage);
		FileReader reader;
		BufferedReader bReader;
		if(fileToLoad!=null) {
			try {
				reader = new FileReader(fileToLoad);
				bReader = new BufferedReader(reader);
				StringBuilder temp = new StringBuilder();
			    String str = "";
			    	try {
			    		while ((str =bReader.readLine()) != null) {
			    			temp.append(str + "\n");
			    			
			    		}
			    		try {
							bReader.close();
							textAreaCode.setText(temp.toString());
						} catch (IOException e) {
							
							textAreaMessage.setText("Load File Error!");
							e.printStackTrace();
							return;
						}
					} catch (IOException e) {
						
						textAreaMessage.setText("Load File Error!");
						e.printStackTrace();
						return;
					}
			        
			} catch (FileNotFoundException e) {
				
				textAreaMessage.setText("Load File Error!");
				e.printStackTrace();
				return;
			}
	        
		}
	}
	
	private void compile() {
		String code=textAreaAssembly.getText();
		String[] str=code.split("\n");
		ArrayList<String> codeLine=new ArrayList<String>();
		ArrayList<Integer> lineNumber=new ArrayList<Integer>();
		ArrayList<Instruction> result=new ArrayList<Instruction>();
		ArrayList<AddressCell> addressTable=new ArrayList<AddressCell>();
		
		for(int i=0;i<str.length;i++) {
			
			String temp =str[i].trim();
			int pos=temp.indexOf('#');
			if(pos==-1) {
				codeLine.add(str[i].trim());
				lineNumber.add(i+1);
			}
			else {
				codeLine.add(str[i].trim().substring(0,pos).trim());
				lineNumber.add(i+1);
			}
			
		}
		int addressCounter=0;
		for(int i=0;i<codeLine.size();i++) {
			if(codeLine.get(i).equals("")) {
				continue;
			}
			else if(codeLine.get(i).equals(".data")) {

				int j;
				for( j=i+1;j<codeLine.size()&&!codeLine.get(j).equals(".text")&&!codeLine.get(j).equals(".data");j++) {
					
					if(codeLine.get(j).equals("")) {
						continue;
					}
					else if(codeLine.get(j).indexOf(".word")!=0) {
						textAreaInfo.setText("Compile Fail. The Error is found in "+ lineNumber.get(j) +" line.\n");
						return;
					}
					else {
						String s=codeLine.get(j).substring(5).trim();
						String [] list=s.split(",");

						for(int k=0;k<list.length;k++) {
							list[k]=list[k].trim();				
						}
						
						for(int k=0;k<list.length;k++) {
							if(list[k].equals("")) {
								textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
								return;
							}
							else {
								String temp=strToBinaryData(list[k],32);
								if(temp.equals("")) {
									textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
									return;
								}
								result.add(new Instruction(temp,addressCounter));
								addressCounter++;
							}

						}
					}
				}
				i=j-1;
				
			}
			else if(codeLine.get(i).equals(".text")){
				int j;
				for(j=i+1;j<codeLine.size()&&!codeLine.get(j).equals(".text")&&!codeLine.get(j).equals(".data");j++) {
					if(codeLine.get(j).equals("")) {
						continue;
					}
					else if(codeLine.get(j).indexOf(':')==-1) {
						addressCounter++;
						continue;
					}
					else {
						int index=codeLine.get(j).indexOf(':');
//						int trueLine=0;
						if(index==codeLine.get(j).length()-1) {
							int m;
							for(m=j+1;m<codeLine.size()&&codeLine.get(m).equals("");m++);
							if(m==codeLine.size()) {
								textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
								return;
							}
							
						}
						

						addressTable.add(new AddressCell(codeLine.get(j).substring(0,index).trim(),addressCounter));
						if(index!=codeLine.get(j).length()-1) {
							addressCounter++;
						}
						String temp=new String(codeLine.get(j).substring(index+1).trim());
						codeLine.set(j, new String(temp));
					}
				}
				i=j-1;
			}
			else {
				textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(i)+ " line.\n");
				return;
			}
		}
		
		
		
		addressCounter=0;
		for(int i=0;i<codeLine.size();i++) {
			if(codeLine.get(i).equals("")) {
				continue;
			}
			else if(codeLine.get(i).equals(".data")) {
				int j;
				for( j=i+1;j<codeLine.size()&&!codeLine.get(j).equals(".text")&&!codeLine.get(j).equals(".data");j++) {
					
					if(codeLine.get(j).equals("")) {
						continue;
					}
					else {
						String s=codeLine.get(j).substring(5).trim();
						String [] list=s.split(",");

						addressCounter=addressCounter+list.length;
					}
				}
				i=j-1;
			 }
					
			else if(codeLine.get(i).equals(".text")) {
				int j;
				for(j=i+1;j<codeLine.size()&&!codeLine.get(j).equals(".text")&&!codeLine.get(j).equals(".data");j++) {
					if(codeLine.get(j).equals("")) {
						continue;
					}
					else {
						String[] s=codeLine.get(j).split("\\s");
						int rs,rt,rd;
						
						
						if(s[0].equals("add")) {
							String[] strList=codeLine.get(j).substring(3).trim().split(",");
							for(int k=0;k<strList.length;k++) {
								strList[k]=strList[k].trim();
							}
							if(strList.length!=3||!strList[0].startsWith("$")||
									!strList[1].startsWith("$")||!strList[2].startsWith("$")||
									(strList[0].length()>3&&strList[0].length()!=5)||
									(strList[1].length()>3&&strList[1].length()!=5)||
									(strList[2].length()>3)&&strList[2].length()!=5) {
								textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
								return;
							}
							else {
								rd=registerNameToNumber(strList[0].substring(1));
								rs=registerNameToNumber(strList[1].substring(1));
								rt=registerNameToNumber(strList[2].substring(1));
								if(rd==-1||rs==-1||rt==-1) {
									textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
									return;
								}
								StringBuilder tempRd=new StringBuilder("00000");
								StringBuilder tempRs=new StringBuilder("00000");
								StringBuilder tempRt=new StringBuilder("00000");
								tempRd.replace(5-Integer.toBinaryString(rd).length(), 5, Integer.toBinaryString(rd));
								tempRs.replace(5-Integer.toBinaryString(rs).length(), 5, Integer.toBinaryString(rs));
								tempRt.replace(5-Integer.toBinaryString(rt).length(), 5, Integer.toBinaryString(rt));
								String temp="000000"+tempRs.toString()+tempRt.toString()+tempRd.toString()+"00000"+"100000";
								result.add(new Instruction(temp,addressCounter));
								addressCounter++;
							}
						}
						else if(s[0].equals("addi")){
							String[] strList=codeLine.get(j).substring(4).trim().split(",");
							for(int k=0;k<strList.length;k++) {
								strList[k]=strList[k].trim();
							}
							
							if(strList.length!=3||!strList[0].startsWith("$")||
									!strList[1].startsWith("$")||
									(strList[0].length()>3&&strList[0].length()!=5)||
									(strList[1].length()>3&&strList[1].length()!=5)) {
								textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
								return;
							}
							else {
								rt=registerNameToNumber(strList[0].substring(1));
								rs=registerNameToNumber(strList[1].substring(1));
								if(rt==-1||rs==-1) {
									textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
									return;
								}
								
								String imm=strToBinaryData(strList[2].trim(),16);
								if(imm.equals("")){
									textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
									return;
								}
								StringBuilder tempRs=new StringBuilder("00000");
								StringBuilder tempRt=new StringBuilder("00000");
								tempRs.replace(5-Integer.toBinaryString(rs).length(), 5, Integer.toBinaryString(rs));
								tempRt.replace(5-Integer.toBinaryString(rt).length(), 5, Integer.toBinaryString(rt));
								
								String temp;
								temp="001000"+tempRs.toString()+tempRt.toString()+imm;
								result.add(new Instruction(temp,addressCounter));
								addressCounter++;
							}
						}
						else if(s[0].equals("lw")||s[0].equals("sw")) {
							String[] strList=codeLine.get(j).substring(2).trim().split(",");
							for(int k=0;k<strList.length;k++) {
								strList[k]=strList[k].trim();
							}
							if(strList.length!=2||!strList[0].startsWith("$")) {
								textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
								return;
							}
							else {
								rt=registerNameToNumber(strList[0].substring(1));
								if(rt==-1) {
									textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
									return;
								}
								String regex="\\-?[0-9a-z]+\\s*\\(\\s*\\$[0-9a-z]{1,5}\\s*\\)";
								if(!strList[1].matches(regex)) {
									textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
									return;
								}
								else {
									int index=strList[1].indexOf('$');
									int indexLeftKuoHao=strList[1].indexOf('(');
									rs=registerNameToNumber(strList[1].substring(index+1,strList[1].length()-1).trim());
									if(rs==-1) {
										textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
										return;
									}
									StringBuilder tempRs=new StringBuilder("00000");
									StringBuilder tempRt=new StringBuilder("00000");
									tempRs.replace(5-Integer.toBinaryString(rs).length(), 5, Integer.toBinaryString(rs));
									tempRt.replace(5-Integer.toBinaryString(rt).length(), 5, Integer.toBinaryString(rt));
									String imm=strToBinaryData(strList[1].substring(0,indexLeftKuoHao).trim(),16);
									if(imm.equals("")) {
										textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
										return;
									}
									
									String temp;
									if(s[0].equals("lw")) {
										temp="100011"+tempRs.toString()+tempRt.toString()+imm;
										
									}
									else {
										temp="101011"+tempRs.toString()+tempRt.toString()+imm;
									}
									result.add(new Instruction(temp,addressCounter));
									addressCounter++;
								}
							}
						}
						else if(s[0].equals("beq")){
							String[] strList=codeLine.get(j).substring(3).trim().split(",");
							for(int k=0;k<strList.length;k++) {
								strList[k]=strList[k].trim();
							}
							if(strList.length!=3||!strList[0].startsWith("$")||
									!strList[1].startsWith("$")||
									(strList[0].length()>3&&strList[0].length()!=5)||
									(strList[1].length()>3&&strList[1].length()!=5)) {
								textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
								return;
							}
							else {
								rs=registerNameToNumber(strList[0].substring(1));
								rt=registerNameToNumber(strList[1].substring(1));
								if(rt==-1||rs==-1) {
									textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
									return;
								}
								int k;
								for(k=0;k<addressTable.size();k++){
									if(addressTable.get(k).getLabel().equals(strList[2])){
										int labelAddress=addressTable.get(k).getAddress();
										String imm=strToBinaryData(Integer.toString(labelAddress-(addressCounter+1)),16);
										StringBuilder tempRs=new StringBuilder("00000");
										StringBuilder tempRt=new StringBuilder("00000");
										tempRs.replace(5-Integer.toBinaryString(rs).length(), 5, Integer.toBinaryString(rs));
										tempRt.replace(5-Integer.toBinaryString(rt).length(), 5, Integer.toBinaryString(rt));
										String temp="000100"+tempRs.toString()+tempRt.toString()+imm;
										result.add(new Instruction(temp,addressCounter));
										addressCounter++;
										break;
									}
								}
								if(k==addressTable.size()){
									textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
									return;
								}
							}
						}
						else if(s[0].equals("j")){
							String strList=codeLine.get(j).substring(1).trim();
				
							int k;
							for(k=0;k<addressTable.size();k++){
								if(addressTable.get(k).getLabel().equals(strList)){
									int labelAddress=addressTable.get(k).getAddress();
									String imm=strToBinaryData(Integer.toString(labelAddress),26);
										
									String temp="000010"+imm;
									result.add(new Instruction(temp,addressCounter));
									addressCounter++;
									break;
								}
							}
							if(k==addressTable.size()){
								textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
								return;
							}
						}
						else {
							textAreaInfo.setText("Compile Fail. The Error is found in " +lineNumber.get(j)+ " line.\n");
							return;
						}
						
					}
				}
				i=j-1;
			}
			
		}
		for(int i=0;i<result.size()-1;i++) {
			for(int j=0;j<result.size()-1-i;j++) {
				if(result.get(j).getLine()>result.get(j+1).getLine()) {
					String tempIns;
					int tempLine;
					tempIns=result.get(j).getIns();
					result.get(j).setIns(result.get(j+1).getIns());
					result.get(j+1).setIns(tempIns);
					tempLine=result.get(j).getLine();
					result.get(j).setLine(result.get(j+1).getLine());
					result.get(j+1).setLine(tempLine);
				}
			}
		}
		StringBuilder binaryFile =new StringBuilder();
		for(int i=0;i<result.size();i++) {
			binaryFile.append(result.get(i).getIns()+"\n");
		}
		
		textAreaCode.setText(binaryFile.toString());
		textAreaInfo.setText("Load Successfully!\n");
		return;
	}
	
	private String strToBinaryData(String str,int length) {
		boolean flag;
		String result;
		if(str.charAt(0)=='-') {
			flag=false;
			str=str.substring(1);
		}
		else {
			flag=true;
		}
		if(str.indexOf("0x")==0) {//16进制数
			String t=str.substring(2);
			if(t.length()>(length/4)) {
				return "";
			}
			else{
				StringBuilder hexStr = new StringBuilder();
				for(int m=0;m<t.length();m++) {
					if(Character.isLetterOrDigit(t.charAt(m))) {
						StringBuilder temp=new StringBuilder("0000");
						String temp1=Integer.toBinaryString(Integer.parseInt(t.substring(m,m+1),16));
						temp.replace(4-temp1.length(),4,temp1);
						hexStr.append(temp.toString());
					}
					else {
						
						return "";
					}
				}
				int len=length-hexStr.length();
				for(int m=0;m<len;m++) {
					hexStr.insert(0,'0');
				}
				result=hexStr.toString();;
				
			}
		}
		else if(str.indexOf("0b")==0) {//2进制数
			String t=str.substring(2);
			if(t.length()>length) {
				
				return "";
			}
			else{
				StringBuilder BinaryStr = new StringBuilder();
				for(int m=0;m<length;m++) {
					BinaryStr.append('0');
				}
				for(int m=0;m<t.length();m++) {
					if(t.charAt(m)!='0'&&t.charAt(m)!='1') {
						
						return "";
					}
				}
				BinaryStr.replace(length-t.length(), length, t);
				result=BinaryStr.toString();
				
			}
		}
		else { //10进制数或不合法的字符
			for(int m=0;m<str.length();m++) {
				if(!Character.isDigit(str.charAt(m))) {
					
					return "";
				}
			}
			StringBuilder temp=new StringBuilder();
			for(int m=0;m<length;m++) {
				temp.append('0');
			}
			String temp1=Integer.toBinaryString(Integer.parseInt(str));
			temp.replace(length-temp1.length(),length,temp1);
			result=temp.toString();
		}
		if(flag) {
			return result;
		}
		else {
			int pos=result.lastIndexOf('1');
			StringBuilder temp=new StringBuilder(result);
			if(pos==-1) {
				return result; 
			}
			else {
				for(int i=0;i<pos;i++) {
					if(temp.charAt(i)=='1') {
						temp.replace(i, i+1, "0");
					}
					else if(temp.charAt(i)=='0'){
						temp.replace(i, i+1, "1");
					}
				}
				return temp.toString();
			}
		}
	}
	
	private int registerNameToNumber(String name) {
		String[] register= {"zero","at","v0","v1","a0","a1","a2","a3","t0","t1","t2",
				"t3","t4","t5","t6","t7","s0","s1","s2","s3","s4","s5","s6","s7","t8","t9",
				"k0","k1","gp","sp","fp","ra"};
		if(name.length()==1) {
			if(Character.isDigit(name.charAt(0))) {
				return Integer.parseInt(name);
			}
			else {
				return -1;
			}
		}
		else if(name.length()==2||name.length()==4) {
			if(Character.isDigit(name.charAt(0))&&Character.isDigit(name.charAt(1))) {
				return Integer.parseInt(name);
			}
			else {
				for(int i=0;i<register.length;i++) {
					if(name.equals(register[i])) {
						return i;
					}
				}
				return -1;
			}
		}
		return -1;
	}
	
	private void loadAssemblyFile() {
		File fileToLoad=fileChooser.showOpenDialog(assemblyStage);
		FileReader reader;
		BufferedReader bReader;
		if(fileToLoad!=null) {
			try {
				reader = new FileReader(fileToLoad);
				bReader = new BufferedReader(reader);
				StringBuilder temp = new StringBuilder();
			    String str = "";
			    	try {
			    		while ((str =bReader.readLine()) != null) {
			    			temp.append(str + "\n");
			    			
			    		}
			    		try {
							bReader.close();
							textAreaAssembly.setText(temp.toString());
						} catch (IOException e) {
							
							textAreaInfo.setText("Load File Error!");
							e.printStackTrace();
							return;
						}
					} catch (IOException e) {
						
						textAreaInfo.setText("Load File Error!");
						e.printStackTrace();
						return;
					}
			        
			} catch (FileNotFoundException e) {
				
				textAreaInfo.setText("Load File Error!");
				e.printStackTrace();
				return;
			}
	        
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
