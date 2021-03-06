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

package net.sf.mzmine.modules.peaklistmethods.dataanalysis.rtmzplots.logratioplot;

import net.sf.mzmine.data.PeakList;
import net.sf.mzmine.data.RawDataFile;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.ComboParameter;
import net.sf.mzmine.parameters.parametertypes.MultiChoiceParameter;
import net.sf.mzmine.parameters.parametertypes.PeakListsParameter;
import net.sf.mzmine.util.ExitCode;
import net.sf.mzmine.util.PeakMeasurementType;

public class LogratioParameters extends SimpleParameterSet {

    public static final PeakListsParameter peakLists = new PeakListsParameter(
	    1, 1);

    public static final MultiChoiceParameter<RawDataFile> groupOneFiles = new MultiChoiceParameter<RawDataFile>(
	    "Group one", "Samples in group one", new RawDataFile[0], null, 1);

    public static final MultiChoiceParameter<RawDataFile> groupTwoFiles = new MultiChoiceParameter<RawDataFile>(
	    "Group two", "Samples in group two", new RawDataFile[0], null, 1);

    public static final ComboParameter<PeakMeasurementType> measurementType = new ComboParameter<PeakMeasurementType>(
	    "Peak measurement type",
	    "Determines whether peak's area or height is used in computations.",
	    PeakMeasurementType.values());

    public LogratioParameters() {
	super(new Parameter[] { peakLists, groupOneFiles, groupTwoFiles,
		measurementType });
    }

    @Override
    public ExitCode showSetupDialog() {

	PeakList selectedPeakLists[] = getParameter(peakLists).getValue();

	if (selectedPeakLists.length != 1) {
	    MZmineCore.getDesktop().displayErrorMessage(
		    "Please select a single peak list");
	    return ExitCode.CANCEL;
	}

	RawDataFile plDataFiles[] = selectedPeakLists[0].getRawDataFiles();

	getParameter(groupOneFiles).setChoices(plDataFiles);
	getParameter(groupTwoFiles).setChoices(plDataFiles);

	return super.showSetupDialog();
    }

}
