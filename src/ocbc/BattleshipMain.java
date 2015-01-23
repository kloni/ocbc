package ocbc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import ocbc.strategy.RandomStrategy;
import ocbc.strategy.Strategy;
import ocbc.strategy.WinStrategy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;

public class BattleshipMain {

	private static final String OPT_I = "I";
	private static final String OPT_S = "S";
	private static final String OPT_BOOTSTRAP_URL = "bootstrapUrl";
	private static final String OPT_LOCAL_URL = "localUrl";
	private static final String OPT_STRATEGY = "strategy";
	private static final String OPT_WITH_CLI = "cli";

	public static void main(String[] args) throws ParseException, MalformedURLException, ServiceException, NoSuchAlgorithmException {
		PropertiesLoader.loadPropertyFile();
		
		Strategy.register("random", new RandomStrategy());
		Strategy.register("win",    new WinStrategy());
		
		CommandLineParser parser = new PosixParser();
		
		Options options = getOptions();
		CommandLine cmd = null;
		try {
			cmd = parser.parse( options, args);
			
		} catch( ParseException exp ) {
		   System.out.println(exp.getMessage());
		   HelpFormatter formatter = new HelpFormatter();
		   formatter.printHelp( "java ocbc.BattleshipMain", options );
		   return;
		}
		
		String localUrl = cmd.getOptionValue(OPT_LOCAL_URL);
		String bootstrapUrl = cmd.getOptionValue(OPT_BOOTSTRAP_URL);
		String strategy = cmd.hasOption(OPT_STRATEGY) ? cmd.getOptionValue(OPT_STRATEGY) : "random";
		Integer i = cmd.hasOption(OPT_I) ? Integer.valueOf(cmd.getOptionValue(OPT_I)) : 100;
		Integer s = cmd.hasOption(OPT_S) ? Integer.valueOf(cmd.getOptionValue(OPT_S)) : 10;
		
		BattleshipClient bc = new BattleshipClient(localUrl, bootstrapUrl, i, s, strategy);

		if(cmd.hasOption(OPT_WITH_CLI)) {
			handleInput(bc);
		}
		
	}
	
	public static void handleInput(BattleshipClient c) throws NoSuchAlgorithmException {
		int key = 0;
		while(key!='q') {
			try {
				key = System.in.read();
				switch (key) {
					case 'f':
						System.out.println("Finger table:");
						for(Node node : c.chord.getFixedFingerTable()) {
							System.out.println("  " + node);
						}
						break;
						
					case 'r':
						if(c.ships==null) c.init();
						ID rndid = IDUtil.rnd();
						System.out.println("Retrieving " + rndid + " ...");
						c.chord.retrieve(c.strategy.next());
						break;
						
					case 'x':
						ID maxid = IDUtil.max();
						System.out.println("Retrieving " + maxid + " ...");
						c.chord.retrieve(maxid);
						break;
						
					case 'n':
						ID minid = IDUtil.min();
						System.out.println("Retrieving " + minid + " ...");
						c.chord.retrieve(minid);
						break;
						
					case 'p':
						System.out.println("Predecessor: " + c.chord.getPredecessorID());
						break;
						
					case 'i':
						c.init();
						break;
	
					default:
						System.out.println("f: Display finger table");
						System.out.println("p: Display predecessor id");
						System.out.println("r: Retrieve next id by strategy");
						System.out.println("n: Retrieve lowest id (00 00 00 ...)");
						System.out.println("x: Retrieve highest id (FF FF FF ...)");
						System.out.println("i: Initialize client (place ships, init strategy)");
						System.out.println("q: Quit");
						break;
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public static Options getOptions() {
		Options options = new Options();
		
		Option localUrl = OptionBuilder.withArgName( "url" )
				.isRequired()
                .hasArg()
                .withDescription(  "local url (port 1 will assign a random port)" )
                .create( OPT_LOCAL_URL );
		
		Option bootstrapUrl = OptionBuilder.withArgName( "url" )
                .hasArg()
                .withDescription(  "bootstrap url" )
                .create( OPT_BOOTSTRAP_URL );

		Option I = OptionBuilder.withArgName( OPT_I )
                .hasArg()
                .withDescription(  "num of intervals" )
                .create( OPT_I );

		Option S = OptionBuilder.withArgName( "S" )
                .hasArg()
                .withDescription(  "num of ships" )
                .create( "S" );
		
		Option strategy = OptionBuilder.withArgName( "name" )
                .hasArg()
                .withDescription(  "targeting strategy implementation" )
                .create( OPT_STRATEGY );
		
		Option withCli = OptionBuilder
                .withDescription(  "with command line interface" )
                .create( OPT_WITH_CLI );

		options.addOption( localUrl );
		options.addOption( bootstrapUrl );
		options.addOption( I );
		options.addOption( S );
		options.addOption( strategy );
		options.addOption( withCli );
		
		return options;
	}

}
