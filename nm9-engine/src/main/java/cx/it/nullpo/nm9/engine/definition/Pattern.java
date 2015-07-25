package cx.it.nullpo.nm9.engine.definition;

import java.io.Serializable;
import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NamedObject;

/**
 * The definition of a Pattern which defines how the group of blocks are formed. (ex. Line Clear)
 * @author NullNoname
 */
@Root
public class Pattern implements NamedObject, Serializable, Copyable {
	private static final long serialVersionUID = 479291493615831858L;

	/**
	 * Name of this pattern. Must be unique.
	 */
	@Attribute
	private String name;

	/**
	 * Match method. Can be Horizontal, Vertical, or Script:YourCustomMatcher.
	 */
	@Attribute
	private String method;

	/**
	 * The action fired when the match is valid. Can be Delete, Connect, or Script:YourCustomAction.
	 */
	@Attribute
	private String action;

	/**
	 * If true, this pattern will not be executed unless you call it manually from a script.
	 */
	@Attribute(required=false)
	private boolean manual;

	/**
	 * Minimum number of blocks that required to fire the action.
	 * 0 means no minimum limit.
	 */
	@Attribute(required=false)
	private int minMatch;

	/**
	 * The action will not fire if the number of matched blocks exceed this value.
	 * 0 means no upper limit.
	 */
	@Attribute(required=false)
	private int maxMatch;

	/**
	 * If true, all tags must exist in a block in order to match with this pattern. (AND-styled Tag)
	 * If false, only one of the listed tag is needed. (OR-styled Tag)
	 */
	@Attribute(required=false)
	private boolean andTag;

	/**
	 *  List of block tags that can match with this pattern (If null or empty, any blocks can match)
	 */
	@ElementList(required=false)
	private ArrayList<String> tags = new ArrayList<String>();

	public Pattern() {
	}

	public Pattern(String name, String method, String action) {
		this.name = name;
		this.method = method;
		this.action = action;
	}

	public Pattern(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		Pattern s = (Pattern)src;
		this.name = s.name;
		this.method = s.method;
		this.action = s.action;
		this.manual = s.manual;
		this.minMatch = s.minMatch;
		this.maxMatch = s.maxMatch;
		this.andTag = s.andTag;
		this.tags = new ArrayList<String>(s.tags);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isManual() {
		return manual;
	}

	public void setManual(boolean manual) {
		this.manual = manual;
	}

	public int getMinMatch() {
		return minMatch;
	}

	public void setMinMatch(int minMatch) {
		this.minMatch = minMatch;
	}

	public int getMaxMatch() {
		return maxMatch;
	}

	public void setMaxMatch(int maxMatch) {
		this.maxMatch = maxMatch;
	}

	public boolean isAndTag() {
		return andTag;
	}

	public void setAndTag(boolean andTag) {
		this.andTag = andTag;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
}
