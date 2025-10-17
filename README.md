# Time Ontology in SHACL
<p align="justify">
This repository contains the SHACL shapes (<i>SHACLshapesTimeOntology.shacl</i>) and SHACL-SPARQL rules (<i>SHACLrulesTimeOntology.shacl</i>) introduced and described in the paper "<i>On the interplay between validation and inference in SHACL: an investigation on the Time Ontology</i>", submitted to the <a href="https://www.semantic-web-journal.net">Semantic Web Journal</a>.
</p>

<p align="justify">
The shapes and rules were designed based on the version of the <a href="https://www.w3.org/TR/owl-time">Time Ontology</a> retrieved on September 10, 2025. However, future updates to the Time Ontology may not be fully compatible with the implementation provided here.
</p>

<p align="justify">
The following Java file is provided to execute the SHACL shapes and SHACL-SPARQL rules on <i>time-ontology-retrieved-10.09.2025.ttl</i>:
<ul>
  <li><i>InferAndValidateThroughSHACL.java</i> — executes the shapes and rules on the file specified as a parameter and prints the validation results.</li>
</ul>
The Java files can be compiled and run on Windows using the scripts <i>compile.bat</i> and <i>run.bat</i>, respectively. You can modify <i>run.bat</i> to change the input parameter for <i>InferAndValidateThroughSHACL</i>. Users are encouraged to experiment with the validation framework to explore and better understand its functionality.
</p>

