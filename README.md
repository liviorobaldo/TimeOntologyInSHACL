# Time Ontology in SHACL
<p align="justify">
This repository contains the SHACL shapes (file <i>timeSHACLshapes.shapes</i>) and the SHACL-SPARQL rules (file <i>timeSHACLrules.shacl</i>) introduced and described in the paper "On the interplay between validation and inference in SHACL - an investigation on the Time Ontology", submitted to the <a href="https://www.semantic-web-journal.net">Semantic Web journal</a>.
</p>

<p align="justify">
The shapes and rules have been designed based on the version of the <a href="https://www.w3.org/TR/owl-time">Time Ontology</a> retrieved on February 20, 2025. However, future versions of the Time Ontology may not be compatible with the implementation proposed here.  
</p>

<p align="justify">
The current axiomatization of the Time Ontology is expressed in <a href="https://www.w3.org/OWL">OWL</a>. Since this work focuses on <a href="https://www.w3.org/TR/shacl">SHACL</a> rather than OWL, the repository separates the RDF model (stored in <i>TimeOntologySHACL/time.rdf</i>) from the OWL axioms (stored in <i>TimeOntologySHACL/owlAxioms.owl</i>).
</p>

<p align="justify">
Users who wish to apply the OWL axioms can do so with <a href="http://www.hermit-reasoner.com">HermiT</a> by using the file <i>InferThroughOWL.java</i>. In contrast, three Java files are provided in this GitHub repository to execute the SHACL shapes and SHACL-SPARQL rules on <i>TimeOntologySHACL/time.rdf</i>:
<ul>
  <li><i>InferAndValidateThroughSHACL.java</i>, which executes the shapes and the rules on the ABox in the file <i>TimeOntologySHACL/ABox.rdf</i> and prints the result of the validation. The user is invited to add to the file <i>ABox.rdf</i> new RDF triples for further testing and "playing with" the proposed shapes and rules.</li>
  <li><i>SyntheticDatasetGenerators/generateAllensCompositionTableOn2ProperIntervals.java</i>, which generates the composition table of Allen's temporal algebra and prints it in the output 
    file <i>AllenIntervalAlgebraCompositionTable.txt</i>.</li>
  <li><i>SyntheticDatasetGenerators/generateAllensTemporalRelationsOn2ProperIntervals.java</i>, which generates and validates all possible knowledge graphs involving two properties 
    denoting one of Allen's temporal relations and connecting the same pair of proper intervals. The results of the validation are then written in the output file <i>TimeOntologySHACL/validationResults.txt</i>.</li>
</ul>
The Java files might be compiled and run on Windows through the files <i>compile.bat</i> and <i>run.bat</i> respectively.
</p>
