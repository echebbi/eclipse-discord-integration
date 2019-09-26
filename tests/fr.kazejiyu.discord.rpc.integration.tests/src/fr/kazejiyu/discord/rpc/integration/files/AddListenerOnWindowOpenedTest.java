package fr.kazejiyu.discord.rpc.integration.files;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.assertj.core.api.WithAssertions;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link AddListenerOnWindowOpened} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("An AddListenerOnWindowOpened")
public class AddListenerOnWindowOpenedTest implements WithAssertions {

    /** Software Under Test. */
    private AddListenerOnWindowOpened<?> sut;

    @Mock
    private SelectionListenerAndPartListener2 listener;
    
    @Mock 
    private IWorkbenchWindow window;
    
    @BeforeEach
    void instanciateClassUnderTest() {
        sut = new AddListenerOnWindowOpened<>(listener);
    }
    
    @Test @DisplayName("does not throw when a null window opens")
    void does_not_throw_when_a_null_window_opens() {
        sut.windowOpened(null);
    }
    
    @Test @DisplayName("does not throw when a null window closes")
    void does_not_throw_when_a_null_window_closes() {
        sut.windowClosed(null);
    }
    
    @Test @DisplayName("adds listener as a new selection listener of each window that opens")
    void adds_listener_as_a_new_selection_listener_of_each_window_that_opens() {
        // Given: A window w/ a selection service & no pages
        ISelectionService service = mock(ISelectionService.class);
        when(window.getSelectionService()).thenReturn(service);
        when(window.getService(ISelectionService.class)).thenReturn(service);
        
        when(window.getPages()).thenReturn(new IWorkbenchPage[] {});
        
        // When: the window opens
        sut.windowOpened(window);
        
        // Then: the listener has been registered
        verify(service, atLeastOnce()).addSelectionListener(listener);
    }
    
    @Test @DisplayName("adds listener as a new part listener of all pages of each window that opens")
    void adds_listener_as_a_new_part_listener_of_all_pages_of_each_window_that_opens() {
        // Given: A window w/ a selection service & some pages
        ISelectionService service = mock(ISelectionService.class);
        when(window.getSelectionService()).thenReturn(service);
        when(window.getService(ISelectionService.class)).thenReturn(service);
    
        IWorkbenchPage[] pages = new IWorkbenchPage[] { mock(IWorkbenchPage.class), mock(IWorkbenchPage.class)};
        when(window.getPages()).thenReturn(pages);
        
        // When: the window opens
        sut.windowOpened(window);
        
        // Then: the listener has been registered
        for (IWorkbenchPage page : pages) {
            verify(page, atLeastOnce()).addPartListener(listener);
        }
    }
    
    @Test @DisplayName("removes listener from the selection listeners of each window that closes")
    void removes_listener_from_the__selection_listeners_of_each_window_that_closes() {
        // Given: A window w/ a selection service & no pages
        ISelectionService service = mock(ISelectionService.class);
        when(window.getSelectionService()).thenReturn(service);
        when(window.getService(ISelectionService.class)).thenReturn(service);
        
        when(window.getPages()).thenReturn(new IWorkbenchPage[] {});
        
        // When: the window opens
        sut.windowClosed(window);
        
        // Then: the listener has been registered
        verify(service, atLeastOnce()).removeSelectionListener(listener);
    }
    
    @Test @DisplayName("removes listener from the part listeners of all pages of each window that closes")
    void removes_listener_from_the_part_listeners_of_all_pages_of_each_window_that_closes() {
        // Given: A window w/ a selection service & some pages
        ISelectionService service = mock(ISelectionService.class);
        when(window.getSelectionService()).thenReturn(service);
        when(window.getService(ISelectionService.class)).thenReturn(service);
    
        IWorkbenchPage[] pages = new IWorkbenchPage[] { mock(IWorkbenchPage.class), mock(IWorkbenchPage.class)};
        when(window.getPages()).thenReturn(pages);
        
        // When: the window opens
        sut.windowClosed(window);
        
        // Then: the listener has been registered
        for (IWorkbenchPage page : pages) {
            verify(page, atLeastOnce()).removePartListener(listener);
        }
    }
    
    @Test @DisplayName("does nothing on window activated")
    void does_nothing_on_window_activated() {
        sut.windowActivated(window);
        verifyZeroInteractions(window);
    }
    
    @Test @DisplayName("does nothing on window deactivated")
    void does_nothing_on_window_deactivated() {
        sut.windowDeactivated(window);
        verifyZeroInteractions(window);
    }
    
    /**
     * Makes easier to mock the argument of AddListenerOnWindowOpened's constructor.
     */
    public abstract static class SelectionListenerAndPartListener2 implements ISelectionListener, IPartListener2 {}
    
}
