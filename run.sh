#!/bin/bash
java -Dsun.java2d.d3d=false -Darrayv.disableSynthReflect=true -cp bin:lib/classgraph-4.8.47.jar:cache main.ArrayVisualizer $1 $2

