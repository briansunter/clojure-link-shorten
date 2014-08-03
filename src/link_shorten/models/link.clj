(ns link-shorten.models.link
  (:require
              [environ.core :refer [env]]
              [korma.db :refer :all]
              [ring.util.codec :refer [base64-encode]]
              [korma.core :refer :all]
              [clojure.set :refer [map-invert]]
              [link-shorten.models.db :refer [init-db]]))

(def alpha "abcdefghjk")

(def mapping (zipmap (range (count alpha)) alpha))

(defn app-host [] (if-let [host (env :domain)]
                    host
                    "http://localhost:5000"))



(defentity links)

(defn encode-id [number-to-encode]
  (clojure.string/join (map #(mapping (Integer/parseInt (str %))) (str number-to-encode))))


(defn list-of-digits-to-number [list-of-digits]
  (Integer/parseInt (clojure.string/join (map str list-of-digits))))

(defn decode-id [number-to-decode]
  (let [reverse-mapping (map-invert mapping)
        list-of-digits  (map reverse-mapping number-to-decode)]
    (list-of-digits-to-number list-of-digits)))

(defn full-link-from-encoded-id
  [encoded-id]
  (str (app-host) "/" encoded-id))

(defn add-link-with-url [url]
  (let [link  (insert links (values {:url url}))
    encoded-id (encode-id (link :id))]
    (full-link-from-encoded-id encoded-id)))

(defn link-from-encoded-id [encoded-id]
  (let [actual-id (decode-id encoded-id)]
    (first (select links (where {:id actual-id})))))


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
