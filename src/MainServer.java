import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *  Server class which starts and manages the server
 *
 */
public class MainServer {

	private static int port;
	private final InvertedIndex index;
	
	MainServer(InvertedIndex inputIndex, int portNumber){
		MainServer.port = portNumber;
		this.index = inputIndex;
	}
	
	/**
	 * Starts the main server, adding servlets as needed
	 */
	public void startServer() {
		
		Server server = new Server(port);

		ServletContextHandler context1 = new ServletContextHandler();
		context1.setContextPath("/");

		context1.addServlet(SearchServlet.class, "/");
		context1.addServlet(new ServletHolder(new PartialResultServlet(index)), "/partialResults");
		context1.addServlet(new ServletHolder(new ExactResultServlet(index)), "/exactResults");
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { context1 });

		server.setHandler(handlers);
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}