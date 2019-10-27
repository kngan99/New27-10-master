package Server;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Database {
    
	Node data;
    public String filePath;
    
    public Database(String filePath) {
        this.filePath = filePath;
    }
    
    public boolean userExists(String username) {
        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            fXmlFile.createNewFile();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            
            NodeList nList = doc.getElementsByTagName("user");
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (getTagValue("username", eElement).equals(username)) {
                        return true;
                    }
                }
            }
            return false;
        }
        catch(Exception ex) {
            System.out.println("Database exception: userExists()");
            return false;
        }
    }
    
    public String getListFriends(String username) {
        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            fXmlFile.createNewFile();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            
            NodeList nList = doc.getElementsByTagName("user");
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if (getTagValue("username", eElement).equals(username)) {
                        return getTagValue("friendlist", eElement);
                    }
                }
            }
            return "";
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
	public boolean checkLogin(String username, String password) {
        
        if (!userExists(username)) { return false; }
        
        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            
            NodeList nList = doc.getElementsByTagName("user");
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if(getTagValue("username", eElement).equals(username) && getTagValue("password", eElement).equals(password)){
                        return true;
                    }
                }
            }
            System.out.println("Hippie");
            return false;
        }
        catch(Exception ex) {
            System.out.println("Database exception: Fail()");
            return false;
        }
    }
    
	public void addFriendtoUser(String friendName, String userName) throws ParserConfigurationException, SAXException, IOException {
		
		 DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 Document doc = docBuilder.parse(filePath);		 
         data = doc.getFirstChild();
         NodeList nodeList = data.getChildNodes();
         Element oldElement = null;
         for (int i = 0; i < nodeList.getLength(); i++) {
        	 Node nNode = nodeList.item(i);
             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                 oldElement = (Element) nNode;
                 if (getTagValue("username", oldElement).equals(userName)) break;
             }
         }
         String[] infor = new String[3];
         infor[0] = getTagValue("username", oldElement);
         infor[1] = getTagValue("password", oldElement);
         infor[2] = getTagValue("friendlist", oldElement);
         if (infor[2].length() > 0) infor[2] = infor[2] + "," + friendName;
         else infor[2] += friendName;
         
         Element newElement = doc.createElement("user");
         Element newusername = doc.createElement("username"); newusername.setTextContent(infor[0]);
         Element newpassword = doc.createElement("password"); newpassword.setTextContent(infor[1]);
         Element friendList = doc.createElement("friendlist"); friendList.setTextContent(infor[2]);
         newElement.appendChild(newusername); newElement.appendChild(newpassword);  newElement.appendChild(friendList);
         
         data.replaceChild(newElement, oldElement);
         
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
	        StreamResult result = new StreamResult(new File(filePath));
			transformer.transform(source, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public void addUser(String username, String password){
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filePath);
 
            data = doc.getFirstChild();
            
            Element newuser = doc.createElement("user");
            Element newusername = doc.createElement("username"); newusername.setTextContent(username);
            Element newpassword = doc.createElement("password"); newpassword.setTextContent(password);
            Element friendList = doc.createElement("friendlist"); 
            newuser.appendChild(newusername); newuser.appendChild(newpassword);  newuser.appendChild(friendList); data.appendChild(newuser);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
 
	   } 
           catch(Exception ex){
		System.out.println("Exceptionmodify xml");
	   }
	}
    
    public static String getTagValue(String sTag, Element eElement) {
    	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }
}
