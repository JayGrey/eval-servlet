package te.homework.web;

import te.homework.logic.Evaluate;
import te.homework.logic.EvaluateException;
import te.homework.utils.WebUtils;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@WebServlet(name = "evalFileServlet", urlPatterns = {"/eval-file"})
@MultipartConfig
public class EvalFileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        List<String> lines = WebUtils.getLinesFromFilePart(request);

        List<EvalData> result = new ArrayList<>();

        for (String line : lines) {
            try {
                final String output = Double.toString(Evaluate.process(line));
                result.add(EvalData.of(line, output, false));
            } catch (EvaluateException e) {
                result.add(EvalData.of(line, "error evaluating expression", true));
            }
        }

        WebUtils.sendJsonArray(response, EvalData.mapper(), result);
    }

    private static class EvalData {
        final String input;
        final String output;
        final boolean error;

        public EvalData(String input, String output, boolean error) {
            this.input = input;
            this.output = output;
            this.error = error;
        }

        private static EvalData of(String input, String output, boolean error) {
            return new EvalData(input, output, error);
        }

        private static Function<EvalData, String> mapper() {
            return evalData -> WebUtils.toJsonObject(
                    "status", evalData.error ? "error" : "ok",
                    "input", evalData.input,
                    "output", evalData.output
            );
        }
    }
}
