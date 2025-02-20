REM comment/uncomment below what you want to execute.

java -cp .;./lib/spark/*;./lib/* -Dfile.encoding=utf-8 InferAndValidateThroughSHACL
REM java -cp .;./lib/spark/*;./lib/* -Dfile.encoding=utf-8 InferThroughOWL
REM java -cp .;./lib/spark/*;./lib/* -Dfile.encoding=utf-8 SyntheticDatasetGenerators/generateAllensCompositionTableOn2ProperIntervals
REM java -cp .;./lib/spark/*;./lib/* -Dfile.encoding=utf-8 SyntheticDatasetGenerators/generateAllensTemporalRelationsOn2ProperIntervals