(ns link-shorten.views.main
  (:require
    [hiccup.core :refer :all]
    [hiccup.page :as page]
    [hiccup.form :refer :all])
  )
(defn main-page [& [message]]
  (page/html5
    [:body [:h1 (or message "Enter the link you want to shorten")]
     (form-to [:POST "/u/shorten-link" ]
              (text-field "link" )
              (submit-button "Submit")
              )]))

