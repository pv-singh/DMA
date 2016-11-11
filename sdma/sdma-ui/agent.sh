# /bin/sh
export JAVA_HOME=/home/amit/Softwares/jdk1.8.0_101/
JAVACMD="$JAVA_HOME/bin/java"
$JAVACMD -javaagent:lib/jborat-agent.jar \
 -noverify \
 -Dch.usi.dag.jborat.exclusionList="conf/exclusion.lst" \
 -Dch.usi.dag.jp2.dumpers="ch.usi.dag.jp2.dump.xml.XmlDumper" \
  -Dch.usi.dag.jborat.liblist="conf/lib.lst" \
   -Dch.usi.dag.jp2.outputFilePrefix="ch_usi_dag_jp2" \
    -Dch.usi.dag.jborat.instrumentation="ch.usi.dag.jp2.instrument.AddInstrumentation" \
     -Dch.usi.dag.jborat.codemergerList="conf/codemerger.lst" \
      -Dch.usi.dag.jborat.uninstrumented="uninstrumented"  \
      -Dch.usi.dag.jborat.instrumented="instrumented" \
       -Xbootclasspath/p:./lib/Thread_JP2.jar:lib/jborat-runtime.jar:lib/jp2-runtime.jar:lib/jp2-entity.jar$SUPPORT_JARS  \
       -jar $APP_JAR
       
