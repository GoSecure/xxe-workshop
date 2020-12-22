package com.h3xstream.webxxe.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.DOMException;

import java.io.*;

public class CovertImageAction extends ActionSupport {

    private InputStream inputStream;
    private String svg = "";

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String execute() {

        PNGTranscoder transcoder = new PNGTranscoder();

        TranscoderInput input = new TranscoderInput(new StringReader(svg));

        ByteArrayOutputStream imageResponseBytes = new ByteArrayOutputStream();
        try {

            TranscoderOutput output = new TranscoderOutput(imageResponseBytes);
            transcoder.transcode(input, output);

            inputStream = new ByteArrayInputStream(imageResponseBytes.toByteArray());
        } catch (TranscoderException e) {
            if(e.getException() instanceof DOMException) {
                try {
                    System.out.println(e.getMessage());
                    byte[] imageErrorBytes = ImageTextUtil.getImage("Unable to convert the image. " + e.getMessage());
                    inputStream = new ByteArrayInputStream(imageErrorBytes);
                } catch (IOException e1) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                throw new RuntimeException(e);
            }
        }
        return "success";
    }

    public void validate(){

    }
}
