package SyntheticDatasetGenerators;
import java.util.*;
import java.io.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.shacl.rules.RuleUtil;

/* This file generates all possible combinations of two properties denoting Allen's temporal relations holding
between two time:ProperInterval(s). Specifically: (1) when the property connects the two time:ProperInterval(s) 
in the same direction and when (2) it connects them in opposite direction. The total number of combination for
each of them is 15!/((15-2)!2!)=105. However, in (2) the two properties can also be the same, therefore we need
to consider 15 further cases. Overall, there are (105*2)+15=225 patterns in total. 
In each pattern so generated, we first execute the rules and then the shapes. Then, we print the evalutation.*/
public class generateAllensTemporalRelationsOn2ProperIntervals 
{
    private static File TBoxFile = new File("./TimeOntologySHACL/time.rdf");
    private static File rulesFile = new File("./TimeOntologySHACL/timeSHACLrules.shacl");
    private static File shapesFile = new File("./TimeOntologySHACL/timeSHACLshapes.shacl");
    private static File validationResultsFile = new File("./TimeOntologySHACL/validationResults.txt");
    
    private static int stateOfAffairsCounter=1;
    
        //This may be changed in the first line of the main file.
    private static PrintStream Output = System.out;
    
    public static void main(String[] args) throws Exception 
    {
        try
        {
                //comment this line if you want the result on System.out
            Output = new PrintStream(validationResultsFile);
            
                //Load the TimeOntology into "model", load the ABox and add it to model.
            Model TBox = ModelFactory.createDefaultModel();
            FileInputStream fisTimeOntology = new FileInputStream(TBoxFile);
            TBox.read(fisTimeOntology, "urn:dummy", FileUtils.langTurtle);
            fisTimeOntology.close();
            
            Property[] propertiesToConnectTheTwoProperIntervals = new Property[]
            {
                TBox.createProperty("http://www.w3.org/2006/time#intervalBefore"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalAfter"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalStarts"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalStartedBy"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalOverlaps"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalOverlappedBy"),                
                TBox.createProperty("http://www.w3.org/2006/time#intervalMeets"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalMetBy"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalFinishes"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalFinishedBy"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalDuring"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalContains"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalEquals"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalIn"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalDisjoint")
            };
            
                //Loading the inference rules
            Model rules = JenaUtil.createMemoryModel();
            FileInputStream fisRules = new FileInputStream(rulesFile);
            rules.read(fisRules, "urn:dummy", FileUtils.langTurtle);
            fisRules.close();
            
                //Load the shapes
            Model shapes = ModelFactory.createDefaultModel();
            FileInputStream fisShapes = new FileInputStream(shapesFile);
            shapes.read(fisShapes, "urn:dummy", FileUtils.langTurtle);
            fisShapes.close();
            
                //(1) This is the cycle that connects the two time:ProperInterval(s) in the same direction.
            for(Property p1:propertiesToConnectTheTwoProperIntervals)
            {
                for(Property p2:propertiesToConnectTheTwoProperIntervals)
                {
                        //We skip the cases in which the property is the same and when p1 and p2 are just swapped
                        //(to avoid putting them twice). We check the alphabetical order of their names.
                    if(p1.getLocalName().compareToIgnoreCase(p2.getLocalName())<=0)continue;
                    
                    Model ABox = ModelFactory.createDefaultModel();
                    ABox.setNsPrefixes(TBox.getNsPrefixMap());

                        //We generate two resources i1 and i2, we associate them with the xsd:dateTime values,
                        //and we connect them with p.
                    Resource pi1 = ABox.createResource(":pi1");
                    Resource pi2 = ABox.createResource(":pi2");
                    ABox.add(ABox.createStatement(pi1, p1, pi2));
                    ABox.add(ABox.createStatement(pi1, p2, pi2));

                        //We print the state of affairs, we validate it, and we print the result.
                    printStateOfAffairs(ABox);
                    
                    Model testModel = ModelFactory.createDefaultModel();
                    testModel.add(TBox);
                    testModel.add(ABox);

                        //Validation
                    ArrayList<String> validationErrors = validateStateOfAffairs(testModel, rules, shapes);
                    if(validationErrors.isEmpty()==true)Output.println("\tThe knowledge graph is valid.");
                    for(String error:validationErrors)Output.println("\t"+error);

                    Output.println();
                }
            }
            
                //(2) This is the cycle that connects the two time:ProperInterval(s) in opposite directions.
            for(Property p1:propertiesToConnectTheTwoProperIntervals)
            {
                for(Property p2:propertiesToConnectTheTwoProperIntervals)
                {
                        //We skip the cases in which p1 and p2 are just swapped (to avoid putting them twice). 
                        //Instead, contrary to the previous case, p1 and p2 can be the same property.
                    if(p1.getLocalName().compareToIgnoreCase(p2.getLocalName())<0)continue;

                    Model ABox = ModelFactory.createDefaultModel();
                    ABox.setNsPrefixes(TBox.getNsPrefixMap());

                        //We generate two resources i1 and i2, we associate them with the xsd:dateTime values,
                        //and we connect them with p.
                    Resource pi1 = ABox.createResource(":pi1");
                    Resource pi2 = ABox.createResource(":pi2");
                    ABox.add(ABox.createStatement(pi1, p1, pi2));
                    ABox.add(ABox.createStatement(pi2, p2, pi1));

                        //We print the state of affairs, we validate it, and we print the result.
                    printStateOfAffairs(ABox);
                    
                    Model testModel = ModelFactory.createDefaultModel();
                    testModel.add(TBox);
                    testModel.add(ABox);

                        //Validation
                    ArrayList<String> validationErrors = validateStateOfAffairs(testModel, rules, shapes);
                    if(validationErrors.isEmpty()==true)Output.println("\tThe knowledge graph is valid.");
                    for(String error:validationErrors)Output.println("\t"+error);

                    Output.println();
                }
            }
        }
        catch(Exception e)
        {
            Output.println("Exception: "+e.getMessage());
        }
    }
    
    private static ArrayList<String> validateStateOfAffairs(Model testModel, Model rules, Model shapes) throws Exception 
    {            
            //Validate & Print
        
            //We iteratively execute the rules until no further RDF triple is added to the model.
            //In the general case, this might loop infinitely, but not in the examples considered here. See paper for details.
        long lastSize = 0;
        while(testModel.size()>lastSize)
        {
            lastSize = testModel.size();
            testModel = RuleUtil.executeRules(testModel, rules, null, null).add(testModel);
        }
        
        Model report = ValidationUtil.validateModel(testModel, shapes, true).getModel();
        //System.out.println(org.topbraid.shacl.util.ModelPrinter.get().print(report));
        
            //We collect and return the validation errors, if any.
        ArrayList<String> validationErrors = new ArrayList<String>();
        Property resultMessage = shapes.createProperty("http://www.w3.org/ns/shacl#resultMessage");
        StmtIterator stmt = report.listStatements();
        while(stmt.hasNext())
        {
            Statement s = stmt.nextStatement();
            if(s.getPredicate().toString().compareToIgnoreCase("http://www.w3.org/ns/shacl#resultMessage")==0)
                validationErrors.add(s.getObject().toString());
        }
        
        return validationErrors;
    }
    
    private static void printStateOfAffairs(Model ABox)
    {
        Output.println("-------------------------------------------------------------------------------------------");
        Output.println("The state of affairs #"+stateOfAffairsCounter+" includes the following triples:\n");
        stateOfAffairsCounter++;

        StmtIterator iterator = ABox.listStatements();
        for(Statement s:iterator.toList())Output.println(s.toString());
        
        Output.println("\nRESULTS:");
    }
}
