#! /bin/bash

ant dist
RUNCOMPILER.sh tests/passing/scopes/test_01.488
