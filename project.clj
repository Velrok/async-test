(defproject async-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.0-SNAPSHOT"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [extra-time "0.1.1"]
                 [incanter "1.5.1"]]
  :profiles {:dev {:dependencies [[speclj "2.5.0"]]}}
  :main async-test.graphs
  :plugins [[speclj "2.5.0"]]
  :test-paths ["spec"])
