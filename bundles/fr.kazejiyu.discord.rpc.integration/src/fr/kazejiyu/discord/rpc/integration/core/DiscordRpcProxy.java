package fr.kazejiyu.discord.rpc.integration.core;

import java.util.Timer;
import java.util.TimerTask;

import com.github.psnrigner.discordrpcjava.DiscordEventHandler;
import com.github.psnrigner.discordrpcjava.DiscordJoinRequest;
import com.github.psnrigner.discordrpcjava.DiscordRichPresence;
import com.github.psnrigner.discordrpcjava.DiscordRpc;
import com.github.psnrigner.discordrpcjava.ErrorCode;

import fr.kazejiyu.discord.rpc.integration.extensions.RichPresence;

/**
 * A quick interface around Discord RPC's API allowing to change the API used easily.
 * 
 * @author Emmanuel CHEBBI
 */
public class DiscordRpcProxy {
	
	/** Identifies the Eclipse Integration Discord app */
	private static final String APPLICATION_ID = "413038514616139786";
	
	/** The delay before shutting down the connection with Discord, in seconds */
	private static final long TIMEOUT_BEFORE_SHUTTING_DOWN = 5000;
	
	/** Helps to close the connection to Discord after a certain delay */
	private final Timer timer = new Timer("Shutdown Discord RPC connection");
	
	private RichPresence lastPresence = null;
	
	/**
	 * Initializes the connection to Discord session.
	 */
	public void initialize() {

	}
	
	/**
	 * Shows given presence on Discord.
	 * 
	 * @param rp
	 * 			Contains the elements to show on Discord.
	 * 			Must not be {@code null}.
	 */
	public void show(RichPresence rp) {
		lastPresence = rp.clone();
		
		// NOTE Currently, API is broken and connection must be re-created at each modification
		// see https://github.com/PSNRigner/discord-rpc-java/issues/13
		
		DiscordRpc rpc = new DiscordRpc();
		rpc.init(APPLICATION_ID, createDiscordEventHandler(), true);
		
		DiscordRichPresence presence = new DiscordRichPresence();
		
		rp.getState().ifPresent(presence::setState);
		rp.getDetails().ifPresent(presence::setDetails);
		rp.getStartTimestamp().ifPresent(presence::setStartTimestamp);
		
		rpc.updatePresence(presence);
		
		rpc.runCallbacks();
		
		// Wait before closing the connection, so that Discord can be notified		
		timer.schedule(shutdown(rpc), TIMEOUT_BEFORE_SHUTTING_DOWN);
	}
	
	public void updateDetails(String details) {
		show(lastPresence.withDetails(details));
	}
	
	public void updateState(String state) {
		show(lastPresence.withState(state));
	}
	
	public void updateStartTimestamp(long start) {
		show(lastPresence.withStartTimestamp(start));
	}
	
	/**
	 * Returns a new {@link TimerTask} instance that calls {@code rpc.shutdown();}.
	 * 
	 * @param rpc
	 * 			The Discord connection to close. Must not be {@code null}.
	 * 
	 * @return the new {@code TimerTask} instance.
	 */
	private TimerTask shutdown(DiscordRpc rpc) {
		return new TimerTask() {
			
			@Override
			public void run() {
				rpc.shutdown();
			}
		};
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
