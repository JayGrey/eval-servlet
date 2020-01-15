package te.homework.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class EvaluateTest {

    @Test
    void process() {

        assertEquals(2.0, Evaluate.process("2"));
        assertEquals(2.1, Evaluate.process("2.1"));

        assertEquals(1, Evaluate.process("1 - 1 + 1"));

        assertEquals(4.0, Evaluate.process("2  + 2"));
        assertEquals(6.0, Evaluate.process("2  + 2 * 2"));
        assertEquals(8.0, Evaluate.process("(2  + 2) * 2"));

        assertEquals(0.0, Evaluate.process("2 - 2"));
        assertEquals(-1.0, Evaluate.process("2 - 3"));

        assertEquals(15, Evaluate.process("3 * 5"));

        assertEquals(0.5, Evaluate.process("1 / 2"));
        assertEquals(2, Evaluate.process("2 / 1"));

        assertEquals(18, Evaluate.process("2 + 6 / 2 * 7 - 5"));

        assertEquals(1.5, Evaluate.process("1 + ( (2 * 3) - 6 + (4/ 2) ) / 4 "));
        assertEquals(0, Evaluate.process("( 1 + 2 - 3)"));

        assertEquals(5.4, Evaluate.process("1.54 + 3.86"));

        assertEquals(-2, Evaluate.process("-2"));
        assertEquals(1, Evaluate.process("-2 + 3"));
        assertEquals(-1, Evaluate.process("-2 + 1"));
        assertEquals(0, Evaluate.process("2 + -2"));
        assertEquals(4, Evaluate.process("-2 * -2"));
    }

    @Test
    void processIncorrectInputs() {
        try {
            Evaluate.process("+ ");
            fail();
        } catch (EvaluateException ignore) {
        }

        try {
            Evaluate.process(" 1 + ");
            fail();
        } catch (EvaluateException ignore) {
        }

        try {
            Evaluate.process(" 2 / ( 1 + ");
            fail();
        } catch (EvaluateException ignore) {
        }

        try {
            Evaluate.process(" 2 /  1 + )");
            fail();
        } catch (EvaluateException ignore) {
        }

        try {
            Evaluate.process(" ()");
            fail();
        } catch (EvaluateException ignore) {
        }
    }
}