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

public class ResultServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Check if the user is logged in by verifying the session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username != null) {
            // User is logged in

            // Get the submitted answers from the session
            String[] submittedAnswers = new String[10]; // Adjust the array size based on the number of questions
            for (int i = 0; i < submittedAnswers.length; i++) {
                submittedAnswers[i] = (String) session.getAttribute("answer_" + (i + 1));
            }

            // Calculate the score
            int score = calculateScore(submittedAnswers);

            // Display the score and answers to the user
            PrintWriter out = response.getWriter();
            out.println("<html><head>");
            out.println("<title>Quiz Result</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f2f2f2; margin: 0; padding: 0; }");
            out.println("h1 { text-align: center; margin-top: 30px; }");
            out.println("p { text-align: center; margin-bottom: 10px; }");
            out.println(".question { font-weight: bold; margin-bottom: 5px; text-align: center; }");
            out.println(".options { text-align: center; }");
            out.println(".options li { margin-bottom: 5px; display: inline-block; text-align: left; }");
            out.println(".correct-answer { color: green; }");
            out.println(".incorrect-answer { color: red; }");
            out.println(".button-container { text-align: center; margin-top: 20px; }");
            out.println(".btn { display: inline-block; padding: 10px 20px; background-color: #4caf50; color: #ffffff; text-decoration: none; border-radius: 4px; transition: background-color 0.3s; cursor: pointer; border: none; margin-right: 10px; }");
            out.println(".btn:hover { background-color: #45a049; }");
            out.println(".btn-download { margin-left: 10px; }");
            out.println(".btn-play-again { background-color: #2196f3; }");
            out.println(".btn-home { background-color: #ff9800; }");
            out.println(".btn-logout { background-color: #f44336; }");
            out.println(".highlight { font-size: 20px; font-weight: bold; }");
            out.println(".center { text-align: center; }");
            out.println("table { border-collapse: collapse; width: 100%; }");
            out.println("th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("tr:hover { background-color: #f5f5f5; }");
            out.println("</style>");
            out.println("</head><body>");

            out.println("<h1>Quiz Result</h1>");
            out.println("<p><span class='highlight'>Username:</span> " + username + "</p>");
            out.println("<p><span class='highlight'>Score:</span> " + score + "</p>");

            displayAnswers(out, submittedAnswers, username, score);

            // Add buttons for Play Again and Home
            out.println("<div class='button-container'>");
            out.println("<a href='QuizServlet?restart=true' class='btn btn-play-again'>Play Again</a>");
            out.println("<a href='HomeServlet' class='btn btn-home'>Home</a>");
            out.println("<a href='LogoutServlet' class='btn btn-logout'>Logout</a>");
            out.println("</div>");


            // Add button for downloading PDF result
            out.println("<div class='button-container'>");
            out.println("<form method='post' action='DownloadResultServlet'>");
            out.println("<input type='hidden' name='username' value='" + username + "'>");
            out.println("<input type='hidden' name='score' value='" + score + "'>");
            for (int i = 0; i < submittedAnswers.length; i++) {
                out.println("<input type='hidden' name='submittedAnswers' value='" + submittedAnswers[i] + "'>");
            }
            out.println("<input type='submit' value='Download Result (PDF)' class='btn btn-download'>");
            out.println("</form>");
            out.println("</div>");

            out.println("</body></html>");
        } else {
            // User is not logged in, redirect to login page
            response.sendRedirect("login.html");
        }
    }

    private void displayAnswers(PrintWriter out, String[] submittedAnswers, String username, int score) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz", "root", "");

            PreparedStatement ps = con.prepareStatement("SELECT * FROM quiz_questions");
            ResultSet rs = ps.executeQuery();

            out.println("<table>");
            out.println("<tr><th>Question</th><th>Correct Answer</th><th>Your Answer</th></tr>");

            int questionNumber = 1;
            while (rs.next()) {
                String correctAnswer = rs.getString("correct_ans");
                String submittedAnswer = submittedAnswers[questionNumber - 1];

                String question = rs.getString("question");

                out.println("<tr>");
                out.println("<td>Question " + questionNumber + ": " + question + "</td>");
                out.println("<td>" + correctAnswer + "</td>");
                out.println("<td class='" + (submittedAnswer != null && submittedAnswer.equals(correctAnswer) ? "correct-answer" : "incorrect-answer") + "'>" + (submittedAnswer != null ? submittedAnswer : "-") + "</td>");
                out.println("</tr>");

                questionNumber++;
            }

            out.println("</table>");

            out.println("<p>Total Questions: " + submittedAnswers.length + "</p>");
            out.println("<p>Correct Answers: " + score + "</p>");
            out.println("<p>Incorrect Answers: " + (submittedAnswers.length - score) + "</p>");

            double percentage = (double) score / submittedAnswers.length * 100;
            out.println("<p>Congratulations! You have scored " + percentage + "% in the quiz.</p>");

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int calculateScore(String[] submittedAnswers) {
        int score = 0;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz", "root", "");

            PreparedStatement ps = con.prepareStatement("SELECT * FROM quiz_questions");
            ResultSet rs = ps.executeQuery();

            int questionNumber = 1;
            while (rs.next()) {
                String correctAnswer = rs.getString("correct_ans");
                String submittedAnswer = submittedAnswers[questionNumber - 1];

                if (submittedAnswer != null && submittedAnswer.equals(correctAnswer)) {
                    score++;
                }

                questionNumber++;
            }

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return score;
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
}
