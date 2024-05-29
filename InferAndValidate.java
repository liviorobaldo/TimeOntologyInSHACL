import java.util.*;
import java.io.*;
import org.apache.jena.rdf.model.*;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.validation.ValidationUtil;

import org.apache.jena.util.FileUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

public class InferAndValidate 
{
    private static File TBoxFile = new File("time.ttl");
    private static File ABoxFile = new File("ABox.ttl");
    private static File rulesFile = new File("timeSHACLrules.ttl");
    private static File shapesFile = new File("timeSHACLshapes.ttl");
    private static File inferredTriplesFile = new File("inferredTriples.rdf");
    
    public static void main(String[] args) throws Exception 
    {
            //Loading the TBox
        Model model = JenaUtil.createMemoryModel();
        FileInputStream fisTBox = new FileInputStream(TBoxFile);
        model.read(fisTBox, "urn:dummy", FileUtils.langTurtle);
        fisTBox.close();
        List<Statement> tboxStatements = model.listStatements().toList();
        
            //Loading the ABox
        Model ABox = JenaUtil.createMemoryModel();
        FileInputStream fisABox = new FileInputStream(ABoxFile);
        ABox.read(fisABox, "urn:dummy", FileUtils.langTurtle);
        fisABox.close();

            //We add the ABox to the TBox, thus model is the union of both.
        model.add(ABox);
        

        /************************************************************************************************************************
            INFERENCE
        /************************************************************************************************************************/

            //Loading the inference rules
        Model rules = JenaUtil.createMemoryModel();
        FileInputStream fisRules = new FileInputStream(rulesFile);
        rules.read(fisRules, "urn:dummy", FileUtils.langTurtle);
        fisRules.close();
        
            //We iteratively execute the rules until no further RDF triple is added to the model.
            //In the general case, this might loop infinitely, but not in the examples considered here. See paper for details.
        long lastSize = 0;
        while(model.size()>lastSize)
        {
            lastSize = model.size();
            model = RuleUtil.executeRules(model, rules, null, null).add(model);
        }
        print(model, tboxStatements, inferredTriplesFile);
        
        
        /************************************************************************************************************************
            VALIDATION
        /************************************************************************************************************************/
        
            //Loading the shapes
        Model shapes = JenaUtil.createMemoryModel();
        FileInputStream fisShapes = new FileInputStream(shapesFile);
        shapes.read(fisShapes, "urn:dummy", FileUtils.langTurtle);
        fisShapes.close();

            //Validate & Print
        Model report = ValidationUtil.validateModel(model, shapes, true).getModel();
        ArrayList<String> errorMessages = extractValidationErrorMessages(report);
		
		System.out.println("\n\n\nVALIDATION RESULTS:\n");
        if(errorMessages.isEmpty())System.out.println("The knowledge graph is valid.");
        for(String m:errorMessages)System.out.println(m);
    }
    
    
    //UTILITY TO PRINT THE MODEL
    private static void print(Model model, List<Statement> tboxStatements, File inferredTriplesFile)throws Exception
    {
            //We only output the inferred statements.
        List<Statement> finalStatements = model.listStatements().toList();
        finalStatements.removeAll(tboxStatements);
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
        
        StmtIterator stmt = report.listStatements();
        while(stmt.hasNext())
        {
            Statement s = stmt.nextStatement();
            if(s.getPredicate().toString().compareToIgnoreCase("http://www.w3.org/ns/shacl#resultMessage")==0)
                ht.put(s.getObject().toString(),"");
        }
        
        return new ArrayList<String>(ht.keySet());
    }
}
