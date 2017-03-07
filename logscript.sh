#!/bin/bash
MIN_NR=$2
NR_OCCURRENCES=$(grep "^$(date -d -1hour +'%Y-%m-%d %H')" $1 | grep -F -c "[INFO]  @virtwho.py:309 - Report for config \"env/cmdline\" hasn't changed, not sending")
if [ "$NR_OCCURRENCES" -lt "$MIN_NR" ]; then
	mail -s "$HOSTNAME" "bvervaele@oxya.be" <<< "Only $NR_OCCURRENCES last hour."
    echo "Only $NR_OCCURRENCES last hour"
	exit 1
fi
echo "$NR_OCCURRENCES last hour, so it still works."

