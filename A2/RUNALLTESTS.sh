#! /bin/sh

#  Location of directory containing  dist/compiler488.jar
WHERE=`dirname $0`


#  TODO: loop through subdirectories (enforcing structure)
#  and run each tests. Differentiate b/w 
#  Output to standard output 
java -jar $WHERE/dist/compiler488.jar  $1



exit 0


