package org.librairy.harvester.elsevier.rest;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Ignore
public class LibrairyRestClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(LibrairyRestClient.class);
    private LibrairyRestClient client;

    @Before
    public void setup(){
        //System.setProperty(LibrairyRestClient.API_HOST, "zavijava.dia.fi.upm.es:8180");
        System.setProperty(LibrairyRestClient.API_HOST, "localhost:8080");
        this.client = new LibrairyRestClient();
    }

    @Test
    public void createDomain() throws UnirestException, UnsupportedEncodingException {

        String response = this.client.createDomain("dom1","sample-domain");
        LOG.info("response: " + response);

    }

    @Test
    public void createDocument() throws UnirestException, UnsupportedEncodingException {

        String response = this.client.createDocument("2-s2.0-85009384684","no-name","content from one");
        LOG.info("response: " + response);

    }

    @Test
    public void createPart() throws UnirestException, UnsupportedEncodingException {

        String response = this.client.createPart("part1", "doc1", "content from one");
        LOG.info("response: " + response);
    }


    @Test
    public void annotateDocument() throws UnirestException, UnsupportedEncodingException {

        String response = this.client.annotateDocument("doc1", "keywords", "content from one");
        LOG.info("response: " + response);
    }


    @Test
    public void addPartToDocument() throws UnirestException, UnsupportedEncodingException {

        String response = this.client.addPartTodDocument("part1", "doc1");
        LOG.info("response: " + response);
    }

    @Test
    public void addDocumentToDomain() throws UnirestException, UnsupportedEncodingException {

        String response = this.client.addDocumentToDomain("doc1", "dom1");
        LOG.info("response: " + response);
    }


}
