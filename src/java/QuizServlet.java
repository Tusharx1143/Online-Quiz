import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class QuizServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Check if the user is logged in by verifying the session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username != null) {
            // User is logged in

            // Check if the current question index is stored in the session
            Integer currentQuestionIndex = (Integer) session.getAttribute("currentQuestionIndex");
            if (currentQuestionIndex == null) {
                // If not, set it to 0 for the first question
                currentQuestionIndex = 0;
                session.setAttribute("currentQuestionIndex", currentQuestionIndex);
            }

            // Retrieve quiz questions from the database
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz", "root", "");

                PreparedStatement ps = con.prepareStatement("SELECT * FROM quiz_questions");
                ResultSet rs = ps.executeQuery();

                PrintWriter out = response.getWriter();
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Quiz</title>");
                out.println("<style>");
                out.println(".quiz-form {");
                out.println("  max-width: 500px;");
                out.println("  margin: 0 auto;");
                out.println("  padding: 20px;");
                out.println("  background-color: #f1f1f1;");
                out.println("  text-align: center;");
                out.println("}");
                out.println(".quiz-form h3 {");
                out.println("  font-size: 18px;");
                out.println("  margin-top: 0;");
                out.println("}");
                out.println(".quiz-form p {");
                out.println("  margin-bottom: 15px;");
                out.println("  text-align: center;");
                out.println("}");
                out.println(".quiz-form input[type='radio'] {");
                out.println("  margin-bottom: 10px;");
                out.println("}");
                out.println(".quiz-form .btn {");
                out.println("  padding: 10px 20px;");
                out.println("  background-color: #4CAF50;");
                out.println("  color: white;");
                out.println("  border: none;");
                out.println("  cursor: pointer;");
                out.println("}");
                out.println(".quiz-form .btn-previous {");
                out.println("  margin-right: 10px;");
                out.println("}");
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");

                // Move the cursor to the current question index
                for (int i = 0; i < currentQuestionIndex; i++) {
                    if (!rs.next()) {
                        // End of questions reached, redirect to the result page
                        response.sendRedirect("ResultServlet");
                        return;
                    }
                }

                if (rs.next()) {
                    // Display the current question
                    int questionId = rs.getInt("question_id");
                    String question = rs.getString("question");
                    String option1 = rs.getString("option1");
                    String option2 = rs.getString("option2");
                    String option3 = rs.getString("option3");

                    out.println("<div class='quiz-form'>");
                    out.println("<h3>Question " + questionId + ":</h3>");
                    out.println("<p>" + question + "</p>");
                    out.println("<form method='post'>");

                    // Retrieve the user's previously selected answer for the current question
                    String previousAnswer = (String) session.getAttribute("answer_" + questionId);

                    out.println("<input type='radio' name='answer' value='" + option1 + "'" +
                            (option1.equals(previousAnswer) ? " checked" : "") + ">" + option1 + "<br>");
                    out.println("<input type='radio' name='answer' value='" + option2 + "'" +
                            (option2.equals(previousAnswer) ? " checked" : "") + ">" + option2 + "<br>");
                    out.println("<input type='radio' name='answer' value='" + option3 + "'" +
                            (option3.equals(previousAnswer) ? " checked" : "") + ">" + option3 + "<br>");
                    out.println("<br>");

                    // Display navigation buttons
                    if (currentQuestionIndex > 0) {
                        out.println("<input type='submit' name='previous' value='Previous' class='btn btn-previous'>");
                    }
                    out.println("<input type='submit' name='submit' value='Submit' class='btn btn-submit'>");
                    if (rs.next()) {
                        out.println("<input type='submit' name='next' value='Next' class='btn btn-next'>");
                    }

                    out.println("</form>");
                    out.println("</div>");

                    // Store the current question ID in the session
                    session.setAttribute("currentQuestionId", questionId);
                } else {
                    // No questions found in the database
                    out.println("<p>No questions available.</p>");
                }

                rs.close();
                ps.close();
                con.close();

                out.println("</body>");
                out.println("</html>");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // User is not logged in, redirect to login page
            response.sendRedirect("login.html");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer currentQuestionIndex = (Integer) session.getAttribute("currentQuestionIndex");

        // Get the selected answer from the request
        String selectedAnswer = request.getParameter("answer");

        // Store the selected answer in the session for the current question
        Integer currentQuestionId = (Integer) session.getAttribute("currentQuestionId");
        session.setAttribute("answer_" + currentQuestionId, selectedAnswer);

        // Handle navigation buttons
        if (request.getParameter("previous") != null && currentQuestionIndex > 0) {
            // Previous button clicked, decrement the current question index
            session.setAttribute("currentQuestionIndex", currentQuestionIndex - 1);
        } else if (request.getParameter("next") != null) {
            // Next button clicked, increment the current question index
            session.setAttribute("currentQuestionIndex", currentQuestionIndex + 1);
        } else if (request.getParameter("submit") != null) {
            // Submit button clicked, redirect to the result page
            response.sendRedirect("ResultServlet");
            return;
        }

        // Redirect back to the QuizServlet to display the next question
        response.sendRedirect("QuizServlet");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public String getServletInfo() {
        return "Quiz Servlet";
    }
}