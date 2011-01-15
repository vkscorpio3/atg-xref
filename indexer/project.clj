(defproject atg-xref-indexer "1.0.0-SNAPSHOT"
  :description "Indexer for ATG codebase cross-reference."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojars.kjw/solrj "1.4.0"]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :main indexer.core)
