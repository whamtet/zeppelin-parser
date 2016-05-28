(ns zeppelin-parser.core
  (:require [clojure.data.json :as json]
            [instaparse.core :as insta]
            [rhizome.viz :as viz]
            [clojure.java.shell :as shell]
            )
  (:import java.io.File
           java.awt.Desktop)
  (:gen-class)
  )

(def parse
  (insta/parser "
                start = prefix assignment *
                prefix = (!'val' wildcard) *
                assignment = val symbol (!val wildcard) +
                val = 'val' #'\\s+'
                wildcard = #'[\\w\\W]'
                symbol = #'\\w+'
                "))

(defn strip-comments [s]
  (if (.contains s "//")
    (.substring s 0 (.indexOf s "//"))
    s))

(defn clean-code [{text "text" :as m}]
  (if (and text (not (.startsWith (.trim text) "%")))
    (apply str
           (interpose "\n"
                      (map strip-comments (.split text "\n"))))))

(defn get-assignment [[_ _ [_ val] & assigment :as x]]
  (let [
         assigment (apply str (map second assigment))
         ]
    [val assigment]))

(defn get-assigments [s]
  (when (.contains s "val ")
    (when-not (every? first (map get-assignment (drop 2 (parse s)))) (println "bad" s (drop 2 (parse s))))
    (map get-assignment (drop 2 (parse s)))))

(defn value-map [f m]
  (zipmap (keys m) (map f (vals m))))

(defn parse-paragraphs [f]
  (mapcat get-assigments (filter identity (map clean-code ((json/read-str (slurp f)) "paragraphs")))))

(defn get-m [f]
  (let [
         assignments (into {} (parse-paragraphs f))
         symbols (keys assignments)
         _ (assert (every? identity symbols))
         symbol-assignments (value-map (fn [s] (filter #(.contains s %) symbols)) assignments)
         ]
    symbol-assignments))

(defn open [s]
  (.open (Desktop/getDesktop) (File. s)))

(defn open-graph [f]
  (let [m (get-m f)]
    (viz/save-graph (keys m) m
                      :node->descriptor (fn [n] {:label n})
                      :vertical? true
                      :filename "graph.png")
    (open "graph.png")))

(defn count-paragraph [{text "text"}]
  (if text (count (.split text "\n")) 0))

(defn count-lines [f]
  (apply + (map count-paragraph ((json/read-str (slurp f)) "paragraphs"))))

(defn -main [& [f]]
  (if f (open-graph f) (println "Usage: java -jar zeppelin-graph.jar notebook.json")))
