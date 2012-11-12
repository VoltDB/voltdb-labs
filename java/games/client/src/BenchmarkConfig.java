package client;

import org.voltdb.CLIConfig;

/**
 * Uses included {@link CLIConfig} class to
 * declaratively state command line options with defaults
 * and validation.
 */
public class BenchmarkConfig extends CLIConfig {
    @Option(desc = "Interval for performance feedback, in seconds.")
    long displayinterval = 5;

    @Option(desc = "Benchmark duration, in seconds.")
    int duration = 20;

    @Option(desc = "Warmup duration in seconds.")
    int warmup = 2;

    @Option(desc = "Comma separated list of the form server[:port] to connect to.")
    String servers = "localhost";

    @Option(desc = "Maximum TPS rate for benchmark.")
    int ratelimit = 100000;

    @Option(desc = "Determine transaction rate dynamically based on latency.")
    boolean autotune = true;

    @Option(desc = "Server-side latency target for auto-tuning.")
    int latencytarget = 10;

    @Option(desc = "Filename to write raw summary statistics to.")
    String statsfile = "";

    @Override
    public void validate() {
        if (duration <= 0) exitWithMessageAndUsage("duration must be > 0");
        if (warmup < 0) exitWithMessageAndUsage("warmup must be >= 0");
        if (displayinterval <= 0) exitWithMessageAndUsage("displayinterval must be > 0");
        if (ratelimit <= 0) exitWithMessageAndUsage("ratelimit must be > 0");
        if (latencytarget <= 0) exitWithMessageAndUsage("latencytarget must be > 0");
    }
}
