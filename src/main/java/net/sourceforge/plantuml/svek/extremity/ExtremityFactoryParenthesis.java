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
package net.sourceforge.plantuml.svek.extremity;

import net.sourceforge.plantuml.cli.GlobalConfig;
import net.sourceforge.plantuml.klimt.geom.Side;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.svek.AbstractExtremityFactory;

public class ExtremityFactoryParenthesis extends AbstractExtremityFactory implements ExtremityFactory {

	@Override
	public UDrawable createUDrawable(XPoint2D p0, double angle, Side side) {
		return new ExtremityParenthesis(p0, angle - Math.PI / 2);
	}

	@Override
	public UDrawable createTBRDrawableLegacy(XPoint2D p0, XPoint2D p1, XPoint2D p2, Side side) {
		final double ortho = atan2(p0, p2);
		if (GlobalConfig.USE_INTERFACE_EYE2) {
			final XPoint2D center = new XPoint2D((p0.getX() + p2.getX()) / 2, (p0.getY() + p2.getY()) / 2);
			return new ExtremityParenthesis2(center, ortho, p1);
		}
		return new ExtremityParenthesis(p1, ortho);
	}

}
