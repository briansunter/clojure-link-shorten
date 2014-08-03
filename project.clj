(defproject link-shorten "1.0.0"
  :description "A simple link shortener written in Clojure"
  :url "https://github.com/brsunter/clojure-link-shorten"
  :license {:name "MIT Licenese"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.nrepl "0.2.3"]
                 [compojure "1.1.8"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [ring/ring-devel "1.2.2"]
                 [ring-basic-authentication "1.0.5"]
                 [com.cemerick/drawbridge "0.0.6"]
                 [environ "0.5.0"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [korma "0.3.2"]
                 [postgresql "9.1-901.jdbc4"]
                 [hiccup "1.0.5"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.2.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "link-shorten-standalone.jar"
  :main ^:skip-aot link-shorten.web)
