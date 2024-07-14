/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 *
 *
 */
package net.sourceforge.plantuml.api;

import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;

public class ImageDataSimple extends ImageDataAbstract {

	private Throwable rootCause;

	public ImageDataSimple(int width, int height) {
		super(width, height);
	}

	public ImageDataSimple(XDimension2D dim) {
		super(dim);
	}

	public ImageDataSimple(XDimension2D dim, int status) {
		super(dim);
		setStatus(status);
	}

	private ImageDataSimple() {
		this(0, 0);
	}

	public boolean containsCMapData() {
		return false;
	}

	public String getCMapData(String nameId) {
		throw new UnsupportedOperationException();
	}

	public String getWarningOrError() {
		return null;
	}

	public static ImageData error() {
		final ImageDataSimple result = new ImageDataSimple();
		result.setStatus(503);
		return result;
	}

	public static ImageData ok() {
		return new ImageDataSimple();
	}

	public static ImageData error(Throwable e) {
		final ImageDataSimple result = new ImageDataSimple();
		result.setStatus(503);
		result.rootCause = e;
		return result;
	}

	@Override
	public Throwable getRootCause() {
		return rootCause;
	}

}
