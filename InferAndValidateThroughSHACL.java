import java.util.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import org.apache.jena.rdf.model.*;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.validation.ValidationUtil;

import org.apache.jena.util.FileUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;


public class InferAndValidateThroughSHACL 
{
    //private static File ABoxFile = new File("../TimeOntologySHACL/ABox.rdf");
    private static File ABoxFile = new File("./Examples/Example14.rdf");
    
    private static File TBoxFile = new File("./time-ontology-retrieved-10.09.2025.ttl");
    private static File SHACLrulesTimeOntology = new File("./SHACLrulesTimeOntology.shacl");
    private static File SHACLshapesTimeOntology = new File("./SHACLshapesTimeOntology.shacl");
    private static File inferredTriplesFile = new File("./inferredTriplesThroughSHACL.rdf");
    
    public static void main(String[] args) throws Exception 
    {
        if(args.length==1)ABoxFile = new File(args[0]);
        
        Model model = ModelFactory.createDefaultModel();
        
            //Loading the TBox
        Model TBox = ModelFactory.createDefaultModel();
        FileInputStream fisTBox = new FileInputStream(TBoxFile);
        InputStreamReader readerTBox = new InputStreamReader(fisTBox, StandardCharsets.UTF_8);
        model.read(readerTBox, "http://www.w3.org/2006/time", "TURTLE");
        readerTBox.close();
        fisTBox.close();
        model.add(TBox);
        List<Statement> tboxStatements = model.listStatements().toList();
        
            //Loading the ABox
        Model ABox = ModelFactory.createDefaultModel();
        FileInputStream fisABox = new FileInputStream(ABoxFile);
        InputStreamReader readerABox = new InputStreamReader(fisABox, StandardCharsets.UTF_8);
        ABox.read(readerABox, "http://www.timeontologysampleabox.org", "TURTLE");
        readerABox.close();
        fisABox.close();
        model.add(ABox);
        

        /************************************************************************************************************************
            INFERENCE
        /************************************************************************************************************************/

            //Loading the inference rules
            //Loading the inference rules
        Model rulesTimeOntology = ModelFactory.createDefaultModel();
        FileInputStream fisRules = new FileInputStream(SHACLrulesTimeOntology);
        InputStreamReader readerRules = new InputStreamReader(fisRules, StandardCharsets.UTF_8);
        rulesTimeOntology.read(readerRules, "urn:dummy", FileUtils.langTurtle);
        readerRules.close();
        fisRules.close();        
        
        
            //We iteratively execute the rules until no further RDF triple is added to the model.
            //In the general case, this might loop infinitely, but not in the examples considered here. See paper for details.
        long lastSize = 0;
                
        /**/
        while(model.size()>lastSize)
        {
            lastSize = model.size();
            model = RuleUtil.executeRules(model, rulesTimeOntology, null, null).add(model);
        }
		


            //Printing inferred triples
        model.setNsPrefixes(rulesTimeOntology.getNsPrefixMap());
        model.setNsPrefixes(TBox.getNsPrefixMap());
        print(model, tboxStatements, inferredTriplesFile);
        System.out.println("\nInferred triples have been saved to: " + inferredTriplesFile.getAbsolutePath()+"\n");
        
        
        
        /************************************************************************************************************************
            VALIDATION
        /************************************************************************************************************************/
        
            //Loading the shapes
        Model shapes = ModelFactory.createDefaultModel();
        FileInputStream fisShapes = new FileInputStream(SHACLshapesTimeOntology);
        InputStreamReader readerShapes = new InputStreamReader(fisShapes, StandardCharsets.UTF_8);
        shapes.read(readerShapes, "urn:dummy", FileUtils.langTurtle);
        readerShapes.close();
        fisShapes.close();

            //Validate & Print
        Model report = ValidationUtil.validateModel(model, shapes, true).getModel();
        ArrayList<String> errorMessages = extractValidationErrorMessages(report);
        if(errorMessages.isEmpty())System.out.println("The knowledge graph is valid.");
        for(String m:errorMessages)System.out.println(m);
    }
    
    //UTILITY TO PRINT THE MODEL
    private static void print(Model model, List<Statement> tboxStatements, File inferredTriplesFile)throws Exception
    {
            //We only output the inferred statements.
        List<Statement> finalStatements = model.listStatements().toList();
        finalStatements.removeAll(tboxStatements);
        
            //In the inferred statements it adds a lot of triples inferred via RDFS schema from the Time Ontology.
            //We are not interested in these, so we remove them, we only keep the triples with the properties we are
            //interested in, namely the ones in Figure 1 in the paper.
        ArrayList<Statement> toRemove = new ArrayList<Statement>();
        for(Statement finalStatement:finalStatements)
        {
            Resource subject = finalStatement.getSubject();
            Property predicate = finalStatement.getPredicate();
            RDFNode object = finalStatement.getObject();
            if((subject.isURIResource())&&(subject.getURI().startsWith("http://www.w3.org/2006/time#")))toRemove.add(finalStatement);
            else if((object.isURIResource())&&(object.asResource().getURI().startsWith("http://www.w3.org/2002/07/owl#")))toRemove.add(finalStatement);
            else if
            (
                (predicate.isURIResource())&&
                (predicate.getURI().startsWith("http://www.w3.org/2006/time#"))&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#equals")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#before")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#after")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#hasBeginning")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#hasEnd")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#inside")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalAfter")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalBefore")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalContains")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalDuring")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalMeets")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalMetBy")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalStarts")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalStartedBy")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalFinishes")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalFinishedBy")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalEquals")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalOverlaps")!=0)&&
                (predicate.getURI().compareToIgnoreCase("http://www.w3.org/2006/time#intervalOverlappedBy")!=0)
            )toRemove.add(finalStatement);
            
        }
        finalStatements.removeAll(toRemove);
        
        Model outputModel = ModelFactory.createDefaultModel();
        outputModel.setNsPrefixes(model.getNsPrefixMap());
        outputModel.add(finalStatements);
        FileOutputStream outputStream = new FileOutputStream(inferredTriplesFile);
        RDFDataMgr.write(outputStream, outputModel, RDFFormat.TURTLE_BLOCKS);
        outputStream.close();        
    }
    
    //UTILITY TO EXTRACT THE VALIDATIION MESSAGES
    private static ArrayList<String> extractValidationErrorMessages(Model report) throws Exception 
    {   
        //System.out.println(org.topbraid.shacl.util.ModelPrinter.get().print(report));
        
            //We use an hashtable to avoid duplicated error messages. This can happen because of SPARQL,
            //which search for invalid triples in the knowledge graph *altogether*.
        Hashtable<String,String> ht = new Hashtable<String,String>();
        
        Property resultMessage = report.getProperty("http://www.w3.org/ns/shacl#resultMessage");
        Property focusNode = report.getProperty("http://www.w3.org/ns/shacl#focusNode");
        
        StmtIterator stmt = report.listStatements(null, resultMessage, (RDFNode) null);
        while (stmt.hasNext()) 
        {
            Statement msgStmt = stmt.nextStatement();
            Resource result = msgStmt.getSubject(); // The validation result node
            String message = msgStmt.getObject().toString();

            // Find corresponding focus node
            Statement focusStmt = result.getProperty(focusNode);
            if (focusStmt != null && focusStmt.getObject().isResource())
            {
                String localName = focusStmt.getObject().asResource().getLocalName();
                if(localName==null)localName="_:"+focusStmt.getObject().toString();
                ht.put(localName+" -- "+message, "");
            }
            else ht.put(message, "");
        }
        

        return new ArrayList<String>(ht.keySet());
    }
}
