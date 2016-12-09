#!/bin/bash
echo ""
echo "  SiteWhere Start Script for Linux/Unix v1.0"
echo ""

if hash java 2>/dev/null; then

#	JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
#	JAVA_OPTS="$JAVA_OPTS -XX:-UseSplitVerifier -noverify"

	if [ -z "$SITEWHERE_HOME" ]; then
		SITEWHERE_FOLDER="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../" && pwd )"
	else
		SITEWHERE_FOLDER=$SITEWHERE_HOME
	fi

	if [ ! -d "$SITEWHERE_FOLDER" ]; then
		echo ERROR! SiteWhere Home Folder not found.
	else

        if [ ! -w "$SITEWHERE_FOLDER" ]; then 
            echo ERROR! SiteWhere Home Folder Permissions not correct.
        else    

		  if [ ! -f "$SITEWHERE_FOLDER/lib/sitewhere.war" ]; then
	           	echo ERROR! SiteWhere WAR not found.
	           	echo $SITEWHERE_FOLDER;
		  else
			 #SITEWHERE_FOLDER=$(echo "$SITEWHERE_FOLDER" | sed 's/ /\\ /g')
			 HOME_OPT="-Dsitewhere.home=$SITEWHERE_FOLDER -Dspring.config.location=$SITEWHERE_FOLDER/conf/"
			 JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$SITEWHERE_FOLDER/heap-dump.hprof"

			echo "-------------------------------------------------------------------------"
			echo ""
			echo "  SITEWHERE_HOME: $SITEWHERE_FOLDER"
			echo ""
			echo "  JAVA_OPTS: $JAVA_OPTS"
			echo ""
			echo "-------------------------------------------------------------------------"
			echo ""
			 # Run SiteWhere
			 WAR_PATH="$SITEWHERE_FOLDER/lib/sitewhere.war"
			 eval \"java\" "$HOME_OPT" "$JAVA_OPTS" -jar "$WAR_PATH"
		  fi
	   fi
    fi

else
	echo You do not have the Java Runtime Environment installed, please install Java JRE from java.com/en/download and try again.
fi
