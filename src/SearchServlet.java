import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {
	
	private static final String TITLE = "Search";

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.printf("<html>%n%n");
        out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body>%n");
		
		out.printf("<h1 align = \"center\">Denali Search</h1>%n%n");
		
		out.printf("<div align=\"center\">");
		out.printf("<form action = \"partialResults\">%n");
		out.printf("<input type =\"text\" name=\"query\">&nbsp");
		out.printf("<input type =\"submit\" name=\"partial\" value = \"Partial Search\">");
		out.printf("</form>%n");
		
		out.printf("<form action = \"exactResults\">%n");
		out.printf("<input type =\"text\" name=\"query\">&nbsp");
		out.printf("<input type =\"submit\" name=\"exact\" value = \"Exact Search\">");
		out.printf("</form>%n");
		
		out.printf("</div>");

		out.printf("%n</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}
}