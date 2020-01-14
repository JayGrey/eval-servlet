package te.homework.web;

import te.homework.logic.Evaluate;
import te.homework.logic.EvaluateException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "evalServlet", urlPatterns = {"/eval"})
public class EvalServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String payload = getPayload(request);
        final String[] elements = payload.split("=");

        if (elements.length < 2) {
            final HashMap<String, String> map = new HashMap<>();
            map.put("status", "error");
            map.put("message", "request error");
            sendJson(response, map);
        } else {
            try {
                final double result = Evaluate.process(elements[1]);
                final HashMap<String, String> map = new HashMap<>();
                map.put("status", "ok");
                map.put("result", Double.toString(result));
                sendJson(response, map);
            } catch (EvaluateException e) {
                final HashMap<String, String> map = new HashMap<>();
                map.put("status", "error");
                map.put("message", "error processing expression");
                sendJson(response, map);
            }
        }
    }

    private String getPayload(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();
        try {
            final BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            return "";
        }
        return buffer.toString();
    }

    private void sendJson(HttpServletResponse response, Map<String, String> data) {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().print(toJson(data));
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String toJson(Map<String, String> values) {
        return values.entrySet().stream()
                .map(e -> String.format("\"%s\":\"%s\"", e.getKey(), e.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
    }
}
