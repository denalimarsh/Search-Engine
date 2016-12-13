import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class PartialResultServlet extends HttpServlet{
	
	private static final String TITLE = "partialResults";
	private final InvertedIndex index;
	
	public PartialResultServlet(InvertedIndex index){
		this.index = index;
	}
	
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		
		String[] query;
		if(request.getParameter("query").contains("+")){
			query = request.getParameter("query").split("\\s+");
		}else{
			query = request.getParameterValues("query");
		}
		
		String stringQuery = "";
		for(String queryElement : query){
			stringQuery += queryElement;
		}
		List<SearchResult> result = index.partialSearch(query);
		

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
        out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body>%n");
		
		out.printf("<h1 align =\"center\">Partial Results</h1>%n%n");
		out.printf("<p>" + "Partial search for \"" + stringQuery + "\" returned these results:" + "<p>");
		
		int i = 1;
		for(SearchResult s: result){
			out.printf("<p>" + i + " <a href=" + s.getFile() + ">" + s.getFile() + "</a><p>");
			i++;
		}
	
		
		out.printf("%n</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}
}