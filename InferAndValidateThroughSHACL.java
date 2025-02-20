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
    private static File TBoxFile = new File("./TimeOntologySHACL/time.rdf");
    private static File ABoxFile = new File("./TimeOntologySHACL/ABox.rdf");
    private static File rulesFile = new File("./TimeOntologySHACL/timeSHACLrules.shacl");
    private static File shapesFile = new File("./TimeOntologySHACL/timeSHACLshapes.shacl");
    private static File inferredTriplesFile = new File("./TimeOntologySHACL/inferredTriplesThroughSHACL.rdf");
    
    public static void main(String[] args) throws Exception 
    {
        if(args.length==4)
        {
            TBoxFile = new File(args[0]);
            ABoxFile = new File(args[1]);
            rulesFile = new File(args[2]);
            shapesFile = new File(args[3]);
        }
        
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
        Model rules = ModelFactory.createDefaultModel();
        FileInputStream fisRules = new FileInputStream(rulesFile);
        InputStreamReader readerRules = new InputStreamReader(fisRules, StandardCharsets.UTF_8);
        rules.read(readerRules, "urn:dummy", FileUtils.langTurtle);
        readerRules.close();
        fisRules.close();
        
            //We iteratively execute the rules until no further RDF triple is added to the model.
            //In the general case, this might loop infinitely, but not in the examples considered here. See paper for details.
        long lastSize = 0;
        while(model.size()>lastSize)
        {
            lastSize = model.size();
            model = RuleUtil.executeRules(model, rules, null, null).add(model);
        }
        
        model.setNsPrefixes(TBox.getNsPrefixMap());
        model.setNsPrefixes(rules.getNsPrefixMap());
        print(model, tboxStatements, inferredTriplesFile);
        //System.out.println("Inferred triples have been saved to: " + inferredTriplesFile.getAbsolutePath());
        
        
        /************************************************************************************************************************
            VALIDATION
        /************************************************************************************************************************/
        
            //Loading the shapes
        Model shapes = ModelFactory.createDefaultModel();
        FileInputStream fisShapes = new FileInputStream(shapesFile);
        InputStreamReader readerShapes = new InputStreamReader(fisShapes, StandardCharsets.UTF_8);
        shapes.read(readerShapes, "urn:dummy", FileUtils.langTurtle);
        readerShapes.close();
        fisShapes.close();

            //Validate & Print
        Model report = ValidationUtil.validateModel(model, shapes, true).getModel();
        ArrayList<String> errorMessages = extractValidationErrorMessages(report);
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
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
