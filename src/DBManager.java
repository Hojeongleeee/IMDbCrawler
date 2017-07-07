import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class DBManager {
	static final String driverName = "org.gjt.mm.mysql.Driver";
	static final String dbURL = "jdbc:mysql://localhost:3306/youtube";
	
	String sql;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	java.sql.PreparedStatement pstmt;
	int n=0;
	int duplicate = 0;
	//생성자로 DB세팅
	public DBManager(){
		try{
			con = DriverManager.getConnection(dbURL,"root","websys");
			stmt = con.createStatement();
		} catch (SQLException sqex) {
			sqex.printStackTrace();
		}
	}//DBManager() constructor

	//쿼리 실행하는 메소드
	public void runSQL(Article art){
		//SQL 실행
		try {
			//preparedStatement에 번호순대로 집어넣기 (setString() 등)
			pstmt = con.prepareStatement("insert into youtube_video (URL, Title, Content, Like, Dislike, Subscribe, Comment, Total, Date, Channel_URL, Channel) values (?,?,?,?,?,?,?,?,?,?,?);");
//			pstmt = con.prepareStatement("CALL insertArticle(?, ?, ?, ?, ?, ?)");
			/** SQL 순서
			 * 1. Title
			 * 
			 * 
			 */
			
//			pstmt.setString(11, art.getChannel());
			n++;
			
			if(pstmt.execute()){ //sql 여기서도 실행됨... 둘중하나!
				rs = pstmt.getResultSet(); //결과 (NullPointerException)
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} //runSQL
	
	public void closeDB(){
		if(rs!=null) {try{ rs.close(); } catch (Exception e){ e.printStackTrace(); }}
		if(stmt!=null) {try{ stmt.close(); } catch (Exception e){ e.printStackTrace(); }}
		if(con!=null) {try{ con.close(); } catch (Exception e){ e.printStackTrace(); }}
	}
}//class