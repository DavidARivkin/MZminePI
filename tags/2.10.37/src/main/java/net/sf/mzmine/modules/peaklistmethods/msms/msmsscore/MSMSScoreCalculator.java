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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.peaklistmethods.msms.msmsscore;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import net.sf.mzmine.data.DataPoint;
import net.sf.mzmine.data.MassList;
import net.sf.mzmine.data.Scan;
import net.sf.mzmine.modules.peaklistmethods.identification.formulaprediction.FormulaGenerator;
import net.sf.mzmine.modules.peaklistmethods.identification.formulaprediction.elements.ElementRule;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.MZTolerance;
import net.sf.mzmine.util.Range;

import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class MSMSScoreCalculator {

	/**
	 * Returns a calculated similarity score of
	 */
	public static MSMSScore evaluateMSMS(IMolecularFormula parentFormula,
			Scan msmsScan, ParameterSet parameters) {

		MZTolerance msmsTolerance = parameters.getParameter(
				MSMSScoreParameters.msmsTolerance).getValue();
		String massListName = parameters.getParameter(
				MSMSScoreParameters.massList).getValue();

		MassList massList = msmsScan.getMassList(massListName);

		if (massList == null) {
			throw new IllegalArgumentException("Scan #"
					+ msmsScan.getScanNumber()
					+ " does not have a mass list called '" + massListName
					+ "'");
		}

		DataPoint msmsIons[] = massList.getDataPoints();

		if (msmsIons == null) {
			throw new IllegalArgumentException("Mass list " + massList
					+ " does not contain data for scan #"
					+ msmsScan.getScanNumber());
		}
		// Sorted by mass in descending order
		ArrayList<ElementRule> rulesSet = new ArrayList<ElementRule>();
		for (IIsotope isotope : parentFormula.isotopes()) {
			ElementRule rule = new ElementRule(isotope.getSymbol(), 0,
					parentFormula.getIsotopeCount(isotope));
			rulesSet.add(rule);
		}
		ElementRule msmsElementRules[] = rulesSet.toArray(new ElementRule[0]);

		int totalMSMSpeaks = 0, interpretedMSMSpeaks = 0;
		Map<DataPoint, String> msmsAnnotations = new Hashtable<DataPoint, String>();

		msmsCycle: for (DataPoint dp : msmsIons) {

			// Check if this is an isotope
			Range isotopeCheckRange = new Range(dp.getMZ() - 1.4,
					dp.getMZ() - 0.6);
			for (DataPoint dpCheck : msmsIons) {
				// If we have any MS/MS peak with 1 neutron mass smaller m/z
				// and higher intensity, it means the current peak is an
				// isotope and we should ignore it
				if (isotopeCheckRange.contains(dpCheck.getMZ())
						&& (dpCheck.getIntensity() > dp.getIntensity())) {
					continue msmsCycle;
				}
			}

			// We don't know the charge of the fragment, so we will simply
			// assume 1
			double neutralLoss = msmsScan.getPrecursorMZ()
					* msmsScan.getPrecursorCharge() - dp.getMZ();

			// Ignore negative neutral losses and parent ion, <5 may be a
			// good threshold
			if (neutralLoss < 5)
				continue;

			Range msmsTargetRange = msmsTolerance
					.getToleranceRange(neutralLoss);

			FormulaGenerator msmsEngine = new FormulaGenerator(msmsTargetRange,
					msmsElementRules);

			MolecularFormula formula = msmsEngine.getNextFormula();
			if (formula != null) {
				String formulaString = MolecularFormulaManipulator
						.getString(formula);
				msmsAnnotations.put(dp, formulaString);
				interpretedMSMSpeaks++;
			}

			totalMSMSpeaks++;

		}

		// If we did not evaluate any MS/MS peaks, we cannot calculate a score
		if (totalMSMSpeaks == 0)
			return null;

		double msmsScore = (double) interpretedMSMSpeaks / totalMSMSpeaks;

		MSMSScore result = new MSMSScore(msmsScore, msmsAnnotations);

		return result;

	}

}
