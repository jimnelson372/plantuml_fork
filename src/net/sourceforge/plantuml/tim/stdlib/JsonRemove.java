/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2021, Arnaud Roques
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
 * Contribution: The-Lum
 *
 */
package net.sourceforge.plantuml.tim.stdlib;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.text.StringLocated;
import net.sourceforge.plantuml.tim.EaterException;
import net.sourceforge.plantuml.tim.TContext;
import net.sourceforge.plantuml.tim.TFunctionSignature;
import net.sourceforge.plantuml.tim.TMemory;
import net.sourceforge.plantuml.tim.expression.TValue;

public class JsonRemove extends SimpleReturnFunction {

	public TFunctionSignature getSignature() {
		return new TFunctionSignature("%json_remove", 2);
	}

	@Override
	public boolean canCover(int nbArg, Set<String> namedArgument) {
		return nbArg == 2;
	}

	@Override
	public TValue executeReturnFunction(TContext context, TMemory memory, StringLocated location, List<TValue> values,
			Map<String, TValue> named) throws EaterException {
		final TValue data = values.get(0);
		if (data.isJson() == false)
			throw new EaterException("Not JSON data", location);
		
		final JsonValue json = data.toJson().cloneMe();
		
		if (!json.isArray() && !json.isObject())
			return data;
		if (json.isArray()) {
			if (values.get(1).isNumber()) {
				final Integer index = values.get(1).toInt();
				if (0 <= index && index < json.asArray().size())
					json.asArray().remove(index);
			}
			return TValue.fromJson(json);
		}
		if (json.isObject()) {
			final String name = values.get(1).toString();
			json.asObject().remove(name);
			return TValue.fromJson(json);
		}
		throw new EaterException("Bad JSON type", location);
	}
}
