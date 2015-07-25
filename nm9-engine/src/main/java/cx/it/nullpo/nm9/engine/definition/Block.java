package cx.it.nullpo.nm9.engine.definition;

import java.io.Serializable;
import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NamedObject;

/**
 * The definition of a Block that can appear during the game.
 * Blocks can be included in a Piece and can be placed to a Field.
 * @author NullNoname
 */
@Root
public class Block implements NamedObject, Serializable, Copyable {
	private static final long serialVersionUID = -8381623348998399693L;

	/**
	 * The name of this block. Must be unique.
	 */
	@Attribute
	private String name;

	/**
	 * Color name of this block. Usually "Red", "Green", "Blue", etc.
	 */
	@Attribute(required=false)
	private String color;

	/**
	 * List of optional tags for this Block
	 */
	@ElementList(required=false)
	private ArrayList<String> tags = new ArrayList<String>();

	public Block() {
		this.name = "";
	}

	public Block(String name) {
		this.name = name;
	}

	public Block(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Block(String name, String color, ArrayList<String> tags) {
		this.name = name;
		this.color = color;
		this.tags = tags;
	}

	public Block(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		Block s = (Block)src;
		this.name = s.name;
		this.color = s.color;
		this.tags = new ArrayList<String>(s.tags);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
}
