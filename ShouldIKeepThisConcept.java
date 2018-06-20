/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shouldikeepthisconcept;

/**
 *
 * @author Usama Rabbani
 */
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

class ConceptDetail {
        int firstPos = 0;
        String conceptID = null;
        String tokenOrgin = null;
        String fileName = null;
        String paragraph = null;
        public ConceptDetail(){
            
        }
        public ConceptDetail(int firstPos, String conceptID, String tokenOrgin, String fileName, String paragraph){
            this.conceptID = conceptID;
            this.fileName = fileName;
            this.firstPos = firstPos;
            this.paragraph = paragraph;
            this.tokenOrgin = tokenOrgin;
        }
        public void setFileName(String fileName){
            this.fileName = fileName;
        }
        public void printDetail(){
            System.out.println(firstPos + " " +conceptID+" "+ tokenOrgin+ " "+ fileName + " "+ paragraph);
        }
        public void setPara(String para){
            this.paragraph = para;
        }
    }
public class ShouldIKeepThisConcept {

    /**
     * @param args the command line arguments
     */
    private HashMap<String, ConceptDetail> hmById = new HashMap<String, ConceptDetail>();
    private HashMap<String, String> hmIdToFilePath = new HashMap<String, String>();
    private HashSet<Character> hs = new HashSet<Character>();
    private String conceptFileName = "Concepts.txt";
    private String mapToConceptFileName = "MapConceptToURL.txt";
    private String urlFileName = "URLPages.txt";
    public ShouldIKeepThisConcept(){
        for (int i = 0; i < 9; i++) {
            char temp = (char)('0' + i);
            hs.add(temp);
        }
        for (int i = 0; i < 26; i++) {
            char tempLowerCase = (char) ('a' + i);
            char tempUpCase = (char) ('A'+ i);
            hs.add(tempLowerCase);
            hs.add(tempUpCase);
            
        }
        hs.add('@');
        hs.add('.');
        hs.add(',');
        hs.add('-');
        hs.add('\'');
    }
    
    private void readConcept(){
        try {
            String line = null;
            System.out.println("-----Reading Concept Table----- ");
            FileReader fileReader =
                    new FileReader(this.conceptFileName);
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);
            // remove first row
            bufferedReader.readLine();
            while((line = bufferedReader.readLine()) != null) {
                String[] column = line.split("\t");
                //String i=column;
                
                this.hmById.put(column[0], new ConceptDetail(Integer.parseInt(column[7]), column[0], column[6], null, null));
                
   
            }
            hmById.remove("1");// remove #1 row wall street
            hmById.remove("2");// remove #2 row ieee
            bufferedReader.close();
            System.out.println("Done");
        } catch(FileNotFoundException ex) {
            ex.printStackTrace(); 
            
        }
        catch(IOException ex) {
            ex.printStackTrace();
        } 
    }
    
    private void readMapConceptToUrl(){
        try {
            String line = null;
            FileReader fileReader = new FileReader(mapToConceptFileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            System.out.println("-----Reading MapConceptToUrl Table------ ");
            // remove first row 
            bufferedReader.readLine();
            String oldID = null;
            while((line = bufferedReader.readLine()) != null) {
                String[] column = line.split("\t");
                String newID = column[0];
                if (!newID.equals(oldID)) {
                    if (hmById.containsKey(newID)) {
                        hmById.get(newID).setFileName(column[1]);
                        hmIdToFilePath.put(column[1], null);
                    }else{
//                        hmById.remove(newID);
//                        System.out.println("******************");
//                        System.out.println("Program cannot find this ID in MapConceptsToUrl: "+newID);
                    }
                    
                    oldID = newID;
                } 
            }
            bufferedReader.close();
            System.out.println("Done");
        } catch(FileNotFoundException ex) {
            ex.printStackTrace(); 
            
        }
        catch(IOException ex) {
            ex.printStackTrace();
        } 
    }
    private void readUrl() {
        FileReader fileReader = null;
        try {
            String line = null;
            fileReader = new FileReader(urlFileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            // remove first row
            System.out.println("------Reading Url Table------- ");
            bufferedReader.readLine();
            while((line = bufferedReader.readLine()) != null) {
                String[] column = line.split("\t");
                String fileID = column[0];
                String filePath = column[1];
                if (hmIdToFilePath.containsKey(fileID)) {
                    hmIdToFilePath.put(fileID, filePath);
                    //System.out.println(fileID + " "+ filePath);
                } 
            }
            // Always close files.
            bufferedReader.close(); 
            System.out.println("Done");
        } catch(FileNotFoundException ex) {
            ex.printStackTrace(); 
            
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    private void parseFile() {
        System.out.println("-----Parsing the files------");
            //Iterator<String> keySetIterator = hmById.keySet().iterator();
        String line = null;
        for(String key : hmById.keySet()){
            try {
                ConceptDetail ct = hmById.get(key);
                String fileId = ct.fileName;
                String fileName = hmIdToFilePath.get(fileId);
                FileReader freader =new FileReader(fileName);
                BufferedReader breader = new BufferedReader(freader);
                // remove first row 
                //String prev = null;
                int firstPos = ct.firstPos;
                String tokenOrign = ct.tokenOrgin;
                String[] keyWord = tokenOrign.split(" ");
                HashSet<String> findW = new HashSet<>();
                int counter=0;
                String para = "";
                while((line = breader.readLine()) != null) {
                    
                    if (keyWord.length == 1) {
                        if (line.contains(tokenOrign)) {
                            line= line +"\t"+ breader.readLine();
                            ct.setPara(line);
                            break;
                        }
                    }else {
                        
                        for (String y : keyWord) {
                            if (line.contains(y) && !findW.contains(y)) {
                                findW.add(y);
                                para= para + line+"\t";
                                ct.setPara(para);
                            }
                        }
                    }
                    //prev = line;
                    if (findW.size() == keyWord.length) {
                        //prev = null;
                        break;
                    }
                    
                    
                    
//                    char[] temp = line.toCharArray();
//                    //System.out.println(temp);
//
//                    for(int i = 0; i< temp.length; i++){
//                        char a = temp[i];
//                        boolean isContain = hs.contains(a);
//                        if (!isContain && a != 0xA && a != 0xD ) {
//                            counter++;
//                            //System.out.println("SSS "+ a);
//                        }
//                        if (isContain){
//                            int start = i+1;
//                            String word=""+ a;
//                            while(start < temp.length && hs.contains(temp[start])  ){
//                                word=word + temp[start];
//                                start++;
//                                i++;
//                            }
//                            counter++;
//                        }
//                      
//                        for(String y : keyWord){
//                            if (line.contains(y)) {
//                                ct.setPara(line);
//                            }
//                        }
//                        if (counter == firstPos) {
//                            System.out.println(firstPos + " "+ ct.tokenOrgin);
//                            System.out.println(line);
//                        }
//                    }
                }
                breader.close();
            } catch(FileNotFoundException ex) {
                ex.printStackTrace(); 

            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
            System.out.println("Done");
    }
    private void createOutputFile(){
        System.out.println("-----Create Output file-----");
        try {
            File file = new File("Output.txt");
            
            if (file.createNewFile()){
                System.out.println("Output.txt is created!");
            }else{
                System.out.println("File already exists.");
            }
            System.out.println("-----Writing to Output file-----");
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String id : hmById.keySet()) {
                ConceptDetail cd = hmById.get(id);
                String detail =cd.conceptID + "\t"+cd.tokenOrgin+"\t"+cd.paragraph+"\n";
                bw.write(detail);
            }
            bw.close();
            System.out.println("Done!");
        } catch(FileNotFoundException ex) {
            ex.printStackTrace(); 
            
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    public HashMap<String, ConceptDetail> getHmById() {
        return hmById;
    }

    public void setHmById(HashMap<String, ConceptDetail> hmById) {
        this.hmById = hmById;
    }

    public HashMap<String, String> getHmIdToFilePath() {
        return hmIdToFilePath;
    }

    public void setHmIdToFilePath(HashMap<String, String> hmIdToFilePath) {
        this.hmIdToFilePath = hmIdToFilePath;
    }
    
    public static void main(String[] args){
        HashMap<String, ConceptDetail> keep = new HashMap<>();
        HashMap<String, ConceptDetail> remove = new HashMap<>();
        HashSet<String> scanned = new HashSet<>();
        ShouldIKeepThisConcept test = new ShouldIKeepThisConcept();
        File f = new File("Output.txt");
        if (!f.exists()) {
            test.readConcept();
            test.readMapConceptToUrl();
            test.readUrl();
            test.parseFile();
            test.createOutputFile();
        }
        try {
            FileReader freader = new FileReader(f);
            BufferedReader breader = new BufferedReader(freader);
            ConceptDetail cd;
            String line = null;
            while((line = breader.readLine()) != null) {
                String[] column = line.split("\t");
                String paragraph = "";
                for (int i = 2; i < column.length; i++) {
                    paragraph= paragraph + column[i] + " ";
                }
                cd = new ConceptDetail(0, column[0], column[1], null, paragraph );
                
                System.out.println("TokenOrign : "+ cd.tokenOrgin);
                System.out.println("Source : ");
                System.out.println(cd.paragraph);
                System.out.println();
                System.out.println("--Enter 1 to keep the concept--");
                System.out.println("--Enter 2 to remove the concept--");
                System.out.println("--Enter 0 to save files and quit--");
                System.out.print("Should I keep this: ");
                Scanner sc = new Scanner(System.in);
                while ( !sc.hasNextInt() ){
                    System.out.print("Invaid Input!!! Re-Enter :");
                    sc.next();
                }
                int k = sc.nextInt();
                if (k == 1){
                    System.out.println("-------------------");
                    System.out.println("Keep the concept");
                    System.out.println();
                    keep.put(column[0], cd);
                }else if (k == 2) {
                    System.out.println("-------------------");
                    System.out.println("Remove the concept");
                    System.out.println();
                    remove.put(column[0],cd);
                }else if ( k == 0) {
                    System.out.println("------Saving -----");
                    File file = new File("Keep.txt");
            
                    if (file.createNewFile()){
                        System.out.println("Keep.txt is created!");
                    }else{
                        System.out.println("keep.txt already exists.");
                    }
                    FileWriter fw = new FileWriter(file, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    for (String id : keep.keySet()) {
                        ConceptDetail info = keep.get(id);
                        String detail =info.conceptID + "\t"+info.tokenOrgin+"\t"+info.paragraph+"\n";
                        bw.write(detail);
                    }
                    bw.close();
                    //System.out.println("------Saving -----");
                    file = new File("Remove.txt");
            
                    if (file.createNewFile()){
                        System.out.println("Remove.txt is created!");
                    }else{
                        System.out.println("Remove.txt already exists.");
                    }
                    fw = new FileWriter(file, true);
                    bw = new BufferedWriter(fw);
                    for (String id : remove.keySet()) {
                        ConceptDetail info = remove.get(id);
                        String detail =info.conceptID + "\t"+info.tokenOrgin+"\t"+info.paragraph+"\n";
                        bw.write(detail);
                    }
                    bw.close();
                    
                    
                    break;
                    

                }
                scanned.add(column[0]);

                
                System.out.println();
                //hmById.put(column[0], new ConceptDetail(Integer.parseInt(column[7]), column[0], column[6], null, null));    
            }
            File inputFile = new File("Output.txt");   // Your file  
            File tempFile = new File("myTempFile.txt");// temp file

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                String[] s = currentLine.split("\t");
                if(scanned.contains(s[0])) continue;
                writer.write(currentLine+"\n");
            }
            reader.close();
            writer.close();
            boolean successful = tempFile.renameTo(inputFile);
            if (successful) {
                System.out.println("Output.txt is updated!");
                System.out.println("--------Save Done------");
            }else{
                System.out.println("Error in Saving Output.txt file");
            }
            
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
            
            
        }
        catch(IOException ex) {
            ex.printStackTrace();
            
        }
        
    }
        
    
}
