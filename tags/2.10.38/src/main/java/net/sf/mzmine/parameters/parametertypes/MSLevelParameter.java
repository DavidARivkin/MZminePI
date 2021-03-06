/*
 * Copyright 2006-2012 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.parameters.parametertypes;

/**
 * 
 */
public class MSLevelParameter extends ComboParameter<Integer> {

	public MSLevelParameter() {
		super("MS level (for complete files)",
				"This parameter is ignored when specific scans have been selected.\n" +
					"It is only used when processing all scans in a given raw data file.\n" +
					"MS level 1 means full scans, MS level 2 means MS/MS, etc.",
				new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 1);
	}

	@Override
	public MSLevelParameter cloneParameter() {
		MSLevelParameter copy = new MSLevelParameter();
		copy.setValue(getValue());
		return copy;
	}
}
