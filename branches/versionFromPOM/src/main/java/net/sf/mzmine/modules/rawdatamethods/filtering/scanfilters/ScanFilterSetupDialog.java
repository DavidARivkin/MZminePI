/*
 * Copyright 2006-2014 The MZmine 2 Development Team
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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.rawdatamethods.filtering.scanfilters;

import java.awt.Color;

import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.modules.visualization.spectra.PlotMode;
import net.sf.mzmine.modules.visualization.spectra.SpectraPlot;
import net.sf.mzmine.modules.visualization.spectra.SpectraVisualizerWindow;
import net.sf.mzmine.modules.visualization.spectra.datasets.ScanDataSet;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.dialogs.ParameterSetupDialogWithScanPreview;

/**
 * This class extends ParameterSetupDialog class, including a spectraPlot. This
 * is used to preview how the selected raw data filter and his parameters works
 * over the raw data file.
 */
public class ScanFilterSetupDialog extends ParameterSetupDialogWithScanPreview {

	private static final long serialVersionUID = 1L;
	private ParameterSet filterParameters;
    private ScanFilter rawDataFilter;

    /**
     * @param parameters
     * @param rawDataFilterTypeNumber
     */
    public ScanFilterSetupDialog(ParameterSet filterParameters,
	    Class<? extends ScanFilter> filterClass) {

	super(filterParameters);
	this.filterParameters = filterParameters;

	try {
	    this.rawDataFilter = filterClass.newInstance();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * This function set all the information into the plot chart
     * 
     * @param scanNumber
     */
    protected void loadPreview(SpectraPlot spectrumPlot, Scan previewScan) {

	Scan newScan = rawDataFilter.filterScan(previewScan, filterParameters);

	ScanDataSet spectraDataSet = new ScanDataSet("Filtered scan", newScan);
	ScanDataSet spectraOriginalDataSet = new ScanDataSet("Original scan",
		previewScan);

	spectrumPlot.removeAllDataSets();

	spectrumPlot.addDataSet(spectraOriginalDataSet,
		SpectraVisualizerWindow.scanColor, true);
	spectrumPlot.addDataSet(spectraDataSet, Color.green, true);

	// if the scan is centroided, switch to centroid mode
	if (previewScan.isCentroided()) {
	    spectrumPlot.setPlotMode(PlotMode.CENTROID);
	} else {
	    spectrumPlot.setPlotMode(PlotMode.CONTINUOUS);
	}

    }
}
