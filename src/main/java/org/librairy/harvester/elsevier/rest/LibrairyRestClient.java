package org.librairy.harvester.elsevier.rest;

import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
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

    public String createItem(String id, String name, String content) throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/items/" + URLEncoder.encode(id,"UTF-8");

        HttpResponse<String> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .body("{ \"name\": \""+StringEscapeUtils.escapeJson(name)+"\", \"content\": \""+StringEscapeUtils.escapeJson(content)+"\", \"language\": \"en\"}")
                .asString();

        if ((response.getStatus() == 409)){
            LOG.warn("Item already exists: " + id);
        }else if ((response.getStatus() != 200) && (response.getStatus() != 201)){
            throw new RuntimeException("Http error: " + response.getStatus());
        }else{
            LOG.info("created a new item in librairy: '" + id + "'");
        }

        return response.getBody();
    }

    public String annotateItem(String docId, String annId, String annotation) throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/items/" + URLEncoder.encode(docId,"UTF-8") + "/annotations" ;

        HttpResponse<String> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .body(
                                "{ "+
                                "\"creator\": \"elsevier\","+
                                "\"description\": \"author keywords\","+
                                "\"format\": \"text\","+
                                "\"language\": \"en\","+
                                "\"purpose\": \"search keywords\","+
                                "\"type\": \""+annId+"\","+
                                "\"value\": {\"content\": \""+StringEscapeUtils.escapeJson(annotation)+"\"}"+
                                "}"
                )
                .asString();

        if ((response.getStatus() != 200) && (response.getStatus() != 201)) throw new RuntimeException("Http error: " + response.getStatus());


        LOG.info("annotated item in librairy: '" + docId + "' with " + annId);
        return response.getBody();
    }

    public JsonNode createPart(String partId, String itemId, String content) throws UnirestException,
            UnsupportedEncodingException {
        String url = baseUrl + "/parts";

        HttpResponse<JsonNode> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .body("{ \"name\": \""+partId+"\", \"content\": \""+StringEscapeUtils.escapeJson(content)+"\", \"language\": \"en\"}")
                .asJson();

        if ((response.getStatus() == 409)){
            LOG.warn("Part already exists: " + partId);

        }else if ((response.getStatus() != 200) && (response.getStatus() != 201)) {
            throw new RuntimeException("Http error: " + response.getStatus());
        }else{
            JSONObject result = response.getBody().getObject();
            String id = result.getString("id");
            addPartToItem(id, itemId);
            LOG.info("created a new part in librairy: '" + id + "'");
        }

        return response.getBody();
    }

    public String addPartToItem(String partId, String itemId) throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/items/" + URLEncoder.encode(itemId,"UTF-8") + "/parts/" + URLEncoder.encode(partId,
                "UTF-8");

        HttpResponse<String> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .asString();

        if ((response.getStatus() != 200) && (response.getStatus() != 201)) throw new RuntimeException("Http error: " + response.getStatus());

        LOG.info("added part: '" + partId + "' to item '"+itemId+"'  in librairy");
        return response.getBody();
    }


    public String addItemToDomain(String itemId, String domainId) throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/domains/" + URLEncoder.encode(domainId,"UTF-8") + "/items/" + URLEncoder.encode
                (itemId,"UTF-8");

        HttpResponse<String> response = Unirest.post(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .asString();

        if ((response.getStatus() != 200) && (response.getStatus() != 201)) throw new RuntimeException("Http error: " + response.getStatus());

        LOG.info("added item '"+itemId+"' to domain '"+domainId+"' in librairy");
        return response.getBody();
    }

    public String updateTopics(String domainId) throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/domains/" + URLEncoder.encode(domainId,"UTF-8") + "/topics";

        HttpResponse<String> response = Unirest.put(url)
                .headers(
                        ImmutableMap.of(
                                "Content-Type", "application/json",
                                "Accept","application/json"))
                .asString();

        if ((response.getStatus() != 200) && (response.getStatus() != 201)) throw new RuntimeException("Http error: " + response.getStatus());

        return response.getBody();
    }

}
