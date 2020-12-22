package net.gosecure.books.model;

public class BookForm {

    private String title;
    private String xmlCode;
    private String template;
    private String outHtml = "";

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getXmlCode() {
        return xmlCode;
    }
    public void setXmlCode(String xmlCode) {
        this.xmlCode = xmlCode;
    }

    public String getTemplate() {
        return template;
    }
    public void setTemplate(String template) {
        this.template = template;
    }

    public String getOutHtml() {
        return outHtml;
    }
    public void setOutHtml(String outHtml) {
        this.outHtml = outHtml;
    }
}
