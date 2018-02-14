package fr.kazejiyu.discord.rpc.integration;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.github.psnrigner.discordrpcjava.DiscordEventHandler;
import com.github.psnrigner.discordrpcjava.DiscordJoinRequest;
import com.github.psnrigner.discordrpcjava.DiscordRichPresence;
import com.github.psnrigner.discordrpcjava.DiscordRpc;
import com.github.psnrigner.discordrpcjava.ErrorCode;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.kazejiyu.discord.rpc.integration"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private DiscordRpc discordRpc = new DiscordRpc();
	
	private static final String applicationId = "413038514616139786";
	
	/**
	 * The constructor
	 */
	public Activator() {
        DiscordEventHandler discordEventHandler = new DiscordEventHandler()
        {
            @Override
            public void ready()
            {
                System.err.println("READY");
            }

            @Override
            public void disconnected(ErrorCode errorCode, String message)
            {
                System.err.println("DISCONNECTED : " + errorCode + " " + message);
            }

            @Override
            public void errored(ErrorCode errorCode, String message)
            {
                System.err.println("ERRORED : " + errorCode + " " + message);
            }

            @Override
            public void joinGame(String joinSecret)
            {
                System.err.println("JOIN GAME : " + joinSecret);
            }

            @Override
            public void spectateGame(String spectateSecret)
            {
                System.err.println("SPECTATE GAME : " + spectateSecret);
            }

            @Override
            public void joinRequest(DiscordJoinRequest joinRequest)
            {
                System.err.println("JOIN REQUEST : " + joinRequest);
            }
        };
        
		discordRpc.init(applicationId, discordEventHandler, true);
		DiscordRichPresence presence = new DiscordRichPresence();
		
		presence.setState("Eclipse IDE");
		presence.setDetails("Developing | Java");
		
		discordRpc.updatePresence(presence);
		discordRpc.runCallbacks();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		discordRpc.shutdown();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	@Override
	public void earlyStartup() {
		// TODO Auto-generated method stub
		
	}

}
