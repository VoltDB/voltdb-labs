# check for voltdb
# if voltdb is in the PATH, use that version
if [ -n "$(which voltdb 2> /dev/null)" ]; then
    # voltdb was already in the PATH, so we just set VOLTDB_HOME for use in scripts
    VOLTDB_BIN=$(dirname "$(which voltdb)")
    export VOLTDB_HOME=$(dirname "$VOLTDB_BIN")
else
    # voltdb was not already in the PATH, but VOLTDB_HOME was set, so the bin subdirectory will be added to the PATH
    if [ -d "$VOLTDB_HOME" ]; then
        VOLTDB_BIN="$VOLTDB_HOME/bin"
        export PATH="$PATH:$VOLTDB_BIN"
    else
        echo "Can't find VoltDB binaries!"
        echo "Add the voltdb bin directory to your PATH, or, set VOLTDB_HOME to the directory where voltdb is installed."
        echo "For example, if voltdb was installed in your HOME directory:"
        echo "   PATH=\"\$PATH:\$HOME/voltdb/bin\""
        echo "     -or-"
        echo "   VOLTDB_HOME=\$HOME/voltdb"
        exit
    fi
fi

if [ -d "$VOLTDB_HOME/lib/voltdb" ]; then
    # Debian paths
    export CLASSPATH="`ls -1 $VOLTDB_HOME/lib/voltdb/voltdb-*.jar $VOLTDB_HOME/lib/*.jar | tr '\n' ':'`"
    export LICENSE="$VOLTDB_HOME/lib/voltdb/license.xml"
    export LOG4J="$VOLTDB_HOME/lib/voltdb/log4j.xml"
else
    # non-Debian paths
    export CLASSPATH="`ls -1 $VOLTDB_HOME/voltdb/voltdb-*.jar $VOLTDB_HOME/lib/*.jar | tr '\n' ':'`"
    export LICENSE="$VOLTDB_HOME/voltdb/license.xml"
    export LOG4J="$VOLTDB_HOME/voltdb/log4j.xml"
fi        

# if any .jar files are in lib/extensions, add those to the CLASSPATH
if ls "$VOLTDB_HOME/lib/extensions/*.jar" &> /dev/null; then
    export CLASSPATH="$CLASSPATH:`ls -1 $VOLTDB_HOME/lib/extensions/*.jar | tr '\n' ':'`"
fi
