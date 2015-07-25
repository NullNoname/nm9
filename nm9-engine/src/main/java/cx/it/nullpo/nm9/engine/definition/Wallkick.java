package cx.it.nullpo.nm9.engine.definition;

import java.io.Serializable;
import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import cx.it.nullpo.nm9.engine.common.Copyable;

/**
 * Wallkick settings
 * @author NullNoname
 */
@Root
public class Wallkick implements Serializable, Copyable {
	private static final long serialVersionUID = -5279473833450484113L;

	/**
	 * Wallkick type
	 */
	@Attribute(required=false)
	private String type;

	/**
	 * Original state
	 */
	@Attribute(required=false)
	private int stateFrom;

	/**
	 * Destination state
	 */
	@Attribute(required=false)
	private int stateTo;

	/**
	 * List of offsets
	 */
	@ElementList(inline=true)
	private ArrayList<XYPair> offsetList = new ArrayList<XYPair>();

	public Wallkick() {
	}

	public Wallkick(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		Wallkick s = (Wallkick)src;
		this.type = s.type;
		this.stateFrom = s.stateFrom;
		this.stateTo = s.stateTo;
		this.offsetList = new ArrayList<XYPair>();
		for(XYPair xyPair: s.offsetList) {
			this.offsetList.add(new XYPair(xyPair));
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStateFrom() {
		return stateFrom;
	}

	public void setStateFrom(int stateFrom) {
		this.stateFrom = stateFrom;
	}

	public int getStateTo() {
		return stateTo;
	}

	public void setStateTo(int stateTo) {
		this.stateTo = stateTo;
	}

	public ArrayList<XYPair> getOffsetList() {
		return offsetList;
	}

	public void setOffsetList(ArrayList<XYPair> offsetList) {
		this.offsetList = offsetList;
	}
}
