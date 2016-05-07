(ns compojure-versioned-routes.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [compojure-versioned-routes.handler :refer :all]))

(defn version-header
  [request version]
  (mock/header request
               "Accept"
               (str "application/vnd.example+json; version=" version)))

(deftest test-app
  (testing "foo v1 works"
    (let [response (app (-> (mock/request :get "/foo")
                            (version-header "1")))]
      (is (= (:status response) 200))
      (is (= (:body response) "I am foo version 1."))))

  (testing "foo v2 works"
    (let [response (app (-> (mock/request :get "/foo")
                            (version-header "2")))]
      (is (= (:status response) 200))
      (is (= (:body response) "I am foo version 2."))))

  (testing "bar v1 works"
    (let [response (app (-> (mock/request :get "/bar")
                            (version-header "1")))]
      (is (= (:status response) 200))
      (is (= (:body response) "I am bar version 1."))))

  (testing "qux v2 works"
    (let [response (app (-> (mock/request :get "/qux")
                            (version-header "2")))]
      (is (= (:status response) 200))
      (is (= (:body response) "I am qux version 2."))))

  (testing "not-found routes"
    (let [bar-v2 (app (-> (mock/request :get "/bar")
                          (version-header "2")))
          qux-v1 (app (-> (mock/request :get "/qux")
                          (version-header "1")))]
      (is (= (:status bar-v2) 404)
          (= (:status qux-v1) 404)))))
