package fr.kazejiyu.discord.rpc.integration.extensions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link EditorInputRichPresence} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("An EditorInputRichPresence")
public class EditorInputRichPresenceTest implements WithAssertions {
    
    @Mock
    private EditorInputRichPresence iut;
    
    @Mock
    private EditorInputRichPresence compared;
    
    @BeforeEach
    private void setupMockToCallRealMethod() {
        when(iut.compareTo(any(EditorInputRichPresence.class))).thenCallRealMethod();
    }
    
    @Test @DisplayName("returns a negative integer when compared to another object with a greater priority")
    void returns_a_negative_integer_when_compared_to_another_object_with_a_greater_priority() {
        when(iut.getPriority()).thenReturn(5);
        when(compared.getPriority()).thenReturn(12);
        
        assertThat(iut.compareTo(compared)).isLessThan(0);
    }
    
    @Test @DisplayName("returns a positive integer when compared to another object with a lower priority")
    void returns_a_positive_integer_when_compared_to_another_object_with_a_lower_priority() {
        when(iut.getPriority()).thenReturn(-5);
        when(compared.getPriority()).thenReturn(-12);
        
        assertThat(iut.compareTo(compared)).isGreaterThan(0);
    }
    
    @Test @DisplayName("returns zero when compared to another object with an equal priority")
    void returns_zero_when_compared_to_another_object_with_an_equal_priority() {
        when(iut.getPriority()).thenReturn(5);
        when(compared.getPriority()).thenReturn(5);
        
        assertThat(iut.compareTo(compared)).isZero();
    }

}
