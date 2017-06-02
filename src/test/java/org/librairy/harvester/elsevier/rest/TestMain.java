package org.librairy.harvester.elsevier.rest;

import com.google.common.base.Strings;
import org.librairy.harvester.elsevier.service.JournalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class TestMain {

    private static final Logger LOG = LoggerFactory.getLogger(TestMain.class);

    public static void main(String[] args) throws Exception {

        System.setProperty(ElsevierRestClient.API_HOST, "api.elsevier.com");
        String librairyHost = "minetur.dia.fi.upm.es:9999";
        System.setProperty(LibrairyRestClient.API_HOST, librairyHost);
        //System.setProperty(LibrairyRestClient.API_HOST, "zavijava.dia.fi.upm.es:8180");
//        System.setProperty(LibrairyRestClient.API_HOST, "librairy.linkeddata.es");


        System.setProperty("DRIconf","src/test/resources/DRIconfig.properties");
        Boolean rethoricalService = false;
        JournalService journalService = new JournalService(rethoricalService);


        Instant start = Instant.now();

        String journalName  = "Journal of Web Semantics";
        Integer maxDocs     = 50;
        Boolean downloadPapers = false;

        journalService.loadJournal(journalName, maxDocs, downloadPapers);

        Instant end = Instant.now();

        LOG.info("Elsevier Corpus created in: "       + ChronoUnit.MINUTES.between(start,end) + "min " + (ChronoUnit.SECONDS.between(start,end)%60) + "secs");


    }
}
