#!/usr/bin/env bash
set -e

# create bin directory if it doesn't exist
[ -d "../bin" ] || mkdir ../bin

# clean previous actuals
[ -e "./ACTUAL1.TXT" ] && rm ACTUAL1.TXT
[ -e "./ACTUAL2.TXT" ] && rm ACTUAL2.TXT

# start from a clean save file so we control the test
[ -e "./data/Usagi.txt" ] && rm ./data/Usagi.app.Usagi.txt

# compile the code into the bin folder, terminate if error occurred
if ! javac -cp ../src/main/java -Xlint:none -d ../bin ../src/main/java/*.java; then
  echo "********** BUILD FAILURE **********"
  exit 1
fi

# run #1: create tasks, mark/unmark, list, save
java -classpath ../bin Usagi.app.Usagi < input1.txt > ACTUAL1.TXT

# compare run #1
if diff ACTUAL1.TXT EXPECTED1.TXT >/dev/null; then
  echo "Test 1 result: PASSED"
else
  echo "Test 1 result: FAILED"
  exit 1
fi

# run #2: new process; should load from data/Usagi.app.Usagi.txt and list
java -classpath ../bin Usagi.app.Usagi < input2.txt > ACTUAL2.TXT

# compare run #2
if diff ACTUAL2.TXT EXPECTED2.TXT >/dev/null; then
  echo "Test 2 result: PASSED"
  echo "********** ALL TESTS PASSED **********"
  exit 0
else
  echo "Test 2 result: FAILED"
  exit 1
fi
