# ATG Xref

Indexing, search, and cross-reference for the BestBuy.com ATG
codebase.

## Getting Started

First, run the Solr server that comes with this install:

    cd solr
    ./start_solr.sh

Now, index the ATG codebase from another terminal (Solr will take over
your first one.)

    ./bin/indexer ../bestbuy/dotcom/dgt

## Querying

There's a web UI that runs on your local machine:

    ./bin/webui

It runs standalone. You can access it at http://localhost:8080/

## Deployment Topology

I expect that the "server" will usually just be your own laptop or
workstation. This also means that the codebase itself should be
available on that host.


## TODO

* General
    * Create high-level "what uses?" page.

* Components
    * Graceful handling of dead-end links (components from ATG)

* Java classes
    * Graceful handling of "dead-end" classes (classes from ATG, Spring, etc.)

* JSP
    * Create JSP page, with syntax highlighting.
    * Look for bean references.
    * Display module name
    * Integrate Code-o-matic

* Locate Problems
    * JSP tag problems: missing start/end, duplicate start/end, too many start/end
    * Unreferenced JSPs. No "include" path from root page.
    * Same java package & class in multiple source files.

## License

Copyright (C) 2011 N6 Consulting LLC, All Rights Reserved
