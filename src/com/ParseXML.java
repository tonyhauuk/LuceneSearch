package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class ParseXML {
	private Info info = null;
	private DBConnector dbc = null;
	private static final String DOTS = "...";
	private static final Analyzer ANALYZER = new IKAnalyzer(true);

	public ArrayList<Info> parse(File fileX, String date, String date1, Highlighter highlighter, 
			String fileName, ArrayList<Info> lists, String keyWord) throws IOException {
		
		String rk = "<font color='#C60A00'>" + keyWord + "</font>";
		
		if (fileX.exists()) {
			
			SAXReader reader = new SAXReader();
			Document doc = null;

			try {
				doc = reader.read(fileX);
			
				Element elm = doc.getRootElement();
				
				Iterator<?> iter = elm.elementIterator("item");
				while (iter.hasNext()) {
					Element e = (Element) iter.next();
	
					String title = e.elementText("title");
					String txt = e.elementText("text");
					String link = e.elementText("link");
					String websource = e.elementText("websource");
					String text = HTMLSpirit.delHTMLTag(txt);
									
					// Set Title HightLighter
					Fragmenter fragTitle = new SimpleFragmenter(50);
					highlighter.setTextFragmenter(fragTitle);
					StringReader titleReader = new StringReader(title); 
					String hightLightTitle = null;
					if (title != null) {
						TokenStream tokenTitle = ANALYZER.tokenStream("title", titleReader);
						try {
							hightLightTitle = highlighter.getBestFragment(tokenTitle, title);
						} catch (InvalidTokenOffsetsException e1) {
							e1.printStackTrace();
						}
					}
	
					// Set description HightLighter
					Fragmenter fragText = new SimpleFragmenter(100);
					highlighter.setTextFragmenter(fragText);
					StringReader textReader = new StringReader(text);
					String hightLightText = null;
					if (text != null) {
						TokenStream tokenText = ANALYZER.tokenStream("content", textReader);
						try {
							hightLightText = highlighter.getBestFragment(tokenText, text);
						} catch (InvalidTokenOffsetsException e1) {
							e1.printStackTrace();
						}
					}
					
					info = new Info();
					
					// Judge multiple key word : Title
					if (keyWord.contains(" ")) {
						String[] mulitKey = keyWord.split(" ");
						
						if (title.length() > 40)
							title = title.substring(0, 39).concat(DOTS);
						
						for (String i : mulitKey) {
							String replace = "(?:" + i + ")";
							title = title.replaceAll(replace, "<font color='#C60A00'>" + i + "</font>");
						}
						
						info.setTitle(title);
					}
					else {
						if (title.contains(keyWord) && hightLightTitle != null) {
							if (title.length() > 50)
								info.setTitle(hightLightTitle.concat(DOTS));
							else
								info.setTitle(hightLightTitle);
						}
						else if (title.contains(keyWord) && hightLightTitle == null) {
							if (title.length() > 50)
								info.setTitle(title.substring(0, 49).replaceAll(keyWord, rk).concat(DOTS));
							else
								info.setTitle(title.replaceAll(keyWord, rk));
						}
						else {
							if (title.length() > 40)
								info.setTitle(title.substring(0, 39).concat(DOTS));
							else
								info.setTitle(title);
						}
					}
					
					info.setLink(link);
					info.setTime(date1);
					
					// Judge multiple key word : Text
					if (keyWord.contains(" ")) {
						String[] mulitKey = keyWord.split(" ");
						
						if (text.length() > 100)
							text = text.substring(0, 99).concat(DOTS);
						
						for (String i : mulitKey) {
							String replace = "(?:" + i + ")";
							text = text.replaceAll(replace, "<font color='#C60A00'>" + i + "</font>");
						}
						
						info.setText(text);
					}
					else {
						if (text.contains(keyWord) && hightLightText != null) {
							if (text.length() > 150)
								info.setText(hightLightText.concat(DOTS));
							else
								info.setText(hightLightText);
						}
						else if (text.contains(keyWord) && hightLightText == null) {
							if (text.length() > 150)
								info.setText(text.substring(0, 149).replaceAll(keyWord, rk).concat(DOTS));
							else
								info.setText(text.replaceAll(keyWord, rk));
						}
						else {
							if (text.length() > 100)
								info.setText(text.substring(0, 99).concat(DOTS));
							else
								info.setText(text);
						}
					}
	
					
					
					String orig = null;
					if (Integer.parseInt(websource) > 0) {
						dbc = new DBConnector();
						try {
							orig = dbc.origName(websource);
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
						}
						info.setOrigsrc(orig + "    ");
					}
					else
						info.setOrigsrc("无");
					
					info.setInlink("http://www.estar360.com/system/show_info.php?i_id=" + fileName);
				
					// Obtain reprint number
					String repnum = null;
					
					try {
						repnum = dbc.repNum(fileName);
					} catch (ClassNotFoundException | SQLException e1) {
						e1.printStackTrace();
					}
					
					if (Integer.parseInt(repnum) > 0) {
						info.setRepnum("  " + repnum + " 条相同新闻");
						info.setReplink("http://www.estar360.com/system/info_reprint.php?infoID=" + fileName);
					}
					else
						info.setRepnum("");
	
					lists.add(info);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				dbc.close();
			} catch (SQLException | NullPointerException e) {
				e.printStackTrace();
			}
		}
		// Search from zip file
		else {
			String filePathZ = "/mnt/einfo2/articles/" + date + ".z";
			File fileZ = new File(filePathZ);
			
			if (fileZ.exists()) {
				ZipInputStream zin = new ZipInputStream(new FileInputStream(filePathZ));
				ZipEntry entry;
				while ((entry = zin.getNextEntry()) != null) {
					String xmlName = fileName + ".xml";

					if (entry.getName().equals(xmlName)) {

						ZipFile zf = new ZipFile(filePathZ);
						ZipEntry ze = zf.getEntry(xmlName);
						InputStream is = zf.getInputStream(ze);

						SAXReader reader = new SAXReader();
						Document doc = null;
						
						try {
							doc = reader.read(is);

							Element elm = doc.getRootElement();
							
							Iterator<?> iter = elm.elementIterator("item");
							while (iter.hasNext()) {
								Element e = (Element) iter.next();

								String title = e.elementText("title");
								String txt = e.elementText("text");
								String link = e.elementText("link");
								String websource = e.elementText("websource");
								String text = HTMLSpirit.delHTMLTag(txt);
								
								// Set Title HightLighter
								Fragmenter fragTitle = new SimpleFragmenter(50);
								highlighter.setTextFragmenter(fragTitle);
								StringReader titleReader = new StringReader(title);
								String hightLightTitle = null;
								if (title != null) {
									TokenStream tokenTitle = ANALYZER.tokenStream("title", titleReader);
									try {
										hightLightTitle = highlighter.getBestFragment(tokenTitle, title);
									} catch (InvalidTokenOffsetsException e1) {
										e1.printStackTrace();
									}
								}

								// Set description HightLighter
								Fragmenter fragText = new SimpleFragmenter(100);
								highlighter.setTextFragmenter(fragText);
								StringReader textReader = new StringReader(text);
								String hightLightText = null;
								if (text != null) {
									TokenStream tokenText = ANALYZER.tokenStream("content", textReader);
									try {
										hightLightText = highlighter.getBestFragment(tokenText, text);
									} catch (InvalidTokenOffsetsException e1) {
										e1.printStackTrace();
									}
								}
								
								info = new Info();
								
								// Judge multiple key word : Title
								if (keyWord.contains(" ")) {
									String[] mulitKey = keyWord.split(" ");
									
									if (title.length() > 40)
										title = title.substring(0, 39).concat(DOTS);
									
									for (String i : mulitKey) {
										String replace = "(?:" + i + ")";
										title = title.replaceAll(replace, "<font color='#C60A00'>" + i + "</font>");
									}
									
									info.setTitle(title);
								}
								else {
									if (title.contains(keyWord) && hightLightTitle != null) {
										if (title.length() > 50)
											info.setTitle(hightLightTitle.concat(DOTS));
										else
											info.setTitle(hightLightTitle);
									}
									else if (title.contains(keyWord) && hightLightTitle == null) {
										if (title.length() > 50)
											info.setTitle(title.substring(0, 49).replaceAll(keyWord, rk).concat(DOTS));
										else
											info.setTitle(title.replaceAll(keyWord, rk));
									}
									else {
										if (title.length() > 40)
											info.setTitle(title.substring(0, 39).concat(DOTS));
										else
											info.setTitle(title);
									}
								}
								
								
								info.setLink(link);
								info.setTime(date1);
								
								// Judge multiple key word : Text
								if (keyWord.contains(" ")) {
									String[] mulitKey = keyWord.split(" ");
									
									if (text.length() > 100)
										text = text.substring(0, 99).concat(DOTS);
									
									for (String i : mulitKey) {
										String replace = "(?:" + i + ")";
										text = text.replaceAll(replace, "<font color='#C60A00'>" + i + "</font>");
									}
									
									info.setText(text);
								}
								else {
									if (text.contains(keyWord) && hightLightText != null) {
										if (text.length() > 150)
											info.setText(hightLightText.concat(DOTS));
										else
											info.setText(hightLightText);
									}
									else if (text.contains(keyWord) && hightLightText == null) {
										if (text.length() > 150)
											info.setText(text.substring(0, 149).replaceAll(keyWord, rk).concat(DOTS));
										else
											info.setText(text.replaceAll(keyWord, rk));
									}
									else {
										if (text.length() > 100)
											info.setText(text.substring(0, 99).concat(DOTS));
										else
											info.setText(text);
									}
								}

								
								
								String orig = null;
								if (Integer.parseInt(websource) > 0) {
									dbc = new DBConnector();
									orig = dbc.origName(websource);
									info.setOrigsrc(orig + "    ");
								}
								else
									info.setOrigsrc("无");
								
								info.setInlink("http://www.estar360.com/system/show_info.php?i_id=" + fileName);
								
								// Obtain reprint number
								String repnum = null;
								
								try {
									repnum = dbc.repNum(fileName);
								} catch (ClassNotFoundException | SQLException e1) {
									e1.printStackTrace();
								}
								
								if (Integer.parseInt(repnum) > 0) {
									info.setRepnum("  " + repnum + " 条相同新闻");
									info.setReplink("http://www.estar360.com/system/info_reprint.php?infoID=" + fileName);
								}
								else
									info.setRepnum("");

								lists.add(info);
							}
							try {
								dbc.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							if (zf != null) {
								zf.close();
								zf = null;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
				if (zin != null) {
					zin.close();
					zin = null;
				}
			}
		}
		return lists;
	}
}
