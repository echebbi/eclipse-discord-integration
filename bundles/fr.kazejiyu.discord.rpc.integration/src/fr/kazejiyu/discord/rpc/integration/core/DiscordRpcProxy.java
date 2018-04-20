package fr.kazejiyu.discord.rpc.integration.core;

import com.github.psnrigner.discordrpcjava.DiscordEventHandler;
import com.github.psnrigner.discordrpcjava.DiscordJoinRequest;
import com.github.psnrigner.discordrpcjava.DiscordRichPresence;
import com.github.psnrigner.discordrpcjava.DiscordRpc;
import com.github.psnrigner.discordrpcjava.ErrorCode;

/**
 * A quick interface around Discord RPC's API allowing to change the API used easily.
 * 
 * @author Emmanuel CHEBBI
 */
public class DiscordRpcProxy {
	
	private static final String applicationId = "413038514616139786";
	
	/**
	 * Initializes the connection to Discord session.
	 */
	public void initialize() {
		
	}

	/**
	 * Updates details within Discord's overlay.
	 * 
	 * @param details
	 * 			The new details. Must not be {@code null}.
	 */
	public void setDetails(String details) {
		// NOTE Currently, API is broken and connection must be re-created at each modification
		// see https://github.com/PSNRigner/discord-rpc-java/issues/13
		
		DiscordRpc rpc = new DiscordRpc();
		rpc.init(applicationId, createDiscordEventHandler(), true);
		
		DiscordRichPresence presence = new DiscordRichPresence();
		presence.setState("Developping");
		presence.setDetails("Current selection: " + details);
		
		rpc.updatePresence(presence);
		
		rpc.runCallbacks();
		
		// Wait so that Discord can be notified before shutting down the connection		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		rpc.shutdown();
	}
	
	/**
	 * Shutdowns the connection to Discord session.<br>
	 * <br>
	 * If this method is called while the connection has already being closed, it has no effect.
	 */
	public void shutdown() {
		
	}
	
	/** Creates an handler listening for Discord events. Not used at the moment. */
	private DiscordEventHandler createDiscordEventHandler() {
		return new DiscordEventHandler() {
			@Override
			public void ready() {
				System.err.println("READY");
			}

			@Override
			public void disconnected(ErrorCode errorCode, String message) {
				System.err.println("DISCONNECTED : " + errorCode + " " + message);
			}

			@Override
			public void errored(ErrorCode errorCode, String message) {
				System.err.println("ERRORED : " + errorCode + " " + message);
			}

			@Override
			public void joinGame(String joinSecret) {
				System.err.println("JOIN GAME : " + joinSecret);
			}

			@Override
			public void spectateGame(String spectateSecret) {
				System.err.println("SPECTATE GAME : " + spectateSecret);
			}

			@Override
			public void joinRequest(DiscordJoinRequest joinRequest) {
				System.err.println("JOIN REQUEST : " + joinRequest);
			}
		};
	}
}
