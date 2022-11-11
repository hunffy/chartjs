package chart;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//http://localhost:8088/chartjs/select 
@WebServlet("/select")
public class SelectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public SelectServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
								throws ServletException, IOException {
	try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection conn = DriverManager.getConnection
				("jdbc:oracle:thin:@localhost:1521:xe","kic","1234");
		String sql = "select writer, count(*) from board group by writer";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		//rs: sql구문 결과들을 담고있음
		Map<String, Integer> map = new HashMap<>();
			while(rs.next()) {
				//rs.getString(1) :writer 컬럼값
				//rs.getInt(2) : count(*) 값
				map.put(rs.getString(1), rs.getInt(2));
			}
			
			List<String> list1 = new ArrayList<>(); //writers(글쓴이)데이터
			List<Integer> list2 = new ArrayList<>();//datas(건수)데이터
			for(Map.Entry<String, Integer> e : map.entrySet()) {
				list1.add(e.getKey()); 		//작성자 정보들만 리스트로 저장
				list2.add(e.getValue()); 	//건수 정보들만 리스트로 저장
			}
			
			Map<String,String> result = new HashMap<>();
			StringBuilder writers = new StringBuilder();
			StringBuilder datas = new StringBuilder();
			/*
			 * writers :
			 * ["홍길동","김삿갓","이몽룡"] => 자바스크립트의 배열값
			 */
			writers.append("[");
			for(int i=0; i<list1.size(); i++) {
				writers.append("\""+list1.get(i)+"\""
						+ (i==list1.size()-1?"":","));
			}
			writers.append("]");
			/*
			 * datas :
			 * ["3","5","7"] => 자바스크립트의 배열값
			 */
			datas.append("[");
			for(int i=0; i<list2.size(); i++) {
				datas.append(list2.get(i) + (i==list2.size()-1?"":","));
			}
			datas.append("]");
			result.put("\"writers\"", writers.toString());
			result.put("\"datas\"", datas.toString());
			//브라우저로 전송할 출력스트림
			PrintWriter out = response.getWriter();
			System.out.println(result);
			String outData = result.toString().replace("=", ":");
			response.setContentType("text/plain; charset=utf-8");
			response.setCharacterEncoding("utf-8");
			//{"writers"=["9","이몽룡","10"], "datas"=[1,1,1]}
			//=> {"writers":["9","이몽룡","10"], "datas":[1,1,1]}
			out.println(outData);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
