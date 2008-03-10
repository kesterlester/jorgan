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
package jorgan.gui.dock;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganEvent;
import jorgan.gui.construct.editor.ElementAwareEditor;
import jorgan.gui.construct.info.spi.ProviderRegistry;
import jorgan.session.OrganSession;
import jorgan.session.event.ElementSelectionEvent;
import jorgan.session.event.ElementSelectionListener;
import jorgan.swing.beans.DefaultBeanCustomizer;
import jorgan.swing.beans.PropertiesPanel;
import bias.Configuration;

/**
 * Dockable shows the properties of elements.
 */
public class PropertiesDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			PropertiesDockable.class);

	/**
	 * The handler of selection changes.
	 */
	private SelectionHandler selectionHandler = new SelectionHandler();

	private OrganSession session;

	private PropertiesPanel propertiesPanel = new PropertiesPanel();

	public PropertiesDockable() {
		config.read(this);

		setContent(propertiesPanel);

		ElementCustomizer customizer = new ElementCustomizer();
		propertiesPanel.setBeanCustomizer(customizer);
		propertiesPanel.addChangeListener(selectionHandler);
	}

	@Override
	public boolean forPlay() {
		return false;
	}
	
	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.removeSelectionListener(selectionHandler);
			this.session.removeOrganListener(selectionHandler);

			selectionHandler.clearProperties();
		}

		this.session = session;

		if (this.session != null) {
			this.session.addSelectionListener(selectionHandler);
			this.session.addOrganListener(selectionHandler);

			selectionHandler.updateProperties();
		}
	}

	/**
	 * The handler of selections.
	 */
	private class SelectionHandler extends OrganAdapter implements
			ElementSelectionListener, ChangeListener {

		private boolean changing = false;

		public void selectionChanged(ElementSelectionEvent ev) {
			updateProperties();
		}

		public void stateChanged(ChangeEvent e) {
			if (!changing) {
				changing = true;

				String property = propertiesPanel.getProperty();
				session.getElementSelection().setLocation(property);

				changing = false;
			}
		}

		@Override
		public void changed(OrganEvent event) {
			if (event.self()) {
				if (propertiesPanel.getBeans().contains(event.getElement())) {
					updateProperties();
				}
			}
		}

		private void updateProperties() {
			if (!changing) {
				changing = true;

				propertiesPanel.setBeans(session.getElementSelection()
						.getSelectedElements());

				Object location = session.getElementSelection().getLocation();
				if (location instanceof String) {
					propertiesPanel.setProperty((String) location);
				} else {
					propertiesPanel.setProperty(null);
				}

				changing = false;
			}
		}

		private void clearProperties() {
			if (!changing) {
				changing = true;

				propertiesPanel.setBean(null);

				changing = false;
			}
		}
	}

	private class ElementCustomizer extends DefaultBeanCustomizer {

		@Override
		public BeanInfo getBeanInfo(Class<?> beanClass)
				throws IntrospectionException {
			Introspector.setBeanInfoSearchPath(ProviderRegistry
					.getBeanInfoSearchPath());

			return super.getBeanInfo(beanClass);
		}

		@Override
		public PropertyEditor getPropertyEditor(PropertyDescriptor descriptor)
				throws IntrospectionException {
			PropertyEditor editor = super.getPropertyEditor(descriptor);

			if (editor != null && editor instanceof ElementAwareEditor) {
				((ElementAwareEditor) editor)
						.setElement((Element) propertiesPanel.getBeans().get(0));
			}

			return editor;
		}
	}
}