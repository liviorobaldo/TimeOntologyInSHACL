# Time Ontology in SHACL
<p align="justify">
This repository contains the SHACL shapes (file <i>timeSHACLshapes.ttl</i>) and the SHACL-SPARQL rules (file <i>timeSHACLrules.ttl</i>) introduced and described in the paper "On the interplay between validation and inference in SHACL - an investigation on the Time Ontology", submitted to the <a href="https://www.semantic-web-journal.net">Semantic Web journal</a>.
</p>

<p align="justify">
The shapes and the rules have been designed for the version of the <a href="https://www.w3.org/TR/owl-time">Time Ontology</a> retrieved on the 1st June 2024. A copy of this version is available on this GitHub repository (file <i>time.ttl</i>); of course, subsequent versions of the Time Ontology could not be compatible with the implementation proposed here.
</p>
  
<p align="justify">
Three Java files are available on this GitHub to execute the shapes and the rules:
<ul>
  <li><i>InferAndValidate.java</i>, which executes the shapes and the rules on the ABox in the file <i>ABox.ttl</i> and prints the result of the validation. The user is invited to add to the file <i>ABox.ttl</i> 
    new RDF triples for further testing and "playing with" the proposed shapes and rules.</li>
  <li><i>generateAllensCompositionTableOn2ProperIntervals.java</i> (in the subfolder <i>SyntheticDatasetGenerators</i>), which generates the composition table of Allen's temporal algebra and prints it in the output 
    file <i>AllenIntervalAlgebraCompositionTable.txt</i>.</li>
  <li><i>generateAllensTemporalRelationsOn2ProperIntervals.java</i> (in the subfolder <i>SyntheticDatasetGenerators</i>), which generates and validates all possible knowledge graphs involving two properties 
    denoting one of Allen's temporal relations and connecting the same pair of proper intervals. The results of the validation are then written in the output file <i>validationResults.txt</i>.</li>
</ul>
The three Java files might be compiled and run on Windows through the files <i>compile.bat</i> and <i>run.bat</i> respectively.
</p>
