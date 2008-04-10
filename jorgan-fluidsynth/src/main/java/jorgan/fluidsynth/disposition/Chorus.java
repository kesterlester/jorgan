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
package jorgan.fluidsynth.disposition;

public class Chorus {
	private int nr;

	private double level;

	private double speed;

	private double depthMs;

	private int type;

	public Chorus(int nr, double level, double speed, double depthMs, int type) {
		super();
		this.nr = nr;
		this.level = level;
		this.speed = speed;
		this.depthMs = depthMs;
		this.type = type;
	}

	public double getDepthMs() {
		return depthMs;
	}

	public double getLevel() {
		return level;
	}

	public int getNr() {
		return nr;
	}

	public double getSpeed() {
		return speed;
	}

	public int getType() {
		return type;
	}
}