import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class Joongang {

	String crawlURL = "";
	Document doc;
	DBManager db = null;
	
	//HTTP��û�� ���� �⤱��������
	private String tempUrl; //������Url
	private HttpGet http;
	private HttpClient httpClient;
	private HttpResponse response;
	private HttpEntity entity;

	private BufferedReader br;
	private StringBuffer sb;

	String result = ""; //doc = Jsoup.parse(result);	
	
	public Joongang(DBManager db) {
		// TODO Auto-generated constructor stub
		this.db = db;
	}

	@SuppressWarnings("null")
	public Article doParse (String crawlURL) {
		Article art=new Article();
		
		//������������ URL�� Dom��ü
			if(!crawlURL.contains("tt")) return art;
			this.doc = getDOM(crawlURL);
			Element list = doc.body();
			
			//�� ���������� ��� 10��
			int n = 0;
			
			//data
			String url = "";
			String title ="";
			String originalTitle ="";
			
			String genres="";
			String story = "";
			String country = "";
			String alsoKnown = "";
			float rate = 0;
			int rateCount = 0;
			String year = "";
			String category = "";
			
						
			//select css query
			
			url = crawlURL;
			title = list.select("#title-overview-widget > div.vital > div.title_block > div > div.titleBar > div.title_wrapper > h1").text().replace("&nbsp;", "");
			if (title.contains("(")) title = title.substring(0,title.indexOf("(")-1);
			else title = title.substring(0,title.length()-1);
			try{
				originalTitle  = list.select("#title-overview-widget > div.vital > div.title_block > div > div.titleBar > div.title_wrapper > div.originalTitle").text().replace(" (original title)", "");
			} catch(Exception e) {
				originalTitle="";
			}
			try {
//				genres = list.select("").toString();
				story = list.select("#title-overview-widget > div.plot_summary_wrapper > div.plot_summary > div.summary_text").text();
//				country = list.select("").toString();
//				alsoKnown = list.select("").toString();
				rate = Float.parseFloat(list.select("#title-overview-widget > div.vital > div.title_block > div > div.ratings_wrapper > div.imdbRating > div.ratingValue > strong > span").text());
				rateCount = Integer.parseInt(list.select("#title-overview-widget > div.vital > div.title_block > div > div.ratings_wrapper > div.imdbRating > a > span").text().replace(",",""));
				year = list.select("#titleUserReviewsTeaser > div > span > div.comment-meta > meta").attr("content");
//				category = list.select("#title-overview-widget > div.vital > div.title_block > div > div.titleBar > div.title_wrapper > div > a:nth-child(8)").toString();	
			} catch(Exception e) {
				System.out.println("error");
			}
						
						
			//article��ü�� ���� setting
			art.setUrl(url);
			art.setTitle(title);
			art.setOriginalTitle(originalTitle);
//			art.setGenres(genres);
			art.setStory(story);
//			art.setCountry(country);
//			art.setAlsoKnown(alsoKnown);
			art.setRate(rate);
			art.setRateCount(rateCount);
			art.setYear(year);
//			art.setCategory(category);
			
			
						
		//DB���� �� �α�
		int items=1;		
		System.out.print(" "+Launch.count++);
		//DB����
		if (Launch.enableDB){
			db.runSQL(art);
		}
		
		return art;
	}
	
	//http ����
	public Document getDOM(String crawlURL) {
		Document doc = null;
		
		//http��û �� doc�� parse��� ����
		try{
			// Http ��û�ؼ� doc�� �������
			http = new HttpGet(crawlURL); //tempUrl ����
			httpClient = HttpClientBuilder.create().build();
			response = httpClient.execute(http);
			entity = response.getEntity();
			ContentType content = ContentType.getOrDefault(entity);
			Charset charset = content.getCharset();
			charset = content.getCharset();
			br = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			doc = Jsoup.parse(result); //doc�� tempUrl�� DOM����
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
}
