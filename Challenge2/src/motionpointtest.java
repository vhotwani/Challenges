import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlMeta;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;





public class motionpointtest {
    public static final String webScrappingResultsCSV="webscrappingresultscsv.csv"; // declaring csv file name
    public static HashSet<String> uniqueURLs=new HashSet();
    public static final String rootURL="https://www.motionpoint.com/";
    public static void main(String[] args){
        try{
            Writer writer1 = Files.newBufferedWriter(Paths.get(webScrappingResultsCSV)); //  
            CSVWriter csvWriter1 = new CSVWriter(writer1,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            extractAndWriteFromURL(rootURL,csvWriter1,4);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    public static void extractAndWriteFromURL(String searchUrl,CSVWriter csvWriter1,int noOfURLS){
        if(noOfURLS!=0){
            uniqueURLs.add(searchUrl);
            WebClient client = new WebClient();
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
            try {
                String[] headers=new String[]{new String("url"),new String("title"),new String("meta-description"),new String("meta-extracted-length")};
                csvWriter1.writeNext(headers);
                HtmlPage page = client.getPage(searchUrl);
                String title= page.getTitleText();
                List<?> items =  page.getByXPath("//meta[@name=\"description\"]") ;
                HtmlMeta meta= (HtmlMeta) items.get(0);
                String metadata= meta.getContentAttribute();
                String meta_content_length=(title.length()+metadata.length())+"";
                String[] contents=new String[]{new String(searchUrl),new String(title),new String(metadata),new String(meta_content_length)};
                List<?> anchorTagHrefs =  page.getByXPath("//a/@href") ;
                for(Object object: anchorTagHrefs){
                    DomAttr hrefAttribute=(DomAttr)object;
                    String hrefValue= hrefAttribute.getValue();
                    if(hrefValue.length()>rootURL.length()&&!uniqueURLs.contains(hrefValue)){
                        searchUrl=hrefValue;
                        break;
                    }
                }
                csvWriter1.writeNext(contents);
                csvWriter1.flush();
                extractAndWriteFromURL(searchUrl,csvWriter1,noOfURLS-1);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }




}
