import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BigInteger
{
	private static final String QUIT_COMMAND = "quit";
	private static final String MSG_INVALID_INPUT = "입력이 잘못되었습니다.";

	// Sign constants : 0 has '+' sign.
	private static final char PLUS_SIGN_CHAR = '+';
	private static final String PLUS_SIGN_STRING = "+";
	private static final boolean PLUS_SIGN = true;
	private static final char MINUS_SIGN_CHAR = '-';
	private static final String MINUS_SIGN_STRING = "-";
	private static final boolean MINUS_SIGN = false;

	private static final String REGEX_STRING = "([+\\-]?[1-9][0-9]*) *([+\\-\\*]) *([+\\-\\*]?[1-9][0-9]*) *";
	private static final Pattern EXPRESSION_PATTERN = Pattern.compile(REGEX_STRING);

	private char[] value;
	private boolean sign;

	public BigInteger(int i) {
		if (i < 0) {
			
		} else {
			
		}
	}

	public BigInteger(int[] num1) {
		
	}

	public BigInteger(String s) {
		if (s.charAt(0) == PLUS_SIGN_CHAR) {
			
		}
	}

	public BigInteger add(BigInteger big) {
		return big;
	}

	public BigInteger subtract(BigInteger big) {
		return big;
	}

	public BigInteger multiply(BigInteger big) {
		return big;
	}

	@Override
	public String toString() {
		return "";
	}

	public static void main(String[] args) throws Exception {
		try (InputStreamReader isr = new InputStreamReader(System.in)) {
			try (BufferedReader reader = new BufferedReader(isr)) {
				boolean done = false;
				while (!done) {
					String input = reader.readLine();

					try {
						done = processInput(input);
					} catch (IllegalArgumentException e) {
						System.err.println(MSG_INVALID_INPUT);
					}
				}
			}
		}
	}

	private static BigInteger evaluate(String input) throws IllegalArgumentException {
		Matcher matcher = EXPRESSION_PATTERN.matcher(input);

		if (!matcher.matches()) {
			throw new IllegalArgumentException();
		}
		
		BigInteger op1 = new BigInteger(matcher.group(1));
		BigInteger op2 = new BigInteger(matcher.group(3));
		BigInteger result;

		switch (matcher.group(2)) {
		case "+" :
			result = op1.add(op2);
			break;
		case "-":
			result = op1.subtract(op2);
			break;
		case "*":
			result = op1.multiply(op2);
			break;
		default:
			throw new IllegalArgumentException();
		}

		return result;
	}

	private static boolean processInput(String input) throws IllegalArgumentException {
		boolean quit = isQuitCmd(input);

		if (quit) {
			return true;
		} else {
			BigInteger result = evaluate(input);
			System.out.println(result.toString());

			return false;
		}
	}

	private static boolean isQuitCmd(String input) {
		return input.equalsIgnoreCase(QUIT_COMMAND);
	}
}
