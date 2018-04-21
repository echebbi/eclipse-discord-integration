package fr.kazejiyu.discord.rpc.integration.listener;

import static java.util.Objects.requireNonNull;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Adds a given {@link ISelectionListener} to each window that opens.
 * 
 * @author Emmanuel CHEBBI
 */
public class AddListenerOnWindowOpened<T extends ISelectionListener & IPartListener2> implements IWindowListener {
	
	/** The listener to add to each window that opens */
	private final T listener;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param listener
	 * 			The listener that should be added to each window that opens.
	 * 			Must not be {@code null}.
	 */
	public AddListenerOnWindowOpened(T listener) {
		this.listener = requireNonNull(listener, "The listener must not be null");
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		addSelectionListener(window);
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		removeSelectionListener(window);
	}

	/** Adds {@code listener} as an ISelectionListener of {@code window} */
    private void addSelectionListener(IWorkbenchWindow window) {
        if (window != null) {
            window.getSelectionService().addSelectionListener(listener);
            
            for (IWorkbenchPage page : window.getPages()) {
				page.addPartListener(listener);
			}
        }
    }

    /** Removes {@code listener} from {@code window}'s ISelectionListeners */
    private void removeSelectionListener(IWorkbenchWindow window) {
        if (window != null) {
            window.getSelectionService().removeSelectionListener(listener);
            
            for (IWorkbenchPage page : window.getPages()) {
				page.removePartListener(listener);
			}
        }
    }
    
    @Override
    public void windowActivated(IWorkbenchWindow window) {}
    
    @Override
    public void windowDeactivated(IWorkbenchWindow window) {}
}
