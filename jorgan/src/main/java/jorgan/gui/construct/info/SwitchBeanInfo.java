/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.gui.construct.info;

import jorgan.disposition.Switch;
import jorgan.gui.construct.editor.BooleanEditor;

/**
 * BeanInfo for {@link jorgan.disposition.Switch}.
 */
public class SwitchBeanInfo extends MomentaryBeanInfo {

	@Override
	protected void registerProperties() {
		super.registerProperties();

		add("active", Switch.class, BooleanEditor.class);
		add("locking", Switch.class, BooleanEditor.class);
	}
}