package mark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Tagging {
	private static IndexSearcher is = null;
	
	public static void tagRole(int role, int id, int amount) throws IOException {
		FSDirectory fs = FSDirectory.open(new File("/einfo/t1/"));
		//FSDirectory fs = FSDirectory.open(new File("/einfo/r1/"));
		IndexReader ir = DirectoryReader.open(fs);
		is = new IndexSearcher(ir);
		
		ArrayList<String> list = new ArrayList<String>();
		
		String [] fields = null;
		
		switch (id) {
			case 1:
				fields = new String[] {"title", "content"};
				break;
			case 2:
				fields = new String[] {"title", "title"};
				break;
		}
		
		ObtainKW getKW = new ObtainKW();
		String spStr = getKW.getKey(role);
		String[] keyWord = spStr.split("/");
		
		Analyzer analyzer = new IKAnalyzer(true);
		
		MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_47, fields, analyzer);
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		//parser.setDefaultOperator(QueryParser.Operator.AND);
		//BooleanClause.Occur[] clause = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
		
		BooleanQuery bQuery = new BooleanQuery();
		Query query = null;
		try {
			for (String i : keyWord) {
				query = parser.parse(i);
				//query = MultiFieldQueryParser.parse(Version.LUCENE_47, QueryParser.escape(i),fields, clause, analyzer);
				bQuery.add(query, BooleanClause.Occur.MUST);
				
				Sort sort = new Sort();
				SortField sortField = new SortField("id", SortField.Type.LONG, true);
				sort.setSort(sortField);
				
				ScoreDoc[] scoreDoc = null;
				TopDocs topDocs = is.search(query, amount, sort);
				
				scoreDoc = topDocs.scoreDocs;
				
				int j = 0;
				try {
					while (j < scoreDoc.length) {
						list.add(is.doc(scoreDoc[j].doc).get("id"));
						j++;
					}
				} catch (ArrayIndexOutOfBoundsException e1) {
					e1.printStackTrace();
				}
			}
			
			list = dedup(list);
			CreateXML.create(list, role);
			list = null;			
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		ir.close();
		fs.close();
	}
	
	private static ArrayList<String> dedup(ArrayList<String> list) {
		ArrayList<String> lists = new ArrayList<String>();
		for (int i = list.size() - 1; i >=0; i--) {
			String rep = list.get(i);
			
			if (lists.indexOf(rep) == -1)
				lists.add(0, rep);
		}
		return lists;
	}
}
