package com.yoyo.admin.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * 解析四则运算表达式并计算结果
 */
public class ParseCalArithmeticUtils {

    /**
     * 校验的基本思路：
     * 1.对公式进行校验，使用正则表达式进行常规性的检查。
     * 2.对公式的格式进行优化，比如去掉公式中的空格、对负数和正数进行补零（例如 -a 优化为 0-a）、补充两个括号之间的乘法运算符（例如 (a+b)(b+c) 优化为 (a+b)*(b+c)）
     *
     * 计算的基本思路：
     * 1.翻译输入的数学表达式，也就是中缀表达式转后缀表达式。例如 a+b*(c-d) 转为后缀表达式就是 abcd-*+
     * 2.对后缀表达式计算结果。这里用到了栈存储计算结果，每次都是对两个数计算，例如 abcd-*+，计算方法是先从头遍历，数字直接入栈，当遇到计算符，则从栈顶取出来两个数计算然后再把结果压栈，最终全部计算完之后栈里面只剩下一个元素就是结果。
     */

    /**
     * 使用正则来校验数学公式
     *
     * @param expression 数学公式，包含变量
     * @param variables  内置变量集合
     */
    public static String validate(String expression, List<String> variables) {
        //if (variables == null || variables.isEmpty()) {
        //    throw new RuntimeException("内置变量为空");
        //}
        // 去空格
        expression = expression.replaceAll(" ", "");
        // 连续运算符处理
        if (expression.split("[\\+\\-\\*\\/]{2,}").length > 1) {
            throw new RuntimeException("公式不合法，包含连续运算符");
        }
        if (expression.contains("()")) {
            throw new RuntimeException("公式不合法，包含空括号");
        }
        expression = expression.replaceAll("\\)\\(", "\\)*\\(");
        expression = expression.replaceAll("\\(\\-", "\\(0-");
        expression = expression.replaceAll("\\(\\+", "\\(0+");
        // 校验变量
        String[] splits = expression.split("\\+|\\-|\\*|\\/|\\(|\\)");
        for (String split : splits) {
            if (StringUtils.isBlank(split) || Pattern.matches("-?(0|([1-9]\\d*))(\\.\\d+)?", split)) {
                continue;
            }
            if (!variables.contains(split)) {
                throw new RuntimeException("公式不合法，包含非法变量或字符");
            }
        }
        // 校验括号
        Character preChar = null;
        Stack<Character> stack = new Stack<>();
        String resultExpression = expression;
        for (int i = 0; i < expression.length(); i++) {
            char currChar = expression.charAt(i);
            if (i == 0) {
                if (Pattern.matches("\\*|\\/", String.valueOf(currChar))) {
                    throw new RuntimeException("公式不合法，以错误运算符开头");
                }
                if (currChar == '+') {
                    resultExpression = expression.substring(1);
                }
                if (currChar == '-') {
                    resultExpression = "0" + expression;
                }
            }
            if ('(' == currChar) {
                stack.push('(');
            } else if (')' == currChar) {
                if (stack.size() > 0) {
                    stack.pop();
                } else {
                    throw new RuntimeException("公式不合法，括号不配对");
                }
            }
            if (preChar != null && preChar == '(' && Pattern.matches("[\\+\\-\\*\\/]+", String.valueOf(currChar))) {
                throw new RuntimeException("公式不合法，左括号后是运算符");
            }
            if (preChar != null && preChar == ')' && !Pattern.matches("[\\+\\-\\*\\/]+", String.valueOf(currChar))) {
                throw new RuntimeException("公式不合法，右括号后面不是运算符");
            }
            if (i == expression.length() - 1) {
                if (Pattern.matches("\\+|\\-|\\*|\\/", String.valueOf(currChar))) {
                    throw new RuntimeException("公式不合法，以运算符结尾");
                }
            }
            preChar = currChar;
        }
        if (stack.size() > 0) {
            throw new RuntimeException("公式不合法，括号不配对");
        }
        return resultExpression;
    }

    /**
     * 比较优先级
     * 返回 true 表示 curr 优先级大于 stackTop
     */
    private static boolean compare(char curr, char stackTop) {
        // 左括号会直接入栈，这里是其他运算符与栈顶左括号对比
        if (stackTop == '(') {
            return true;
        }
        // 乘除法的优先级大于加减法
        if (curr == '*' || curr == '/') {
            return stackTop == '+' || stackTop == '-';
        }
        // 运算符优先级相同时，先入栈的优先级更高
        return false;
    }

    /**
     * 将中缀表达式，转换为后缀表达式，支持多位数、小数
     */
    private static LinkedList<String> getPostfix(String mathStr) {
        // 后缀表达式链
        LinkedList<String> postfixList = new LinkedList<>();
        // 运算符栈
        Stack<Character> optStack = new Stack<>();
        // 多位数链
        LinkedList<Character> multiDigitList = new LinkedList<>();
        char[] arr = mathStr.toCharArray();
        for (char c : arr) {
            if (Character.isDigit(c) || '.' == c) {
                multiDigitList.addLast(c);
            } else {
                // 处理当前的运算符之前，先处理多位数链中暂存的数据
                if (!multiDigitList.isEmpty()) {
                    StringBuilder temp = new StringBuilder();
                    while (!multiDigitList.isEmpty()) {
                        temp.append(multiDigitList.removeFirst());
                    }
                    postfixList.addLast(temp.toString());
                }
            }
            // 如果当前字符是左括号，将其压入运算符栈
            if ('(' == c) {
                optStack.push(c);
            }
            // 如果当前字符为运算符
            else if ('+' == c || '-' == c || '*' == c || '/' == c) {
                while (!optStack.isEmpty()) {
                    char stackTop = optStack.pop();
                    // 若当前运算符的优先级高于栈顶元素，则一起入栈
                    if (compare(c, stackTop)) {
                        optStack.push(stackTop);
                        break;
                    }
                    // 否则，弹出栈顶运算符到后缀表达式，继续下一次循环
                    else {
                        postfixList.addLast(String.valueOf(stackTop));
                    }
                }
                optStack.push(c);
            }
            // 如果当前字符是右括号，反复将运算符栈顶元素弹出到后缀表达式，直到栈顶元素是左括号"("为止，并将左括号从栈中弹出丢弃。
            else if (c == ')') {
                while (!optStack.isEmpty()) {
                    char stackTop = optStack.pop();
                    if (stackTop != '(') {
                        postfixList.addLast(String.valueOf(stackTop));
                    } else {
                        break;
                    }
                }
            }
        }
        // 遍历结束时，若多位数链中具有数据，说明公式是以数字结尾
        if (!multiDigitList.isEmpty()) {
            StringBuilder temp = new StringBuilder();
            while (!multiDigitList.isEmpty()) {
                temp.append(multiDigitList.removeFirst());
            }
            postfixList.addLast(temp.toString());
        }
        // 遍历结束时，运算符栈若有数据，说明是由括号所致，需要补回去
        while (!optStack.isEmpty()) {
            postfixList.addLast(String.valueOf(optStack.pop()));
        }
        return postfixList;
    }

    /**
     * 根据后缀表达式，得到计算结果
     */
    private static BigDecimal doCalculate(LinkedList<String> postfixList) {
        // 操作数栈
        Stack<BigDecimal> numStack = new Stack<>();
        while (!postfixList.isEmpty()) {
            String item = postfixList.removeFirst();
            BigDecimal a, b;
            switch (item) {
                case "+":
                    a = numStack.pop();
                    b = numStack.pop();
                    numStack.push(b.add(a));
                    break;
                case "-":
                    a = numStack.pop();
                    b = numStack.pop();
                    numStack.push(b.subtract(a));
                    break;
                case "*":
                    a = numStack.pop();
                    b = numStack.pop();
                    numStack.push(b.multiply(a));
                    break;
                case "/":
                    a = numStack.pop();
                    b = numStack.pop();
                    numStack.push(b.divide(a, 2, RoundingMode.HALF_UP));
                    break;
                default:
                    numStack.push(new BigDecimal(item));
                    break;
            }
        }
        return numStack.pop();
    }

    /**
     * 1. 将中缀表达式转后缀表达式
     * 2. 根据后缀表达式进行计算
     */
    public static BigDecimal calculate(String mathStr) {
        if (mathStr == null || mathStr.length() == 0) {
            return null;
        }
        LinkedList<String> postfixList = getPostfix(mathStr);
        //System.out.println("后缀表达式：" + postfixList);
        return doCalculate(postfixList);
    }

    public static void main(String[] args) {

        // 校验测试
        List<String> variables = Arrays.asList("height", "length", "width", "num");
        String result01 = validate("(height+length)(width+num)", variables);
        System.out.println("result01 = " + result01);
        String result02 = validate("-num+100", variables);
        System.out.println("result02 = " + result02);
        String result03 = validate("(length*(1+width)/height)*num", variables);
        System.out.println("result03 = " + result03);

        // 功能测试
        String str1 = "5-1*(5+6)+2";
        System.out.println(str1 + " = " + calculate(str1));
        str1 = "50-1*(5+6)+2";
        System.out.println(str1 + " = " + calculate(str1));
        str1 = "(50.5-1)*(5+6)+2";
        System.out.println(str1 + " = " + calculate(str1));
        str1 = "1+2*(3-4*(5+6))";
        System.out.println(str1 + " = " + calculate(str1));
        str1 = "1/2*(3-4*(5+6))*10";
        System.out.println(str1 + " = " + calculate(str1));
        str1 = "43*(2+1)+2*32+98";
        System.out.println(str1 + " = " + calculate(str1));
        str1 = "3-10*2+5";
        System.out.println(str1 + " = " + calculate(str1));

        // 性能测试
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            String str2 = "43*(2+1.4)+2*32/(3-2.1)" + "+" + i;
            BigDecimal result = calculate(str2);
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
    }

}
