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

/* Code created was by or on behalf of Syngenta and is released under the open source license in use for the
 * pre-existing code or project. Syngenta does not assert ownership or copyright any over pre-existing work.
 */

package net.sf.mzmine.modules.peaklistmethods.peakpicking.smoothing;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.modules.MZmineModuleCategory;
import net.sf.mzmine.modules.MZmineProcessingModule;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.util.ExitCode;

/**
 * Chromatographic smoothing.
 * 
 * @version $Revision: 3286 $
 */
public class SmoothingModule implements MZmineProcessingModule {

    private static final String MODULE_NAME = "Smoothing";
    private static final String MODULE_DESCRIPTION = "This module performs smoothing of chromatograms prior to deconvolution.";

    @Override
    public @Nonnull String getName() {
	return MODULE_NAME;
    }

    @Override
    public @Nonnull String getDescription() {
	return MODULE_DESCRIPTION;
    }

    @Override
    @Nonnull
    public ExitCode runModule(@Nonnull ParameterSet parameters,
	    @Nonnull Collection<Task> tasks) {
	PeakList peakLists[] = parameters.getParameter(
		SmoothingParameters.peakLists).getValue();

	for (final PeakList peakList : peakLists) {
	    Task newTask = new SmoothingTask(peakList, parameters);
	    tasks.add(newTask);
	}

	return ExitCode.OK;
    }

    @Override
    public @Nonnull MZmineModuleCategory getModuleCategory() {
	return MZmineModuleCategory.PEAKLISTPICKING;
    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
	return SmoothingParameters.class;
    }

}
