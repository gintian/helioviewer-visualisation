package ch.fhnw.i4ds.timelineviz.utils;

import org.joda.time.ReadableInstant;

public class TimeUtils {
	
	/**
	 * Does this time interval (defined with startReadableInstant and
	 * endReadableInstant) contain the readableInstant.
	 * <p>
	 * Non-zero duration intervals are inclusive of the start instant and
	 * inclusive of the end.
	 * <p>
	 * For example:
	 * 
	 * <pre>
	 * [09:00 to 10:00) contains 08:59  = false (before start)
	 * [09:00 to 10:00) contains 09:00  = true
	 * [09:00 to 10:00) contains 09:59  = true
	 * [09:00 to 10:00) contains 10:00  = true
	 * [09:00 to 10:00) contains 10:01  = false (after end)
	 * 
	 * [14:00 to 14:00) contains 14:00  = true
	 * </pre>
	 * 
	 * @param readableInstant
	 *            the instant
	 * @param startReadableInstant
	 *            start of interval
	 * @param endReadableInstant
	 *            end of interval
	 * @return true if the time interval contains the instant
	 */
	public static boolean isFittingInInterval(ReadableInstant readableInstant, ReadableInstant startReadableInstant,
			ReadableInstant endReadableInstant) {
		return readableInstant.isEqual(startReadableInstant) || readableInstant.isEqual(endReadableInstant)
				|| (readableInstant.isAfter(startReadableInstant) && readableInstant.isBefore(endReadableInstant));
	}

	/**
	 * Get the later ReadableInstant.
	 * @param readableInstantA
	 * @param readableInstantB
	 * @return max ReadableInstant 
	 */
	public static ReadableInstant getMaxReadableInstant(ReadableInstant readableInstantA,
			ReadableInstant readableInstantB) {
		if (readableInstantA == null) {
			return readableInstantB;
		}
		if (readableInstantB == null) {
			return readableInstantA;
		}

		if (readableInstantA.compareTo(readableInstantB) >= 0) {
			return readableInstantA;
		} else {
			return readableInstantB;
		}
	}
}
