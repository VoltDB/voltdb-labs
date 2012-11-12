package client;

import java.util.Random;
import java.util.ArrayList;
import org.voltdb.client.ClientStats;
import org.voltdb.VoltTable;

public class GameBenchmark extends GenericBenchmark {

    private int gameCount = 0;
    private int[] gamePopularityMap = new int[100];
    private int playerCount = 500000;
    private int nextPlayerId = 0;
    private ArrayList<Session> sessions = new ArrayList<Session>();
    private int sessionsRun = 0;
    private NameGenerator gen = new NameGenerator();
    private Random rand = new Random();

    // nested class
    public class Session {
        int game_id;
        int player_id;
    }
    
    // constructor
    public GameBenchmark(BenchmarkConfig config) {
        super(config);
        
        // set any instance attributes here
    }

    // load any preliminary data into the database prior to the benchmark
    public void initialize() throws Exception {

        getGameCount();
        if (gameCount == 0) {
            insertGames();
        }

        getPlayerCount();
        if (nextPlayerId==0) {
            insertPlayers();
        }
    }

    public void getGameCount() throws Exception {
        VoltTable tables[] = client.callProcedure("GAMES_SELECT_ALL").getResults();
        VoltTable table = tables[0];
        gameCount = table.getRowCount();
    }

    public void insertGames() throws Exception {
        System.out.println("Loading Games");
        insertGame("FALKEN'S MAZE");
        insertGame("BLACK JACK");
        insertGame("GIN RUMMY");
        insertGame("HEARTS");
        insertGame("BRIDGE");
        insertGame("CHECKERS");
        insertGame("CHESS");
        insertGame("POKER");
        insertGame("FIGHTER COMBAT");
        insertGame("GUERRILLA ENGAGEMENT");
        insertGame("DESERT WARFARE");
        insertGame("AIR-TO-GROUND ACTIONS");
        insertGame("THEATERWIDE TACTICAL WARFARE");
        insertGame("THEATERWIDE BIOTOXIC AND CHEMICAL WARFARE");
        insertGame("GLOBAL THERMONUCLEAR WAR");

    }

    public void insertGame(String name) throws Exception {
        client.callProcedure(new GenericCallback("GAMES.insert"),"GAMES.insert",gameCount,name);
        gameCount++;
        System.out.println(" "+name);
    }

    public void getPlayerCount() throws Exception {
        // if players exist, don't pre-load any more
        VoltTable tables[] = client.callProcedure("GET_MAX_PLAYER_ID").getResults();
        VoltTable table = tables[0];
        int maxPlayerId = (int)tables[0].asScalarLong();
        //System.out.println("maxPlayerId = "+maxPlayerId);
        if (maxPlayerId > 0) 
            nextPlayerId = maxPlayerId + 1;
    }


    public void iterate() throws Exception {

        int player_id = 0;
        Session session = null;

        if (sessions.size() > 0 && rand.nextInt(3) < 2) {
            // existing session
            session = sessions.get(rand.nextInt(sessions.size()));
        } else {
            // new session
            if (rand.nextInt(20) == 0) {
                // new player
                player_id = newPlayer();
            } else {
                // returning player
                player_id = rand.nextInt(nextPlayerId);
            }
            session = newSession(player_id);
        }

        // play game a bit
        playSome(session);

        // end player game session
        if (rand.nextInt(2) == 0)
            endSession(session);
    }

    public void insertPlayers() throws Exception {
        System.out.println("\nLoading Players");
        for (int i=0; i<playerCount; i++) {
            newPlayer();
            if (i % 50000 == 0)
                System.out.println("  "+i);
        }
        System.out.println("  "+playerCount);
    }
    public int newPlayer() throws Exception {
        String name = gen.getFullName();
        int player_id = nextPlayerId;
        client.callProcedure(new GenericCallback("PLAYERS.insert"),"PLAYERS.insert",player_id,name,"");
        nextPlayerId++;
        return player_id;
    }

    public Session newSession(int player_id) throws Exception {
        int game_id = 0;
        int r = rand.nextInt(4);
        if (r > 2) {
            game_id = rand.nextInt(8);
        } else if(r > 0) {
            game_id = rand.nextInt(gameCount);
        } else {
            game_id = 7;
        }

        // insert a new session
        //client.callProcedure(new GenericCallback("GAME_PLAYERS.insert"),"GAME_PLAYERS.insert",game_id,player_id,0,1,0,1);
        client.callProcedure(new GenericCallback("NewSession"),"NewSession",game_id,player_id);

        Session session = new Session();
        session.game_id = game_id;
        session.player_id = player_id;

        sessions.add(session);
        sessionsRun++;

        return session;
    }

    public void playSome(Session session) throws Exception {
        int turns = rand.nextInt(5);
        for (int i=0; i<turns; i++) {
            // increment score
            incrementScore(session);

            // level up
            if (rand.nextInt(2) == 0)
                levelUp(session);
        }
    }

    public void incrementScore(Session session) throws Exception {
        int scoreIncrement = rand.nextInt(500);
        client.callProcedure(new GenericCallback("INCREMENT_SCORE"),"INCREMENT_SCORE",scoreIncrement, session.game_id, session.player_id);
    }

    public void levelUp(Session session) throws Exception {
        client.callProcedure(new GenericCallback("LEVEL_UP"), "LEVEL_UP", session.game_id, session.player_id);
    }

    public void endSession(Session session) throws Exception {
        client.callProcedure(new GenericCallback("END_SESSION"), "END_SESSION", session.game_id, session.player_id);
        sessions.remove(session);
    }


    public void printResults() throws Exception {
        
        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println(" Transaction Results");
        System.out.println(HORIZONTAL_RULE);
        GenericCallbackCounter.printProcedureResults("GAMES.insert");
        GenericCallbackCounter.printProcedureResults("PLAYERS.insert");
        GenericCallbackCounter.printProcedureResults("NewSession");
        GenericCallbackCounter.printProcedureResults("INCREMENT_SCORE");
        GenericCallbackCounter.printProcedureResults("LEVEL_UP");
        GenericCallbackCounter.printProcedureResults("END_SESSION");

        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println(" Game Results");
        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println("Active Sessions: " + sessions.size());
        System.out.println("Total Sessions: " + sessionsRun);

        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println(" Client Workload Statistics");
        System.out.println(HORIZONTAL_RULE);
        ClientStats stats = fullStatsContext.fetch().getStats();
        System.out.printf("Average throughput:            %,9d txns/sec\n", stats.getTxnThroughput());
        System.out.printf("Average latency:               %,9d ms\n", stats.getAverageLatency());
        System.out.printf("95th percentile latency:       %,9d ms\n", stats.kPercentileLatency(.95));
        System.out.printf("99th percentile latency:       %,9d ms\n", stats.kPercentileLatency(.99));

        System.out.print("\n" + HORIZONTAL_RULE);
        System.out.println(" System Server Statistics");
        System.out.println(HORIZONTAL_RULE);
        if (config.autotune) {
            System.out.printf("Targeted Internal Avg Latency: %,9d ms\n", config.latencytarget);
        }
        System.out.printf("Reported Internal Avg Latency: %,9d ms\n", stats.getAverageInternalLatency());

        client.writeSummaryCSV(stats, config.statsfile);
    }
    
    public static void main(String[] args) throws Exception {
        BenchmarkConfig config = new BenchmarkConfig();
        config.parse(GenericBenchmark.class.getName(), args);
        
        GameBenchmark benchmark = new GameBenchmark(config);
        benchmark.runBenchmark();

    }
}
