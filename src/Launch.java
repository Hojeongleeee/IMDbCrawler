import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Launch {
	public static int count = 0; //��ü URL ��
	public static int error = 0;
	public static String str = "";
	public static final boolean enableDB = false; //DB���忩��
	public static final boolean enableWrite = true; //DB���忩��
	static int hour=3; //�ݺ��ֱ�: 3�ð�����

	public static void main(String[] args) {
		while(true){
			double starttime = System.currentTimeMillis();
			DBManager db = new DBManager();
			
			//����&����
			long time = System.currentTimeMillis();
			SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			System.out.println("current time:"+currentTime.format(new Date(time)));			
	
			//��� URL�� ����! TODO: urlSet�� csv�� �޾Ƽ� list�� �Ҵ��ؾ���
			List<String>urlSet = new ArrayList<String>();
	        List<String[]> list = new ArrayList<String[]>();
	        CSVRead read = new CSVRead();
	        list = read.readCsv();
	 
	        Iterator<String[]> it = list.iterator();
	        while (it.hasNext()) {
	            String[] array = (String[]) it.next();
	            urlSet.add(array[0]);
//	            System.out.println(array[0]);
	            for (String s : array) {
//	                System.out.print(s + " ");
	            }
//	            System.out.print("\n");
	        }
	        
	        //Crawl and Save in data (List)
	        List<String[]> write = new ArrayList<String[]>();
			write.add(new String[] {"URL","Title","OriginalTitle","Story","Rate","RateCount","ReleasedDate","Genres","Category"});
			for (String crawlURL : urlSet){
				Joongang jn = new Joongang(db);
				Article art = jn.doParse(crawlURL);
		        write.add(new String[] {
		        		art.getUrl()
		        		,art.getTitle()
		        		,art.getOriginalTitle()
		        		,art.getStory()
		        		,art.getRate()+""
		        		,art.getRateCount()+""
		        		,art.getYear()
		        		,art.getGenres()
		        		,art.getCategory()
		        		}
		        );
			}//for
	   	 	
			//CSVWrite
	        CSVWrite cw = new CSVWrite();
	        if(enableWrite)cw.writeCsv(write);
			
			//���
			System.out.println("Article:"+count);
			
			double endtime = System.currentTimeMillis();
			System.out.println("running time:"+(endtime-starttime)/1000);
			System.out.println("Registered Article:"+(count-db.duplicate));
			
			try {
				Thread.sleep(1000*60*60*hour); //3�ð�
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} //while
	}

}