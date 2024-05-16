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
 */
package net.sourceforge.plantuml.timingdiagram.graphic;

import net.sourceforge.plantuml.decoration.WithLinkType;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.klimt.font.UFontContext;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.MagneticBorder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignatureBasic;

import java.util.Map;

public class TimeArrow implements UDrawable {

	private final XPoint2D start;
	private final XPoint2D end;
	private final Display label;
	private final ISkinParam skinParam;
	private StyleBuilder styleBuilder;
	private final WithLinkType type;

	public static TimeArrow create(IntricatedPoint pt1, IntricatedPoint pt2, Display label, ISkinParam spriteContainer,
			WithLinkType type) {
		final TimeArrow arrow1 = new TimeArrow(pt1.getPointA(), pt2.getPointA(), label, spriteContainer, type);
		final TimeArrow arrow2 = new TimeArrow(pt1.getPointA(), pt2.getPointB(), label, spriteContainer, type);
		final TimeArrow arrow3 = new TimeArrow(pt1.getPointB(), pt2.getPointA(), label, spriteContainer, type);
		final TimeArrow arrow4 = new TimeArrow(pt1.getPointB(), pt2.getPointB(), label, spriteContainer, type);
		return shorter(arrow1, arrow2, arrow3, arrow4);
	}

	private TimeArrow(XPoint2D start, XPoint2D end, Display label, ISkinParam skinParam, WithLinkType type) {
		this.start = start;
		this.type = type;
		this.end = end;
		this.label = label;
		this.skinParam = skinParam;
		this.styleBuilder = skinParam.getCurrentStyleBuilder();
	}

	private double getAngle() {
		return Math.atan2(end.getX() - start.getX(), end.getY() - start.getY());
	}

	private static TimeArrow shorter(TimeArrow arrow1, TimeArrow arrow2) {
		if (arrow1.len() < arrow2.len()) {
			return arrow1;
		}
		return arrow2;
	}

	private static TimeArrow shorter(TimeArrow arrow1, TimeArrow arrow2, TimeArrow arrow3, TimeArrow arrow4) {
		return shorter(shorter(arrow1, arrow2), shorter(arrow3, arrow4));
	}

	private double len() {
		return start.distance(end);
	}

	public TimeArrow translate(UTranslate translate) {
		return new TimeArrow(translate.getTranslated(start), translate.getTranslated(end), label, skinParam,
				type);
	}

	public static XPoint2D onCircle(XPoint2D pt, double alpha, double radius) {
		final double x = pt.getX() - Math.sin(alpha) * radius;
		final double y = pt.getY() - Math.cos(alpha) * radius;
		return new XPoint2D(x, y);
	}

	private FontConfiguration getFontConfiguration() {
		return getStyle().getFontConfiguration(skinParam.getIHtmlColorSet());
	}

	public void drawU(UGraphic ug) {
		final double angle = getAngle();

		ug = ug.apply(type.getSpecificColor()).apply(type.getUStroke());
		final ULine line = new ULine(end.getX() - start.getX(), end.getY() - start.getY());
		ug.apply(UTranslate.point(start)).draw(line);

		final double delta = 20.0 * Math.PI / 180.0;
		final XPoint2D pt1 = onCircle(end, angle + delta,8);
		final XPoint2D pt2 = onCircle(end, angle - delta,8);

		final UPolygon polygon = new UPolygon();
		polygon.addPoint(pt1.getX(), pt1.getY());
		polygon.addPoint(pt2.getX(), pt2.getY());
		polygon.addPoint(end.getX(), end.getY());

		ug = ug.apply(type.getSpecificColor().bg());
		ug.draw(polygon);

		final TextBlock textLabel = getTextBlock(label);
		final XDimension2D dimLabel = textLabel.calculateDimension(ug.getStringBounder());
		double yOffset = start.distance(end);// + dimLabel.getHeight();
		double yOffset2 = 0.0;
		UFont f = getFontConfiguration().getFont();
		double descent = ug.getStringBounder().getDescent(f,label.toString());
		if (start.getY() < end.getY()) {
			yOffset2 = -dimLabel.getHeight(); //+ descent;
		} else {
			//yOffset2 = -descent;//;
		}


		double xOffset = angle == 0 || angle - Math.PI < 0.01 ? 2.0 : 0.0;

		final XPoint2D textAngledPoint = onCircle(start, angle+Math.PI,yOffset/2);

		textLabel.drawU(ug.apply(new UTranslate(textAngledPoint.getX() + xOffset,textAngledPoint.getY() + yOffset2)));

	}

	private TextBlock getTextBlock(Display display) {
		return display.create(getFontConfiguration(), HorizontalAlignment.LEFT, skinParam);
	}

	private Style getStyle() {
		return getStyleSignature().getMergedStyle(styleBuilder);
	}

	private StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram,SName.arrow);
	}
}
