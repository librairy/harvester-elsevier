package org.librairy.harvester.elsevier;

import com.google.common.base.Strings;
import org.librairy.harvester.elsevier.rest.ElsevierRestClient;
import org.librairy.harvester.elsevier.rest.LibrairyRestClient;
import org.librairy.harvester.elsevier.service.JournalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
//@EnableAutoConfiguration
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static final String banner = "\n" +
            "                    ┬  ┬┌┐ ┬─┐╔═╗╦┬─┐┬ ┬                  \n" +
            "                    │  │├┴┐├┬┘╠═╣║├┬┘└┬┘                  \n" +
            "                    ┴─┘┴└─┘┴└─╩ ╩╩┴└─ ┴                   \n" +
            "┬ ┬┌─┐┬─┐┬  ┬┌─┐┌─┐┌┬┐┌─┐┬─┐       ┌─┐┬  ┌─┐┌─┐┬  ┬┬┌─┐┬─┐\n" +
            "├─┤├─┤├┬┘└┐┌┘├┤ └─┐ │ ├┤ ├┬┘  ───  ├┤ │  └─┐├┤ └┐┌┘│├┤ ├┬┘\n" +
            "┴ ┴┴ ┴┴└─ └┘ └─┘└─┘ ┴ └─┘┴└─       └─┘┴─┘└─┘└─┘ └┘ ┴└─┘┴└─\n" +
            "                                            \n" +
            "\n" +
            "                      cbadenes@fi.upm.es\n" +
            "                       ocorcho@fi.upm.es\n" +
            "                  Ontology Engineering Group\n" +
            "                             2017\n";



    public static void main(String[] args) throws Exception {

        System.out.println(banner);

        // Read environment variables
        String librairyHostEnv         = System.getenv("LIBRAIRY_HOST");
        String rhetoricalAnalsysEnv    = System.getenv("RHETORICAL_ANALYSIS");
        String journalNameEnv          = System.getenv("JOURNAL_NAME");
        String numPapersEnv            = System.getenv("NUM_PAPERS");
        String downloadPapersEnv       = System.getenv("DOWNLOAD_PAPERS");


        System.setProperty(ElsevierRestClient.API_HOST, "api.elsevier.com");

        String librairyHost = !Strings.isNullOrEmpty(librairyHostEnv)? librairyHostEnv : "localhost:8080";

        System.setProperty(LibrairyRestClient.API_HOST, librairyHost);


        Boolean rhetoricalService = !Strings.isNullOrEmpty(rhetoricalAnalsysEnv)? Boolean.valueOf(rhetoricalAnalsysEnv) : false;
        JournalService journalService = new JournalService(rhetoricalService);


        Instant start = Instant.now();
//        Lists.newArrayList(
////                "International Immunopharmacology",     // Pharma
////                "Evolution and Human Behavior",         // Arts and Humanities
////                "Economic Modelling",                   // Economics
////                "Advances in Space Research",           // Earth and Planetary Sciences
////                "Advances in Engineering Software",     // Computer Science
////                "Advances in Applied Mathematics"      // Mathematics
////                "Journal of Power Sources",
//                "Journal of Web Semantics"
//        ).parallelStream().forEach(journal -> journalService.loadJournal(journal, 5, false));

        String journalName = !Strings.isNullOrEmpty(journalNameEnv)? journalNameEnv : "Journal of Web Semantics";
        Integer maxDocs     = !Strings.isNullOrEmpty(numPapersEnv)? Integer.valueOf(numPapersEnv) : 5;
        Boolean downloadPapers = !Strings.isNullOrEmpty(downloadPapersEnv)? Boolean.valueOf(downloadPapersEnv) : false;


        StringBuilder summary = new StringBuilder("Harvesting Summary:\n");
        summary.append("- Journal: ").append(journalName).append("\n");
        summary.append("- Num Papers: ").append(maxDocs).append("\n");
        summary.append("- Download: ").append(downloadPapers).append("\n");
        summary.append("- Rhetorical Analysis: ").append(rhetoricalService).append("\n");
        summary.append("- Librairy Host: ").append(librairyHost).append("\n");


        LOG.info(summary.toString());

        journalService.loadJournal(journalName, maxDocs, downloadPapers);

        Instant end = Instant.now();

        LOG.info("Elsevier Corpus created in: "       + ChronoUnit.MINUTES.between(start,end) + "min " + (ChronoUnit.SECONDS.between(start,end)%60) + "secs");


    }
}
