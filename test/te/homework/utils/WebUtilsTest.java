package te.homework.utils;

import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WebUtilsTest {

    @Test
    void getLinesFromRequestBody() throws IOException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        assertEquals("", WebUtils.getLinesFromRequestBody(null));
        verify(request, never()).getReader();

        when(request.getReader()).thenThrow(new IOException());
        assertEquals("", WebUtils.getLinesFromRequestBody(request));
    }

    @Test
    void getLinesFromFilePart() throws IOException, ServletException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        assertEquals(0, WebUtils.getLinesFromFilePart(null).size());
        verify(request, never()).getPart(anyString());

        when(request.getPart(anyString())).thenThrow(new IOException(), new ServletException());
        assertEquals(0, WebUtils.getLinesFromFilePart(null).size());
        assertEquals(0, WebUtils.getLinesFromFilePart(null).size());
    }

    @Test
    void sendJson() throws IOException {
        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));
        WebUtils.sendJson(response, "some data");
        verify(response).setStatus(eq(HttpServletResponse.SC_OK));
        verify(response).setCharacterEncoding(eq("utf-8"));
        verify(response).setContentType(eq("application/json"));

        when(response.getWriter()).thenThrow(new IOException());
        WebUtils.sendJson(response, "");
        verify(response).setStatus(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    void sendJsonArray() throws IOException {
        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));
        WebUtils.sendJsonArray(response, null, null);
        verify(response).setStatus(eq(HttpServletResponse.SC_OK));
        verify(response).setCharacterEncoding(eq("utf-8"));
        verify(response).setContentType(eq("application/json"));
    }

    @Test
    void toJson() {
        assertEquals("{\"a\":\"b\"}", WebUtils.toJsonObject("a", "b"));
        assertEquals("{\"a\":\"b\",\"c\":\"d\"}", WebUtils.toJsonObject("a", "b", "c", "d"));
        assertEquals("{\"a\":\"b\",\"c\":\"d\",\"e\":\"f\"}",
                WebUtils.toJsonObject("a", "b", "c", "d", "e", "f"));
        assertEquals("{\"a\":\"b\"}", WebUtils.toJsonObject("a", "b", "c", "d", "e"));
    }
}