package te.homework.utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface WebUtils {
    static String getLinesFromRequestBody(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

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

    static List<String> getLinesFromFilePart(HttpServletRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }

        try {
            final Part filePart = request.getPart("file");

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(filePart.getInputStream()));

            List<String> result = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    result.add(line);
                }
            }
            return result;

        } catch (IOException | ServletException e) {
            e.printStackTrace(System.err);
        }

        return Collections.emptyList();
    }

    static void sendJson(HttpServletResponse response, String data) {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().print(data);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    static <T> void sendJsonArray(HttpServletResponse response, Function<T, String> mapper,
                                  List<T> array) {
        sendJson(response, toJsonArray(mapper, array));
    }

    static String toJsonObject(String key, String value, String... entries) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(String.format("\"%s\":\"%s\"", key, value));
        if (entries.length % 2 == 0) {
            for (int i = 0; i < entries.length; i += 2) {
                buffer.append(',').append(String.format("\"%s\":\"%s\"", entries[i],
                        entries[i + 1]));
            }
        }
        buffer.insert(0, '{');
        buffer.insert(buffer.length(), '}');

        return buffer.toString();
    }

    static <T> String toJsonArray(Function<T, String> objectMapper, List<T> array) {
        return array.stream()
                .map(objectMapper)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
