#! /bin/bash

USAGE() {
	echo "Usage: sh RUNALLTESTS.sh [-f] compiler.jar test_dir"
	echo "Use -f if the scripts in test_dir are designed to fail compilation."

	exit 1
}

#Check for the correct usage
if [ "$#" == 2 ]
then
	COMPILER=$1
	TEST_DIR=$2
	CHECK_FOR_FAIL=0
elif [ "$#" == 3 ]
then
	if [ "$1" != "-f" ]
	then
		USAGE
	fi

	COMPILER=$2
    TEST_DIR=$3
	CHECK_FOR_FAIL=1
else
	USAGE
fi

echo "RUNNING TESTS IN FOLDER: $TEST_DIR"
echo "USING COMPILER: $COMPILER"
echo

#Iterate over all of the .488 files in the passing directory.
for file in $(find $TEST_DIR -name '*.488')
do
	#Try to compile the file.
	result="$(java -jar $COMPILER $file 2>&1 )"

	#Check if the compilation output contains the word error.
	grep -qv "Error" <<< $result
	if [ ! $? -eq 0 ]
	then
		if [ $CHECK_FOR_FAIL == 0 ]
		then
			#This is the case where there was an error but there should not have been.
			echo "Error in file: $file"
			echo "$result"
			echo
		fi
	else
		if [ $CHECK_FOR_FAIL == 1 ]
		then
			#This is the case where there was not an error but there should have been.
			echo "Error in file: $file"
			echo "Compilation should have failed."
			echo
		fi
	fi	
done

exit 0

