package utils.structures.range;

public interface RangeMapInterface<O>
{

	/**
	 * Clears a Rangemap
	 */
	public abstract void clear();

	/**
	 * Puts a mapping between integers in the Range and an Object.
	 * Any existing mapping is overwritten.
	 * @param pRange
	 * @param pObject
	 */
	public abstract void put(final Range pRange, final O pObject);

	/**
	 * Gets the Object associated to an integer position.
	 * @param pPosition 
	 * @return
	 */
	public abstract O get(final int pPosition);

	/**
	 * Returns the first non null Object mapped by the (smallest) integer.
	 * @return
	 */
	public abstract O getFirst();

	/**
	 * Returns the last non null Object mapped by the (biggest) integer.
	 * @return
	 */
	public abstract O getLast();

	/**
	 * Translates all mappings by a given offset.
	 * @param pOffset
	 */
	public abstract void translate(int pOffset);

}