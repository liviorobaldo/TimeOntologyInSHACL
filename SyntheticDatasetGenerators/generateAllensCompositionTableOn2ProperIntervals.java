package SyntheticDatasetGenerators;
import java.util.*;
import java.io.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;

/* This file generates all possible combinations of two properties denoting Allen's temporal relations holding
between a sequence of three time:ProperInterval(s). It then applies the rules and print the composition table
in the output file.*/
public class generateAllensCompositionTableOn2ProperIntervals 
{
    private static File TBoxFile = new File("./TimeOntologySHACL/time.rdf");
    private static File rulesFile = new File("./TimeOntologySHACL/timeSHACLrules.shacl");
    private static File validationResultsFile = new File("./TimeOntologySHACL/validationResults.txt");
    
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
            
            Property[] propertiesDenotingAllensTemporalRelations = new Property[]
            {
                TBox.createProperty("http://www.w3.org/2006/time#intervalBefore"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalAfter"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalDuring"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalContains"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalOverlaps"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalOverlappedBy"),                
                TBox.createProperty("http://www.w3.org/2006/time#intervalMeets"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalMetBy"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalStarts"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalStartedBy"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalFinishes"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalFinishedBy"),
                TBox.createProperty("http://www.w3.org/2006/time#intervalEquals")
            };
            
                //Loading the inference rules
            Model rules = JenaUtil.createMemoryModel();
            FileInputStream fisRules = new FileInputStream(rulesFile);
            rules.read(fisRules, "urn:dummy", FileUtils.langTurtle);
            fisRules.close();
            
            for(Property p1:propertiesDenotingAllensTemporalRelations)
            {
                for(Property p2:propertiesDenotingAllensTemporalRelations)
                {
                    Model ABox = ModelFactory.createDefaultModel();
                    ABox.setNsPrefixes(TBox.getNsPrefixMap());

                        //We generate two resources i1 and i2, we associate them with the xsd:dateTime values,
                        //and we connect them with p.
                    Resource pi1 = ABox.createResource(":pi1");
                    Resource pi2 = ABox.createResource(":pi2");
                    Resource pi3 = ABox.createResource(":pi3");
                    ABox.add(ABox.createStatement(pi1, p1, pi2));
                    ABox.add(ABox.createStatement(pi2, p2, pi3));

                    Model testModel = ModelFactory.createDefaultModel();
                    testModel.add(TBox);
                    testModel.add(ABox);

                        //Validation
                    ArrayList<ArrayList<String>> results = inferredAllensTemporalRelationsBetweenTheExtremes(testModel, rules, p1, p2);
                    
                    String nameP1=p1.toString().substring(p1.toString().indexOf("#")+1, p1.toString().length());
                    String nameP2=p2.toString().substring(p2.toString().indexOf("#")+1, p2.toString().length());
                    Output.println("----------------------------------------------------------");
                    Output.println(nameP1+"+"+nameP2+":");
                    Output.println("\n\tFrom "+nameP1+"'s subject to "+nameP2+"'s object:");
                    for(String result:results.get(0))Output.println("\t\t"+result);
                    Output.println("\n\tFrom "+nameP2+"'s object to "+nameP1+"'s subject (opposite direction):");
                    for(String result:results.get(1))Output.println("\t\t"+result);
                    Output.println();
                }
            }
        }
        catch(Exception e)
        {
            Output.println("Exception: "+e.getMessage());
        }
    }

        //This method executes the rules then find the right reification of the conjoined path :pi1=p1=>:pi2=p2=>:pi3
        //and prints the property in :oneOf with that reification.
    private static ArrayList<ArrayList<String>> inferredAllensTemporalRelationsBetweenTheExtremes
    (
        Model testModel, Model rules, Property p1, Property p2
    ) throws Exception 
    {
        ArrayList<String> pi1topi3 = new ArrayList<String>();
        ArrayList<String> pi3topi1 = new ArrayList<String>();
        
            //We iteratively execute the rules until no further RDF triple is added to the model.
            //In the general case, this might loop infinitely, but not in the examples considered here. See paper for details.
        long lastSize = 0;
        while(testModel.size()>lastSize)
        {
            lastSize = testModel.size();
            testModel = RuleUtil.executeRules(testModel, rules, null, null).add(testModel);
        }

        Property rdfType = testModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource rdfStatement = testModel.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement");
        Property rdfSubject = testModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#subject");
        Property rdfObject = testModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#object");
        Property oneOf = testModel.createProperty("http://robaldoandbatsakis/timeontologyshacl#oneOf");
        Property rdfSource = testModel.createProperty("http://robaldoandbatsakis/timeontologyshacl#source");
        Property rdfSourcei = testModel.createProperty("http://robaldoandbatsakis/timeontologyshacl#sourcei");
        Property rdfSource1 = testModel.createProperty("http://robaldoandbatsakis/timeontologyshacl#source1");
        Property rdfSource2 = testModel.createProperty("http://robaldoandbatsakis/timeontologyshacl#source2");
        
        List<Resource> reifications=testModel.listSubjectsWithProperty(rdfType, rdfStatement).toList();
        for(Resource reification:reifications)
        {
            Resource subject = testModel.listObjectsOfProperty(reification, rdfSubject).toList().get(0).asResource();
            Resource object = testModel.listObjectsOfProperty(reification, rdfObject).toList().get(0).asResource();

            if((subject.toString().compareToIgnoreCase(":pi1")==0)&&(object.toString().compareToIgnoreCase(":pi3")==0))
            {
                Resource source1 = testModel.listObjectsOfProperty(reification, rdfSource1).toList().get(0).asResource();
                Resource source2 = testModel.listObjectsOfProperty(reification, rdfSource2).toList().get(0).asResource();
                Resource subject1 = testModel.listObjectsOfProperty(source1, rdfSubject).toList().get(0).asResource();
                Resource object1 = testModel.listObjectsOfProperty(source1, rdfObject).toList().get(0).asResource();
                Resource subject2 = testModel.listObjectsOfProperty(source2, rdfSubject).toList().get(0).asResource();
                Resource object2 = testModel.listObjectsOfProperty(source2, rdfObject).toList().get(0).asResource();                
               
                if
                (
                    (subject1.toString().compareToIgnoreCase(":pi1")==0)&&(object1.toString().compareToIgnoreCase(":pi2")==0)&&
                    (subject2.toString().compareToIgnoreCase(":pi2")==0)&&(object2.toString().compareToIgnoreCase(":pi3")==0)
                )
                {
                    List<RDFNode> nodes1 = testModel.listObjectsOfProperty(source1, rdfSource).toList();
                    List<RDFNode> nodes2 = testModel.listObjectsOfProperty(source2, rdfSource).toList();
                    if((nodes1.isEmpty()==false)&&(nodes2.isEmpty()==false))
                    {
                        Resource property_from_pi1_to_pi2 = nodes1.get(0).asResource();
                        Resource property_from_pi2_to_pi3 = nodes2.get(0).asResource();
                        if
                        (
                            (property_from_pi1_to_pi2.toString().compareToIgnoreCase(p1.toString())==0)&&
                            (property_from_pi2_to_pi3.toString().compareToIgnoreCase(p2.toString())==0)
                        )
                        {
                            List<RDFNode> possibleProperties = testModel.listObjectsOfProperty(reification, oneOf).toList();
                            
                            for(RDFNode possibleProperty:possibleProperties)
                            {
                                String name = possibleProperty.toString();
                                pi1topi3.add(name.substring(name.indexOf("#")+1, name.length()));
                            }
                        }
                    }
                }
            }
            
            if((subject.toString().compareToIgnoreCase(":pi3")==0)&&(object.toString().compareToIgnoreCase(":pi1")==0))
            {
                Resource source1 = testModel.listObjectsOfProperty(reification, rdfSource1).toList().get(0).asResource();
                Resource source2 = testModel.listObjectsOfProperty(reification, rdfSource2).toList().get(0).asResource();
                Resource subject1 = testModel.listObjectsOfProperty(source1, rdfSubject).toList().get(0).asResource();
                Resource object1 = testModel.listObjectsOfProperty(source1, rdfObject).toList().get(0).asResource();
                Resource subject2 = testModel.listObjectsOfProperty(source2, rdfSubject).toList().get(0).asResource();
                Resource object2 = testModel.listObjectsOfProperty(source2, rdfObject).toList().get(0).asResource();                
               
                if
                (
                    (subject1.toString().compareToIgnoreCase(":pi3")==0)&&(object1.toString().compareToIgnoreCase(":pi2")==0)&&
                    (subject2.toString().compareToIgnoreCase(":pi2")==0)&&(object2.toString().compareToIgnoreCase(":pi1")==0)
                )
                {
                    List<RDFNode> nodes1 = testModel.listObjectsOfProperty(source1, rdfSourcei).toList();
                    List<RDFNode> nodes2 = testModel.listObjectsOfProperty(source2, rdfSourcei).toList();
                    if((nodes1.isEmpty()==false)&&(nodes2.isEmpty()==false))
                    {
                        Resource property_from_pi3_to_pi2 = nodes1.get(0).asResource();
                        Resource property_from_pi2_to_pi1 = nodes2.get(0).asResource();
                        if
                        (
                            (property_from_pi3_to_pi2.toString().compareToIgnoreCase(p2.toString())==0)&&
                            (property_from_pi2_to_pi1.toString().compareToIgnoreCase(p1.toString())==0)
                        )
                        {                
                            List<RDFNode> possibleProperties = testModel.listObjectsOfProperty(reification, oneOf).toList();
                            for(RDFNode possibleProperty:possibleProperties)
                            {
                                String name = possibleProperty.toString();
                                pi3topi1.add(name.substring(name.indexOf("#")+1, name.length()));
                            }
                        }
                    }
                }
            }
        }

        ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
        ret.add(pi1topi3);
        ret.add(pi3topi1);
        return ret;
    }
}
