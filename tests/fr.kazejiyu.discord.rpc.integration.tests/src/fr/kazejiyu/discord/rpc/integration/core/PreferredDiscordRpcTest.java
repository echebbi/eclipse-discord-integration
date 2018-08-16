package fr.kazejiyu.discord.rpc.integration.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("A PreferredDiscordRpc")
public class PreferredDiscordRpcTest implements WithAssertions {
	
	@Mock
	private GlobalPreferences preferences;
	
	@Mock
	private DiscordRpcLifecycle delegate;
	
	@Mock
	private RichPresence presence;
	
	private PreferredDiscordRpc discord;
	
	@BeforeEach
	void instanciate() {
		discord = new PreferredDiscordRpc(delegate, preferences);
	}

	@Nested
	@DisplayName("during instanciation")
	class DuringInstanciation {
		
		@Test @DisplayName("throws when DiscordRpcLifecycle is null")
		void throws_when_DiscordRpcLifecycle_is_null() {
			assertThatNullPointerException().isThrownBy(() ->
				new PreferredDiscordRpc(null, preferences)
			);
		}
		
		@Test @DisplayName("throws when UserPreferences are null")
		void throws_when_preferences_are_null() {
			assertThatNullPointerException().isThrownBy(() ->
				new PreferredDiscordRpc(delegate, null)
			);
		}
		
	}
	
	@Test @DisplayName("delegates the call to initialize")
	void delegates_the_call_to_initialize() {
		discord.initialize();
		
		verify(delegate, only()).initialize();
		verify(delegate, times(1)).initialize();
	}
	
	@Test @DisplayName("delegates the call to shutdown")
	void delegates_the_call_to_shutdown() {
		discord.shutdown();
		
		verify(delegate, only()).shutdown();
		verify(delegate, times(1)).shutdown();
	}
	
	@Test @DisplayName("delegates the call to showNothing")
	void delegates_the_call_to_showNothing() {
		discord.showNothing();
		
		verify(delegate, only()).showNothing();
		verify(delegate, times(1)).showNothing();
	}
	
	@Test @DisplayName("delegates the call to show")
	void delegates_the_call_to_show() {
		// Prevent NPE from being thrown
		when(preferences.getApplicablePreferencesFor(any())).thenReturn(preferences);
		
		discord.show(presence);
		
		verify(delegate, only()).show(any(RichPresence.class));
		verify(delegate, times(1)).show(any(RichPresence.class));
	}
	
	@ParameterizedTest(name="when isConnected={0}")
	@ValueSource(strings= {"true", "false"})
	void delegates_the_call_to_isConnected(boolean isConnected) {
		when(delegate.isConnected()).thenReturn(isConnected);
		assertThat(discord.isConnected()).isEqualTo(isConnected);
	}
	
}
