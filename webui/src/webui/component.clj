(ns webui.component
  (:use webui.nav
        webui.search))

(defn component-link [m] {:link (str "/component/" (.get m "component")) :name (.get m "component")})

(defn components-named [pat] (map #(into {} %) (solr-query (str "component:" pat))))

(defn components-crumbs [] (conj (home-crumbs) (crumb "/components" "Components")))

(defn component-crumbs [comp] (conj (components-crumbs) (crumb (str "/component/" comp) comp)))

(defn components-page [] (view/components (map component-link (take 35 (components-named "*")))))

(defn component-page [comp] (view/component {:name comp :component-defs (components-named comp)}))