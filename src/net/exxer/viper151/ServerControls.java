package net.exxer.viper151;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class ServerControls {
	private DiscordApi api;
	private Process server;
	private boolean isOnline = false;
	private Bot bot;
	
	private final String STARTSCRIPT = "./start_server.sh"; 
	
	public ServerControls(DiscordApi api, Bot bot) {
		this.api = api;
		this.bot = bot;
	}
	
	public void restartServer() {
		if(!isOnline) {
			startServer();
			return;
		}
		
		bot.getCommandChannel().sendMessage(new EmbedBuilder()
				.setDescription("The server will restart shortly.")
				.setColor(Color.YELLOW)
				.setTitle("Restarting Server...")
			);
		
		api.updateActivity("Restarting Server");
		
		runCommand("stop");
		
		isOnline = false;
		
		try {
			server = Runtime.getRuntime().exec(STARTSCRIPT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		isOnline = true;
		
		try {
			TimeUnit.MINUTES.sleep(2);
			if(bot.isOnline()) {
				bot.getCommandChannel().sendMessage(new EmbedBuilder()
						.setDescription("The server is now online.")
						.setColor(Color.GREEN)
						.setTitle("Server Online")
					);
			}else {
				bot.getCommandChannel().sendMessage(new EmbedBuilder()
						.setDescription("Unable to determine automatically if the server is online.")
						.setColor(Color.YELLOW)
						.setTitle("Server Online")
					);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		api.updateActivity("Server Online");
	}
	
	public void startServer() {
		if(!isOnline) {
			bot.getCommandChannel().sendMessage(new EmbedBuilder()
					.setDescription("The server will start shortly.")
					.setColor(Color.YELLOW)
					.setTitle("Starting Server...")
				);
			
			api.updateActivity("Starting Server");

			try {
				server = Runtime.getRuntime().exec(STARTSCRIPT);
				isOnline = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				TimeUnit.MINUTES.sleep(2);
				if(bot.isOnline()) {
					bot.getCommandChannel().sendMessage(new EmbedBuilder()
							.setDescription("The server is now online.")
							.setColor(Color.GREEN)
							.setTitle("Server Online")
						);
				}else {
					bot.getCommandChannel().sendMessage(new EmbedBuilder()
							.setDescription("Unable to determine automatically if the server is online.")
							.setColor(Color.YELLOW)
							.setTitle("Server Online")
						);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}else {
			
			bot.getCommandChannel().sendMessage(new EmbedBuilder()
					.setDescription("The server is allready online. Do you mean //Restart ?")
					.setColor(Color.GREEN)
					.setTitle("Server Online")
				);
			
		}
		
		api.updateActivity("Server Online");
	}
	
	public void stopServer() {
		if(isOnline) {
			bot.getCommandChannel().sendMessage(new EmbedBuilder()
					.setDescription("The server will shutdown shortly.")
					.setColor(Color.YELLOW)
					.setTitle("Stopping Server...")
				);
			
			api.updateActivity("Stopping Server");

			runCommand("stop");
			
			isOnline = false;
			
			try {
				TimeUnit.MINUTES.sleep(2);
				if(!bot.isOnline()) {
					bot.getCommandChannel().sendMessage(new EmbedBuilder()
							.setDescription("The server is now offline.")
							.setColor(Color.RED)
							.setTitle("Server Closed")
						);
				}else {
					bot.getCommandChannel().sendMessage(new EmbedBuilder()
							.setDescription("Unable to determine automatically if the server is offline.")
							.setColor(Color.YELLOW)
							.setTitle("Server Offline")
						);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}else {
			bot.getCommandChannel().sendMessage(new EmbedBuilder()
					.setDescription("The server is allready offline. Do you mean //Start ?")
					.setColor(Color.RED)
					.setTitle("Server Offline")
				);
		}
		
		api.updateActivity("Server Offline");
	}
	
	public void startServerInit() {
		
		api.updateActivity("Starting Server");

		try {
			server = Runtime.getRuntime().exec(STARTSCRIPT);
			isOnline = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		api.updateActivity("Server Online");
	}
	
	private void runCommand(String command) {
		try(BufferedWriter streamInput = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()))) {
	        streamInput.write(command);
	        streamInput.newLine();
	     }catch (IOException e) {
	    	 e.printStackTrace();
	     }
	}
	
}
