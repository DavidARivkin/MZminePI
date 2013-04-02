/*
 * Copyright 2013-2013 The Veritomyx
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

package net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.Veritomyx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import net.sf.mzmine.data.DataPoint;
import net.sf.mzmine.data.RawDataFile;
import net.sf.mzmine.data.Scan;
import net.sf.mzmine.data.impl.SimpleDataPoint;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.MassDetector;
import net.sf.mzmine.parameters.ParameterSet;
import FileChecksum.FileChecksum;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Veritomyx implements MassDetector
{
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private boolean scans_dumped = false;

	/**
	 * 
	 */
	public DataPoint[] getMassValues(Scan scan, ParameterSet parameters)
	{
		int     first_scan = parameters.getParameter(VeritomyxParameters.first_scan).getValue();
		int     last_scan  = parameters.getParameter(VeritomyxParameters.last_scan).getValue();
		boolean dump_scans = parameters.getParameter(VeritomyxParameters.dump_scans).getValue();
		boolean read_peaks = parameters.getParameter(VeritomyxParameters.read_peaks).getValue();
		
		RawDataFile rawdata = scan.getDataFile();
		ArrayList<DataPoint> mzPeaks = new ArrayList<DataPoint>();

		if (dump_scans && !scans_dumped)
		{
			for (int s = first_scan; s <= last_scan; s++)
			{
				scan = rawdata.getScan(s);
				if (scan == null)
					continue;
				scan.exportToFile("", "");
			}
			scans_dumped = true;
		
			try {
				String args = "p=" + URLEncoder.encode("1000", "UTF-8");
				args += "&Version=1.2.4";
				URL url = new URL("http://test.veritomyx.com/vtmx/interface/vtmx_batch_internal.php");
				URLConnection connection = url.openConnection();
	
				connection.setDoOutput(true);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(args);
				out.close();
	
		        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String decodedString;
				while ((decodedString = in.readLine()) != null)
				{
					System.out.println(decodedString);
				}
				in.close();
	
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (read_peaks)
		{
			int s = scan.getScanNumber();
			if ((s < first_scan) || (s > last_scan))
			{
				return mzPeaks.toArray(new DataPoint[0]);
			}

			String centfilename = scan.exportFilename("").replace(".txt", ".vcent.txt");
			logger.info("Reading centroided data from " + centfilename);
			try
			{
				File centfile = new File(centfilename);
				FileChecksum fchksum = new FileChecksum(centfile);
				if (!fchksum.verify(false))
				{
					throw new IOException("Invalid checksum in centroided file " + centfilename);
				}
				List<String> lines = Files.readLines(centfile , Charsets.UTF_8);
				for (String line:lines)
				{
					if (line.startsWith("#") || line.isEmpty())	// skip comment lines
						continue;

					Scanner sc = new Scanner(line);
					double mz  = sc.nextDouble();
					double y   = sc.nextDouble();
					mzPeaks.add(new SimpleDataPoint(mz, y));
					sc.close();
				}
			}
			catch (Exception e)
			{
				logger.info(e.getMessage());
				e.printStackTrace();
				return mzPeaks.toArray(new DataPoint[0]);
			}
		}

		// Return an array of detected peaks sorted by MZ
		return mzPeaks.toArray(new DataPoint[0]);
    }

    public @Nonnull String getName()
    {
    	return "Veritomyx Centroid";
    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass()
    {
    	return VeritomyxParameters.class;
    }
}