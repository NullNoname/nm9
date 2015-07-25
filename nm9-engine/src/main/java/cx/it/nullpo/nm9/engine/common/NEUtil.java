package cx.it.nullpo.nm9.engine.common;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Static utilities
 * @author NullNoname
 */
public class NEUtil {
	static public final Pattern PATTERN_NUMBERS = Pattern.compile("-?\\d+");

	/**
	 * Get a NamedObject from an iterable Collection. Returns null in failure.
	 * @param name Name
	 * @param c Collection
	 * @return First specified NamedObject found in the Collection, or null if not found
	 */
	static public NamedObject getNamedObject(final String name, final Collection<? extends Object> c) {
		if(c == null) return null;

		for(final Object n: c) {
			if(n instanceof NamedObject) {
				NamedObject n2 = (NamedObject)n;
				if((name != null && name.equals(n2.getName())) || (name == null && null == n2.getName()) ) {
					return n2;
				}
			}
		}

		return null;
	}

	/**
	 * Get a NamedObject from an iterable Collection. Throws exceptions in failure.
	 * @param name Name
	 * @param c Collection
	 * @return First specified NamedObject found in the Collection
	 * @throws NullPointerException (runtime) If the specified Collection is null
	 * @throws NamedObjectNotFoundException (runtime) If the specified object cannot be found in the collection
	 */
	static public NamedObject getNamedObjectE(final String name, final Collection<? extends Object> c) {
		if(c == null) {
			throw new NullPointerException("The specified collection is null");
		}

		for(final Object n: c) {
			if(n instanceof NamedObject) {
				NamedObject n2 = (NamedObject)n;
				if((name != null && name.equals(n2.getName())) || (name == null && null == n2.getName()) ) {
					return n2;
				}
			}
		}

		throw new NamedObjectNotFoundException("Couldn't find " + name + " in the specified collection");
	}

	static public int getIntByRegex(final CharSequence s, final int defaultValue) {
		Matcher mn = PATTERN_NUMBERS.matcher(s);
		if(mn.find()) {
			String n = mn.group();
			try {
				return Integer.parseInt(n);
			} catch (NumberFormatException e) {}
		}
		return defaultValue;
	}

	static public int getIntByRegex(final CharSequence s, final int defaultValue, final Pattern p) {
		Matcher m = p.matcher(s);
		if(m.find()) {
			return getIntByRegex(m.group(), defaultValue);
		}
		return defaultValue;
	}

	static public boolean hasCommonItem(final List<? extends Object> list1, final List<? extends Object> list2) {
		if(list1 == null || list2 == null) return false;
		for(Object obj: list1) {
			if(list2.contains(obj)) {
				return true;
			}
		}
		return false;
	}

	static public boolean checkTags(final List<? extends Object> list1, final List<? extends Object> list2) {
		if(list1 == null || list2 == null) return false;

		for(Object obj: list1) {
			String str = obj.toString();
			if(str.startsWith("!")) {
				// NOT
				String s = str.substring(1);
				if(list2.contains(s))
					return false;
			} else {
				if(!list2.contains(str))
					return false;
			}
		}

		return true;
	}

	static public boolean isAndroid() {
		return StringUtils.equals("Dalvik", System.getProperty("java.vm.name"));
	}
}
