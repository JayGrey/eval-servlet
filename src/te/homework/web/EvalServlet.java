package te.homework.web;

import te.homework.logic.Evaluate;
import te.homework.logic.EvaluateException;
import te.homework.utils.WebUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@WebServlet(name = "evalServlet", urlPatterns = {"/eval"})
public class EvalServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String payload = WebUtils.getPayload(request);
        final String[] elements = payload.split("=");

        if (elements.length < 2) {
            final HashMap<String, String> map = new HashMap<>();
            map.put("status", "error");
            map.put("message", "request error");
            WebUtils.sendJson(response, map);
        } else {
            try {
                final double result = Evaluate.process(elements[1]);
                final HashMap<String, String> map = new HashMap<>();
                map.put("status", "ok");
                map.put("result", Double.toString(result));
                WebUtils.sendJson(response, map);
            } catch (EvaluateException e) {
                final HashMap<String, String> map = new HashMap<>();
                map.put("status", "error");
                map.put("message", "error processing expression");
                WebUtils.sendJson(response, map);
            }
        }
    }
}
