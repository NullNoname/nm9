package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NEUtil;
import cx.it.nullpo.nm9.engine.definition.Pattern;
import cx.it.nullpo.nm9.engine.definition.XYPair;
import cx.it.nullpo.nm9.engine.script.ScriptHost;

/**
 * Game field
 * @author NullNoname
 */
public class Field implements Serializable, Copyable {
	private static final long serialVersionUID = 6832244310602097833L;

	/**
	 * Coordinate type: Normal zone (visible portion of playfield)
	 */
	public static final int COORD_NORMAL = 0;

	/**
	 * Coordinate type: Hidden zone (hidden portion of playfield)
	 */
	public static final int COORD_HIDDEN = 1;

	/**
	 * Coordinate type: Vanish zone (pieces can move through, but any blocks placed here will vanish)
	 */
	public static final int COORD_VANISH = 2;

	/**
	 * Coordinate type: Wall/Floor/Ceiling
	 */
	public static final int COORD_WALL = 3;

	/**
	 * The blocks in this field. null means empty cell. (Size: blocks[hiddenHeight+height][width])
	 */
	private RuntimeBlock[][] blocks;

	/**
	 * Width of this field
	 */
	private int width = 10;

	/**
	 * Visible height of this field
	 */
	private int height = 20;

	/**
	 * Hidden height of this field (exists above visible portion)
	 */
	private int hiddenHeight = 20;

	/**
	 * true if this field has field ceiling
	 */
	private boolean fieldCeilingEnable;

	/**
	 * Y position of field ceiling
	 */
	private int fieldCeilingY = 0;

	/**
	 * Multi-purpose "hit" flag for this field
	 */
	private boolean[][] hitFlag;

	/**
	 * Flag for blocks that fell
	 */
	private boolean[][] fallFlag;

	/**
	 * Create an empty field with 10w x 20+20h size
	 */
	public Field() {
		init();
	}

	/**
	 * Create an empty field with custom width and 20+20 height
	 * @param width Width
	 */
	public Field(int width) {
		this.width = width;
		init();
	}

	/**
	 * Create an empty field with custom size
	 * @param width Width
	 * @param height Height (also used by hidden height)
	 */
	public Field(int width, int height) {
		this.width = width;
		this.height = height;
		this.hiddenHeight = height;
		init();
	}

	/**
	 * Create an empty field with custom size
	 * @param width Width
	 * @param height Height (visible portion)
	 * @param hiddenHeight Hidden height
	 */
	public Field(int width, int height, int hiddenHeight) {
		this.width = width;
		this.height = height;
		this.hiddenHeight = hiddenHeight;
		init();
	}

	public Field(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		Field s = (Field)src;

		this.width = s.width;
		this.height = s.height;
		this.hiddenHeight = s.hiddenHeight;
		this.fieldCeilingEnable = s.fieldCeilingEnable;
		this.fieldCeilingY = s.fieldCeilingY;

		this.blocks = new RuntimeBlock[s.hiddenHeight+s.height][s.width];
		this.hitFlag = new boolean[s.hiddenHeight+s.height][s.width];
		this.fallFlag = new boolean[s.hiddenHeight+s.height][s.width];
		for(int i = 0; i < s.hiddenHeight+s.height; i++) {
			for(int j = 0; j < s.width; j++) {
				this.blocks[i][j] = (s.blocks[i][j] == null) ? null : new RuntimeBlock(s.blocks[i][j]);
				this.hitFlag[i][j] = s.hitFlag[i][j];
				this.fallFlag[i][j] = s.fallFlag[i][j];
			}
		}
	}

	/**
	 * Reset this field
	 */
	public void init() {
		blocks = new RuntimeBlock[hiddenHeight+height][width];
		resetHitFlag();
		resetFallFlag();
	}

	/**
	 * Reset the hit flag
	 */
	public void resetHitFlag() {
		hitFlag = createCustomHitFlagArray();
	}

	/**
	 * Reset the fall flag
	 */
	public void resetFallFlag() {
		fallFlag = createCustomHitFlagArray();
	}

	/**
	 * Create a custom hit flag array
	 * @return Hit flag array
	 */
	public boolean[][] createCustomHitFlagArray() {
		return new boolean[hiddenHeight+height][width];
	}

	/**
	 * Get the hit flag (returns true on out-of-bounds)
	 * @param x X position
	 * @param y Y position
	 * @return The state of hit flag (returns true on out-of-bounds)
	 */
	public boolean getHitFlag(int x, int y) {
		return getHitFlag(hitFlag, x, y, true);
	}

	/**
	 * Get the hit flag
	 * @param x X position
	 * @param y Y position
	 * @param def Default value (used when the position is out-of-bounds)
	 * @return The state of hit flag
	 */
	public boolean getHitFlag(int x, int y, boolean def) {
		return getHitFlag(hitFlag, x, y, def);
	}

	/**
	 * Get the hit flag
	 * @param x X position
	 * @param y Y position
	 * @return The state of hit flag
	 * @throws ArrayIndexOutOfBoundsException If the position is invalid
	 */
	public boolean getHitFlagE(int x, int y) throws ArrayIndexOutOfBoundsException {
		return getHitFlagE(hitFlag, x, y);
	}

	/**
	 * Set the hit flag
	 * @param x X position
	 * @param y Y position
	 * @param b State of hit flag
	 * @return true if successful
	 */
	public boolean setHitFlag(int x, int y, boolean b) {
		return setHitFlag(hitFlag, x, y, b);
	}

	/**
	 * Set the hit flag
	 * @param x X position
	 * @param y Y position
	 * @param b State of hit flag
	 * @return Always true
	 * @throws ArrayIndexOutOfBoundsException If the position is invalid
	 */
	public boolean setHitFlagE(int x, int y, boolean b) throws ArrayIndexOutOfBoundsException {
		return setHitFlagE(hitFlag, x, y, b);
	}

	/**
	 * Create a hit list by using hit flag
	 * @return Hit list
	 */
	public ArrayList<XYPair> createHitListByHitFlag() {
		return createHitListByHitFlag(hitFlag);
	}

	/**
	 * Get the hit flag from custom array (returns true on out-of-bounds)
	 * @param f Hit flag array
	 * @param x X position
	 * @param y Y position
	 * @return The state of hit flag (returns true on out-of-bounds)
	 */
	public boolean getHitFlag(boolean[][] f, int x, int y) {
		return getHitFlag(f, x, y, true);
	}

	/**
	 * Get the hit flag from custom array
	 * @param f Hit flag array
	 * @param x X position
	 * @param y Y position
	 * @param def Default value (used when the position is out-of-bounds)
	 * @return The state of hit flag
	 */
	public boolean getHitFlag(boolean[][] f, int x, int y, boolean def) {
		try {
			return f[hiddenHeight+y][x];
		} catch (ArrayIndexOutOfBoundsException e) {
			return def;
		}
	}

	/**
	 * Get the hit flag from custom array
	 * @param f Hit flag array
	 * @param x X position
	 * @param y Y position
	 * @return The state of hit flag
	 * @throws ArrayIndexOutOfBoundsException If the position is invalid
	 */
	public boolean getHitFlagE(boolean[][] f, int x, int y) throws ArrayIndexOutOfBoundsException {
		return f[hiddenHeight+y][x];
	}

	/**
	 * Set the hit flag to custom array
	 * @param f Hit flag array
	 * @param x X position
	 * @param y Y position
	 * @param b State of hit flag
	 * @return true if successful
	 */
	public boolean setHitFlag(boolean[][] f, int x, int y, boolean b) {
		try {
			f[hiddenHeight+y][x] = b;
			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Set the hit flag to custom array
	 * @param f Hit flag array
	 * @param x X position
	 * @param y Y position
	 * @param b State of hit flag
	 * @return Always true
	 * @throws ArrayIndexOutOfBoundsException If the position is invalid
	 */
	public boolean setHitFlagE(boolean[][] f, int x, int y, boolean b) throws ArrayIndexOutOfBoundsException {
		f[hiddenHeight+y][x] = b;
		return true;
	}

	/**
	 * Create a hit list by using custom hit flag
	 * @param f Hit flag array
	 * @return Hit list
	 */
	public ArrayList<XYPair> createHitListByHitFlag(boolean[][] f) {
		ArrayList<XYPair> hitList = new ArrayList<XYPair>();
		for(int i = -hiddenHeight; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(getHitFlag(f, j, i, false)) {
					hitList.add(new XYPair(j, i));
				}
			}
		}
		return hitList;
	}

	/**
	 * Get the coodinate type of given position
	 * @param x X position
	 * @param y Y position
	 * @return Coodinate type of given position (COORD_*)
	 */
	public int getCoordType(int x, int y) {
		if(x < 0 || x >= width || y >= height || (fieldCeilingEnable && y <= fieldCeilingY)) {
			return COORD_WALL;
		} else if(y < -hiddenHeight) {
			return COORD_VANISH;
		} else if(y < 0) {
			return COORD_HIDDEN;
		}
		return COORD_NORMAL;
	}

	/**
	 * Get a row of RuntimeBlock as an array
	 * @param y Y position
	 * @return Row of RuntimeBlock, or null if the Y position is invalid
	 */
	public RuntimeBlock[] getRow(int y) {
		try {
			return getRowE(y);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Get a row of RuntimeBlock as an array
	 * @param y Y position
	 * @return Row of RuntimeBlock
	 * @throws ArrayIndexOutOfBoundsException If the Y position is invalid
	 */
	public RuntimeBlock[] getRowE(int y) throws ArrayIndexOutOfBoundsException {
		return blocks[hiddenHeight+y];
	}

	/**
	 * Set a row of RuntimeBlock
	 * @param y Y position
	 * @param row Row of RuntimeBlock
	 * @return true if successful, false if the Y position is invalid
	 */
	public boolean setRow(int y, RuntimeBlock[] row) {
		try {
			return setRowE(y, row);
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Set a row of RuntimeBlock
	 * @param y Y position
	 * @param row Row of RuntimeBlock
	 * @return Always true
	 * @throws ArrayIndexOutOfBoundsException If the Y position is invalid
	 */
	public boolean setRowE(int y, RuntimeBlock[] row) throws ArrayIndexOutOfBoundsException {
		blocks[y] = row;
		return true;
	}

	/**
	 * Get a RuntimeBlock in specific position
	 * @param x X position
	 * @param y Y position
	 * @return RuntimeBlock if the block exists. null when the cell is empty or position is invalid.
	 */
	public RuntimeBlock getBlock(int x, int y) {
		try {
			return getBlockE(x, y);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Get a RuntimeBlock in specific position
	 * @param x X position
	 * @param y Y position
	 * @return RuntimeBlock if the block exists. null when the cell is empty
	 * @throws ArrayIndexOutOfBoundsException If the position is invalid
	 */
	public RuntimeBlock getBlockE(int x, int y) throws ArrayIndexOutOfBoundsException {
		return blocks[hiddenHeight+y][x];
	}

	/**
	 * Set a RuntimeBlock in specific position
	 * @param x X position
	 * @param y Y position
	 * @param runtimeBlock RuntimeBlock to set
	 * @return true if successful, false if the position is invalid
	 */
	public boolean setBlock(int x, int y, RuntimeBlock runtimeBlock) {
		try {
			return setBlockE(x, y, runtimeBlock);
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Set a RuntimeBlock in specific position
	 * @param x X position
	 * @param y Y position
	 * @param runtimeBlock RuntimeBlock to set
	 * @return Always true
	 * @throws ArrayIndexOutOfBoundsException If the position is invalid
	 */
	public boolean setBlockE(int x, int y, RuntimeBlock runtimeBlock) throws ArrayIndexOutOfBoundsException {
		blocks[hiddenHeight+y][x] = runtimeBlock;
		return true;
	}

	/**
	 * Check if the whole field is empty
	 * @return true if the whole field is empty
	 */
	public boolean isEmpty() {
		for(int i = 0; i < getTotalHeight(); i++) {
			for(int x = 0; x < getWidth(); x++) {
				if(blocks[i][x] != null)
					return false;
			}
		}
		return true;
	}

	/**
	 * Check if the specific position is empty
	 * @param x X position
	 * @param y Y position
	 * @return true if the specified position is empty or vanish zone, false when the space is not empty or invalid position
	 */
	public boolean isEmpty(int x, int y) {
		try {
			return isEmptyE(x, y);
		} catch (ArrayIndexOutOfBoundsException e) {
			// false if wall, true if vanish zone
			return (getCoordType(x,y) == COORD_VANISH);
		}
	}

	/**
	 * Check if the specific position is empty
	 * @param x X position
	 * @param y Y position
	 * @return true if the specified position is empty
	 * @throws ArrayIndexOutOfBoundsException If the position is invalid
	 */
	public boolean isEmptyE(int x, int y) throws ArrayIndexOutOfBoundsException {
		return (blocks[hiddenHeight+y][x] == null);
	}

	/**
	 * Check if the specific position is empty or invalid position
	 * @param x X position
	 * @param y Y position
	 * @return true if the specified position is empty or invalid position
	 */
	public boolean isEmptyOrWall(int x, int y) {
		try {
			return isEmptyE(x, y);
		} catch (ArrayIndexOutOfBoundsException e) {
			return (getCoordType(x,y) == COORD_WALL);
		}
	}

	/**
	 * Check if the specific position is anti gravity
	 * @param x X position
	 * @param y Y position
	 * @return true if the specified position is anti gravity
	 */
	public boolean isAntiGravity(int x, int y) {
		if(getCoordType(x, y) == COORD_WALL) return true;
		RuntimeBlock b = getBlock(x, y);
		if(b != null) {
			if(b.getBlockDef().getTags().contains("@AntiGravity"))
				return true;
		}
		return false;
	}

	/**
	 * Get the highest Y block position
	 * @return Highest Y block position (equals to getHeight() when the field is empty)
	 */
	public int getHighestBlockY() {
		int m = getHeight();
		for(int x = 0; x < width; x++) {
			m = Math.min(m, getHighestBlockY(x));
		}
		return m;
	}

	/**
	 * Get the highest Y block position of given X position
	 * @param x X position
	 * @return Highest Y block position (equals to getHeight() when the given X position has no blocks)
	 */
	public int getHighestBlockY(int x) {
		for(int i = -hiddenHeight; i < height; i++) {
			if(!isEmpty(x, i))
				return i;
		}
		return height;
	}

	/**
	 * Get the lowest Y block position
	 * @return Lowest Y block position (equals to -getHiddenHeight()-1 when the field is empty)
	 */
	public int getLowestBlockY() {
		int m = 0;
		for(int x = 0; x < width; x++) {
			m = Math.max(m, getLowestBlockY(x));
		}
		return m;
	}

	/**
	 * Get the lowest Y block position of given X position
	 * @param x X position
	 * @return Lowest Y block position (equals to -getHiddenHeight()-1 when the given X position has no blocks)
	 */
	public int getLowestBlockY(int x) {
		for(int i = height - 1; i >= -hiddenHeight; i--) {
			if(!isEmpty(x, i))
				return i;
		}
		return -hiddenHeight-1;
	}

	/**
	 * Get the width of this field
	 * @return Width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the visible height of this field
	 * @return Visible height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the hidden height of this field
	 * @return Hidden height
	 */
	public int getHiddenHeight() {
		return hiddenHeight;
	}

	/**
	 * Get the total height (visible+hidden) of this field
	 * @return Total height
	 */
	public int getTotalHeight() {
		return hiddenHeight+height;
	}

	/**
	 * Returns true if field ceiling is enabled
	 * @return If field ceiling is enabled
	 */
	public boolean isFieldCeilingEnable() {
		return fieldCeilingEnable;
	}

	/**
	 * Enable or disable field ceiling feature
	 * @param fieldCeilingEnable true to enable field ceiling
	 */
	public void setFieldCeilingEnable(boolean fieldCeilingEnable) {
		this.fieldCeilingEnable = fieldCeilingEnable;
	}

	/**
	 * Get the Y position of field ceiling.
	 * This value is not used unless field ceiling is enabled by setFieldCeilingEnable(boolean).
	 * @return Y position of field ceiling
	 */
	public int getFieldCeilingY() {
		return fieldCeilingY;
	}

	/**
	 * Set the Y position of field ceiling.
	 * This value is not used unless field ceiling is enabled by setFieldCeilingEnable(boolean).
	 * @param fieldCeilingY Y position of field ceiling
	 */
	public void setFieldCeilingY(int fieldCeilingY) {
		this.fieldCeilingY = fieldCeilingY;
	}

	/**
	 * Create a hit list for continuous horizontal line of blocks
	 * @param x Starting X position
	 * @param y Starting Y position
	 * @param tags Required tags (null or empty list for no requirement)
	 * @return XYPair hit list
	 */
	public List<XYPair> matchHorizontal(int x, int y, List<String> tags) {
		return matchHorizontal(x, y, tags, false);
	}

	/**
	 * Create a hit list for continuous horizontal line of blocks
	 * @param x Starting X position
	 * @param y Starting Y position
	 * @param tags Required tags (null or empty list for no requirement)
	 * @param andTag If true, all tags listed in "tags" parameter must exist in a block to match
	 * @return XYPair hit list
	 */
	public List<XYPair> matchHorizontal(int x, int y, List<String> tags, boolean andTag) {
		ArrayList<XYPair> hitList = new ArrayList<XYPair>();

		// The starting point
		RuntimeBlock bStart = getBlock(x, y);
		if(bStart == null) return hitList;
		if(tags != null && tags.size() > 0) {
			if(andTag) {
				if(!NEUtil.checkTags(tags, bStart.getBlockDef().getTags()))
					return hitList;
			} else {
				if(!NEUtil.hasCommonItem(tags, bStart.getBlockDef().getTags()))
					return hitList;
			}
		}
		hitList.add(new XYPair(x, y));

		int j;

		// Left
		j = x - 1;
		while(j >= 0) {
			RuntimeBlock b = getBlock(j, y);
			if(b != null) {
				boolean tagMatched = true;
				if(tags != null && tags.size() > 0) {
					if(andTag) {
						tagMatched = NEUtil.checkTags(tags, b.getBlockDef().getTags());
					} else {
						tagMatched = NEUtil.hasCommonItem(tags, b.getBlockDef().getTags());
					}
				}
				if(tagMatched) {
					hitList.add(new XYPair(j, y));
					j--;
					continue;
				}
			}
			break;
		}

		// Right
		j = x + 1;
		while(j < width) {
			RuntimeBlock b = getBlock(j, y);
			if(b != null) {
				boolean tagMatched = true;
				if(tags != null && tags.size() > 0) {
					if(andTag) {
						tagMatched = NEUtil.checkTags(tags, b.getBlockDef().getTags());
					} else {
						tagMatched = NEUtil.hasCommonItem(tags, b.getBlockDef().getTags());
					}
				}
				if(tagMatched) {
					hitList.add(new XYPair(j, y));
					j++;
					continue;
				}
			}
			break;
		}

		return hitList;
	}

	/**
	 * Create a hit list for continuous vertical line of blocks
	 * @param x Starting X position
	 * @param y Starting Y position
	 * @param tags Required tags (null or empty list for no requirement)
	 * @return XYPair hit list
	 */
	public List<XYPair> matchVertical(int x, int y, List<String> tags) {
		return matchVertical(x, y, tags, false);
	}

	/**
	 * Create a hit list for continuous vertical line of blocks
	 * @param x Starting X position
	 * @param y Starting Y position
	 * @param tags Required tags (null or empty list for no requirement)
	 * @param andTag If true, all tags listed in "tags" parameter must exist in a block to match
	 * @return XYPair hit list
	 */
	public List<XYPair> matchVertical(int x, int y, List<String> tags, boolean andTag) {
		ArrayList<XYPair> hitList = new ArrayList<XYPair>();

		// The starting point
		RuntimeBlock bStart = getBlock(x, y);
		if(bStart == null) return hitList;
		if(tags != null && tags.size() > 0) {
			if(andTag) {
				if(!NEUtil.checkTags(tags, bStart.getBlockDef().getTags()))
					return hitList;
			} else {
				if(!NEUtil.hasCommonItem(tags, bStart.getBlockDef().getTags()))
					return hitList;
			}
		}
		hitList.add(new XYPair(x, y));

		int i;

		// Up
		i = y - 1;
		while(i >= -hiddenHeight) {
			RuntimeBlock b = getBlock(x, i);
			if(b != null) {
				boolean tagMatched = true;
				if(tags != null && tags.size() > 0) {
					if(andTag) {
						tagMatched = NEUtil.checkTags(tags, b.getBlockDef().getTags());
					} else {
						tagMatched = NEUtil.hasCommonItem(tags, b.getBlockDef().getTags());
					}
				}
				if(tagMatched) {
					hitList.add(new XYPair(x, i));
					i--;
					continue;
				}
			}
			break;
		}

		// Down
		i = y + 1;
		while(i < height) {
			RuntimeBlock b = getBlock(x, i);
			if(b != null) {
				boolean tagMatched = true;
				if(tags != null && tags.size() > 0) {
					if(andTag) {
						tagMatched = NEUtil.checkTags(tags, b.getBlockDef().getTags());
					} else {
						tagMatched = NEUtil.hasCommonItem(tags, b.getBlockDef().getTags());
					}
				}
				if(tagMatched) {
					hitList.add(new XYPair(x, i));
					i++;
					continue;
				}
			}
			break;
		}

		return hitList;
	}

	/**
	 * Create a hit list with flood fill algorithm
	 * @param x Starting X position
	 * @param y Starting Y position
	 * @param tags Required tags (null or empty list for no requirement)
	 * @param andTag If true, all tags listed in "tags" parameter must exist in a block to match
	 * @param diagonal true to check diagonal connection
	 * @return XYPair hit list
	 */
	public List<XYPair> matchFloodFill(int x, int y, List<String> tags, boolean andTag, boolean diagonal) {
		resetHitFlag();
		boolean matched = matchFloodFillSub(x, y, tags, andTag, diagonal);
		if(matched) {
			return createHitListByHitFlag();
		}
		return new ArrayList<XYPair>();
	}

	protected boolean matchFloodFillSub(int x, int y, List<String> tags, boolean andTag, boolean diagonal) {
		if(getHitFlag(x, y, true)) return false;

		RuntimeBlock b = getBlock(x, y);
		if(b != null) {
			boolean tagMatched = true;
			if(tags != null && tags.size() > 0) {
				if(andTag) {
					tagMatched = NEUtil.checkTags(tags, b.getBlockDef().getTags());
				} else {
					tagMatched = NEUtil.hasCommonItem(tags, b.getBlockDef().getTags());
				}
			}
			if(tagMatched) {
				setHitFlag(x, y, true);
				matchFloodFillSub(x, y - 1, tags, andTag, diagonal);
				matchFloodFillSub(x, y + 1, tags, andTag, diagonal);
				matchFloodFillSub(x - 1, y, tags, andTag, diagonal);
				matchFloodFillSub(x + 1, y, tags, andTag, diagonal);
				if(diagonal) {
					matchFloodFillSub(x - 1, y - 1, tags, andTag, diagonal);
					matchFloodFillSub(x + 1, y - 1, tags, andTag, diagonal);
					matchFloodFillSub(x - 1, y + 1, tags, andTag, diagonal);
					matchFloodFillSub(x + 1, y + 1, tags, andTag, diagonal);
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * Create a hit list by using the block's connection flags
	 * @param x Starting X position
	 * @param y Starting Y position
	 * @param tags Required tags (null or empty list for no requirement)
	 * @return XYPair hit list
	 */
	public List<XYPair> matchConnect(int x, int y, List<String> tags) {
		return matchConnect(x, y, tags, false);
	}

	/**
	 * Create a hit list by using the block's connection flags
	 * @param x Starting X position
	 * @param y Starting Y position
	 * @param tags Required tags (null or empty list for no requirement)
	 * @param andTag If true, all tags listed in "tags" parameter must exist in a block to match
	 * @return XYPair hit list
	 */
	public List<XYPair> matchConnect(int x, int y, List<String> tags, boolean andTag) {
		resetHitFlag();
		boolean matched = matchConnectSub(x, y, tags, andTag);
		if(matched) {
			return createHitListByHitFlag();
		}
		return new ArrayList<XYPair>();
	}

	protected boolean matchConnectSub(int x, int y, List<String> tags, boolean andTag) {
		if(getHitFlag(x, y, true)) return false;

		RuntimeBlock b = getBlock(x, y);
		if(b != null) {
			boolean tagMatched = true;
			if(tags != null && tags.size() > 0) {
				if(andTag) {
					tagMatched = NEUtil.checkTags(tags, b.getBlockDef().getTags());
				} else {
					tagMatched = NEUtil.hasCommonItem(tags, b.getBlockDef().getTags());
				}
			}
			if(tagMatched) {
				setHitFlag(x, y, true);
				if(b.isConnectUp())    matchConnectSub(x, y - 1, tags, andTag);
				if(b.isConnectDown())  matchConnectSub(x, y + 1, tags, andTag);
				if(b.isConnectLeft())  matchConnectSub(x - 1, y, tags, andTag);
				if(b.isConnectRight()) matchConnectSub(x + 1, y, tags, andTag);
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if gravity can be applied to the specific position by using flood fill algorithm
	 * @param x Starting X position
	 * @param y Starting Y position
	 * @param tags Required tags (null or empty list for no requirement)
	 * @param andTag If true, all tags listed in "tags" parameter must exist in a block to match
	 * @param diagonal true to check diagonal connection
	 * @return true if gravity can be applied
	 */
	public boolean checkGravityFloodFill(int x, int y, List<String> tags, boolean andTag, boolean diagonal) {
		List<XYPair> xyList = matchFloodFill(x, y, tags, andTag, diagonal);
		if(!xyList.isEmpty()) {
			for(XYPair xyPair: xyList) {
				XYPair xyDown = new XYPair(xyPair.getX(), xyPair.getY()+1);

				if(isAntiGravity(xyDown.getX(), xyDown.getY())) {
					return false;
				}
				if(!isEmpty(xyDown.getX(), xyDown.getY()) && !xyList.contains(xyDown)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Check if gravity can be applied to somewhere by using flood fill algorithm
	 * @param tags Required tags (null or empty list for no requirement)
	 * @param andTag If true, all tags listed in "tags" parameter must exist in a block to match
	 * @param diagonal true to check diagonal connection
	 * @return true if gravity can be applied
	 */
	public boolean checkGravityFloodFill(List<String> tags, boolean andTag, boolean diagonal) {
		boolean result = false;
		resetFallFlag();

		for(int i = height - 1; i >= -hiddenHeight; i--) {
			for(int j = 0; j < width; j++) {
				if(getHitFlag(fallFlag, j, i - 1, false) == false && !isEmpty(j, i)) {
					if(checkGravityFloodFill(j, i, tags, andTag, diagonal)) {
						result = true;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Apply gravity to specific position by using flood fill algorithm
	 * @param x Starting X position
	 * @param y Starting Y position
	 * @param tags Required tags (null or empty list for no requirement)
	 * @param andTag If true, all tags listed in "tags" parameter must exist in a block to match
	 * @param diagonal true to check diagonal connection
	 * @return true if gravity is applied and something fell
	 */
	public boolean doGravityFloodFill(int x, int y, List<String> tags, boolean andTag, boolean diagonal) {
		if(!checkGravityFloodFill(x, y, tags, andTag, diagonal)) return false;
		return (doGravity() > 0);
	}

	/**
	 * Apply gravity to entire field by using flood fill algorithm
	 * @param tags Required tags (null or empty list for no requirement)
	 * @param andTag If true, all tags listed in "tags" parameter must exist in a block to match
	 * @param diagonal true to check diagonal connection
	 * @return true if gravity is applied and something fell
	 */
	public boolean doGravityFloodFill(List<String> tags, boolean andTag, boolean diagonal) {
		boolean result = false;
		resetFallFlag();

		for(int i = height - 1; i >= -hiddenHeight; i--) {
			for(int j = 0; j < width; j++) {
				if(getHitFlag(fallFlag, j, i - 1, false) == false && !isEmpty(j, i)) {
					if(doGravityFloodFill(j, i, tags, andTag, diagonal)) {
						result = true;
					}
				}
			}
		}

		return result;
	}

	public boolean checkGravityConnect(int x, int y, List<String> tags) {
		return checkGravityConnect(x, y, tags, false);
	}

	public boolean checkGravityConnect(int x, int y, List<String> tags, boolean andTag) {
		List<XYPair> xyList = matchConnect(x, y, tags, andTag);
		if(!xyList.isEmpty()) {
			for(XYPair xyPair: xyList) {
				XYPair xyDown = new XYPair(xyPair.getX(), xyPair.getY()+1);

				if(isAntiGravity(xyDown.getX(), xyDown.getY())) {
					return false;
				}
				if(!isEmpty(xyDown.getX(), xyDown.getY()) && !xyList.contains(xyDown)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean checkGravityConnect(List<String> tags) {
		return checkGravityConnect(tags, false);
	}

	public boolean checkGravityConnect(List<String> tags, boolean andTag) {
		boolean result = false;
		resetFallFlag();

		for(int i = height - 1; i >= -hiddenHeight; i--) {
			for(int j = 0; j < width; j++) {
				if(getHitFlag(fallFlag, j, i, false) == false && !isEmpty(j, i)) {
					if(checkGravityConnect(j, i, tags, andTag)) {
						result = true;
					}
				}
			}
		}

		return result;
	}

	public boolean doGravityConnect(int x, int y, List<String> tags) {
		return doGravityConnect(x, y, tags, false);
	}

	public boolean doGravityConnect(int x, int y, List<String> tags, boolean andTag) {
		if(!checkGravityConnect(x, y, tags, andTag)) return false;
		return (doGravity() > 0);
	}

	public boolean doGravityConnect(List<String> tags) {
		return doGravityConnect(tags, false);
	}

	public boolean doGravityConnect(List<String> tags, boolean andTag) {
		boolean result = false;
		resetFallFlag();

		for(int i = height - 1; i >= -hiddenHeight; i--) {
			for(int j = 0; j < width; j++) {
				if(getHitFlag(fallFlag, j, i, false) == false && !isEmpty(j, i)) {
					if(doGravityConnect(j, i, tags, andTag)) {
						result = true;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Move all floating block one space down by using the default hit flag and fall flag
	 * @return Number of blocks fell
	 */
	public int doGravity() {
		return doGravity(hitFlag, fallFlag);
	}

	/**
	 * Move all floating block one space down by using custom hit flag and fall flag
	 * @param hitFlag Hit flag array
	 * @param fallFlag Fall flag array
	 * @return Number of blocks fell
	 */
	public int doGravity(boolean[][] hitFlag, boolean[][] fallFlag) {
		int count = 0;

		for(int i = height - 1; i >= -hiddenHeight; i--) {
			for(int j = 0; j < width; j++) {
				if(getHitFlag(hitFlag, j, i - 1, false) == true) {
					setBlock(j, i, getBlock(j, i - 1));
					setBlock(j, i - 1, null);
					setHitFlag(hitFlag, j, i, true);
					setHitFlag(hitFlag, j, i - 1, false);
					setHitFlag(fallFlag, j, i, true);
					setHitFlag(fallFlag, j, i - 1, false);
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * Classic gravity used by the Soviet mind game. This method is quite hackish, sorry :p
	 * @param ySet [In/Out] Set of Y positions where a "Line" is cleared. The contents of this set will get modified!
	 * @return true if something fell
	 */
	public boolean doGravityClassic(Set<Integer> ySet) {
		int y = height - 1;

		for(int i = -hiddenHeight; i < height; i++) {
			if(ySet.contains(y)) {
				for(int k = y; k >= -hiddenHeight; k--) {
					for(int j = 0; j < width; j++) {
						setBlock(j, k, getBlock(j, k - 1));
						setBlock(j, k - 1, null);
					}
					if(ySet.contains(k - 1)) {
						ySet.remove(k - 1);
						ySet.add(k);
					} else {
						ySet.remove(k);
					}
				}
				return true;
			} else {
				y--;
			}
		}

		return false;
	}

	/**
	 * Free Fall gravity. No connection checks. It does check the anti gravity tag, though.
	 * @return true if something fell
	 */
	public boolean doGravityFreeFall() {
		boolean result = false;

		for(int i = height - 1; i >= -hiddenHeight; i--) {
			for(int j = 0; j < width; j++) {
				if(isEmpty(j, i) && !isEmpty(j, i - 1) && !isAntiGravity(j, i - 1)) {
					result = true;
					setBlock(j, i, getBlock(j, i - 1));
					setBlock(j, i - 1, null);
				}
			}
		}

		return result;
	}

	public Set<XYPair> createXYPairSetByPattern(Pattern pat) {
		return createXYPairSetByPattern(pat, null);
	}

	public Set<XYPair> createXYPairSetByPattern(Pattern pat, ScriptHost scriptHost) {
		HashSet<XYPair> resultSet = new HashSet<XYPair>();

		for(int i = height - 1; i >= -hiddenHeight; i--) {
			for(int j = 0; j < width; j++) {
				resultSet.addAll(createXYPairSetByPattern(pat, j, i, scriptHost));
			}
		}

		return resultSet;
	}

	public Set<XYPair> createXYPairSetByPattern(Pattern pat, int x, int y) {
		return createXYPairSetByPattern(pat, x, y, null);
	}

	public Set<XYPair> createXYPairSetByPattern(Pattern pat, int x, int y, ScriptHost scriptHost) {
		HashSet<XYPair> resultSet = new HashSet<XYPair>();
		List<XYPair> tempList = null;

		if("Horizontal".equals(pat.getMethod()))
			tempList = matchHorizontal(x, y, pat.getTags(), pat.isAndTag());
		else if("Vertical".equals(pat.getMethod()))
			tempList = matchVertical(x, y, pat.getTags(), pat.isAndTag());
		else if("FloodFill".equals(pat.getMethod()))
			tempList = matchFloodFill(x, y, pat.getTags(), pat.isAndTag(), false);
		else if("FloodFillDiagonal".equals(pat.getMethod()))
			tempList = matchFloodFill(x, y, pat.getTags(), pat.isAndTag(), true);
		else if("Connect".equals(pat.getMethod()))
			tempList = matchConnect(x, y, pat.getTags(), pat.isAndTag());
		else if("None".equals(pat.getMethod()))
			tempList = null;
		else if(StringUtils.startsWith(pat.getMethod(), "Script:"))
		{
			final String patMethod = StringUtils.removeStart(pat.getMethod(), "Script:");

			if(scriptHost == null) {
				throw new IllegalArgumentException("Script pattern method '" + patMethod + "' is used, " +
													"but no ScriptHost is available here.");
			} else {
				tempList =
				(ArrayList<XYPair>)(
					scriptHost.callDef(
						"matchCustom", new ArrayList<XYPair>(),
						patMethod, x, y, pat.getTags(), pat.isAndTag()
					)
				);
			}
		}
		else if(!StringUtils.isEmpty(pat.getMethod()))
			throw new IllegalArgumentException("Unknown pattern method '" + pat.getMethod() + "'");

		if((tempList != null) && !tempList.isEmpty()) {
			if( (tempList.size() >= pat.getMinMatch()) &&
				((tempList.size() < pat.getMaxMatch()) || (pat.getMaxMatch() <= 0)) )
			{
				resultSet.addAll(tempList);
			}
		}

		return resultSet;
	}
}
