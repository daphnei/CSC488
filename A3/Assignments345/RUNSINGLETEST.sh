#! /bin/bash

ant dist
RUNCOMPILER.sh ./current_test.488 #tests/passing/scopes/test_01.488
