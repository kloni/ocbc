package test;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import javax.security.auth.callback.Callback;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class CopyOfTest {

	/**
	 * @param args
	 * @throws UnknownHostException 
	 * @throws MalformedURLException 
	 * @throws ServiceException 
	 */
	public static void main(String[] args) throws UnknownHostException, MalformedURLException, ServiceException {
		PropertiesLoader.loadPropertyFile();
		
		Chord chord = new ChordImpl();
		chord.setCallback(new NotifyCallback() {
			
			@Override
			public void retrieved(ID target) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void broadcast(ID source, ID target, Boolean hit) {
				// TODO Auto-generated method stub
				
			}
		});
		
		String host = "127.0.0.1";
		int port = 6000;
		URL localurl = new URL(URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL) + "://" + host + ":" + port + "/");
		URL firsturl = new URL(URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL) + "://192.168.178.34:6000/");
		
		chord.join(localurl, firsturl);
		//chord.create(localurl);

	}

}
