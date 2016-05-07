(ns compojure-versioned-routes.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/api/v2/qux" [] "I am qux version 2.")
  (GET "/api/v2/foo" [] "I am foo version 2.")
  (GET "/api/v1/bar" [] "I am bar version 1.")
  (GET "/api/v1/foo" [] "I am foo version 1.")
  (route/not-found "Not Found"))

(defn prepend-version
  [uri version]
  (str "/api/v" version uri))

(defn wrap-versioning
  [handler]
  (fn [req]
    (let [accept (get-in req [:headers "accept"])
          [format version-str] (clojure.string/split accept #";")
          version-str (or version-str "version=1")
          [_  version] (clojure.string/split version-str #"=")]
      (handler
       (update req :uri prepend-version version)))))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (wrap-versioning)))
