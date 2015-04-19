;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; This is a simple program to read the cards found in the input line by line.  We just accumulate all the cards in
;; appropriate lists.  We then check the validity of the hints coming in and perform the appriate action of adding
;; or removing cards accordingly.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns eighthlight.core
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:use matchure))

(use ['clojure.string :only '(split)])

(use 'clojure.java.io)

(def discards  (ref ()))
(def rocky     (ref ()))
(def danny     (ref ()))
(def lil       (ref ()))
(def shady     (ref ()))

(defn seq-contains?
  [coll target] (some #(= target %) coll))


(defn add-card [set card]
  (dosync (alter set conj card)))

(defn remove-card [set card]
  (let [tmp (filter #(if (not= card %) %) @set)]
     (dosync (ref-set set tmp))))

(defn process-card [set card]
  (cond
   (and (.startsWith card "+") (not (.contains card "?"))
        (not (seq-contains? @set (clojure.string/replace card #"[-\+]" ""))))
         (add-card set (clojure.string/replace card #"\+" ""))
   (and (.startsWith card "-") (not (.contains card "?")))
     (remove-card set (clojure.string/replace card #"-" ""))))

(defn build-set [set array]
  (doseq [item array]
    (let [tmp (split item #":")]
    (if-match [[?fst & ?rst] tmp]
              (if (some #(= "discard" %) rst)
                (do (process-card discards (clojure.string/replace fst #"-" "+"))
                    (process-card set fst))
                (process-card set fst))))))

(defn build-set-for-lil [set array]
  (build-set set array)
  (apply println (reverse @lil))
  (let [dcv (into [] @discards)
        lv  (into [] @lil)
        tmp (filter #(not (seq-contains? dcv %)) lv)]
     (dosync (ref-set set tmp))))

(defn valid-signal? [person card]
  (let [cleaned (clojure.string/replace card #"[\+-]" "")]
    (cond
     (and (.startsWith card "+") person)
       (case person
        "Shady" (not (.contains @discards cleaned))
        "Rocky" (and (not (.contains @discards cleaned)) (.contains @rocky cleaned))
        "Danny" (not (.contains @discards cleaned)))
     (and (.startsWith card "-") person)
       (and (not (.contains @discards cleaned)) (not (.contains @rocky cleaned))  (.contains @lil cleaned))
     (and (.startsWith card "+") (not person))
       (not (or (.contains @discards cleaned)  (.contains @rocky cleaned)
                (.contains @danny cleaned)     (.contains @shady cleaned)))
     (and (.startsWith card "-") (not person))
       (and (not (.contains @discards cleaned)) (.contains @lil cleaned)))))


(defn process-card-for [person card]
  (if (.startsWith card "+")
    (let [flip (clojure.string/replace card #"\+" "-")]
      (case person
        "Shady" (process-card shady flip)
        "Rocky" (process-card rocky flip)
        "Danny" (process-card danny flip)))
    (let [flip (clojure.string/replace card #"-" "+")]
      (case person
        "Shady" (process-card shady flip)
        "Rocky" (process-card rocky flip)
        "Danny" (process-card danny flip)))))

(defn process-signals [array]
  (doseq [item array]
    (let [arr (split item #":")
          card (first arr)
          person (second arr)]
      (if (valid-signal? person card)
        (cond
           person (do (process-card-for person card)
                      (process-card lil card))
           :else  (process-card lil card)))))
  (apply println (reverse @lil)))

(defn solution [file]
  (with-open [rdr (reader file)]
    (doseq [line (line-seq rdr)]
      (if-match [["Shady"   & ?rst] (split line #" ")] (build-set shady rst))
      (if-match [["Rocky"   & ?rst] (split line #" ")] (build-set rocky rst))
      (if-match [["Danny"   & ?rst] (split line #" ")] (build-set danny rst))
      (if-match [["discard" & ?rst] (split line #" ")] (build-set discards rst))
      (if-match [["Lil"     & ?rst] (split line #" ")] (build-set-for-lil lil rst))
      (if-match [["*"       & ?rst] (split line #" ")] (process-signals rst)))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Running Main setup
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def cli-options
  [["-f" "--file PATH" "Path to file to read in the card game"
    :default "files/SAMPLE_INPUT.txt"
    :parse-fn #(str %)
    :validate [#(.exists (as-file %)) "File must exist"]]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["This is to run the card game as describe in the 5-counting-cards.md."
        ""
        "Usage: lein run -f <path to file>"
        ""]
       (clojure.string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (clojure.string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main
  "resolve the card game."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    ;; Handle help and error conditions
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count options) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;;(println options) ;; for debug
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      (solution (:file options))))
