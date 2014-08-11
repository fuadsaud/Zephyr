package zephyr.ui.components;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * This class works just like {@link JScrollPane}, with the difference that this
 * class keeps a field, with a reference to the component added to the viewport.
 * 
 * @see JScrollPane
 * 
 * @author Fuad Saud
 * 
 * @param <T>
 *            the type to be added on the scroll pane.
 */
public class GenericScrollPane<T extends Component> extends JScrollPane {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = -1435005419107329120L;

	private T component;

	/**
	 * This method is equivalent to a call of
	 * "JScrollPane.getViewport().add(Component)". The only difference is that
	 * this method keeps a reference to the encapsulated component as a field.
	 * 
	 * @see JViewport#add(Component)
	 * 
	 * @param component
	 *            The component to be added on the viewport.
	 */
	public void addOnViewPort(T component) {
		super.getViewport().add(component);
		this.component = component;
	}

	/**
	 * This methods returns the component inside de scroll pane.
	 * 
	 * @return the component on the viewport.
	 */
	public T getComponent() {
		return component;
	}

}
