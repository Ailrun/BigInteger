import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
 
public class BigInteger
{
    private static final String QUIT_COMMAND = "quit";
    private static final String MSG_INVALID_INPUT = "입력이 잘못되었습니다.";
 
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile(" *([+\\-]?) *(0|[1-9][0-9]*) *([+\\-\\*]) *([+\\-]?) *(0|[1-9][0-9]*) *");

	// Function for adding character array to character array with fixed size order of length.
	private static char[] addValue(char[] opLong, char[] opShort) {
		char[] result = new char[opLong.length + 1];
		int index = 1;
		int digit = 0;
		int carry = 0;

		// add shorter part.
		while (index <= opShort.length) {
			digit = Character.getNumericValue(opLong[opLong.length-index]) + Character.getNumericValue(opShort[opShort.length-index]) + carry;
			carry = digit / 10;
			digit = digit % 10;
			result[opLong.length+1 - index] = Character.forDigit(digit, 10);
			index++;
		}

		// carry rippling
		while (index <= opLong.length) {
			digit = Character.getNumericValue(opLong[opLong.length-index]) + carry;
			carry = digit / 10;
			digit = digit % 10;
			result[opLong.length+1 - index] = Character.forDigit(digit, 10);
			index++;
		}

		// biggest digit check
		if (carry > 0) {
			result[0] = Character.forDigit(carry, 10);
			return result;
		} else {
			char[] realResult = new char[opLong.length];
			System.arraycopy(result, 1, realResult, 0, opLong.length);
			return realResult;
		}
	}

	// Function for subtracting character value from character value with fixed order of length.
	private static char[] subtractValue(char[] opLong, char[] opShort) {
		char[] result = new char[opLong.length];
		int index = 1;
		int digit = 0;
		int carry = 0;

		// subtract shorter part
		while (index <= opShort.length) {
			digit = Character.getNumericValue(opLong[opLong.length-index]) - Character.getNumericValue(opShort[opShort.length-index]) - carry;
			carry = digit < 0 ? 1 : 0;
			digit = digit + (carry == 1? 10 : 0);
			result[opLong.length - index] = Character.forDigit(digit, 10);
			index++;
		}

		// carry rippling
		while (index <= opLong.length) {
			digit = Character.getNumericValue(opLong[opLong.length-index]) - carry;
			carry = digit < 0 ? 1 : 0;
			digit = digit + (carry == 1 ? 10 : 0);
			result[opLong.length - index] = Character.forDigit(digit, 10);
			index++;
		}

		// biggest digit
		if (result[0] != '0') {
			return result;
		} else {
			char[] realResult = new char[opLong.length-1];
			System.arraycopy(result, 1, realResult, 0, opLong.length-1);
			return realResult;
		}
	}

	// Function for multiplying character value to character value with fixed order of length.
	private static char[] multiplyValue(char[] opLong, char[] opShort) {

		// calculate opLong * 1 ~ opLong * 9
		char[][] oneDigitMult = new char[9][];
		oneDigitMult[0] = new char[opLong.length];
		System.arraycopy(opLong, 0, oneDigitMult[0], 0, opLong.length);
		for (int i = 1; i < 9; i++) {
			oneDigitMult[i] = addValue(oneDigitMult[i-1], opLong);
		}

		char[] result = null;
		//result[0] = '0';
		int oneDigit = Character.getNumericValue(opShort[0]);
		if (oneDigit == 0) {
			//Error with first 0 character.
			result = new char[1];
			result[0] = '0';
		} else {
			//Initializing result.
			result = oneDigitMult[oneDigit-1];
		}

		// iteratively adding each digit
		char[] temp = null;
		for (int i = 1; i < opShort.length; i++) {
			temp = new char[result.length+1];
			System.arraycopy(result, 0, temp, 0, result.length);
			temp[temp.length-1] = '0';
			result = temp;
			oneDigit = Character.getNumericValue(opShort[i]);
			if (oneDigit != 0) {
				result = addValue(result, oneDigitMult[oneDigit-1]);
			}
		}
		return result;
	}

	private char[] value = null;
	private char sign = '\0';
	private int length = 0;

    public BigInteger(int i)
    {
		if (i >= 0) {
			value = Integer.toString(i).toCharArray();
			length = value.length;
			sign = '+';
		} else if (i < 0) {
			value = Integer.toString(i).substring(1).toCharArray();
			length = value.length;
			sign = '-';
		}
    }

	// {1, 2, 3} to BigInteger(123);
	// {-1, 2, 3} to BigInteger(-123);
	// {1, -2, 3} to BigInteger(123);
	// {} to BigInteger(0);
    public BigInteger(int[] num1)
    {
		int len = num1.length;
		String temp = new String("");

		if (len > 0) {
			if (num1[0] >= 0) {
				sign = '+';
			} else {
				sign = '-';
			}
			
			for (int i = 0; i < len; i++) {
				temp += Integer.toString(Math.abs(num1[i]));
			}

			value = temp.toCharArray();
			length = value.length;
		} else {
			sign = '+';
			value = new char[1];
			value[0] = '0';
			length = 1;
		}
    }
 
    public BigInteger(String s)
    {
		value = s.substring(1).toCharArray();
		length = value.length;
		if (length == 1 && value[0] == '0') {
			sign = '+';
		} else {
			sign = s.charAt(0);
		}
    }

	// Check integer is positive (including zero) or not.
	public boolean isPositive() {
		return sign == '+';
	}

	public boolean isZero() {
		return length == 1 && value[0] == '0';
	}

	public BigInteger neg() {
		if (isPositive()) {
			return new BigInteger("-" + new String(value));
		} else {
			return new BigInteger("+" + new String(value));
		}
	}

	// if absolute of op is bigger than absolute of this, result < 0
	// else if smaller, result > 0
	// else result = 0
	public int compareAbs(BigInteger op) {
		if (length > op.length) {
			return 1;
		} else if (length < op.length) {
			return -1;
		} else {
			int result = 0;
			int index = 0;
			while (result == 0 && index < length) {
				result = value[index] - op.value[index];
			}
			return result;
		}
	}

	// if op is bigger than this, result < 0
	// else if smaller, result > 0
	// else result = 0 
	public int compare(BigInteger op) {
		if (isPositive()) {
			if (op.isPositive()) {
				return compareAbs(op);
			} else {
				return 1;
			}
		} else {
			if (op.isPositive()) {
				return -1;
			} else {
				return compareAbs(op);
			}
		}
	}

    public BigInteger add(BigInteger big)
    {
		if (isZero()) {
			return big;
		}

		if (big.isZero()) {
			return new BigInteger(sign + new String(value));
		}
		
		if (sign == big.sign) {
			if (length > big.length) {
				return new BigInteger(sign + new String(addValue(this.value, big.value)));
			} else {
				return new BigInteger(sign + new String(addValue(big.value, this.value)));
			}
		} else {
			if (compareAbs(big) > 0) {
				return new BigInteger(sign + new String(subtractValue(this.value, big.value)));
			} else if (compareAbs(big) < 0){
				return new BigInteger(big.sign + new String(subtractValue(big.value, this.value)));
			} else {
				return new BigInteger(0);
			}
		}
    }
 
    public BigInteger subtract(BigInteger big)
    {
		if (isZero()) {
			return new BigInteger(big.sign + new String(big.value)).neg();
		}

		if (big.isZero()) {
			return new BigInteger(sign + new String(value));
		}

		if (sign != big.sign) {
			if (length > big.length) {
				return new BigInteger(sign + new String(addValue(this.value, big.value)));
			} else {
				return new BigInteger(sign + new String(addValue(big.value, this.value)));
			}
		} else {
			if (compareAbs(big) > 0) {
				return new BigInteger(sign + new String(subtractValue(this.value, big.value)));
			} else if (compareAbs(big) < 0){
				return new BigInteger(sign + new String(subtractValue(big.value, this.value))).neg();
			} else {
				return new BigInteger(0);
			}
		}
    }
 
    public BigInteger multiply(BigInteger big)
    {
		if (sign == big.sign) {
			if (length > big.length) {
				return new BigInteger("+" + new String(multiplyValue(this.value, big.value)));
			} else {
				return new BigInteger("+" + new String(multiplyValue(big.value, this.value)));
			}
		} else {
			if (length > big.length) {
				return new BigInteger("-" + new String(multiplyValue(this.value, big.value)));
			} else {
				return new BigInteger("-" + new String(multiplyValue(big.value, this.value)));
			}
		}
    }
 
    @Override
    public String toString()
    {
		if (isPositive()) {
			return new String(value);
		} else {
			return sign + new String(value);
		}
    }
 
    static BigInteger evaluate(String input) throws IllegalArgumentException
    {
		Matcher matcher = EXPRESSION_PATTERN.matcher(input);
		if (!matcher.matches()) {
			throw new IllegalArgumentException();
		}

		BigInteger num1 = null;
		BigInteger num2 = null;
		if (matcher.group(1).equals("-")) {
			num1 = new BigInteger("-" + matcher.group(2));
		} else {
			num1 = new BigInteger("+" + matcher.group(2));
		}
		if (matcher.group(4).equals("-")) {
			num2 = new BigInteger("-" + matcher.group(5));
		} else {
			num2 = new BigInteger("+" + matcher.group(5));
		}
		
		BigInteger result = null;
		switch (matcher.group(3)) {
		case "+" :
			result = num1.add(num2);
			break;
		case "-" :
			result = num1.subtract(num2);
			break;
		case "*" :
			result = num1.multiply(num2);
			break;
		default :
			System.err.println("");
			throw new IllegalArgumentException();
		}
		
		return result;
    }
 
    public static void main(String[] args) throws Exception
    {
        try (InputStreamReader isr = new InputStreamReader(System.in))
        {
            try (BufferedReader reader = new BufferedReader(isr))
            {
                boolean done = false;
                while (!done)
                {
                    String input = reader.readLine();
 
                    try
                    {
                        done = processInput(input);
                    }
                    catch (IllegalArgumentException e)
                    {
                        System.err.println(MSG_INVALID_INPUT);
                    }
                }
            }
        }
    }
 
    static boolean processInput(String input) throws IllegalArgumentException
    {
        boolean quit = isQuitCmd(input);
 
        if (quit)
        {
            return true;
        }
        else
        {
            BigInteger result = evaluate(input);
            System.out.println(result.toString());
 
            return false;
        }
    }
 
    static boolean isQuitCmd(String input)
    {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}
