package net.gosecure.books.controller;

import com.sun.org.apache.bcel.internal.classfile.Code;
import net.gosecure.books.model.BookForm;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @RequestMapping("/")
    public ModelAndView index(@RequestParam(value = "message",defaultValue = "") String message) {
        ModelAndView view =  new ModelAndView("admin_index");
        if(message.equals("success")) {
            view.addObject("message", "The book was submit with success.");
        }

        return view;
    }


    @GetMapping(value = "/backup-1")
    public String backup1() throws Exception {
        return "Top Secret Backup 1";
    }

    @GetMapping(value = "/backup-2")
    public String backup2() throws Exception {
        return "Top Secret Backup 2";
    }

    @RequestMapping(value = "/upload",method = RequestMethod.GET)
    public ModelAndView uploadBooksGet() throws Exception {

        BookForm book = new BookForm();
        book.setTitle("New Book");
        book.setXmlCode(getDocBookTemplate());
        book.setTemplate("doc_book_preview.xsl");

        return new ModelAndView("admin_upload").addObject("bookForm", book);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView uploadBooksPost(@ModelAttribute BookForm bookForm, @RequestParam("action") String action) throws Exception {

        if("save".equals(action)) {
            return new ModelAndView("redirect:/admin/").addObject("message","success");
        }
        else {
            String htmlOut = transformXslt(bookForm.getXmlCode(), bookForm.getTemplate());
            bookForm.setOutHtml(htmlOut);
            return new ModelAndView("admin_upload");
        }
    }

    private String getDocBookTemplate() {
        InputStream inputStream = getClass().getResourceAsStream("/data/doc_book_sample.xml");

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(inputStream, writer, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    private String transformXslt(String xml,String templateId) throws TransformerException, FileNotFoundException {
        TransformerFactory factory = TransformerFactory.newInstance();

        //getClass().getResourceAsStream("/data/"+templateId)
        String folder = System.getProperty("resourceDir");
        if(folder == null) {
            String devPath = "src/main/resources/data/";
            if(new File(devPath).exists()) {
                folder = devPath;
            }
        }
        try (FileInputStream in = new FileInputStream(folder + "/" + templateId)) {
            Source xslt = new StreamSource(in);
            Transformer transformer = factory.newTransformer(xslt);

            Source text = new StreamSource(new StringReader(xml));
            StringWriter writer = new StringWriter();
            transformer.transform(text, new StreamResult(writer));

            return writer.toString();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

}
