package te.homework.web;

import te.homework.logic.Evaluate;
import te.homework.logic.EvaluateException;
import te.homework.utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "evalFileServlet", urlPatterns = {"/eval-file"})
@MultipartConfig
public class EvalFileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) {

        try {
            List<String> lines = WebUtils.getLinesFromFilePart(request);
            System.out.println(lines);

            List<EvalData> result = new ArrayList<>();

            for (String line : lines) {
                try {
                    final String output = Double.toString(Evaluate.process(line));
                    result.add(EvalData.of(line, output));
                } catch (EvaluateException e) {
                    result.add(EvalData.of(line, "error evaluating expression"));
                }
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(result.stream()
                    .map(this::toJsonObject)
                    .collect(Collectors.joining(", ", "[", "]"))
            );

        } catch (IOException | ServletException e) {
            e.printStackTrace(System.err);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String toJsonObject(EvalData evalData) {
        return String.format("{\"input\": \"%s\", \"output\": \"%s\"}",
                evalData.input, evalData.output);
    }

    private static class EvalData {
        final String input;
        final String output;

        public EvalData(String input, String output) {
            this.input = input;
            this.output = output;
        }

        private static EvalData of(String input, String output) {
            return new EvalData(input, output);
        }
    }
}
