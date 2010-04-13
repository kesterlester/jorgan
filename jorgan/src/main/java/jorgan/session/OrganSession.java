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
package jorgan.session;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.disposition.Organ;
import jorgan.disposition.Element.FastPropertyChange;
import jorgan.disposition.event.Change;
import jorgan.disposition.event.OrganObserver;
import jorgan.disposition.spi.ElementRegistry;
import jorgan.io.DispositionStream;
import jorgan.session.spi.SessionRegistry;
import jorgan.util.ShutdownHook;
import bias.Configuration;

/**
 * A session of interaction with an {@link Organ}.
 */
public class OrganSession {

	private static Logger logger = Logger.getLogger(OrganSession.class
			.getName());

	private static Configuration config = Configuration.getRoot().get(
			OrganSession.class);

	/**
	 * The file the current organ is associated with.
	 */
	private File file;

	private Organ organ;

	private List<SessionListener> listeners = new ArrayList<SessionListener>();

	private boolean modified = false;

	private boolean constructing = false;

	private boolean sealed = false;

	private Map<Class<? extends Object>, Object> ts = new HashMap<Class<? extends Object>, Object>();

	private ShutdownHook shutdownHook;

	public OrganSession(File file) throws IOException {
		this.file = file;

		if (file.exists()) {
			organ = new DispositionStream().read(file);
		} else {
			organ = createOrgan();
		}

		organ.addOrganObserver(new OrganObserver() {
			public void onChange(Change change) {
				if (change instanceof FastPropertyChange) {
					if (((FastPropertyChange) change).isDerived()) {
						// don't mark modified for derived changes
						return;
					}
				}

				markModified();
			}
		});

		SessionRegistry.init(this);

		config.read(this);
	}

	public void setSealed(boolean sealed) {
		this.sealed = sealed;
	}

	public boolean isSealed() {
		return sealed;
	}

	public void setSaveOnShutdown(boolean save) {
		if (save) {
			if (shutdownHook == null) {
				shutdownHook = new ShutdownHook(new Runnable() {
					public void run() {
						if (modified) {
							logger.log(Level.INFO, "save on shutdown");

							try {
								save();
							} catch (IOException ex) {
								logger.log(Level.WARNING,
										"unable to save on shutdown", ex);
							}
						}
					}
				});
			}
		} else {
			if (shutdownHook != null) {
				shutdownHook.release();
				shutdownHook = null;
			}
		}
	}

	public boolean isModified() {
		return modified;
	}

	public void markModified() {
		if (!modified) {
			modified = true;

			for (SessionListener listener : listeners) {
				listener.modified();
			}
		}
	}

	public void save() throws IOException {
		new DispositionStream().write(organ, file);

		modified = false;

		for (SessionListener listener : listeners) {
			listener.saved(file);
		}
	}

	public File getFile() {
		return file;
	}

	public boolean isConstructing() {
		return constructing;
	}

	public void setConstructing(boolean constructing) {
		if (sealed && constructing) {
			return;
		}

		if (constructing != this.constructing) {
			this.constructing = constructing;

			for (SessionListener listener : listeners) {
				listener.constructingChanged(constructing);
			}
		}
	}

	public void addListener(SessionListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SessionListener listener) {
		listeners.remove(listener);
	}

	public Organ getOrgan() {
		return organ;
	}

	@SuppressWarnings("unchecked")
	public <T> T lookup(Class<T> clazz) {
		T t = (T) ts.get(clazz);
		if (t == null) {
			t = (T) SessionRegistry.create(this, clazz);
			if (t == null) {
				throw new IllegalArgumentException();
			} else {
				ts.put(clazz, t);
			}
		}
		return t;
	}

	public String deresolve(File file) {
		if (file.isAbsolute()) {
			String directory = this.file.getParentFile().getAbsolutePath()
					.replace('\\', '/');
			if (!directory.endsWith("/")) {
				directory += "/";
			}

			String path = file.getPath().replace('\\', '/');
			if (path.startsWith(directory)) {
				return path.substring(directory.length());
			}
		}

		return file.getPath();
	}

	public File resolve(String name) {
		File file = new File(name);

		if (!file.isAbsolute()) {
			file = new File(getFile().getParentFile(), name);
		}

		return file;
	}

	private Organ createOrgan() {
		Organ organ = new Organ();

		ElementRegistry.init(organ);

		return organ;
	}

	public void destroy() {
		for (SessionListener listener : listeners) {
			listener.destroyed();
		}

		if (shutdownHook != null) {
			shutdownHook.release();
		}
	}
}