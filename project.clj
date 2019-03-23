(defproject
  cv
  "0.0.0-SNAPSHOT"
  :repositories
  [["clojars" {:url "https://repo.clojars.org/"}]
   ["maven-central" {:url "https://repo1.maven.org/maven2"}]]
  :dependencies
  [[onetom/boot-lein-generate "0.1.3"]
   [adzerk/boot-cljs "2.0.0" :scope "test"]
   [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
   [adzerk/boot-reload "0.5.2" :scope "test"]
   [pandeiro/boot-http "0.8.3" :scope "test"]
   [com.cemerick/piggieback "0.2.1" :scope "test"]
   [org.clojure/tools.nrepl "0.2.13" :scope "test"]
   [weasel "0.7.0" :scope "test"]
   [org.clojure/clojure "1.9.0"]
   [com.andrewmcveigh/cljs-time "0.5.2"]
   [org.clojure/clojurescript "1.10.339"]
   [crisptrutski/boot-cljs-test "0.3.0" :scope "test"]
   [reagent "0.8.1"]
   [binaryage/devtools "0.9.4" :scope "test"]
   [binaryage/dirac "1.2.9" :scope "test"]
   [powerlaces/boot-cljs-devtools "0.2.0" :scope "test"]]
  :source-paths
  ["src/cljs"]
  :resource-paths
  ["resources"])