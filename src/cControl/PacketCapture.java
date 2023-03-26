package cControl;
 
import java.io.IOException;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import jpcap.*;
import jpcap.packet.*;
 
/*ץ��*/
public class PacketCapture implements Runnable {
	private boolean flag = false;
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public boolean getFlag() {
		return this.flag;
	}
	
	NetworkInterface device;
	public static Integer count = 0;
	static DefaultTableModel tablemodel;
	static String FilterMess = "";
	static ArrayList<Packet> packetlist = new ArrayList<Packet>();
	static ArrayList<Packet> bak = new ArrayList<Packet>();
	public PacketCapture() {
	}
	public void setDevice(NetworkInterface device){
		this.device = device;
	}
	public void setTable(DefaultTableModel tablemodel){
		this.tablemodel = tablemodel;
	}
	public void setFilter(String FilterMess){
		this.FilterMess = FilterMess;
	}
	public void clearpackets(){
		count = 0;
		packetlist.clear();
		for(Packet p : bak) {
			if(TestFilter(p)) {
				packetlist.add(p);
				showTable(p);
				count++;
			}
		}
		
	}
	@Override
	public void run() {
		
		
		
		// TODO Auto-generated method stub
		Packet packet;
		try {
			JpcapCaptor captor = JpcapCaptor.openDevice(device, 65535,true, 20);
			//System.out.println(device.name);
			while(!this.flag){

				long startTime = System.currentTimeMillis();
				while (startTime + 600 >= System.currentTimeMillis()) {
					//captor.setFilter(FilterMess, true);
					packet = captor.getPacket();
					// ���ù�����
					if(packet!=null&&TestFilter(packet)){
						count++;
//						System.out.println(packet);
						packetlist.add(packet);
						bak.add(packet);
						showTable(packet);
						
					}
				}
				
				Thread.sleep(2000);
			}

			this.flag = false;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	//��ץ��������Ϣ��ӵ��б�
	public static void showTable(Packet packet){
		String[] rowData = getObj(packet);
		tablemodel.addRow(rowData);
	}
	//������ͨ���˷�����ȡPacket���б�
	public static ArrayList<Packet> getpacketlist(){
		return packetlist;
	}
	//���ù��˹���
	public static boolean TestFilter(Packet packet){
		if(FilterMess.contains("sip")){
			String sip = FilterMess.substring(4, FilterMess.length());
			if(new PacketAnalyze(packet).packetClass().get("ԴIP").equals(sip)){
				return true;
			}
		}else if(FilterMess.contains("dip")){
			String dip = FilterMess.substring(4, FilterMess.length());
			if(new PacketAnalyze(packet).packetClass().get("Ŀ��IP").equals(dip)){
				return true;
			}
		}else if(FilterMess.contains("ICMP")){
			if(new PacketAnalyze(packet).packetClass().get("Э��").equals("ICMP")){
				return true;
			}
		}
		else if(FilterMess.contains("UDP")){
			if(new PacketAnalyze(packet).packetClass().get("Э��").equals("UDP")){
				return true;
			}
		}else if(FilterMess.contains("TCP")){
			if(new PacketAnalyze(packet).packetClass().get("Э��").equals("TCP")){
				return true;
			}
		}else if(FilterMess.contains("keyword")){
			String keyword = FilterMess.substring(8, FilterMess.length());
			if(new PacketAnalyze(packet).packetClass().get("����").contains(keyword)){
				return true;
			}
		}else if(FilterMess.equals("")){
			return true;
		}
		return false;
	}
	//��ץ�İ��Ļ�����Ϣ��ʾ���б��ϣ�������Ϣ��String[]��ʽ
	public static String[] getObj(Packet packet){
		String[] data = new String[6];
		if (packet != null&&new PacketAnalyze(packet).packetClass().size()>=3) {
			Date d = new Date();
			DateFormat df = new SimpleDateFormat("HH:mm:ss");
			data[0]=df.format(d);
			data[1]=new PacketAnalyze(packet).packetClass().get("ԴIP");
			data[2]=new PacketAnalyze(packet).packetClass().get("Ŀ��IP");
			data[3]=new PacketAnalyze(packet).packetClass().get("Э��");
			data[4]=String.valueOf(packet.len);
		}
		return data;
	}
}