java -cp .;./lib/spark/*;./lib/* -Dfile.encoding=utf-8 InferAndValidate.java
java -cp .;./lib/spark/*;./lib/* -Dfile.encoding=utf-8 SyntheticDatasetGenerators/generateAllensCompositionTableOn2ProperIntervals.java
java -cp .;./lib/spark/*;./lib/* -Dfile.encoding=utf-8 SyntheticDatasetGenerators/generateAllensTemporalRelationsOn2ProperIntervals.java