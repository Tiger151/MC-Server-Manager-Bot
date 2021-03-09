package net.exxer.viper151;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;

public class Bot {
	
	public static void main(final String args[]) {
		new Bot();
	}
	
	private final long CHANNEL = 0L;      //Command Channel ID
	private final String TOKEN = "";	  //Bot Token
	
	private TextChannel commandChannel;
	
	public Bot() {
		DiscordApi api = new DiscordApiBuilder().setToken(TOKEN).login().join();
		api.updateActivity("Intializing...");
		ServerControls control = new ServerControls(api, this);
		commandChannel = api.getTextChannelById(CHANNEL).get();
		api.setMessageCacheSize(0, 0);
		
		api.addMessageCreateListener(event -> {
			if(commandChannel.equals(event.getChannel())) {
				String messageRaw = event.getMessageContent();
				if(messageRaw.length() >= 2) {
					if(messageRaw.substring(0, 2).equals("//")) {
						String message = messageRaw.substring(2);
						
						switch(message.toLowerCase()) {
						case "ip":
							
							try {
								String ip = new BufferedReader(new InputStreamReader(new URL("https://api.ipify.org/").openStream())).readLine();
								commandChannel.sendMessage(new EmbedBuilder()
										.setDescription("Current Server IP:\n\n" + ip)
										.setColor(Color.BLUE)
										.setTitle("Server IP")
									);
							} catch (MalformedURLException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							break;
						case "start":
							
							control.startServer();
							
							break;
						case "stop":	
							
							control.stopServer();
							
							break;
						case "restart":
							
							control.restartServer();
							
							break;
						default:
							commandChannel.sendMessage(new EmbedBuilder()
								.setDescription("//IP : Get the current IP of the server\n//Start : Start the server\n//Stop : Shutdown the server (No Warning)\n//Restart : Manually restart the server (No Warning)")
								.setColor(Color.BLUE)
								.setTitle("Commands")
							);
						}
					}else {
						event.getMessage().delete();
					}
				}
				if(messageRaw.length() == 1) { //catch 1 char messages
					event.getMessage().delete();
				}
			}
		});
		api.getTextChannelById(515020547696885784L).get().sendMessage(new EmbedBuilder()
				.setDescription("The server will start shortly.")
				.setColor(Color.YELLOW)
				.setTitle("Starting Server...")
			);
		
		control.startServerInit();
		
		try {
			TimeUnit.MINUTES.sleep(1);
			if(isOnline()) {
				commandChannel.sendMessage(new EmbedBuilder()
						.setDescription("The server is now online.")
						.setColor(Color.GREEN)
						.setTitle("Server Online")
					);
			}else {
				commandChannel.sendMessage(new EmbedBuilder()
						.setDescription("Unable to determine automatically if the server is online.")
						.setColor(Color.YELLOW)
						.setTitle("Server Online")
					);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		
	}
	
	public boolean isOnline() {
		try {
			String ip = new BufferedReader(new InputStreamReader(new URL("https://api.ipify.org/").openStream())).readLine();
			JSONObject obj = new JSONObject(new BufferedReader(new InputStreamReader(new URL("https://api.mcsrvstat.us/2/" + ip).openStream())).readLine());
			return obj.getBoolean("online");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public TextChannel getCommandChannel() {
		return commandChannel;
	}
}
