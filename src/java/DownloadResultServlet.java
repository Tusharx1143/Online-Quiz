import com.itextpdf.text.BaseColor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DownloadResultServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=result.pdf");

        // Get the username, score, and submitted answers from the request
        String username = request.getParameter("username");
        int score = Integer.parseInt(request.getParameter("score"));
        String[] submittedAnswers = request.getParameterValues("submittedAnswers");

        // Generate the PDF result
        generatePDFResult(response, username, score, submittedAnswers);
    }

    private void generatePDFResult(HttpServletResponse response, String username, int score, String[] submittedAnswers) {
        try {
            // Create a new document
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            // Open the document
            document.open();

            // Add content to the document
            document.add(new Paragraph("Quiz Result"));
            document.add(new Paragraph("Username: " + username));
            document.add(new Paragraph("Score: " + score));

            // Add answers to the document
            displayAnswers(document, submittedAnswers);

            // Close the document
            document.close();

            // Write the PDF document to the response output stream
            OutputStream out = response.getOutputStream();
            baos.writeTo(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

private void displayAnswers(Document document, String[] submittedAnswers) {
    try {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz", "root", "");

        PreparedStatement ps = con.prepareStatement("SELECT * FROM quiz_questions");
        ResultSet rs = ps.executeQuery();

        int questionNumber = 1;
        while (rs.next()) {
            String correctAnswer = rs.getString("correct_ans");
            String submittedAnswer = submittedAnswers[questionNumber - 1];

            String question = rs.getString("question");
            Paragraph questionParagraph = new Paragraph("Question " + questionNumber + ": " + question);
            document.add(questionParagraph);

            for (int optionNumber = 1; optionNumber <= 3; optionNumber++) {
                String option = rs.getString("option" + optionNumber);
                String optionText = optionNumber + ") " + option;

                // Check if the submitted answer matches the option
                if (submittedAnswer != null && submittedAnswer.equals(option)) {
                    // Check if the submitted answer is correct
                    if (submittedAnswer.equals(correctAnswer)) {
                        // Set the font color to green for correct answers
                        Paragraph optionParagraph = new Paragraph(optionText);
                        optionParagraph.getFont().setColor(BaseColor.GREEN);
                        document.add(optionParagraph);
                    } else {
                        // Set the font color to red for wrong answers
                        Paragraph optionParagraph = new Paragraph(optionText);
                        optionParagraph.getFont().setColor(BaseColor.RED);
                        document.add(optionParagraph);
                    }
                } else {
                    // Add options as regular paragraphs
                    Paragraph optionParagraph = new Paragraph(optionText);
                    document.add(optionParagraph);
                }
            }

            questionNumber++;
        }

        rs.close();
        ps.close();
        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
