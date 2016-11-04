import java.util.HashMap;
import java.util.Map;

/**
 * Parses and stores an array of argument into flag, value pairs for easy access
 * later. Useful to parse command-line arguments.
 */
public class ArgumentParser {

	/**
	 * Stores parsed flag, value pairs.
	 */
	private final Map<String, String> argumentMap;

	/**
	 * Creates a new and empty argument parser.
	 */
	public ArgumentParser() {
		argumentMap = new HashMap<String, String>();
	}

	/**
	 * Creates a new argument parser and immediately parses the passed
	 * arguments.
	 * 
	 * @param args
	 *            arguments to parse into flag, value pairs
	 * @see #parseArguments(String[])
	 */
	public ArgumentParser(String[] args) {
		this();
		parseArguments(args);
	}

	/**
	 * Parses the array of arguments into flag, value pairs. If an argument is a
	 * flag, tests whether the next argument is a value. If it is a value, then
	 * stores the flag, value pair. Otherwise, it stores the flag with no value.
	 * If the flag appears multiple times with different values, only the last
	 * value will be kept. Values without an associated flag are ignored.
	 * 
	 * @param args
	 *            arguments to parse into flag, value pairs
	 * @see #isFlag(String)
	 * @see #isValue(String)
	 */
	public void parseArguments(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (isFlag(args[i])) {
				if (i + 1 < args.length) {
					if (isValue(args[i + 1])) {
						argumentMap.put(args[i], args[i + 1]);
					} else {
						argumentMap.put(args[i], null);
					}
				} else {
					argumentMap.put(args[i], null);
				}
			}
		}
	}

	/**
	 * Tests whether the argument is a valid flag, i.e. it is not null, and
	 * after trimming it starts with a dash "-" character followed by at least 1
	 * character.
	 * 
	 * @param arg
	 *            argument to test
	 * @return {@code true} if the argument is a valid flag
	 */
	public static boolean isFlag(String arg) {
		String trimmed = arg.trim();
		return (trimmed.startsWith("-") && arg.trim().length() > 1);
	}

	/**
	 * Tests whether the argument is a valid value, i.e. it is not null, does
	 * not start with a dash "-" character, and is not empty after trimming.
	 * 
	 * @param arg
	 *            argument to test
	 * @return {@code true} if the argument is a valid value
	 */
	public static boolean isValue(String arg) {
		return (!arg.startsWith("-") && !arg.trim().isEmpty());
	}

	/**
	 * Returns the number of flags stored.
	 * 
	 * @return number of flags
	 */
	public int numFlags() {
		return argumentMap.size();
	}

	/**
	 * Checks if the flag exists.
	 * 
	 * @param flag
	 *            flag to check for
	 * @return {@code true} if flag exists
	 */
	public boolean hasFlag(String flag) {
		if (argumentMap.containsKey(flag)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the flag exists and has a non-null value.
	 * 
	 * @param flag
	 *            flag whose associated value is to be checked
	 * @return {@code true} if the flag exists and has a non-null value
	 */
	public boolean hasValue(String flag) {

		if (argumentMap.keySet().contains(flag) && (argumentMap.get(flag) != null)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the value associated with the specified flag. May be {@code null}
	 * if a {@code null} value was stored or if the flag does not exist.
	 * 
	 * @param flag
	 *            flag whose associated value is to be returned
	 * @return value associated with the flag or {@code null} if the flag does
	 *         not exist
	 */
	public String getValue(String flag) {
		if (argumentMap.containsKey(flag)) {
			return argumentMap.get(flag);
		}
		return null;
	}

	/**
	 * Returns the value for a flag. If the flag is missing or the value is
	 * {@code null}, returns the default value instead.
	 * 
	 * @param flag
	 *            flag whose associated value is to be returned
	 * @param defaultValue
	 *            the default mapping of the flag
	 * @return value of flag or {@code defaultValue} if the flag is missing or
	 *         the value is {@code null}
	 */
	public String getValue(String flag, String defaultValue) {
		if (argumentMap.containsKey(flag) && argumentMap.get(flag) != null) {
			return argumentMap.get(flag);
		}
		return defaultValue;
	}

	/**
	 * Returns the value for a flag as an integer. If the flag or value is
	 * missing or if the value cannot be converted to an integer, returns the
	 * default value instead.
	 * 
	 * @param flag
	 *            flag whose associated value is to be returned
	 * @param defaultValue
	 *            the default mapping of the flag
	 * @return value of flag as an integer or {@code defaultValue} if the value
	 *         cannot be returned as an integer
	 */

	public int getValue(String flag, int defaultValue) {
		if (argumentMap.containsKey(flag) && argumentMap.get(flag) != null) {
			return Integer.parseInt(argumentMap.get(flag));
		}
		return defaultValue;
	}

	/**
	 * Returns the argument map as a String for viewing the data quickly
	 * 
	 * @return the argument map as a string
	 */
	@Override
	public String toString() {
		return argumentMap.toString();
	}
}
