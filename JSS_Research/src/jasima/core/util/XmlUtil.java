/*******************************************************************************
 * Copyright (c) 2010-2015 Torsten Hildebrandt and jasima contributors
 *
 * This file is part of jasima, v1.2.
 *
 * jasima is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jasima is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jasima.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package jasima.core.util;

import jasima.core.experiment.Experiment.UniqueNamesCheckingHashMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Provides utility methods to read and write arbitrary Java objects as xml
 * (xml-Serialization using the xstream library).
 * 
 * @author Torsten Hildebrandt, 2012-07-13
 * @version 
 *          "$Id$"
 */
public class XmlUtil {

	/**
	 * Loads an object from a String containing xml.
	 * 
	 * @param xmlString
	 *            A String containing xml data.
	 * @see #saveXML(FileFormat,Object)
	 * 
	 * @return The object contained in {@code xmlString}.
	 */
	public static Object loadXML(FileFormat format, String xmlString) {
		XStream xstream = getXStream(format);
		Object o = xstream.fromXML(xmlString);
		return o;
	}

	/**
	 * Loads an object from a file.
	 * 
	 * @param f
	 *            The file to load.
	 * @return The object contained in {@code f}.
	 */
	public static Object loadXML(FileFormat format, File f) {
		XStream xstream = getXStream(format);
		Object o = xstream.fromXML(f);
		return o;
	}

	/**
	 * Loads an object from a {@link Reader}.
	 * 
	 * @param r
	 *            Source of the xml.
	 * @return The object contained in {@code r}.
	 */
	public static Object loadXML(FileFormat format, Reader r) {
		XStream xstream = getXStream(format);
		Object o = xstream.fromXML(r);
		return o;
	}

	/**
	 * Converts an object into a xml String.
	 * 
	 * @param o
	 *            The object to convert.
	 * @return The object serialized to xml.
	 */
	public static String saveXML(FileFormat format, Object o) {
		XStream xstream = getXStream(format);
		return xstream.toXML(o);
	}

	/**
	 * Converts an object into xml and writes the result in {@code w}.
	 * 
	 * @param o
	 *            The object to convert.
	 * @param w
	 *            The output writer.
	 */
	public static void saveXML(FileFormat format, Object o, Writer w) {
		XStream xstream = getXStream(format);
		xstream.toXML(o, w);
	}

	/**
	 * Converts an object into xml and saves the result in a file {@code f}.
	 * 
	 * @param o
	 *            The object to convert.
	 * @param f
	 *            The output file. This file is overwritten if it already
	 *            exists.
	 */
	public static void saveXML(FileFormat format, Object o, File f) {
		XStream xstream = getXStream(format);
		try (BufferedWriter fw = new BufferedWriter(new FileWriter(f))) {
			xstream.toXML(o, fw);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static XStream getXStream(final FileFormat format) {
		XStream xstream = new XStream(new DomDriver() {
			@Override
			public HierarchicalStreamWriter createWriter(Writer out) {
				if(format == FileFormat.JASIMA_BEAN) {
					try {
						out.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<?jasima bean?>\n");
					} catch(IOException e) {
						throw new XStreamException(e);
					}
				}
				return super.createWriter(out);
			}
		});
		xstream.registerConverter(new MapConverter(xstream.getMapper()) {
			@SuppressWarnings("rawtypes")
			@Override
			public boolean canConvert(Class type) {
				if (type.equals(UniqueNamesCheckingHashMap.class))
					return true;
				else
					return super.canConvert(type);
			}
		});
		if(format == FileFormat.JASIMA_BEAN) {
			xstream.registerConverter(new JasimaBeanConverter(xstream.getMapper(), true), -10);
		}
		return xstream;
	}
}
