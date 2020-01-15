package te.homework.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebUtilsTest {

    @Test
    void toJson() {
        assertEquals("{\"a\":\"b\"}", WebUtils.toJson("a", "b"));
        assertEquals("{\"a\":\"b\",\"c\":\"d\"}", WebUtils.toJson("a", "b", "c", "d"));
        assertEquals("{\"a\":\"b\",\"c\":\"d\",\"e\":\"f\"}",
                WebUtils.toJson("a", "b", "c", "d", "e", "f"));
        assertEquals("{\"a\":\"b\"}", WebUtils.toJson("a", "b", "c", "d", "e"));
    }
}