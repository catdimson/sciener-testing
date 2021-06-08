package news.di.container;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

@Deprecated
public class XmlBeansConfiguration {
    private String pathToConfig;
    private Document document;

    private void loadConfig() {
        File xmlFile = new File(pathToConfig);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            document = null;
        }
    }

    public void setPathAndLoad(String pathToConfig) {
        this.pathToConfig = pathToConfig;
        loadConfig();
    }

    public String getRelationClassFromCurrent(String currentClass) {
        String ref = "";
        String result = "";
        String findClass = "";

        if (document != null) {
            NodeList beanList = document.getElementsByTagName("bean");
            if (beanList.getLength() == 0) {
                return null;
            }
            for (int i = 0; i < beanList.getLength(); i++) {
                Element bean = (Element) beanList.item(i);
                if (bean.getAttribute("class").equals(currentClass)) {
                    findClass = currentClass;
                    Element constr = (Element) bean.getElementsByTagName("constructor-arg").item(0);
                    if (constr.hasAttribute("ref")) {
                        ref = constr.getAttribute("ref");
                    }
                    break;
                }
            }
            if (!findClass.equals("")) {
                for (int i = 0; i < beanList.getLength(); i++) {
                    Element bean = (Element) beanList.item(i);
                    if (bean.getAttribute("id").equals(ref)) {
                        result = bean.getAttribute("class");
                    }
                }
            } else {
                return null;
            }
            if (!ref.equals("") && result.equals("")) {
                return null;
            }
            return result;
        } else {
            return null;
        }
    }
}
