(ns link-shorten.models.link
  (:require
   [environ.core :refer [env]]
   [korma.db :refer :all]
   [ring.util.codec :refer [base64-encode]]
   [korma.core :refer :all]
   [clojure.set :refer [map-invert]]
   [link-shorten.models.db :refer [init-db]]))

(defn generate-unicode-characters []
  (let [ce (-> "UTF-8" java.nio.charset.Charset/forName .newEncoder)]
    (->> (range 0 (int Character/MAX_VALUE)) (map char)
         (filter #(and (.canEncode ce %) (Character/isLetter %))))))

(def unicodes  (take 45000 (generate-unicode-characters)))


(defn app-host [] (if-let [host (env :domain)]
                    host
                    "http://localhost:5000"))

(defentity links)

(def conversion-table
  (zipmap
   unicodes
   (range)))

(defn base-n-to-base-10
  [^String string ^Integer base]
  (let [string (clojure.string/upper-case string)]
    (assert (every? #(< (conversion-table %) base) string))
    (loop [num string
           acc 0]
      (if (seq num)
        (recur (drop 1 num) (+ (* base acc) (get conversion-table (first num))))
        acc))))

(defn base-10-to-base-n
  [^Integer number ^Integer base]
  (loop [num number
         acc []]
    (if (zero? num)
      (clojure.string/join (reverse acc))
      (recur (int (/ num base))
             (conj acc (nth unicodes (mod num base)))))))

(defn base-10-to-base-n
  [^Integer number ^Integer base]
  (loop [num number
         acc []]
    (if (zero? num)
      (clojure.string/join (reverse acc))
      (recur (int (/ num base))
             (conj acc (nth unicodes (mod num base)))))))

(defn encode-id [number-to-encode]
  (base-10-to-base-n number-to-encode (count unicodes)))

(defn decode-id [number-to-decode]
  (base-n-to-base-10 number-to-decode (count unicodes)))

(defn full-link-from-encoded-id
  [encoded-id]
  (str (app-host) "/" encoded-id))

(defn add-link-with-url [url]
  (let [link  (insert links (values {:url url}))
        encoded-id (encode-id  (link :id))]
    (full-link-from-encoded-id encoded-id)))

(defn link-from-encoded-id [encoded-id]
  (let [actual-id (decode-id encoded-id)]
    (first (select links (where {:id  actual-id })))))

(defn url-from-encoded-id [encoded-id]
  (let [link-from-url (link-from-encoded-id encoded-id)]
    (if-not (nil? link-from-url)
      (link-from-url :url))))


(defn starts-with-http?
  [url]
  (re-matches #"^https?://.*" url))

(defn url-with-http
  [url]
  (if (starts-with-http? url)
    url
    (str "http://" url)))

(def url-matcher-regex #"(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$")

(defn validate-domain-format
  [url]
  (first (re-matches url-matcher-regex  url)))

(defn validate-url
  [url function]
  (let [url-with-prefix (url-with-http url)]
    (if-let [valid-url (validate-domain-format url-with-prefix)]
      (function url-with-prefix)
      (str url "is not a valid URL"))))
