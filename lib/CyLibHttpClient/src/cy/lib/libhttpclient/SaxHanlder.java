package cy.lib.libhttpclient;

import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHanlder extends DefaultHandler {
	//存放所有的节点（这里的节点等于原来的节点+编号）以及它所对应的值
	  private HashMap<String,String> hashMap = new HashMap<String,String>();
	  //目前的节点
	  private String currentElement = null;
	  //目前节点所对应的值
	  private String currentValue = null;
	  //用于节点编号（具体到person）
	  private static int i=-1;
	  private StringBuilder builder;
	  @Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		builder = new StringBuilder();
	}

	  public HashMap getHashMap() {
	    return hashMap;
	  }

	  public void characters(char[] ch, int start, int length) throws SAXException {
	    //取出目前节点对应的值
	    currentValue = new String(ch, start, length);
	    builder.append(ch, start, length);  //将读取的字符数组追加到builder中
	  }

	  public void startElement(String uri, String localName, String qName,
	                           Attributes attr) throws SAXException {

		 // System.out.println(qName);
	    builder.setLength(0);   //将字符长度设置为0 以便重新开始读取元素内的字符节点

	  }

	  public void endElement(String uri, String localName, String qName) throws SAXException {
		  if(qName.equalsIgnoreCase("present_available_point")){
			  System.out.println("present_available_point:" + builder.toString());

		  }else if(qName.equalsIgnoreCase("boss_available_point")){
			  System.out.println("boss_available_point:" + builder.toString());
		  }else if(qName.equalsIgnoreCase("PRACTIC_POINT")){
			  System.out.println("PRACTIC_POINT:" + builder.toString());
		  }else if(qName.equalsIgnoreCase("total")){
			  System.out.println("total:" + builder.toString());
		  }else if(qName.equalsIgnoreCase("jf")){
			  System.out.println("0");
		  }
		  else if(qName.equalsIgnoreCase("mer")){
			  System.out.println("1");
		  }
		  else if(qName.equalsIgnoreCase("tim")){
			  System.out.println("2");
		  }
		  else if(qName.equalsIgnoreCase("vip_card")){
			  System.out.println("3");
		  }
	   if(qName.equalsIgnoreCase("Property")){
	    	//System.out.println(myData.toString());

	    }
	  }}