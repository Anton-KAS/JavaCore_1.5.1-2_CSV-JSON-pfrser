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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        // Задача 1. CSV to JSON
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        // Задача 2. XML to JSON
        String fileNameXML = "data.xml";
        List<Employee> list2 = parseXML(fileNameXML);
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");

    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReadwe = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReadwe)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> list = csv.parse();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List list) {
        GsonBuilder builder = new GsonBuilder();
        Gson json = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return json.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        List<Employee> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Map<String, String> valuesMap = new HashMap<>();
            Node node_ = nodeList.item(i);

            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                NodeList attrNodeList = node_.getChildNodes();

                for (int a = 0; a < attrNodeList.getLength(); a++) {
                    Node node__ = attrNodeList.item(a);
                    if (Node.ELEMENT_NODE == node__.getNodeType()) {
                        valuesMap.put(node__.getNodeName(), node__.getTextContent());
                    }
                }

            }

            if (valuesMap.size() > 0) {
                list.add(new Employee(
                        Long.parseLong(valuesMap.get("id")),
                        valuesMap.get("firstName"),
                        valuesMap.get("lastName"),
                        valuesMap.get("country"),
                        Integer.parseInt(valuesMap.get("age"))));
            }

        }
        return list;
    }
}
