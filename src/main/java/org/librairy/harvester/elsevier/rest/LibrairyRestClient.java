package org.librairy.harvester.elsevier.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Predicate;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class LibrairyRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(LibrairyRestClient.class);

    public static final String API_HOST = "librairy.api.host";

    private final String baseUrl;

    public LibrairyRestClient(){
        this.baseUrl = "http://" + System.getProperty(API_HOST) + "/api";
    }

    public String createDomain(String id, String name) throws UnirestException, UnsupportedEncodingException {

        String url = baseUrl + "/domains/" + URLEncoder.encode(id,"UTF-8");

        HttpResponse<JsonNode> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .body("{ \"name\": \""+name+"\", \"description\": \"created from elsevier-harvester\"}")
                .asJson();

        if (response.getStatus() == 409){
            LOG.warn("Domain already exists: " + id);
        }else if (response.getStatus() != 201){
            throw new RuntimeException("Http error: " + response.getStatus());
        }else{
            LOG.info("created a new domain in librairy: '" + id + "'");
        }

        return response.getBody().toString();
    }

    public String createDocument(String id, String name, String content) throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/documents/" + URLEncoder.encode(id,"UTF-8");

        HttpResponse<String> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .body("{ \"name\": \""+StringEscapeUtils.escapeJson(name)+"\", \"content\": \""+StringEscapeUtils.escapeJson(content)+"\", \"language\": \"en\"}")
                .asString();

        if ((response.getStatus() == 409)){
            LOG.warn("Document already exists: " + id);
        }else if ((response.getStatus() != 200) && (response.getStatus() != 201)){
            throw new RuntimeException("Http error: " + response.getStatus());
        }else{
            LOG.info("created a new document in librairy: '" + id + "'");
        }

        return response.getBody();
    }

    public String annotateDocument(String docId, String annId, String annotation) throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/documents/" + URLEncoder.encode(docId,"UTF-8") + "/annotations/" + URLEncoder.encode(annId,"UTF-8");

        HttpResponse<String> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .body("{ \"value\": \""+StringEscapeUtils.escapeJson(annotation)+"\"}")
                .asString();

        if ((response.getStatus() != 200) && (response.getStatus() != 201)) throw new RuntimeException("Http error: " + response.getStatus());


        LOG.info("annotated document in librairy: '" + docId + "' with " + annId);
        return response.getBody();
    }

    public String createPart(String partId, String docId, String content) throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/parts/" + URLEncoder.encode(partId,"UTF-8");

        HttpResponse<String> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .body("{ \"name\": \""+partId+"\", \"content\": \""+StringEscapeUtils.escapeJson(content)+"\", \"language\": \"en\"}")
                .asString();

        if ((response.getStatus() == 409)){
            LOG.warn("Part already exists: " + partId);

        }else if ((response.getStatus() != 200) && (response.getStatus() != 201)) {
            throw new RuntimeException("Http error: " + response.getStatus());
        }else{
            addPartTodDocument(partId, docId);
            LOG.info("created a new part in librairy: '" + partId + "'");
        }

        return response.getBody();
    }

    public String addPartTodDocument(String partId, String documentId) throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/documents/" + URLEncoder.encode(documentId,"UTF-8") + "/parts/" + URLEncoder.encode(partId,"UTF-8");

        HttpResponse<String> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .asString();

        if ((response.getStatus() != 200) && (response.getStatus() != 201)) throw new RuntimeException("Http error: " + response.getStatus());

        LOG.info("added part: '" + partId + "' to document '"+documentId+"'  in librairy");
        return response.getBody();
    }


    public String addDocumentToDomain(String documentId, String domainId) throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/domains/" + URLEncoder.encode(domainId,"UTF-8") + "/documents/" + URLEncoder.encode(documentId,"UTF-8");

        HttpResponse<String> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .asString();

        if ((response.getStatus() != 200) && (response.getStatus() != 201)) throw new RuntimeException("Http error: " + response.getStatus());

        LOG.info("added document '"+documentId+"' to domain '"+domainId+"' in librairy");
        return response.getBody();
    }

}
