(ns link-shorten.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.middleware.stacktrace :as trace]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as cookie]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.basic-authentication :as basic]
            [cemerick.drawbridge :as drawbridge]
            [environ.core :refer [env]]
            [korma.db :refer :all]
            [korma.core :refer :all]
            [ring.util.response :refer [redirect]]
            [environ.core :refer [env]]
            [link-shorten.models.link :refer [add-link-with-url link-from-encoded-id validate-url]]
            [link-shorten.views.main :refer [main-page]]
            [link-shorten.models.db :refer [init-db]]
            ))



(defn handle-new-link
  [link]
  (let [shortened-link (validate-url link add-link-with-url)]
    (main-page shortened-link)))

(defn- authenticated? [user pass]
  (and (= user (env :user) (= pass (env :pass)))))

(def ^:private drawbridge
  (-> (drawbridge/ring-handler)
      (session/wrap-session)
      (basic/wrap-basic-authentication authenticated?)))


(defroutes app
  (ANY "/u/repl" {:as req}
       (drawbridge req))
  (GET "/" [] (main-page))
  (GET "/:encoded" [encoded] (redirect ((link-from-encoded-id encoded) :url)))
  (POST "/u/shorten-link" [link] (handle-new-link link) )
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (slurp (io/resource "500.html"))}))))

(defn wrap-app [app]
  (let [store (cookie/cookie-store {:key (env :session-secret)})]
    (-> app
        ((if (env :production)
           wrap-error-page
           trace/wrap-stacktrace))
        (site {:session {:store store}}))))

(defn -main [& [port]]
(init-db)
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-app #'app) {:port port :join? false})))
