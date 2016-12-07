package com;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SearchProcess {
	private static IndexSearcher is = null;
	private static final int PAGESIZE = 20;
	
	public static ArrayList<Info> results (String keyWord, int pubNum, int filedNum, int curPage) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<Info> al = new ArrayList<Info>();
		
		FSDirectory fs = FSDirectory.open(new File("/mnt/einfo3/r1/"));
		IndexReader ir = DirectoryReader.open(fs);
		is = new IndexSearcher(ir);
		
		String [] fields = null;
		
		switch (filedNum) {
			case 8:
				fields = new String[] {"title", "content"};
				break;
			case 9:
				fields = new String[] {"title", "title"};
				break;
		}
		
		IKAnalyzer analyzer = new IKAnalyzer(true);
		
		MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_47, fields, analyzer);
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		
		BooleanQuery bQuery = new BooleanQuery();
		Query query = null;
		try {
			query = parser.parse(keyWord);
			bQuery.add(query, BooleanClause.Occur.MUST);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
				
		String pub = null;
		
		switch (pubNum) {
			case 1:
				pub = "新闻";
				break;
			case 2:
				pub = "微博";
				break;
			case 3:
				pub = "论坛";
				break;
			case 4:
				pub = "博客";
				break;
		}
		
		TermsFilter filter = null;
		Term term = new Term("publish", pub);
		if (pub != null)
			filter = new TermsFilter(term);
	
		Sort sort = new Sort();
		SortField sortField = new SortField("id", SortField.Type.LONG, true);
		sort.setSort(sortField);
		
		int begin = PAGESIZE * (curPage - 1);
		int seaNum = curPage * PAGESIZE;
		
		ScoreDoc[] scoreDoc = null;
		TopDocs topDocs = is.search(query, filter, seaNum, sort);
		scoreDoc = topDocs.scoreDocs;
		
		try {
			while (begin < scoreDoc.length && list.size() <= 20) {
				list.add(is.doc(scoreDoc[begin].doc).get("id"));
				begin++;
			}
		} catch (ArrayIndexOutOfBoundsException e1) {
			e1.printStackTrace();
		}
		ir.close();
		fs.close();
		
		// Highlight
		SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<font color='#C60A00'>", "</font>");
		QueryScorer scorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, scorer);
					
		for (int i = 0; i < list.size(); i++) {
			String fileName = list.get(i);
			long ts = Long.parseLong("1" + fileName) * 100;
			
			// Convert time stamp for compression file
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
			String date = sdf.format(new Date(ts));
			
			// Convert time stamp for xml file
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date1 = sdf1.format(new Date(ts));

			String filePathX = "/mnt/einfo0/articles/" + date + "/" + fileName + ".xml";
			
			File fileX = new File(filePathX);
			
			// Begin parse XML
			ParseXML parse = new ParseXML();
			al = parse.parse(fileX, date, date1, highlighter, fileName, al, keyWord);
		}
		list = null;
		return al;
	}
}
