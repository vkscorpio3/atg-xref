(ns indexer.core
  (:use atg.module
        indexer.java
        [clojure.contrib.io :only (as-file)])
  (:require [clojure.contrib.str-utils2 :as str]
            [clojure.contrib.logging :as log])
  (:import [java.io File FileNotFoundException]
           [org.apache.solr.client.solrj SolrServer]
           [org.apache.solr.common SolrInputDocument]
           [org.apache.solr.client.solrj.impl CommonsHttpSolrServer])
  (:gen-class))

(def *solr-server*)

(defn initialize-solr
  [url]
  (CommonsHttpSolrServer. url))

(defmacro with-connection
  [solr-url & body]
  `(binding [*solr-server* (initialize-solr ~solr-url)]
     ~@body))

(def nilsafe-split (fnil str/split ""))

(defn map->solr-input
  [m]
  (let [doc (SolrInputDocument.)]
    (doseq [[k v] m]
      (if-not (nil? v)
        (if (coll? v)
          (doseq [subval v]
            (.addField doc (name k) subval))
          (.addField doc (name k) v))))
    doc))

(defn document-for-source
  [mod src]
  (for [[cls body] (:typedecls src)]
    {:id (str (:qname mod) ":" (:path src))
     :name cls
     :module (:qname mod)
     :package (:package src)
     :classname cls
     :source body
     :references (:typerefs src)}))

(defn sources-seq [m] (for [s (source-files m)] (summary-info s)))

(defn source-documents [m] (map #(document-for-source m %) (sources-seq m)))

(defn document-for-component
  [mod sect compf]
  (let [comp (parse-component mod sect compf)]
    {:id (str (:qname mod) ":" (:section comp) ":" (:name comp))
     :module (:qname mod)
     :component (:name comp)
     :instantiates (:$class comp)
     :scope (:$scope comp)
     :body (:body comp)
     :source (:path comp)
     :references (:references comp)}))

(defn component-documents
  [m]
  (flatten
   (for [sect ["config" "liveconfig" "cacheconfig"]]
     (map #(document-for-component m sect %) (component-names m sect)))))

(defn document-for-module
  [manifest]
  {:id (:qname manifest)
   :name (:qname manifest)
   :product (:ATG-Product manifest)
   :required (nilsafe-split (:ATG-Required manifest) #" ")
   :classpath (nilsafe-split (:ATG-Class-Path manifest) #" ")
   :configpath (nilsafe-split (:ATG-Config-Path manifest) #" ")})

(defn module-documents [m] (list (document-for-module m)))

(defn index-documents
  "Index a sequence of solr documents generated by f"
  [f]
  (doseq [d (f)] (.add *solr-server* (map->solr-input d))))

(defn index-modules
  [ms]
  (doseq [m ms]
    (index-documents #(module-documents m))
    (index-documents #(component-documents m))
    (index-documents #(source-documents m)))
  (.commit *solr-server*))

(defn -main [root]
  (with-connection "http://localhost:8983/solr"
    (index-modules (load-modules root))))
