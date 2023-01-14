import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

    public void convertCSVToJSON(String[] columnMapping, String fileName) {
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeToJSONFile(json, "data.json");
    }

    public void convertXMLToJSON(String fileName) throws IOException, ParserConfigurationException, SAXException {
        List<Employee> list = parseXML(fileName);
        String json = listToJson(list);
        writeToJSONFile(json, "data2.json");
    }

    private List<Employee> parseXML(String fileName) throws IOException, SAXException, ParserConfigurationException {
        List<Employee> list = new ArrayList<Employee>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        NodeList employees = doc.getElementsByTagName("employee");
        NodeList ids = doc.getElementsByTagName("id");
        NodeList firstNames = doc.getElementsByTagName("firstName");
        NodeList lastNames = doc.getElementsByTagName("lastName");
        NodeList countries = doc.getElementsByTagName("country");
        NodeList ages = doc.getElementsByTagName("age");

        for (int i = 0; i < employees.getLength(); i++) {
            long id = Long.parseLong(ids.item(i).getTextContent());
            String firstName = firstNames.item(i).getTextContent();
            String lastName = lastNames.item(i).getTextContent();
            String country = countries.item(i).getTextContent();
            int age = Integer.parseInt(ages.item(i).getTextContent());
            Employee employee = new Employee(id, firstName, lastName, country, age);
            list.add(employee);
        }

        return list;
    }

    private List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
            List<Employee> dataList = csv.parse();
            return dataList;
        } catch (IOException e) {
            return null;
        }
    }

    private String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private void writeToJSONFile(String json, String jsonFileName) {
        try (FileWriter file = new FileWriter(jsonFileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}