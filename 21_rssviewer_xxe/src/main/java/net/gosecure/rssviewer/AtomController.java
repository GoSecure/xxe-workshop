package net.gosecure.rssviewer;

import net.gosecure.rssviewer.model.RssInputForm;
import net.gosecure.rssviewer.model.RssItem;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AtomController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtomController.class);

    @RequestMapping("/")
    public String index(final RssInputForm rssForm, final BindingResult bindingResult,Model model) {
        model.addAttribute("now", LocalDateTime.now());

        if(rssForm.getUrl() == null)
            rssForm.setUrl("https://www.reddit.com/r/netsec/.rss");

        model.addAttribute("rssForm", rssForm);

        return "index";
    }

    @RequestMapping("/preview")
    public String rssPreview(@RequestParam("url") String url, Model model) throws Exception {

        LOGGER.info("Load Atom feed: "+url);

        Document doc = loadDocumentFromRssUrl(url);

        model.addAttribute("rssItems", buildModel(doc));
        return "preview";
    }

    private static Document loadDocumentFromRssUrl(String rssUrl) throws Exception
    {
        URL url = new URL(rssUrl);
        validateUrl(url);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        try (InputStream in = conn.getInputStream()) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            return builder.parse(new InputSource(in));
        }
    }

    private static List<RssItem> buildModel(Document doc) {
        List<RssItem> items = new ArrayList<>();

        NodeList nodes = doc.getElementsByTagName("entry");
        for(int i=0; i<nodes.getLength() ; i++) {
            Node node = nodes.item(i);

            Element element = (Element) node;
            try {
                //Attempt to read ATOM format

                String title = element.getElementsByTagName("title").item(0).getTextContent();
                String url = element.getElementsByTagName("link").item(0).getAttributes().getNamedItem("href").getTextContent();
                String description = element.getElementsByTagName("content").item(0).getTextContent();

                //HTML sanitization
                PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS);
                String safeDescription = policy.sanitize(description);

                items.add(new RssItem(url, title, safeDescription));
            } catch (RuntimeException e) {
                //Invalid rss item (Missing information)
                LOGGER.warn("Invalid node ", e);
            }
        }


        nodes = doc.getElementsByTagName("item");
        for(int i=0; i<nodes.getLength() ; i++) {
            Node node = nodes.item(i);

            Element element = (Element) node;
            try {
                //Attempt to read RSS format

                String title = element.getElementsByTagName("title").item(0).getTextContent();
                String url = element.getElementsByTagName("link").item(0).getTextContent();
                String description = element.getElementsByTagName("description").item(0).getTextContent();

                //HTML sanitization
                PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS);
                String safeDescription = policy.sanitize(description);

                items.add(new RssItem(url, title, safeDescription));
            }
            catch (RuntimeException e){
                //Invalid rss item (Missing information)
                LOGGER.warn("Invalid node ",e);
            }

        }
        return items;
    }

    private static void validateUrl(URL url) throws UnknownHostException {
        if(!url.getProtocol().startsWith("http"))
            throw new RuntimeException();
        InetAddress inetAddress = InetAddress.getByName(url.getHost());
        if(inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isLinkLocalAddress())
            throw new IllegalStateException("localhost");
    }
}
