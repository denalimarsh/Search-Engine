import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
/**
 *  Server servlet which displays exact search results
 *
 */
public class ExactResultServlet extends HttpServlet{
	
	private static final String TITLE = "exactResults";
	private final InvertedIndex index;
	
	public ExactResultServlet(InvertedIndex index){
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
		
		List<SearchResult> result = index.exactSearch(query);

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
        out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body>%n");
		
		out.printf("<h1 align =\"center\">Exact Results</h1>%n%n");
		out.printf("<p>" + "Exact search for \"" + stringQuery + "\" returned these results:" + "<p>");
		
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