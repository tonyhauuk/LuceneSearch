package mark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class CreateXML {
	public static void create(ArrayList<String> list, int roleID) {
		try {
			XMLWriter writer = null;
			Document doc = buildXML(roleID, list);
			
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			
			String filePath = "/eprogram/estar/manage/program/biaozhu/" + roleID + ".xml";
			File file = new File(filePath);
			 
			if (file.exists()) {
				writer = new XMLWriter(new FileWriter(file), format);
				writer.write(doc);
				writer.flush();
				writer.close();
			}
			else if (!file.exists()){
				writer = new XMLWriter(new FileWriter(file), format);
				writer.write(doc);
				writer.flush();
				writer.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Document buildXML(int roleID, ArrayList<String> list)	{
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("document");
		Element role = root.addElement("roldID");
		role.setText(String.valueOf(roleID));
		
		Element infos = root.addElement("infos");
		
		for (int i = 0; i < list.size(); i++) {
			Element info = infos.addElement("info");
			Element infoID = info.addElement("infoID");
			infoID.setText(list.get(i));
		}
		return doc;
	}
}
