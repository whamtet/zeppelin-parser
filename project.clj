(defproject zeppelin-parser "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [rhizome "0.2.5"]
                 [instaparse "1.4.2"]
                 ]
  :profiles {:uberjar {:aot :all}}
  :main zeppelin-parser.core
  :local-repo "repo"
  )
