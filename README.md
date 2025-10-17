# Time Ontology in SHACL
<p align="justify">
This repository contains the SHACL shapes (file <i>SHACLshapesTimeOntology.shacl</i>) and the SHACL-SPARQL rules (file <i>SHACLrulesTimeOntology.shacl</i>) introduced and described in the paper "On the interplay between validation and inference in SHACL - an investigation on the Time Ontology", submitted to the <a href="https://www.semantic-web-journal.net">Semantic Web journal</a>.
</p>

<p align="justify">
The shapes and rules have been designed based on the version of the <a href="https://www.w3.org/TR/owl-time">Time Ontology</a> retrieved on September 10, 2025. However, future versions of the Time Ontology may not be compatible with the implementation proposed here.  
</p>

<p align="justify">
The following Java file is provided to execute the SHACL shapes and SHACL-SPARQL rules on <i>time-ontology-retrieved-10.09.2025.ttl</i>:
<ul>
  <li><i>InferAndValidateThroughSHACL.java</i>, which executes the shapes and the rules on the ABox in the file <i>TimeOntologySHACL/ABox.rdf</i> and prints the result of the validation. The user is invited to add to the file <i>ABox.rdf</i> new RDF triples for further testing and "playing with" the proposed shapes and rules.</li>
</ul>
The Java files might be compiled and run on Windows through the files <i>compile.bat</i> and <i>run.bat</i> respectively.
</p>
