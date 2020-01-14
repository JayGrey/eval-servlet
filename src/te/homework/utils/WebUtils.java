package te.homework.utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface WebUtils {
    static String getPayload(HttpServletRequest request) {
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

    static void sendJson(HttpServletResponse response, Map<String, String> data) {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().print(toJson(data));
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    static String toJson(Map<String, String> values) {
        return values.entrySet().stream()
                .map(e -> String.format("\"%s\":\"%s\"", e.getKey(), e.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
    }

    static List<String> getLinesFromFilePart(HttpServletRequest request) throws IOException, ServletException {
        final Part filePart = request.getPart("file");
        final String filename = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream()));

        List<String> result = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().length() > 0) {
                result.add(line);
            }
        }

        return result;
    }
}
