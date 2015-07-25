package cx.it.nullpo.nm9.engine.definition;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NamedObject;

/**
 * The definition of a Level.
 * @author NullNoname
 */
@Root
public class Level implements NamedObject, Serializable, Copyable {
	private static final long serialVersionUID = 8804526134884775643L;

	/**
	 * The "internal" name of this Level. Must be unique.
	 * The Level can be referenced from script by using the internal name.
	 */
	@Attribute
	private String name;

	/**
	 * The displayed name of this level. You can omit it to use the internal name.
	 * It doesn't have to be unique; multiple levels can use the same displayed name.
	 */
	@Attribute(required=false)
	private String dispName;

	/**
	 * The speed setting to use (name reference). You can omit it to reuse previous speed setting.
	 */
	@Attribute(required=false)
	private String speed;

	/**
	 * Proceed to the next level when this condition is met
	 */
	@Attribute(required=false)
	private String condition;

	public Level() {
	}

	public Level(Copyable src) {
		copy(src);
	}

	public Level(String name) {
		this.name = name;
	}

	public void copy(Copyable src) {
		Level s = (Level)src;
		this.name = s.name;
		this.dispName = s.dispName;
		this.speed = s.speed;
		this.condition = s.condition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDispName() {
		return dispName;
	}

	public void setDispName(String dispName) {
		this.dispName = dispName;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
