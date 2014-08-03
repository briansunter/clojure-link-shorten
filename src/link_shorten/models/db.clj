(ns link-shorten.models.db
  (:require  [clojure.java.jdbc :as sql]
              [korma.db :refer :all]
             [korma.core :refer :all]))


(def test-db "//localhost:5432/links")

(defn prod-db
  "This formats the heroku database url format into the
  format that korma wants"
  []
  (if (System/getenv "DATABASE_URL")
    (let [db-uri (java.net.URI. (System/getenv "DATABASE_URL"))
          user-and-password (clojure.string/split (.getUserInfo db-uri) #":")]
      {:classname "org.postgresql.Driver"
       :subprotocol "postgresql"
       :user (get user-and-password 0)
       :password (get user-and-password 1) ; may be nil
       :subname (if (= -1 (.getPort db-uri))
                  (format "//%s%s" (.getHost db-uri) (.getPath db-uri))
                  (format "//%s:%s%s" (.getHost db-uri) (.getPort db-uri) (.getPath db-uri)))})
    (postgres
     {:db "links"
      :user "admin"
      :password "1234"
      ;;OPTIONAL KEYS
      :host "localhost"})))

(defn create-link-table [the-db]
(sql/db-do-commands the-db (sql/create-table-ddl  :links
    [:id   "BIGSERIAL PRIMARY KEY"]
    [:url "varchar(255)"]
    [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"])))


(defn init-db
  []
  (defdb korma-db (prod-db))
  (try
  (create-link-table (prod-db))
    (catch Exception e (str "caught exception: " (.getMessage e)))))








