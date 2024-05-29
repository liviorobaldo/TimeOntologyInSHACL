# Time Ontology in SHACL
<p>
This repository contains the SHACL shapes (file timeSHACLshapes.ttl) and the SHACL-SPARQL rules (file timeSHACLrules.ttl) described in the paper "On the interplay between validation and inference in SHACL - an investigation on the Time Ontology", submitted to the <a href="https://www.semantic-web-journal.net">Semantic Web journal</a>.
</p>

<p>
The shapes and the rules have been designed on the version of the <a href="https://www.w3.org/TR/owl-time">Time Ontology</a> retrieved on 1st June 2024. A copy of this version is available on this GitHub repository (file time.ttl); of course, subsequent versions of the Time Ontology could not be compatible with the implementation proposed here.
</p>

<p>
Three Java files are available on this GitHub to execute the shapes and the rules:
<ul>
  <li><b>InferAndValidate.java</b>, which execute the shapes and the rules on the ABox in the file ABox.ttl and prints the result of the validation. The user is invited to add in the file ABox.ttl new RDF triples to
  test and "play with" the proposed shapes and rules.</li>
  <li><b>generateAllensCompositionTableOn2ProperIntervals.java</b> (in the subfolder SyntheticDatasetGenerators), which generates the composition table of Allen's temporal algebra and prints it in the output file AllenIntervalAlgebraCompositionTable.txt.</li>
  <li><b>generateAllensTemporalRelationsOn2ProperIntervals.java</b> (in the subfolder SyntheticDatasetGenerators), which generates and validates all possible knowledge graphs involving two properties denoting one of Allen's temporal relations on the same pair of proper interval. The results of the validation are then written in the file validationResults.txt.</li>
</ul>
The three Java files might be compiled and run on Windows through the files "compile.bat" and "run.bat" respectively.
</p>
