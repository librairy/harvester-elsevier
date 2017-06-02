# Harvester Papers from ELSEVIER-API to librairy
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5e3e2fe9b1c242b6a4a13b7e6459b68e)](https://www.codacy.com/app/cbadenes/harvester-elsevier)
[![Build Status](https://travis-ci.org/librairy/harvester-elsevier.svg?branch=master)](https://travis-ci.org/librairy/harvester-elsevier)

Load research papers from a journal published at [Elsevier-API](https://dev.elsevier.com) into librairy system

## Get Started!

A prerequisite to consider is to have installed [Docker-Engine](https://docs.docker.com) in your system.

You can run this service by typing:

```sh
$ docker run -d --name harvesterElsevier \
 -e "ELSEVIER_API_KEY=<api-key>" \
 -e "LIBRAIRY_HOST=<server-ip>" \
 -e "RHETORICAL_ANALYSIS=<true or false>"  \
 -e "JOURNAL_NAME=<journal>"  \
 -e "NUM_PAPERS=<number>"  \
 -e "DOWNLOAD_PAPERS=<true or false>" \
 librairy/harvester-elsevier
```
That's all!! **harvester-elsevier** should be run in your system now uploading documents to **librairy**.

Remember that by using the flags: `-it --rm`, the services runs in foreground mode. Instead, you can deploy it in background mode as a domain service by using: `-d --restart=always`

This is a basic example:

```sh
$ docker run -it --rm --name harvesterElsevier \
 -e "ELSEVIER_API_KEY=9991" \
 -e "LIBRAIRY_HOST=localhost:8080" \
 -e "JOURNAL_NAME=Journal of Web Semantics"  \
 -e "NUM_PAPERS=5"  \
 librairy/harvester-elsevier
```

This is a more complex example which retrieves the rhetorical content from papers. It requieres more than 4G of dedicated memory:

```sh
$ docker run -it --rm --name harvesterElsevier \
 -e "ELSEVIER_API_KEY=9991" \
 -e "LIBRAIRY_HOST=localhost:8080" \
 -e "RHETORICAL_ANALYSIS=true"  \
 -e "JOURNAL_NAME=Journal of Web Semantics"  \
 -e "NUM_PAPERS=1500"  \
 -e "JAVA_OPTS=-Xmx6296m -Xms124m" \
 librairy/harvester-elsevier
```
