package te.homework.logic;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Evaluate {
    public static double process(String expression) {
        // convert to RPN
        List<Token> tokens = convertToRPN(expression);

        // evaluate RPN
        return evaluateRPN(tokens);
    }

    private static List<Token> convertToRPN(String expression) {
        if (expression == null || expression.trim().length() == 0) {
            throw new EvaluateException();
        }

        StringBuilder buffer = new StringBuilder();

        List<Token> result = new LinkedList<>();
        Deque<Token> stack = new LinkedList<>();

        boolean isUnary = true;

        for (char ch : expression.toCharArray()) {

            if (Character.isSpaceChar(ch)) {
                continue;
            }

            if (Character.isDigit(ch) || ch == '.') {
                buffer.append(ch);
                continue;
            }

            if (buffer.length() > 0) {
                result.add(new Token(Double.parseDouble(buffer.toString())));
                buffer.delete(0, buffer.length());
                isUnary = false;
            }

            if (Token.isOperator(ch)) {

                //change sign
                if (ch == '-' && isUnary) {
                    stack.push(Token.REVERSE_OPERATION);
                    continue;
                }

                final Token token = Token.of(ch);
                while (!stack.isEmpty() && Token.comparePriority(stack.peek(), token) >= 0) {
                    result.add(stack.pop());
                }
                stack.push(token);

                isUnary = true;

                continue;
            }

            if (ch == '(') {
                stack.push(Token.OPEN_BRACKET);
                isUnary = true;
                continue;
            }

            if (ch == ')') {
                while (true) {
                    if (stack.isEmpty()) {
                        throw new EvaluateException("brackets not balanced");
                    }

                    final Token token = stack.pop();
                    if (token == Token.OPEN_BRACKET) {
                        break;
                    } else {
                        result.add(token);
                    }
                }
            }

        }

        if (buffer.length() > 0) {
            result.add(new Token(Double.parseDouble(buffer.toString())));
        }

        while (!stack.isEmpty()) {
            final Token token = stack.pop();
            if (token == Token.OPEN_BRACKET) {
                throw new EvaluateException("brackets not balanced");
            }
            result.add(token);
        }

        return result;
    }

    private static double evaluateRPN(List<Token> tokens) {
        if (tokens == null || tokens.size() == 0) {
            throw new EvaluateException("incorrect input");
        }

        Deque<Token> stack = new LinkedList<>();

        for (Token token : tokens) {
            switch (token.type) {
                case DIGIT: {
                    stack.push(token);
                    break;
                }

                case ADD: {
                    if (stack.size() < 2) {
                        throw new EvaluateException();
                    }

                    stack.push(new Token(stack.pop().val + stack.pop().val));
                    break;
                }

                case SUBTRACT: {
                    if (stack.size() < 2) {
                        throw new EvaluateException();
                    }

                    double b = stack.pop().val;
                    double a = stack.pop().val;
                    stack.push(new Token(a - b));
                    break;
                }

                case MULTIPLY: {
                    if (stack.size() < 2) {
                        throw new EvaluateException();
                    }

                    stack.push(new Token(stack.pop().val * stack.pop().val));
                    break;
                }

                case DIVIDE: {
                    if (stack.size() < 2) {
                        throw new EvaluateException();
                    }

                    double b = stack.pop().val;
                    double a = stack.pop().val;
                    stack.push(new Token(a / b));
                    break;
                }

                case REVERSE_SIGN: {
                    if (stack.size() < 1) {
                        throw new EvaluateException();
                    }

                    stack.push(new Token(-stack.pop().val));
                    break;
                }
            }
        }

        if (stack.size() != 1) {
            throw new EvaluateException();
        }

        return stack.pop().val;
    }

    private static class Token {
        private static final Token ADD_OPERATION = new Token(Double.NaN, Type.ADD);
        private static final Token SUBTRACT_OPERATION = new Token(Double.NaN, Type.SUBTRACT);
        private static final Token MULTIPLY_OPERATION = new Token(Double.NaN, Type.MULTIPLY);
        private static final Token DIVIDE_OPERATION = new Token(Double.NaN, Type.DIVIDE);
        private static final Token REVERSE_OPERATION = new Token(Double.NaN, Type.REVERSE_SIGN);
        private static final Token OPEN_BRACKET = new Token(Double.NaN, Type.OPEN_BRACKET);
        private final double val;
        private final Type type;

        public Token(double val, Type type) {
            this.val = val;
            this.type = type;
        }

        public Token(double val) {
            this(val, Type.DIGIT);
        }

        private static boolean isOperator(char ch) {
            return ch == '+' || ch == '-' || ch == '/' || ch == '*';
        }

        private static Token of(char ch) {
            switch (ch) {
                case '+': {
                    return ADD_OPERATION;
                }

                case '-': {
                    return SUBTRACT_OPERATION;
                }

                case '*': {
                    return MULTIPLY_OPERATION;
                }

                case '/': {
                    return DIVIDE_OPERATION;
                }

                default: {
                    throw new EvaluateException("unknown operation, " + ch);
                }
            }
        }

        private static int comparePriority(Token token1, Token token2) {
            return token1.type.priority - token2.type.priority;
        }

        @Override
        public String toString() {
            switch (type) {
                case ADD:
                    return "[+]";

                case SUBTRACT:
                    return "[-]";

                case MULTIPLY:
                    return "[*]";

                case DIVIDE:
                    return "[/]";

                case OPEN_BRACKET:
                    return "[(]";

                case CLOSE_BRACKET:
                    return "[)]";

                case REVERSE_SIGN:
                    return "[(-)]";

                case DIGIT:
                    return String.format("[%f]", val);

                default:
                    return "[?]";
            }
        }

        private enum Type {
            DIGIT, SUBTRACT(1), ADD(1), MULTIPLY(2), DIVIDE(3),
            REVERSE_SIGN(3),
            OPEN_BRACKET,
            CLOSE_BRACKET;

            private int priority;

            Type() {
                priority = 0;
            }

            Type(int priority) {
                this.priority = priority;
            }
        }
    }
}
