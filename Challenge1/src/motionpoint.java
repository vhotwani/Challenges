import com.opencsv.CSVWriter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;




public class motionpoint {




        public static final String dobGreaterThanMiddleDate="dobGreaterThanMiddleDate.csv";
        public static final String dobLessThanMiddleDate="dobLessThanMiddleDate.csv";
        public static final String americanAfter1980="americanAfter1980.csv";
        public static void main(String[] args) {
            String url= "https://randomuser.me/api/?results=100&nat=us,de,gb";
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            try {
                JSONObject json= readJsonFromUrl(url);// fetching nested json objects from url
                JSONArray userList= (JSONArray) json.get("results");// fetching json objects in results and converting to json array
                ArrayList mainUserList= (ArrayList) userList.toList();
                ArrayList<HashMap> flattenedList=flattenJsonDataList(mainUserList);// using hashmap to get key and values
                Writer writer1 = Files.newBufferedWriter(Paths.get(dobGreaterThanMiddleDate)); // defining files and using csv writer libraray to write
                Writer writer2 = Files.newBufferedWriter(Paths.get(dobLessThanMiddleDate));
                Writer writer3 = Files.newBufferedWriter(Paths.get(americanAfter1980));
                CSVWriter csvWriter1 = new CSVWriter(writer1,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
                CSVWriter csvWriter2 = new CSVWriter(writer2,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
                CSVWriter csvWriter3 = new CSVWriter(writer3,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
                Object[] objs = flattenedList.get(0).keySet().toArray();
                String[] headers=new String[objs.length];
                for(int i=0;i<objs.length;i++){
                    headers[i]=(String)objs[i];
                }
                csvWriter1.writeNext(headers);
                csvWriter2.writeNext(headers);
                csvWriter3.writeNext(headers);
                csvWriter1.flush();
                csvWriter2.flush();
                csvWriter3.flush();
                DateTime middleDate = formatter.parseDateTime("1990-01-01 00:00:00");
                DateTime thirdPartDate = formatter.parseDateTime("1980-01-01 00:00:00");
                Boolean thirdPartDateFlag=false;
                for(HashMap hashMap:flattenedList){
                    Object[] userDetailsArray=hashMap.values().toArray();
                    String[] userDetailsRow=new String[userDetailsArray.length];
                    for(int i=0;i<userDetailsRow.length;i++){
                        try{
                            userDetailsRow[i]=(String)userDetailsArray[i];
                        }
                        catch (ClassCastException cce){
                            userDetailsRow[i]=userDetailsArray[i].toString();
                        }
                    }
                    String dob=(String) hashMap.get("dob");
                    DateTime dobDate = formatter.parseDateTime(dob);
                    if(dobDate.getMillis()<thirdPartDate.getMillis()){
                        thirdPartDateFlag=true;
                    }
                    if(dobDate.getMillis()<middleDate.getMillis()){
                        csvWriter2.writeNext(userDetailsRow);
                        csvWriter2.flush();
                    }
                    else {
                        csvWriter1.writeNext(userDetailsRow);
                        csvWriter1.flush();
                    }
                    String nationality=(String)hashMap.get("nat");
                    if(nationality.equals("US")&&thirdPartDateFlag) {
                        csvWriter3.writeNext(userDetailsRow);
                        thirdPartDateFlag=false;
                    }
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        } // method to make connection and geting json objecte
        public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }
        private static String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        } // here we flatten json data
        private static ArrayList<HashMap> flattenJsonDataList(ArrayList mainUserList){
            ArrayList<HashMap> newFlattenedList=new ArrayList();
            for(Object object: mainUserList) {
                HashMap map=(HashMap)object;
                HashMap temMap=new HashMap();
                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    try {
                        HashMap nestedMap = (HashMap) pair.getValue();
                        temMap.putAll(nestedMap);
                    }
                    catch (ClassCastException cce) {
                        temMap.putAll(map);
                    }
                    finally {
                        it.remove();
                    }
                }
                temMap.putAll(map);
                newFlattenedList.add(temMap);
            }
            return newFlattenedList;
        }


















    }
