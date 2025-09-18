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
package net.sourceforge.plantuml.klimt.geom;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.shape.TextBlock;

public class ULayoutGroup {
	// ::remove file when __HAXE__

	private final PlacementStrategy placementStrategy;

	public ULayoutGroup(PlacementStrategy placementStrategy) {
		this.placementStrategy = placementStrategy;
	}

	public void drawU(UGraphic ug, double width, double height) {
		for (Map.Entry<TextBlock, XPoint2D> ent : placementStrategy.getPositions(width, height).entrySet()) {
			final TextBlock block = ent.getKey();
			final XPoint2D pos = ent.getValue();
			block.drawU(ug.apply(UTranslate.point(pos)));
		}
	}

	public void add(TextBlock block) {
		placementStrategy.add(block);

	}

	public XRectangle2D getInnerPosition(CharSequence member, double width, double height,
			StringBounder stringBounder) {
		final Set<Entry<TextBlock, XPoint2D>> all = placementStrategy.getPositions(width, height).entrySet();
		return tryOne(all, member, stringBounder);
	}

	private XRectangle2D tryOne(final Set<Entry<TextBlock, XPoint2D>> all, CharSequence member,
			StringBounder stringBounder) {
		for (Map.Entry<TextBlock, XPoint2D> ent : all) {
			final TextBlock block = ent.getKey();
			final XRectangle2D result = block.getInnerPosition(member, stringBounder);
			if (result != null) {
				final UTranslate translate = UTranslate.point(ent.getValue());
				return translate.apply(result);
			}
		}
		return null;
	}

}
