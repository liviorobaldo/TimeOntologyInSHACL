import org.semanticweb.HermiT.Reasoner.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.*;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import java.util.*;
import java.io.*;

public class InferThroughOWL 
{
    private static File TBoxFile = new File("./TimeOntologySHACL/time.rdf");
    private static File ABoxFile = new File("./TimeOntologySHACL/ABox.rdf");
    private static File owlAxioms = new File("./TimeOntologySHACL/owlAxioms.owl");
    
    private static File inferredTriplesFile = new File("./TimeOntologySHACL/inferredTriplesThroughOWL.rdf");
    
    public static void main(String[] args) throws Exception 
    {
        if(args.length==3)
        {
            TBoxFile = new File(args[0]);
            ABoxFile = new File(args[1]);
            owlAxioms = new File(args[2]);
        }

        // We load and merge the three ttl files into a single OWL ontology
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(TBoxFile);
        manager.addAxioms(ontology, manager.loadOntologyFromOntologyDocument(ABoxFile).getAxioms());
        manager.addAxioms(ontology, manager.loadOntologyFromOntologyDocument(owlAxioms).getAxioms());
        
        
        /************************************************************************************************************************
            INFERENCE through HermiT
        /************************************************************************************************************************/
        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

            //Ensure the ontology is consistent
        if(!reasoner.isConsistent()) 
        {
            System.out.println("The ontology is inconsistent.");
            return;
        }
        
        // Prepare to collect inferred axioms
        List<InferredAxiomGenerator<? extends OWLAxiom>> generators = Arrays.asList(
                new InferredSubClassAxiomGenerator(),                // For subclass relationships
                new InferredClassAssertionAxiomGenerator()//,          // For class assertions (individuals)
                // Add more generators if needed
        );

        // Create a new empty ontology to store inferred axioms
        OWLOntology inferredOntology = manager.createOntology();

        // Generate inferred axioms and add them to the new ontology
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, generators);
        iog.fillOntology(manager, inferredOntology);

        FileOutputStream fos = new FileOutputStream(inferredTriplesFile);
        manager.saveOntology(inferredOntology, new TurtleOntologyFormat(), fos);
        fos.close();
        
        // Clean up the reasoner
        reasoner.dispose();

        System.out.println("Inferred triples have been saved to: " + inferredTriplesFile.getAbsolutePath());
    }
}
