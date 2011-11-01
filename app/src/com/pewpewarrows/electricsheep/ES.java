package com.pewpewarrows.electricsheep;

/**
 * "ES" is the short-hand equivalent of a Constants class for ElectricSheep
 * 
 * This should only contain properties of type "public static final", excluding
 * the constructor. In addition, those values should only be related to the 
 * library in general.
 */
public final class ES {
	// public static final boolean DEBUG = true;
	
	/**
	 * Intentially private. Java has no native way to describe a class that
	 * should never be instantiated.
	 * 
	 * The error is thrown in the event that someone attempts to be sneaky and
	 * use reflection.
	 */
	private ES() {
		throw new UnsupportedOperationException();
	}
}