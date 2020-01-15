package te.homework.web;

import te.homework.logic.Evaluate;
import te.homework.logic.EvaluateException;
import te.homework.utils.WebUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "evalServlet", urlPatterns = {"/eval"})
public class EvalServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String payload = WebUtils.getLinesFromRequestBody(request);
        final String[] elements = payload.split("=");

        if (elements.length < 2) {

            WebUtils.sendJson(response,
                    WebUtils.toJsonObject(
                            "status", "error",
                            "message", "request error"
                    )
            );
        } else {
            try {
                final double result = Evaluate.process(elements[1]);

                WebUtils.sendJson(response,
                        WebUtils.toJsonObject(
                                "status", "ok",
                                "result", Double.toString(result)
                        )
                );

            } catch (EvaluateException e) {
                WebUtils.sendJson(response,
                        WebUtils.toJsonObject(
                                "status", "error",
                                "message", "error evaluating expression"
                        )
                );
            }
        }
    }
}
