import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HomeServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Check if the user is logged in by verifying the session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username != null) {
            // User is logged in

            // Calculate the login duration

            // Generate the HTML response
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>Home Page</title>");
            out.println("<style>");
            out.println("body {");
            out.println("    font-family: Arial, sans-serif;");
            out.println("    background-color: #f2f2f2;");
            out.println("    padding: 20px;");
            out.println("    text-align: center;");
            out.println("}");
            out.println("h2 {");
            out.println("    color: #333333;");
            out.println("    margin-bottom: 20px;");
            out.println("}");
            out.println("p {");
            out.println("    color: #666666;");
            out.println("    margin-bottom: 10px;");
            out.println("}");
            out.println("a {");
            out.println("    display: inline-block;");
            out.println("    margin-right: 10px;");
            out.println("    padding: 10px 20px;");
            out.println("    background-color: #4caf50;");
            out.println("    color: #ffffff;");
            out.println("    text-decoration: none;");
            out.println("    border-radius: 4px;");
            out.println("    transition: background-color 0.3s;");
            out.println("}");
            out.println("a:hover {");
            out.println("    background-color: #45a049;");
            out.println("}");
            out.println(".illusion-container {");
            out.println("    position: relative;");
            out.println("    margin-top: 30px;");
            out.println("    perspective: 500px;");
            out.println("}");
            out.println(".illusion-text {");
            out.println("    position: absolute;");
            out.println("    top: 50%;");
            out.println("    left: 50%;");
            out.println("    transform: translate(-50%, -50%) rotateY(0deg);");
            out.println("    font-size: 36px;");
            out.println("    font-weight: bold;");
            out.println("    color: #999999;");
            out.println("    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);");
            out.println("    animation: illusion 3s linear infinite;");
            out.println("}");
            out.println("@keyframes illusion {");
            out.println("    0% { transform: translate(-50%, -50%) rotateY(0deg); }");
            out.println("    100% { transform: translate(-50%, -50%) rotateY(360deg); }");
            out.println("}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Welcome, " + username + "!</h2>");
            out.println("<a href='QuizServlet'>Quiz</a>");
            out.println("<a href='LogoutServlet'>Logout</a>");
            out.println("<div class=\"illusion-container\">");
           // out.println("<div class=\"illusion-text\">Keep thinking it never goes to waste !!</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        } else {
            // User is not logged in, redirect to login page
            response.sendRedirect("login.html");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Home Servlet";
    }
}
