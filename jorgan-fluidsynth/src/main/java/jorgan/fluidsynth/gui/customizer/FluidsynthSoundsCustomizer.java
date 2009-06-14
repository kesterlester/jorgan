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
package jorgan.fluidsynth.gui.customizer;

import javax.swing.JComponent;

import jorgan.customizer.gui.Customizer;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.session.OrganSession;
import bias.Configuration;

/**
 * customizer of {@link FluidsynthSound}s.
 */
public class FluidsynthSoundsCustomizer implements Customizer {

	private static Configuration config = Configuration.getRoot().get(
			FluidsynthSoundsCustomizer.class);

	private String description;

	private FluidsynthSoundsPanel panel;

	public FluidsynthSoundsCustomizer(OrganSession session) {
		config.read(this);

		panel = new FluidsynthSoundsPanel(session.getOrgan().getElements(
				FluidsynthSound.class));
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JComponent getComponent() {
		return panel;
	}

	public void apply() {
		panel.apply();
	}

	public static boolean customizes(OrganSession session) {
		return session.getOrgan().getElements(FluidsynthSound.class).size() > 0;
	}
}